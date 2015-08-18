'use strict';

angular.module('voxxrin')
    .service('Reminder', function ($resource, configuration) {

        var reminder = $resource(configuration.backendUrl + '/api/remindme', { presentationId: '@presentationId', email: '@email'});
        return angular.extend(reminder, {});
    });