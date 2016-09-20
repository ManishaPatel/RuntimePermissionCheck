package spacemx.com.runtimepermissionchecker.permissionhelper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spacemx.com.runtimepermissionchecker.R;

/**
 * Created by credencys on 13/9/16.
 */
public class RuntimePermissionChecker extends Fragment {
    public static final int MY_PERMISSIONS_REQUEST_URI = 101;
    public static final int REQUEST_PERMISSION_SETTING = 102;
    public static final String TAG = "RuntimePermissionHelper";

    private String[] arrPermissions;
    private Activity activityContext;

    public RuntimePermissionChecker() {}

    public static RuntimePermissionChecker newInstance() {
        return new RuntimePermissionChecker();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public boolean checkPermissions(String[] permissions) {
        arrPermissions = permissions;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int myPermission;
            List<String> listPermissionsNeeded = new ArrayList<>();
            for (String permission : arrPermissions) {
                myPermission = ContextCompat.checkSelfPermission(activityContext, permission);
                if (myPermission != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(permission);
                }
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(activityContext, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                        MY_PERMISSIONS_REQUEST_URI);
                return false;
            }
            return true;
        }
        return true;
    }

    public void onRequestPermissionsResult(int reqCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (reqCode) {
            case MY_PERMISSIONS_REQUEST_URI: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                for (String permission : arrPermissions)
                    perms.put(permission, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);

                    boolean perGranted = false;
                    for (String permission : permissions)
                        if (perms.get(permission) == PackageManager.PERMISSION_GRANTED)
                            perGranted = true;

                    // Check for both permissions
                    if (perGranted) {
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        Toast.makeText(getActivity(), "Permission granted!", Toast.LENGTH_SHORT).show();
                    } else {
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        boolean shouldShowRationale = false;
                        for (String perm : arrPermissions) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(activityContext, perm))
                                shouldShowRationale = true;
                        }
                        if (shouldShowRationale) {
                            showPermissionDialog(getString(R.string.permission_dialog_alert), getString(R.string.btn_ok), getString(R.string.btn_cancel),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            checkPermissions(arrPermissions);
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            showPermissionDialog(getString(R.string.permission_dialog_alert), getString(R.string.btn_go_to_setting), getString(R.string.btn_cancel),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts(getString(R.string.key_package), getActivity().getPackageName(), null);
                                            intent.setData(uri);
                                            startActivityForResult(intent, RuntimePermissionChecker.REQUEST_PERMISSION_SETTING);
                                        }
                                    });
                        }
                    }
                }
            }
        }
    }

    private void showPermissionDialog(String message, String posBtnTxt, String negBtnTxt, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(posBtnTxt, okListener)
                .setNegativeButton(negBtnTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Permission(s) were denied!!!", Toast.LENGTH_SHORT).show();
                    }
                })
                .create()
                .show();
    }
}
