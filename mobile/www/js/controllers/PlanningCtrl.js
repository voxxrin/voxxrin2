'use strict';

angular.module('voxxrin')
    .controller('PlanningCtrl', function ($stateParams, $state, $scope, Event, Presentation) {

        var computePlanningModel = function (presentations) {
            $scope.events = [];
            // TODO
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
            eventsList: [
                {
                    title: 'Keynote',
                    type: 'keynote',
                    speaker: '?',
                    place: 'GA',
                    from: '2015-04-12T08:00:00.000+0200',
                    to: '2015-04-12T09:00:00.000+0200',
                    icon: 'picture1.png'
                },
                {
                    title: 'Petit dej\'',
                    type: 'lunch',
                    speaker: 'Everyone',
                    place: 'RC',
                    from: '2015-04-12T09:00:00.000+0200',
                    to: '2015-04-12T10:00:00.000+0200',
                    icon: 'picture1.png'
                },
                {
                    title: 'Introduction à l\'histoire des sciences',
                    type: 'quickie',
                    speaker: 'Gérard Blanchard',
                    place: 'A',
                    from: '2015-04-12T08:30:00.000+0200',
                    to: '2015-04-12T10:10:00.000+0200',
                    icon: 'picture1.png'
                },
                {
                    title: 'Live coding Restx (Java) / AngularJS',
                    type: 'university',
                    speaker: 'Gérard Blanchard',
                    place: 'B',
                    from: '2015-04-12T09:00:00.000+0200',
                    to: '2015-04-12T11:20:00.000+0200',
                    icon: 'picture1.png'
                },
                {
                    title: 'Histoire Mérovingienne',
                    type: 'hands-on',
                    speaker: 'Hugh Capet',
                    place: 'C',
                    from: '2015-04-12T08:00:00.000+0200',
                    to: '2015-04-12T09:30:00.000+0200',
                    icon: 'picture1.png'
                },
                {
                    title: 'Histoire de france',
                    type: 'university',
                    speaker: 'Hugh Capet',
                    place: 'D',
                    from: '2015-04-12T08:00:00.000+0200',
                    to: '2015-04-12T09:30:00.000+0200',
                    icon: 'picture1.png'
                },
                {
                    title: 'De Zero à Héro avec Spring Boot',
                    type: 'university',
                    speaker: 'Léon Blum',
                    place: 'GA',
                    from: '2015-04-12T10:30:00.000+0200',
                    to: '2015-04-12T12:00:00.000+0200',
                    icon: 'picture1.png'
                },
                {
                    title: 'Monitorer ses logs avec Elastic Search et Kibana',
                    type: 'university',
                    speaker: 'John Malkovich',
                    place: 'GA',
                    from: '2015-04-12T15:00:00.000+0200',
                    to: '2015-04-12T17:00:00.000+0200',
                    icon: 'picture2.png'
                },
                {
                    title: 'Apéro',
                    type: 'university',
                    speaker: 'Everyone',
                    place: 'RC',
                    from: '2015-04-12T18:00:00.000+0200',
                    to: '2015-04-12T19:00:00.000+0200',
                    icon: 'picture2.png'
                },
                {
                    title: 'Apéro + Keynote fermeture',
                    type: 'university',
                    speaker: 'Surprise',
                    place: 'RC',
                    from: '2015-04-13T18:00:00.000+0200',
                    to: '2015-04-13T19:00:00.000+0200',
                    icon: 'picture2.png'
                },
                {
                    title: 'Cloud direct with Docker',
                    type: 'quickie',
                    speaker: 'Miles Davis',
                    place: 'A',
                    from: '2015-04-12T14:00:00.000+0200',
                    to: '2015-04-12T15:00:00.000+0200',
                    icon: 'picture3.png'
                },
                {
                    title: 'Docker on the rocks',
                    type: 'hands-on',
                    speaker: 'John Doe',
                    place: 'B',
                    from: '2015-04-13T11:00:00.000+0200',
                    to: '2015-04-13T12:00:00.000+0200',
                    icon: 'picture3.png'
                },
                {
                    title: 'Deploy faster on GAE',
                    type: 'hands-on',
                    speaker: 'Richard Johns',
                    place: 'A',
                    from: '2015-04-13T08:00:00.000+0200',
                    to: '2015-04-13T11:30:00.000+0200',
                    icon: 'picture3.png'
                },
                {
                    title: 'Magic with Kubernetes',
                    type: 'hands-on',
                    speaker: 'Alexis Robert',
                    place: 'E',
                    from: '2015-04-12T08:45:00.000+0200',
                    to: '2015-04-12T16:30:00.000+0200',
                    icon: 'picture3.png'
                },
                {
                    title: 'Mongo DB on fire',
                    type: 'hands-on',
                    speaker: 'Tug Smith',
                    place: 'F',
                    from: '2015-04-12T08:20:00.000+0200',
                    to: '2015-04-12T14:30:00.000+0200',
                    icon: 'picture3.png'
                },
                {
                    title: 'Marionette for dumies',
                    type: 'university',
                    speaker: 'John Doe',
                    place: 'GA',
                    from: '2015-04-13T08:00:00.000+0200',
                    to: '2015-04-13T12:00:00.000+0200',
                    icon: 'picture3.png'
                }
            ],
            options: {
                oneHourSlotSize: '200px',
                eventClasses: function (event) {
                    return [event.type];
                }
            }
        });
    });