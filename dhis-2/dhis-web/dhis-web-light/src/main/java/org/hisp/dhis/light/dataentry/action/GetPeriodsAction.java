/*
 * Copyright (c) 2004-2011, University of Oslo
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
package org.hisp.dhis.light.dataentry.action;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;

import com.opensymphony.xwork2.Action;

/**
 * @author mortenoh
 */
public class GetPeriodsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private CompleteDataSetRegistrationService registrationService;

    public void setRegistrationService( CompleteDataSetRegistrationService registrationService )
    {
        this.registrationService = registrationService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    public Integer getOrganisationUnitId()
    {
        return organisationUnitId;
    }

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    public Integer getDataSetId()
    {
        return dataSetId;
    }

    private Map<Period, Boolean> periodCompletedMap = new HashMap<Period, Boolean>();

    public Map<Period, Boolean> getPeriodCompletedMap()
    {
        return periodCompletedMap;
    }

    private List<Period> periods = new ArrayList<Period>();

    public List<Period> getPeriods()
    {
        return periods;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    
    @Override
    public String execute()
    {
        if ( dataSetId != null )
        {
            OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );
            DataSet dataSet = dataSetService.getDataSet( dataSetId );
            CalendarPeriodType periodType = (CalendarPeriodType) dataSet.getPeriodType();
            periods = periodType.generatePeriods( new Date() );
            Calendar cal = new GregorianCalendar();
            Date dateEnd = cal.getTime();
            cal.add( Calendar.MONTH, -12 );
            Date dateStart = cal.getTime();

            //FilterUtils.filter( periods, new PastAndCurrentPeriodFilter() );
            Collection<Period> periodsBetweenDates = periodService.getPeriodsBetweenDates( periodType, dateStart, dateEnd );
            periods = (List<Period>) periodsBetweenDates;
            for ( Period period : periods )
            {
                period.setName( format.formatPeriod( period ) );

                CompleteDataSetRegistration registration = registrationService.getCompleteDataSetRegistration( dataSet,
                    period, organisationUnit );

                periodCompletedMap.put( period, registration != null ? true : false );
            }

        }

        return SUCCESS;
    }
}
