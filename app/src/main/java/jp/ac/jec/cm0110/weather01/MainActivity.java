package jp.ac.jec.cm0110.weather01;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    Button btn;
    RequestQueue queue;
    String url = "https://api.openweathermap.org/data/2.5/weather?id=1850147&appid=4812e468fd047aa1041be7e6fc6f2af3&units=metric&lang=ja";
    TextView textView2;
    TextView textView3;
    ImageView image;
    TextView textview4;

    String url2 = "https://openweathermap.org/img/wn/";
    String url3 = "";
    String url4 = "@2x.png";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView =  findViewById(R.id.text);
        btn =  findViewById(R.id.btnWeather);
        btn.setOnClickListener(new btnClickAction());
        queue = Volley.newRequestQueue(this);
        textView2 = findViewById(R.id.text2);
        textView3 = findViewById(R.id.text3);
        image =  findViewById(R.id.image);
        textview4 = findViewById(R.id.text4);


    }


    class btnClickAction implements View.OnClickListener {
        String sbSentence = "";


        @Override
        public void onClick(View view) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            sbSentence = response.substring(0,response.length());
//                            textView.setText("Response is:" + response.substring(0,response.length()));
                            try {
                                JSONObject jsonObj = new JSONObject(sbSentence.toString());
//                                textView.setText(jsonObj.get("name").toString());

                                textView2.setText(jsonObj.get("name").toString());


                                JSONArray jsonObjWeather = (JSONArray) jsonObj.get("weather");
                                JSONObject obj = (JSONObject) jsonObjWeather.get(0);
//                                Log.i("jsonparse", obj.toString());
                                JSONObject descriptionJS = new JSONObject(obj.toString());

//                                String sWeather = jsonObjWeather.getString("description");
                                textView.setText(descriptionJS.get("description").toString());


                                JSONObject jsonObject2 = jsonObj.getJSONObject("main");
                                int ihum = jsonObject2.getInt("humidity");
                                textView3.setText(String.valueOf(ihum) + "%");

                                JSONObject jsonObject3 = jsonObj.getJSONObject("main");
                                double dTempK = jsonObject3.getDouble("temp");
                                textview4.setText(String.valueOf(dTempK) + "℃");

                                JSONArray jArray = (JSONArray) jsonObj.getJSONArray("weather");
                                JSONObject jObj = jArray.getJSONObject(0);
                                String sObj = jObj.getString("icon");
                                url3 = sObj;



//                                image.setImageIcon(Icon.createWithContentUri(sObj));

                                dowloadImage(url2 + url3 + url4);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    textView.setText("That did't work!");
                }
        });
            queue.add(stringRequest);

            btn.setText("天気取得済み");




        }
    }

    private void dowloadImage(String urlSt){
        // Singleの別スレッドを立ち上げる
        //非同期処理を行ってる
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                URL url = new URL(urlSt);

                HttpURLConnection urlCon =  (HttpURLConnection) url.openConnection();

                // タイムアウト設定
                urlCon.setReadTimeout(10000);
                urlCon.setConnectTimeout(20000);

                // リクエストメソッド
                urlCon.setRequestMethod("GET");

                // リダイレクトを自動で許可しない設定
                urlCon.setInstanceFollowRedirects(false);

                InputStream is = urlCon.getInputStream();
                Bitmap bmp = BitmapFactory.decodeStream(is);

                // 別スレッド内での処理を管理し実行する
                HandlerCompat.createAsync(getMainLooper()).post(() ->
                        // Mainスレッドに渡す
                        image.setImageBitmap(bmp)
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}