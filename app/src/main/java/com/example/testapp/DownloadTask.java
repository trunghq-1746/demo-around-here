package com.example.testapp;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadTask extends AsyncTask<String, Integer, DownloadTask.Result> {
    private DownloadCallback<String> mCallback;

    public DownloadTask(DownloadCallback<String> callback) {
        setDownloadCallback(callback);
    }

    public void setDownloadCallback(DownloadCallback<String> callback) {
        mCallback = callback;
    }

    @Override
    protected Result doInBackground(String... urls) {
        Result result = null;
        if (!isCancelled() && urls != null && urls.length > 0 ) {
            String urlString = urls[0];
            try {
                String resultString = downloadUrl(urlString);
                if (resultString != null) {
                    result = new Result(resultString);
                } else {
                    throw new IOException("No response received");
                }
            } catch (IOException e) {
                result = new Result(e);
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(Result result) {
        if (result != null && mCallback != null) {
            if (result.mException != null) {
                mCallback.updateFromDownload(result.mException.getMessage());
            } else {
                mCallback.updateFromDownload(result.mResult);
            }
        }
    }

    private String downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        InputStream inputStream = null;
        HttpsURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            inputStream = connection.getInputStream();
            if (inputStream != null) {
                result = parseJSONToObject(readStream(inputStream));
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    private String readStream(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        return builder.toString();
    }

    private String parseJSONToObject(String in) {
        try {
            JSONObject reader = new JSONObject(in);
            JSONObject searchItems = reader.getJSONObject("searchItems");
            JSONObject item0 = searchItems.getJSONObject("0");
            return item0.getString("Name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    static class Result {
        public String mResult;
        public Exception mException;

        public Result(String result) {
            mResult = result;
        }

        public Result(Exception exception) {
            mException = exception;
        }
    }
}
