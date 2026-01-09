package pl.fulllegitcode.utilcordova;

import android.app.Activity;
import android.view.WindowManager;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

import pl.fulllegitcode.util.Exo;
import pl.fulllegitcode.util.Util;

public class UtilCordova extends CordovaPlugin {
  public static final String ACTION_ACQUIRE_WAKE_LOCK = "acquireWakeLock";
  public static final String ACTION_RELEASE_WAKE_LOCK = "releaseWakeLock";
  public static final String ACTION_SET_KEEP_SCREEN_ON = "setKeepScreenOn";
  public static final String ACTION_DECODE_IMAGE = "decodeImage";
  public static final String ACTION_GET_IP = "getIp";
  public static final String ACTION_EXO_CREATE = "exoCreate";
  public static final String ACTION_EXO_DISPOSE = "exoDispose";
  public static final String ACTION_EXO_GET_FRAME = "exoGetFrame";
  public static final String ACTION_EXO_SET_KEY = "exoSetKey";
  public static final String ACTION_EXO_SET_PLAYING = "exoSetPlaying";
  public static final String ACTION_EXO_SET_SPEED = "exoSetSpeed";
  public static final String ACTION_EXO_SET_VOLUME = "exoSetVolume";
  public static final String ACTION_EXO_SEEK = "exoSeek";

  private final ArrayList<Exo> exos = new ArrayList<>();

  @Override
  public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
    Activity activity = cordova.getActivity();
    if (action.equals(ACTION_ACQUIRE_WAKE_LOCK)) {
      _acquireWakeLockThread(!args.isNull(0) ? args.getInt(0) : Util.WAKE_LOCK_TIMEOUT, callbackContext);
      return true;
    }
    if (action.equals(ACTION_RELEASE_WAKE_LOCK)) {
      _releaseWakeLockThread(callbackContext);
      return true;
    }
    if (action.equals(ACTION_SET_KEEP_SCREEN_ON)) {
      _setKeepScreenOn(args.getBoolean(0), callbackContext);
      return true;
    }
    if (action.equals(ACTION_DECODE_IMAGE)) {
      _decodeImage(args.getArrayBuffer(0), callbackContext);
      return true;
    }
    if (action.equals(ACTION_GET_IP)) {
      callbackContext.success(Util.getIp(cordova.getActivity()));
      return true;
    }
    if (action.equals(ACTION_EXO_CREATE)) {
      activity.runOnUiThread(() -> {
        Exo exo = null;
        try {
          ExecutorService threadPool = cordova.getThreadPool();
          String uri = args.getString(0);
          exo = new Exo(activity, threadPool, uri, new Exo.Callback() {
            @Override
            public void onDispose() {
              PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
              result.setKeepCallback(false);
              callbackContext.sendPluginResult(result);
            }

            @Override
            public void onDuration(long duration) {
              try {
                JSONObject event = new JSONObject();
                event.put("type", "duration");
                event.put("duration", duration);
                PluginResult result = new PluginResult(PluginResult.Status.OK, event);
                result.setKeepCallback(true);
                callbackContext.sendPluginResult(result);
              } catch (JSONException ignored) {
              }
            }

            @Override
            public void onError(int error) {
              try {
                JSONObject event = new JSONObject();
                event.put("type", "error");
                event.put("error", error);
                PluginResult result = new PluginResult(PluginResult.Status.OK, event);
                result.setKeepCallback(true);
                callbackContext.sendPluginResult(result);
              } catch (JSONException ignored) {
              }
            }

            @Override
            public void onImageAvailable(int width, int height) {
              try {
                JSONObject event = new JSONObject();
                event.put("type", "imageAvailable");
                event.put("width", width);
                event.put("height", height);
                PluginResult result = new PluginResult(PluginResult.Status.OK, event);
                result.setKeepCallback(true);
                callbackContext.sendPluginResult(result);
              } catch (JSONException ignored) {
              }
            }

            @Override
            public void onPlaybackState(int state) {
              try {
                JSONObject event = new JSONObject();
                event.put("type", "playbackState");
                event.put("state", state);
                PluginResult result = new PluginResult(PluginResult.Status.OK, event);
                result.setKeepCallback(true);
                callbackContext.sendPluginResult(result);
              } catch (JSONException ignored) {
              }
            }

            @Override
            public void onPosition(long position) {
              try {
                JSONObject event = new JSONObject();
                event.put("type", "position");
                event.put("position", position);
                PluginResult result = new PluginResult(PluginResult.Status.OK, event);
                result.setKeepCallback(true);
                callbackContext.sendPluginResult(result);
              } catch (JSONException ignored) {
              }
            }
          });
          exos.add(exo);
          JSONObject event = new JSONObject();
          event.put("type", "create");
          event.put("id", exo.id());
          PluginResult result = new PluginResult(PluginResult.Status.OK, event);
          result.setKeepCallback(true);
          callbackContext.sendPluginResult(result);
        } catch (Exception e) {
          if (exo != null) {
            exo.dispose();
            exos.remove(exo);
          }
          callbackContext.error(e.getMessage());
        }
      });
      return true;
    }
    if (action.equals(ACTION_EXO_DISPOSE)) {
      activity.runOnUiThread(() -> {
        try {
          Exo exo = getExo(args.getInt(0));
          if (exo != null) {
            exo.dispose();
            exos.remove(exo);
            callbackContext.success();
          } else {
            callbackContext.error("exo not found");
          }
        } catch (Exception e) {
          callbackContext.error(e.getMessage());
        }
      });
      return true;
    }
    if (action.equals(ACTION_EXO_GET_FRAME)) {
      activity.runOnUiThread(() -> {
        try {
          Exo exo = getExo(args.getInt(0));
          if (exo != null) {
            byte[] data = exo.getFrame();
            if (data != null)
              callbackContext.success(data);
            else
              callbackContext.success(0);
          } else {
            callbackContext.error("exo not found");
          }
        } catch (Exception e) {
          callbackContext.error(e.getMessage());
        }
      });
      return true;
    }
    if (action.equals(ACTION_EXO_SET_KEY)) {
      activity.runOnUiThread(() -> {
        try {
          Exo exo = getExo(args.getInt(0));
          if (exo != null) {
            String key = args.getString(1);
            exo.setKey(key);
            callbackContext.success();
          } else {
            callbackContext.error("exo not found");
          }
        } catch (Exception e) {
          callbackContext.error(e.getMessage());
        }
      });
      return true;
    }
    if (action.equals(ACTION_EXO_SET_PLAYING)) {
      activity.runOnUiThread(() -> {
        try {
          Exo exo = getExo(args.getInt(0));
          if (exo != null) {
            boolean playing = args.getBoolean(1);
            exo.setPlaying(playing);
            callbackContext.success();
          } else {
            callbackContext.error("exo not found");
          }
        } catch (Exception e) {
          callbackContext.error(e.getMessage());
        }
      });
      return true;
    }
    if (action.equals(ACTION_EXO_SET_SPEED)) {
      activity.runOnUiThread(() -> {
        try {
          Exo exo = getExo(args.getInt(0));
          if (exo != null) {
            double speed = args.getDouble(1);
            exo.setSpeed((float)speed);
            callbackContext.success();
          } else {
            callbackContext.error("exo not found");
          }
        } catch (Exception e) {
          callbackContext.error(e.getMessage());
        }
      });
      return true;
    }
    if (action.equals(ACTION_EXO_SET_VOLUME)) {
      activity.runOnUiThread(() -> {
        try {
          Exo exo = getExo(args.getInt(0));
          if (exo != null) {
            double volume = args.getDouble(1);
            exo.setVolume((float)volume);
            callbackContext.success();
          } else {
            callbackContext.error("exo not found");
          }
        } catch (Exception e) {
          callbackContext.error(e.getMessage());
        }
      });
      return true;
    }
    if (action.equals(ACTION_EXO_SEEK)) {
      activity.runOnUiThread(() -> {
        try {
          Exo exo = getExo(args.getInt(0));
          if (exo != null) {
            long position = args.getLong(1);
            exo.seek(position);
            callbackContext.success();
          } else {
            callbackContext.error("exo not found");
          }
        } catch (Exception e) {
          callbackContext.error(e.getMessage());
        }
      });
      return true;
    }
    return false;
  }

  @Override
  public void onDestroy() {
    Util.releaseWakeLock();
    super.onDestroy();
  }

  private void _acquireWakeLockThread(final int timeout, final CallbackContext callbackContext) {
    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        String error = Util.acquireWakeLock(timeout, cordova.getActivity());
        if (error != null) {
          callbackContext.error(error);
          return;
        }
        callbackContext.success();
      }
    });
  }

  private void _releaseWakeLockThread(final CallbackContext callbackContext) {
    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        String error = Util.releaseWakeLock();
        if (error != null) {
          callbackContext.error(error);
          return;
        }
        callbackContext.success();
      }
    });
  }

  private void _setKeepScreenOn(final boolean value, final CallbackContext callbackContext) {
    cordova.getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (value) {
          cordova.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
          cordova.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        callbackContext.success();
      }
    });
  }

  private void _decodeImage(final byte[] bytes, final CallbackContext callbackContext) {
    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        callbackContext.success(Util.decodeImage(bytes));
      }
    });
  }

  private Exo getExo(int id) {
    for (int i = 0; i < exos.size(); i++) {
      Exo exo = exos.get(i);
      if (exo.id() == id)
        return exo;
    }
    return null;
  }
}
