package it.uniba.dib.sms22235.activities.veterinarian;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import it.uniba.dib.sms22235.R;

public class VeterinarianNavigationActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_veterinarian_navigation);

        BottomNavigationView navViewVeterinarian = findViewById(R.id.nav_view_vet);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.veterinarian_profile, R.id.veterinarian_reservation
        ).build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_veterinarian_navigation);

        assert navHostFragment != null;

        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navViewVeterinarian, navController);

        fab = findViewById(R.id.floatingActionButton_veterinarian);
    }
}
