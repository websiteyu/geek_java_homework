package com.mine.krimz;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpRequest {
    final static OkHttpClient client = new OkHttpClient();

    public static String run(String url, String server) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("server",server)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static void registerServer(int port) throws IOException {
        run("http://127.0.0.1:18808","http://127.0.0.1:"+port);
    }

}
