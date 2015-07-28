'use strict';

angular.module('voxxrin')
  .directive('conferencePlanning', function () {

    var throwInvalidEventDef = function (_event, cause) {
      throw new Error('invalid event definition (' + cause + ') => ' + JSON.stringify(_event));
    };

    var throwInvalidOptions = function (cause) {
      throw new Error('invalid provided options (' + cause + ')');
    };

    var prepareOptions = function (_options) {

      if (!_options) _options = {};
      if (!_options.eventClasses) _options.eventClasses = function () {
        return [];
      };
      if (!_options.slots) _options.slots = {};
      if (!_options.slots.from) _options.slots.from = 8;
      if (!_options.slots.to) _options.slots.to = 19;

      if (!_options.oneHourSlotSize) _options.oneHourSlotSize = '200px';
      if (_.endsWith(_options.oneHourSlotSize, 'px')) {
        _options.oneHourSlotSize = _options.oneHourSlotSize.replace('px', '');
      } else {
        throwInvalidOptions('bad provided oneHourSlotSize option, px size supported only');
      }

      return _options;
    };

    var computeWidth = function (_event, options) {

      var eventStart = moment(_event.from);
      var eventEnd = moment(_event.to);

      var elapsedSeconds = eventEnd.diff(eventStart, 'seconds');

      // By default a slot of 3600 seconds is represented by 200px (may be configured)
      // 200 px => 3600 seconds
      // ?   px => { elapsedSeconds } seconds
      return (options.oneHourSlotSize * elapsedSeconds) / 3600;
    };

    var computeMargin = function (_event, options) {

      var eventStart = moment(_event.from);
      var beginningOfTheDay = moment(_event.from).hours(options.slots.from).minutes(0).seconds(0).milliseconds(0);

      var elapsedSeconds = eventStart.diff(beginningOfTheDay, 'seconds');

      // By default a slot of 3600 seconds is represented by 200px (may be configured)
      // 200 px => 3600 seconds
      // ?   px => { elapsedSeconds } seconds
      return ((options.oneHourSlotSize * elapsedSeconds) / 3600) + 60;
    };

    var transform = function (_events) {

      // Transform provided events into this model structure
      // some dates => some places => some events

      var model = {
        lanes: {},
        dates: {}
      };

      _.each(_events, function (_event) {

        if (!_event.from || !_event.to || !_event.place) {
          throwInvalidEventDef(_event, 'some attributes are missing');
        }

        var from = moment(_event.from);
        var to = moment(_event.to);
        var fromStr = from.format('YYYY-MM-DD');
        var toStr = to.format('YYYY-MM-DD');

        if (from.isAfter(to)) {
          throwInvalidEventDef(_event, 'event.from > event.to');
        }

        // Event is not on the same day
        if (fromStr != toStr) {
          // TODO
        } else {
          if (!model.lanes[fromStr]) model.lanes[fromStr] = {};
          if (!model.lanes[fromStr][_event.place]) model.lanes[fromStr][_event.place] = [];
          model.lanes[fromStr][_event.place].push(_event);
        }

        model.dates = _.keys(model.lanes);

      });

      return model;
    };

    var buildAxes = function (scope) {
      scope.axes = {
        X: []
      };

      var from = scope.options.slots.from;
      var to = scope.options.slots.to;
      for (var i = from; i <= to;) {
        scope.axes.X.push(i++);
      }
    };

    return {
      scope: {
        events: '=',
        options: '='
      },
      restrict: 'EA',
      templateUrl: 'views/ConferencePlanning.html',
      transclude: true,
      controller: function ($scope) {

        $scope.loadDate = function (date) {
          if (date) {
            $scope.lanes = $scope.model.lanes[date];
          }
        };

        $scope.computeEventStyle = function (event) {
          var margin = computeMargin(event, $scope.options);
          var width = computeWidth(event, $scope.options);
          return {'margin-left': margin + 'px', 'width': width + 'px'};
        };

        $scope.$watchCollection('events', function (_events) {

          if (!_events || _.isEmpty(_events)) {
            return;
          }

          var model = $scope.model = transform($scope.events);
          $scope.loadDate(model.dates[0]);

        });
      },
      link: function (scope) {

        scope.options = prepareOptions(scope.options);

        buildAxes(scope);
      }
    };

  });
