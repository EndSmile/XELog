package com.ldy.xelogsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private NetLog netLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        netLog = new NetLog();
    }

    public void click(View view) {
        netLog.v("{\n" +
                "      \"request\": {\n" +
                "        \"jsonrpc\": \"2.0\",\n" +
                "        \"method\": \"login\",\n" +
                "        \"params\": {\n" +
                "          \"iccid\": \"\",\n" +
                "          \"imei\": \"352584064425702\",\n" +
                "          \"validateName\": \"008497\",\n" +
                "          \"validatePass\": \"123456\",\n" +
                "          \"clientType\": \"0\"\n" +
                "        },\n" +
                "        \"response\": {\n" +
                "          \"jsonrpc\": \"2.0\",\n" +
                "          \"method\": \"login\",\n" +
                "          \"result\": {\n" +
                "            \"info\": {\n" +
                "              \"accessKey\": \"eafc9ed1-b44d-d91b-6cb8-18ef1df2b142\",\n" +
                "              \"personId\": \"100000014\",\n" +
                "              \"policeNo\": \"008497\",\n" +
                "              \"policeName\": \"张忠祥\",\n" +
                "              \"identifier\": \"520111196612040139\",\n" +
                "              \"deptNo\": \"520111000000\",\n" +
                "              \"deptName\": \"花溪分局\",\n" +
                "              \"mobile\": \"13999999900\",\n" +
                "              \"powers\": \"\",\n" +
                "              \"depId\": \"120120723120111272\"\n" +
                "            },\n" +
                "            \"resultStatus\": \"1\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }");
    }
}
