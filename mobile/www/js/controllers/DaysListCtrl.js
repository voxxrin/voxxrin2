'use strict';

angular.module('voxxrin')
    .controller('DaysListCtrl', function ($stateParams, $state, $scope, Day) {

        $scope.days = Day.fromEvent($stateParams.eventId);

        $scope.goToPresentations = function (day) {
            $state.go('presentations', {dayId: day._id});
        };
    });