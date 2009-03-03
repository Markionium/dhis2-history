package org.hisp.dhis.rt.dataaccess;

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

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Lars Helge Overland
 * @version $Id: ReportDataAccess.java 2871 2007-02-20 16:04:11Z andegje $
 */
public interface ReportDataAccess
{
    String BEAN_ID = ReportDataAccess.class.getName();

    /**
     * Retrieves all DataElements.
     * 
     * @throws ReportDataAccessException
     */
    Collection<DataElement> getAllDataElements()
        throws ReportDataAccessException;

    /**
     * Retrieves all DataElementGroups.
     * 
     * @throws ReportDataAccessException
     */
    Collection<DataElementGroup> getAllDataElementGroups()
        throws ReportDataAccessException;

    /**
     * Retrieves members of DataElementGroup.
     * 
     * @throws ReportDataAccessException
     */
    Collection<DataElement> getMembersOfDataElementGroup( int dataElementGroupId )
        throws ReportDataAccessException;

    /**
     * Retrieves all Indicators.
     * 
     * @throws ReportDataAccessException
     */
    Collection<Indicator> getAllIndicators()
        throws ReportDataAccessException;

    /**
     * Retrieves all IndicatorGroups.
     * 
     * @throws ReportDataAccessException
     */
    Collection<IndicatorGroup> getAllIndicatorGroups()
        throws ReportDataAccessException;

    /**
     * Retrieves members of IndicatorGroup.
     * 
     * @throws ReportDataAccessException
     */
    Collection<Indicator> getMembersOfIndicatorGroup( int indicatorGroupId )
        throws ReportDataAccessException;

    /**
     * Calculates the aggregated value of the given DataElement and source
     * between start date and end date.
     * 
     * @throws ReportDataAccessException
     */
    double getAggregatedDataValue( int dataElementId, Date startDate, Date endDate, String source )
        throws ReportDataAccessException;

    /**
     * Calculates the aggregated value of the given Indicator and source between
     * start date and end date.
     * 
     * @throws ReportDataAccessException
     */
    double getAggregatedIndicatorValue( int indicatorId, Date startDate, Date endDate, String source )
        throws ReportDataAccessException;

    /**
     * Gets the name of the Organisation Unit with the given identifier.
     * 
     * @throws ReportDataAccessException
     */
    String getOrganisationUnitName( int organisationUnitId )
        throws ReportDataAccessException;

    /**
     * Gets the short-name of the Organisation Unit with the given identifier.
     * 
     * @throws ReportDataAccessException
     */
    String getOrganisationUnitShortName( int organisationUnitId )
        throws ReportDataAccessException;

    /**
     * Gets the name of the DataElement with the given identifier.
     * 
     * @throws ReportDataAccessException
     */
    String getDataElementName( int dataElementId )
        throws ReportDataAccessException;

    /**
     * Gets the short name of the DataElement with the given identifier.
     * 
     * @throws ReportDataAccessException
     */
    String getDataElementShortName( int dataElementId )
        throws ReportDataAccessException;

    /**
     * Gets the name of the Indicator with the given identifier.
     * 
     * @throws ReportDataAccessException
     */
    String getIndicatorName( int indicatorId )
        throws ReportDataAccessException;

    /**
     * Gets the short name of the Indicator with the given identifier.
     * 
     * @throws ReportDataAccessException
     */
    String getIndicatorShortName( int indicatorId )
        throws ReportDataAccessException;

    /**
     * Add this menthod for Hue's report.
     */
    Set<OrganisationUnit> getOrganisationUnitChildren( int organisationUnitId )
        throws ReportDataAccessException;

    int getOrganisationUnitByName( String name )
        throws ReportDataAccessException;
}
