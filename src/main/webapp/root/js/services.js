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
} ]).factory('Cart', [ '$resource', function($resource) {
	var cartItems = [];
	return {
		get : function() {
			return cartItems;
		},
		add : function(phone) {
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
		},
		remove : function(phone) {
			var index = cartItems.indexOf(phone);
			if (index > -1) {
				cartItems.splice(index, 1);
			}
		}
	}
} ]);
