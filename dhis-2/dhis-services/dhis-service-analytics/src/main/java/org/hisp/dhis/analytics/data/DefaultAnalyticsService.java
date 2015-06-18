package org.hisp.dhis.analytics.data;

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

import static org.hisp.dhis.analytics.AnalyticsTableManager.ANALYTICS_TABLE_NAME;
import static org.hisp.dhis.analytics.AnalyticsTableManager.COMPLETENESS_TABLE_NAME;
import static org.hisp.dhis.analytics.AnalyticsTableManager.COMPLETENESS_TARGET_TABLE_NAME;
import static org.hisp.dhis.analytics.AnalyticsTableManager.ORGUNIT_TARGET_TABLE_NAME;
import static org.hisp.dhis.analytics.DataQueryParams.COMPLETENESS_DIMENSION_TYPES;
import static org.hisp.dhis.analytics.DataQueryParams.DISPLAY_NAME_CATEGORYOPTIONCOMBO;
import static org.hisp.dhis.analytics.DataQueryParams.DISPLAY_NAME_DATA_X;
import static org.hisp.dhis.analytics.DataQueryParams.DISPLAY_NAME_LATITUDE;
import static org.hisp.dhis.analytics.DataQueryParams.DISPLAY_NAME_LONGITUDE;
import static org.hisp.dhis.analytics.DataQueryParams.DISPLAY_NAME_ORGUNIT;
import static org.hisp.dhis.analytics.DataQueryParams.DISPLAY_NAME_PERIOD;
import static org.hisp.dhis.analytics.DataQueryParams.DX_INDEX;
import static org.hisp.dhis.analytics.DataQueryParams.KEY_DE_GROUP;
import static org.hisp.dhis.common.DimensionalObject.CATEGORYOPTIONCOMBO_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.DATAELEMENT_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.DATASET_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.DATA_X_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.DIMENSION_SEP;
import static org.hisp.dhis.common.DimensionalObject.INDICATOR_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.LATITUDE_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.LONGITUDE_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.ORGUNIT_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.PERIOD_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.PROGRAM_ATTRIBUTE_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.PROGRAM_DATAELEMENT_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.PROGRAM_INDICATOR_DIM_ID;
import static org.hisp.dhis.common.DimensionalObjectUtils.toDimension;
import static org.hisp.dhis.common.IdentifiableObjectUtils.getLocalPeriodIdentifier;
import static org.hisp.dhis.common.IdentifiableObjectUtils.getLocalPeriodIdentifiers;
import static org.hisp.dhis.common.IdentifiableObjectUtils.getUids;
import static org.hisp.dhis.common.NameableObjectUtils.asList;
import static org.hisp.dhis.common.NameableObjectUtils.asTypedList;
import static org.hisp.dhis.organisationunit.OrganisationUnit.KEY_LEVEL;
import static org.hisp.dhis.organisationunit.OrganisationUnit.KEY_ORGUNIT_GROUP;
import static org.hisp.dhis.organisationunit.OrganisationUnit.KEY_USER_ORGUNIT;
import static org.hisp.dhis.organisationunit.OrganisationUnit.KEY_USER_ORGUNIT_CHILDREN;
import static org.hisp.dhis.organisationunit.OrganisationUnit.KEY_USER_ORGUNIT_GRANDCHILDREN;
import static org.hisp.dhis.organisationunit.OrganisationUnit.getParentGraphMap;
import static org.hisp.dhis.organisationunit.OrganisationUnit.getParentNameGraphMap;
import static org.hisp.dhis.period.PeriodType.getPeriodTypeFromIsoString;
import static org.hisp.dhis.reporttable.ReportTable.IRT2D;
import static org.hisp.dhis.reporttable.ReportTable.addIfEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.analytics.AggregationType;
import org.hisp.dhis.analytics.AnalyticsManager;
import org.hisp.dhis.analytics.AnalyticsSecurityManager;
import org.hisp.dhis.analytics.AnalyticsService;
import org.hisp.dhis.analytics.DataQueryGroups;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.analytics.DimensionItem;
import org.hisp.dhis.analytics.QueryPlanner;
import org.hisp.dhis.analytics.event.EventAnalyticsService;
import org.hisp.dhis.analytics.event.EventQueryParams;
import org.hisp.dhis.calendar.Calendar;
import org.hisp.dhis.calendar.DateTimeUnit;
import org.hisp.dhis.common.AnalyticalObject;
import org.hisp.dhis.common.BaseDimensionalObject;
import org.hisp.dhis.common.CodeGenerator;
import org.hisp.dhis.common.CombinationGenerator;
import org.hisp.dhis.common.DimensionType;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.DimensionalObjectUtils;
import org.hisp.dhis.common.DisplayProperty;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.common.IdentifiableObjectUtils;
import org.hisp.dhis.common.IdentifiableProperty;
import org.hisp.dhis.common.IllegalQueryException;
import org.hisp.dhis.common.MapMap;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.common.NameableObjectUtils;
import org.hisp.dhis.commons.collection.ListUtils;
import org.hisp.dhis.commons.collection.UniqueArrayList;
import org.hisp.dhis.commons.util.DebugUtils;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.CategoryOptionGroup;
import org.hisp.dhis.dataelement.CategoryOptionGroupSet;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementDomain;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementOperandService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.RelativePeriodEnum;
import org.hisp.dhis.period.RelativePeriods;
import org.hisp.dhis.period.comparator.AscendingPeriodEndDateComparator;
import org.hisp.dhis.program.ProgramIndicator;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.system.util.SystemUtils;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.util.Timer;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

/**
 * @author Lars Helge Overland
 */
public class DefaultAnalyticsService
    implements AnalyticsService
{
    private static final Log log = LogFactory.getLog( DefaultAnalyticsService.class );

    private static final String VALUE_HEADER_NAME = "Value";
    private static final int PERCENT = 100;
    private static final int MAX_QUERIES = 8;

    //TODO make sure data x dims are successive
    //TODO completeness on time

    @Autowired
    private AnalyticsManager analyticsManager;

    @Autowired
    private AnalyticsSecurityManager securityManager;

    @Autowired
    private QueryPlanner queryPlanner;

    @Autowired
    private IdentifiableObjectManager idObjectManager;
    
    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private ExpressionService expressionService;

    @Autowired
    private ConstantService constantService;

    @Autowired
    private DataElementOperandService operandService;

    @Autowired
    private SystemSettingManager systemSettingManager;

    @Autowired
    private EventAnalyticsService eventAnalyticsService;    
    
    @Autowired
    private ProgramService programService;
    
    @Autowired
    private ProgramStageService programStageService;
    
    @Autowired
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService; // Testing purposes
    }

    // -------------------------------------------------------------------------
    // Methods for retrieving aggregated data
    // -------------------------------------------------------------------------

    @Override
    public Grid getAggregatedDataValues( DataQueryParams params )
    {
        // ---------------------------------------------------------------------
        // Security and validation
        // ---------------------------------------------------------------------

        securityManager.decideAccess( params );

        securityManager.applyDataApprovalConstraints( params );

        securityManager.applyDimensionConstraints( params );

        queryPlanner.validate( params );

        params.conform();

        // ---------------------------------------------------------------------
        // Headers
        // ---------------------------------------------------------------------

        Grid grid = new ListGrid();

        addHeaders( params, grid );

        // ---------------------------------------------------------------------
        // Data
        // ---------------------------------------------------------------------

        addIndicatorValues( params, grid );

        addDataElementValues( params, grid );

        addDataSetValues( params, grid );
        
        addProgramValues( params, grid );

        addDynamicDimensionValues( params, grid );

        // ---------------------------------------------------------------------
        // Meta-data
        // ---------------------------------------------------------------------

        addMetaData( params, grid );
        
        applyIdScheme( params, grid  );

        return grid;
    }

    /**
     * Adds headers to the given grid based on the given data query parameters.
     */
    private void addHeaders( DataQueryParams params, Grid grid )
    {
        for ( DimensionalObject col : params.getHeaderDimensions() )
        {
            grid.addHeader( new GridHeader( col.getDimension(), col.getDisplayName(), String.class.getName(), false, true ) );
        }

        grid.addHeader( new GridHeader( DataQueryParams.VALUE_ID, VALUE_HEADER_NAME, Double.class.getName(), false, false ) );
    }
    
    /**
     * Adds indicator values to the given grid based on the given data query
     * parameters.
     *
     * @param params the data query parameters.
     * @param grid the grid.
     */
    private void addIndicatorValues( DataQueryParams params, Grid grid )
    {
        if ( !params.getIndicators().isEmpty() )
        {
            DataQueryParams dataSourceParams = params.instance();
            dataSourceParams.removeDimensions( DATAELEMENT_DIM_ID, DATASET_DIM_ID, PROGRAM_INDICATOR_DIM_ID, PROGRAM_DATAELEMENT_DIM_ID, PROGRAM_ATTRIBUTE_DIM_ID );

            List<Indicator> indicators = asTypedList( dataSourceParams.getIndicators() );

            Period filterPeriod = dataSourceParams.getFilterPeriod();

            Map<String, Double> constantMap = constantService.getConstantMap();

            // -----------------------------------------------------------------
            // Get indicator values
            // -----------------------------------------------------------------

            Map<String, Map<String, Integer>> permutationOrgUnitTargetMap = getOrgUnitTargetMap( dataSourceParams, indicators );

            List<List<DimensionItem>> dimensionItemPermutations = dataSourceParams.getDimensionItemPermutations();

            Map<String, Map<DataElementOperand, Double>> permutationOperandValueMap = getPermutationOperandValueMap( dataSourceParams );

            for ( Indicator indicator : indicators )
            {
                for ( List<DimensionItem> dimensionItems : dimensionItemPermutations )
                {
                    String permKey = DimensionItem.asItemKey( dimensionItems );

                    Map<DataElementOperand, Double> valueMap = permutationOperandValueMap.get( permKey );

                    if ( valueMap == null )
                    {
                        continue;
                    }

                    Period period = filterPeriod != null ? filterPeriod : (Period) DimensionItem.getPeriodItem( dimensionItems );

                    OrganisationUnit unit = (OrganisationUnit) DimensionItem.getOrganisationUnitItem( dimensionItems );

                    String ou = unit != null ? unit.getUid() : null;

                    Map<String, Integer> orgUnitCountMap = permutationOrgUnitTargetMap != null ? permutationOrgUnitTargetMap.get( ou ) : null;

                    Double value = expressionService.getIndicatorValue( indicator, period, valueMap, constantMap, orgUnitCountMap );

                    if ( value != null )
                    {
                        List<DimensionItem> row = new ArrayList<>( dimensionItems );

                        row.add( DX_INDEX, new DimensionItem( INDICATOR_DIM_ID, indicator ) );

                        Double roundedValue = indicator.hasDecimals() ? MathUtils.getRounded( value, indicator.getDecimals() ) : MathUtils.getRounded( value );
                        
                        grid.addRow();
                        grid.addValues( DimensionItem.getItemIdentifiers( row ) );
                        grid.addValue( dataSourceParams.isSkipRounding() ? value : roundedValue );
                    }
                }
            }
        }
    }

    /**
     * Adds data element values to the given grid based on the given data query
     * parameters.
     *
     * @param params the data query parameters.
     * @param grid the grid.
     */
    private void addDataElementValues( DataQueryParams params, Grid grid )
    {
        if ( !params.getDataElements().isEmpty() )
        {
            DataQueryParams dataSourceParams = params.instance();
            dataSourceParams.removeDimensions( INDICATOR_DIM_ID, DATASET_DIM_ID, PROGRAM_INDICATOR_DIM_ID, PROGRAM_DATAELEMENT_DIM_ID, PROGRAM_ATTRIBUTE_DIM_ID );

            Map<String, Object> aggregatedDataMap = getAggregatedDataValueMapObjectTyped( dataSourceParams );

            for ( Map.Entry<String, Object> entry : aggregatedDataMap.entrySet() )
            {
                grid.addRow();
                grid.addValues( entry.getKey().split( DIMENSION_SEP ) );
                grid.addValue( params.isSkipRounding() ? entry.getValue() : getRounded( entry.getValue() ) );
            }
        }
    }
    
    /**
     * Adds data set values to the given grid based on the given data query
     * parameters.
     *
     * @param params the data query parameters.
     * @param grid the grid.
     */
    private void addDataSetValues( DataQueryParams params, Grid grid )
    {
        if ( !params.getDataSets().isEmpty() )
        {
            // -----------------------------------------------------------------
            // Get complete data set registrations
            // -----------------------------------------------------------------

            DataQueryParams dataSourceParams = params.instance();
            dataSourceParams.ignoreDataApproval(); // No approval for reporting rates
            dataSourceParams.removeDimensions( INDICATOR_DIM_ID, DATAELEMENT_DIM_ID, PROGRAM_INDICATOR_DIM_ID, PROGRAM_DATAELEMENT_DIM_ID, PROGRAM_ATTRIBUTE_DIM_ID );
            dataSourceParams.setAggregationType( AggregationType.COUNT );

            if ( !COMPLETENESS_DIMENSION_TYPES.containsAll( dataSourceParams.getDimensionTypes() ) )
            {
                return;
            }
            
            Map<String, Double> aggregatedDataMap = getAggregatedCompletenessValueMap( dataSourceParams );

            // -----------------------------------------------------------------
            // Get completeness targets
            // -----------------------------------------------------------------

            List<Integer> completenessDimIndexes = dataSourceParams.getCompletenessDimensionIndexes();
            List<Integer> completenessFilterIndexes = dataSourceParams.getCompletenessFilterIndexes();

            DataQueryParams targetParams = dataSourceParams.instance();

            targetParams.setDimensions( ListUtils.getAtIndexes( targetParams.getDimensions(), completenessDimIndexes ) );
            targetParams.setFilters( ListUtils.getAtIndexes( targetParams.getFilters(), completenessFilterIndexes ) );
            targetParams.setSkipPartitioning( true );

            Map<String, Double> targetMap = getAggregatedCompletenessTargetMap( targetParams );

            Integer periodIndex = dataSourceParams.getPeriodDimensionIndex();
            Integer dataSetIndex = dataSourceParams.getDataSetDimensionIndex();

            Map<String, PeriodType> dsPtMap = dataSourceParams.getDataSetPeriodTypeMap();

            PeriodType filterPeriodType = dataSourceParams.getFilterPeriodType();

            // -----------------------------------------------------------------
            // Join data maps, calculate completeness and add to grid
            // -----------------------------------------------------------------

            for ( Map.Entry<String, Double> entry : aggregatedDataMap.entrySet() )
            {
                List<String> dataRow = Lists.newArrayList( entry.getKey().split( DIMENSION_SEP ) );

                List<String> targetRow = ListUtils.getAtIndexes( dataRow, completenessDimIndexes );
                String targetKey = StringUtils.join( targetRow, DIMENSION_SEP );
                Double target = targetMap.get( targetKey );

                if ( target != null && entry.getValue() != null )
                {
                    PeriodType queryPt = filterPeriodType != null ? filterPeriodType : getPeriodTypeFromIsoString( dataRow.get( periodIndex ) );
                    PeriodType dataSetPt = dsPtMap.get( dataRow.get( dataSetIndex ) );

                    target = target * queryPt.getPeriodSpan( dataSetPt );

                    double value = entry.getValue() * PERCENT / target;

                    grid.addRow();
                    grid.addValues( dataRow.toArray() );
                    grid.addValue( params.isSkipRounding() ? value : MathUtils.getRounded( value ) );
                }
            }
        }
    }

    /**
     * Adds program data element values to the given grid based on the given data
     * query parameters.
     * 
     * @param params the data query parameters.
     * @param grid the grid.
     */
    private void addProgramValues( DataQueryParams params, Grid grid )
    {
        if ( !params.getProgramDataElements().isEmpty() || !params.getProgramAttributes().isEmpty() )
        {
            DataQueryParams dataSourceParams = params.instance();
            dataSourceParams.removeDimensions( INDICATOR_DIM_ID, DATASET_DIM_ID, DATAELEMENT_DIM_ID );
            
            EventQueryParams eventQueryParams = EventQueryParams.fromDataQueryParams( dataSourceParams );
            
            Grid eventGrid = eventAnalyticsService.getAggregatedEventData( eventQueryParams );
            
            grid.addRows( eventGrid );
        }
    }

    /**
     * Adds values to the given grid based on dynamic dimensions from the given
     * data query parameters. This assumes that no fixed dimensions are part of
     * the query.
     *
     * @param params the data query parameters.
     * @param grid the grid.
     */
    private void addDynamicDimensionValues( DataQueryParams params, Grid grid )
    {
        if ( params.getIndicators().isEmpty() && params.getDataElements().isEmpty() && params.getDataSets().isEmpty() && params.getProgramDataElements().isEmpty() )
        {
            Map<String, Double> aggregatedDataMap = getAggregatedDataValueMap( params.instance() );

            for ( Map.Entry<String, Double> entry : aggregatedDataMap.entrySet() )
            {
                grid.addRow();
                grid.addValues( entry.getKey().split( DIMENSION_SEP ) );
                grid.addValue( params.isSkipRounding() ? entry.getValue() : MathUtils.getRounded( entry.getValue() ) );
            }
        }
    }

    /**
     * Adds meta data values to the given grid based on the given data query
     * parameters.
     *
     * @param params the data query parameters.
     * @param grid the grid.
     */
    private void addMetaData( DataQueryParams params, Grid grid )
    {
        if ( !params.isSkipMeta() )
        {
            Map<Object, Object> metaData = new HashMap<>();

            // -----------------------------------------------------------------
            // Names element
            // -----------------------------------------------------------------

            Map<String, String> uidNameMap = getUidNameMap( params );
            Map<String, String> cocNameMap = getCocNameMap( params );
            uidNameMap.putAll( cocNameMap );
            uidNameMap.put( DATA_X_DIM_ID, DISPLAY_NAME_DATA_X );

            metaData.put( NAMES_META_KEY, uidNameMap );

            // -----------------------------------------------------------------
            // Item order elements
            // -----------------------------------------------------------------

            Calendar calendar = PeriodType.getCalendar();

            List<String> periodUids = calendar.isIso8601() ? 
                getUids( params.getDimensionOrFilter( PERIOD_DIM_ID ) ) :
                    getLocalPeriodIdentifiers( params.getDimensionOrFilter( PERIOD_DIM_ID ), calendar );

            metaData.put( PERIOD_DIM_ID, periodUids );
            metaData.put( ORGUNIT_DIM_ID, getUids( params.getDimensionOrFilter( ORGUNIT_DIM_ID ) ) );
            metaData.put( CATEGORYOPTIONCOMBO_DIM_ID, cocNameMap.keySet() );

            User user = currentUserService.getCurrentUser();
            
            List<OrganisationUnit> organisationUnits = asTypedList( params.getDimensionOrFilter( ORGUNIT_DIM_ID ), OrganisationUnit.class );
            Collection<OrganisationUnit> roots = user != null ? user.getOrganisationUnits() : null;
            
            if ( params.isHierarchyMeta() )
            {
                metaData.put( OU_HIERARCHY_KEY, getParentGraphMap( organisationUnits, roots ) );
            }

            if ( params.isShowHierarchy() )
            {
                metaData.put( OU_NAME_HIERARCHY_KEY, getParentNameGraphMap( organisationUnits, roots, true, params.getDisplayProperty() ) );
            }

            for ( DimensionalObject dim : params.getDimensionsAndFilters() )
            {
                if ( dim.isAllItems() )
                {
                    metaData.put( dim.getDimension(), getUids( dim.getItems() ) );
                }
            }
            
            grid.setMetaData( metaData );
        }
    }

    /**
     * Substitutes the meta data of the grid with the identifier scheme meta data
     * property indicated in the query.
     * 
     * @param params the data query parameters.
     * @param grid the grid.
     */
    private void applyIdScheme( DataQueryParams params, Grid grid )
    {
        if ( params.hasNonUidOutputIdScheme() )
        {
            List<NameableObject> items = params.getAllDimensionItems();
            
            Map<String, String> map = NameableObjectUtils.getUidPropertyMap( items, params.getOutputIdScheme() );
            
            grid.substituteMetaData( map );
        }
    }
    
    @Override
    public Grid getAggregatedDataValues( DataQueryParams params, boolean tableLayout, List<String> columns, List<String> rows )
    {
        if ( !tableLayout )
        {
            return getAggregatedDataValues( params );
        }
        
        params.setOutputIdScheme( null );
        
        Grid grid = getAggregatedDataValues( params );

        ListUtils.removeEmptys( columns );
        ListUtils.removeEmptys( rows );

        queryPlanner.validateTableLayout( params, columns, rows );

        ReportTable reportTable = new ReportTable();

        List<NameableObject[]> tableColumns = new ArrayList<>();
        List<NameableObject[]> tableRows = new ArrayList<>();

        if ( columns != null )
        {
            for ( String dimension : columns )
            {
                reportTable.getColumnDimensions().add( dimension );

                tableColumns.add( params.getDimensionArrayCollapseDxExplodeCoc( dimension ) );
            }
        }

        if ( rows != null )
        {
            for ( String dimension : rows )
            {
                reportTable.getRowDimensions().add( dimension );

                tableRows.add( params.getDimensionArrayCollapseDxExplodeCoc( dimension ) );
            }
        }

        reportTable.setGridColumns( new CombinationGenerator<>( tableColumns.toArray( IRT2D ) ).getCombinations() );
        reportTable.setGridRows( new CombinationGenerator<>( tableRows.toArray( IRT2D ) ).getCombinations() );

        addIfEmpty( reportTable.getGridColumns() );
        addIfEmpty( reportTable.getGridRows() );

        reportTable.setTitle( IdentifiableObjectUtils.join( params.getFilterItems() ) );
        reportTable.setHideEmptyRows( params.isHideEmptyRows() );
        reportTable.setShowHierarchy( params.isShowHierarchy() );

        Map<String, Object> valueMap = getAggregatedDataValueMapping( grid );

        return reportTable.getGrid( new ListGrid( grid.getMetaData() ), valueMap, false );
    }

    @Override
    public Map<String, Object> getAggregatedDataValueMapping( DataQueryParams params )
    {
        Grid grid = getAggregatedDataValues( params );

        return getAggregatedDataValueMapping( grid );
    }

    @Override
    public Map<String, Object> getAggregatedDataValueMapping( AnalyticalObject object, I18nFormat format )
    {
        DataQueryParams params = getFromAnalyticalObject( object, format );

        return getAggregatedDataValueMapping( params );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Generates a mapping of permutations keys (organisation unit id or null)
     * and mappings of organisation unit group and counts.
     *
     * @param params the data query parameters.
     * @param indicators the indicators for which formulas to scan for organisation
     *        unit groups.
     * @return a map of maps.
     */
    private Map<String, Map<String, Integer>> getOrgUnitTargetMap( DataQueryParams params, Collection<Indicator> indicators )
    {
        Set<OrganisationUnitGroup> orgUnitGroups = expressionService.getOrganisationUnitGroupsInIndicators( indicators );

        if ( orgUnitGroups == null || orgUnitGroups.isEmpty() )
        {
            return null;
        }

        DataQueryParams orgUnitTargetParams = params.instance().pruneToDimensionType( DimensionType.ORGANISATIONUNIT );
        orgUnitTargetParams.getDimensions().add( new BaseDimensionalObject( DimensionalObject.ORGUNIT_GROUP_DIM_ID, null, new ArrayList<NameableObject>( orgUnitGroups ) ) );
        orgUnitTargetParams.setSkipPartitioning( true );

        Map<String, Double> orgUnitCountMap = getAggregatedOrganisationUnitTargetMap( orgUnitTargetParams );

        return DataQueryParams.getPermutationOrgUnitGroupCountMap( orgUnitCountMap );
    }

    /**
     * Generates a mapping where the key represents the dimensional item identifiers
     * concatenated by "-" and the value is the corresponding aggregated data value
     * based on the given grid.
     *
     * @param grid the grid.
     * @return a mapping between item identifiers and aggregated values.
     */
    private Map<String, Object> getAggregatedDataValueMapping( Grid grid )
    {
        Map<String, Object> map = new HashMap<>();

        int metaCols = grid.getWidth() - 1;
        int valueIndex = grid.getWidth() - 1;

        for ( List<Object> row : grid.getRows() )
        {
            StringBuilder key = new StringBuilder();

            for ( int index = 0; index < metaCols; index++ )
            {
                key.append( row.get( index ) ).append( DIMENSION_SEP );
            }

            key.deleteCharAt( key.length() - 1 );

            Object value = row.get( valueIndex );

            map.put( key.toString(), value );
        }

        return map;
    }

    /**
     * Generates aggregated values for the given query. Creates a mapping between
     * a dimension key and the aggregated value. The dimension key is a
     * concatenation of the identifiers of the dimension items separated by "-".
     *
     * @param params the data query parameters.
     * @return a mapping between a dimension key and the aggregated value.
     */
    private Map<String, Double> getAggregatedDataValueMap( DataQueryParams params )
    {
        return getDoubleMap( getAggregatedValueMap( params, ANALYTICS_TABLE_NAME ) );
    }

    /**
     * Generates aggregated values for the given query. Creates a mapping between
     * a dimension key and the aggregated value. The dimension key is a
     * concatenation of the identifiers of the dimension items separated by "-".
     *
     * @param params the data query parameters.
     * @return a mapping between a dimension key and the aggregated value.
     */
    private Map<String, Object> getAggregatedDataValueMapObjectTyped( DataQueryParams params )
    {
        return getAggregatedValueMap( params, ANALYTICS_TABLE_NAME );
    }

    /**
     * Generates aggregated values for the given query. Creates a mapping between
     * a dimension key and the aggregated value. The dimension key is a
     * concatenation of the identifiers of the dimension items separated by "-".
     *
     * @param params the data query parameters.
     * @return a mapping between a dimension key and the aggregated value.
     */
    private Map<String, Double> getAggregatedCompletenessValueMap( DataQueryParams params )
    {
        return getDoubleMap( getAggregatedValueMap( params, COMPLETENESS_TABLE_NAME ) );
    }

    /**
     * Generates a mapping between the the data set dimension key and the count
     * of expected data sets to report.
     *
     * @param params the data query parameters.
     * @return a mapping between the the data set dimension key and the count of
     *         expected data sets to report.
     */
    private Map<String, Double> getAggregatedCompletenessTargetMap( DataQueryParams params )
    {
        return getDoubleMap( getAggregatedValueMap( params, COMPLETENESS_TARGET_TABLE_NAME ) );
    }

    /**
     * Generates a mapping between the the org unit dimension key and the count
     * of org units inside the subtree of the given organisation units and
     * members of the given organisation unit groups.
     *
     * @param params the data query parameters.
     * @return a mapping between the the data set dimension key and the count of
     *         expected data sets to report.
     */
    private Map<String, Double> getAggregatedOrganisationUnitTargetMap( DataQueryParams params )
    {
        return getDoubleMap( getAggregatedValueMap( params, ORGUNIT_TARGET_TABLE_NAME ) );
    }

    /**
     * Generates a mapping between a dimension key and the aggregated value. The
     * dimension key is a concatenation of the identifiers of the dimension items
     * separated by "-".
     *
     * @param params the data query parameters.
     * @return a mapping between a dimension key and aggregated values.
     */
    private Map<String, Object> getAggregatedValueMap( DataQueryParams params, String tableName )
    {
        queryPlanner.validateMaintenanceMode();

        int optimalQueries = MathUtils.getWithin( getProcessNo(), 1, MAX_QUERIES );

        int maxLimit = getMaxLimit();
        
        Timer t = new Timer().start().disablePrint();

        DataQueryGroups queryGroups = queryPlanner.planQuery( params, optimalQueries, tableName );

        t.getSplitTime( "Planned analytics query, got: " + queryGroups.getLargestGroupSize() + " for optimal: " + optimalQueries );

        Map<String, Object> map = new HashMap<>();

        for ( List<DataQueryParams> queries : queryGroups.getSequentialQueries() )
        {
            List<Future<Map<String, Object>>> futures = new ArrayList<>();

            for ( DataQueryParams query : queries )
            {
                futures.add( analyticsManager.getAggregatedDataValues( query, maxLimit ) );
            }

            for ( Future<Map<String, Object>> future : futures )
            {
                try
                {
                    Map<String, Object> taskValues = future.get();

                    if ( taskValues != null )
                    {
                        map.putAll( taskValues );
                    }
                }
                catch ( Exception ex )
                {
                    log.error( DebugUtils.getStackTrace( ex ) );
                    log.error( DebugUtils.getStackTrace( ex.getCause() ) );

                    throw new RuntimeException( "Error during execution of aggregation query task", ex );
                }
            }
        }

        t.getTime( "Got analytics values" );

        return map;
    }

    // -------------------------------------------------------------------------
    // Methods for assembling DataQueryParams
    // -------------------------------------------------------------------------

    @Override
    public DataQueryParams getFromUrl( Set<String> dimensionParams, Set<String> filterParams, AggregationType aggregationType,
        String measureCriteria, boolean skipMeta, boolean skipRounding, boolean hierarchyMeta, boolean ignoreLimit,
        boolean hideEmptyRows, boolean showHierarchy, DisplayProperty displayProperty, IdentifiableProperty outputIdScheme, 
        String approvalLevel, String program, String stage, I18nFormat format )
    {
        DataQueryParams params = new DataQueryParams();

        params.setAggregationType( aggregationType );
        params.setIgnoreLimit( ignoreLimit );

        if ( dimensionParams != null && !dimensionParams.isEmpty() )
        {
            params.addDimensions( getDimensionalObjects( dimensionParams, format ) );
        }

        if ( filterParams != null && !filterParams.isEmpty() )
        {
            params.addFilters( getDimensionalObjects( filterParams, format ) );
        }

        if ( measureCriteria != null && !measureCriteria.isEmpty() )
        {
            params.setMeasureCriteria( DataQueryParams.getMeasureCriteriaFromParam( measureCriteria ) );
        }

        params.setSkipMeta( skipMeta );
        params.setSkipRounding( skipRounding );
        params.setHierarchyMeta( hierarchyMeta );
        params.setHideEmptyRows( hideEmptyRows );
        params.setShowHierarchy( showHierarchy );
        params.setDisplayProperty( displayProperty );
        params.setOutputIdScheme( outputIdScheme );
        params.setApprovalLevel( approvalLevel );

        if ( program != null )
        {
            params.setProgram( programService.getProgram( program ) );
        }

        if ( stage != null )
        {
            params.setProgramStage( programStageService.getProgramStage( stage ) );
        }

        return params;
    }

    @Override
    public DataQueryParams getFromAnalyticalObject( AnalyticalObject object, I18nFormat format )
    {
        DataQueryParams params = new DataQueryParams();

        if ( object != null )
        {
            Date date = object.getRelativePeriodDate();

            object.populateAnalyticalProperties();

            for ( DimensionalObject column : object.getColumns() )
            {
                params.addDimensions( getDimension( toDimension( column.getDimension() ), getUids( column.getItems() ), date, format, false ) );
            }

            for ( DimensionalObject row : object.getRows() )
            {
                params.addDimensions( getDimension( toDimension( row.getDimension() ), getUids( row.getItems() ), date, format, false ) );
            }

            for ( DimensionalObject filter : object.getFilters() )
            {
                params.addFilters( getDimension( toDimension( filter.getDimension() ), getUids( filter.getItems() ), date, format, false ) );
            }
        }

        return params;
    }

    @Override
    public List<DimensionalObject> getDimensionalObjects( Set<String> dimensionParams, I18nFormat format )
    {
        List<DimensionalObject> list = new ArrayList<>();

        if ( dimensionParams != null )
        {
            for ( String param : dimensionParams )
            {
                String dimension = DimensionalObjectUtils.getDimensionFromParam( param );
                List<String> items = DimensionalObjectUtils.getDimensionItemsFromParam( param );

                if ( dimension != null && items != null )
                {
                    list.addAll( getDimension( dimension, items, null, format, false ) );
                }
            }
        }

        return list;
    }

    // TODO verify that current user can read each dimension and dimension item
    // TODO optimize so that org unit levels + boundary are used in query instead of fetching all org units one by one

    @Override
    public List<DimensionalObject> getDimension( String dimension, List<String> items, Date relativePeriodDate, I18nFormat format, boolean allowNull )
    {
        final boolean allItems = items.isEmpty();
        
        if ( DATA_X_DIM_ID.equals( dimension ) )
        {
            List<DimensionalObject> dataDimensions = new ArrayList<>();
            List<DataElementGroup> dataElementGroups = new ArrayList<>();
            
            List<NameableObject> indicators = new ArrayList<>();
            List<NameableObject> dataElements = new ArrayList<>();
            List<NameableObject> dataSets = new ArrayList<>();
            List<NameableObject> operandDataElements = new ArrayList<>();
            List<NameableObject> programIndicators = new ArrayList<>();
            List<NameableObject> programDataElements = new ArrayList<>();
            List<NameableObject> programAttributes = new ArrayList<>();

            itemLoop:
            for ( String uid : items )
            {
                if ( uid != null && uid.startsWith( KEY_DE_GROUP ) )
                {
                    String groupUid = DimensionalObjectUtils.getUidFromGroupParam( uid );
                    
                    DataElementGroup group = idObjectManager.get( DataElementGroup.class, groupUid );
                    
                    if ( group != null )
                    {
                        dataElementGroups.add( group );
                    }
                    
                    continue itemLoop;
                }
                
                Indicator in = idObjectManager.get( Indicator.class, uid );

                if ( in != null )
                {
                    indicators.add( in );
                    continue itemLoop;
                }

                DataElement de = idObjectManager.get( DataElement.class, uid );

                if ( de != null && DataElementDomain.AGGREGATE.equals( de.getDomainType() ) )
                {
                    dataElements.add( de );
                    continue itemLoop;
                }
                
                if ( de != null && DataElementDomain.TRACKER.equals( de.getDomainType() ) )
                {
                    programDataElements.add( de );
                    continue itemLoop;
                }

                DataSet ds = idObjectManager.get( DataSet.class, uid );

                if ( ds != null )
                {
                    dataSets.add( ds );
                    continue itemLoop;
                }

                DataElementOperand dc = operandService.getDataElementOperandByUid( uid );

                if ( dc != null )
                {
                    operandDataElements.add( dc.getDataElement() );
                    continue itemLoop;
                }
                
                ProgramIndicator pi = idObjectManager.get( ProgramIndicator.class, uid );
                
                if ( pi != null )
                {
                    programIndicators.add( pi );
                    continue itemLoop;
                }
                
                TrackedEntityAttribute pa = idObjectManager.get( TrackedEntityAttribute.class, uid );
                
                if ( pa != null )
                {
                    programAttributes.add( pa );
                    continue itemLoop;
                }

                throw new IllegalQueryException( "Data dimension option identifier does not reference any option: " + uid );
            }
            
            if ( !dataElementGroups.isEmpty() )
            {
                for ( DataElementGroup group : dataElementGroups )
                {
                    dataElements.addAll( group.getMembers() );
                }
            }
            
            if ( !indicators.isEmpty() )
            {
                dataDimensions.add( new BaseDimensionalObject( INDICATOR_DIM_ID, DimensionType.INDICATOR, indicators ) );
            }

            if ( !dataElements.isEmpty() )
            {
                dataDimensions.add( new BaseDimensionalObject( DATAELEMENT_DIM_ID, DimensionType.DATAELEMENT, dataElements ) );
            }

            if ( !dataSets.isEmpty() )
            {
                dataDimensions.add( new BaseDimensionalObject( DATASET_DIM_ID, DimensionType.DATASET, dataSets ) );
            }

            if ( !operandDataElements.isEmpty() )
            {
                dataDimensions.add( new BaseDimensionalObject( DATAELEMENT_DIM_ID, DimensionType.DATAELEMENT, operandDataElements ) );
                dataDimensions.add( new BaseDimensionalObject( CATEGORYOPTIONCOMBO_DIM_ID, DimensionType.CATEGORY_OPTION_COMBO, new ArrayList<NameableObject>() ) );
            }
            
            if ( !programIndicators.isEmpty() )
            {
                dataDimensions.add( new BaseDimensionalObject( PROGRAM_INDICATOR_DIM_ID, DimensionType.PROGRAM_INDICATOR, programIndicators ) );
            }
            
            if ( !programDataElements.isEmpty() )
            {
                dataDimensions.add( new BaseDimensionalObject( PROGRAM_DATAELEMENT_DIM_ID, DimensionType.PROGRAM_DATAELEMENT, programDataElements ) );
            }

            if ( !programAttributes.isEmpty() )
            {
                dataDimensions.add( new BaseDimensionalObject( PROGRAM_ATTRIBUTE_DIM_ID, DimensionType.PROGRAM_ATTRIBUTE, programAttributes ) );
            }
            
            if ( indicators.isEmpty() && dataElements.isEmpty() && dataSets.isEmpty() && operandDataElements.isEmpty() && 
                programDataElements.isEmpty() && programAttributes.isEmpty() && programIndicators.isEmpty() )
            {
                throw new IllegalQueryException( "Dimension dx is present in query without any valid dimension options" );
            }

            return dataDimensions;
        }

        if ( CATEGORYOPTIONCOMBO_DIM_ID.equals( dimension ) )
        {
            DimensionalObject object = new BaseDimensionalObject( dimension, DimensionType.CATEGORY_OPTION_COMBO, null, DISPLAY_NAME_CATEGORYOPTIONCOMBO, new ArrayList<NameableObject>() );

            return Lists.newArrayList( object );
        }

        if ( PERIOD_DIM_ID.equals( dimension ) )
        {
            Calendar calendar = PeriodType.getCalendar();

            Set<Period> periods = new HashSet<>();

            for ( String isoPeriod : items )
            {
                if ( RelativePeriodEnum.contains( isoPeriod ) )
                {
                    RelativePeriodEnum relativePeriod = RelativePeriodEnum.valueOf( isoPeriod );
                    List<Period> relativePeriods = RelativePeriods.getRelativePeriodsFromEnum( relativePeriod, relativePeriodDate, format, true );
                    periods.addAll( relativePeriods );
                }
                else
                {
                    Period period = PeriodType.getPeriodFromIsoString( isoPeriod );

                    if ( period != null )
                    {
                        periods.add( period );
                    }
                }
            }

            if ( periods.isEmpty() )
            {
                throw new IllegalQueryException( "Dimension pe is present in query without any valid dimension options" );
            }

            for ( Period period : periods )
            {
                period.setName( format != null ? format.formatPeriod( period ) : null );

                if ( !calendar.isIso8601() )
                {
                    period.setUid( getLocalPeriodIdentifier( period, calendar ) );
                }
            }

            List<Period> periodList = new ArrayList<>( periods );
            Collections.sort( periodList, AscendingPeriodEndDateComparator.INSTANCE );

            DimensionalObject object = new BaseDimensionalObject( dimension, DimensionType.PERIOD, null, DISPLAY_NAME_PERIOD, asList( periodList ) );

            return Lists.newArrayList( object );
        }

        if ( ORGUNIT_DIM_ID.equals( dimension ) )
        {
            User user = currentUserService.getCurrentUser();

            List<NameableObject> ous = new UniqueArrayList<>();
            List<Integer> levels = new UniqueArrayList<>();
            List<OrganisationUnitGroup> groups = new UniqueArrayList<>();

            for ( String ou : items )
            {
                if ( KEY_USER_ORGUNIT.equals( ou ) && user != null && user.hasOrganisationUnit() )
                {
                    ous.add( user.getOrganisationUnit() );
                }
                else if ( KEY_USER_ORGUNIT_CHILDREN.equals( ou ) && user != null && user.hasOrganisationUnit() )
                {
                    ous.addAll( user.getOrganisationUnit().getSortedChildren() );
                }
                else if ( KEY_USER_ORGUNIT_GRANDCHILDREN.equals( ou ) && user != null && user.hasOrganisationUnit() )
                {
                    ous.addAll( user.getOrganisationUnit().getSortedGrandChildren() );
                }
                else if ( ou != null && ou.startsWith( KEY_LEVEL ) )
                {
                    int level = DimensionalObjectUtils.getLevelFromLevelParam( ou );

                    if ( level > 0 )
                    {
                        levels.add( level );
                    }
                }
                else if ( ou != null && ou.startsWith( KEY_ORGUNIT_GROUP ) )
                {
                    String uid = DimensionalObjectUtils.getUidFromGroupParam( ou );

                    OrganisationUnitGroup group = idObjectManager.get( OrganisationUnitGroup.class, uid );

                    if ( group != null )
                    {
                        groups.add( group );
                    }
                }
                else if ( CodeGenerator.isValidCode( ou ) )
                {
                    OrganisationUnit unit = organisationUnitService.getOrganisationUnit( ou );

                    if ( unit != null )
                    {
                        ous.add( unit );
                    }
                }
            }

            List<NameableObject> orgUnits = new UniqueArrayList<>();
            List<OrganisationUnit> ousList = NameableObjectUtils.asTypedList( ous );

            if ( !levels.isEmpty() )
            {
                orgUnits.addAll( organisationUnitService.getOrganisationUnitsAtLevels( levels, ousList ) );
            }

            if ( !groups.isEmpty() )
            {
                orgUnits.addAll( organisationUnitService.getOrganisationUnits( groups, ousList ) );
            }

            // -----------------------------------------------------------------
            // When levels / groups are present, OUs are considered boundaries
            // -----------------------------------------------------------------

            if ( levels.isEmpty() && groups.isEmpty() )
            {
                orgUnits.addAll( ous );
            }

            if ( orgUnits.isEmpty() )
            {
                throw new IllegalQueryException( "Dimension ou is present in query without any valid dimension options" );
            }

            DimensionalObject object = new BaseDimensionalObject( dimension, DimensionType.ORGANISATIONUNIT, null, DISPLAY_NAME_ORGUNIT, orgUnits );

            return Lists.newArrayList( object );
        }

        if ( LONGITUDE_DIM_ID.contains( dimension ) )
        {
            DimensionalObject object = new BaseDimensionalObject( dimension, DimensionType.STATIC, null, DISPLAY_NAME_LONGITUDE, new ArrayList<NameableObject>() );

            return Lists.newArrayList( object );
        }

        if ( LATITUDE_DIM_ID.contains( dimension ) )
        {
            DimensionalObject object = new BaseDimensionalObject( dimension, DimensionType.STATIC, null, DISPLAY_NAME_LATITUDE, new ArrayList<NameableObject>() );

            return Lists.newArrayList( object );
        }

        OrganisationUnitGroupSet ougs = idObjectManager.get( OrganisationUnitGroupSet.class, dimension );

        if ( ougs != null && ougs.isDataDimension() )
        {
            List<NameableObject> ous = !allItems ? asList( idObjectManager.getByUidOrdered( OrganisationUnitGroup.class, items ) ) : ougs.getItems();

            DimensionalObject object = new BaseDimensionalObject( dimension, DimensionType.ORGANISATIONUNIT_GROUPSET, null, ougs.getDisplayName(), ous, allItems );

            return Lists.newArrayList( object );
        }

        DataElementGroupSet degs = idObjectManager.get( DataElementGroupSet.class, dimension );

        if ( degs != null && degs.isDataDimension() )
        {
            List<NameableObject> des = !allItems ? asList( idObjectManager.getByUidOrdered( DataElementGroup.class, items ) ) : degs.getItems();

            DimensionalObject object = new BaseDimensionalObject( dimension, DimensionType.DATAELEMENT_GROUPSET, null, degs.getDisplayName(), des, allItems );

            return Lists.newArrayList( object );
        }

        CategoryOptionGroupSet cogs = idObjectManager.get( CategoryOptionGroupSet.class, dimension );

        if ( cogs != null && cogs.isDataDimension() )
        {
            List<NameableObject> cogz = !allItems ? asList( idObjectManager.getByUidOrdered( CategoryOptionGroup.class, items ) ) : cogs.getItems();

            DimensionalObject object = new BaseDimensionalObject( dimension, DimensionType.CATEGORYOPTION_GROUPSET, null, cogs.getDisplayName(), cogz, allItems );

            return Lists.newArrayList( object );
        }

        DataElementCategory dec = idObjectManager.get( DataElementCategory.class, dimension );

        if ( dec != null && dec.isDataDimension() )
        {
            List<NameableObject> decos = !allItems ? asList( idObjectManager.getByUidOrdered( DataElementCategoryOption.class, items ) ) : dec.getItems();

            DimensionalObject object = new BaseDimensionalObject( dimension, DimensionType.CATEGORY, null, dec.getDisplayName(), decos, allItems );

            return Lists.newArrayList( object );
        }

        if ( allowNull )
        {
            return null;
        }

        throw new IllegalQueryException( "Dimension identifier does not reference any dimension: " + dimension );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Returns a mapping of permutation keys and mappings of data element operands
     * and values, based on the given mapping of dimension option keys and 
     * aggregated values.
     * 
     * @param params the data query parameters.
     */
    private Map<String, Map<DataElementOperand, Double>> getPermutationOperandValueMap( DataQueryParams params )
    {
        Map<String, Double> aggregatedDataTotalsMap = getAggregatedDataValueMapTotals( params );
        Map<String, Double> aggregatedDataOptionCombosMap = getAggregatedDataValueMapOptionCombos( params );
        
        MapMap<String, DataElementOperand, Double> permOperandValueMap = new MapMap<>();

        DataQueryParams.putPermutationOperandValueMap( permOperandValueMap, aggregatedDataTotalsMap, false );
        DataQueryParams.putPermutationOperandValueMap( permOperandValueMap, aggregatedDataOptionCombosMap, true );
        
        return permOperandValueMap;
    }
    
    /**
     * Returns a mapping of dimension keys and aggregated values for the data
     * element totals part of the indicators in the given query.
     *
     * @param params the data query parameters.
     * @return a mapping of dimension keys and aggregated values.
     */
    private Map<String, Double> getAggregatedDataValueMapTotals( DataQueryParams params )
    {
        List<Indicator> indicators = asTypedList( params.getIndicators() );
        List<NameableObject> dataElements = asList( expressionService.getDataElementTotalsInIndicators( indicators ) );

        if ( !dataElements.isEmpty() )
        {
            DataQueryParams dataSourceParams = params.instance().removeDimensions( DataQueryParams.DATA_DIMS );
            
            dataSourceParams.getDimensions().add( DX_INDEX, new BaseDimensionalObject( 
                DATAELEMENT_DIM_ID, DimensionType.DATAELEMENT, dataElements ) );
    
            return getAggregatedDataValueMap( dataSourceParams );
        }
        
        return new HashMap<>();
    }

    /**
     * Returns a mapping of dimension keys and aggregated values for the data
     * elements with category option combinations part of the indicators in the 
     * given query.
     *
     * @param params the data query parameters.
     * @return a mapping of dimension keys and aggregated values.
     */
    private Map<String, Double> getAggregatedDataValueMapOptionCombos( DataQueryParams params )
    {
        List<Indicator> indicators = asTypedList( params.getIndicators() );
        List<NameableObject> dataElements = asList( expressionService.getDataElementWithOptionCombosInIndicators( indicators ) );

        if ( !dataElements.isEmpty() )
        {
            DataQueryParams dataSourceParams = params.instance().removeDimensions( DataQueryParams.DATA_DIMS );
            
            dataSourceParams.getDimensions().add( DataQueryParams.DX_INDEX, new BaseDimensionalObject( 
                DATAELEMENT_DIM_ID, DimensionType.DATAELEMENT, dataElements ) );
            dataSourceParams.getDimensions().add( DataQueryParams.CO_IN_INDEX, new BaseDimensionalObject( 
                CATEGORYOPTIONCOMBO_DIM_ID, DimensionType.CATEGORY_OPTION_COMBO, new ArrayList<NameableObject>() ) );
    
            return getAggregatedDataValueMap( dataSourceParams );
        }
        
        return new HashMap<>();
    }

    /**
     * Returns a mapping between identifiers and names for the given dimensional
     * objects.
     *
     * @param params the data query parameters.
     * @return a mapping between identifiers and names.
     */
    private Map<String, String> getUidNameMap( DataQueryParams params )
    {
        List<DimensionalObject> dimensions = params.getDimensionsAndFilters();
        
        Map<String, String> map = new HashMap<>();
        
        Calendar calendar = PeriodType.getCalendar();

        for ( DimensionalObject dimension : dimensions )
        {
            List<NameableObject> items = new ArrayList<>( dimension.getItems() );

            for ( NameableObject object : items )
            {
                if ( DimensionType.PERIOD.equals( dimension.getDimensionType() ) && !calendar.isIso8601() )
                {
                    Period period = (Period) object;
                    DateTimeUnit dateTimeUnit = calendar.fromIso( period.getStartDate() );
                    map.put( period.getPeriodType().getIsoDate( dateTimeUnit ), period.getDisplayName() );
                }
                else
                {
                    map.put( object.getUid(), NameableObjectUtils.getDisplayProperty( object, params.getDisplayProperty() ) );
                }

                if ( DimensionType.ORGANISATIONUNIT.equals( dimension.getDimensionType() ) && params.isHierarchyMeta() )
                {
                    OrganisationUnit unit = (OrganisationUnit) object;
                    
                    map.putAll( NameableObjectUtils.getUidDisplayPropertyMap( unit.getAncestors(), params.getDisplayProperty() ) );
                }
            }

            map.put( dimension.getDimension(), NameableObjectUtils.getDisplayProperty( dimension, params.getDisplayProperty() ) );
        }

        return map;
    }
    
    /**
     * Returns a mapping between the category option combo identifiers and names
     * in the given grid.
     *
     * @param params the data query parameters.
     * @param a mapping between identifiers and names.
     */
    private Map<String, String> getCocNameMap( DataQueryParams params )
    {
        Map<String, String> metaData = new HashMap<>();

        List<NameableObject> des = params.getDimensionOrFilter( DATAELEMENT_DIM_ID );

        if ( des != null && !des.isEmpty() )
        {
            Set<DataElementCategoryCombo> categoryCombos = new HashSet<>();

            for ( NameableObject de : des )
            {
                DataElement dataElement = (DataElement) de;

                if ( dataElement.hasCategoryCombo() )
                {
                    categoryCombos.add( dataElement.getCategoryCombo() );
                }
            }

            for ( DataElementCategoryCombo cc : categoryCombos )
            {
                for ( DataElementCategoryOptionCombo coc : cc.getOptionCombos() )
                {
                    metaData.put( coc.getUid(), coc.getName() );
                }
            }
        }

        return metaData;
    }

    /**
     * Gets the number of available cores. Uses explicit number from system
     * setting if available. Detects number of cores from current server runtime
     * if not.
     * 
     * @return the number of available cores.
     */
    private int getProcessNo()
    {
        Integer cores = (Integer) systemSettingManager.getSystemSetting( SystemSettingManager.KEY_DATABASE_SERVER_CPUS );

        return ( cores == null || cores == 0 ) ? SystemUtils.getCpuCores() : cores;
    }

    /**
     * Converts a String, Object map into a specific String, Double map.
     *
     * @param map the map to convert.
     * @return a mapping between string and double values.
     */
    private Map<String, Double> getDoubleMap( Map<String, Object> map )
    {
        Map<String, Double> typedMap = new HashMap<>();

        for ( Map.Entry<String, Object> entry : map.entrySet() )
        {
            final Object value = entry.getValue();

            if ( value != null && Double.class.equals( value.getClass() ) )
            {
                typedMap.put( entry.getKey(), (Double) entry.getValue() );
            }
        }

        return typedMap;
    }

    /**
     * Returns the given value. If of class Double the value is rounded.
     *
     * @param value the value to return and potentially round.
     * @return the rounded value.
     */
    private Object getRounded( Object value )
    {
        return value != null && Double.class.equals( value.getClass() ) ? MathUtils.getRounded( (Double) value ) : value;
    }

    /**
     * Returns the max records limit. 0 indicates no limit.
     * 
     * @return the max records limit.
     */
    private int getMaxLimit()
    {
        return (Integer) systemSettingManager.getSystemSetting( SystemSettingManager.KEY_ANALYTICS_MAX_LIMIT, SystemSettingManager.DEFAULT_ANALYTICS_MAX_LIMIT );
    }
}
