package pl.fulllegitcode.util;

import android.app.Activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.zip.DeflaterOutputStream;

import pl.fulllegitcode.flcexoplayer.Player;

public class Exo {
  public interface Callback {
    void onDispose();

    void onError(int error);

    void onImageAvailable(int width, int height);

    void onPlaybackState(int state);
  }

  private static int _nextId = 1;

  private final int _id = _nextId++;
  private final Callback callback;
  private final Player player;
  int error = -1;
  int playbackState = 0;
  private boolean disposed = false;
  private boolean imageAvailable = false;

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
            int errorNew = player.getPlaybackErrorType();
            if (errorNew != error) {
              error = errorNew;
              callback.onError(errorNew);
            }
            int playbackStateNew = player.getPlaybackState();
            if (playbackStateNew != playbackState) {
              playbackState = playbackStateNew;
              callback.onPlaybackState(playbackStateNew);
            }
            boolean imageAvailableNew = player.isImageAvailable();
            if (imageAvailableNew && !imageAvailable) {
              imageAvailable = true;
              callback.onImageAvailable(player.getImageWidth(), player.getImageHeight());
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

  public void setPlaying(boolean playing) {
    player.setPlayWhenReady(playing);
  }

  public void dispose() {
    disposed = true;
    player.dispose();
    callback.onDispose();
  }
}
