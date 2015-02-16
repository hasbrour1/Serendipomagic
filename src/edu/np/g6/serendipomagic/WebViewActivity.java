package edu.np.g6.serendipomagic;

import org.apache.http.cookie.Cookie;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebViewActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Make layout fullscreen, no title bar.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    setContentView(R.layout.fragment_web_view);
	    
	    WebView webview = (WebView)findViewById(R.id.webView1);
	    
	    //Using WebSettings to enable javascript, which is disabled by default.
	    WebSettings webSettings = webview.getSettings();
	    webSettings.setJavaScriptEnabled(true);
	    
	    
	    //Syncing session cookies between HttpClient and WebView
	    Cookie sessionCookie = (Cookie) SendTextActivity.cookieHolder;
	    CookieSyncManager.createInstance(this);
	    CookieManager cookieManager = CookieManager.getInstance();
	    if (sessionCookie != null) {
	    	cookieManager.removeAllCookie(); // Line required to clear cookies. removeSessionCookie() didn't work properly.
	        String cookieString = sessionCookie.getName() + "=" + sessionCookie.getValue() + "; domain=" + sessionCookie.getDomain();
	        cookieManager.setCookie(SendTextActivity.SERENDIPOMATIC_BASE_URL, cookieString);
	        CookieSyncManager.getInstance().sync();
	    }   
	    
	    webview.loadUrl(SendTextActivity.SERENDIPOMATIC_RESULT_URL);
	    

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
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
			View rootView = inflater.inflate(R.layout.fragment_web_view,
					container, false);
			return rootView;
		}
	}

}
