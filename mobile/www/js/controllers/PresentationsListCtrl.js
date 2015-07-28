'use strict';

angular.module('voxxrin')
    .controller('PrensentationsListCtrl', function ($stateParams, $state, $scope, Day, Presentation) {

        $scope.day = Day.get({ id: $stateParams.dayId });
        $scope.presentations = Presentation.fromDay($stateParams.dayId);

        $scope.goToPresentation = function (presentation) {
            $state.go('presentation', {id: presentation._id});
        };
    });