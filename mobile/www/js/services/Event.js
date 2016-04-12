'use strict';

angular.module('voxxrin')
    .service('Event', function ($resource, ServerUrl) {

        var event = $resource(ServerUrl + '/api/events/:id');

        return angular.extend(event, {});
    });