/**
 * The angularBeans object in the angularBeans javascript api
 * Code is taken unchanged from StaticJsCache by Bassem Hmidi.
 * 
 * @author Michael Kulla <info@michael-kulla.com>
 * @author Bassem Hmidi
 */
function AngularEvent(data, dataClass) {

	if (dataClass != 'String' && dataClass) {
		this.dataClass = dataClass;
		this.data = JSON.stringify(data);
	} else {
		this.data = data
	}
}
;

var angularBeans = {
	bind: function (scope, service, modelsName) {

		scope[service.serviceID] = service;

		for (i in modelsName) {

			modelsName[i] = service.serviceID + '.' + modelsName[i];
		}

		scope.$watch(angular.toJson(modelsName).split('\"').join(''), function (newValue, oldValue) {

			for (i in modelsName) {
				scope[modelsName[i].split(service.serviceID + '.')[1]] = newValue[i];
			}

		}, true);

	}

	, addMethod: function (object, name, fn) {

		if (object['$ab_fn_cache'] == null) {
			object['$ab_fn_cache'] = [];
		}

		if ((object['$ab_fn_cache'][name]) == undefined) {
			object['$ab_fn_cache'][name] = [];
		}

		var index = object['$ab_fn_cache'][name].length;

		object['$ab_fn_cache'][name][index] = fn;

		object[name] = function () {

			for (index in object['$ab_fn_cache'][name]) {

				var actf = object['$ab_fn_cache'][name][index];

				if (arguments.length == actf.length) {

					return actf.apply(object, arguments);

				}
			}
		};

	}

	, isIn: function (array, elem) {
		var found = false;
		for (item in array) {
			if (this.isSame(array[item], elem)) {
				found = true;
				return item;
			}
		}
		return -1;
	}

	, isSame: function (item1, item2) {

		var same = true;


		for (prop in item1) {

			if (prop == '$$hashKey') {
				continue;
			}

			if (item1[prop] instanceof String) {
				if (item1[prop].startsWith('lob/')) {
					continue;
				}
			}

			if (!(angular.toJson(item1[prop]) === angular.toJson(item2[prop]))) {

				same = false;
				break;
			}

		}

		return same;
	}

};
