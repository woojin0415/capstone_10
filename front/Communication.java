package com.dinkar.blescanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Communication {
    private static String temp;
    private static String error = "F";
    // HttpURLConnection 참조 변수.
    HttpURLConnection urlConn = null;
    // URL 뒤에 붙여서 보낼 파라미터.
    StringBuffer sbParams = new StringBuffer();

    public static String SignIn(String UserID_, String password_, Integer type_) {
        String url_signin= "http://13.125.248.219:8080/sign/signup/";
        String UserID = UserID_;
        String password = password_;
        Integer type = type_; // 1= 택배기사, 2=수령인


        // URL 뒤에 붙여서 보낼 파라미터.
        StringBuffer sbParams = new StringBuffer();

        //하나라도 빈칸인 경우
        if((UserID == null)||(password == null)||(type == null)){
            return error;
        }
        else{
            //내용이 더 채워져 있음 => 서버로 보냄
            //파라미터 채우기
            sbParams.append("phonenumber="+UserID+"&password="+password+"&type="+type);
            // AsyncTask를 통해 HttpURLConnection 수행.
            NetworkTask networkTask = new NetworkTask(url_signin, sbParams);
            networkTask.execute();
        }
        return temp;
    }

    public static String LogIn(String UserID_, String password_){
        String url_login= "http://13.125.248.219:8080/sign/login/";
        String UserID = UserID_;
        String password = password_;

        // URL 뒤에 붙여서 보낼 파라미터.
        StringBuffer sbParams = new StringBuffer();

        //하나라도 빈칸인 경우
        if((UserID == null)||(password == null)){
            return error;
        }
        else{
            //내용이 더 채워져 있음 => 서버로 보냄
            //파라미터 채우기
            sbParams.append("phonenumber="+UserID+"&password="+password);
            // AsyncTask를 통해 HttpURLConnection 수행.
            NetworkTask networkTask = new NetworkTask(url_login, sbParams);
            networkTask.execute();
        }

        return temp;
    }

    public static String CheckID(String UserID_){
        String url_checkid= "http://13.125.248.219:8080/sign/checkid/";
        String UserID = UserID_;
        String result;

        // URL 뒤에 붙여서 보낼 파라미터.
        StringBuffer sbParams = new StringBuffer();

        //하나라도 빈칸인 경우
        if(UserID == null){
            return error;
        }
        else{
            //내용이 더 채워져 있음 => 서버로 보냄
            //파라미터 채우기
            sbParams.append("phonenumber="+UserID);
            // AsyncTask를 통해 HttpURLConnection 수행.
            NetworkTask networkTask = new NetworkTask(url_checkid, sbParams);
            networkTask.execute();
        }
        return temp;
    }

    public static  String Findpassword(String phonenumber_){
        String url_findpassword = "http://13.125.248.219:8080/sign/findpassword/";
        String UserID = phonenumber_;

        // URL 뒤에 붙여서 보낼 파라미터.
        StringBuffer sbParams = new StringBuffer();

        //하나라도 빈칸인 경우
        if(UserID == null){
            return error;
        }
        else{
            //내용이 더 채워져 있음 => 서버로 보냄
            //파라미터 채우기
            sbParams.append("phonenumber="+UserID);
            // AsyncTask를 통해 HttpURLConnection 수행.
            NetworkTask networkTask = new NetworkTask(url_findpassword, sbParams);
            networkTask.execute();
        }
        return temp;
    }

    public static String request(String _url, StringBuffer sbParams){
        // HttpURLConnection 참조 변수.
        HttpURLConnection urlConn = null;

        try{
            URL url = new URL(_url);
            urlConn = (HttpURLConnection) url.openConnection();

            // [2-1]. urlConn 설정.
            urlConn.setConnectTimeout(15000);
            urlConn.setReadTimeout(5000);
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.
            urlConn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
            urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencode");
            urlConn.setRequestProperty("apikey", ""); // ""안에 apikey를 입력


            // [2-2]. parameter 전달 및 데이터 읽어오기.
            String strParams = sbParams.toString(); //sbParams에 정리한 파라미터들을 스트링으로 저장. 예)id=id1&pw=123;
            OutputStream os = urlConn.getOutputStream();
            os.write(strParams.getBytes("UTF-8")); // 출력 스트림에 출력.
            os.flush(); // 출력 스트림을 플러시(비운다)하고 버퍼링 된 모든 출력 바이트를 강제 실행.
            os.close(); // 출력 스트림을 닫고 모든 시스템 자원을 해제.

            // [2-3]. 연결 요청 확인.
            // 실패 시 null을 리턴하고 메서드를 종료.
            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            // [2-4]. 읽어온 결과물 리턴.
            // 요청한 URL의 출력물을 BufferedReader로 받는다.
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

            // 출력물의 라인과 그 합에 대한 변수.
            String line;
            String page = "";

            // 라인을 받아와 합친다.
            while ((line = reader.readLine()) != null){
                page += line;
            }

            return page;

        } catch (MalformedURLException e) { // for URL.
            e.printStackTrace();
        } catch (IOException e) { // for openConnection().
            e.printStackTrace();
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }
        return null;
    }

    public static void StoreTemp(String temp_){
        temp = temp_;
    }
    public static String reqTemp(){
        return temp;
    }
}


