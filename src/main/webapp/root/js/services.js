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
		add : function(itemId) {
			var existing = false;
			for (var i = 0; i < cartItems.length; i++) {
				if (cartItems[i].id == itemId) {
					cartItems[i].count = cartItems[i].count + 1;
					existing = true;
					break;
				}
			}
			if (!existing) {
				var item = {
					id : itemId,
					count : 1
				};
				cartItems.push(item);
			}
			console.log('added ' + itemId);
		},
		remove : function(item) {
			var index = cartItems.indexOf(item);
			if (index > -1) {
				cartItems.splice(index, 1);
			}
		}
	}
} ]);
