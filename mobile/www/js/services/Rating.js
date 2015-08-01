'use strict';

angular.module('voxxrin')
    .service('Rating', function (configuration, $resource) {

        var rating = $resource(configuration.backendUrl + '/api/ratings/:presentationId', { uuid: '@uuid', rate: '@rate'}, {
            'put': {method: 'PUT'}
        });

        return angular.extend(rating, {
            send: function (presentation, rate, uuid) {
                if (!uuid) {
                    return null;
                }
                return rating.put({
                    presentationId: presentation._id,
                    rate: rate,
                    uuid: uuid
                });
            }
        });
    });