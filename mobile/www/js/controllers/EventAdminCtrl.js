'use strict';

angular.module('voxxrin')
    .controller('EventAdminCtrl', function ($scope, $stateParams, Event, Stats) {

        Event.get({id: $stateParams.eventId}).$promise.then(function (event) {
            $scope.currentEvent = event;
            $scope.stats = Stats.forEvent(event._id);
        });

    });