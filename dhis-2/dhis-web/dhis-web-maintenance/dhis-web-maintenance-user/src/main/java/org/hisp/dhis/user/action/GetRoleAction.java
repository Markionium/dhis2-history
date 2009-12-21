package org.hisp.dhis.user.action;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.comparator.DataSetNameComparator;
import org.hisp.dhis.reportexcel.ReportExcel;
import org.hisp.dhis.reportexcel.ReportExcelService;
import org.hisp.dhis.reportexcel.comparator.ReportExcelNameComparator;
import org.hisp.dhis.security.authority.SystemAuthoritiesProvider;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserStore;

import com.opensymphony.xwork2.Action;

/**
 * @author Thanh Nguyen
 * @version $Id: GetRoleAction.java 6261 2008-11-11 16:48:05Z larshelg $
 */
public class GetRoleAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserStore userStore;

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private SystemAuthoritiesProvider authoritiesProvider;

    public void setAuthoritiesProvider( SystemAuthoritiesProvider authoritiesProvider )
    {
        this.authoritiesProvider = authoritiesProvider;
    }

    private ReportExcelService reportExcelService;

    public void setReportExcelService( ReportExcelService reportExcelService )
    {
        this.reportExcelService = reportExcelService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private UserAuthorityGroup userAuthorityGroup;

    public UserAuthorityGroup getUserAuthorityGroup()
    {
        return userAuthorityGroup;
    }

    private List<DataSet> availableDataSets;

    public List<DataSet> getAvailableDataSets()
    {
        return availableDataSets;
    }

    private List<DataSet> roleDataSets;

    public List<DataSet> getRoleDataSets()
    {
        return roleDataSets;
    }

    private List<String> availableAuthorities;

    public List<String> getAvailableAuthorities()
    {
        return availableAuthorities;
    }

    private List<String> roleAuthorities;

    public List<String> getRoleAuthorities()
    {
        return roleAuthorities;
    }

    private List<ReportExcel> availableReportExcels;

    public List<ReportExcel> getAvailableReportExcels()
    {
        return availableReportExcels;
    }

    private List<ReportExcel> reportExcels;

    public List<ReportExcel> getReportExcels()
    {
        return reportExcels;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        userAuthorityGroup = userStore.getUserAuthorityGroup( id );
        
        // ---------------------------------------------------------------------
        // DataSets
        // ---------------------------------------------------------------------

        availableDataSets = new ArrayList<DataSet>( dataSetService.getAllDataSets() );

        availableDataSets.removeAll( userAuthorityGroup.getDataSets() );

        Collections.sort( availableDataSets, new DataSetNameComparator() );

        roleDataSets = new ArrayList<DataSet>( userAuthorityGroup.getDataSets() );

        Collections.sort( roleDataSets, new DataSetNameComparator() );

        // ---------------------------------------------------------------------
        // Authorities
        // ---------------------------------------------------------------------

        availableAuthorities = new ArrayList<String>( authoritiesProvider.getSystemAuthorities() );

        availableAuthorities.removeAll( userAuthorityGroup.getAuthorities() );

        Collections.sort( availableAuthorities );

        roleAuthorities = new ArrayList<String>( userAuthorityGroup.getAuthorities() );

        Collections.sort( roleAuthorities );

        // ---------------------------------------------------------------------
        // Report Excel
        // ---------------------------------------------------------------------

        availableReportExcels = new ArrayList<ReportExcel>( reportExcelService.getALLReportExcel() );

        reportExcels = new ArrayList<ReportExcel>( userAuthorityGroup.getReportExcels() );

        availableReportExcels.removeAll( reportExcels );

        Collections.sort( availableReportExcels, new ReportExcelNameComparator() );

        Collections.sort( reportExcels, new ReportExcelNameComparator() );

        return SUCCESS;
    }
}
