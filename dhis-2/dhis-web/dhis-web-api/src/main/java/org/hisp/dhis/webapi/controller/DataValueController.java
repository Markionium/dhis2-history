package org.hisp.dhis.webapi.controller;

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

import static org.hisp.dhis.setting.SystemSettingManager.KEY_DATA_IMPORT_REQUIRE_CATEGORY_OPTION_COMBO;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_DATA_IMPORT_STRICT_CATEGORY_OPTION_COMBOS;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_DATA_IMPORT_STRICT_ORGANISATION_UNITS;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_DATA_IMPORT_STRICT_PERIODS;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.common.ValueType;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.util.ValidationUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.webapi.utils.InputUtils;
import org.hisp.dhis.webapi.utils.WebMessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = DataValueController.RESOURCE_PATH )
public class DataValueController
{
    public static final String RESOURCE_PATH = "/dataValues";

    // ---------------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------------

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private DataElementCategoryService categoryService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private DataValueService dataValueService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private IdentifiableObjectManager idObjectManager;
    
    @Autowired
    private SystemSettingManager systemSettingManager;

    @Autowired
    private InputUtils inputUtils;

    // ---------------------------------------------------------------------
    // POST
    // ---------------------------------------------------------------------

    @PreAuthorize( "hasRole('ALL') or hasRole('F_DATAVALUE_ADD')" )
    @RequestMapping( method = RequestMethod.POST, produces = "text/plain" )
    public void saveDataValue(
        @RequestParam String de,
        @RequestParam( required = false ) String co,
        @RequestParam( required = false ) String cc,
        @RequestParam( required = false ) String cp,
        @RequestParam String pe,
        @RequestParam String ou,
        @RequestParam( required = false ) String value,
        @RequestParam( required = false ) String comment,
        @RequestParam( required = false ) boolean followUp, HttpServletResponse response ) throws WebMessageException
    {
        boolean strictPeriods = (Boolean) systemSettingManager.getSystemSetting( KEY_DATA_IMPORT_STRICT_PERIODS, false );
        boolean strictCategoryOptionCombos = (Boolean) systemSettingManager.getSystemSetting( KEY_DATA_IMPORT_STRICT_CATEGORY_OPTION_COMBOS, false );
        boolean strictOrgUnits = (Boolean) systemSettingManager.getSystemSetting( KEY_DATA_IMPORT_STRICT_ORGANISATION_UNITS, false );
        boolean requireCategoryOptionCombo = (Boolean) systemSettingManager.getSystemSetting( KEY_DATA_IMPORT_REQUIRE_CATEGORY_OPTION_COMBO, false );
        
        // ---------------------------------------------------------------------
        // Input validation
        // ---------------------------------------------------------------------

        DataElement dataElement = idObjectManager.get( DataElement.class, de );

        if ( dataElement == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Illegal data element identifier: " + de ) );
        }

        DataElementCategoryOptionCombo categoryOptionCombo = categoryService.getDataElementCategoryOptionCombo( co );

        if ( categoryOptionCombo == null )
        {
            if ( requireCategoryOptionCombo )
            {
                throw new WebMessageException( WebMessageUtils.conflict( "Category option combo is required but is not specified" ) );
            }
            else if ( co != null )
            {
                throw new WebMessageException( WebMessageUtils.conflict( "Illegal category option combo identifier: " + co ) );
            }
            else
            {
                categoryOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
            }
        }

        DataElementCategoryOptionCombo attributeOptionCombo = inputUtils.getAttributeOptionCombo( cc, cp );

        if ( attributeOptionCombo == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Illegal attribute option combo identifier: " + cc + " " + cp ) );
        }

        Period period = PeriodType.getPeriodFromIsoString( pe );

        if ( period == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Illegal period identifier: " + pe ) );
        }

        OrganisationUnit organisationUnit = idObjectManager.get( OrganisationUnit.class, ou );

        if ( organisationUnit == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Illegal organisation unit identifier: " + ou ) );
        }

        boolean inUserHierarchy = organisationUnitService.isInUserHierarchy( organisationUnit );

        if ( !inUserHierarchy )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Organisation unit is not in the hierarchy of the current user: " + ou ) );
        }
        
        boolean invalidFuturePeriod = period.isFuture() && dataElement.getOpenFuturePeriods() <= 0;
        
        if ( invalidFuturePeriod )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "One or more data sets for data element does not allow future periods: " + de ) );
        }

        String valueValid = ValidationUtils.dataValueIsValid( value, dataElement );

        if ( valueValid != null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Invalid value: " + value + ", must match data element type: " + dataElement.getDetailedType() ) );
        }

        String commentValid = ValidationUtils.commentIsValid( comment );

        if ( commentValid != null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Invalid comment: " + comment ) );
        }

        // ---------------------------------------------------------------------
        // Optional constraints
        // ---------------------------------------------------------------------

        if ( strictPeriods && !dataElement.getPeriodTypes().contains( period.getPeriodType() ) )
        {
            throw new WebMessageException( WebMessageUtils.conflict( 
                "Period type of period: " + period.getIsoDate() + " not valid for data element: " + dataElement.getUid() ) );
        }
        
        if ( strictCategoryOptionCombos && !dataElement.getCategoryCombo().getOptionCombos().contains( categoryOptionCombo ) )
        {
            throw new WebMessageException( WebMessageUtils.conflict( 
                "Category option combo: " + categoryOptionCombo.getUid() + " must be part of category combo of data element: " + dataElement.getUid() ) );
        }
        
        if ( strictOrgUnits && !dataElement.hasDataSetOrganisationUnit( organisationUnit ) )
        {
            throw new WebMessageException( WebMessageUtils.conflict( 
                "Data element: " + dataElement.getUid() + " must be assigned through data sets to organisation unit: " + organisationUnit.getUid() ) );
        }
        
        // ---------------------------------------------------------------------
        // Locking validation
        // ---------------------------------------------------------------------

        if ( dataSetService.isLocked( dataElement, period, organisationUnit, null ) )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Data set is locked" ) );
        }

        // ---------------------------------------------------------------------
        // Assemble and save data value
        // ---------------------------------------------------------------------

        String storedBy = currentUserService.getCurrentUsername();

        Date now = new Date();

        DataValue dataValue = dataValueService.getDataValue( dataElement, period, organisationUnit, categoryOptionCombo, attributeOptionCombo );

        if ( dataValue == null )
        {
            dataValue = new DataValue( dataElement, period, organisationUnit, categoryOptionCombo, attributeOptionCombo,
                StringUtils.trimToNull( value ), storedBy, now, StringUtils.trimToNull( comment ) );

            dataValueService.addDataValue( dataValue );
        }
        else
        {
            if ( value == null && ValueType.TRUE_ONLY.equals( dataElement.getValueType() ) )
            {
                if ( comment == null )
                {
                    dataValueService.deleteDataValue( dataValue );
                    return;
                }
                else
                {
                    value = "false";
                }
            }

            if ( value != null )
            {
                dataValue.setValue( StringUtils.trimToNull( value ) );
            }

            if ( comment != null )
            {
                dataValue.setComment( StringUtils.trimToNull( comment ) );
            }

            if ( followUp )
            {
                dataValue.toggleFollowUp();
            }

            dataValue.setLastUpdated( now );
            dataValue.setStoredBy( storedBy );

            dataValueService.updateDataValue( dataValue );
        }
    }

    // ---------------------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------------------

    @PreAuthorize( "hasRole('ALL') or hasRole('F_DATAVALUE_DELETE')" )
    @RequestMapping( method = RequestMethod.DELETE, produces = "text/plain" )
    public void deleteDataValue(
        @RequestParam String de,
        @RequestParam( required = false ) String co,
        @RequestParam( required = false ) String cc,
        @RequestParam( required = false ) String cp,
        @RequestParam String pe,
        @RequestParam String ou, HttpServletResponse response ) throws WebMessageException
    {
        // ---------------------------------------------------------------------
        // Input validation
        // ---------------------------------------------------------------------

        DataElement dataElement = idObjectManager.get( DataElement.class, de );

        if ( dataElement == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Illegal data element identifier: " + de ) );
        }

        DataElementCategoryOptionCombo categoryOptionCombo;

        if ( co != null )
        {
            categoryOptionCombo = categoryService.getDataElementCategoryOptionCombo( co );
        }
        else
        {
            categoryOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        }

        if ( categoryOptionCombo == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Illegal category option combo identifier: " + co ) );
        }

        DataElementCategoryOptionCombo attributeOptionCombo = inputUtils.getAttributeOptionCombo( cc, cp );

        if ( attributeOptionCombo == null )
        {
            return;
        }

        Period period = PeriodType.getPeriodFromIsoString( pe );

        if ( period == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Illegal period identifier: " + pe ) );
        }

        OrganisationUnit organisationUnit = idObjectManager.get( OrganisationUnit.class, ou );

        if ( organisationUnit == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Illegal organisation unit identifier: " + ou ) );
        }

        boolean isInHierarchy = organisationUnitService.isInUserHierarchy( organisationUnit );

        if ( !isInHierarchy )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Organisation unit is not in the hierarchy of the current user: " + ou ) );
        }

        // ---------------------------------------------------------------------
        // Locking validation
        // ---------------------------------------------------------------------

        if ( dataSetService.isLocked( dataElement, period, organisationUnit, null ) )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Data set is locked" ) );
        }

        // ---------------------------------------------------------------------
        // Delete data value
        // ---------------------------------------------------------------------

        DataValue dataValue = dataValueService.getDataValue( dataElement, period, organisationUnit, categoryOptionCombo, attributeOptionCombo );

        if ( dataValue == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Data value cannot be deleted because it does not exist" ) );
        }

        dataValueService.deleteDataValue( dataValue );
    }

    // ---------------------------------------------------------------------
    // GET
    // ---------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.GET )
    public String getDataValue(
        @RequestParam String de,
        @RequestParam( required = false ) String co,
        @RequestParam( required = false ) String cc,
        @RequestParam( required = false ) String cp,
        @RequestParam String pe,
        @RequestParam String ou,
        Model model, HttpServletResponse response ) throws WebMessageException
    {
        // ---------------------------------------------------------------------
        // Input validation
        // ---------------------------------------------------------------------

        DataElement dataElement = idObjectManager.get( DataElement.class, de );

        if ( dataElement == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Illegal data element identifier: " + de ) );
        }

        DataElementCategoryOptionCombo categoryOptionCombo;

        if ( co != null )
        {
            categoryOptionCombo = categoryService.getDataElementCategoryOptionCombo( co );
        }
        else
        {
            categoryOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        }

        if ( categoryOptionCombo == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Illegal category option combo identifier: " + co ) );
        }

        DataElementCategoryOptionCombo attributeOptionCombo = inputUtils.getAttributeOptionCombo( cc, cp );

        if ( attributeOptionCombo == null )
        {
            return null;
        }

        Period period = PeriodType.getPeriodFromIsoString( pe );

        if ( period == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Illegal period identifier: " + pe ) );
        }

        OrganisationUnit organisationUnit = idObjectManager.get( OrganisationUnit.class, ou );

        if ( organisationUnit == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Illegal organisation unit identifier: " + ou ) );
        }

        boolean isInHierarchy = organisationUnitService.isInUserHierarchy( organisationUnit );

        if ( !isInHierarchy )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Organisation unit is not in the hierarchy of the current user: " + ou ) );
        }

        // ---------------------------------------------------------------------
        // Locking validation
        // ---------------------------------------------------------------------

        if ( dataSetService.isLocked( dataElement, period, organisationUnit, null ) )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Data set is locked" ) );
        }

        // ---------------------------------------------------------------------
        // Get data value
        // ---------------------------------------------------------------------

        DataValue dataValue = dataValueService.getDataValue( dataElement, period, organisationUnit, categoryOptionCombo, attributeOptionCombo );

        if ( dataValue == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Data value does not exist" ) );
        }

        List<String> value = new ArrayList<>();
        value.add( dataValue.getValue() );

        model.addAttribute( "model", value );

        return "value";
    }
}
