package org.hisp.dhis.rbf.quality.dataentry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.rbf.api.Lookup;
import org.hisp.dhis.rbf.api.LookupService;
import org.hisp.dhis.rbf.api.QualityMaxValue;
import org.hisp.dhis.rbf.api.QualityMaxValueService;
import org.hisp.dhis.rbf.api.QualityScorePayment;
import org.hisp.dhis.rbf.api.QualityScorePaymentService;
import org.hisp.dhis.rbf.impl.DefaultPBFAggregationService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class LoadQualityScoreDetailsAction
    implements Action
{
    private final static String TARIFF_SETTING_AUTHORITY = "MAXSCORE_SETTING_AUTHORITY";

    private final static String QUALITY_MAX_DATAELEMENT = "QUALITY_MAX_DATAELEMENT";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private QualityMaxValueService qualityMaxValueService;

    public void setQualityMaxValueService( QualityMaxValueService qualityMaxValueService )
    {
        this.qualityMaxValueService = qualityMaxValueService;
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

    private LookupService lookupService;

    public void setLookupService( LookupService lookupService )
    {
        this.lookupService = lookupService;
    }

    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    @Autowired
    private DefaultPBFAggregationService defaultPBFAggregationService;
    
    @Autowired
    private PeriodService periodService;
    
    @Autowired
    private QualityScorePaymentService qualityScorePaymentService;
    
    // -------------------------------------------------------------------------
    // Input / Output
    // -------------------------------------------------------------------------

    private String orgUnitId;

    public void setOrgUnitId( String orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    private String dataSetId;

    public void setDataSetId( String dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private String selectedPeriodId;

    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }

    List<DataElement> dataElements = new ArrayList<DataElement>();

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );;

    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }

    private Map<Integer, QualityMaxValue> qualityMaxValueMap = new HashMap<Integer, QualityMaxValue>();

    public Map<Integer, QualityMaxValue> getQualityMaxValueMap()
    {
        return qualityMaxValueMap;
    }

    private Map<Integer, DataValue> dataValueMap = new HashMap<Integer, DataValue>();

    public Map<Integer, DataValue> getDataValueMap()
    {
        return dataValueMap;
    }

    private String paymentMessage = "No Data avialable for ";
    
    public String getPaymentMessage()
    {
        return paymentMessage;
    }
    
    private Double totalUnadjustedAmt = 0.0;

    public Double getTotalUnadjustedAmt()
    {
        return totalUnadjustedAmt;
    }
    
    private Set<QualityScorePayment> qualityScorePayments;
    
    public Set<QualityScorePayment> getQualityScorePayments()
    {
        return qualityScorePayments;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

        Period period = PeriodType.getPeriodFromIsoString( selectedPeriodId );
        Constant tariff_authority = constantService.getConstantByName( TARIFF_SETTING_AUTHORITY );
        int tariff_setting_authority = 0;
        if ( tariff_authority == null )
        {
            tariff_setting_authority = 1;

        }
        else
        {
            tariff_setting_authority = (int) tariff_authority.getValue();
        }
        
        Constant qualityMaxDataElement = constantService.getConstantByName( QUALITY_MAX_DATAELEMENT );
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        DataSet dataSet = dataSetService.getDataSet( Integer.parseInt( dataSetId ) );

        DataElementCategoryOptionCombo optionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();

        List<DataElement> dataElementList = new ArrayList<DataElement>( dataSet.getDataElements() );
        for ( DataElement de : dataElementList )
        {
            Set<AttributeValue> attrValueSet = new HashSet<AttributeValue>( de.getAttributeValues() );
            for ( AttributeValue attValue : attrValueSet )
            {
                if ( attValue.getAttribute().getId() == qualityMaxDataElement.getValue() )
                {
                    dataElements.add( de );
                }
            }
        }
        
        for ( DataElement dataElement : dataElements )
        {
            List<QualityMaxValue> qualityMaxValues = new ArrayList<QualityMaxValue>();
            OrganisationUnit parentOrgunit = findParentOrgunitforTariff( organisationUnit, tariff_setting_authority );
            if ( parentOrgunit != null )
            {
                qualityMaxValues = new ArrayList<QualityMaxValue>( qualityMaxValueService.getQuanlityMaxValues( parentOrgunit, dataElement ) );
            }
            
            DataValue dataValue = dataValueService.getDataValue( dataElement, period, organisationUnit, optionCombo );
            for ( QualityMaxValue qualityMaxValue : qualityMaxValues )
            {

                if ( qualityMaxValue.getStartDate().getTime() <= period.getStartDate().getTime()
                    && period.getEndDate().getTime() <= qualityMaxValue.getEndDate().getTime() )
                {
                    qualityMaxValueMap.put( dataElement.getId(), qualityMaxValue );
                    if ( dataValue != null )
                    {
                        dataValueMap.put( dataElement.getId(), dataValue );
                    }

                    System.out.println( "In Quality Data Value" );
                    break;
                }
            }
        }
                
        Collections.sort( dataElements );
        
        List<Lookup> lookups = new ArrayList<Lookup>( lookupService.getAllLookupsByType( Lookup.DS_PAYMENT_TYPE ) );
        DataSet paymentDataSet = null;
        for ( Lookup lookup : lookups )
        {
            String[] lookupType = lookup.getValue().split( ":" );
            System.out.println( lookup.getValue() +"  " + Integer.parseInt( lookupType[0] ) + "  " + Integer.parseInt( dataSetId ) );
            if ( Integer.parseInt( lookupType[1] ) == Integer.parseInt( dataSetId ) )
            {
                paymentDataSet = dataSetService.getDataSet( Integer.parseInt(  lookupType[0] ) );
                break;
            }
        }
        
        int flag = 1;
        Set<Period> periods = new HashSet<Period>( periodService.getIntersectingPeriodsByPeriodType( paymentDataSet.getPeriodType(), period.getStartDate(), period.getEndDate() ) );
        for( Period period1 : periods )
        {
            Double overAllAdjustedAmt = defaultPBFAggregationService.calculateOverallUnadjustedPBFAmount( period1, organisationUnit, paymentDataSet );
            
            if( overAllAdjustedAmt == null || overAllAdjustedAmt == 0 )
            {
                paymentMessage += period1.getStartDateString() +", ";
                flag = 2;
            }
            else
            {
                totalUnadjustedAmt += overAllAdjustedAmt;
            }
        }
        
        if( flag == 1 )
        {
            paymentMessage = "Data available for this quarter";
        }

        qualityScorePayments = new HashSet<QualityScorePayment>( qualityScorePaymentService.getAllQualityScorePayments() );
        
        return SUCCESS;
    }

    public OrganisationUnit findParentOrgunitforTariff( OrganisationUnit organisationUnit, Integer tariffOULevel )
    {
        Integer ouLevel = organisationUnitService.getLevelOfOrganisationUnit( organisationUnit.getId() );
        if ( tariffOULevel == ouLevel )
        {
            return organisationUnit;
        }
        else
        {
            return findParentOrgunitforTariff( organisationUnit.getParent(), tariffOULevel );
        }
    }
}