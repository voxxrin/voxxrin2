'use strict';

angular.module('voxxrin')
    .service('Presentation', function ($resource, ServerUrl) {

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

        var presentation = withPrototype($resource(ServerUrl + '/api/presentations/:id'));
        var dayPresentations = withPrototype($resource(ServerUrl + '/api/days/:dayId/presentations'));
        var eventPresentations = withPrototype($resource(ServerUrl + '/api/events/:eventId/presentations'));

        return angular.extend(presentation, {
            fromDay: function (dayId) {
                return dayPresentations.query({dayId: dayId});
            },
            fromEvent: function (eventId) {
                return eventPresentations.query({eventId: eventId});
            }
        });
    });