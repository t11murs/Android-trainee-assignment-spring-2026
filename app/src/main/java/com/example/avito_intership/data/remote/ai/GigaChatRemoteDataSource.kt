package com.example.avito_intership.data.remote.ai

import com.example.avito_intership.BuildConfig
import com.example.avito_intership.core.result.AppError
import com.example.avito_intership.core.result.AppResult
import com.example.avito_intership.data.remote.ai.dto.AiGenerateRequestDto
import com.example.avito_intership.data.remote.ai.dto.AiGenerateResponseDto
import com.example.avito_intership.data.remote.ai.dto.GigaChatTokenResponseDto
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

@Singleton
class GigaChatRemoteDataSource @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val tokenStore: GigaChatTokenStore,
) : AiRemoteDataSource {

    private val tokenMutex = Mutex()

    override suspend fun generateReply(request: AiGenerateRequestDto): AppResult<AiGenerateResponseDto> {
        val tokenResult = getAccessToken()
        val token = when (tokenResult) {
            is AppResult.Success -> tokenResult.data
            is AppResult.Error -> return tokenResult
        }

        val jsonBody = JSONObject().apply {
            put("model", "GigaChat")
            put(
                "messages",
                JSONArray().put(
                    JSONObject().apply {
                        put("role", "user")
                        put("content", request.prompt)
                    },
                ),
            )
        }

        val httpRequest = Request.Builder()
            .url("https://gigachat.devices.sberbank.ru/api/v1/chat/completions")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer $token")
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        return withContext(Dispatchers.IO) {
            try {
                okHttpClient.newCall(httpRequest).execute().use { response ->
                    if (!response.isSuccessful) {
                        return@withContext AppResult.Error(
                            AppError.Unknown("Ошибка GigaChat: ${response.code}"),
                        )
                    }

                    val body = response.body?.string().orEmpty()
                    val root = JSONObject(body)
                    val choices = root.optJSONArray("choices")
                    val text = choices
                        ?.optJSONObject(0)
                        ?.optJSONObject("message")
                        ?.optString("content")
                        ?.trim()
                        .orEmpty()

                    if (text.isBlank()) {
                        AppResult.Error(AppError.Unknown("Пустой ответ от GigaChat"))
                    } else {
                        AppResult.Success(AiGenerateResponseDto(text = text))
                    }
                }
            } catch (exception: SSLHandshakeException) {
                AppResult.Error(
                    AppError.Unknown(
                        "Не удалось проверить сертификат GigaChat. ${exception.message.orEmpty()}",
                    ),
                )
            } catch (exception: SSLException) {
                AppResult.Error(
                    AppError.Unknown(
                        "Ошибка TLS при подключении к GigaChat. ${exception.message.orEmpty()}",
                    ),
                )
            } catch (_: UnknownHostException) {
                AppResult.Error(AppError.Network)
            } catch (_: SocketTimeoutException) {
                AppResult.Error(AppError.Network)
            } catch (_: IOException) {
                AppResult.Error(AppError.Network)
            } catch (exception: Exception) {
                AppResult.Error(AppError.Unknown(exception.message))
            }
        }
    }

    private suspend fun getAccessToken(): AppResult<String> {
        val authKey = BuildConfig.GIGACHAT_AUTH_KEY
        if (authKey.isBlank()) {
            return AppResult.Error(AppError.Configuration)
        }

        val cached = tokenStore.getValidToken(System.currentTimeMillis())
        if (cached != null) return AppResult.Success(cached)

        return tokenMutex.withLock {
            val secondCheck = tokenStore.getValidToken(System.currentTimeMillis())
            if (secondCheck != null) {
                return@withLock AppResult.Success(secondCheck)
            }

            val formBody = FormBody.Builder()
                .add("scope", BuildConfig.GIGACHAT_SCOPE)
                .build()

            val tokenRequest = Request.Builder()
                .url("https://ngw.devices.sberbank.ru:9443/api/v2/oauth")
                .addHeader("Accept", "application/json")
                .addHeader("RqUID", UUID.randomUUID().toString())
                .addHeader("Authorization", "Basic $authKey")
                .post(formBody)
                .build()

            withContext(Dispatchers.IO) {
                try {
                    okHttpClient.newCall(tokenRequest).execute().use { response ->
                        if (!response.isSuccessful) {
                            return@withContext AppResult.Error(
                                AppError.Unknown("Ошибка получения токена GigaChat: ${response.code}"),
                            )
                        }

                        val body = response.body?.string().orEmpty()
                        val json = JSONObject(body)
                        val accessToken = json.optString("access_token")
                        val expiresAt = json.optLong("expires_at")

                        if (accessToken.isBlank()) {
                            return@withContext AppResult.Error(
                                AppError.Unknown("Не удалось получить access token GigaChat"),
                            )
                        }

                        tokenStore.save(
                            GigaChatTokenResponseDto(
                                accessToken = accessToken,
                                expiresAtMillis = expiresAt,
                            ),
                        )
                        AppResult.Success(accessToken)
                    }
                } catch (exception: SSLHandshakeException) {
                    AppResult.Error(
                        AppError.Unknown(
                            "Не удалось проверить сертификат при получении токена GigaChat. ${exception.message.orEmpty()}",
                        ),
                    )
                } catch (exception: SSLException) {
                    AppResult.Error(
                        AppError.Unknown(
                            "Ошибка TLS при получении токена GigaChat. ${exception.message.orEmpty()}",
                        ),
                    )
                } catch (_: UnknownHostException) {
                    AppResult.Error(AppError.Network)
                } catch (_: SocketTimeoutException) {
                    AppResult.Error(AppError.Network)
                } catch (_: IOException) {
                    AppResult.Error(AppError.Network)
                } catch (exception: Exception) {
                    AppResult.Error(AppError.Unknown(exception.message))
                }
            }
        }
    }
}
