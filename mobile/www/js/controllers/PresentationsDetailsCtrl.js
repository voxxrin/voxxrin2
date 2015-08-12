'use strict';

angular.module('voxxrin')
    .controller('PresentationDetailsCtrl', function ($stateParams, $scope, Presentation, Rating) {

        $scope.$watch('presentations', function (presentations) {
            if (presentations) {
                $scope.presentation = presentations[$stateParams.id];
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
            if ($scope.platform.uuid) {
                Rating.send(presentation, rate, $scope.platform.uuid).$promise.then(function () {
                    loadRates(presentation._id);
                });
            } else {
                console.log('Device uuid is not defined !');
            }
        };
    });