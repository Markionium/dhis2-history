package org.hisp.dhis.program;

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

import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.system.deletion.DeletionHandler;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.validation.ValidationCriteria;

/**
 * @author Chau Thu Tran
 * @version ProgramDeleteHandler.java Sep 30, 2010 1:39:15 PM
 */
public class ProgramDeletionHandler
    extends DeletionHandler
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
    // DeletionHandler implementation
    // -------------------------------------------------------------------------

    @Override
    public String getClassName()
    {
        return Program.class.getSimpleName();
    }

    @Override
    public void deleteValidationCriteria( ValidationCriteria validationCriteria )
    {
        Collection<Program> programs = programService.getPrograms( validationCriteria );

        for ( Program program : programs )
        {
            program.getPatientValidationCriteria().remove( validationCriteria );
            programService.updateProgram( program );
        }
    }
    
    @Override
    public void deletePatientIdentifierType( PatientIdentifierType patientIdentifierType )
    {
        Collection<Program> programs = programService.getAllPrograms();

        for ( Program program : programs )
        {
            program.getPatientIdentifierTypes().clear();
            programService.updateProgram( program );
        }
    }
    
    @Override
    public void deletePatientAttribute( PatientAttribute patientAttribute )
    {
        Collection<Program> programs = programService.getAllPrograms();

        for ( Program program : programs )
        {
            program.getPatientAttributes().clear();
            programService.updateProgram( program );
        }
    }
    
    @Override
    public void deleteOrganisationUnit( OrganisationUnit unit )
    {
        //TODO improve performance
        
        Collection<Program> programs = programService.getAllPrograms();
        
        for ( Program program : programs )
        {
            if ( program.getOrganisationUnits().remove( unit ) )
            {
                programService.updateProgram( program );
            }
        }
    }
    
    @Override
    public void deleteUserAuthorityGroup( UserAuthorityGroup group )
    {
        Collection<Program> programs = programService.getAllPrograms();

        for ( Program program : programs )
        {
            if ( program.getUserRoles().remove( group ) )
            {
                programService.updateProgram( program );
            }
        }
    }
}
