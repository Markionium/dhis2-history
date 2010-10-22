
function showDataElementCategoryComboDetails( categoryComboId )
{
    var request = new Request();
    request.setResponseTypeXML( 'dataElementCategoryCombo' );
    request.setCallbackSuccess( dataElementCategoryComboReceived );
    request.send( 'getDataElementCategoryCombo.action?id=' + categoryComboId );
}

function dataElementCategoryComboReceived( dataElementCategoryComboElement )
{
    setInnerHTML( 'nameField', getElementValue( dataElementCategoryComboElement, 'name' ) );
	setInnerHTML( 'dataElementCategoryCountField', getElementValue( dataElementCategoryComboElement, 'dataElementCategoryCount' ) );
          
    showDetails();
}

// -----------------------------------------------------------------------------
// Delete Category
// -----------------------------------------------------------------------------

function removeDataElementCategoryCombo( categoryComboId, categoryComboName )
{
	removeItem( categoryComboId, categoryComboName, i18n_confirm_delete, 'removeDataElementCategoryCombo.action' );
}

// ----------------------------------------------------------------------
// Validation
// ----------------------------------------------------------------------

function validateSelectedCategories( form )
{
	jQuery.postJSON( "validateDataElementCategoryCombo.action",
		{ selectedCategories: getArrayValueOfListById( 'selectedList' ) },
		function( json ){
			if( json.response == 'success' ) form.submit();
			else markInvalid( 'selectedCategories', json.message );
		});
}