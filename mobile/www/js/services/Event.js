'use strict';

angular.module('voxxrin')
    .service('Event', function (configuration, $resource) {

        var event = $resource(configuration.backendUrl + '/api/events/:id');

        return angular.extend(event, {});
    });