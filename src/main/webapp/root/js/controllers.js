'use strict';

/* Controllers */

var phonecatControllers = angular.module('phonecatControllers', []);

phonecatControllers.controller('PhoneListCtrl', ['$scope', 'Phone', 'Cart', function($scope, Phone, Cart) {
	var self = this;
	$scope.phones = Phone.query();
	$scope.orderProp = 'age';
	$scope.addToCart = function(phone) {
		Cart.add(phone);
	}
	$scope.cartItems =  Cart.get();
	$scope.removeFromCart = function(phone) {
		Cart.remove(phone);
	}
  }]);

phonecatControllers.controller('PhoneDetailCtrl', ['$scope', '$routeParams', 'Phone',
  function($scope, $routeParams, Phone) {
    $scope.phone = Phone.get({phoneId: $routeParams.phoneId}, function(phone) {
      $scope.mainImageUrl = phone.images[0];
    });

    $scope.setImage = function(imageUrl) {
      $scope.mainImageUrl = imageUrl;
    }
  }]);

phonecatControllers.controller('CartCtrl', ['$scope', 'Phone',
                                                 function($scope, Phone) {
                                                   $scope.phones = Phone.query();
                                                   $scope.orderProp = 'age';
                                                 }]);
