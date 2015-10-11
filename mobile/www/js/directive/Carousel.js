'use strict';

angular.module('voxxrin')
    .directive('carousel', function () {

        return {
            restrict: 'E',
            scope: {
                elements: '='
            },
            link: function (scope, elt) {

                var setupCarousel = function () {
                    $(elt).slick({
                        arrows: true,
                        dots: true,
                        infinite: true,
                        slidesToShow: 1,
                        slidesToScroll: 1,
                        responsive: [
                            {
                                breakpoint: 1024,
                                settings: {
                                    slidesToShow: 1,
                                    slidesToScroll: 1,
                                    infinite: true,
                                    dots: true
                                }
                            },
                            {
                                breakpoint: 600,
                                settings: {
                                    slidesToShow: 1,
                                    slidesToScroll: 1
                                }
                            },
                            {
                                breakpoint: 480,
                                settings: {
                                    slidesToShow: 1,
                                    slidesToScroll: 1
                                }
                            }
                        ]
                    });
                };

                scope.$watchCollection('elements', function (_elements) {
                    if (_elements && _elements.length > 0) {
                        setupCarousel();
                    }
                });
            }
        };
    });