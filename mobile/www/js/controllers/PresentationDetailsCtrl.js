'use strict';

angular.module('voxxrin')
    .controller('PresentationDetailsCtrl', function ($stateParams, $scope, Presentation, Rating) {

        $scope.presentation = Presentation.get({id: $stateParams.id});

        var loadRates = function (presentationId) {
            $scope.rates = Rating.query({presentationId: presentationId});
        };

        $scope.sendRate = function (presentation, rate) {
            Rating.send(presentation, rate, $scope.platform.uuid).$promise.then(function () {
                loadRates(presentation._id);
            });
        };

        loadRates($stateParams.id);
    });