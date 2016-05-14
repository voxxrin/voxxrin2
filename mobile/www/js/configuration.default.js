(function (module) {

    var Envs = {
        local: ''
    };
    Envs._default = Envs.local;

    var ServerConfig = {
        backendUrl: function () {
            console.log('failover local backend configuration : ', Envs.local);
            return Envs.local;
        }
    };

    module
        .constant('ServerConfig', ServerConfig)
        .constant('ServerUrl', ServerConfig.backendUrl())
        .constant('ServerEnvs', Envs);

})(angular.module('config', []));
