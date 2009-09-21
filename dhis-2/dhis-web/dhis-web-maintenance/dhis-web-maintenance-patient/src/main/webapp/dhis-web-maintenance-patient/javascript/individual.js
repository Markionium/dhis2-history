
function organisationUnitSelected( orgUnits )
{	
    window.location.href = 'individual.action';
}

selection.setListenerFunction( organisationUnitSelected );

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showIndividualDetails( patientId )
{
    var request = new Request();
    request.setResponseTypeXML( 'patient' );
    request.setCallbackSuccess( patientReceived );
    request.send( 'getPatient.action?id=' + patientId );
}

function patientReceived( patientElement )
{
	setFieldValue( 'identifierField', getElementValue( patientElement, 'identifier' ) );
	setFieldValue( 'firstNameField', getElementValue( patientElement, 'firstName' ) );
    setFieldValue( 'middleNameField', getElementValue( patientElement, 'middleName' ) );
    setFieldValue( 'lastNameField', getElementValue( patientElement, 'lastName' ) );
    setFieldValue( 'gender', getElementValue( patientElement, 'gender' ) );  
    setFieldValue( 'dateOfBirthField', getElementValue( patientElement, 'dateOfBirth' ) );
      
    
    var address1 = getElementValue( patientElement, 'address1');
    setFieldValue( 'address1Field', address1 ? address1 : '[' + i18n_none + ']' );
    
    var address2 = getElementValue( patientElement, 'address2');
    setFieldValue( 'address2Field', address2 ? address2 : '[' + i18n_none + ']' );
    
    var landMark = getElementValue( patientElement, 'landMark');
    setFieldValue( 'landMarkField', landMark ? landMark : '[' + i18n_none + ']' );
    
    var cityVillage = getElementValue( patientElement, 'cityVillage');
    setFieldValue( 'cityVillageField', cityVillage ? cityVillage : '[' + i18n_none + ']' );
    
    var stateProvince = getElementValue( patientElement, 'stateProvince');
    setFieldValue( 'stateProvinceField', stateProvince ? stateProvince : '[' + i18n_none + ']' );
    
    var stateProvince = getElementValue( patientElement, 'stateProvince');
    setFieldValue( 'stateProvinceField', stateProvince ? stateProvince : '[' + i18n_none + ']' );
    
    var country = getElementValue( patientElement, 'country');
    setFieldValue( 'countryField', country ? country : '[' + i18n_none + ']' );
    
    var postalCode = getElementValue( patientElement, 'postalCode');
    setFieldValue( 'postalCodeField', postalCode ? postalCode : '[' + i18n_none + ']' );    
   
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove patient
// -----------------------------------------------------------------------------

function removeIndividual( patientId, firstName, lastName )
{
    var result = window.confirm( i18n_confirm_death + '\n\n' + lastName + ',' + firstName );
    
    if ( result )
    {
    	var request = new Request();
        request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( removeIndividualCompleted );
        window.location.href = 'removeIndividual.action?id=' + patientId;
    }
}

function removeIndividualCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        window.location.href = 'individual.action';
    }
    else if ( type = 'error' )
    {
        setFieldValue( 'warningField', message );
        
        showWarning();
    }
}

//-----------------------------------------------------------------------------
// Search Individual
//-----------------------------------------------------------------------------

function validateSearchIndividual()
{	
	
	var url = 'validateSearchIndividual.action?' +
			'searchText=' + getFieldValue( 'searchText' );	
	
	var request = new Request();
	request.setResponseTypeXML( 'message' );
 	request.setCallbackSuccess( searchValidationCompleted );    
 	request.send( url );        

 	return false;
}

function searchValidationCompleted( messageElement )
{
	var type = messageElement.getAttribute( 'type' );
	var message = messageElement.firstChild.nodeValue;
	
	if ( type == 'success' )
	{
		var form = document.getElementById( 'searchIndividualForm' );        
		form.submit();
	}
	else if ( type == 'error' )
	{
		window.alert( i18n_searching_patient_failed + ':' + '\n' + message );
	}
	else if ( type == 'input' )
	{
		document.getElementById( 'message' ).innerHTML = message;
		document.getElementById( 'message' ).style.display = 'block';
	}
}


// -----------------------------------------------------------------------------
// Add Patient
// -----------------------------------------------------------------------------

function validateAddIndividual()
{	
	
	var url = 'validateIndividual.action?' +
			'identifier=' + getFieldValue( 'identifier' ) +
			'&firstName=' + getFieldValue( 'firstName' ) +
	        '&middleName=' + getFieldValue( 'middleName' ) +
	        '&lastName=' + getFieldValue( 'lastName' ) +
	        '&gender=' + getFieldValue( 'gender' ) +
	        '&birthDate=' + getFieldValue( 'birthDate' ) ;
	
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addValidationCompleted );    
    request.send( url );        

    return false;
}

function addValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var form = document.getElementById( 'addIndividualForm' );        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_adding_patient_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

// -----------------------------------------------------------------------------
// Update Patient
// -----------------------------------------------------------------------------

function validateUpdateIndividual()
{
    var url = 'validateIndividual.action?' + 
    		'id=' + getFieldValue( 'id' ) +
    		'&identifier=' + getFieldValue( 'identifier' ) +
    		'&firstName=' + getFieldValue( 'firstName' ) +
	        '&middleName=' + getFieldValue( 'middleName' ) +
	        '&lastName=' + getFieldValue( 'lastName' ) +
	        '&gender=' + getFieldValue( 'gender' ) +
	        '&birthDate=' + getFieldValue( 'birthDate' ) ;
	
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateValidationCompleted );   
    
    request.send( url );
        
    return false;
}

function updateValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
    	var form = document.getElementById( 'updateIndividualForm' );        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_saving_patient_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}