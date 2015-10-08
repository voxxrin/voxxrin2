'use strict';

angular.module('voxxrin')
    .service('Calendar', function ($cordovaCalendar) {

        return {
            createEntry: function (presentation, event) {

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

            }
        };
    });