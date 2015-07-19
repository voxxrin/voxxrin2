'use strict';

angular.module('voxxrin')
    .controller('EventsListCtrl', function ($scope, Event) {

        $scope.events = Event.query();

    });