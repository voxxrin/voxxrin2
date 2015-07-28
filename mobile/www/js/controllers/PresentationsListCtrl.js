'use strict';

angular.module('voxxrin')
    .controller('PrensentationsListCtrl', function ($stateParams, $state, $scope, Presentation) {

        $scope.presentations = Presentation.fromDay($stateParams.dayId);

        $scope.goToPresentation = function (presentation) {
            $state.go('presentation', {id: presentation._id});
        };
    });