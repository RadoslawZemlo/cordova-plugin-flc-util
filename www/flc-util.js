const exec = require('cordova/exec');

function FlcUtil() {}

FlcUtil.setDebug = function(value) {
  exec(
    function() {},
    function() {},
    'FlcUtil',
    'setDebug',
    [value]
  );
};

FlcUtil.acquireWakeLock = function(a, b, c) {
  if (a !== null && typeof a !== 'number') {
    FlcUtil.acquireWakeLock(null, a, b);
    return;
  }
  const timeout = a;
  const successCallback = b;
  const errorCallback = c;
  exec(
    function() {
      if (successCallback) {
        successCallback();
      }
    },
    function(error) {
      if (errorCallback) {
        errorCallback(error);
      }
    },
    'FlcUtil',
    'acquireWakeLock',
    [timeout || null]
  );
};

FlcUtil.releaseWakeLock = function(successCallback, errorCallback) {
  exec(
    function() {
      if (successCallback) {
        successCallback();
      }
    },
    function(error) {
      if (errorCallback) {
        errorCallback(error);
      }
    },
    'FlcUtil',
    'releaseWakeLock'
  );
};

FlcUtil.setKeepScreenOn = function(value, successCallback, errorCallback) {
  exec(
    function() {
      if (successCallback) {
        successCallback();
      }
    },
    function(error) {
      if (errorCallback) {
        errorCallback(error);
      }
    },
    'FlcUtil',
    'setKeepScreenOn',
    [value]
  );
};

FlcUtil.decodeImage = function(buffer, successCallback, errorCallback) {
  exec(
    function(buffer) {
      if (successCallback) {
        successCallback(buffer);
      }
    },
    function(error) {
      if (errorCallback) {
        errorCallback(error);
      }
    },
    'FlcUtil',
    'decodeImage',
    [buffer]
  );
};

FlcUtil.getIp = function(successCallback, errorCallback) {
  exec(
    function(ip) {
      if (successCallback) {
        successCallback(ip);
      }
    },
    function(error) {
      if (errorCallback) {
        errorCallback(error);
      }
    },
    'FlcUtil',
    'getIp'
  );
};

FlcUtil.getUuid = function(successCallback, errorCallback) {
  exec(
    function(uuid) {
      if (successCallback) {
        successCallback(uuid);
      }
    },
    function(error) {
      if (errorCallback) {
        errorCallback(error);
      }
    },
    'FlcUtil',
    'getUuid'
  );
};

FlcUtil.test = function(value, successCallback, errorCallback) {
  exec(
    function(result) {
      if (successCallback) {
        successCallback(result);
      }
    },
    function(error) {
      if (errorCallback) {
        errorCallback(error);
      }
    },
    'FlcUtil',
    'test',
    [value]
  );
};

FlcUtil.exoCreate = function(uri, successCallback, errorCallback) {
  exec(
    function(event) {
      if (successCallback)
        successCallback(event);
    },
    function(error) {
      if (errorCallback)
        errorCallback(error);
    },
    'FlcUtil',
    'exoCreate',
    [uri]
  )
};

FlcUtil.exoDispose = function(id, successCallback, errorCallback) {
  exec(
    function() {
      if (successCallback)
        successCallback();
    },
    function(error) {
      if (errorCallback)
        errorCallback(error);
    },
    'FlcUtil',
    'exoDispose',
    [id]
  )
};

FlcUtil.exoGetFrame = function(id, successCallback, errorCallback) {
  exec(
    function(result) {
      if (successCallback)
        successCallback(result);
    },
    function(error) {
      if (errorCallback)
        errorCallback(error);
    },
    'FlcUtil',
    'exoGetFrame',
    [id]
  )
};

FlcUtil.exoSetPlaying = function(id, playing, successCallback, errorCallback) {
  exec(
    function() {
      if (successCallback)
        successCallback();
    },
    function(error) {
      if (errorCallback)
        errorCallback(error);
    },
    'FlcUtil',
    'exoSetPlaying',
    [id, playing]
  )
};

FlcUtil.exoSetSpeed = function(id, speed, successCallback, errorCallback) {
  exec(
    function() {
      if (successCallback)
        successCallback();
    },
    function(error) {
      if (errorCallback)
        errorCallback(error);
    },
    'FlcUtil',
    'exoSetSpeed',
    [id, speed]
  )
};

FlcUtil.exoSetVolume = function(id, volume, successCallback, errorCallback) {
  exec(
    function() {
      if (successCallback)
        successCallback();
    },
    function(error) {
      if (errorCallback)
        errorCallback(error);
    },
    'FlcUtil',
    'exoSetVolume',
    [id, volume]
  )
};

FlcUtil.exoSeek = function(id, position, successCallback, errorCallback) {
  exec(
    function() {
      if (successCallback)
        successCallback();
    },
    function(error) {
      if (errorCallback)
        errorCallback(error);
    },
    'FlcUtil',
    'exoSeek',
    [id, position]
  )
};

module.exports = FlcUtil;

