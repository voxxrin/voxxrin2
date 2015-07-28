'use strict';

angular.module('voxxrin')
    .controller('DaysListCtrl', function ($stateParams, $state, $scope, Event, Day) {

        $scope.event = Event.get({ id: $stateParams.eventId });
        $scope.days = Day.fromEvent($stateParams.eventId);

        $scope.goToPlanning = function (event) {
            $state.go('planning', {eventId: event._id});
        };

        $scope.goToPresentations = function (day) {
            $state.go('presentations', {dayId: day._id});
        };
    });