package it.uniba.dib.sms22235.tasks.organization;

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
import it.uniba.dib.sms22235.entities.users.Organization;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class OrganizationNavigationActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private transient Organization organization;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_organization_navigation);

        BottomNavigationView navViewVeterinarian = findViewById(R.id.nav_view_org);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.organization_profile
        ).build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_organization_navigation);

        assert navHostFragment != null;

        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navViewVeterinarian, navController);

        Bundle loginBundle = getIntent().getExtras(); // get the login bundle

        // Extract the bundle data sent from login activity
        if (loginBundle != null) {
            organization = (Organization) loginBundle.getSerializable(KeysNamesUtils.BundleKeys.ORGANZIATION);
        }

        fab = findViewById(R.id.floatingActionButton_organization);
        }

        public FloatingActionButton getFab() {
        return fab;
    }
    }
