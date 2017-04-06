package com.xdja.xelog.okhttp_interceptor;

import com.elvishew.xlog.LogConfiguration;
import com.ldy.xelog.config.XELogConfig;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSource;

import static okhttp3.internal.platform.Platform.INFO;

/**
 * Created by ldy on 2017/4/6.
 */
public class OkHttpLogInterceptor extends XELogConfig implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String LINE_SEPARATOR = "\n";
    public static final String REQUEST_START = "--> ";
    public static final String RESPONSE_START = "<-- ";
    protected Request request;
    protected Response response;
    protected String author;
    private boolean isJsonBody;
    private LogConfiguration logConfiguration;

    public OkHttpLogInterceptor() {
    }

    public OkHttpLogInterceptor(String author) {
        this.author = author;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getSummary(String msg) {
        Request currentRequest;
        String start;
        if (msg.startsWith(RESPONSE_START)) {
            currentRequest = response.request();
            start = "response:";
        } else {
            currentRequest = request;
            start = "request:";
        }
        String urlStr = currentRequest.url().toString();
        int index = urlStr.indexOf('/', currentRequest.url().scheme().length() + 3);
        return start + urlStr.substring(index, urlStr.length());
    }

    @Override
    public List<String> getBaseTag() {
        ArrayList<String> tags = new ArrayList<String>();
        tags.add("net");
        return tags;
    }

    @Override
    public boolean withStackTrace() {
        return false;
    }

    @Override
    public boolean withThread() {
        return false;
    }

    @Override
    public LogConfiguration getXLogConfiguration() {
        if (logConfiguration == null) {
            logConfiguration = new LogConfiguration.Builder().build();
        }
        return logConfiguration;
    }

    public void jsonBody() {
        isJsonBody = true;
    }

    public enum Level {
        /**
         * No logs.
         */
        NONE,
        /**
         * Logs request and response lines.
         * <p>
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1 (3-byte body)
         *
         * <-- 200 OK (22ms, 6-byte body)
         * }</pre>
         */
        BASIC,
        /**
         * Logs request and response lines and their respective headers.
         * <p>
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         * <-- END HTTP
         * }</pre>
         */
        HEADERS,
        /**
         * Logs request and response lines and their respective headers and bodies (if present).
         * <p>
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         *
         * Hi?
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         *
         * Hello!
         * <-- END HTTP
         * }</pre>
         */
        BODY
    }

    public interface Logger {
        void log(String message);

        /**
         * A {@link OkHttpLogInterceptor.Logger} defaults output appropriate for the current platform.
         */
        OkHttpLogInterceptor.Logger DEFAULT = new OkHttpLogInterceptor.Logger() {
            @Override
            public void log(String message) {
                Platform.get().log(INFO, message, null);
            }
        };
    }

    private volatile OkHttpLogInterceptor.Level level = OkHttpLogInterceptor.Level.NONE;

    /**
     * Change the level at which this interceptor logs.
     */
    public OkHttpLogInterceptor setLevel(OkHttpLogInterceptor.Level level) {
        if (level == null) throw new NullPointerException("level == null. Use Level.NONE instead.");
        this.level = level;
        return this;
    }

    public OkHttpLogInterceptor.Level getLevel() {
        return level;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        OkHttpLogInterceptor.Level level = this.level;

        response = null;
        request = chain.request();

        if (level == OkHttpLogInterceptor.Level.NONE) {
            return chain.proceed(request);
        }

        boolean logBody = level == OkHttpLogInterceptor.Level.BODY;
        boolean logHeaders = logBody || level == OkHttpLogInterceptor.Level.HEADERS;

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;

        StringBuilder requestStrBuilder = new StringBuilder();

        String requestStartMessage = REQUEST_START + request.method() + ' ' + request.url() + ' ' + protocol;
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
        }
        requestStrBuilder.append(requestStartMessage);

        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody.contentType() != null) {
                    requestStrBuilder.append(LINE_SEPARATOR);
                    requestStrBuilder.append("Content-Type: ").append(requestBody.contentType());

                }
                if (requestBody.contentLength() != -1) {
                    requestStrBuilder.append(LINE_SEPARATOR);
                    requestStrBuilder.append("Content-Length: ").append(requestBody.contentLength());
                }
            }

            Headers headers = request.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    requestStrBuilder.append(LINE_SEPARATOR);
                    requestStrBuilder.append(name).append(": ").append(headers.value(i));
                }
            }

            if (!logBody || !hasRequestBody) {
                requestStrBuilder.append(LINE_SEPARATOR);
                requestStrBuilder.append("--> END ").append(request.method());
            } else if (bodyEncoded(request.headers())) {
                requestStrBuilder.append(LINE_SEPARATOR);
                requestStrBuilder.append("--> END ").append(request.method()).append(" (encoded body omitted)");
            } else {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }
//                requestStrBuilder.append(LINE_SEPARATOR);
//                requestStrBuilder.append("");
                if (isPlaintext(buffer)) {
                    requestStrBuilder.append(LINE_SEPARATOR);
                    requestStrBuilder.append(getFormatBody(buffer.readString(charset)));
                    requestStrBuilder.append(LINE_SEPARATOR);
                    requestStrBuilder.append("--> END ")
                            .append(request.method())
                            .append(" (")
                            .append(requestBody.contentLength())
                            .append("-byte body)");
                } else {
                    requestStrBuilder.append(LINE_SEPARATOR);
                    requestStrBuilder
                            .append("--> END ")
                            .append(request.method())
                            .append(" (binary ")
                            .append(requestBody.contentLength())
                            .append("-byte body omitted)");
                }
            }
        }

        v(requestStrBuilder.toString());

        StringBuilder responseStrBuilder = new StringBuilder();

        long startNs = System.nanoTime();
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            responseStrBuilder.append(RESPONSE_START + "HTTP FAILED: ").append(e);
            v(responseStrBuilder.toString());
            throw new RuntimeException(e);
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
        responseStrBuilder.append(RESPONSE_START).append(response.code()).append(' ').append(response.message()).append(' ').append(response.request().url()).append(" (").append(tookMs).append("ms").append(!logHeaders ? ", "
                + bodySize + " body" : "").append(')');

        if (logHeaders) {
            Headers headers = response.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                responseStrBuilder.append(LINE_SEPARATOR);
                responseStrBuilder.append(headers.name(i)).append(": ").append(headers.value(i));
            }

            if (!logBody || !HttpHeaders.hasBody(response)) {
                responseStrBuilder.append(LINE_SEPARATOR);
                responseStrBuilder.append("<-- END HTTP");
            } else if (bodyEncoded(response.headers())) {
                responseStrBuilder.append(LINE_SEPARATOR);
                responseStrBuilder.append("<-- END HTTP (encoded body omitted)");
            } else {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.buffer();

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8);
                    } catch (UnsupportedCharsetException e) {
                        responseStrBuilder.append(LINE_SEPARATOR);
                        responseStrBuilder.append("");
                        responseStrBuilder.append(LINE_SEPARATOR);
                        responseStrBuilder.append("Couldn't decode the response body; charset is likely malformed.");
                        responseStrBuilder.append(LINE_SEPARATOR);
                        responseStrBuilder.append("<-- END HTTP");
                        v(responseStrBuilder.toString());
                        return response;
                    }
                }

                if (!isPlaintext(buffer)) {
                    responseStrBuilder.append(LINE_SEPARATOR);
                    responseStrBuilder.append("");
                    responseStrBuilder.append(LINE_SEPARATOR);
                    responseStrBuilder.append("<-- END HTTP (binary ").append(buffer.size()).append("-byte body omitted)");
                    v(responseStrBuilder.toString());
                    return response;
                }

                if (contentLength != 0) {
                    responseStrBuilder.append(LINE_SEPARATOR);
                    responseStrBuilder.append(getFormatBody(buffer.clone().readString(charset)));
                }
                responseStrBuilder.append(LINE_SEPARATOR);
                responseStrBuilder.append("<-- END HTTP (").append(buffer.size()).append("-byte body)");
            }
        }
        v(responseStrBuilder.toString());
        return response;
    }

    private String getFormatBody(String bodyStr) {
        if (isJsonBody) {
            LogConfiguration xLogConfiguration = getXLogConfiguration();
            if (xLogConfiguration != null) {
                return xLogConfiguration.jsonFormatter.format(bodyStr);
            }
        }
        return bodyStr;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }
}
