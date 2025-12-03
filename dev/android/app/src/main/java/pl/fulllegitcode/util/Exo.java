package pl.fulllegitcode.util;

import android.app.Activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.zip.DeflaterOutputStream;

import pl.fulllegitcode.flcexoplayer.AesDataSource;
import pl.fulllegitcode.flcexoplayer.Player;

public class Exo {
  public interface Callback {
    void onDispose();

    void onDuration(long duration);

    void onError(int error);

    void onImageAvailable(int width, int height);

    void onPlaybackState(int state);

    void onPosition(long position);
  }

  private static int _nextId = 1;

  private final int _id = _nextId++;
  private final Callback callback;
  private final Player player;
  boolean durationSet = false;
  int error = -1;
  private boolean imageAvailable = false;
  int playbackState = 1;
  long position = 0;
  private boolean disposed = false;

  public int id() {
    return _id;
  }

  public Exo(Activity activity, ExecutorService threadPool, String uri, Callback callback) {
    this.callback = callback;
    player = new Player(activity);
    player.prepare(uri, new String[]{}, false);
    threadPool.execute(() -> {
      try {
        while (!disposed) {
          activity.runOnUiThread(() -> {
            if (disposed)
              return;
            if (!durationSet) {
              long duration = player.getDuration();
              if (duration > 0) {
                durationSet = true;
                callback.onDuration(duration);
              }
            }
            int errorNew = player.getPlaybackErrorType();
            if (errorNew != error) {
              error = errorNew;
              callback.onError(errorNew);
            }
            boolean imageAvailableNew = player.isImageAvailable();
            if (imageAvailableNew && !imageAvailable) {
              imageAvailable = true;
              callback.onImageAvailable(player.getImageWidth(), player.getImageHeight());
            }
            int playbackStateNew = player.getPlaybackState();
            if (playbackStateNew != playbackState) {
              playbackState = playbackStateNew;
              callback.onPlaybackState(playbackStateNew);
            }
            long positionNew = player.getCurrentPosition();
            if (positionNew != position) {
              position = positionNew;
              callback.onPosition(positionNew);
            }
          });
          Thread.sleep(5);
        }
      } catch (InterruptedException ignored) {
      }
    });
  }

  public byte[] getFrame() throws IOException {
    imageAvailable = false;
    byte[] data = player.getImageBytes();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DeflaterOutputStream dos = new DeflaterOutputStream(baos);
    dos.write(data);
    dos.close();
    return baos.toByteArray();
  }

  public void setKey(String key) {
    AesDataSource.setKey(key);
  }

  public void setPlaying(boolean playing) {
    player.setPlayWhenReady(playing);
  }

  public void setSpeed(float speed) {
    player.setSpeed(speed);
  }

  public void setVolume(float volume) {
    player.setVolume(volume);
  }

  public void seek(long position) {
    player.seekTo(position);
  }

  public void dispose() {
    disposed = true;
    player.dispose();
    callback.onDispose();
  }
}
