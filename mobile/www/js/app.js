var appVersion = '';

angular.module('voxxrin', [
        'ionic',
        'ionic.service.core',
        'ionic.service.analytics',
        'ionic.service.push',
        'ngCordova',
        'ngResource',
        'config',
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

            if (window.cordova) {
                cordova.getAppVersion(function (version) {
                    appVersion = version;
                });
            } else {
                appVersion = 'dev';
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

    .run(function ($rootScope, $state, $localstorage) {

        // Manage intro
        $rootScope.$on('$stateChangeStart', function (event, toState) {

            if (toState.name === 'login') {

                var skipIntro = $localstorage.getObject('intro.skip');
                var introHasBeenShown = $rootScope.introHasBeenShown;

                if (skipIntro !== true && introHasBeenShown !== true) {
                    event.preventDefault();
                    return $state.go('intro');
                }
            }
        });

    })

    .run(function ($rootScope, $log, $ionicPush) {

        $rootScope.$on('ionicUser:identified', function (evt, ioUser) {

            $ionicPush.init({
                pluginConfig: {
                    ios: {
                        badge: true,
                        sound: true
                    },
                    android: {
                        icon: 'icon',
                        forceShow: true
                    }
                },
                onNotification: function (notification) {
                    var payload = notification.payload;
                    console.log(notification, payload);
                },
                onRegister: function (data) {
                    if (ioUser) {
                        $ionicPush.addTokenToUser(ioUser);
                        if (ioUser.id) {
                            console.log('saving user token');
                            ioUser.save();
                        }
                    }
                    $log.info('Registered PUSH token', data.token);
                }
            });

            $ionicPush.register();
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
            // Dashboard (setting event)
            ////////////////////////
            .state('dashboard', {
                url: '/dashboard',
                templateUrl: 'templates/dashboard.html'
            })
            ////////////////////////
            // Admin
            ////////////////////////
            .state('admin', {
                url: '/admin',
                abstract: true,
                template: "<ion-nav-view></ion-nav-view>"
            })
            .state('admin.event', {
                url: '/event/{eventId}',
                templateUrl: 'templates/admin.event.html'
            })
            ////////////////////////
            // Events (list, nested days)
            ////////////////////////
            .state('events', {
                url: '/events',
                abstract: true,
                template: "<ion-nav-view></ion-nav-view>"
            })
            .state('events.list', {
                url: '',
                templateUrl: 'templates/events.html'
            })
            .state('events.days', {
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
                url: '/planning',
                templateUrl: 'templates/planning.html'
            })
            .state('presentations.list', {
                url: '',
                templateUrl: 'templates/presentations.list.html'
            })
            .state('presentations.details', {
                url: '/{id}',
                templateUrl: 'templates/presentations.details.html'
            });

        // if none of the above states are matched, use this as the fallback
        $urlRouterProvider.otherwise('/');

    });
