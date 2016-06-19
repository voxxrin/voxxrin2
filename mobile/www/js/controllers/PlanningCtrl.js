'use strict';

angular.module('voxxrin')
    .controller('PlanningCtrl', function ($stateParams, $scope, Event, Presentation) {

        $scope.presentations = Presentation.fromDay($stateParams.dayId);

    });