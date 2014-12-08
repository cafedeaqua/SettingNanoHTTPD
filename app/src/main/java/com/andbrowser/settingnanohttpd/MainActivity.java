package com.andbrowser.settingnanohttpd;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Properties;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "HTTP";
    private static final int PORT = 8080;
    String ipAddress;
    View mRootView;
    private Context mApplicationContext;
    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        // TextViewの作成
        TextView text = (TextView) findViewById(R.id.textView1);
        ipAddress = getLocalIpAddress();
        if (ipAddress != null) {
            text.setText("Please Access:\n" + "http://" + ipAddress + ":" + PORT);
        } else {
            text.setText("Wi-Fi Network Not Available");
        }
        try {
            new SettingNanoHTTPD();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRootView = findViewById(android.R.id.content).getRootView();
        mRootView.setBackgroundColor(Color.parseColor("#ffffff"));
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /* 端末のIPを取得 */
    public String getLocalIpAddress()
    {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface





                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(inetAddress
                            .getHostAddress())) {

                        String ipAddr = inetAddress.getHostAddress();
                        return ipAddr;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.d(TAG, ex.toString());
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class SettingNanoHTTPD extends NanoHTTPD {

        /**
         * Starts a HTTP server to given port.<p>
         * Throws an IOException if the socket is already in use
         *
         * @param port
         * @param wwwroot
         */
        public SettingNanoHTTPD() throws IOException {
            super(PORT, new File("./"));
        }

        public Response serve( String uri, String method, Properties header, Properties parms, Properties files )
        {
            Log.d(TAG, method + " '" + uri + "' ");
            String msg = "<html><body><h1>Please set background color(e.g. #aaeecc)</h1>\n";
            if ( parms.getProperty("backgroundcolor") == null ) {
                msg +=
                        "<form action='?' method='get'>\n" +
                                "  <p>BackGround Color: <input type='text' name='backgroundcolor'></p>\n" +
                                "</form>\n";
            }else {
                final String str = parms.getProperty("backgroundcolor");
                try {
                    final String strBackground = URLDecoder.decode(str, "UTF-8");
                    msg += "<p>Set Background, " + strBackground + "!</p>";
                    msg += "<form action='?' method='get'>\n" +
                            "  <p>BackGround Color: <input type='text' name='backgroundcolor'></p>\n" +
                            "</form>\n";
                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            mRootView.setBackgroundColor(Color.parseColor(strBackground));
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            msg += "</body></html>\n";
            return new com.andbrowser.settingnanohttpd.NanoHTTPD.Response( HTTP_OK, MIME_HTML, msg );
        }


    }
}
