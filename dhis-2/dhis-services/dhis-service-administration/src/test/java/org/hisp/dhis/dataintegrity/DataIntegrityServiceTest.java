package org.hisp.dhis.dataintegrity;

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

import java.util.Collection;

import org.hisp.dhis.DhisConvenienceTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataIntegrityServiceTest
    extends DhisConvenienceTest
{
    private DataIntegrityService dataIntegrityService;
    
    private DataElement elementA;
    private DataElement elementB;
    private DataElement elementC;
    
    private DataElementGroup elementGroupA;
    
    private IndicatorType indicatorTypeA;
    
    private Indicator indicatorA;
    private Indicator indicatorB;
    private Indicator indicatorC;
    
    private IndicatorGroup indicatorGroupA;
    
    private DataSet dataSetA;
    private DataSet dataSetB;
    
    private OrganisationUnit unitA;
    private OrganisationUnit unitB;
    private OrganisationUnit unitC;
    private OrganisationUnit unitD;
    private OrganisationUnit unitE;
    private OrganisationUnit unitF;
    
    private OrganisationUnitGroup unitGroupA;
    private OrganisationUnitGroup unitGroupB;
    private OrganisationUnitGroup unitGroupC;
    
    private OrganisationUnitGroupSet unitGroupSetA;
    private OrganisationUnitGroupSet unitGroupSetB;    

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
        // ---------------------------------------------------------------------
        // Services
        // ---------------------------------------------------------------------

        dataIntegrityService = (DataIntegrityService) getBean( DataIntegrityService.ID );        
        dataElementService = (DataElementService) getBean( DataElementService.ID );        
        indicatorService = (IndicatorService) getBean( IndicatorService.ID );        
        dataSetService = (DataSetService) getBean( DataSetService.ID );        
        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );        
        organisationUnitGroupService = (OrganisationUnitGroupService) getBean( OrganisationUnitGroupService.ID );

        // ---------------------------------------------------------------------
        // Objects
        // ---------------------------------------------------------------------

        elementA = createDataElement( 'A' );
        elementB = createDataElement( 'B' );
        elementC = createDataElement( 'C' );

        dataElementService.addDataElement( elementA );
        dataElementService.addDataElement( elementB );
        dataElementService.addDataElement( elementC );
                
        indicatorTypeA = createIndicatorType( 'A' );
        
        indicatorService.addIndicatorType( indicatorTypeA );
        
        indicatorA = createIndicator( 'A', indicatorTypeA );
        indicatorB = createIndicator( 'B', indicatorTypeA );
        indicatorC = createIndicator( 'C', indicatorTypeA );
        
        indicatorA.setNumerator( " " );
        indicatorB.setNumerator( "Numerator" );
        indicatorB.setDenominator( "Denominator" );
        indicatorC.setNumerator( "Numerator" );
        indicatorC.setDenominator( "Denominator" );

        indicatorService.addIndicator( indicatorA );
        indicatorService.addIndicator( indicatorB );
        indicatorService.addIndicator( indicatorC );        
        
        unitA = createOrganisationUnit( 'A' );
        unitB = createOrganisationUnit( 'B', unitA );
        unitC = createOrganisationUnit( 'C', unitB );
        unitD = createOrganisationUnit( 'D', unitC );
        unitE = createOrganisationUnit( 'E', unitD );
        unitF = createOrganisationUnit( 'F' );

        organisationUnitService.addOrganisationUnit( unitA );
        organisationUnitService.addOrganisationUnit( unitB );
        organisationUnitService.addOrganisationUnit( unitC );
        organisationUnitService.addOrganisationUnit( unitD ); 
        organisationUnitService.addOrganisationUnit( unitE );
        organisationUnitService.addOrganisationUnit( unitF );
        
        unitA.setParent( unitC );
        
        organisationUnitService.updateOrganisationUnit( unitA );
        
        dataSetA = createDataSet( 'A', new MonthlyPeriodType() );
        dataSetB = createDataSet( 'B', new MonthlyPeriodType() );

        dataSetA.getDataElements().add( elementA );
        dataSetA.getDataElements().add( elementB );
        
        dataSetA.getSources().add( unitA );
        
        dataSetService.addDataSet( dataSetA );
        dataSetService.addDataSet( dataSetB );
              
        // ---------------------------------------------------------------------
        // Groups
        // ---------------------------------------------------------------------

        elementGroupA = createDataElementGroup( 'A' );
        
        elementGroupA.getMembers().add( elementA );
        
        dataElementService.addDataElementGroup( elementGroupA );
        
        indicatorGroupA = createIndicatorGroup( 'A' );
        
        indicatorGroupA.getMembers().add( indicatorA );
        
        indicatorService.addIndicatorGroup( indicatorGroupA );
        
        unitGroupA = createOrganisationUnitGroup( 'A' );
        unitGroupB = createOrganisationUnitGroup( 'B' );
        unitGroupC = createOrganisationUnitGroup( 'C' );
        
        unitGroupA.getMembers().add( unitA );
        unitGroupA.getMembers().add( unitB );
        unitGroupA.getMembers().add( unitC );
        
        unitGroupB.getMembers().add( unitA );
        unitGroupB.getMembers().add( unitB );
        unitGroupB.getMembers().add( unitF );
        
        organisationUnitGroupService.addOrganisationUnitGroup( unitGroupA );
        organisationUnitGroupService.addOrganisationUnitGroup( unitGroupB );
        organisationUnitGroupService.addOrganisationUnitGroup( unitGroupC );
        
        unitGroupSetA = new OrganisationUnitGroupSet( "GroupSetA", "GroupSetA", true, false );
        unitGroupSetB = new OrganisationUnitGroupSet( "GroupSetB", "GroupSetB", false, true );
        
        unitGroupSetA.getOrganisationUnitGroups().add( unitGroupA );    
        
        unitGroupSetB.getOrganisationUnitGroups().add( unitGroupA );
        unitGroupSetB.getOrganisationUnitGroups().add( unitGroupB );
                
        organisationUnitGroupService.addOrganisationUnitGroupSet( unitGroupSetA );
        organisationUnitGroupService.addOrganisationUnitGroupSet( unitGroupSetB );
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    public void testGetDataElementsWithoutDataSet()
    {
        Collection<DataElement> expected = dataIntegrityService.getDataElementsWithoutDataSet();
        
        assertTrue( equals( expected, elementC ) );
    }
    
    public void testGetDataElementsWithoutGroups()
    {
        Collection<DataElement> expected = dataIntegrityService.getDataElementsWithoutGroups();
        
        assertTrue( equals( expected, elementB, elementC ) );
    }
    
    public void testGetDataSetsNotAssignedToOrganisationUnits()
    {
        Collection<DataSet> expected = dataIntegrityService.getDataSetsNotAssignedToOrganisationUnits();
        
        assertTrue( equals( expected, dataSetB ) );
    }
    
    public void testGetIndicatorsWithBlankFormulas()
    {
        Collection<Indicator> expected = dataIntegrityService.getIndicatorsWithBlankFormulas();
        
        assertTrue( equals( expected, indicatorA ) );
    }
    
    public void testGetIndicatorsWithIdenticalFormulas()
    {
        Collection<Indicator> expected = dataIntegrityService.getIndicatorsWithIdenticalFormulas();
        
        assertTrue( equals( expected, indicatorC ) );
    }
    
    public void testGetIndicatorsWithoutGroups()
    {
        Collection<Indicator> expected = dataIntegrityService.getIndicatorsWithoutGroups();
        
        assertTrue( equals( expected, indicatorB, indicatorC ) );
    }
    
    public void testGetOrganisationUnitsWithCyclicReferences()
    {
        Collection<OrganisationUnit> expected = dataIntegrityService.getOrganisationUnitsWithCyclicReferences();
        
        assertTrue( equals( expected, unitA, unitB, unitC ) );
    }
    
    public void testGetOrphanedOrganisationUnits()
    {
        Collection<OrganisationUnit> expected = dataIntegrityService.getOrphanedOrganisationUnits();
        
        assertTrue( equals( expected, unitF ) );
    }
    
    public void testGetOrganisationUnitsWithoutGroups()
    {
        Collection<OrganisationUnit> expected = dataIntegrityService.getOrganisationUnitsWithoutGroups();
        
        assertTrue( equals( expected, unitD, unitE ) );
    }
    
    public void testGetOrganisationUnitsViolatingCompulsoryGroupSets()
    {
        Collection<OrganisationUnit> expected = dataIntegrityService.getOrganisationUnitsViolatingCompulsoryGroupSets();
        
        assertTrue( equals( expected, unitD, unitE, unitF ) );
    }
    
    public void testGetOrganisationUnitsViolatingExclusiveGroupSets()
    {
        Collection<OrganisationUnit> expected = dataIntegrityService.getOrganisationUnitsViolatingExclusiveGroupSets();
        
        assertTrue( equals( expected, unitA, unitB ) );
    }
    
    public void testGetOrganisationUnitGroupsWithoutGroupSets()
    {
        Collection<OrganisationUnitGroup> expected = dataIntegrityService.getOrganisationUnitGroupsWithoutGroupSets();
        
        assertTrue( equals( expected, unitGroupC ) );
    }
}
