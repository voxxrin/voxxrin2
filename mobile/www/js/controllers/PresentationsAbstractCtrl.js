'use strict';

angular.module('voxxrin')
    .controller('PresentationsAbstractCtrl', function ($rootScope, $stateParams, $scope, $ionicPopup, Day,
                                                       Presentation, RemindMe, Favorite, Calendar) {

        var slotFormat = 'HH[h]mm';

        $scope.$on('presentation:updated', function (event, presentation) {
            updateModel(presentation);
        });

        var buildPresentationsMap = function (presentations) {
            return _.indexBy(presentations, '_id');
        };

        var buildSlots = function (presentations) {

            var slotIndex = 0;
            var slots = {};
            _.each(presentations, function (prez) {
                var from = moment(prez.from);
                var to = moment(prez.to);
                var slotName = from.format(slotFormat) + '-' + to.format(slotFormat);
                if (!slots[slotName]) {
                    slots[slotName] = {
                        index: slotIndex++,
                        presentations: []
                    };
                }
                prez.slot = {
                    name: slotName,
                    index: (slots[slotName].presentations.length)
                };
                slots[slotName].presentations.push(prez);
            });

            return slots;
        };

        /**
         * Compute model (slots, ids, ...)
         */
        var computeModel = function (presentations) {
            $scope.presentations = buildPresentationsMap(presentations);
            $scope.slots = buildSlots(presentations);
        };

        var updateModel = function (presentation) {
            Presentation.get({id: presentation._id}, function (_updatedPresentation) {
                _updatedPresentation = $scope.weaveRefs(presentation, _updatedPresentation);
                $scope.presentations[presentation._id] = _updatedPresentation;
                var index = _.findIndex($scope.slots[presentation.slot.name].presentations, function (_prez) {
                    return _prez._id === presentation._id;
                });
                $scope.slots[presentation.slot.name].presentations[index] = _updatedPresentation;
            });
        };

        angular.extend($scope, {
            day: Day.get({id: $stateParams.dayId}),
            reminder: {},
            weaveRefs: function (oldPrez, newPrez) {
                newPrez.slot = oldPrez.slot;
                return newPrez;
            },
            remindMe: function (presentation) {
                RemindMe.save({presentationId: presentation._id})
                    .$promise
                    .then(function () {
                        $rootScope.$broadcast('presentation:updated', presentation);
                    })
                    .catch(function () {
                        $ionicPopup.alert({
                            title: 'Prevenez-moi !',
                            template: 'Vous devez être connecté pour beneficier de cette fonctionnalité'
                        });
                    });
            },
            favorite: function (presentation) {
                Favorite.save({presentationId: presentation._id, deviceToken: $rootScope.pushToken})
                    .$promise
                    .then(function () {
                        $rootScope.$broadcast('presentation:updated', presentation);
                    })
                    .catch(function () {
                        $ionicPopup.alert({
                            title: 'Favoriser !',
                            template: 'Vous devez être connecté pour beneficier de cette fonctionnalité'
                        });
                    });
            },
            addToCalendar: function (presentation) {
                Calendar.createEvent(presentation);
            }
        });

        Presentation.fromDay($stateParams.dayId).$promise.then(computeModel);

    });