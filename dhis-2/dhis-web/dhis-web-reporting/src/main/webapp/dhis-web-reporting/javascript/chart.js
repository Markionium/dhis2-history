// -----------------------------------------------------------------------------
// View chart
// -----------------------------------------------------------------------------

function viewChart( url, size )
{
    var width = size === 'wide' ? 1000 : 700;
    var height = size === 'tall' ? 800 : 500;

    $( '#chartImage' ).attr( 'src', url );
    $( '#chartView' ).dialog( {
        autoOpen : true,
        modal : true,
        height : height + 65,
        width : width + 45,
        resizable : false,
        title : 'Viewing Chart'
    } );
}

// -----------------------------------------------------------------------------
// Remove chart
// -----------------------------------------------------------------------------

function removeChart( chartId, chartTitle )
{
    removeItem( chartId, chartTitle, i18n_confirm_delete, "removeChart.action" );
}

// -----------------------------------------------------------------------------
// Show chart details
// -----------------------------------------------------------------------------

function showChartDetails( chartId )
{
    jQuery.post( 'getChart.action', {
        id : chartId
    }, function( json )
    {
        var indicators = parseInt( json.chart.indicators );
        var dataElements = parseInt( json.chart.dataElements );
        var dataSets = parseInt( json.chart.dataSets );

        setInnerHTML( 'titleField', json.chart.title );
        setInnerHTML( 'dimensionField', json.chart.dimension );

        if ( isIndicatorChart( json.chart.dimension ) )
        {
            $( '#dataElementsView' ).hide();
            $( '#dataSetsView' ).hide();
            $( '#indicatorsView' ).show();

            $( '#indicatorsField' ).text( indicators );
        }
        else if ( isDataElementChart( json.chart.dimension ) )
        {
            $( '#indicatorsView' ).hide();
            $( '#dataSetsView' ).hide();
            $( '#dataElementsView' ).show();

            $( '#dataElementsField' ).text( dataElements );
        }
        else if ( isCompletenessChart( json.chart.dimension ) )
        {
            $( '#indicatorsView' ).hide();
            $( '#dataElementsView' ).hide();
            $( '#dataSetsView' ).show();

            $( '#dataSetsField' ).text( dataSets );
        }

        setInnerHTML( 'periodsField', json.chart.periods );
        setInnerHTML( 'organisationUnitsField', json.chart.organisationUnits );

        showDetails();
    } );
}

// -----------------------------------------------------------------------------
// Validate and save
// -----------------------------------------------------------------------------

function saveChart( dimension )
{
    if ( validateTargetLine() && validateCollections( dimension ) )
    {
        $.postJSON( "validateChart.action", {
            id : getFieldValue( "id" ),
            title : getFieldValue( "title" )
        }, function( json )
        {
            if ( json.response == "input" )
            {
                setMessage( json.message );
                return false;
            }
            else if ( json.response == "success" )
            {
                if ( $( "#selectedIndicators" ).attr( 'multiple' ) !== undefined )
                {
                    $( "#selectedIndicators" ).children().attr( "selected", true );
                }

                if ( $( "#selectedDataElements" ).attr( 'multiple' ) !== undefined )
                {
                    $( "#selectedDataElements" ).children().attr( "selected", true );
                }

                if ( $( "#selectedDataSets" ).attr( 'multiple' ) !== undefined )
                {
                    $( "#selectedDataSets" ).children().attr( "selected", true );
                }

                if ( $( "#selectedPeriods" ).attr( 'multiple' ) !== undefined )
                {
                    $( "#selectedPeriods" ).children().attr( "selected", true );
                }

                if ( $( "#selectedOrganisationUnits" ).attr( 'multiple' ) !== undefined )
                {
                    $( "#selectedOrganisationUnits" ).children().attr( "selected", true );
                }

                $( "#chartForm" ).submit();
            }
        } );
    }
}

function selectedChartType()
{
    return $( "#type" ).val();
}

function selectedIndicatorsCount()
{
    return $( "#selectedIndicators option" ).length;
}

function validateTargetLine()
{
    var targetLine = $( "#targetLine" ).attr( "checked" );

    if ( targetLine )
    {
        var targetLineValue = $( "#targetLineValue" ).val();

        if ( targetLineValue.length == 0 )
        {
            setMessage( i18n_target_line_value_must_be_provided );

            return false;
        }

        if ( isNaN( targetLineValue ) )
        {
            setMessage( i18n_target_line_value_must_be_number );

            return false;
        }
    }

    return true;
}

function isIndicatorChart( dimension )
{
    if ( dimension == "period" || dimension == "organisationUnit" || dimension == "indicator" )
    {
        return true;
    }

    return false;
}

function isDataElementChart( dimension )
{
    if ( dimension == "period_dataElement" || dimension == "organisationUnit_dataElement"
            || dimension == "dataElement_period" )
    {
        return true;
    }

    return false;
}

function isCompletenessChart( dimension )
{
    if ( dimension == "period_completeness" || dimension == "organisationUnit_completeness"
            || dimension == "completeness_period" )
    {
        return true;
    }

    return false;
}

function validateCollections( dimension )
{
    if ( isIndicatorChart( dimension ) && !hasElements( "selectedIndicators" ) )
    {
        setMessage( i18n_must_select_at_least_one_indicator );

        return false;
    }

    if ( isDataElementChart( dimension ) && !hasElements( "selectedDataElements" ) )
    {
        setMessage( i18n_must_select_at_least_one_dataelement );

        return false;
    }

    if ( isCompletenessChart( dimension ) && !hasElements( "selectedDataSets" ) )
    {
        setMessage( i18n_must_select_at_least_one_dataset );

        return false;
    }

    if ( !hasElements( "selectedOrganisationUnits" ) && !isChecked( "userOrganisationUnit" ) )
    {
        setMessage( i18n_must_select_at_least_one_unit );

        return false;
    }

    if ( !hasElements( "selectedPeriods" ) && !relativePeriodsChecked() )
    {
        setMessage( i18n_must_select_at_least_one_period );

        return false;
    }

    return true;
}
