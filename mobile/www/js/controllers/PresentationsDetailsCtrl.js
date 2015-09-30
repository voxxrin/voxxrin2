'use strict';

angular.module('voxxrin')
    .controller('PresentationDetailsCtrl', function ($stateParams, $scope, Presentation, Rating) {

        $scope.$on('presentation:updated', function (event, presentation) {
            Presentation.get({id: presentation._id}, function (_presentation) {
                $scope.presentation = $scope.fullPresentation = $scope.weaveRefs($scope.presentation, _presentation);
            });
        });

        $scope.$watch('presentations', function (presentations) {
            if (presentations) {
                $scope.presentation = presentations[$stateParams.id];
                $scope.fullPresentation = Presentation.get({ id: $stateParams.id });
                loadRates($scope.presentation._id);
            }
        });

        var loadRates = function (presentationId) {
            Rating.query({presentationId: presentationId}).$promise.then(function (ratings) {
                var sortedRatings = $scope.ratings = ratings.sort(function (a, b) {
                    return new Date(a) - new Date(b);
                });
                if (sortedRatings && sortedRatings.length > 0) {
                    $scope.rate = _.last(sortedRatings).rate;
                }
            });
        };

        $scope.sendRate = function (presentation, rate) {
            Rating.send(presentation, rate).$promise.then(function () {
                loadRates(presentation._id);
            });
        };
    });