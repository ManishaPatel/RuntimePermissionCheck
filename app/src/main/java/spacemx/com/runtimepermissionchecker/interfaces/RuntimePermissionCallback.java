package spacemx.com.runtimepermissionchecker.interfaces;

/**
 * Created by credencys on 13/9/16.
 */
public interface RuntimePermissionCallback {
    void onPermissionGranted();
    void displayRationaleDialog();
    void displaySettingsDialog();
}