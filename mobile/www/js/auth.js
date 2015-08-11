angular.module('voxxrin')
    .config(function ($authProvider, $injector, configuration) {
        var storage = {
            getStorage: function () {
                var UserService = $injector.get('UserService');
                var $window = $injector.get('$window');
                return $window.localStorage;
            },
            persistData: function (key, val) {
                if (key === 'auth_headers') {
                    if (_.isEmpty(val)) {
                        window.localStorage.setItem(key, JSON.stringify(val));
                        window.sessionStorage.setItem(key, JSON.stringify(val));
                    } else {
                        if (JSON.parse(window.atob(val['access-token'].split('.')[1])).rememberMe) {
                            window.localStorage.setItem(key, JSON.stringify(val));
                        } else {
                            window.sessionStorage.setItem(key, JSON.stringify(val));
                        }
                    }
                } else {
                    window.localStorage.setItem(key, JSON.stringify(val));
                }
            },
            retrieveData: function (key) {
                var item = window.localStorage.getItem(key);
                if (_.isEmpty(item) || item === '{}') {
                    item = window.sessionStorage.getItem(key);
                }
                if (angular.isUndefined(item)) {
                    return undefined;
                }
                return JSON.parse(item);
            },
            deleteData: function (key) {
                window.localStorage.removeItem(key);
                window.sessionStorage.removeItem(key);
            }
        };

        $authProvider.configure({
            apiUrl: configuration.backendUrl + '/api',
            storage: storage,
            emailRegistrationPath: '/users',
            tokenValidationPath: '/auth/validate',
            signOutUrl: '/auth/logout',
            authProviderPaths: {
                twitter: '/auth/redirect/twitter'
            },
            omniauthWindowType: 'inAppBrowser',
            handleLoginResponse: function (response) {
                return response;
            },
            handleAccountResponse: function (response) {
                return response;
            },
            handleTokenValidationResponse: function (response) {
                return response;
            }
        });
    })
    .run(function ($rootScope, $auth, $state, $location) {
        $rootScope.$on('auth:validation-success', function (event, user) {
            //UserService.setCurrentUser(user);
            //if (!UserService.isProfileComplete(user)) {
            //    alertify.warning('Merci de  définir votre profil.');
            //    $state.go('app.register.company.simple');
            //} else if ($state.is('app.home') || $location.path() === '' || $location.path() == '/app/home') {
            //    $state.go('app.dashboard');
            //}
            console.log(user);
        });
        $rootScope.$on('auth:email-confirmation-error', function (event, user) {
            //UserService.setCurrentUser(user);
            //$state.go('app.common.confirmationmailneed');
        });

        $rootScope.$on('auth:login-success', function (event, user) {
            //if (!user.socialLogin || !angular.isDefined(user.profile._id)) {
            //    UserService.setCurrentUser(user);
            //}
            console.log(user);
        });

        $rootScope.$on('auth:logout-success', function () {
            //$state.go('app.home');
        })
    });
