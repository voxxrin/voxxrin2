'use strict';

angular.module('voxxrin')
    .service('Stats', function ($resource, ServerUrl) {

        var resource = $resource(ServerUrl + '/api/stats/:type/:id');

        return angular.extend(resource, {
            forEvent: function (id) {
                return resource.get({type: 'event', id: id});
            }
        });
    });