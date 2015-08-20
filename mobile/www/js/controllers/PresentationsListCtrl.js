'use strict';

angular.module('voxxrin')
    .controller('PresentationsListCtrl', function ($stateParams, $scope, Day) {

        $scope.days = Day.fromEvent($stateParams.eventId);

        $scope.isActiveDay = function (day) {
            if (day._id === $stateParams.dayId) {
                return ['active'];
            }
            return [];
        };
    });