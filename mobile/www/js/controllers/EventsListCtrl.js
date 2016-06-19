'use strict';

angular.module('voxxrin')
    .controller('EventsListCtrl', function ($scope, $location, Event) {

        $scope.$watch('mode', function (mode) {
            if (mode) {
                $scope.events = Event.query({mode: mode});
            }
        });

        $scope.showEvents = function (mode) {
            $scope.mode = mode;
        };

        $scope.isModeEnabled = function (mode) {
            return $scope.mode === mode;
        };

        $scope.showEvents('future');

    });