'use strict';

angular.module('voxxrin')
    .service('Presentation', function (configuration, $resource) {

        var withPrototype = function (resource) {

            angular.extend(resource.prototype, {
                isFavorite: function () {
                    return this.favorite === true;
                },
                isReminded: function () {
                    return this.reminded === true;
                }
            });

            return resource;
        };

        var presentation = withPrototype($resource(configuration.backendUrl() + '/api/presentations/:id'));
        var dayPresentations = withPrototype($resource(configuration.backendUrl() + '/api/days/:dayId/presentations'));
        var eventPresentations = withPrototype($resource(configuration.backendUrl() + '/api/events/:eventId/presentations'));

        return angular.extend(presentation, {
            fromDay: function (dayId) {
                return dayPresentations.query({dayId: dayId});
            },
            fromEvent: function (eventId) {
                return eventPresentations.query({eventId: eventId});
            }
        });
    });