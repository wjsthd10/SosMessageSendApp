package com.example.sosmessagesendapp;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ShortURL {

    public static String main(String baseUrl) {
        String clientId = "k0lio77gbx";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "Nb6J5uCJvW3twHvpzdr01j1ypBzoiDZt4d1qq4zo";//애플리케이션 클라이언트 시크릿값";

        String text = null;
        try {
            text = URLEncoder.encode(baseUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패",e);
        }

        String apiURL = "https://naveropenapi.apigw.ntruss.com/util/v1/shorturl?url=" + text;    // json 결과
        //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // xml 결과
        Log.e("yun_log", "show apiURL = "+apiURL);

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-NCP-APIGW-API-KEY-ID", clientId);
        requestHeaders.put("X-NCP-APIGW-API-KEY", clientSecret);
        String responseBody = get(apiURL,requestHeaders);

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(responseBody);
        jsonElement = jsonElement.getAsJsonObject().get("result");
//        System.out.println(responseBody);
        String result = String.valueOf(jsonElement.getAsJsonObject().get("url"));
        result = result.substring(1);
        result = result.substring(0, result.length()-1);
        Log.e("yun_log", "show result = "+result);
        return result;
    }


    private static String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

}
