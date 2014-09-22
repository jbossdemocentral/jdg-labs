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
    }).otherwise({
        redirectTo: '/'
    });
});
 
app.controller('ListCtrl', function ($scope, $http) {
    $http.get('/mytodo/rest/tasks').success(function (data) {
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
        $http.put('/mytodo/rest/tasks/' + task.id, task).success(function (data) {
            console.log('status changed');
        }).error(function (data, status) {
            console.log('Error ' + data);
        });
    };
    

});
 
app.controller('CreateCtrl', function ($scope, $http, $location) {
    $scope.task = {
        done: false
    };
 
    $scope.createTask = function () {
        console.log($scope.task);
        $http.post('/mytodo/rest/tasks', $scope.task).success(function (data) {
            $location.path('/');
        }).error(function (data, status) {
            console.log('Error ' + data);
        });
    };
      
});
