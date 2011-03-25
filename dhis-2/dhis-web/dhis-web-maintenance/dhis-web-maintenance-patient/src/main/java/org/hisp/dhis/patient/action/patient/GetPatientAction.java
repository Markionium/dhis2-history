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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeGroupService;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class GetPatientAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientService patientService;

    private PatientIdentifierService patientIdentifierService;

    private ProgramService programService;

    private PatientAttributeValueService patientAttributeValueService;

    private PatientAttributeService patientAttributeService;

    private PatientAttributeGroupService patientAttributeGroupService;

    private PatientIdentifierTypeService patientIdentifierTypeService;

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    private Patient patient;

    private PatientIdentifier patientIdentifier;

    private Collection<Program> programs;

    private Map<Integer, String> patientAttributeValueMap = new HashMap<Integer, String>();

    private Collection<PatientAttribute> noGroupAttributes;

    private Collection<PatientAttributeGroup> attributeGroups;

    private Collection<PatientIdentifierType> identifierTypes;

    private Map<Integer, String> identiferMap;

    private String childContactName;

    private String childContactType;

    private String systemIdentifier;

    private String benicode;

    private String yearcode;

    private String progcode;

    private String orgunitcode;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    { 
        try
    {

            
        patient = patientService.getPatient( id );
        
        patientIdentifier = patientIdentifierService.getPatientIdentifier( patient );
        
        identifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();
        
        identiferMap = new HashMap<Integer, String>();

        PatientIdentifierType idType = null;
        Patient representative = patient.getRepresentative();
        
        if ( patient.isUnderAge() && representative != null )
        {
            for ( PatientIdentifier representativeIdentifier : representative.getIdentifiers() )
            {
                if ( representativeIdentifier.getIdentifierType() != null
                    && representativeIdentifier.getIdentifierType().isRelated() )
                {
                    identiferMap.put( representativeIdentifier.getIdentifierType().getId(),
                        representativeIdentifier.getIdentifier() );
                }
            }
        }
       
        for ( PatientIdentifier identifier : patient.getIdentifiers() )
        {
            idType = identifier.getIdentifierType();

            if ( idType != null )
            {
                identiferMap.put( identifier.getIdentifierType().getId(), identifier.getIdentifier() );

                if ( idType.getFormat().equals( "State Format" ) )
                {
                    String iden = identifier.getIdentifier();
                    benicode = iden.substring( 12, 16 );
                    yearcode = iden.substring( 10, 12 );
                    progcode = iden.substring( 9, 10 );
                    orgunitcode = iden.substring( 0, 9 );
                }
            }
            else
                systemIdentifier = identifier.getIdentifier();
        }
        
        for ( PatientAttribute patientAttribute : patient.getAttributes() )
        {
            patientAttributeValueMap.put( patientAttribute.getId(), PatientAttributeValue.UNKNOWN );
        }

        Collection<PatientAttributeValue> patientAttributeValues = patientAttributeValueService
            .getPatientAttributeValues( patient );
        
        for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
        {
            if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( patientAttributeValue.getPatientAttribute()
                .getValueType() ) )
            {
                patientAttributeValueMap.put( patientAttributeValue.getPatientAttribute().getId(),
                    patientAttributeValue.getPatientAttributeOption().getName() );
            }
            else
            {
                patientAttributeValueMap.put( patientAttributeValue.getPatientAttribute().getId(),
                    patientAttributeValue.getValue() );
            }
        }

        programs = programService.getAllPrograms();
System.out.println("\n programs : " + programs);
        noGroupAttributes = patientAttributeService.getPatientAttributesNotGroup();
System.out.println("\n noGroupAttributes : " + noGroupAttributes);
        attributeGroups = patientAttributeGroupService.getAllPatientAttributeGroups();
System.out.println("\n attributeGroups : " + attributeGroups);

        }catch(Exception ex){
            ex.printStackTrace();
        }
        return SUCCESS;

    }

    // -----------------------------------------------------------------------------
    // Getter / Setter
    // -----------------------------------------------------------------------------

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    public void setPatientAttributeGroupService( PatientAttributeGroupService patientAttributeGroupService )
    {
        this.patientAttributeGroupService = patientAttributeGroupService;
    }

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public Patient getPatient()
    {
        return patient;
    }

    public PatientIdentifier getPatientIdentifier()
    {
        return patientIdentifier;
    }

    public Collection<Program> getPrograms()
    {
        return programs;
    }

    public Map<Integer, String> getPatientAttributeValueMap()
    {
        return patientAttributeValueMap;
    }

    public Collection<PatientAttribute> getNoGroupAttributes()
    {
        return noGroupAttributes;
    }

    public Collection<PatientAttributeGroup> getAttributeGroups()
    {
        return attributeGroups;
    }

    public Collection<PatientIdentifierType> getIdentifierTypes()
    {
        return identifierTypes;
    }

    public Map<Integer, String> getIdentiferMap()
    {
        return identiferMap;
    }

    public String getChildContactName()
    {
        return childContactName;
    }

    public String getChildContactType()
    {
        return childContactType;
    }

    public String getSystemIdentifier()
    {
        return systemIdentifier;
    }

    public String getBenicode()
    {
        return benicode;
    }

    public String getOrgunitcode()
    {
        return orgunitcode;
    }

    public String getProgcode()
    {
        return progcode;
    }

    public String getYearcode()
    {
        return yearcode;
    }
}
