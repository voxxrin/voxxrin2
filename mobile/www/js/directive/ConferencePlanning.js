'use strict';

angular.module('voxxrin')
    .directive('conferencePlanning', function ($stateParams, $filter) {

        var hourFormat = 'HH:mm';

        var transformDate = function (momentDate) {
            return {
                year: momentDate.year(),
                month: momentDate.month(),
                day: momentDate.date(),
                hour: momentDate.hour(),
                minute: momentDate.minute(),
                second: momentDate.second()
            };
        };

        var buildEventText = function (_event) {

            var detailButton = '<a href="#/events/' + $stateParams.eventId
                + '/days/' + $stateParams.dayId
                + '/presentations/' + _event._id + '">Détails presentation</a>';

            return detailButton
                + '<br/>'
                + '<br/>'
                + '<b>' + _event.location.fullName + '</b>'
                + '<br/>'
                + $filter('speakers')(_event.speakers)
                + '<br/>'
                + '<br/>'
                + _event.summary
        };

        var transformEvent = function (_event) {

            var speaker = _event.speakers[0];
            var from = moment(_event.from);
            var to = moment(_event.to);

            return {
                start_date: transformDate(from),
                end_date: transformDate(to),
                text: {
                    headline: _event.title,
                    text: buildEventText(_event)
                },
                display_date: (from.format(hourFormat) + ' - ' + to.format(hourFormat)),
                media: {
                    url: speaker ? speaker.avatarUrl : ''
                },
                group: _event.location ? _event.location.fullName : '-'
            }
        };

        return {
            scope: {
                events: '=',
                options: '='
            },
            template: '<div id="timeline-embed"></div>',
            controller: function ($scope) {

                $scope.options = angular.extend($scope.options || {}, {
                    timenav_height_percentage: 45,
                    timenav_mobile_height_percentage: 45
                });

                $scope.$watchCollection('events', function (_events) {

                    if (_events && !_.isEmpty(_events)) {

                        var _model = {
                            events: _.map(_events, transformEvent)
                        };

                        new TL.Timeline('timeline-embed', _model, $scope.options);
                    }
                });
            }
        };
    });