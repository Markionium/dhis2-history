/*
 * Copyright (c) 2004-2009, University of Oslo
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
package org.hisp.dhis.patient.action.patient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.patient.state.SelectedStateManager;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramInstanceStage;
import org.hisp.dhis.program.ProgramInstanceStageService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.system.util.DateUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class SaveProgramEnrollmentAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }    

    private ProgramInstanceStageService programInstanceStageService;

    public void setProgramInstanceStageService( ProgramInstanceStageService programInstanceStageService )
    {
        this.programInstanceStageService = programInstanceStageService;
    }

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }

    private Patient patient;

    public Patient getPatient()
    {
        return patient;
    }

    private ProgramInstance programInstance;

    public ProgramInstance getProgramInstance()
    {
        return programInstance;
    }

    private Integer programId;

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    public Integer getProgramId()
    {
        return programId;
    }

    private Integer programInstanceId;

    public Integer getProgramInstanceId()
    {
        return programInstanceId;
    }

    public void setProgramInstanceId( Integer programInstanceId )
    {
        this.programInstanceId = programInstanceId;
    }

    private String enrollmentDate;

    public void setEnrollmentDate( String enrollmentDate )
    {
        this.enrollmentDate = enrollmentDate;
    }

    private String dateOfIncident;

    public void setDateOfIncident( String dateOfIncident )
    {
        this.dateOfIncident = dateOfIncident;
    }

    private Integer age;

    public Integer getAge()
    {
        return age;
    }

    private Collection<Program> programs = new ArrayList<Program>();

    public Collection<Program> getPrograms()
    {
        return programs;
    }

    private Collection<ProgramInstanceStage> programInstanceStages = new ArrayList<ProgramInstanceStage>();

    public Collection<ProgramInstanceStage> getProgramInstanceStages()
    {
        return programInstanceStages;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        patient = selectedStateManager.getSelectedPatient();

        Program program = selectedStateManager.getSelectedProgram();

        programId = program.getId();

        age = patient.getAge();

        programs = programService.getAllPrograms();

        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( patient, program,
            false );

        if ( programInstances.iterator().hasNext() )
        {
            programInstance = programInstances.iterator().next();
        }

        if ( programInstance == null )
        {
            programInstance = new ProgramInstance();
            programInstance.setEnrollmentDate( format.parseDate( enrollmentDate ) );
            programInstance.setDateOfIncident( format.parseDate( dateOfIncident ) );
            programInstance.setProgram( program );
            programInstance.setPatient( patient );
            programInstance.setCompleted( false );

            programInstanceService.addProgramInstance( programInstance );

            patient.getPrograms().add( program );
            patientService.updatePatient( patient );
            
            for( ProgramStage programStage : program.getProgramStages() )
            {
                ProgramInstanceStage programInstanceStage = new ProgramInstanceStage();
                programInstanceStage.setProgramInstance( programInstance );
                programInstanceStage.setProgramStage( programStage );
                programInstanceStage.setStageInProgram( programStage.getStageInProgram() );
                
                Date dueDate = DateUtils.getDateAfterAddition( format.parseDate( dateOfIncident ), programStage.getMinDaysFromStart() ) ;
                    
                programInstanceStage.setDueDate( dueDate );
                
                programInstanceStageService.addProgramInstanceStage( programInstanceStage );
                
                programInstanceStages.add( programInstanceStage );
            }
        }

        else
        {
            programInstance.setEnrollmentDate( format.parseDate( enrollmentDate ) );
            programInstance.setDateOfIncident( format.parseDate( dateOfIncident ) );           

            programInstanceService.updateProgramInstance( programInstance );
            
            for( ProgramInstanceStage programInstanceStage : programInstance.getProgramInstanceStages() )
            {
                
                Date dueDate = DateUtils.getDateAfterAddition( format.parseDate( dateOfIncident ), programInstanceStage.getProgramStage().getMinDaysFromStart() ) ;
                
                programInstanceStage.setDueDate( dueDate );
                
                programInstanceStageService.updateProgramInstanceStage( programInstanceStage );
                
                programInstanceStages.add( programInstanceStage );
            }
        }

        return SUCCESS;
    }
}
