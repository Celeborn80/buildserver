(function() {
  'use strict';
  angular.module('app').component('listBuilds', {
    controller: listBuildsCtrl,
    controllerAs: 'ctrl',
    templateUrl: '../app/html/list-builds.html',
  });

  listBuildsCtrl.$inject = [ '$http', '$interval', 'modalService' ];
  function listBuildsCtrl($http, $interval, modalService) {
    var ctrl = this;
    /* Konstanten */

    /* Einfache Variablen */
    var updateBuildOutputInterval;

    /* Komplexe Variablen */
    ctrl.buildJobs = [];
    ctrl.builds = [];
    ctrl.buildRequest = [];
    ctrl.buildHistory = [];
    ctrl.selectedBuild = null;
    ctrl.selectedBuildOutput = null;

    /* Öffentliche Methoden */
    ctrl.hideSelectedBuild = hideSelectedBuild;
    ctrl.listBuildJobs = listBuildJobs;
    ctrl.runJob = runJob;
    ctrl.showSelectedBuild = showSelectedBuild;
    ctrl.cancelBuild = cancelBuild;
    ctrl.showDetails = showDetails;

    /* Öffentliche Methoden Implementierung */
    function listBuildJobs() {
      $http.get('http://pcsk:8080/builds/jobs/list').then(function(response) {
        ctrl.buildJobs = response.data;
      });
    }

    function listBuildHistorie() {
      $http.get('http://pcsk:8080/builds/history/list').then(function(response) {
        ctrl.buildHistory = response.data;
      });
    }

    function runJob(id) {
      $http.get('http://pcsk:8080/builds/jobs/' + id + '/run').then(function() {
        reload();
      });
    }

    function cancelBuild(buildNumber) {
      $http.get('http://pcsk:8080/builds/jobs/' + buildNumber + '/cancel').then(function() {
        reload();
        hideSelectedBuild(buildNumber);
      });
    }

    function showSelectedBuild(build) {
      hideSelectedBuild();
      ctrl.selectedBuild = build;
      updateSelectedBuildOutput();
      updateBuildOutputInterval = $interval(updateSelectedBuildOutput, 1000);
    }

    function updateSelectedBuildOutput() {
      $http.get('http://pcsk:8080/builds/' + ctrl.selectedBuild.buildNumber + '/output').then(function(response) {
        if (response.data === 'finished') {
          hideSelectedBuild();
        } else {
          ctrl.selectedBuildOutput = response.data.output;
        }
      });
    }

    function hideSelectedBuild(buildNumber) {
      if (buildNumber === undefined || (ctrl.selectedBuild && ctrl.selectedBuild.buildNumber === buildNumber)) {
        ctrl.selectedBuild = null;
        if (updateBuildOutputInterval) {
          $interval.cancel(updateBuildOutputInterval);
        }
      }
    }

    function showDetails(buildNumber) {
      $http.get('http://pcsk:8080/builds/history/' + buildNumber).then(function(response) {
        modalService.openSimple(dialogDefinition(response.data.output));
      });
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
      listBuildJobs();
      reload();
      $interval(reload, 2000);
    }

    function listBuilds() {
      $http.get('http://pcsk:8080/builds/active/list').then(function(response) {
        ctrl.builds = response.data;
      });
    }

    function listBuildQueue() {
      $http.get('http://pcsk:8080/builds/requests/list').then(function(response) {
        ctrl.buildRequest = response.data;
      });
    }

    function reload() {
      listBuilds();
      listBuildQueue();
      listBuildHistorie();
    }

  }
})();
