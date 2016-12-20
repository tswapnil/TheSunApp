package com.example.android.thesunapp;

import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.content.*;
public class DetailActivity extends AppCompatActivity {
    private Button mButton;
    private TextView tvDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mButton = (Button) findViewById(R.id.btn_go_back);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        tvDetail = (TextView) findViewById(R.id.tv_detail);
        Intent received = getIntent();
        String data = received.getStringExtra("ExtraData");
        tvDetail.setText(data);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.detailfile,menu);
        MenuItem menuItem = menu.findItem(R.id.share_detail_action);
        String mimeType = "text/html";
        String textToShare = "Hello There";
        String title = "Share Weather Details";
        ShareCompat.IntentBuilder i2 = ShareCompat.IntentBuilder.from(this)
                .setChooserTitle(title)
                .setType(mimeType)
                .setText(textToShare);
        menuItem.setIntent(i2.getIntent());

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int itemThatWasClicked = menuItem.getItemId();

        if(itemThatWasClicked == R.id.detail_setting_action){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
