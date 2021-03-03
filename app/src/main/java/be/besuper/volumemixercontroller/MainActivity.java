package be.besuper.volumemixercontroller;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;

import be.besuper.volumemixercontroller.websocket.WebSockClient;

public class MainActivity extends AppCompatActivity {

    public static MainActivity activity;
    public static Menu current_menu;

    public static WebSockClient client;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;

        setSupportActionBar(findViewById(R.id.toolbar));
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        current_menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}