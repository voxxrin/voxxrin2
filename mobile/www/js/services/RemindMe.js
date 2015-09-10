'use strict';

angular.module('voxxrin')
    .service('RemindMe', function ($resource, configuration) {

        var reminder = $resource(configuration.backendUrl + '/api/remindme', {presentationId: '@presentationId'});
        return angular.extend(reminder, {});
    });