'use strict';

angular.module('voxxrin')
    .controller('PresentationDetailsCtrl', function ($stateParams, $scope, Presentation) {

        $scope.presentation = Presentation.get({ id: $stateParams.id });

    });