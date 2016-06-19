'use strict';

angular.module('voxxrin')
    .service('Rating', function ($resource, ServerUrl) {

        var rating = $resource(ServerUrl + '/api/ratings/:presentationId', { presentationId: '@presentationId', uuid: '@uuid', rate: '@rate'}, {
            'put': {method: 'PUT'}
        });

        return angular.extend(rating, {
            send: function (presentation, rate) {
                return rating.put({
                    presentationId: presentation._id,
                    rate: rate
                });
            }
        });
    });