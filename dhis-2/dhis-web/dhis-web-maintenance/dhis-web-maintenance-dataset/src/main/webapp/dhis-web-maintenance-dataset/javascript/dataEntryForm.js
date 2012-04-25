
// -----------------------------------------------------------------------------
// Delete DataEntryForm
// -----------------------------------------------------------------------------

function removeDataEntryForm( dataSetIdField, dataEntryFormId, dataEntryFormName )
{
	var result = window.confirm( i18n_confirm_delete + '\n\n' + dataEntryFormName );

	if ( result )
	{
		window.location.href = 'delDataEntryForm.action?dataSetId=' + dataSetIdField + "&dataEntryFormId=" + dataEntryFormId;
	}
}

// ----------------------------------------------------------------------
// Validation
// ----------------------------------------------------------------------

function validateDataEntryForm()
{  
	$.postUTF8( 'validateDataEntryForm.action',
	{
		name: $( '#nameField' ).val(),
		dataSetId: $( '#dataSetIdField' ).val(),
		dataEntryFormId: dataEntryFormId
	}, 
	function( json ) 
	{
		if ( autoSave == false )
		{
			dataEntryFormValidationCompleted( json );
		}
		else
		{
			autoSaveDataEntryFormValidationCompleted( json );
		}
	} );
}

function dataEntryFormValidationCompleted( json )
{
	if ( json.response == 'success' )
	{  
		$( '#saveDataEntryForm' ).submit();
	}
	else if ( json.response = 'input' )
	{
		setHeaderDelayMessage( json.message );
	}
}

// -----------------------------------------------------------------------------
// Auto-save DataEntryForm
// -----------------------------------------------------------------------------

function autoSaveDataEntryFormValidationCompleted( json )
{
	if ( json.response == 'success' )
	{
		autoSaveDataEntryForm();
	}
	else if ( json.response = 'input' )
	{
		setHeaderDelayMessage( json.message );
	}
}

function autoSaveDataEntryForm() 
{
	var field = $("#designTextarea").ckeditorGet();
	var designTextarea = field.getData();
	
	$.postUTF8( 'autoSaveDataEntryForm.action',
	{
		nameField: getFieldValue('nameField'),
		designTextarea: designTextarea,
		dataSetIdField: getFieldValue('dataSetIdField'),
		dataEntryFormId: dataEntryFormId
	},
	function( json ) 
	{
		setHeaderDelayMessage( i18n_save_success ); 
		stat = "EDIT";
		dataEntryFormId = json.message;
		enable('delete');
	} );
}