'use strict';

angular.module('voxxrin')
    .controller('PresentationsListCtrl', function ($stateParams, $scope, Day) {

        $scope.days = Day.fromEvent($stateParams.eventId);
        $scope.filters = {
            favorite: false,
            reminded: false
        };

        $scope.isActiveDay = function (day) {
            if (day._id === $stateParams.dayId || day.externalId === $stateParams.dayId) {
                return ['active'];
            }
            return [];
        };

        $scope.toggleFilter = function (name) {
            if ($scope.filters[name] === null) {
                return;
            }
            $scope.filters[name] = !$scope.filters[name];
        };

        $scope.isFilterActive = function (name) {
            return $scope.filters[name] != null ? $scope.filters[name] : false;
        };
    });