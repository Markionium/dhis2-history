
var significantZeros = []; // Identifiers for which zero values are insignificant, also used in entry.js

function addEventListeners() 
{
	var inputs = document.getElementsByName( "entryfield" ) 

	for ( var i = 0, input; input = inputs[i]; i++ )
	{
		input.addEventListener('focus', valueFocus, false);
	}

    var selects = document.getElementsByName( "entryselect" );

	for ( var i = 0, select; select = selects[i]; i++ )
	{
		select.addEventListener('focus', valueFocus, false);
	}
}

function clearPeriod()
{	
	clearList( document.getElementById( 'selectedPeriodIndex' ) );	
	clearEntryForm();
}

function clearEntryForm()
{
	$( '#contentDiv' ).html( '' );
}

// -----------------------------------------------------------------------------
// OrganisationUnit Selection
// -----------------------------------------------------------------------------

function organisationUnitSelected( orgUnits )
{
    $( '#selectedDataSetId' ).removeAttr( 'disabled' );
    
    var dataSetId = $( '#selectedDataSetId' ).val();
    
    var url = 'loadDataSets.action';
    
    var list = document.getElementById( 'selectedDataSetId' );
    
    clearList( list );
    
    $.getJSON( url, function( json ) {    
    	$( '#selectedOrganisationUnit' ).val( json.organisationUnit.name );
    	$( '#currentOrganisationUnit' ).html( json.organisationUnit.name );
    	
    	addOptionToList( list, '-1', '[ ' + i18n_select_data_set + ' ]' );
    	
    	for ( i in json.dataSets ) {
    		addOptionToList( list, json.dataSets[i].id, json.dataSets[i].name );
    	}
    	
    	if ( json.dataSetValid && dataSetId != null ) {
    		$( '#selectedDataSetId' ).val( dataSetId );
    		
    		if ( json.periodValid ) {
    			showLoader();
    			$( '#contentDiv' ).load( 'select.action', displayEntryFormCompleted );
    		}
    	}
    	else {
    		clearPeriod();
    	}
    } );
}

selection.setListenerFunction( organisationUnitSelected );

// -----------------------------------------------------------------------------
// Next/Previous Periods Selection
// -----------------------------------------------------------------------------

function nextPeriodsSelected()
{
	displayPeriodsInternal( true, false );
}

function previousPeriodsSelected()
{
	displayPeriodsInternal( false, true );
}

function displayPeriodsInternal( next, previous ) 
{
	disableNextPrevButtons();
	
	var url = 'loadNextPreviousPeriods.action?next=' + next + '&previous=' + previous;
	
	var list = document.getElementById( 'selectedPeriodIndex' );
		
	clearList( list );
	    
    $.getJSON( url, function( json ) {    	
		addOptionToList( list, '-1', '[ ' + i18n_select_period + ' ]' );
	
    	for ( i in json.periods ) {
    		addOptionToList( list, i, json.periods[i].name );
    	}
    	
    	enableNextPrevButtons();
    } );
}

function disableNextPrevButtons()
{
	$( '#nextButton' ).attr( 'disabled', 'disabled' );
	$( '#prevButton' ).attr( 'disabled', 'disabled' );
}

function enableNextPrevButtons()
{
	$( '#nextButton' ).removeAttr( 'disabled' );
	$( '#prevButton' ).removeAttr( 'disabled' );
}

// -----------------------------------------------------------------------------
// DataSet Selection
// -----------------------------------------------------------------------------

function dataSetSelected()
{
    $( '#selectedPeriodIndex' ).removeAttr( 'disabled' );
    $( '#prevButton' ).removeAttr( 'disabled' );
    $( '#nextButton' ).removeAttr( 'disabled' );
    
	var dataSetId = $( '#selectedDataSetId' ).val();	
	var periodIndex = $( '#selectedPeriodIndex' ).val();
	
	if ( dataSetId && dataSetId != -1 )
	{
		var url = 'loadPeriods.action?dataSetId=' + dataSetId;

		var list = document.getElementById( 'selectedPeriodIndex' );
		
	    clearList( list );
	    
	    $.getJSON( url, function( json ) {
	    	significantZeros = json.significantZeros;
	    	
	    	addOptionToList( list, '-1', '[ ' + i18n_select_period + ' ]' );
		
	    	for ( i in json.periods ) {
	    		addOptionToList( list, i, json.periods[i].name );
	    	}
	    	
	    	if ( json.periodValid && periodIndex != null ) {
	    		showLoader();	    		
	    		$( '#selectedPeriodIndex' ).val( periodIndex );
    			$( '#contentDiv' ).load( 'select.action', setDisplayModes );
	    	}
	    	else {
	    		clearEntryForm();
	    	}
	    } );
	}
}

// -----------------------------------------------------------------------------
// DisplayMode Selection
// -----------------------------------------------------------------------------

function displayModeSelected()
{
	showLoader();
	
	var url = 'select.action?displayMode=' + $("input[name='displayMode']:checked").val();
	
	$( '#contentDiv' ).load( url, displayEntryFormCompleted );
}

// -----------------------------------------------------------------------------
// Period Selection
// -----------------------------------------------------------------------------

function periodSelected()
{
	var periodName = $( '#selectedPeriodIndex :selected' ).text();
	
	$( '#currentPeriod' ).html( periodName );
		
	var periodIndex = $( '#selectedPeriodIndex' ).val();
	
	if ( periodIndex && periodIndex != -1 )	{
		showLoader();
		var url = 'select.action?selectedPeriodIndex=' + periodIndex;
		$( '#contentDiv' ).load( url, setDisplayModes );
	}
}

function displayEntryFormCompleted()
{
	addEventListeners();
	hideLoader();
	enable( 'validationButton' );
}

function setDisplayModes()
{
	displayEntryFormCompleted();
	
	$.getJSON( 'loadDisplayModes.action', function( json ) {
		$( '#displayModeCustom' ).removeAttr( 'disabled' );
		$( '#displayModeSection' ).removeAttr( 'disabled' );
		$( '#displayModeDefault' ).removeAttr( 'disabled' );
		
		$( '#displayModeCustom' ).removeAttr( 'checked' );
		$( '#displayModeSection' ).removeAttr( 'checked' );
		$( '#displayModeDefault' ).removeAttr( 'checked' );
		
		if ( json.displayMode == 'customform' ) {
			$( '#displayModeCustom' ).attr( 'checked', 'checked' );
		}
		else if ( json.displayMode == 'sectionform' ) {
			$( '#displayModeSection' ).attr( 'checked', 'checked' );
		}
		else {
			$( '#displayModeDefault' ).attr( 'checked', 'checked' );
		}
		
		if ( !json.customForm ) {
			$( '#displayModeCustom' ).attr( 'disabled', 'disabled' );
		}		
		if ( !json.sectionForm ) {
			$( '#displayModeSection' ).attr( 'disabled', 'disabled' );
		}		
	} );
}

function valueFocus(e) 
{
	//Retrieve the data element id from the id of the field
	var baseId = e.target.id;	
	
	var opId = baseId;
	var str = baseId;
	
	if(	baseId.indexOf(':') != -1 )
	{
		opId = baseId.substr( baseId.indexOf(':')+1, baseId.length );
		str = baseId.substr( 0, baseId.indexOf(':') );
	}
	
	var match1 = /.*\[(.*)\]/.exec(str); //value[-dataElementId-]	
	var match2 = /.*\[(.*)\]/.exec(opId); //value[-optionComboId-]
	
	if ( ! match1 )
	{				
		return;
	}

	deId = match1[1];
	ocId = match2[1];		
	
	var nameContainer = document.getElementById('value[' + deId + '].name');
	var opCbContainer = document.getElementById('value[option' + ocId + '].name');
	var minContainer = document.getElementById('value[' + deId + ':' + ocId +'].min');	
	var maxContainer = document.getElementById('value[' + deId + ':' + ocId +'].max');
	
	if ( ! nameContainer )
	{		
		return;
	}

	var name = '';
	var optionName = '';
	
	var as = nameContainer.getElementsByTagName('a');

	if ( as.length > 0 )	//Admin rights: Name is in a link
	{
		name = as[0].firstChild.nodeValue;
	} 
	else 
	{
		name = nameContainer.firstChild.nodeValue;
	}
	
	if( opCbContainer )
	{	    
		if( opCbContainer.firstChild )
		{
		    optionName = opCbContainer.firstChild.nodeValue;
		}			
	}
	
	if( minContainer )
	{
	    	    
	    if( minContainer.firstChild )
	    {        
	        optionName += " - "+minContainer.firstChild.nodeValue; 
	    }	    
	}
	
	if( maxContainer )
	{
	    if( maxContainer.firstChild )
	    {
	        optionName += " - "+maxContainer.firstChild.nodeValue;
	    }
	}
	    
    var curDeSpan = document.getElementById('currentDataElement');
     
    curDeSpan.firstChild.nodeValue = name;
    
    document.getElementById("currentOptionCombo").innerHTML  = optionName;
	
}

function keyPress( event, field )
{
    var key = event.keyCode || event.charCode || event.which;
    
    var focusField = ( key == 13 || key == 40 ) ? getNextEntryField( field ) : ( key == 38 ) ? getPreviousEntryField( field ) : false;
    
    if ( focusField )
    {
        focusField.focus();
    }
    
    return true;
}

function getNextEntryField( field )
{
    var fields = $('input[name="entryfield"]');

    var index = field.tabIndex;
    
    while (fields[index]) {
        if (!fields[index].disabled) {
            return fields[index];
        }
        index++;
    }
}

function getPreviousEntryField( field )
{
    var fields = $('input[name="entryfield"]');
    
    var index = field.tabIndex - 2;
    
    while (fields[index]) {
        if (!fields[index].disabled) {
            return fields[index];
        }
        index--;
    }
}

// -----------------------------------------------------------------------------
// Data completeness
// -----------------------------------------------------------------------------

function validateCompleteDataSet()
{
	var confirmed = confirm( i18n_confirm_complete );
	
	if ( confirmed )
	{
	    var url = "getValidationViolations.action";
    
        var request = new Request();
        request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( registerCompleteDataSet );
        request.send( url );        
	}
}

function registerCompleteDataSet( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    
    if ( type == "none" )
    {    
        var url = "registerCompleteDataSet.action";
    
        var request = new Request();
		request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( registerReceived );
        request.send( url );
    }
    else
    {
        window.open( 'validate.action', '_blank', 'width=800, height=400, scrollbars=yes, resizable=yes' );
    }
}

function registerReceived( messageElement )
{
	var type = messageElement.getAttribute( 'type' );
	
    if ( type=='input' )
    {
		setHeaderDelayMessage( messageElement.firstChild.nodeValue );
		return;
	}
	
	document.getElementById( "completeButton" ).disabled = true;
	document.getElementById( "undoButton" ).disabled = false;
}

function undoCompleteDataSet()
{
	var confirmed = confirm( i18n_confirm_undo );
	
	if ( confirmed )
	{
        var url = "undoCompleteDataSet.action";
    
        var request = new Request();
        request.setCallbackSuccess( undoReceived );
        request.send( url );
	}
}

function undoReceived( messageElement )
{
    document.getElementById( "completeButton" ).disabled = false;
    document.getElementById( "undoButton" ).disabled = true;
}

// -----------------------------------------------------------------------------
// Validation
// -----------------------------------------------------------------------------

function validate()
{
    window.open( 'validate.action', '_blank', 'width=800, height=400, scrollbars=yes, resizable=yes' );
}

// -----------------------------------------------------------------------------
// History
// -----------------------------------------------------------------------------

function viewHist( dataElementId, optionComboId )
{
	viewHistory( dataElementId, optionComboId, true );
}

function viewHistory( dataElementId, optionComboId, showComment )
{
    window.open( 'viewHistory.action?dataElementId=' + dataElementId + '&optionComboId=' + optionComboId + '&showComment=' + showComment, '_blank', 'width=580,height=710,scrollbars=yes' );
}
