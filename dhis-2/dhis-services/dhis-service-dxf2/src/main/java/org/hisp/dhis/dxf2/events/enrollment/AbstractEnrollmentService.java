package org.hisp.dhis.dxf2.events.enrollment;

/*
 * Copyright (c) 2004-2014, University of Oslo
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
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.dxf2.events.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.dxf2.events.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.dxf2.importsummary.ImportStatus;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public abstract class AbstractEnrollmentService
    implements EnrollmentService
{
    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private TrackedEntityInstanceService trackedEntityInstanceService;

    @Autowired
    private org.hisp.dhis.trackedentity.TrackedEntityInstanceService entityInstanceService;

    @Autowired
    private I18nManager i18nManager;

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    @Override
    public Enrollments getEnrollments()
    {
        List<Program> programs = getProgramsWithRegistration();

        List<ProgramInstance> programInstances = new ArrayList<ProgramInstance>(
            programInstanceService.getProgramInstances( programs ) );

        return getEnrollments( programInstances );
    }

    @Override
    public Enrollments getEnrollments( EnrollmentStatus status )
    {
        List<Program> programs = getProgramsWithRegistration();

        List<ProgramInstance> programInstances = new ArrayList<ProgramInstance>(
            programInstanceService.getProgramInstances( programs, status.getValue() ) );

        return getEnrollments( programInstances );
    }

    @Override
    public Enrollments getEnrollments( TrackedEntityInstance trackedEntityInstance )
    {
        org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstance = getTrackedEntityInstance( trackedEntityInstance.getTrackedEntityInstance() );
        return getEnrollments( entityInstance );
    }

    @Override
    public Enrollments getEnrollments( TrackedEntityInstance trackedEntityInstance, EnrollmentStatus status )
    {
        org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstance = getTrackedEntityInstance( trackedEntityInstance.getTrackedEntityInstance() );
        return getEnrollments( entityInstance, status );
    }

    @Override
    public Enrollments getEnrollments( org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstance )
    {
        List<ProgramInstance> programInstances = new ArrayList<ProgramInstance>( entityInstance.getProgramInstances() );

        return getEnrollments( programInstances );
    }

    @Override
    public Enrollments getEnrollments( org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstance, EnrollmentStatus status )
    {
        List<ProgramInstance> programInstances = new ArrayList<ProgramInstance>(
            programInstanceService.getProgramInstances( entityInstance, status.getValue() ) );

        return getEnrollments( programInstances );
    }

    @Override
    public Enrollments getEnrollments( Program program )
    {
        List<ProgramInstance> programInstances = new ArrayList<ProgramInstance>(
            programInstanceService.getProgramInstances( program ) );
        return getEnrollments( programInstances );
    }

    @Override
    public Enrollments getEnrollments( Program program, EnrollmentStatus status )
    {
        List<ProgramInstance> programInstances = new ArrayList<ProgramInstance>(
            programInstanceService.getProgramInstances( program, status.getValue() ) );

        return getEnrollments( programInstances );
    }

    @Override
    public Enrollments getEnrollments( OrganisationUnit organisationUnit )
    {
        List<Program> programs = getProgramsWithRegistration();
        List<ProgramInstance> programInstances = new ArrayList<ProgramInstance>(
            programInstanceService.getProgramInstances( programs, organisationUnit ) );

        return getEnrollments( programInstances );
    }

    @Override
    public Enrollments getEnrollments( OrganisationUnit organisationUnit, EnrollmentStatus status )
    {
        List<Program> programs = getProgramsWithRegistration();
        List<ProgramInstance> programInstances = new ArrayList<ProgramInstance>(
            programInstanceService.getProgramInstances( programs, organisationUnit, status.getValue() ) );

        return getEnrollments( programInstances );
    }

    @Override
    public Enrollments getEnrollments( Program program, OrganisationUnit organisationUnit )
    {
        return getEnrollments( programInstanceService.getProgramInstances( program, organisationUnit, 0, null ) );
    }

    @Override
    public Enrollments getEnrollments( Program program, TrackedEntityInstance trackedEntityInstance )
    {
        org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstance = getTrackedEntityInstance( trackedEntityInstance.getTrackedEntityInstance() );
        return getEnrollments( programInstanceService.getProgramInstances( entityInstance, program ) );
    }

    @Override
    public Enrollments getEnrollments( Program program, TrackedEntityInstance trackedEntityInstance, EnrollmentStatus status )
    {
        org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstance = getTrackedEntityInstance( trackedEntityInstance.getTrackedEntityInstance() );
        return getEnrollments( programInstanceService.getProgramInstances( entityInstance, program, status.getValue() ) );
    }

    @Override
    public Enrollments getEnrollments( Collection<ProgramInstance> programInstances )
    {
        Enrollments enrollments = new Enrollments();

        for ( ProgramInstance programInstance : programInstances )
        {
            // check for null, both for pi, and for pi.entityInstance, there are DBs
            // out there where trackedentityinstanceid == null
            // even if the program is of type 1/2.
            if ( programInstance != null && programInstance.getEntityInstance() != null )
            {
                enrollments.getEnrollments().add( getEnrollment( programInstance ) );
            }
        }

        return enrollments;
    }

    @Override
    public Enrollment getEnrollment( String id )
    {
        ProgramInstance programInstance = programInstanceService.getProgramInstance( id );

        return programInstance != null ? getEnrollment( programInstance ) : null;
    }

    @Override
    public Enrollment getEnrollment( ProgramInstance programInstance )
    {
        Enrollment enrollment = new Enrollment();

        enrollment.setEnrollment( programInstance.getUid() );
        enrollment.setTrackedEntityInstance( programInstance.getEntityInstance().getUid() );
        enrollment.setProgram( programInstance.getProgram().getUid() );
        enrollment.setStatus( EnrollmentStatus.fromInt( programInstance.getStatus() ) );
        enrollment.setDateOfEnrollment( programInstance.getEnrollmentDate() );
        enrollment.setDateOfIncident( programInstance.getDateOfIncident() );

        return enrollment;
    }

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    @Override
    public ImportSummary addEnrollment( Enrollment enrollment )
    {
        org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstance = getTrackedEntityInstance( enrollment.getTrackedEntityInstance() );
        TrackedEntityInstance trackedEntityInstance = trackedEntityInstanceService.getTrackedEntityInstance( entityInstance );
        Program program = getProgram( enrollment.getProgram() );

        Enrollments enrollments = getEnrollments( program, trackedEntityInstance, EnrollmentStatus.ACTIVE );

        if ( !enrollments.getEnrollments().isEmpty() )
        {
            ImportSummary importSummary = new ImportSummary( ImportStatus.ERROR, "TrackedEntityInstance " + trackedEntityInstance.getTrackedEntityInstance()
                + " already have an active enrollment in program " + program.getUid() );
            importSummary.getImportCount().incrementIgnored();

            return importSummary;
        }

        ProgramInstance programInstance = programInstanceService.enrollTrackedEntityInstance( entityInstance, program,
            enrollment.getDateOfEnrollment(), enrollment.getDateOfIncident(), entityInstance.getOrganisationUnit(),
            i18nManager.getI18nFormat() );

        if ( programInstance == null )
        {
            return new ImportSummary( ImportStatus.ERROR, "Could not enroll TrackedEntityInstance " + enrollment.getTrackedEntityInstance()
                + " into program " + enrollment.getProgram() );
        }

        ImportSummary importSummary = new ImportSummary( ImportStatus.SUCCESS );
        importSummary.setReference( programInstance.getUid() );
        importSummary.setDataValueCount( null );
        importSummary.getImportCount().incrementImported();

        return importSummary;
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    @Override
    public ImportSummary updateEnrollment( Enrollment enrollment )
    {
        if ( enrollment == null || enrollment.getEnrollment() == null )
        {
            ImportSummary importSummary = new ImportSummary( ImportStatus.ERROR,
                "No enrollment or enrollment ID was supplied" );
            importSummary.getImportCount().incrementIgnored();

            return importSummary;
        }

        ProgramInstance programInstance = programInstanceService.getProgramInstance( enrollment.getEnrollment() );

        if ( programInstance == null )
        {
            ImportSummary importSummary = new ImportSummary( ImportStatus.ERROR, "Enrollment ID was not valid." );
            importSummary.getImportCount().incrementIgnored();

            return importSummary;
        }

        org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstance = getTrackedEntityInstance( enrollment.getTrackedEntityInstance() );
        Program program = getProgram( enrollment.getProgram() );

        programInstance.setProgram( program );
        programInstance.setEntityInstance( entityInstance );
        programInstance.setDateOfIncident( enrollment.getDateOfIncident() );
        programInstance.setEnrollmentDate( enrollment.getDateOfEnrollment() );

        if ( programInstance.getStatus() != enrollment.getStatus().getValue() )
        {
            if ( enrollment.getStatus().equals( EnrollmentStatus.CANCELLED ) )
            {
                programInstanceService.cancelProgramInstanceStatus( programInstance );
            }
            else if ( enrollment.getStatus().equals( EnrollmentStatus.COMPLETED ) )
            {
                programInstanceService.completeProgramInstanceStatus( programInstance, i18nManager.getI18nFormat() );
            }
            else
            {
                ImportSummary importSummary = new ImportSummary( ImportStatus.ERROR,
                    "Re-enrollment is not allowed, please create a new enrollment." );
                importSummary.getImportCount().incrementIgnored();

                return importSummary;
            }
        }

        programInstanceService.updateProgramInstance( programInstance );

        ImportSummary importSummary = new ImportSummary( ImportStatus.SUCCESS );
        importSummary.setReference( enrollment.getEnrollment() );
        importSummary.setDataValueCount( null );
        importSummary.getImportCount().incrementImported();

        return importSummary;
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @Override
    public void deleteEnrollment( Enrollment enrollment )
    {
        ProgramInstance programInstance = programInstanceService.getProgramInstance( enrollment.getEnrollment() );
        Assert.notNull( programInstance );

        programInstanceService.deleteProgramInstance( programInstance );
    }

    @Override
    public void cancelEnrollment( Enrollment enrollment )
    {
        ProgramInstance programInstance = programInstanceService.getProgramInstance( enrollment.getEnrollment() );
        Assert.notNull( programInstance );

        programInstanceService.cancelProgramInstanceStatus( programInstance );
    }

    @Override
    public void completeEnrollment( Enrollment enrollment )
    {
        ProgramInstance programInstance = programInstanceService.getProgramInstance( enrollment.getEnrollment() );
        Assert.notNull( programInstance );

        programInstanceService.completeProgramInstanceStatus( programInstance, i18nManager.getI18nFormat() );
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    private List<Program> getProgramsWithRegistration()
    {
        List<Program> programs = new ArrayList<Program>();
        programs.addAll( programService.getPrograms( Program.SINGLE_EVENT_WITH_REGISTRATION ) );
        programs.addAll( programService.getPrograms( Program.MULTIPLE_EVENTS_WITH_REGISTRATION ) );

        return programs;
    }

    private org.hisp.dhis.trackedentity.TrackedEntityInstance getTrackedEntityInstance( String trackedEntityInstance )
    {
        org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstance = entityInstanceService.getTrackedEntityInstance( trackedEntityInstance );

        if ( entityInstance == null )
        {
            throw new IllegalArgumentException( "TrackedEntityInstance does not exist." );
        }

        return entityInstance;
    }

    private Program getProgram( String id )
    {
        Program program = programService.getProgram( id );

        if ( program == null )
        {
            throw new IllegalArgumentException( "Program does not exist." );
        }

        return program;
    }
}