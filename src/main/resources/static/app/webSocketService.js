(function() {
  'use strict';
  angular.module('app.services').factory('webSocket', webSocketService);

  webSocketService.$inject = [ '$rootScope' ];
  function webSocketService($rootScope) {
    var stompClient;
    var defaultDebug;

    return {
      init: function(url) {
        stompClient = Stomp.over(new SockJS(url));
        defaultDebug = stompClient.debug;
        this.toggleDebug(localStorage.getItem('websocketdebug'));
      },
      connect: function(successCallback, errorCallback) {

        stompClient.connect({}, function(frame) {
          $rootScope.$apply(function() {
            successCallback(frame);
          });
        }, function(error) {
          $rootScope.$apply(function() {
            errorCallback(error);
          });
        });
      },
      subscribe: function(destination, callback) {
        stompClient.subscribe(destination, function(message) {
          $rootScope.$apply(function() {
            callback(message);
          });
        });
      },
      send: function(destination, headers, object) {
        stompClient.send(destination, headers, object);
      },
      toggleDebug: function(enable) {
        if (typeof stompClient.debug === "function" || enable === "false") {
          stompClient.debug = null;
          enable = false;
        } else {
          stompClient.debug = defaultDebug;
          enable = true;
        }
        localStorage.setItem('websocketdebug', enable);
      },
    }

  }
})();
