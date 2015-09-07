package org.hisp.dhis.importexport.dhis14.xml.converter;

/*
 * Copyright (c) 2004-2015, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementDomain;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.analysis.ImportAnalyser;
import org.hisp.dhis.importexport.dhis14.util.Dhis14DateUtil;
import org.hisp.dhis.importexport.dhis14.util.Dhis14ParsingUtils;
import org.hisp.dhis.importexport.importer.DataElementImporter;
import org.hisp.dhis.importexport.mapping.NameMappingUtil;

import java.util.Collection;
import java.util.Map;

import static org.hisp.dhis.dataelement.DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME;
import static org.hisp.dhis.importexport.dhis14.util.Dhis14TypeHandler.*;

/**
 * @author Lars Helge Overland
 * @version $Id: DataElementConverter.java 6455 2008-11-24 08:59:37Z larshelg $
 */
public class DataElementConverter
    extends DataElementImporter
    implements XMLConverter
{
    public static final String ELEMENT_NAME = "DataElement";

    private static final String FIELD_ID = "DataElementID";

    private static final String FIELD_UID = "DataElementCode";

    private static final String FIELD_NAME = "DataElementName";

    private static final String FIELD_SHORT_NAME = "DataElementShort";

    private static final String FIELD_DOS = "DataElementDOS";

    private static final String FIELD_META = "MetaDataElement";

    //private static final String FIELD_DATA_TYPE = "DataTypeID";

    private static final String FIELD_PERIOD_TYPE = "DataPeriodTypeID";

    private static final String FIELD_VALID_FROM = "ValidFrom";

    private static final String FIELD_VALID_TO = "ValidTo";

    private static final String FIELD_DESCRIPTION = "DataElementDescription";

    private static final String FIELD_DESCRIPTIONEXTENDED = "DataElementDescriptionExtended";

    private static final String FIELD_COMMENT = "Comment";

    private static final String FIELD_DATAELEMENTINCLUSIONS = "DataElementInclusions";

    private static final String FIELD_DATAELEMENTEXCLUSIONS = "DataElementExclusions";

    private static final String FIELD_CALCULATED = "Calculated";

    private static final String FIELD_SAVE_CALCULATED = "SaveCalculated";

    private static final String FIELD_AGGREGATION_START_LEVEL = "AggregateStartLevel";

    private static final String FIELD_AGGREGATION_OPERATOR = "AggregateOperator";

    private static final String FIELD_SELECTED = "Selected";

    private static final String FIELD_LAST_USER = "LastUserID";

    private static final String FIELD_LAST_UPDATED = "LastUpdated";

    // added
    private static final String FIELD_CALCULATED_VALID_FROM = "CalculatedValidFrom";

    private static final String FIELD_CALCULATED_VALID_TO = "CalculatedValidTo";

    private static final String FIELD_CALCULATEONLYATLEVEL = "CalculateOnlyAtLevel";

    private static final String FIELD_EDITINGPERIOD = "EditingPeriod";

    private static final String FIELD_ZEROVALUEHANDLING = "ZeroValueHandling";

    private static final String FIELD_PASSWORDENCRYPTED = "PasswordEncrypted";

    private static final String FIELD_AUTHORITYID = "AuthorityID";

    private static final String FIELD_COLLECTEDBY = "CollectedBy";

    private static final String FIELD_COLLECTIONPOINT = "CollectionPoint";

    private static final String FIELD_COLLECTIONTOOL = "CollectionTool";

    private static final String FIELD_INCLUDEEXCELOVERVIEW = "IncludeExcelOverview";

    private static final String FIELD_DATAELEMENTNAMEALT1 = "DataElementNameAlt1";

    private static final String FIELD_DATAELEMENTNAMESHORTALT1 = "DataElementShortAlt1";

    private static final String FIELD_DATAELEMENTNAMEALT2 = "DataElementNameAlt2";

    private static final String FIELD_DATAELEMENTNAMESHORTALT2 = "DataElementShortAlt2";

    private static final String FIELD_DATAELEMENTNAMEALT3 = "DataElementNameAlt3";

    private static final String FIELD_DATAELEMENTNAMESHORTALT3 = "DataElementShortAlt3";

    // added
    private static final String FIELD_DATALEMENTPROMPT = "DataElementPrompt";

    private static final int VALID_FROM = 34335;

    private static final int VALID_TO = 2958465;

    private static final int AGG_START_LEVEL = 5;

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private DataElementCategoryService categoryService;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public DataElementConverter( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    /**
     * Constructor for read operations.
     *
     * @param importObjectService the importObjectService to use.
     * @param dataElementService  the dataElementService to use.
     */
    public DataElementConverter( ImportObjectService importObjectService, DataElementService dataElementService,
        DataElementCategoryService categoryService, ImportAnalyser importAnalyser )
    {
        this.importObjectService = importObjectService;
        this.dataElementService = dataElementService;
        this.categoryService = categoryService;
        this.importAnalyser = importAnalyser;
    }

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    @Override
    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<DataElement> elements = dataElementService.getDataElements( params.getDataElements() );

        if ( elements != null && elements.size() > 0 )
        {
            for ( DataElement object : elements )
            {
                String name = object.getName();
                String shortName = object.getShortName();

                name = name.replaceAll( "ï", "i" );
                shortName = shortName.replaceAll( "ï", "i" );

                writer.openElement( ELEMENT_NAME );

                writer.writeElement( FIELD_ID, String.valueOf( object.getId() ) );
                writer.writeElement( FIELD_UID, object.getUid() );
                writer.writeElement( FIELD_NAME, name );
                writer.writeElement( FIELD_SHORT_NAME, shortName );
                writer.writeElement( FIELD_DOS, object.getShortName().replaceAll( "[^a-zA-Z0-9]", "" ) );
                writer.writeElement( FIELD_DATALEMENTPROMPT, "" );
                writer.writeElement( FIELD_META, String.valueOf( 0 ) );
                // writer.writeElement( FIELD_DATA_TYPE, convertTypeToDhis14( object.getType() ) );
                writer.writeElement( FIELD_PERIOD_TYPE, String.valueOf( 1 ) );
                writer.writeElement( FIELD_VALID_FROM, String.valueOf( VALID_FROM ) );
                writer.writeElement( FIELD_VALID_TO, String.valueOf( VALID_TO ) );
                writer.writeElement( FIELD_DESCRIPTION, object.getDescription() );
                writer.writeElement( FIELD_DESCRIPTIONEXTENDED, "" );
                writer.writeElement( FIELD_COMMENT, "" );
                writer.writeElement( FIELD_DATAELEMENTINCLUSIONS, "" );
                writer.writeElement( FIELD_DATAELEMENTEXCLUSIONS, "" );
                writer.writeElement( FIELD_CALCULATED, convertBooleanToDhis14( false ) );
                writer.writeElement( FIELD_CALCULATED_VALID_FROM, "" );
                writer.writeElement( FIELD_CALCULATED_VALID_TO, "" );
                writer.writeElement( FIELD_SAVE_CALCULATED, convertBooleanToDhis14( false ) );
                writer.writeElement( FIELD_AGGREGATION_START_LEVEL, String.valueOf( AGG_START_LEVEL ) );
                writer.writeElement( FIELD_AGGREGATION_OPERATOR,
                    convertAggregationOperatorToDhis14( object.getAggregationOperator() ) );
                writer.writeElement( FIELD_SELECTED, String.valueOf( 0 ) );
                writer.writeElement( FIELD_LAST_USER, String.valueOf( 1 ) );
                writer.writeElement( FIELD_LAST_UPDATED, Dhis14DateUtil.getDateString( object.getLastUpdated() ) );
                writer.writeElement( FIELD_CALCULATEONLYATLEVEL, "" );
                writer.writeElement( FIELD_EDITINGPERIOD, "" );
                writer.writeElement( FIELD_ZEROVALUEHANDLING, "" );
                writer.writeElement( FIELD_PASSWORDENCRYPTED, "" );
                writer.writeElement( FIELD_AUTHORITYID, "" );
                writer.writeElement( FIELD_COLLECTEDBY, "" );
                writer.writeElement( FIELD_COLLECTIONPOINT, "" );
                writer.writeElement( FIELD_COLLECTIONTOOL, "" );
                writer.writeElement( FIELD_INCLUDEEXCELOVERVIEW, "" );
                writer.writeElement( FIELD_DATAELEMENTNAMEALT1, "" );
                writer.writeElement( FIELD_DATAELEMENTNAMESHORTALT1, "" );
                writer.writeElement( FIELD_DATAELEMENTNAMEALT2, "" );
                writer.writeElement( FIELD_DATAELEMENTNAMESHORTALT2, "" );
                writer.writeElement( FIELD_DATAELEMENTNAMEALT3, "" );
                writer.writeElement( FIELD_DATAELEMENTNAMESHORTALT3, "" );

                writer.closeElement();

                NameMappingUtil.addDataElementAggregationOperatorMapping( object.getId(),
                    object.getAggregationOperator() );
            }
        }
    }

    @Override
    public void read( XMLReader reader, ImportParams params )
    {
        Map<String, String> values = reader.readElements( ELEMENT_NAME );

        DataElementCategoryCombo categoryCombo = categoryService
            .getDataElementCategoryComboByName( DEFAULT_CATEGORY_COMBO_NAME );
        DataElementCategoryCombo proxyCategoryCombo = new DataElementCategoryCombo();
        proxyCategoryCombo.setId( categoryCombo.getId() );

        DataElement element = new DataElement();

        element.setCategoryCombo( proxyCategoryCombo );
        element.setId( Integer.valueOf( values.get( FIELD_ID ) ) );
        element.setName( values.get( FIELD_NAME ) );
        element.setShortName( values.get( FIELD_SHORT_NAME ) );
        element.setDescription( Dhis14ParsingUtils.removeNewLine( values.get( FIELD_DESCRIPTION ) ) );
        element.setDomainType( DataElementDomain.AGGREGATE );
        element.setZeroIsSignificant( false );
        // element.setType( Dhis14ObjectMappingUtil.getDataElementTypeMap().get(Integer.parseInt( values.get( FIELD_DATA_TYPE ) ) ) );
        element.setAggregationOperator( convertAggregationOperatorFromDhis14( values.get( FIELD_AGGREGATION_OPERATOR ) ) );
        element.setLastUpdated( Dhis14DateUtil.getDate( values.get( FIELD_LAST_UPDATED ) ) );

        if ( values.get( FIELD_CALCULATED ).equals( "0" ) ) // Ignore calculated
        // data elements
        {
            importObject( element, params );
        }
    }
}
