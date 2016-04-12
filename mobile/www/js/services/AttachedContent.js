'use strict';

angular.module('voxxrin')
    .service('AttachedContent', function ($resource, ServerUrl) {

        var resource = $resource(ServerUrl + '/api/presentation/:presentationId/attachedContent');

        return angular.extend(resource, {

            attach: function (presentationId, content) {
                return resource.save({presentationId: presentationId}, content);
            }

        });
    });
