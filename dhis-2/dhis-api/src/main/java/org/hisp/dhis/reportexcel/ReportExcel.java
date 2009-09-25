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
package org.hisp.dhis.reportexcel;

import java.util.HashSet;
import java.util.Set;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.UserAuthorityGroup;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public abstract class ReportExcel
{
    private int id;

    private String name;

    private int periodRow;

    private int periodColumn;

    private int organisationRow;

    private int organisationColumn;

    private Set<ReportExcelItem> reportExcelItems = new HashSet<ReportExcelItem>();

    private Set<OrganisationUnit> organisationAssocitions = new HashSet<OrganisationUnit>();   

    private String group;

    private String excelTemplateFile;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    public ReportExcel()
    {
        super();
    }

    public ReportExcel( String name, String excelTemplateFile, int periodRow, int periodColumn, int organisationRow,
        int organisationColumn, Set<ReportExcelItem> reportExcelItems, Set<OrganisationUnit> organisationAssocitions,
        Set<UserAuthorityGroup> userRoles, String group )
    {
        super();
        this.name = name;
        this.excelTemplateFile = excelTemplateFile;
        this.periodRow = periodRow;
        this.periodColumn = periodColumn;
        this.organisationRow = organisationRow;
        this.organisationColumn = organisationColumn;
        this.reportExcelItems = reportExcelItems;
        this.organisationAssocitions = organisationAssocitions;      
        this.group = group;
    }

    public ReportExcelItem getReportExcelItem( String name )
    {
        for ( ReportExcelItem reportExcelItem : this.reportExcelItems )
        {
            if ( reportExcelItem.getName().equalsIgnoreCase( name ) )
            {
                return reportExcelItem;
            }
        }

        return null;
    }

    public boolean isCategory()
    {
        return this.getReportType().equalsIgnoreCase( TYPE.CATEGORY );
    }

    public boolean isOrganisationUnitGroupListing()
    {
        return this.getReportType().equalsIgnoreCase( TYPE.ORGANIZATION_GROUP_LISTING );
    }

    public boolean isPeriodColumnListing()
    {
        return this.getReportType().equalsIgnoreCase( TYPE.PERIOD_COLUMN_LISTING );
    }

    public boolean isNormal()
    {
        return this.getReportType().equalsIgnoreCase( TYPE.NORMAL );
    }

    // -------------------------------------------------------------------------
    // Abstract methods
    // -------------------------------------------------------------------------

    public abstract String getReportType();

    // -------------------------------------------------------------------------
    // Internal classes
    // -------------------------------------------------------------------------

    public static class TYPE
    {
        public static final String NORMAL = "NORMAL";

        public static final String CATEGORY = "CATEGORY";

        public static final String PERIOD_COLUMN_LISTING = "PERIOD_COLUMN_LISTING";

        public static final String ORGANIZATION_GROUP_LISTING = "ORGANIZATION_GROUP_LISTING";
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + name.hashCode();
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        ReportExcel other = (ReportExcel) obj;
        if ( name != other.name )
            return false;
        return true;
    }

    // ----------------------------------------------------------------------
    // Getters and setters
    // ----------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public int getPeriodRow()
    {
        return periodRow;
    }

    public void setPeriodRow( int periodRow )
    {
        this.periodRow = periodRow;
    }

    public int getPeriodColumn()
    {
        return periodColumn;
    }

    public void setPeriodColumn( int periodColumn )
    {
        this.periodColumn = periodColumn;
    }

    public int getOrganisationRow()
    {
        return organisationRow;
    }

    public void setOrganisationRow( int organisationRow )
    {
        this.organisationRow = organisationRow;
    }

    public int getOrganisationColumn()
    {
        return organisationColumn;
    }

    public void setOrganisationColumn( int organisationColumn )
    {
        this.organisationColumn = organisationColumn;
    }

    public Set<ReportExcelItem> getReportExcelItems()
    {
        return reportExcelItems;
    }

    public void setReportExcelItems( Set<ReportExcelItem> reportExcelItems )
    {
        this.reportExcelItems = reportExcelItems;
    }

    public Set<OrganisationUnit> getOrganisationAssocitions()
    {
        return organisationAssocitions;
    }

    public void setOrganisationAssocitions( Set<OrganisationUnit> organisationAssocitions )
    {
        this.organisationAssocitions = organisationAssocitions;
    }
   
    public String getGroup()
    {
        return group;
    }

    public void setGroup( String group )
    {
        this.group = group;
    }

    public String getExcelTemplateFile()
    {
        return excelTemplateFile;
    }

    public void setExcelTemplateFile( String excelTemplateFile )
    {
        this.excelTemplateFile = excelTemplateFile;
    }
}
