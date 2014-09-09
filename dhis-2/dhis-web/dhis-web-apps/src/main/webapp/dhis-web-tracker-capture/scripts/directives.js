'use strict';

/* Directives */

var trackerCaptureDirectives = angular.module('trackerCaptureDirectives', [])

.directive('inputValidator', function() {
    
    return {
        require: 'ngModel',
        link: function (scope, element, attrs, ctrl) {  

            ctrl.$parsers.push(function (value) {
                return parseFloat(value || '');
            });
        }
    };   
})

.directive('selectedOrgUnit', function() {        

    return {        
        restrict: 'A',        
        link: function(scope, element, attrs){  
            
            //when tree has loaded, get selected orgunit - if there is any - and inform angular           
            $(function() {                 
                
                var adapters = [];
                var partial_adapters = [];

                if( dhis2.ou.memoryOnly ) {
                    adapters = [ dhis2.storage.InMemoryAdapter ];
                    partial_adapters = [ dhis2.storage.InMemoryAdapter ];
                } else {
                    adapters = [ dhis2.storage.IndexedDBAdapter, dhis2.storage.DomLocalStorageAdapter, dhis2.storage.InMemoryAdapter ];
                    partial_adapters = [ dhis2.storage.IndexedDBAdapter, dhis2.storage.DomSessionStorageAdapter, dhis2.storage.InMemoryAdapter ];
                }

                dhis2.ou.store = new dhis2.storage.Store({
                    name: OU_STORE_NAME,
                    objectStores: [
                        {
                            name: OU_KEY,
                            adapters: adapters
                        },
                        {
                            name: OU_PARTIAL_KEY,
                            adapters: partial_adapters
                        }
                    ]
                });

                dhis2.ou.store.open().done( function() {
                    selection.load();
                    $( "#orgUnitTree" ).one( "ouwtLoaded", function() {
                        var selected = selection.getSelected()[0];
                        selection.getOrganisationUnit(selected).done(function(data){                            
                            if( data ){
                                scope.selectedOrgUnit = {id: selected, name: data[selected].n};
                                scope.$apply();                                                              
                            }                        
                        });
                    });
                    
                });
            });
            
            //listen to user selection, and inform angular         
            selection.setListenerFunction( organisationUnitSelected );
            selection.responseReceived();
            
            function organisationUnitSelected( orgUnits, orgUnitNames ) {
                scope.selectedOrgUnit = {id: orgUnits[0], name: orgUnitNames[0]};                    
                scope.$apply();
            }            
        }  
    };
})

.directive('d2CustomForm', function($compile, $parse, CustomFormService) {
    return{ 
        restrict: 'E',
        link: function(scope, elm, attrs){   
            
            var customFormType = attrs.customFormType;
            var customFormObject = $parse(attrs.customFormObject)(scope);
            
            if(customFormType === 'PROGRAM_STAGE'){                
                var customForm = CustomFormService.getForProgramStage(customFormObject);  
                elm.html(customForm ? customForm : '');
                $compile(elm.contents())(scope);     
            }
        }
    };
})

.directive('d2PopOver', function($compile, $templateCache){
    return {        
        restrict: 'EA',
        link: function(scope, element, attrs){
            var content = $templateCache.get("note.html");
            content = $compile(content)(scope);
            var options = {
                    content: content,
                    placement: 'bottom',
                    trigger: 'hover',
                    html: true,
                    title: scope.title               
                };            
            $(element).popover(options);
        },
        scope: {
            content: '=',
            title: '@details',
            template: "@template"
        }
    };
})

.directive('sortable', function() {        

    return {        
        restrict: 'A',        
        link: function(scope, element, attrs){
            element.sortable({
                connectWith: ".connectedSortable",
                placeholder: "ui-state-highlight",
                tolerance: "pointer",
                handle: '.handle'
            }).disableSelection();  
            //scope.$apply();
        }  
    };
})

.directive('d2ContextMenu', function(ContextMenuSelectedItem) {
        
    return {        
        restrict: 'A',
        link: function(scope, element, attrs){
            var contextMenu = $("#contextMenu");                   
            
            element.click(function (e) {
                var selectedItem = $.parseJSON(attrs.selectedItem);
                ContextMenuSelectedItem.setSelectedItem(selectedItem);
                
                var menuHeight = contextMenu.height();
                var menuWidth = contextMenu.width();
                var winHeight = $(window).height();
                var winWidth = $(window).width();

                var pageX = e.pageX;
                var pageY = e.pageY;

                contextMenu.show();

                if( (menuWidth + pageX) > winWidth ) {
                  pageX -= menuWidth;
                }

                if( (menuHeight + pageY) > winHeight ) {
                  pageY -= menuHeight;

                  if( pageY < 0 ) {
                      pageY = e.pageY;
                  }
                }
                
                contextMenu.css({
                    left: pageX,
                    top: pageY
                });

                return false;
            });
            
            contextMenu.on("click", "a", function () {                    
                contextMenu.hide();
            });

            $(document).click(function () {                                        
                contextMenu.hide();
            });
        }     
    };
})

.directive('ngDate', function($filter) {
    return {
        restrict: 'A',
        require: 'ngModel',        
        link: function(scope, element, attrs, ctrl) {
            element.datepicker({
                changeYear: true,
                changeMonth: true,
                dateFormat: 'yy-mm-dd',
                yearRange: '-120:+0',
                minDate: attrs.minDate,
                maxDate: attrs.maxDate,
                onSelect: function(date) {
                    //scope.date = date;
                    ctrl.$setViewValue(date);
                    $(this).change();                    
                    scope.$apply();
                }                
            })
            .change(function() {
                //var rawDate = $filter('date')(this.value, 'yyyy-MM-dd'); 
                var rawDate = this.value;
                var convertedDate = moment(this.value, 'YYYY-MM-DD')._d;
                convertedDate = $filter('date')(convertedDate, 'yyyy-MM-dd');       

                if(rawDate != convertedDate){
                    scope.invalidDate = true;
                    ctrl.$setViewValue(this.value);                                   
                    ctrl.$setValidity('foo', false);                    
                    scope.$apply();     
                }
                else{
                    scope.invalidDate = false;
                    ctrl.$setViewValue(this.value);                                   
                    ctrl.$setValidity('foo', true);                    
                    scope.$apply();     
                }
            });    
        }      
    };   
})

.directive('blurOrChange', function() {
    
    return function( scope, elem, attrs) {
        elem.datepicker({
            onSelect: function() {
                scope.$apply(attrs.blurOrChange);
                $(this).change();                                        
            }
        }).change(function() {
            scope.$apply(attrs.blurOrChange);
        });
    };
})

.directive('typeaheadOpenOnFocus', function ($compile) {
  return {
    require: ['typeahead', 'ngModel'],
    link: function (scope, element, attr, ctrls) {        
        element.bind('focus', function () {          
            ctrls[0].getMatchesAsync(ctrls[1].$viewValue);
            scope.$watch(attr.ngModel, function(value) {
                if(value === '' || angular.isUndefined(value)){
                    ctrls[0].getMatchesAsync(ctrls[1].$viewValue);
                }                
            });
      });
    }
  };
})

.directive('paginator', function factory() {
    return {
        restrict: 'E',
        controller: function ($scope, Paginator) {
            $scope.paginator = Paginator;
        },
        templateUrl: 'views/pagination.html'
    };
})

.directive('draggableModal', function(){
    return {
      restrict: 'EA',
      link: function(scope, element) {
        element.draggable();
      }
    };  
})

.directive('serversidePaginator', function factory() {
    return {
        restrict: 'E',
        controller: function ($scope, Paginator) {
            $scope.paginator = Paginator;
        },
        templateUrl: 'views/serverside-pagination.html'
    };
})

.directive('clientsidePaginator', function factory() {
    return {
        restrict: 'E',
        controller: function ($scope, Paginator) {
            $scope.paginator = Paginator;
        },
        templateUrl: 'views/clientside-pagination.html'
    };
})

.directive('d2Enter', function () {
    return function (scope, element, attrs) {
        element.bind("keydown keypress", function (event) {
            if(event.which === 13) {
                scope.$apply(function (){
                    scope.$eval(attrs.d2Enter);
                });
                event.preventDefault();
            }
        });
    };
});

