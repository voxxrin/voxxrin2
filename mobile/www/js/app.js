angular.module('voxxrin', [
    'ionic',
    'ionic.service.core',
    'ionic.service.analytics',
    'ionic.service.push',
    'ngCordova',
    'ngResource',
    'ionic.rating',
    'ion-sticky',
    'ng-token-auth',
    'ngIOS9UIWebViewPatch'
])
    .run(function ($rootScope, $ionicPlatform, $ionicLoading, $ionicAnalytics) {

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

            $ionicAnalytics.register();
        });

        $rootScope.$on('loading:show', function () {
            $ionicLoading.show();
        });

        $rootScope.$on('loading:hide', function () {
            $ionicLoading.hide();
        });
    })

    .run(function ($rootScope, $ionicPush, $log) {

        $rootScope.$on('$cordovaPush:tokenReceived', function (event, data) {
            $log.info('Ionic Push: Got token ', data.token, data.platform);
            $rootScope.pushToken = data.token;
        });

        $rootScope.$on('ionicUser:identified', function () {
            $ionicPush.register({
                canShowAlert: true,
                canSetBadge: true,
                canPlaySound: true,
                canRunActionsOnWake: true,
                onNotification: function (notification) {
                    console.log(notification);
                    return true;
                }
            });
        });
    })

    .config(function ($httpProvider) {
        $httpProvider.interceptors.push('MainHttpRequestInterceptor');
    })

    .config(function ($stateProvider, $urlRouterProvider) {

        // Ionic uses AngularUI Router which uses the concept of states
        // Learn more here: https://github.com/angular-ui/ui-router
        // Set up the various states which the app can be in.
        // Each state's controller can be found in controllers.js
        $stateProvider
            .state('intro', {
                url: '/intro',
                templateUrl: 'templates/intro.html'
            })
            .state('login', {
                url: '/',
                templateUrl: 'templates/login.html'
            })
            .state('settings', {
                url: '/settings',
                templateUrl: 'templates/settings.html'
            })
            ////////////////////////
            // Events (list, planning, nested days)
            ////////////////////////
            .state('events', {
                url: '/events',
                abstract: true,
                template: "<ion-nav-view></ion-nav-view>"
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
                cache: false,
                url: '/events/{eventId}/days/{dayId}/presentations',
                templateUrl: 'templates/presentations.html'
            })
            .state('presentations.planning', {
                parent: 'presentations',
                url: '/planning',
                templateUrl: 'templates/planning.html'
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
