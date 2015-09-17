'use strict';

angular.module('voxxrin')
    .filter('prezFilter', function () {

        return function (presentations, filters) {
            // Show only favorites presentations
            if (filters.favorite === true) {
                return _.filter(presentations, function (prez) {
                    return prez.favorite === true;
                });
            }
            // Show only reminded presentations
            if (filters.reminded === true) {
                return _.filter(presentations, function (prez) {
                    return prez.reminded === true;
                });
            }
            // Otherwise : show all presentations
            return presentations;
        };
    });
