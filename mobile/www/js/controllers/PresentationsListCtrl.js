'use strict';

angular.module('voxxrin')
    .controller('PresentationsListCtrl', function ($stateParams, $scope, Day) {

        $scope.days = Day.fromEvent($stateParams.eventId);
    });