'use strict';

angular.module('voxxrin')
    .controller('PlanningCtrl', function ($stateParams, $state, $scope, Event, Presentation) {

        var computePlanningModel = function (presentations) {
            $scope.events = _.map(presentations, function (prez) {
                return {
                    title: prez.title,
                    type: prez.kind,
                    speaker: prez.speaker,
                    place: prez.location.name,
                    from: prez.from,
                    to: prez.to
                };
            });
        };

        $scope.event = Event.get({id: $stateParams.eventId});

        $scope.presentations = Presentation
            .fromEvent($stateParams.eventId)
            .$promise
            .then(computePlanningModel);

        angular.extend($scope, {
            placeMapping: {
                'A': 'Amphi. A',
                'B': 'Amphi. B',
                'C': 'Amphi. C',
                'D': 'Amphi. D',
                'E': 'Amphi. E',
                'F': 'Amphi. F',
                'RC': 'Rez. de C.',
                'GA': 'Grand Amphi.'
            },
            options: {
                oneHourSlotSize: '200px',
                eventClasses: function (event) {
                    if (event.type) {
                        return [event.type.toLowerCase()];
                    }
                    return '';
                }
            }
        });
    });