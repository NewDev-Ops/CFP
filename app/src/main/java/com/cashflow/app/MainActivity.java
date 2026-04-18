package com.cashflow.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    BottomNavigationView nav = findViewById(R.id.bottom_nav);
    nav.setOnNavigationItemSelectedListener(item -> {
      Fragment f = null;
      switch (item.getItemId()) {
        case R.id.nav_home:
          f = new HomeFragment(); break;
        case R.id.nav_history:
          f = new HistoryFragment(); break;
        case R.id.nav_analytics:
          f = new AnalyticsFragment(); break;
        case R.id.nav_settings:
          f = new SettingsFragment(); break;
      }
      if (f != null) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).commit();
        return true;
      }
      return false;
    });
    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }
  }
}
