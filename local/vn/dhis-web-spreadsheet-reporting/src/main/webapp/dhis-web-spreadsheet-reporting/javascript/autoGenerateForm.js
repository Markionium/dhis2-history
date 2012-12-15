/**
 * Global variables
 */

spreadsheetTreePath = '../dhis-web-spreadsheet-reporting/';

idTemp = null;
importlist = null;
importItemIds = new Array();

// ----------------------------------------------------------------------
// Methods
// ----------------------------------------------------------------------

function validateUploadExcelTemplateForGenerateForm()
{
	jQuery( "#upload" ).upload( 'validateUploadExcelTemplate.action', { 'draft': true },
		function ( data )
		{
			data = data.getElementsByTagName('message')[0];
			var type = data.getAttribute("type");

			if ( type == 'error' ) {
				setMessage( data.firstChild.nodeValue );
			} else {
				uploadExcelTemplateForGenerateForm();
			}
		}, 'xml'
	);
}
	
function uploadExcelTemplateForGenerateForm()
{
	jQuery( "#upload" ).upload( 'uploadExcelTemplateForGenerateForm.action',
		{ 'draft': true, 'allowNewName': false },
		function( data, e ) {
			try {
				window.location.reload();
			}
			catch(e) {
				alert(e);
			}
		}
	);
}

function autoGenerateFormByTemplate()
{
	lockScreen();

	$.ajax({
		cache: false,
		url: spreadsheetTreePath + "autoGenerateFormByTemplate.action",
		dataType: 'xml',
		success: autoGenerateFormByTemplateReceived
	});
}

function autoGenerateFormByTemplateReceived( parentElement ) 
{
	var type = getElementAttribute( parentElement, 'message', 'type' );
	
	if ( type && type == 'error' )
	{
		showErrorMessage( parentElement.firstChild.nodeValue );
	}
	else
	{	
		var aKey 	= new Array();
		var aMerged = new Array();	
		var cells 	= parentElement.getElementsByTagName( 'cell' );

		for (var i  = 0 ; i < cells.length ; i ++)
		{	
			aKey[i]		= cells[i].getAttribute( 'iKey' );
			aMerged[i]	= cells[i].firstChild.nodeValue;
		}

		var _index		= 0;
		var _orderSheet	= 0;
		var _sPattern	= "";
		var _rows 		= "";
		var _cols 		= "";
		var _sHTML		= [];
		var _sheets		= parentElement.getElementsByTagName( 'sheet' );
		
		var dataSetId	= getElementAttribute( parentElement, 'ds', 'id' );
		var dataSetName	= getElementAttribute( parentElement, 'ds', 'n' );

		for (var s = 0 ; s < _sheets.length ; s ++)
		{
			_rows 		= _sheets[s].getElementsByTagName( 'row' );
			_orderSheet	= getRootElementAttribute( _sheets[s], "id" );

			_sHTML.push( "<table cellspacing='1'><tbody>" );

			for (var i = 0 ; i < _rows.length ; i ++)
			{
				_index	= 0;
				_sHTML.push( "<tr>" );

				_cols 	= _rows[i].getElementsByTagName( 'col' );

				for (var j 	= 0 ; j < _cols.length ; )
				{
					var _number	= getRootElementAttribute( _cols[j], 'no' );
					var keyId 	= getRootElementAttribute( _cols[j], 'id' );

					// Printing out the unformatted cells
					for (; _index < _number ; _index ++)
					{
						_sHTML.push( "<td>&nbsp;</td>" );
					}

					if ( _index == _number )
					{
						var _sData		= getElementValue( _cols[j], 'data' );
						var _align		= getElementAttribute( _cols[j], 'format', 'a' );
						var _border		= getElementAttribute( _cols[j], 'format', 'b' );
						var _width		= getElementAttribute( _cols[j], 'format', 'w' );
						var _size		= getElementAttribute( _cols[j], 'font', 's' );
						var _bold		= getElementAttribute( _cols[j], 'font', 'b' );
						var _italic		= getElementAttribute( _cols[j], 'font', 'i' );
						var _fcolor		= getElementAttribute( _cols[j], 'font', 'c' );
						var _bgcolor	= getElementAttribute( _cols[j], 'bg', 'c' );

						// If this cell is merged - Key's form: Sheet#Row#Col
						_sPattern 		=  _orderSheet + "#" + i + "#" + _number;
						var _colspan 	= getMergedNumberForEachCell( aKey, _sPattern, aMerged );

						// Jumping for <For Loop> AND <Empty Cells>
						j 		= Number(j) + Number(_colspan);
						_index 	= Number(_index) + Number(_colspan);
						_size	= Number(_size) + 2;

						// style for <td>
						_sHTML.push( "<td colspan='", _colspan, "'" );
						_sHTML.push( " style='text-align: ", _align, ";" );
						_sHTML.push( _bgcolor == "" ? "" : " background-color: " + _bgcolor + ";" );
						_sHTML.push( _width > 0 ? " width: " + _width + ";" : "" );
						_sHTML.push( "'>" );
						
						if ( _bold == "1" )
						{
							_sData = "<strong>" + _sData + "<strong>";
						}
						if ( _italic == "true" )
						{
							_sData = "<i>" + _sData + "</i>";
						}
						if ( _size > 0 )
						{
							_sData = "<span style='font-size: " + _size + "px;'>" + _sData + "</span>";
						}
						if ( _fcolor != "" )
						{
							_sData = "<span style='color:" + _fcolor + ";'>" + _sData + "</span>";
						}
						
						_sHTML.push( _sData, "</td>" );
					}
				}
				_sHTML.push( "</tr>" );
			}
			_sHTML.push( "</tbody></table>" );
		}

		//jQuery( '#previewDiv' ).html( _sHTML.join('') );
		//showById( "previewDiv" );
			
		unLockScreen();
		showSuccessMessage( i18n_auto_generate_form_completed );

		if ( _sHTML.length > 0 )
		{
			jQuery.postUTF8( '../dhis-web-maintenance-dataset/saveDataEntryForm.action',
			{
				dataSetIdField: dataSetId,
				nameField: dataSetName,
				style: 'regular',
				designTextarea: _sHTML.join('')
			} );
		}
	}
}

function getMergedNumberForEachCell( aKey, sKey, aMerged )
{
	for (var i = 0 ; i < aKey.length ; i ++)
	{
		if ( aKey[i] == sKey )
		{
			return Number(aMerged[i]);
		}
	}
	return 1;
}