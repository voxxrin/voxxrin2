'use strict';

angular.module('voxxrin')
    .service('Calendar', function ($cordovaCalendar) {

        var _createEntry = function (presentation, event) {

            $cordovaCalendar.createEventInteractively({
                title: '[ ' + event.name + ' ] ' + presentation.title,
                location: presentation.location ? presentation.location.fullName : '-',
                startDate: moment(presentation.from).toDate(),
                endDate: moment(presentation.to).toDate()
            }).then(function () {
                console.log('L\'evenement a été ajouté à votre calendrier !');
            }, function (err) {
                console.log('Error during event creation.', err);
            });
        };

        return {
            createEntry: function (presentation, event) {
                if (window.cordova) {
                    _createEntry(presentation, event);
                } else {
                    console.log('No calendar is supported on this device');
                }
            }
        };
    });