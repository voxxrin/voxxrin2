'use strict';

angular.module('voxxrin')
    .controller('PresentationsAbstractCtrl', function ($rootScope, $stateParams, $scope, Day, Presentation,
                                                       RemindMe, Favorite, Calendar, Notification) {

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

            var kindClassIndex = 1;
            var kindClasses = {};

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
                var kindClass = kindClasses[prez.kind];
                if (!kindClass) {
                    kindClass = 'cat-' + (kindClassIndex++);
                    kindClasses[prez.kind] = kindClass;
                }
                prez.kindClass = kindClass;
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
                var index = _.findIndex($scope.slots[presentation.slot.name].presentations, function (_prez) {
                    return _prez._id === presentation._id;
                });
                $scope.applySrvData($scope.presentations[presentation._id], _updatedPresentation);
                $scope.applySrvData($scope.slots[presentation.slot.name].presentations[index], _updatedPresentation);
            });
        };

        angular.extend($scope, {
            day: Day.get({id: $stateParams.dayId}),
            reminder: {},
            applySrvData: function (localPrez, srvPrez) {
                localPrez.remindMeCount = srvPrez.remindMeCount;
                localPrez.favoriteCount = srvPrez.favoriteCount;
                localPrez.reminded = srvPrez.reminded;
                localPrez.favorite = srvPrez.favorite;
                return localPrez;
            },
            remindMe: function (presentation) {
                if (presentation.isReminded()) {
                    RemindMe.delete({presentationId: presentation._id})
                        .$promise
                        .then(function () {
                            $rootScope.$broadcast('presentation:updated', presentation);
                        });
                } else {
                    RemindMe.save({presentationId: presentation._id})
                        .$promise
                        .then(function () {
                            $rootScope.$broadcast('presentation:updated', presentation);
                        })
                        .catch(function () {
                            Notification.popup.warning('Prevenez-moi !', 'Vous devez être connecté pour beneficier de cette fonctionnalité');
                        });
                }
            },
            favorite: function (presentation) {
                if (presentation.isFavorite()) {
                    Favorite.delete({presentationId: presentation._id})
                        .$promise
                        .then(function () {
                            $rootScope.$broadcast('presentation:updated', presentation);
                        });
                } else {
                    Favorite.save({presentationId: presentation._id, deviceToken: $rootScope.pushToken})
                        .$promise
                        .then(function () {
                            $rootScope.$broadcast('presentation:updated', presentation);
                        })
                        .catch(function () {
                            Notification.popup.warning('Favoriser !', 'Vous devez être connecté pour beneficier de cette fonctionnalité');
                        });
                }
            },
            addToCalendar: function (presentation, event) {
                Calendar.createEntry(presentation, event);
            }
        });

        Presentation.fromDay($stateParams.dayId).$promise.then(computeModel);

    });