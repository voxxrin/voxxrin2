'use strict';

angular.module('voxxrin')
    .service('Favorite', function ($resource, ServerUrl) {

        var favorite = $resource(ServerUrl + '/api/favorite', {presentationId: '@presentationId', deviceToken: '@deviceToken'});
        return angular.extend(favorite, {});
    });