'use strict';

/**
 * Main controller
 */
function ContenTypeCtrl($scope, Restangular) {
    $scope.types = Restangular.all('types').getList().$object;
};

/*
 * Error.
 */
function ErrorCtrl() {
} ;