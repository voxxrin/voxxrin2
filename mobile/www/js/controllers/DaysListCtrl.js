'use strict';

angular.module('voxxrin')
    .controller('DaysListCtrl', function ($stateParams, $scope, Event, Day) {

        $scope.currentEvent = Event.get({id: $stateParams.eventId});
        $scope.days = Day.fromEvent($stateParams.eventId);

    });