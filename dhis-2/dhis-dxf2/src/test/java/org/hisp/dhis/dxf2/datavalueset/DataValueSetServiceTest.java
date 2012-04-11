package org.hisp.dhis.dxf2.datavalueset;

/*
 * Copyright (c) 2012, University of Oslo
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

import java.util.Collection;

import org.hisp.dhis.DhisTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import static junit.framework.Assert.*;

public class DataValueSetServiceTest
    extends DhisTest
{
    @Autowired
    private DataElementService dataElementService;
    
    @Autowired
    private DataElementCategoryService categoryService;
    
    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private PeriodService periodService;
    
    @Autowired
    private DataValueSetService dataValueSetService;
    
    @Autowired
    private DataValueService dataValueService;
    
    @Autowired
    private CompleteDataSetRegistrationService registrationService;
    
    private DataElement deA;
    private DataElement deB;
    private DataElement deC;
    private DataSet dsA;
    private OrganisationUnit ouA;
    private Period peA;
    private DataElementCategoryOptionCombo optionComboA;
    
    @Override
    public void setUpTest()
    {
        deA = createDataElement( 'A' );
        deB = createDataElement( 'B' );
        deC = createDataElement( 'C' );
        dsA = createDataSet( 'A', new MonthlyPeriodType() );
        ouA = createOrganisationUnit( 'A' );
        peA = createPeriod( getDate( 2012, 1, 1 ), getDate( 2012, 1, 31 ) );
        optionComboA = categoryService.getDefaultDataElementCategoryOptionCombo();
        
        deA.setUid( "f7n9E0hX8qk" );
        deB.setUid( "Ix2HsbDMLea" );
        deC.setUid( "eY5ehpbEsB7" );
        dsA.setUid( "pBOMPrpg1QX" );
        ouA.setUid( "DiszpKrYNg8" );
        
        dataElementService.addDataElement( deA );
        dataElementService.addDataElement( deB );
        dataElementService.addDataElement( deC );
        dataSetService.addDataSet( dsA );
        organisationUnitService.addOrganisationUnit( ouA );
        periodService.addPeriod( peA );
    }
    
    @Test
    public void testImport()
        throws Exception
    {
        ImportSummary summary = dataValueSetService.saveDataValueSet( new ClassPathResource( "dataValueSetA.xml" ).getInputStream() );
        
        assertNotNull( summary );
        
        Collection<DataValue> dataValues = dataValueService.getAllDataValues();
        
        assertNotNull( dataValues );
        assertEquals( 3, dataValues.size() );
        assertTrue( dataValues.contains( new DataValue( deA, peA, ouA, optionComboA ) ) );
        assertTrue( dataValues.contains( new DataValue( deB, peA, ouA, optionComboA ) ) );
        assertTrue( dataValues.contains( new DataValue( deC, peA, ouA, optionComboA ) ) );
        
        CompleteDataSetRegistration registration = registrationService.getCompleteDataSetRegistration( dsA, peA, ouA );
        
        assertNotNull( registration );
        assertEquals( dsA, registration.getDataSet() );
        assertEquals( peA, registration.getPeriod() );
        assertEquals( ouA, registration.getSource() );
        assertEquals( getDate( 2012, 1, 2 ), registration.getDate() );
    }
}
