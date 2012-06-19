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

package org.hisp.dhis.light.anonymous.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;

import com.opensymphony.xwork2.Action;

/**
 * @author Nguyen Kim Lai
 *
 * @version $ ShowAnonymousFormAction.java $
 */
public class ShowAnonymousFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    Integer programId;

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }
    
    public Integer getProgramId()
    {
        return programId;
    }

    private ProgramStage programStage;

    public ProgramStage getProgramStage()

    {
        return programStage;
    }
    
    private Program program;

    public Program getProgram()

    {
        return program;
    }
    
    List<DataElement> dataElements = new ArrayList<DataElement>();
    
    public List<DataElement> getDataElements()
    {
        return dataElements;
    }
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
    
    @Override
    public String execute()
        throws Exception
    {
        
        program = programService.getProgram( programId );
        
        programStage = program.getProgramStages().iterator().next();
        
        Set<ProgramStageDataElement> programStageDataElement = null;
    
        programStageDataElement = programStage.getProgramStageDataElements();  
        
        if( programStageDataElement != null )
        {    
            for( ProgramStageDataElement each: programStageDataElement )
            {
                dataElements.add( each.getDataElement() );
            }
        }
        return SUCCESS;
    }

}
