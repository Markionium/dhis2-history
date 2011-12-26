package org.hisp.dhis.importexport.action.exp;

/*
 * Copyright (c) 2004-2012, University of Oslo
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.HrDataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Lars Helge Overland
 * @version $Id: GetOptionsAction.java 2869 2007-02-20 14:26:09Z andegje $
 */
public class GetExportOptionsAction
    extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
	private HrDataSetService hrDataSetService;
	
	public void setHrDataSetService( HrDataSetService hrDataSetService )
	{
		this.hrDataSetService = hrDataSetService;
	}
	
	private ProgramService programService;
	
	public void setProgramService( ProgramService programService )
	{
		this.programService = programService;
	}

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private String exportFormat;

    public String getExportFormat()
    {
        return exportFormat;
    }

    public void setExportFormat( String format )
    {
        this.exportFormat = format;
    }    
    
    private List<HrDataSet> hrDataSets;
    
    public List<HrDataSet> getHrDataSets()
    {
    	return hrDataSets;
    }
    
    private List<Program> programs;
    
    public List<Program> getPrograms()
    {
    	return programs;
    }
    
    private List<DataSet> dataSets;

    public List<DataSet> getDataSets()
    {
        return dataSets;
    }
    
    private List<OrganisationUnitLevel> levels;

    public List<OrganisationUnitLevel> getLevels()
    {
        return levels;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        selectionTreeManager.clearSelectedOrganisationUnits();
        
        dataSets = new ArrayList<DataSet>( dataSetService.getAllDataSets() );
        
        hrDataSets = new ArrayList<HrDataSet>( hrDataSetService.getAllHrDataSets() );
        
        levels = new ArrayList<OrganisationUnitLevel>( organisationUnitService.getOrganisationUnitLevels() );

        return SUCCESS;
    }
}
