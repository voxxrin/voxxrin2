'use strict';

angular.module('voxxrin')
    .controller('NavigationCtrl', function ($scope, $state) {

        $scope.goToLogin = function () {
            $state.go('login');
        };

        $scope.goToEvents = function () {
            $state.go('events');
        };

        $scope.goToPlanning = function (event) {
            $state.go('planning', {eventId: event._id});
        };

        $scope.goToDays = function (event) {
            $state.go('days', {eventId: event._id});
        };

        $scope.goToPresentations = function (day) {
            $state.go('presentations.list', {dayId: day._id});
        };

        $scope.goToPresentation = function (presentation) {
            $state.go('presentations.details', {id: presentation._id});
        };

        var selectPrez = function (slots, slot, way) {

            var currentSlot = slots[slot.name];
            var newPrezIndex;
            var newSlotIndex;

            if (way === '+') {
                newPrezIndex = slot.index + 1;
                newSlotIndex = currentSlot.index + 1;
            } else if (way === '-') {
                newPrezIndex = slot.index - 1;
                newSlotIndex = currentSlot.index - 1;
            } else return null;

            if (newPrezIndex < currentSlot.presentations.length && newPrezIndex > 0) {
                // current slot
                return currentSlot.presentations[newPrezIndex];
            } else {
                currentSlot = _.find(slots, function (_slot) {
                    return _slot.index === newSlotIndex;
                });
                if (way === '+') {
                    return currentSlot.presentations[0];
                } else if (way === '-') {
                    return _.last(currentSlot.presentations);
                }
            }
        };

        $scope.goToPreviousPrez = function (presentations, slots, presentation) {
            var selectedPrez = selectPrez(slots, presentation.slot, '-');
            if (selectedPrez) {
                $state.go('presentations.details', {id: selectedPrez._id});
            }
        };

        $scope.goToNextPrez = function (presentations, slots, presentation) {
            var selectedPrez = selectPrez(slots, presentation.slot, '+');
            if (selectedPrez) {
                $state.go('presentations.details', {id: selectedPrez._id});
            }
        };
    });