'use strict';

angular.module('voxxrin')
    .controller('DaysListCtrl', function ($stateParams, $scope, Session, Event, Day) {

        $scope.days = Day.fromEvent($stateParams.eventId);
        $scope.isEventAdmin = false;

        Event.get({id: $stateParams.eventId}).$promise.then(function (event) {
            $scope.currentEvent = event;
            if (Session.getPrincipal) {
                Session.getPrincipal().then(function (principal) {
                    var roles = principal.principalRoles;
                    if (principal.admin || (roles && roles.indexOf(event.eventId + '-admin') >= 0)) {
                        $scope.isEventAdmin = true;
                    }
                });
            }
        });
    });