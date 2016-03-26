'use strict';

angular.module('voxxrin')
    .service('Stats', function ($resource, configuration) {

        var resource = $resource(configuration.backendUrl() + '/api/stats/:type/:id');

        return angular.extend(resource, {
            forEvent: function (id) {
                return resource.get({type: 'event', id: id});
            }
        });
    });