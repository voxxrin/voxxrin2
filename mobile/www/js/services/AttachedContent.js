'use strict';

angular.module('voxxrin')
    .service('AttachedContent', function (configuration, $resource) {

        var resource = $resource(configuration.backendUrl() + '/api/presentation/:presentationId/attachedContent');

        return angular.extend(resource, {

            attach: function (presentationId, content) {
                return resource.save({presentationId: presentationId}, content);
            }

        });
    });
