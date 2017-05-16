(function() {
  'use strict';
  angular.module('app').component('listBuilds', {
    controller: listBuildsCtrl,
    controllerAs: 'ctrl',
    templateUrl: '../app/html/list-builds.html',
  });

  listBuildsCtrl.$inject = [ '$http', '$scope', 'modalService', 'toaster', 'webSocket' ];
  function listBuildsCtrl($http, $scope, modalService, toaster, webSocket) {
    var ctrl = this;
    /* Konstanten */

    /* Einfache Variablen */

    /* Komplexe Variablen */
    ctrl.buildJobs = [];
    ctrl.builds = {};
    ctrl.buildHistory = {};
    ctrl.buildRequest = [];
    ctrl.selectedBuild = null;
    ctrl.buildSize = 0;

    /* Öffentliche Methoden */
    ctrl.cancelBuild = cancelBuild;
    ctrl.hideSelectedBuild = hideSelectedBuild;
    ctrl.runJob = runJob;
    ctrl.showDetails = showDetails;
    ctrl.showSelectedBuild = showSelectedBuild;
    ctrl.toggleWebSocketDebug = toggleWebSocketDebug;

    /* Öffentliche Methoden Implementierung */
    function cancelBuild(buildNumber) {
      $http.get('http://pcsk:8080/builds/jobs/' + buildNumber + '/cancel').then(function() {
        hideSelectedBuild(buildNumber);
      });
    }

    function runJob(id) {
      $http.get('http://pcsk:8080/builds/jobs/' + id + '/run');
    }

    function showDetails(buildNumber) {
      $http.get('http://pcsk:8080/builds/history/' + buildNumber).then(function(response) {
        modalService.openSimple(dialogDefinition(response.data.output));
      });
    }

    function showSelectedBuild(buildNumber) {
      ctrl.selectedBuild = ctrl.builds[buildNumber];
    }

    function toggleWebSocketDebug() {
      webSocket.toggleDebug();
    }

    function dialogDefinition(detailsText) {
      return {
        id: 'BuildHistoryDetails',
        title: 'Build-Details',
        buttons: [ {
          name: 'Schliesen',
          cssClass: 'c2 right light button',
          func: modalService.close
        }, ],
        data: {
          text: detailsText,
        },
      };
    }

    /* Hilfsmethoden */
    init();
    function init() {
      initWebSocket();
      listBuildJobs();
    }

    function initWebSocket() {
      webSocket.init('/ws');

      webSocket.connect(function() {
        toaster.pop('info', 'Websocket-Verbindung', 'Websocket-Verbindung erfolgreich aufgebaut.');

        webSocket.subscribe('/topic/builds/list', function(builds) {
          ctrl.builds = JSON.parse(builds.body);
          ctrl.buildSize = _.size(ctrl.builds);
        });

        webSocket.subscribe('/topic/builds/updated', function(build) {
          var buildObject = JSON.parse(build.body);
          ctrl.builds[buildObject.buildNumber] = buildObject;
          ctrl.buildSize = _.size(ctrl.builds);
          if (ctrl.selectedBuild && ctrl.selectedBuild.buildNumber === buildObject.buildNumber) {
            ctrl.selectedBuild = buildObject;
          }
        });

        webSocket.subscribe('/topic/builds/finished', function(build) {
          var buildObject = JSON.parse(build.body);
          delete ctrl.builds[buildObject.buildNumber];
          ctrl.buildSize = _.size(ctrl.builds);

          hideSelectedBuild(buildObject.buildNumber);
        });

        webSocket.subscribe('/topic/buildRequests/list', function(requests) {
          ctrl.buildRequest = JSON.parse(requests.body);
        });

        webSocket.subscribe('/topic/buildHistory/list', function(history) {
          ctrl.buildHistory = JSON.parse(history.body);
        });

        listBuilds();
        listBuildRequests();
        listBuildHistory();

      }, function(error) {
        toaster.pop('error', 'Error', 'Connection error ' + error);
      });
    }

    function hideSelectedBuild(buildNumber) {
      if (ctrl.selectedBuild && ctrl.selectedBuild.buildNumber === buildNumber) {
        ctrl.selectedBuild = null;
      }
    }

    function listBuilds() {
      webSocket.send('/app/listBuilds', {}, {});
    }

    function listBuildRequests() {
      webSocket.send('/app/listBuildRequests', {}, {});
    }

    function listBuildHistory() {
      webSocket.send('/app/listBuildHistory', {}, {});
    }

    function listBuildJobs() {
      $http.get('http://pcsk:8080/builds/jobs/list').then(function(response) {
        ctrl.buildJobs = response.data;
      });
    }

  }
})();
