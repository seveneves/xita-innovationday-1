'use strict';

/* Services */

var phonecatServices = angular.module('phonecatServices', [ 'ngResource' ]);

phonecatServices.factory('Phone', [ '$resource', function($resource) {
	return $resource('phones/:phoneId.json', {}, {
		query : {
			method : 'GET',
			params : {
				phoneId : 'phones'
			},
			isArray : true
		}
	});
} ]).factory('Cart', ['$http', '$q', function ($http, $q) {
	var cartItems = [];
//	var sendSuccess = function (resp) {
//          user = resp.data.user;
//          return user;
//      };
	
    var sendFailure = function (err) {
          console.log('Rejecting');
          return $q.reject(err.data);
      };
	var updateCart = function(phone) {
		var existing = false;
		for (var i = 0; i < cartItems.length; i++) {
			if (cartItems[i].id == phone.id) {
				cartItems[i].count = cartItems[i].count + 1;
				existing = true;
				break;
			}
		}
		if (!existing) {
			phone.count = 1;
			cartItems.push(phone);
		}
		console.log('added this: ' + phone);
	}
	var removeFromCart = function(phone) {
		var index = cartItems.indexOf(phone);
		if (index > -1) {
			cartItems.splice(index, 1);
		}
	}
	
	return {
		get : function() {
			return cartItems;
		},
		add : function(phone) {
			 return $http.post('/cart', {productId: phone.id})
			 				.then((function(resp){updateCart(phone)}), sendFailure);
		},
		remove : function(phone) {
			 return $http.post('/cart', {productId: phone.id})
				.then((function(resp){removeFromCart(phone)}), sendFailure);
		}
	}
} ]);
