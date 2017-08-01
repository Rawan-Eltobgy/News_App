package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ProgressBar;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ArticleLoader extends AsyncTaskLoader<List<Article>> {

    String baseRequestUrl;
    String jsonResponse = "";
    String key;
    ProgressBar mProgressBar;
    private String mUrl;

    public ArticleLoader(Context context, String baseRequestUrl, String key, ProgressBar mProgressBar) {
        super(context);
        this.baseRequestUrl = baseRequestUrl;
        this.key = key;
        this.mProgressBar = mProgressBar;

    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    private static List<Article> extractFeatureFromJson(String articleJSON) {
        // If the JSON string is empty or null, then return early.
        String LOG_TAG = ArticleLoader.class.getName();
        if (TextUtils.isEmpty(articleJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        List<Article> list = new ArrayList<>();

        try {
            Log.e(LOG_TAG, "Fetching properties .....  ");
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(articleJSON);
            JSONObject responses = baseJsonResponse.getJSONObject("response");
            JSONArray resultsArray = responses.getJSONArray("results");

            if (resultsArray != null) {
                for (int i = 0; i < resultsArray.length(); i++) {

                    JSONObject currentArticle = resultsArray.getJSONObject(i);
                    String sectionName = currentArticle.getString("sectionName");
                    String webTitle = currentArticle.getString("webTitle");
                    String webUrl = currentArticle.getString("webUrl");
                    String datestr = currentArticle.getString("webPublicationDate");

                    String jsonDatePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
                    SimpleDateFormat jsonFormatter = new SimpleDateFormat(jsonDatePattern, Locale.ENGLISH);

                    Date parsedJsonDate = jsonFormatter.parse(datestr);
                    String finalDatePattern = "h:mm a MMM d, yyy ";
                    SimpleDateFormat finalDateFormatter = new SimpleDateFormat(finalDatePattern, Locale.ENGLISH);
                    String webPublicationDate = finalDateFormatter.format(parsedJsonDate);


                    list.add(new Article(sectionName, webTitle, webPublicationDate, webUrl));

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "NO PROPERTIES FOUND  ", e);
        }

        return list;
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<Article> loadInBackground() {

        /** if (mUrl == null) {
         Log.e("App Name", "Failing you: " + baseRequestUrl);
         return null;
         }**/
        String LOG_TAG = ArticleLoader.class.getName();
        Log.e(LOG_TAG, "REQUEST is: trial 2 : " + baseRequestUrl);
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        // baseRequestUrl += key;
        Uri uri = Uri.parse(baseRequestUrl);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .encodedAuthority("content.guardianapis.com")
                .appendPath("search")
                .appendQueryParameter("order-by", "newest")
                .appendQueryParameter("api-key", key);
        String stringurl = builder.build().toString();
        Log.e(LOG_TAG, "REQUEST is Final : " + stringurl);
        URL url = null;
        try {
            url = new URL(stringurl);
            Log.e(LOG_TAG, "REQUEST After editing : " + url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            assert url != null;
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {


                inputStream = urlConnection.getInputStream();

                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the books JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        List<Article> articles = extractFeatureFromJson(jsonResponse);
        return articles;
    }
}



