'use strict';

angular.module('voxxrin')
    .service('Day', function (configuration, $resource) {

        var day = $resource(configuration.backendUrl + '/api/days');

        return angular.extend(day, {});
    });