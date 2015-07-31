'use strict';

angular.module('voxxrin')
    .controller('NavigationCtrl', function ($scope, $state) {

        $scope.goToHome = function () {
            $state.go('events');
        };

        $scope.goToPlanning = function (event) {
            $state.go('planning', {eventId: event._id});
        };

        $scope.goToDays = function (event) {
            $state.go('days', {eventId: event._id});
        };

        $scope.goToPresentations = function (day) {
            $state.go('presentations', {dayId: day._id});
        };

        $scope.goToPresentation = function (presentation) {
            $state.go('presentation', {id: presentation._id});
        };

    });