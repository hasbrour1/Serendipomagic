package edu.np.g6.serendipomagic;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieSyncManager;

public class SendTextActivity extends ActionBarActivity {
	
	
	static String message;
	static String charset = "UTF-8";
	static String contentType = "application/x-www-form-urlencoded";
	public static Cookie cookieHolder = null;
	public final static String SERENDIPOMATIC_BASE_URL = "http://serendipomatic.com";
	public final static String SERENDIPOMATIC_RESULT_URL = "http://serendipomatic.com/discoveries/";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Make layout fullscreen, no title bar.
				requestWindowFeature(Window.FEATURE_NO_TITLE);
		        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
		                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.fragment_send_text);
		
		// Get message from Intent in MainActivity.
		Intent intent = getIntent();
		message = intent.getStringExtra(Activity2.EXTRA_MESSAGE);
		
		postData();
	}
	
	public void postData()
	{
		// Need to handle networking in AsyncTask
		new NetworkingTask().execute(message);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.send_text, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.send_result) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_send_text,
					container, false);
			return rootView;
		}
	}

	public class NetworkingTask extends AsyncTask<String, String, String>
	{

		@Override
		protected String doInBackground(String... args) {
			String msgToSend = args[0];
			String csrftoken = new String();
			String responseString = new String();
			Header[] cookie = null;
					

			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(SendTextActivity.SERENDIPOMATIC_BASE_URL);
			
			
			
			
	   	    HttpPost httppost = new HttpPost(SendTextActivity.SERENDIPOMATIC_BASE_URL);
	   	 try {
	   		 
	   		HttpResponse response = httpclient.execute(httpget);
	   		
	   		// Parse csrftoken from cookie header. Required for POST verification
	   		cookie = (Header[]) response.getHeaders("Set-Cookie");
	   		String cookieString = new String();
            for(int h=0; h<cookie.length; h++){
            	cookieString = cookie[h].toString();
            	if (cookieString.indexOf("csrftoken") != -1)
				{
					int csrftokenIndex = cookieString.indexOf("csrftoken");
					int csrftokenSemicolon = cookieString.indexOf(";", csrftokenIndex);
					csrftoken = cookieString.substring(csrftokenIndex + 10, csrftokenSemicolon);
				}
            }
	   		 
           // Assemble POST query
	   	   List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	       nameValuePairs.add(new BasicNameValuePair("text", msgToSend));
	       nameValuePairs.add(new BasicNameValuePair("csrfmiddlewaretoken", csrftoken));
	       httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	   	   response = httpclient.execute(httppost);
	
	   	   // TODO Could probably be combined with above cookie code, but too tired to figure that out right now.
		   	List<Cookie> cookies = httpclient.getCookieStore().getCookies();
		   	for (int i = 0; i < cookies.size(); i++) {
		   	    cookieHolder = cookies.get(i);
		   	}
	   	   

	   	   // Build response string. Consists of HTML from response page.
		   	responseString = new BasicResponseHandler().handleResponse(response);
		   	Log.i("Response String", responseString);
		   	

	     } catch (ClientProtocolException e) {
	    	 e.printStackTrace();
	     } catch (IOException e) {
	    	 e.printStackTrace();
	     }
      	 
			
			return responseString;
		}
		
		protected void onPostExecute(String responseString)
		{
			InputStream inputStream = null;
			String outString = null;
			
			//
			
			Intent myWebViewIntent = new Intent(getApplicationContext(), WebViewActivity.class);
		   	startActivity(myWebViewIntent);
			
		}
		
	}
	
}
