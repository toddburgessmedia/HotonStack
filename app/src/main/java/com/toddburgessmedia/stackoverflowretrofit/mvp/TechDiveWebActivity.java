/*
 * Copyright 2016 Todd Burgess Media
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.toddburgessmedia.stackoverflowretrofit.mvp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.toddburgessmedia.stackoverflowretrofit.PreferencesActivity;
import com.toddburgessmedia.stackoverflowretrofit.PrivacyPolicyActivity;
import com.toddburgessmedia.stackoverflowretrofit.R;

public class TechDiveWebActivity extends AppCompatActivity {

    WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url = getIntent().getStringExtra("url");
        webView = new WebView(this);
        webView.clearCache(false);
        webView.getSettings().setJavaScriptEnabled(true);
        setContentView(webView);
        webView.loadUrl(url);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.webview_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.webview_menu_refresh:
                webView.reload();
                break;
            case R.id.webview_menu_preferences:
                Intent i = new Intent(this,PreferencesActivity.class);
                startActivity(i);
                break;
            case R.id.webview_menu_privacy:
                Intent pi = new Intent(this, PrivacyPolicyActivity.class);
                startActivity(pi);
                break;
        }
        return true;
    }

}
