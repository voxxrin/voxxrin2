angular.module('voxxrin')
    .config(function ($authProvider, $injector, configuration) {

        $authProvider.configure({
            apiUrl: configuration.backendUrl + '/api',
            storage: 'localStorage',
            emailRegistrationPath: '/users',
            tokenValidationPath: '/auth/validate',
            signOutUrl: '/auth/logout',
            authProviderPaths: {
                twitter: '/auth/redirect/twitter',
                linkedin: '/auth/redirect/linkedin'
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
    .run(function ($rootScope, $auth, $state, Session) {

        $rootScope.$on('auth:validation-success', function (event, user) {
            Session.setCurrent(user);
            $state.go('events.list');
        });

        $rootScope.$on('auth:login-success', function (event, user) {
            Session.setCurrent(user);
            $state.go('events.list');
        });

        $rootScope.$on('auth:logout-success', function () {
            Session.destroy();
            $state.go('login');
        });
    });
