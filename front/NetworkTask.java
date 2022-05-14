package com.dinkar.blescanner;

import android.os.AsyncTask;

public class NetworkTask extends AsyncTask<Void, Void, String> {
    private String url;
    private StringBuffer values;

    public NetworkTask(String url, StringBuffer values) {

        this.url = url;
        this.values = values;
    }

    @Override
    protected String doInBackground(Void... params) {

        String result; // 요청 결과를 저장할 변수.
        Communication communication = new Communication();
        result = communication.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.

        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 저장한다.
        Communication.StoreTemp(s);
    }
}
