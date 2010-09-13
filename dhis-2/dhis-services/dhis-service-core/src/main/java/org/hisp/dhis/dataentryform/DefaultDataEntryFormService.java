package org.hisp.dhis.dataentryform;

/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import static org.hisp.dhis.system.util.ExceptionUtils.throwException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author Bharath Kumar
 * @version $Id$
 */
@Transactional
public class DefaultDataEntryFormService
    implements DataEntryFormService
{
    private static final Pattern INPUT_PATTERN = Pattern.compile( "value\\[\\d+\\]\\.value:value\\[\\d+\\]\\.value" );
    private static final Pattern OPERAND_PATTERN = Pattern.compile( "\\d+" );
    
    // ------------------------------------------------------------------------
    // Dependencies
    // ------------------------------------------------------------------------

    private DataEntryFormStore dataEntryFormStore;

    public void setDataEntryFormStore( DataEntryFormStore dataEntryFormStore )
    {
        this.dataEntryFormStore = dataEntryFormStore;
    }
    
    // ------------------------------------------------------------------------
    // Implemented Methods
    // ------------------------------------------------------------------------

    public int addDataEntryForm( DataEntryForm dataEntryForm)
    {
        return dataEntryFormStore.addDataEntryForm( dataEntryForm );
    }

    public void updateDataEntryForm( DataEntryForm dataEntryForm )
    {
        dataEntryFormStore.updateDataEntryForm( dataEntryForm );
    }

    public void deleteDataEntryForm( DataEntryForm dataEntryForm )
    {
        dataEntryFormStore.deleteDataEntryForm( dataEntryForm );
    }

    public DataEntryForm getDataEntryForm( int id )
    {
        return dataEntryFormStore.getDataEntryForm( id );
    }

    public DataEntryForm getDataEntryFormByName( String name )
    {
        return dataEntryFormStore.getDataEntryFormByName( name );
    }
    
    public Collection<DataEntryForm> getAllDataEntryForms()
    {
        return dataEntryFormStore.getAllDataEntryForms();
    }

    public String prepareDataEntryFormCode( String preparedCode )
    {
        // ---------------------------------------------------------------------
        // Buffer to contain the final result.
        // ---------------------------------------------------------------------

        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match data elements in the HTML code.
        // ---------------------------------------------------------------------

        Pattern patDataElement = Pattern.compile( "(<input.*?)[/]?>" );
        Matcher matDataElement = patDataElement.matcher( preparedCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields.
        // ---------------------------------------------------------------------

        boolean result = matDataElement.find();

        while ( result )
        {
            // -----------------------------------------------------------------
            // Get input HTML code (HTML input field code).
            // -----------------------------------------------------------------

            String dataElementCode = matDataElement.group( 1 );

            // -----------------------------------------------------------------
            // Pattern to extract data element name from data element field
            // -----------------------------------------------------------------

            Pattern patDataElementName = Pattern.compile( "value=\"\\[ (.*) \\]\"" );
            Matcher matDataElementName = patDataElementName.matcher( dataElementCode );

            Pattern patTitle = Pattern.compile( "title=\"-- (.*) --\"" );
            Matcher matTitle = patTitle.matcher( dataElementCode );

            if ( matDataElementName.find() && matDataElementName.groupCount() > 0 )
            {
                String temp = "[ " + matDataElementName.group( 1 ) + " ]";
                dataElementCode = dataElementCode.replace( temp, "" );

                if ( matTitle.find() && matTitle.groupCount() > 0 )
                {
                    temp = "-- " + matTitle.group( 1 ) + " --";
                    dataElementCode = dataElementCode.replace( temp, "" );
                }

                // -------------------------------------------------------------
                // Appends dataElementCode
                // -------------------------------------------------------------

                String appendCode = dataElementCode;
                appendCode += "/>";
                matDataElement.appendReplacement( sb, appendCode );
            }

            // -----------------------------------------------------------------
            // Go to next data entry field
            // -----------------------------------------------------------------

            result = matDataElement.find();
        }

        // -----------------------------------------------------------------
        // Add remaining code (after the last match), and return formatted code.
        // -----------------------------------------------------------------

        matDataElement.appendTail( sb );

        return sb.toString();
    }
    
    public String convertDataEntryForm( String htmlCode, Map<Object, Integer> dataElementMap, Map<Object, Integer> categoryOptionComboMap )
    {
        Matcher inputMatcher = INPUT_PATTERN.matcher( htmlCode );        
        StringBuffer buffer = new StringBuffer();
        
        while ( inputMatcher.find() )
        {
            String input = inputMatcher.group();
            Matcher operandMatcher = OPERAND_PATTERN.matcher( input );
            
            operandMatcher.find();            
            String d = operandMatcher.group();
            throwException( d == null, "Could not find data element identifier in form" );
            Integer dataElement = dataElementMap.get( Integer.parseInt( d ) );
            throwException( dataElement == null, "Data element identifier does not exist: " + d );
            
            operandMatcher.find();
            String c = operandMatcher.group();
            throwException( c == null, "Could not find category option combo identifier in form" );
            Integer categoryOptionCombo = categoryOptionComboMap.get( Integer.parseInt( c ) );
            throwException( categoryOptionCombo == null, "Category option combo identifier does not exist: " + c );
            
            inputMatcher.appendReplacement( buffer, "value[" + dataElement + "].value:value[" + categoryOptionCombo + "].value" );
        }
        
        inputMatcher.appendTail( buffer );
        
        return buffer.toString();
    }

    public Collection<DataEntryForm> listDisctinctDataEntryFormByProgramStageIds( List<Integer> programStageIds )
    {
        if ( programStageIds == null || programStageIds.size() == 0 )
        {
            return null;
        }

        return dataEntryFormStore.listDisctinctDataEntryFormByProgramStageIds( programStageIds );
    }

    public Collection<DataEntryForm> listDisctinctDataEntryFormByDataSetIds( List<Integer> dataSetIds )
    {
        if ( dataSetIds == null || dataSetIds.size() == 0 )
        {
            return null;
        }

        return dataEntryFormStore.listDisctinctDataEntryFormByDataSetIds( dataSetIds );
    }
}
