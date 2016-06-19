'use strict';

angular.module('voxxrin')
    .service('RemindMe', function ($resource, ServerUrl) {

        var reminder = $resource(ServerUrl + '/api/remindme', {presentationId: '@presentationId'});
        return angular.extend(reminder, {});
    });