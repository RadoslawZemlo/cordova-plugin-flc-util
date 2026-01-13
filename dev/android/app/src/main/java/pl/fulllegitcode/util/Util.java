package pl.fulllegitcode.util;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.WIFI_SERVICE;

public class Util {

  public static final int WAKE_LOCK_TIMEOUT = 60 * 60 * 1000;

  private static PowerManager.WakeLock _wakeLock;
  private static TemperatureReceiver _temperatureReceiver;

  public static String acquireWakeLock(Context context) {
    return acquireWakeLock(WAKE_LOCK_TIMEOUT, context);
  }

  public static String acquireWakeLock(int timeout, Context context) {
    try {
      PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
      if (powerManager != null) {
        _wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "FlcUtil");
        _wakeLock.acquire(timeout);
        if (_wakeLock != null && _wakeLock.isHeld()) {
          Log.d("FlcUtil", String.format(Locale.ENGLISH, "wake lock acquired. timeout=%d", timeout));
          return null;
        }
      }
      Log.e("FlcUtil", String.format(Locale.ENGLISH, "acquire wake lock failed. wakeLock=%s", _wakeLock.toString()));
      return "wake lock is null or not held ;)";
    } catch (NullPointerException e) {
      Log.e("FlcUtil", String.format(Locale.ENGLISH, "acquire wake lock error. message=%s", e.getMessage()));
      return e.getMessage();
    }
  }

  public static String releaseWakeLock() {
    if (_wakeLock == null) {
      return "wake lock does not exist";
    }
    if (!_wakeLock.isHeld()) {
      return "wake lock is not held";
    }
    _wakeLock.release();
    return null;
  }

  public static byte[] decodeImage(byte[] bytes) {
    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
    bitmap.copyPixelsToBuffer(buffer);
    return buffer.array();
  }

  public static String getIp(Context context) {
    try {
      WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
      if (manager != null) {
        int ip = manager.getConnectionInfo().getIpAddress();
        if (ip != 0) {
          return String.format(Locale.ENGLISH, "%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
        }
      }
    } catch (Exception e) {
      Log.e("FlcUtil", String.format(Locale.ENGLISH, "get ip. message=%s", e.getMessage()));
    }
    return "192.168.43.1";
  }

  //region permissions

  public static PermissionResult checkPermissions(Activity activity, String[] permissions) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      return _makePermissionResult(permissions);
    }
    int[] grantResults = new int[permissions.length];
    for (int i = 0; i < permissions.length; i++) {
      grantResults[i] = activity.checkSelfPermission(permissions[i]);
    }
    return _makePermissionResult(permissions, grantResults);
  }

  public static RequestPermissionsDelegate requestPermissions(final String[] permissions, final RequestPermissionsCallback callback) {
    return new RequestPermissionsDelegate(new RequestPermissionsDelegate.InnerCallback() {
      @Override
      public void run(Activity activity, int requestCode) {
        Log.d("FlcUtil", String.format(Locale.ENGLISH, "[Util.requestPermissions] requestCode=%d", requestCode));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
          callback.onResult(_makePermissionResult(permissions));
        } else {
          activity.requestPermissions(permissions, requestCode);
        }
      }

      @Override
      public void run(Fragment fragment, int requestCode) {
        Log.d("FlcUtil", String.format(Locale.ENGLISH, "[Util.requestPermissions] requestCode=%d", requestCode));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
          callback.onResult(_makePermissionResult(permissions));
        } else {
          fragment.requestPermissions(permissions, requestCode);
        }
      }

      @Override
      public void onRequestPermissionsResult(String[] permissions, int[] grantResults) {
        callback.onResult(_makePermissionResult(permissions, grantResults));
      }
    });
  }

  private static PermissionResult _makePermissionResult(String[] permissions) {
    PermissionResult result = new PermissionResult();
    result.granted = permissions;
    result.denied = new String[0];
    return result;
  }

  private static PermissionResult _makePermissionResult(String[] permissions, int[] grantResults) {
    ArrayList<String> granted = new ArrayList<>();
    ArrayList<String> denied = new ArrayList<>();
    for (int i = 0; i < permissions.length; i++) {
      if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
        granted.add(permissions[i]);
      } else {
        denied.add(permissions[i]);
      }
    }
    PermissionResult result = new PermissionResult();
    result.granted = granted.toArray(new String[0]);
    result.denied = denied.toArray(new String[0]);
    return result;
  }

  public static String getExternalFilesDirs(Context context) {
    File[] dirs = context.getExternalFilesDirs(null);
    for (File dir : dirs) {
      if (Environment.isExternalStorageRemovable(dir)) {
        return getRootPath(dir.getAbsolutePath());
      }
    }
    return null;
  }

  public static String getRootPath(String fullPath) {
    if (fullPath == null || fullPath.isEmpty()) {
      return null;
    }
    int androidIndex = fullPath.toLowerCase().indexOf("/android");
    if (androidIndex > 0) {
      return fullPath.substring(0, androidIndex);
    }
    return null;
  }

  //endregion

  public static float getTemperature(Activity activity) {
    if (_temperatureReceiver == null) {
      _temperatureReceiver = new TemperatureReceiver(activity);
    }
    return _temperatureReceiver.temperature();
  }

}
