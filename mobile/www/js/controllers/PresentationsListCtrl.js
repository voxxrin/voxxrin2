'use strict';

angular.module('voxxrin')
    .controller('PresentationsListCtrl', function ($stateParams, $scope, Day, Presentation) {

        var slotFormat = 'HH[h]mm';

        var computeSlots = function (presentations) {
            var slots = $scope.slots = {};
            _.each(presentations, function (prez) {
                var from = moment(prez.from);
                var to = moment(prez.to);
                var slotName = from.format(slotFormat) + '-' + to.format(slotFormat);
                if (!slots[slotName]) {
                    slots[slotName] = [];
                }
                slots[slotName].push(prez);
            });
        };

        $scope.day = Day.get({id: $stateParams.dayId});
        Presentation.fromDay($stateParams.dayId).$promise.then(computeSlots);

    });