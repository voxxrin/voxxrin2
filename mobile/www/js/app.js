angular.module('voxxrin', [
    'ionic',
    'ngResource',
    'AngularConferencePlanning',
    'ionic.rating',
    'ion-sticky',
    'ng-token-auth'
])
    .run(function ($ionicPlatform) {
        $ionicPlatform.ready(function () {
            // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
            // for form inputs)
            if (window.cordova && window.cordova.plugins && window.cordova.plugins.Keyboard) {
                cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
                cordova.plugins.Keyboard.disableScroll(true);

            }
            if (window.StatusBar) {
                // org.apache.cordova.statusbar required
                StatusBar.styleLightContent();
            }
        });
    })

    .config(function ($stateProvider, $urlRouterProvider) {

        // Ionic uses AngularUI Router which uses the concept of states
        // Learn more here: https://github.com/angular-ui/ui-router
        // Set up the various states which the app can be in.
        // Each state's controller can be found in controllers.js
        $stateProvider

            .state('login', {
                url: '/',
                templateUrl: 'templates/login.html'
            })
            ////////////////////////
            // Events (list, planning, nested days)
            ////////////////////////
            .state('events', {
                url: '/events',
                abstract: true,
                template: "<ion-nav-view></ion-nav-view>"
            })
            .state('events.planning', {
                parent: 'events',
                url: '/{eventId}/planning',
                templateUrl: 'templates/planning.html'
            })
            .state('events.list', {
                parent: 'events',
                url: '/list',
                templateUrl: 'templates/events.html'
            })
            .state('events.days', {
                parent: 'events',
                url: '/{eventId}/days',
                templateUrl: 'templates/days.html'
            })
            ////////////////////////
            // Presentations
            ////////////////////////
            .state('presentations', {
                abstract: true,
                url: '/events/{eventId}/days/{dayId}/presentations',
                templateUrl: 'templates/presentations.html'
            })
            .state('presentations.list', {
                parent: 'presentations',
                url: '/list',
                templateUrl: 'templates/presentations.list.html'
            })
            .state('presentations.details', {
                parent: 'presentations',
                url: '/{id}',
                templateUrl: 'templates/presentations.details.html'
            });

        // if none of the above states are matched, use this as the fallback
        $urlRouterProvider.otherwise('/');

    });
