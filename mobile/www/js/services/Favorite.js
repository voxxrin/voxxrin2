'use strict';

angular.module('voxxrin')
    .service('Favorite', function ($resource, configuration) {

        var favorite = $resource(configuration.backendUrl + '/api/favorite', {presentationId: '@presentationId'});
        return angular.extend(favorite, {});
    });