'use strict';

angular.module('voxxrin')
    .service('Event', function (configuration, $resource) {

        var event = $resource(configuration.backendUrl + '/api/events/:id/:sub');

        return angular.extend(event, {
            days: function (eventId) {
                return event.query({id: eventId, sub: 'days'});
            }
        });
    });