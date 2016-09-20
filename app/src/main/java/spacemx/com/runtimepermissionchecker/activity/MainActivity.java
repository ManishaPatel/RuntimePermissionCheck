package spacemx.com.runtimepermissionchecker.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import spacemx.com.runtimepermissionchecker.R;
import spacemx.com.runtimepermissionchecker.permissionhelper.RuntimePermissionChecker;

public class MainActivity extends AppCompatActivity{

    private RuntimePermissionChecker permissionChecker;
    private String[] permissionToCheck = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        * CREATE OBJECT FOR HEADLESS FRAGMENT TO CHECK PERMISSION
        * */
        permissionChecker = (RuntimePermissionChecker) getSupportFragmentManager()
                .findFragmentByTag(RuntimePermissionChecker.TAG);
        if (permissionChecker == null) {
            permissionChecker = RuntimePermissionChecker.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(permissionChecker, RuntimePermissionChecker.TAG)
                    .commit();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final RuntimePermissionChecker finalPermissionChecker = permissionChecker;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean permissionGranted = finalPermissionChecker.checkPermissions(permissionToCheck);
                if(permissionGranted)
                    Toast.makeText(MainActivity.this, "All permissions granted!", Toast.LENGTH_SHORT).show();
            }
        });
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // CALL FRAGMENT METHOD ON PERMISSION CALLBACK RECIEVED
        permissionChecker.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }
}
