'use strict';

angular.module('voxxrin')
    .controller('DaysListCtrl', function ($stateParams, $scope, Event) {

        $scope.days = Event.days($stateParams.eventId);

    });