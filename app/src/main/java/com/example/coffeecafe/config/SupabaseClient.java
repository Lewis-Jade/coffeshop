package com.example.coffeecafe.config;

import android.content.Context;
import com.example.coffeecafe.utils.Constants;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Supabase REST API Client for Java
 * Provides methods to interact with Supabase database and auth
 */
public class SupabaseClient {
    private static SupabaseClient instance;
    private final OkHttpClient httpClient;
    private String supabaseUrl;
    private String supabaseKey;
    private String authToken;

    private SupabaseClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }
    
    public void initialize() {
        if (this.supabaseUrl == null) {
            this.supabaseUrl = Constants.getSupabaseUrl();
            this.supabaseKey = Constants.getSupabaseAnonKey();
            
            // Validate credentials
            if (this.supabaseUrl == null || this.supabaseUrl.isEmpty()) {
                throw new IllegalStateException("Supabase URL not configured. Please set up local.properties file.");
            }
            if (this.supabaseKey == null || this.supabaseKey.isEmpty()) {
                throw new IllegalStateException("Supabase Key not configured. Please set up local.properties file.");
            }
        }
    }

    public static synchronized SupabaseClient getInstance() {
        if (instance == null) {
            instance = new SupabaseClient();
        }
        return instance;
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public String getAuthToken() {
        return authToken;
    }

    // Auth Methods
    public interface AuthCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }

    public void signUp(String email, String password, String fullName, String phone, AuthCallback callback) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("email", email);
            requestBody.put("password", password);
            
            JSONObject metadata = new JSONObject();
            metadata.put("full_name", fullName);
            metadata.put("phone", phone);
            requestBody.put("data", metadata);

            Request request = new Request.Builder()
                    .url(supabaseUrl + "/auth/v1/signup")
                    .addHeader("apikey", supabaseKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        if (response.isSuccessful()) {
                            callback.onSuccess(json);
                        } else {
                            String error = json.optString("error_description", json.optString("message", "Signup failed"));
                            callback.onError(error);
                        }
                    } catch (JSONException e) {
                        callback.onError("Failed to parse response");
                    }
                }
            });
        } catch (JSONException e) {
            callback.onError(e.getMessage());
        }
    }

    public void signIn(String email, String password, AuthCallback callback) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("email", email);
            requestBody.put("password", password);

            Request request = new Request.Builder()
                    .url(supabaseUrl + "/auth/v1/token?grant_type=password")
                    .addHeader("apikey", supabaseKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        if (response.isSuccessful()) {
                            String token = json.optString("access_token");
                            setAuthToken(token);
                            callback.onSuccess(json);
                        } else {
                            String error = json.optString("error_description", json.optString("message", "Login failed"));
                            callback.onError(error);
                        }
                    } catch (JSONException e) {
                        callback.onError("Failed to parse response");
                    }
                }
            });
        } catch (JSONException e) {
            callback.onError(e.getMessage());
        }
    }

    public void signOut(AuthCallback callback) {
        Request request = new Request.Builder()
                .url(supabaseUrl + "/auth/v1/logout")
                .addHeader("apikey", supabaseKey)
                .addHeader("Authorization", "Bearer " + authToken)
                .post(RequestBody.create("", MediaType.parse("application/json")))
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    setAuthToken(null);
                    callback.onSuccess(new JSONObject());
                } else {
                    callback.onError("Logout failed");
                }
            }
        });
    }

    // Database Methods
    public interface DatabaseCallback {
        void onSuccess(JSONArray response);
        void onError(String error);
    }

    public interface SingleRecordCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }

    public void select(String table, String columns, String filter, DatabaseCallback callback) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(supabaseUrl + "/rest/v1/" + table).newBuilder();
        if (columns != null && !columns.isEmpty()) {
            urlBuilder.addQueryParameter("select", columns);
        }
        if (filter != null && !filter.isEmpty()) {
            String[] filters = filter.split("&");
            for (String f : filters) {
                String[] parts = f.split("=", 2);
                if (parts.length == 2) {
                    urlBuilder.addQueryParameter(parts[0], parts[1]);
                }
            }
        }

        Request.Builder requestBuilder = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("apikey", supabaseKey)
                .addHeader("Content-Type", "application/json");

        if (authToken != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = requestBuilder.get().build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                try {
                    if (response.isSuccessful()) {
                        JSONArray json = new JSONArray(responseBody);
                        callback.onSuccess(json);
                    } else {
                        JSONObject error = new JSONObject(responseBody);
                        callback.onError(error.optString("message", "Query failed"));
                    }
                } catch (JSONException e) {
                    callback.onError("Failed to parse response");
                }
            }
        });
    }

    public void insert(String table, JSONObject data, SingleRecordCallback callback) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(supabaseUrl + "/rest/v1/" + table)
                .addHeader("apikey", supabaseKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation");

        if (authToken != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = requestBuilder
                .post(RequestBody.create(data.toString(), MediaType.parse("application/json")))
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                try {
                    if (response.isSuccessful()) {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        if (jsonArray.length() > 0) {
                            callback.onSuccess(jsonArray.getJSONObject(0));
                        } else {
                            callback.onError("No data returned");
                        }
                    } else {
                        JSONObject error = new JSONObject(responseBody);
                        callback.onError(error.optString("message", "Insert failed"));
                    }
                } catch (JSONException e) {
                    callback.onError("Failed to parse response");
                }
            }
        });
    }

    public void update(String table, String filter, JSONObject data, SingleRecordCallback callback) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(supabaseUrl + "/rest/v1/" + table).newBuilder();
        if (filter != null && !filter.isEmpty()) {
            String[] filters = filter.split("&");
            for (String f : filters) {
                String[] parts = f.split("=", 2);
                if (parts.length == 2) {
                    urlBuilder.addQueryParameter(parts[0], parts[1]);
                }
            }
        }

        Request.Builder requestBuilder = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("apikey", supabaseKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation");

        if (authToken != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = requestBuilder
                .patch(RequestBody.create(data.toString(), MediaType.parse("application/json")))
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                try {
                    if (response.isSuccessful()) {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        if (jsonArray.length() > 0) {
                            callback.onSuccess(jsonArray.getJSONObject(0));
                        } else {
                            callback.onError("No data returned");
                        }
                    } else {
                        JSONObject error = new JSONObject(responseBody);
                        callback.onError(error.optString("message", "Update failed"));
                    }
                } catch (JSONException e) {
                    callback.onError("Failed to parse response");
                }
            }
        });
    }

    public void delete(String table, String filter, AuthCallback callback) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(supabaseUrl + "/rest/v1/" + table).newBuilder();
        if (filter != null && !filter.isEmpty()) {
            String[] filters = filter.split("&");
            for (String f : filters) {
                String[] parts = f.split("=", 2);
                if (parts.length == 2) {
                    urlBuilder.addQueryParameter(parts[0], parts[1]);
                }
            }
        }

        Request.Builder requestBuilder = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("apikey", supabaseKey)
                .addHeader("Content-Type", "application/json");

        if (authToken != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = requestBuilder.delete().build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess(new JSONObject());
                } else {
                    String responseBody = response.body().string();
                    try {
                        JSONObject error = new JSONObject(responseBody);
                        callback.onError(error.optString("message", "Delete failed"));
                    } catch (JSONException e) {
                        callback.onError("Delete failed");
                    }
                }
            }
        });
    }
}
