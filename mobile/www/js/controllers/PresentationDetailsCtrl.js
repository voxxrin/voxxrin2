'use strict';

angular.module('voxxrin')
    .controller('PresentationDetailsCtrl', function ($stateParams, $state, $scope, Presentation) {

        $scope.presentation = Presentation.get({ id: $stateParams.id });

    });