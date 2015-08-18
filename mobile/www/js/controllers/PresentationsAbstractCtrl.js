'use strict';

angular.module('voxxrin')
    .controller('PresentationsAbstractCtrl', function ($stateParams, $scope, $ionicPopup, Day, Presentation, Reminder) {

        var slotFormat = 'HH[h]mm';
        var emailRegexp = /.+@.+[.].+/;

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

        angular.extend($scope, {
            day: Day.get({id: $stateParams.dayId}),
            reminder: {},
            openReminder: function (presentation) {
                $ionicPopup.show({
                    template: '<input type="email" ng-model="reminder.email">',
                    title: 'We will notify you about the release of the video concerning this presentation',
                    subTitle: 'Enter your email',
                    scope: $scope,
                    buttons: [
                        { text: 'Cancel' },
                        {
                            text: '<b>OK</b>',
                            type: 'button-positive',
                            onTap: function (e) {
                                if (!$scope.reminder.email || !$scope.reminder.email.match(emailRegexp)) {
                                    e.preventDefault();
                                } else {
                                    return $scope.reminder.email;
                                }
                            }
                        }
                    ]
                }).then(function (email) {
                    Reminder.save({
                        presentationId: presentation._id,
                        email: email
                    });
                });
            },
            star: function (presentation) {
                // TODO
            }
        });

        Presentation.fromDay($stateParams.dayId).$promise.then(computeModel);

    });