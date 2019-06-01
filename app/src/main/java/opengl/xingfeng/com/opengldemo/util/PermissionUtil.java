package opengl.xingfeng.com.opengldemo.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.lang.reflect.Method;

/**
 * Created by nicholas on 16-4-19.
 * a util to determine whether our application have been granted a particular
 * permission.
 * for android 6.0 dynamic permission check
 */
public final class PermissionUtil {
    private PermissionUtil() {
    }

    /**
     * Determine whether <em>you</em> have been granted a particular permission.
     */
    public static boolean checkSelfPermission(String permission) {
        if (Build.VERSION.SDK_INT < 23) {
            // under android 23, the permission check is not strictly necessary
            return true;
        }
        try {
            Method checkPermissionMethod = Context.class
                    .getMethod("checkSelfPermission", String.class);
            checkPermissionMethod.setAccessible(true);
            Object result = checkPermissionMethod.invoke(
                    ContextProvider.getApplicationContext(), permission);
            return result != null && result instanceof Integer
                    && PackageManager.PERMISSION_GRANTED == (Integer) result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean hasPermission(Context context, String permission){
        PackageManager pm = context.getPackageManager();
        boolean result = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission(permission, context.getPackageName()));
        return result;
    }
}
