(function() {
  'use strict';
  angular.module('app').service('modalService', modalService);

  modalService.$inject = [ '$uibModal' ];
  function modalService($uibModal) {
    var service = this; // jshint ignore:line
    /* Konstanten */

    /* Einfache Variablen */

    /* Komplexe Variablen */
    service.modalInstance = null;

    /* Öffentliche Methoden */
    service.close = close;
    service.open = open;
    service.openSimple = openSimple;

    /* Öffentliche Methoden Implementierung */
    function close() {
      service.modalInstance.close();
    }

    function open(config) {
      service.modalInstance = $uibModal.open({
        animation: false,
        backdrop: 'static',
        controller: config.controller,
        controllerAs: config.controllerAs || 'ctrl',
        resolve: { config: () => config },
        // small, medium, large, huge
        size: config.size,
        template: getTemplate(config),
      });
    }
    
    function openSimple(config) {
      service.modalInstance = $uibModal.open({
        animation: false,
        backdrop: 'static',
        controller: DialogController,
        controllerAs: 'ctrl',
        resolve: { config: () => config },
        size: config.size || 'small',
        templateUrl: '../app/html/basicDialog.html',
      });
    }

    /* Hilfsmethoden */
    DialogController.$inject = [ 'config' ];
    function DialogController(config) {
      this.config = config;
    }

  }
})();
