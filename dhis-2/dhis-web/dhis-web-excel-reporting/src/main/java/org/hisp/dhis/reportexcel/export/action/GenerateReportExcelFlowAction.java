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
package org.hisp.dhis.reportexcel.export.action;

import org.hisp.dhis.reportexcel.ReportExcel;
import org.hisp.dhis.reportexcel.ReportExcelService;
import org.hisp.dhis.reportexcel.period.generic.PeriodGenericManager;
import org.hisp.dhis.reportexcel.state.SelectionManager;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class GenerateReportExcelFlowAction
    implements Action
{

    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private ReportExcelService reportService;

    private SelectionManager selectionManager;

    private PeriodGenericManager periodGenericManager;

    // -------------------------------------------
    // Input & Output
    // -------------------------------------------

    private Integer reportId;

    private Integer periodIndex;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------

    public void setReportId( Integer reportId )
    {
        this.reportId = reportId;
    }

    public void setSelectionManager( SelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    public void setPeriodGenericManager( PeriodGenericManager periodGenericManager )
    {
        this.periodGenericManager = periodGenericManager;
    }

    public void setPeriodIndex( Integer periodIndex )
    {
        this.periodIndex = periodIndex;
    }

    public void setReportService( ReportExcelService reportService )
    {
        this.reportService = reportService;
    }

    // -------------------------------------------
    // Action implementation
    // -------------------------------------------

    public String execute()
        throws Exception
    {

        ReportExcel reportExcel = reportService.getReportExcel( reportId );

        periodGenericManager.setSelectedPeriodIndex( periodIndex );

        selectionManager.setSelectedReportId( reportId );

        return reportExcel.getReportType();
    }

}
