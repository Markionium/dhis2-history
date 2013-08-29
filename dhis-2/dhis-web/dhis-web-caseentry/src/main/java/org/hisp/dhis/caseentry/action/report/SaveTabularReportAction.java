package org.hisp.dhis.caseentry.action.report;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patientreport.PatientTabularReport;
import org.hisp.dhis.patientreport.PatientTabularReportService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version $SaveTabularReportAction.java May 7, 2012 3:13:11 PM$
 */
public class SaveTabularReportAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientTabularReportService tabularReportService;

    public void setTabularReportService( PatientTabularReportService tabularReportService )
    {
        this.tabularReportService = tabularReportService;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String name;

    private String programId;

    private String programStageId;

    private String startDate;

    private String endDate;

    private List<String> orgunitIds;

    private List<String> item = new ArrayList<String>();

    private String asc;

    private String desc;

    private int level;

    private String facilityLB;

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

    public void setName( String name )
    {
        this.name = name;
    }

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    public void setOrgunitIds( List<String> orgunitIds )
    {
        this.orgunitIds = orgunitIds;
    }

    public void setProgramId( String programId )
    {
        this.programId = programId;
    }

    public void setProgramStageId( String programStageId )
    {
        this.programStageId = programStageId;
    }

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    public void setItem( List<String> item )
    {
        this.item = item;
    }

    public void setAsc( String asc )
    {
        this.asc = asc;
    }

    public void setDesc( String desc )
    {
        this.desc = desc;
    }
    
    public void setLevel( int level )
    {
        this.level = level;
    }

    public void setFacilityLB( String facilityLB )
    {
        this.facilityLB = facilityLB;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        Set<OrganisationUnit> orgunits = new HashSet<OrganisationUnit>(
            organisationUnitService.getOrganisationUnitsByUid( orgunitIds ) );

        Program program = programService.getProgram( programId );
        ProgramStage programStage = programStageService.getProgramStage( programStageId );

        // ---------------------------------------------------------------------
        // Get fixed-attributes
        // ---------------------------------------------------------------------

        PatientTabularReport tabularReport = new PatientTabularReport( name );
        tabularReport.setStartDate( format.parseDate( startDate ) );
        tabularReport.setEndDate( format.parseDate( endDate ) );
        tabularReport.setOrganisationUnits( orgunits );
        tabularReport.setLevel( level );
        tabularReport.setFacilityLB( facilityLB );
        tabularReport.setUser( currentUserService.getCurrentUser() );
        tabularReport.setItems( item );
        tabularReport.setSortByAsc( asc );
        tabularReport.setSortByDesc( desc );
        tabularReport.setProgramStage( programStage );
        tabularReport.setProgram( program );

        tabularReportService.saveOrUpdate( tabularReport );

        return SUCCESS;
    }

}
