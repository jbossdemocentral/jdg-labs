var app = angular.module('task', [
    'ngCookies',
    'ngResource',
    'ngSanitize',
    'ngRoute'
]);
 
app.config(function ($routeProvider) {
    $routeProvider.when('/', {
        templateUrl: 'views/list.html',
        controller: 'ListCtrl'
    }).when('/create', {
        templateUrl: 'views/create.html',
        controller: 'CreateCtrl'
    }).when('/edit', {
        templateUrl: 'views/create.html',
        controller: 'EditCtrl'
    }).otherwise({
        redirectTo: '/'
    });
});
 
app.controller('ListCtrl', function ($scope, $http, $location) {
	var baseUrl=getBaseUrl($location);
    $http.get(baseUrl + '/rest/tasks').success(function (data) {
        $scope.tasks = data;
    }).error(function (data, status) {
        console.log('Error ' + data);
    });
 
    $scope.taskStatusChanged = function (task) {
        console.log(task);
        if(task.done)
        	task.completedOn = new Date();
        else
        	task.completedOn = null;
        $http.put(baseUrl + '/rest/tasks/' + task.id, task).success(function (data) {
            console.log('status changed');
            $location.path('/');
        }).error(function (data, status) {
            console.log('Error ' + data);
        });
    };
    
    $scope.deleteTask = function (task) {
        //Since delete is a reserved keyword in EMCAScript, IE has issues with $http.delete(...)
        $http['delete'](baseUrl + '/rest/tasks/' + task.id).success(function (data) {
            console.log('Task deleted');
            $location.path('/');
        }).error(function (data, status) {
            console.log('Error ' + data);
        });
    };
    
    $scope.editTask = function (task) {
    	console.log("Edit task with id " + task.id)
    	$scope.task = task;
    	$location.path('/edit');
    };
});
 
app.controller('CreateCtrl', function ($scope, $http, $location) {
	var baseUrl=getBaseUrl($location);
	$scope.task = {
        done: false
    };
 
    $scope.saveTask = function () {
        console.log($scope.task);
        $http.post(baseUrl + '/rest/tasks', $scope.task).success(function (data) {
            $location.path('/');
        }).error(function (data, status) {
            console.log('Error ' + data);
        });
    };
});

app.controller('EditCtrl', function ($scope, $http, $location) {
	var baseUrl=getBaseUrl($location);
    $scope.saveTask = function () {
        console.log($scope.task);
        $http.put(baseUrl + '/rest/tasks', $scope.task).success(function (data) {
            $location.path('/');
        }).error(function (data, status) {
            console.log('Error ' + data);
        });
    };
});

var getBaseUrl = function($location) {
	return '/' + $location.absUrl().substr(7).split('/')[1];
};