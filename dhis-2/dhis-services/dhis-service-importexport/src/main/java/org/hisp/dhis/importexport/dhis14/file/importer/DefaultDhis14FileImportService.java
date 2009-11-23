package org.hisp.dhis.importexport.dhis14.file.importer;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import static org.hisp.dhis.expression.Expression.SEPARATOR;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.cache.HibernateCacheManager;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datamart.DataMartService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.importexport.GroupMemberAssociation;
import org.hisp.dhis.importexport.ImportDataValue;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.ImportService;
import org.hisp.dhis.importexport.analysis.ImportAnalyser;
import org.hisp.dhis.importexport.dhis14.file.query.QueryManager;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.CalculatedDataElementRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.DataElementGroupMemberRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.DataElementGroupRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.DataElementRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.DataSetMemberRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.DataSetOrganisationUnitAssociationRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.DataSetRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.GroupSetMemberRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.GroupSetRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.IndicatorGroupMemberRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.IndicatorGroupRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.IndicatorRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.IndicatorTypeRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.OnChangePeriodRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.OrganisationUnitGroupMemberRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.OrganisationUnitGroupRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.OrganisationUnitRelationshipRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.OrganisationUnitRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.PeriodRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.RoutineDataValueRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.SemiPermanentDataValueRowHandler;
import org.hisp.dhis.importexport.dhis14.object.Dhis14CalculatedDataElementEntry;
import org.hisp.dhis.importexport.dhis14.util.Dhis14PeriodUtil;
import org.hisp.dhis.importexport.mapping.NameMappingUtil;
import org.hisp.dhis.importexport.mapping.ObjectMappingGenerator;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.jdbc.batchhandler.DataElementBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementGroupBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementGroupMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataSetBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataSetMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataSetSourceAssociationBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataValueBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.GroupSetBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.GroupSetMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.ImportDataValueBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorGroupBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorGroupMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorTypeBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.OrganisationUnitBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.OrganisationUnitGroupBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.OrganisationUnitGroupMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.PeriodBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.SourceBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.source.Source;
import org.hisp.dhis.system.util.AppendingHashMap;
import org.hisp.dhis.system.util.DateUtils;

import com.ibatis.sqlmap.client.event.RowHandler;

/**
 * @author Lars Helge Overland
 * @version $Id: DefaultDhis14FileImportService.java 6425 2008-11-22 00:08:57Z larshelg $
 */
public class DefaultDhis14FileImportService
    implements ImportService
{
    private final Log log = LogFactory.getLog( DefaultDhis14FileImportService.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private QueryManager queryManager;

    public void setQueryManager( QueryManager queryManager )
    {
        this.queryManager = queryManager;
    }

    private ObjectMappingGenerator objectMappingGenerator;

    public void setObjectMappingGenerator( ObjectMappingGenerator objectMappingGenerator )
    {
        this.objectMappingGenerator = objectMappingGenerator;
    }
    
    private BatchHandlerFactory batchHandlerFactory;

    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }
    
    private ImportObjectService importObjectService;

    public void setImportObjectService( ImportObjectService importObjectService )
    {
        this.importObjectService = importObjectService;
    }
    
    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService categoryService;
    
    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }
    
    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }
    
    private DataMartService dataMartService;
    
    public void setDataMartService( DataMartService dataMartService )
    {
        this.dataMartService = dataMartService;
    }

    private ImportAnalyser importAnalyser;

    public void setImportAnalyser( ImportAnalyser importAnalyser )
    {
        this.importAnalyser = importAnalyser;
    }    

    private HibernateCacheManager cacheManager;

    public void setCacheManager( HibernateCacheManager cacheManager )
    {
        this.cacheManager = cacheManager;
    }

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public DefaultDhis14FileImportService()
    {
        super();
    }
    
    // -------------------------------------------------------------------------
    // ImportService implementation
    // -------------------------------------------------------------------------

    public void importData( ImportParams params, InputStream inputStream )
    {
        NameMappingUtil.clearMapping();
        
        if ( params.isPreview() )
        {
            importObjectService.deleteImportObjects();
        }

        importDataElements( params );
        importCalculatedDataElements( params, createCalculatedDataElementEntryMap() );
        importIndicatorTypes( params );
        importIndicators( params );
        importDataElementGroups( params );
        importDataElementGroupMembers( params );
        importIndicatorGroups( params );
        importIndicatorGroupMembers( params );
        
        importDataSets( params );
        importDataSetMembers( params );

        importOrganisationUnits( params );            
        importOrganisationUnitGroups( params );
        importOrganisationUnitGroupMembers( params );
        importGroupSets( params );
        importGroupSetMembers( params );
        importOrganisationUnitRelationships( params );
        importOrganisationUnitHierarchy();

        importDataSetOrganisationUnitAssociations( params );
        
        if ( params.isDataValues() && !params.isAnalysis() )
        {
            importPeriods( params );
            importRoutineDataValues( params );

            importOnChangePeriods( params );
            importSemiPermanentDataValues( params );
        }
        
        if ( params.isAnalysis() )
        {
            //setOutput( importAnalyser.getImportAnalysis() ); //TODO fix
        }
        
        //setMessage( "import_process_done" );
        
        NameMappingUtil.clearMapping();
        Dhis14PeriodUtil.clear();
        
        cacheManager.clearCache();
    }
    
    // -------------------------------------------------------------------------
    // DataElement and Indicator
    // -------------------------------------------------------------------------

    private void importDataElements( ImportParams params )
    {
        //setMessage( "importing_data_elements" );
        
        BatchHandler<DataElement> batchHandler = batchHandlerFactory.createBatchHandler( DataElementBatchHandler.class );
        
        DataElementCategoryCombo categoryCombo = categoryService.
            getDataElementCategoryComboByName( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );
        
        RowHandler rowHandler = new DataElementRowHandler( batchHandler,
            importObjectService,
            dataElementService, 
            params,
            categoryCombo,
            importAnalyser );

        batchHandler.init();
        
        queryManager.queryWithRowhandler( "getDataElements", rowHandler );

        batchHandler.flush();
        
        log.info( "Imported DataElements" );
    }

    private void importCalculatedDataElements( ImportParams params, Map<Integer, String> calculatedEntryMap )
    {
        //setMessage( "importing_data_elements" );
        
        DataElementCategoryCombo categoryCombo = categoryService.
            getDataElementCategoryComboByName( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );
        
        RowHandler rowHandler = new CalculatedDataElementRowHandler( importObjectService,
            dataElementService, 
            params,
            categoryCombo,
            importAnalyser,
            expressionService,
            calculatedEntryMap,
            objectMappingGenerator.getDataElementMapping( params.skipMapping() ),
            getCategoryOptionComboMapping() );

        queryManager.queryWithRowhandler( "getCalculatedDataElements", rowHandler );

        log.info( "Imported CalculatedDataElements" );
    }
    
    private void importIndicatorTypes( ImportParams params )
    {
        //setMessage( "importing_indicator_types" );
        
        BatchHandler<IndicatorType> batchHandler = batchHandlerFactory.createBatchHandler( IndicatorTypeBatchHandler.class );
        
        RowHandler rowHandler = new IndicatorTypeRowHandler( batchHandler,
            importObjectService,
            indicatorService,
            params );
        
        batchHandler.init();
        
        queryManager.queryWithRowhandler( "getIndicatorTypes", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported IndicatorTypes" );
    }
    
    private void importIndicators( ImportParams params )
    {
        //setMessage( "importing_indicators" );
        
        BatchHandler<Indicator> indicatorBatchHandler = batchHandlerFactory.createBatchHandler( IndicatorBatchHandler.class );
        BatchHandler<DataElement> dataElementBatchHandler = batchHandlerFactory.createBatchHandler( DataElementBatchHandler.class );
        BatchHandler<IndicatorType> indicatorTypeBatchHandler = batchHandlerFactory.createBatchHandler( IndicatorTypeBatchHandler.class );
        
        RowHandler rowHandler = new IndicatorRowHandler( indicatorBatchHandler,
            importObjectService,
            indicatorService,
            objectMappingGenerator.getIndicatorTypeMapping( params.skipMapping() ), 
            objectMappingGenerator.getDataElementMapping( params.skipMapping() ),
            categoryService.getDefaultDataElementCategoryOptionCombo(),
            params,
            importAnalyser );
        
        indicatorBatchHandler.init();
        dataElementBatchHandler.init();
        indicatorTypeBatchHandler.init();
        
        queryManager.queryWithRowhandler( "getIndicators", rowHandler );
        
        indicatorBatchHandler.flush();
        dataElementBatchHandler.flush();
        indicatorTypeBatchHandler.flush();
        
        log.info( "Imported Indicators" );
    }
    
    private void importDataElementGroups( ImportParams params )
    {
        //setMessage( "importing_data_element_groups" );
        
        BatchHandler<DataElementGroup> batchHandler = batchHandlerFactory.createBatchHandler( DataElementGroupBatchHandler.class );
        
        RowHandler rowHandler = new DataElementGroupRowHandler( batchHandler,
            importObjectService,
            dataElementService, 
            params );
        
        batchHandler.init();        
        
        queryManager.queryWithRowhandler( "getDataElementGroups", rowHandler );
        
        batchHandler.flush();        
        
        log.info( "Imported DataElementGroups" );
    }
    
    private void importIndicatorGroups( ImportParams params )
    {
        //setMessage( "importing_indicator_groups" );
        
        BatchHandler<IndicatorGroup> batchHandler = batchHandlerFactory.createBatchHandler( IndicatorGroupBatchHandler.class );
        
        RowHandler rowHandler = new IndicatorGroupRowHandler( batchHandler,
            importObjectService,
            indicatorService, 
            params );
        
        batchHandler.init();
        
        queryManager.queryWithRowhandler( "getIndicatorGroups", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported IndicatorGroups" );
    }
    
    private void importDataElementGroupMembers( ImportParams params )
    {
        //setMessage( "importing_data_element_group_members" );
        
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( DataElementGroupMemberBatchHandler.class );
        
        RowHandler rowHandler = new DataElementGroupMemberRowHandler( batchHandler,
            importObjectService,
            objectMappingGenerator.getDataElementMapping( params.skipMapping() ),
            objectMappingGenerator.getDataElementGroupMapping( params.skipMapping() ),
            params );
        
        batchHandler.init();
        
        queryManager.queryWithRowhandler( "getDataElementGroupMembers", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported DataElementGroup members" );
    }

    private void importIndicatorGroupMembers( ImportParams params )
    {
        //setMessage( "importing_indicator_group_members" );
        
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( IndicatorGroupMemberBatchHandler.class );
        
        RowHandler rowHandler = new IndicatorGroupMemberRowHandler( batchHandler,
            importObjectService,
            objectMappingGenerator.getIndicatorMapping( params.skipMapping() ),
            objectMappingGenerator.getIndicatorGroupMapping( params.skipMapping() ),
            params );
        
        batchHandler.init();
        
        queryManager.queryWithRowhandler( "getIndicatorGroupMembers", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported IndicatorGroup members" );
    }
    
    // -------------------------------------------------------------------------
    // DataSet
    // -------------------------------------------------------------------------

    private void importDataSets( ImportParams params )
    {
        //setMessage( "importing_data_sets" );
        
        BatchHandler<DataSet> batchHandler = batchHandlerFactory.createBatchHandler( DataSetBatchHandler.class );
        
        RowHandler rowHandler = new DataSetRowHandler( batchHandler,
            importObjectService,
            dataSetService,
            objectMappingGenerator.getPeriodTypeMapping(),
            params,
            importAnalyser );
        
        batchHandler.init();
        
        queryManager.queryWithRowhandler( "getDataSets", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported DataSets" );
    }
    
    private void importDataSetMembers( ImportParams params )
    {
        //setMessage( "importing_data_set_members" );
        
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( DataSetMemberBatchHandler.class );
        
        RowHandler rowHandler = new DataSetMemberRowHandler( batchHandler,
            importObjectService,
            objectMappingGenerator.getDataElementMapping( params.skipMapping() ), 
            objectMappingGenerator.getDataSetMapping( params.skipMapping() ),
            params );
        
        batchHandler.init();
        
        queryManager.queryWithRowhandler( "getDataSetMembers", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported DataSet members" );
    }

    // -------------------------------------------------------------------------
    // OrganisatonUnit
    // -------------------------------------------------------------------------

    private void importOrganisationUnits( ImportParams params )
    {
        //setMessage( "importing_organisation_units" );
        
        BatchHandler<Source> sourceBatchHandler = batchHandlerFactory.createBatchHandler( SourceBatchHandler.class );
        BatchHandler<OrganisationUnit> organisationUnitBatchHandler = batchHandlerFactory.createBatchHandler( OrganisationUnitBatchHandler.class );
        
        RowHandler rowHandler = new OrganisationUnitRowHandler( organisationUnitBatchHandler, 
            sourceBatchHandler,
            importObjectService,
            organisationUnitService,
            params,
            importAnalyser );
        
        sourceBatchHandler.init();
        organisationUnitBatchHandler.init();
        
        queryManager.queryWithRowhandler( "getOrganisationUnits", rowHandler );
        
        sourceBatchHandler.flush();
        organisationUnitBatchHandler.flush();
        
        log.info( "Imported OrganisationUnits" );       
    }
    
    private void importOrganisationUnitGroups( ImportParams params )
    {
        //setMessage( "importing_organisation_unit_groups" );
        
        BatchHandler<OrganisationUnitGroup> batchHandler = batchHandlerFactory.createBatchHandler( OrganisationUnitGroupBatchHandler.class );
        
        RowHandler rowHandler = new OrganisationUnitGroupRowHandler( batchHandler,
            importObjectService,
            organisationUnitGroupService,
            params );
        
        batchHandler.init();
        
        queryManager.queryWithRowhandler( "getOrganisationUnitGroups", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported OrganisationUnitGroups" );
    }
    
    private void importOrganisationUnitGroupMembers( ImportParams params )
    {
        //setMessage( "importing_organisation_unit_group_members" );
        
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( OrganisationUnitGroupMemberBatchHandler.class );
        
        RowHandler rowHandler = new OrganisationUnitGroupMemberRowHandler( batchHandler,
            importObjectService,
            objectMappingGenerator.getOrganisationUnitMapping( params.skipMapping() ),
            objectMappingGenerator.getOrganisationUnitGroupMapping( params.skipMapping() ),
            params );
        
        batchHandler.init();
        
        queryManager.queryWithRowhandler( "getOrganisationUnitGroupMembers", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported OrganisationUnitGroup members" );
    }
    
    private void importGroupSets( ImportParams params )
    {
        //setMessage( "importing_organisation_unit_group_sets" );
        
        BatchHandler<OrganisationUnitGroupSet> batchHandler = batchHandlerFactory.createBatchHandler( GroupSetBatchHandler.class );
        
        RowHandler rowHandler = new GroupSetRowHandler( batchHandler, 
            importObjectService,
            organisationUnitGroupService,
            params );
        
        batchHandler.init();
        
        queryManager.queryWithRowhandler( "getOrganisationUnitGroupSets", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported OrganisationUnitGroupSets" );  
    }
    
    private void importGroupSetMembers( ImportParams params )
    {
        //setMessage( "importing_organisation_unit_group_set_members" );
        
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( GroupSetMemberBatchHandler.class );
        
        RowHandler rowHandler = new GroupSetMemberRowHandler( batchHandler,
            importObjectService,
            objectMappingGenerator.getOrganisationUnitGroupMapping( params.skipMapping() ),
            objectMappingGenerator.getOrganisationUnitGroupSetMapping( params.skipMapping() ),
            params );
        
        batchHandler.init();
        
        queryManager.queryWithRowhandler( "getOrganisationUnitGroupSetMembers", rowHandler );
        
        batchHandler.flush();
                
        log.info( "Imported OrganisationUnitGroupSet members" );
    }

    private void importOrganisationUnitRelationships( ImportParams params )
    {
        //setMessage( "importing_organisation_unit_relationships" );
        
        BatchHandler<OrganisationUnit> batchHandler = batchHandlerFactory.createBatchHandler( OrganisationUnitBatchHandler.class );
        
        RowHandler rowHandler = new OrganisationUnitRelationshipRowHandler( batchHandler,
            importObjectService,
            organisationUnitService,
            objectMappingGenerator.getOrganisationUnitMapping( params.skipMapping() ),
            params );
        
        batchHandler.init();
        
        queryManager.queryWithRowhandler( "getOrganisationUnitRelationships", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported OrganisationUnitRelationships" );
    }
    
    private void importOrganisationUnitHierarchy()
    {
        //setMessage( "importing_organisation_unit_hierarchy" );
        
        organisationUnitService.addOrganisationUnitHierarchy( DateUtils.getEpoch() );
        
        log.info( "Imported OrganisationUnitHierarchy" );
    }

    // -------------------------------------------------------------------------
    // DataSet - OrganisationUnit Associations
    // -------------------------------------------------------------------------

    private void importDataSetOrganisationUnitAssociations( ImportParams params )
    {
        //setMessage( "importing_data_set_organisation_unit_associations" );
        
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( DataSetSourceAssociationBatchHandler.class );
        
        RowHandler rowHandler = new DataSetOrganisationUnitAssociationRowHandler( batchHandler,
            importObjectService,
            objectMappingGenerator.getDataSetMapping( params.skipMapping() ), 
            objectMappingGenerator.getOrganisationUnitMapping( params.skipMapping() ),
            params );
        
        batchHandler.init();
        
        queryManager.queryWithRowhandler( "getDataSetOrganisationUnitAssociations", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported DataSet OrganisationUnit Associations" );
    }

    // -------------------------------------------------------------------------
    // Period
    // -------------------------------------------------------------------------

    private void importPeriods( ImportParams params )
    {   
        //setMessage( "importing_periods" );
        
        BatchHandler<Period> batchHandler = batchHandlerFactory.createBatchHandler( PeriodBatchHandler.class );
        
        RowHandler rowHandler = new PeriodRowHandler( batchHandler, 
            importObjectService,
            periodService,
            objectMappingGenerator.getPeriodTypeMapping(),
            params,
            getPeriodWithDataIdentifiers() );
        
        batchHandler.init();
        
        queryManager.queryWithRowhandler( "getPeriods", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported Periods" );
    }
    
    // -------------------------------------------------------------------------
    // RoutineDataValue
    // -------------------------------------------------------------------------

    private void importRoutineDataValues( ImportParams params )
    {
        //setMessage( "importing_routine_data_values" );

        DataElementCategoryOptionCombo categoryOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        
        BatchHandler<DataValue> batchHandler = batchHandlerFactory.createBatchHandler( DataValueBatchHandler.class );
        
        BatchHandler<ImportDataValue> importDataValueBatchHandler = batchHandlerFactory.createBatchHandler( ImportDataValueBatchHandler.class );
                
        RowHandler rowHandler = new RoutineDataValueRowHandler( batchHandler,
            importDataValueBatchHandler,
            dataValueService,
            dataMartService,
            objectMappingGenerator.getDataElementMapping( params.skipMapping() ),
            objectMappingGenerator.getPeriodMapping( params.skipMapping() ),
            objectMappingGenerator.getOrganisationUnitMapping( params.skipMapping() ),
            categoryOptionCombo,
            params );
        
        batchHandler.init();
        
        importDataValueBatchHandler.init();
        
        if ( params.getLastUpdated() == null )
        {
            queryManager.queryWithRowhandler( "getRoutineDataValues", rowHandler );
        }
        else
        {            
            queryManager.queryWithRowhandler( "getRoutineDataValuesLastUpdated", rowHandler, params.getLastUpdated() );
        }
        
        batchHandler.flush();
        
        importDataValueBatchHandler.flush();
        
        log.info( "Imported RoutineDataValues" );
    }

    // -------------------------------------------------------------------------
    // OnChangePeriod
    // -------------------------------------------------------------------------

    private void importOnChangePeriods( ImportParams params )
    {
        //setMessage( "importing_on_change_periods" );
        
        BatchHandler<Period> batchHandler = batchHandlerFactory.createBatchHandler( PeriodBatchHandler.class );
        
        RowHandler rowHandler = new OnChangePeriodRowHandler( batchHandler,
            importObjectService,
            periodService,
            objectMappingGenerator.getPeriodTypeMapping(),
            params );
        
        batchHandler.init();
        
        queryManager.queryWithRowhandler( "getOnChangePeriods", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported OnChangePeriods" );
    }

    // -------------------------------------------------------------------------
    // SemiPermanentDataValue
    // -------------------------------------------------------------------------

    private void importSemiPermanentDataValues( ImportParams params )
    {
        //setMessage( "importing_semi_permanent_data_values" );

        DataElementCategoryOptionCombo categoryOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        
        BatchHandler<DataValue> batchHandler = batchHandlerFactory.createBatchHandler( DataValueBatchHandler.class );

        BatchHandler<ImportDataValue> importDataValueBatchHandler = batchHandlerFactory.createBatchHandler( ImportDataValueBatchHandler.class );
        
        RowHandler rowHandler = new SemiPermanentDataValueRowHandler( batchHandler,
            importDataValueBatchHandler,
            dataValueService,
            dataMartService,
            objectMappingGenerator.getDataElementMapping( params.skipMapping() ),
            objectMappingGenerator.getPeriodObjectMapping( params.skipMapping() ),
            objectMappingGenerator.getOrganisationUnitMapping( params.skipMapping() ),
            categoryOptionCombo,
            params );
        
        batchHandler.init();
        
        importDataValueBatchHandler.init();
        
        if ( params.getLastUpdated() == null )
        {
            queryManager.queryWithRowhandler( "getSemiPermanentDataValues", rowHandler );
        }
        else
        {
            queryManager.queryWithRowhandler( "getSemiPermanentDataValuesLastUpdated", rowHandler, params.getLastUpdated() );
        }
        
        batchHandler.flush();
        
        importDataValueBatchHandler.flush();
        
        log.info( "Imported SemiPermanentDataValues" );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Creates a map where key is the calculated data element identifier and the
     * value if the formula.
     */
    private Map<Integer, String> createCalculatedDataElementEntryMap()
    {
        int categoryOptionComboId = categoryService.getDefaultDataElementCategoryOptionCombo().getId();
        
        List<?> calculatedDataElements = queryManager.queryForList( "getCalculatedDataElementEntries", null );
        
        Map<Integer, String> map = new AppendingHashMap<Integer, String>(); // Calculated data element id, formula
        
        //TODO factor should be double
        
        for ( Object element : calculatedDataElements )
        {
            Dhis14CalculatedDataElementEntry calculated = (Dhis14CalculatedDataElementEntry) element;
            
            String formula = "([" + calculated.getDataElementId() + SEPARATOR + categoryOptionComboId + "]*" + calculated.getFactor() + ")";
            
            if ( map.containsKey( calculated.getCalculatedDataElementId() ) )
            {
                formula = "+" + formula;
            }
            
            map.put( calculated.getCalculatedDataElementId(), formula );
        }
        
        return map;
    }

    /**
     * Returns a list of distinct period identifiers from the RoutineDataValue table,
     * ie. periods which have registered data. Could be used to avoid importing 
     * periods without data.
     */
    private Set<Integer> getPeriodWithDataIdentifiers()
    {
        Set<Integer> identifiers = new HashSet<Integer>();

        List<?> list = queryManager.queryForList( "getDistinctPeriodIdentifiers", null );
        
        for ( Object id : list )
        {
            identifiers.add( (Integer) id );
        }
        
        return identifiers;
    }
    
    /**
     * Returns a mapping for category option combo. Since DHIS 1.4 does not have
     * this it will always be default.
     */
    private Map<Object, Integer> getCategoryOptionComboMapping()
    {
        Integer categoryOptionComboId = categoryService.getDefaultDataElementCategoryOptionCombo().getId();
        
        Map<Object, Integer> mapping = new HashMap<Object, Integer>();
        
        mapping.put( categoryOptionComboId, categoryOptionComboId );
        
        return mapping;
    }
}
