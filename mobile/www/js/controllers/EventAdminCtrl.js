'use strict';

angular.module('voxxrin')
    .controller('EventAdminCtrl', function ($rootScope, $scope, $ionicPopup, $stateParams, Event, Stats,
                                            Presentation, AttachedContent, Notification) {

        Event.get({id: $stateParams.eventId}).$promise.then(function (event) {
            $scope.currentEvent = event;
            $scope.stats = Stats.forEvent(event._id);
            $scope.presentations = Presentation.fromEvent(event._id);
        });

        angular.extend($scope, {
            attachContent: function (prez) {
                var popupScope = angular.extend($rootScope.$new(), {
                    attached: {}
                });
                $ionicPopup.show({
                    templateUrl: 'templates/popups/attach.content.html',
                    title: 'Attacher un contenu',
                    scope: popupScope,
                    buttons: [
                        {
                            text: 'Annuler'
                        },
                        {
                            text: '<b>OK</b>',
                            type: 'button-positive',
                            onTap: function (e) {
                                if (popupScope.attached.url && popupScope.attached.description) {
                                    AttachedContent.attach(prez._id, {
                                        url: popupScope.attached.url,
                                        description: popupScope.attached.description
                                    }).$promise.then(function () {
                                        Notification.popup.success('Succès', 'Le contenu a été attaché avec succès,' +
                                            ' les utilisateurs inscrits aux notifications sur la présentation ont été notifiés.');
                                    });
                                } else {
                                    e.preventDefault();
                                    Notification.popup.error('Erreur', 'Merci de renseigner tous les champs');
                                }
                            }
                        }
                    ]
                });

            }
        });
    });