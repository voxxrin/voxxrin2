'use strict';

angular.module('voxxrin')
    .controller('EventsListCtrl', function ($scope, $state, Event) {

        $scope.events = Event.query();

        $scope.goToDays = function (event) {
            $state.go('days', { eventId: event._id });
        };

    });