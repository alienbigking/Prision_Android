package com.gkzxhn.wisdom.async

import com.android.volley.toolbox.HttpStack
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Protocol
import com.squareup.okhttp.RequestBody
import org.apache.http.HttpEntity
import org.apache.http.ProtocolVersion
import org.apache.http.entity.BasicHttpEntity
import org.apache.http.message.BasicHeader
import org.apache.http.message.BasicHttpResponse
import org.apache.http.message.BasicStatusLine
import java.io.IOException
import java.util.concurrent.TimeUnit




/**
 * Created by Raleigh.Luo on 17/11/20.
 * 解决Volley Patch请求在5.0以下不能使用的问题
 * com.android.volley.NoConnectionError: java.net.ProtocolException: Unknown method 'PATCH'; must be one of [OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE]
 */
class OkHttpStack : HttpStack {
    private val mClient: OkHttpClient
    constructor(client: OkHttpClient) {
        this.mClient = client
    }

    @Throws(IOException::class, AuthFailureError::class)
    override fun performRequest(request: Request<*>?, additionalHeaders: MutableMap<String, String>?): org.apache.http.HttpResponse {
        val client = mClient.clone()
        val timeoutMs = request?.timeoutMs?.toLong()?:0L
        client.setConnectTimeout(timeoutMs, TimeUnit.MILLISECONDS)
        client.setReadTimeout(timeoutMs, TimeUnit.MILLISECONDS)
        client.setWriteTimeout(timeoutMs, TimeUnit.MILLISECONDS)

        val okHttpRequestBuilder = com.squareup.okhttp.Request.Builder()
        okHttpRequestBuilder.url(request?.url)

        val headers = request?.headers
        headers?.keys?.let {
            for (name in it) {
                okHttpRequestBuilder.addHeader(name, headers.get(name))
            }
        }
        additionalHeaders?.keys?.let {
            for (name in it) {
                okHttpRequestBuilder.addHeader(name, additionalHeaders[name])
            }
        }
        setConnectionParametersForRequest(okHttpRequestBuilder, request!!)

        val okHttpRequest = okHttpRequestBuilder.build()
        val okHttpCall = client.newCall(okHttpRequest)
        val okHttpResponse = okHttpCall.execute()

        val responseStatus = BasicStatusLine(
                parseProtocol(okHttpResponse.protocol()),
                okHttpResponse.code(),
                okHttpResponse.message()
        )

        val response = BasicHttpResponse(responseStatus)
        response.entity = entityFromOkHttpResponse(okHttpResponse)

        val responseHeaders = okHttpResponse.headers()

        var i = 0
        val len = responseHeaders.size()
        while (i < len) {
            val name = responseHeaders.name(i)
            val value = responseHeaders.value(i)

            if (name != null) {
                response.addHeader(BasicHeader(name, value))
            }
            i++
        }

        return response
    }

    @Throws(IOException::class)
    private fun entityFromOkHttpResponse(r: com.squareup.okhttp.Response): HttpEntity {
        val entity = BasicHttpEntity()
        val body = r.body()

        entity.content = body.byteStream()
        entity.contentLength = body.contentLength()
        entity.setContentEncoding(r.header("Content-Encoding"))

        if (body.contentType() != null) {
            entity.setContentType(body.contentType().type())
        }
        return entity
    }


    @Throws(IOException::class, AuthFailureError::class)
    private fun setConnectionParametersForRequest(builder: com.squareup.okhttp.Request.Builder, request: Request<*>) {
        when (request.method) {
            Request.Method.DEPRECATED_GET_OR_POST -> {
                // Ensure backwards compatibility.
                // Volley assumes a request with a null body is a GET.
                val postBody = request.postBody

                if (postBody != null) {
                    builder.post(RequestBody.create(MediaType.parse(request.postBodyContentType), postBody))
                }
            }

            Request.Method.GET -> builder.get()

            Request.Method.DELETE -> builder.delete()

            Request.Method.POST -> builder.post(createRequestBody(request))

            Request.Method.PUT -> builder.put(createRequestBody(request))

            Request.Method.HEAD -> builder.head()

            Request.Method.OPTIONS -> builder.method("OPTIONS", null)

            Request.Method.TRACE -> builder.method("TRACE", null)

            Request.Method.PATCH -> builder.patch(createRequestBody(request))

            else -> throw IllegalStateException("Unknown method type.")
        }
    }

    private fun parseProtocol(p: Protocol): ProtocolVersion {
        when (p) {
            Protocol.HTTP_1_0 -> return ProtocolVersion("HTTP", 1, 0)
            Protocol.HTTP_1_1 -> return ProtocolVersion("HTTP", 1, 1)
            Protocol.SPDY_3 -> return ProtocolVersion("SPDY", 3, 1)
            Protocol.HTTP_2 -> return ProtocolVersion("HTTP", 2, 0)
        }

        throw IllegalAccessError("Unkwown protocol")
    }

    @Throws(AuthFailureError::class)
    private fun createRequestBody(r: Request<*>): RequestBody? {
        val body = r.body ?: return null

        return RequestBody.create(MediaType.parse(r.bodyContentType), body)
    }
}