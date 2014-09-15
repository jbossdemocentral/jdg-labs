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
    }).when('/stats', {
        templateUrl: 'views/stats.html',
        controller: 'StatsCtrl'
    }).otherwise({
        redirectTo: '/'
    });
});
 
app.controller('ListCtrl', function ($scope, $http) {
    $http.get('/todo/rest/tasks').success(function (data) {
        $scope.tasks = data;
    }).error(function (data, status) {
        console.log('Error ' + data);
    });
 
    $scope.taskStatusChanged = function (task) {
        if(task.done)
        	task.completedOn = new Date();
        else
        	task.completedOn = null;
        $http.put('/todo/rest/tasks/' + task.id, task).success(function (data) {
            console.log('status changed');
        }).error(function (data, status) {
            console.log('Error ' + data);
        });
    };
    
    $scope.filter = function() {
    	var value = $scope.filter.value;
    	if(value.length > 0) {
	    	$http.get('/todo/rest/tasks/filter/' + $scope.filter.value).success(function (data) {
	            $scope.tasks = data;
	        }).error(function (data, status) {
	            console.log('Error ' + data);
	        });
    	} else {
    		$http.get('/todo/rest/tasks').success(function (data) {
    	        $scope.tasks = data;
    	    }).error(function (data, status) {
    	        console.log('Error ' + data);
    	    });
    	}
	};
});
 
app.controller('CreateCtrl', function ($scope, $http, $location) {
    $scope.task = {
        done: false
    };
 
    $scope.createTask = function () {
        $http.post('/todo/rest/tasks', $scope.task).success(function (data) {
            $location.path('/');
        }).error(function (data, status) {
            console.log('Error ' + data);
        });
    };
      
});

app.controller('StatsCtrl', function ($scope, $http, $location) {
	$http.get('/todo/rest/stats/os').success(function (data) {
		drawStatImage(data,"#os-chart");
	}).error(function (data, status) {
	    console.log('Error ' + data);
	});	
	
	$http.get('/todo/rest/stats/browser').success(function (data) {
		drawStatImage(data,"#browser-chart");
	}).error(function (data, status) {
	    console.log('Error ' + data);
	});
	
	$scope.genTestData = function() {
		$http.get('/todo/rest/stats/gentestdata').success(
				function(data) {
					window.location.reload();
				}
		);	
	};
});


var drawStatImage = function(data, divId) {
	var keys = $.map(data, function(v,k) { return k; });
	var values = $.map(data, function(v,k){ return v; });
	
	var width = 500, barHeight = 40;	
	
	var x = d3.scale.linear()
	    .domain([0, d3.max(values)])
	    .range([0, width]);
	
	var chart = d3.select(divId).append("svg")
	    .attr("width", width)
	    .attr("height", barHeight * keys.length);
	
	var bar = chart.selectAll("g")
	    .data(keys)
		  .enter().append("g")
		  .attr("transform", function(d, i) { return "translate(0," + i * barHeight + ")"; });
	
	bar.append("rect")
		.attr("style", function(d) { return getColorForEntry(d); })
	    .attr("width", function(d) { return x(data[d]); })
	    .attr("height", barHeight - 1);
	
	bar.append("text")
	    .attr("x", function(d) { return x(data[d]) - 3; })
	    .attr("y", barHeight / 2)
	    .attr("dy", ".35em")
	    .text(function(d) { return d + " (" + data[d] + ") "; });
};

var getColorForEntry = function(text) {
	if(text=="iPhone") {	
		return "fill: #cc0000";
	} else if(text=="Android") {	
		return "fill: #9e292b";
	} else if(text=="Macintosh") {	
		return "fill: #781f1c";
	} else if(text=="Windows") {	
		return "fill: #003d6e";
	} else if(text=="Internet Explorer") {	
		return "fill: #003d6e";
	} else if(text=="Googel Chrome") {	
		return "fill: #cc0000";
	} else if(text=="Safari") {	
		return "fill: #9e292b";
	} else {
		return "fill: steelblue";
	}
};