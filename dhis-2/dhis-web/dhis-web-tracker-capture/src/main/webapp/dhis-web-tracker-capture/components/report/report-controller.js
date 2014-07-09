trackerCapture.controller('ReportController',
        function($scope,
                $modal,
                DateUtils,
                EventUtils,
                TEIService,
                TEIGridService,
                TranslationService,
                AttributesFactory,
                DHIS2EventFactory) {

    TranslationService.translate();
    
    $scope.ouModes = [{name: 'SELECTED'}, {name: 'CHILDREN'}, {name: 'DESCENDANTS'}, {name: 'ACCESSIBLE'}];         
    $scope.selectedOuMode = $scope.ouModes[0];
    $scope.report = {};
    
    $scope.generateReport = function(){
        
        $scope.dataReady = false;
        
        //check for form validity
        $scope.outerForm.submitted = true;        
        if( $scope.outerForm.$invalid || !$scope.selectedProgram){
            return false;
        }
        
        $scope.programStages = [];
        angular.forEach($scope.selectedProgram.programStages, function(stage){
            $scope.programStages[stage.id] = stage;
        });
            
        AttributesFactory.getByProgram($scope.selectedProgram).then(function(atts){            
            $scope.gridColumns = TEIGridService.generateGridColumns(atts, $scope.selectedOuMode.name);      
        });  
        
        //fetch TEIs for the selected program and orgunit/mode
        TEIService.search($scope.selectedOrgUnit.id, 
                            $scope.selectedOuMode.name,
                            null,
                            'program=' + $scope.selectedProgram.id,
                            null,
                            $scope.pager,
                            false).then(function(data){
            if(data.rows){
                $scope.teiCount = data.rows.length;
                $scope.dataReady = true;
            }
            
            
            //process tei grid
            $scope.teiList = TEIGridService.format(data);          
            
            DHIS2EventFactory.getByOrgUnitAndProgram($scope.selectedOrgUnit.id, $scope.selectedOuMode.name, $scope.selectedProgram.id).then(function(eventList){
                $scope.dhis2Events = [];
                angular.forEach(eventList, function(ev){
                    if(ev.trackedEntityInstance){
                        ev.name = $scope.programStages[ev.programStage].name;
                        ev.programName = $scope.selectedProgram.name
                        ev.statusColor = EventUtils.getEventStatusColor(ev); 
                        ev.eventDate = DateUtils.format(ev.eventDate);
                        
                        if($scope.dhis2Events[ev.trackedEntityInstance]){
                            $scope.dhis2Events[ev.trackedEntityInstance].push(ev);
                        }
                        else{
                            $scope.dhis2Events[ev.trackedEntityInstance] = [ev];
                        }
                        ev = EventUtils.setEventOrgUnitName(ev);
                    }
                });
            });
        });
    };
    
    $scope.showEventDetails = function(dhis2Event, selectedTei){
        
        var modalInstance = $modal.open({
            templateUrl: 'components/report/eventDetails.html',
            controller: 'EventDetailsController',
            resolve: {
                dhis2Event: function () {
                    return dhis2Event;
                },
                gridColumns: function(){
                    return $scope.gridColumns;
                },
                selectedTei: function(){
                    return selectedTei;
                },
                entityName: function(){
                    return $scope.selectedProgram.trackedEntity.name;
                },
                reportMode: function(){
                    return 'PROGRAM';
                }
            }
        });

        modalInstance.result.then({
        });
    };    
})

//Controller for event details
.controller('EventDetailsController', 
    function($scope, 
            $modalInstance,
            orderByFilter,
            ProgramStageFactory,
            dhis2Event,
            selectedTei,
            gridColumns,
            entityName,
            reportMode){
    
    $scope.selectedTei = selectedTei;
    $scope.gridColumns = gridColumns;
    $scope.entityName = entityName;
    $scope.reportMode = reportMode;
    $scope.currentEvent = dhis2Event;
    $scope.currentEvent.providedElsewhere = [];
    
    if(!angular.isUndefined( $scope.currentEvent.notes)){
        $scope.currentEvent.notes = orderByFilter($scope.currentEvent.notes, '-storedDate');            
        angular.forEach($scope.currentEvent.notes, function(note){
            note.storedDate = moment(note.storedDate).format('YYYY-MM-DD @ hh:mm A');
        });
    }
    
    ProgramStageFactory.get($scope.currentEvent.programStage).then(function(stage){
        $scope.currentStage = stage;

        $scope.allowProvidedElsewhereExists = false;
        angular.forEach($scope.currentStage.programStageDataElements, function(prStDe){
            $scope.currentStage.programStageDataElements[prStDe.dataElement.id] = prStDe.dataElement;
            if(prStDe.allowProvidedElsewhere){
                $scope.allowProvidedElsewhereExists = true;
                $scope.currentEvent.providedElsewhere[prStDe.dataElement.id] = '';   
            }                
        });
        angular.forEach($scope.currentEvent.dataValues, function(dataValue){
            var val = dataValue.value;
            if(val){
                var de = $scope.currentStage.programStageDataElements[dataValue.dataElement];
                if( de && de.type === 'int' && val){
                    val = parseInt(val);
                    dataValue.value = val;
                }
                $scope.currentEvent[dataValue.dataElement] = val;
            }                    
        });
    });
    
    $scope.close = function () {
        $modalInstance.close();
    };
})

//conroller for tei report
.controller('TeiReportController',
        function($scope,
                $modal,
                CurrentSelection,
                storage,
                DateUtils,
                EventUtils,
                TranslationService,
                ProgramFactory,
                DHIS2EventFactory) {

    TranslationService.translate();    
    
    $scope.programs = [];  
    $scope.programNames = [];  
    $scope.programStageNames = [];
    ProgramFactory.getAll().then(function(programs){
        
        angular.forEach(programs, function(pr){
            $scope.programs.push({id: pr.id, name: pr.name});
            $scope.programNames[pr.id] = {id: pr.id, name: pr.name};
            angular.forEach(pr.programStages, function(stage){
                $scope.programStageNames[stage.id] = stage;
            });
        });
    });
    
    $scope.$on('dashboard', function(event, args) {
        var selections = CurrentSelection.get();
        $scope.selectedOrgUnit = storage.get('SELECTED_OU');
        $scope.selectedTei = selections.tei;  
        $scope.selectedEntity = selections.te;
        $scope.selectedProgram = selections.pr;        
        $scope.selectedEnrollment = selections.enrollment; 
        
        if($scope.selectedTei){            
            $scope.getEvents();
        }       
    });
    
    $scope.getEvents = function(){
        
        $scope.dataFetched = false;
        $scope.dataExists = false;
        var programId = null, orgUnitId = null;
        
        if($scope.selectedProgram){
            programId = $scope.selectedProgram.id;
        }
        
        /*if($scope.selectedOrgUnit){
            orgUnitId = $scope.selectedOrgUnit.id;
        }*/
        
        $scope.report = [];
        angular.forEach($scope.programs, function(pr){
            $scope.report[pr.id] = {events: []};
        });
        
        DHIS2EventFactory.getEventsByProgram($scope.selectedTei.trackedEntityInstance, orgUnitId, programId).then(function(eventList){

            angular.forEach(eventList, function(ev){
                if(ev.program){                    
                    ev.name = $scope.programStageNames[ev.programStage].name;
                    ev.programName = $scope.programNames[ev.program].name;
                    ev.statusColor = EventUtils.getEventStatusColor(ev); 
                    ev.eventDate = DateUtils.format(ev.eventDate);                    
                    $scope.report[ev.program].events.push(ev);
                    ev = EventUtils.setEventOrgUnitName(ev);
                }                
            });
            if(eventList){
                $scope.dataExists = true;
            }
            $scope.dataFetched = true;
        });
    };
    
    $scope.showEventDetails = function(dhis2Event){
        
        var modalInstance = $modal.open({
            templateUrl: 'components/report/eventDetails.html',
            controller: 'EventDetailsController',
            resolve: {
                dhis2Event: function () {
                    return dhis2Event;
                },
                gridColumns: function(){
                    return $scope.gridColumns;
                },
                selectedTei: function(){
                    return $scope.selectedTei;
                },
                entityName: function(){
                    return $scope.selectedEntity.name;
                },
                reportMode: function(){
                    return 'TEI';
                }
            }
        });

        modalInstance.result.then({
        });
    };    
});