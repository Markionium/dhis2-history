package org.hisp.dhis.dataapproval;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.*;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dataelement.*;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Jim Grace
 * @version $Id$
 */
public class DataApprovalServiceTest
    extends DhisSpringTest
{
    @Autowired
    private DataApprovalService dataApprovalService;

    @Autowired
    private DataApprovalLevelService dataApprovalLevelService;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private DataElementCategoryService categoryService;

    @Autowired
    private DataSetService dataSetService;
    
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    // -------------------------------------------------------------------------
    // Supporting data
    // -------------------------------------------------------------------------

    private DataSet dataSetA;

    private DataSet dataSetB;

    private Period periodA;

    private Period periodB;

    private Period periodC;

    private Period periodD;

    private OrganisationUnit organisationUnitA;

    private OrganisationUnit organisationUnitB;

    private OrganisationUnit organisationUnitC;

    private OrganisationUnit organisationUnitD;

    private OrganisationUnit organisationUnitE;

    private OrganisationUnit organisationUnitF;

    private OrganisationUnitLevel orgUnitLevel1;

    private OrganisationUnitLevel orgUnitLevel2;

    private OrganisationUnitLevel orgUnitLevel3;

    private OrganisationUnitLevel orgUnitLevel4;

    private DataApprovalLevel dataApprovalLevel1;

    private DataApprovalLevel dataApprovalLevel2;

    private DataApprovalLevel dataApprovalLevel3;

    private DataApprovalLevel dataApprovalLevel4;

    private DataApprovalLevel dataApprovalLevel1A;

    private DataApprovalLevel dataApprovalLevel2A;

    private DataApprovalLevel dataApprovalLevel3A;

    private DataApprovalLevel dataApprovalLevel4A;

    private DataApprovalLevel dataApprovalLevel1B;

    private DataApprovalLevel dataApprovalLevel2B;

    private DataApprovalLevel dataApprovalLevel3B;

    private DataApprovalLevel dataApprovalLevel4B;

    private User userA;

    private User userB;

    private DataElementCategoryOption optionA;

    private DataElementCategoryOption optionB;

    private DataElementCategoryOption optionC;

    private DataElementCategoryOption optionD;

    private DataElementCategoryOption optionE;

    private DataElementCategoryOption optionF;

    private DataElementCategoryOption optionG;

    private DataElementCategoryOption optionH;

    private DataElementCategoryOptionCombo optionComboA;

    private DataElementCategoryOptionCombo optionComboB;

    private DataElementCategoryOptionCombo optionComboC;

    private DataElementCategoryOptionCombo optionComboD;

    private DataElementCategoryOptionCombo optionComboE;

    private DataElementCategoryOptionCombo optionComboF;

    private DataElementCategoryOptionCombo optionComboG;

    private DataElementCategoryOptionCombo optionComboH;

    private DataElementCategoryOptionCombo optionComboI;

    private DataElementCategoryOptionCombo optionComboJ;

    private DataElementCategoryOptionCombo optionComboK;

    private DataElementCategoryOptionCombo optionComboL;

    private DataElementCategoryOptionCombo optionComboM;

    private DataElementCategoryOptionCombo optionComboN;

    private DataElementCategoryOptionCombo optionComboO;

    private DataElementCategoryOptionCombo optionComboP;

    private DataElementCategory categoryA;

    private DataElementCategory categoryB;

    private DataElementCategoryCombo categoryComboA;

    private CategoryOptionGroup noGroup;

    private CategoryOptionGroup groupA;

    private CategoryOptionGroup groupB;

    private CategoryOptionGroup groupC;

    private CategoryOptionGroup groupD;

    private CategoryOptionGroupSet groupSetA;

    private CategoryOptionGroupSet groupSetB;

    // -------------------------------------------------------------------------
    // Set up/tear down
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest() throws Exception
    {
        identifiableObjectManager = (IdentifiableObjectManager) getBean( IdentifiableObjectManager.ID );
        userService = (UserService) getBean( UserService.ID );
        
        // ---------------------------------------------------------------------
        // Add supporting data
        // ---------------------------------------------------------------------

        PeriodType periodType = PeriodType.getPeriodTypeByName( "Monthly" );

        dataSetA = createDataSet( 'A', periodType );
        dataSetB = createDataSet( 'B', periodType );

        dataSetService.addDataSet( dataSetA );
        dataSetService.addDataSet( dataSetB );

        periodA = createPeriod( getDay( 5 ), getDay( 6 ) );
        periodB = createPeriod( getDay( 6 ), getDay( 7 ) );
        periodC = createPeriod( PeriodType.getPeriodTypeByName( "Yearly" ), getDay( 1 ), getDay( 365 ) );
        periodD = createPeriod( PeriodType.getPeriodTypeByName( "Weekly" ), getDay( 1 ), getDay( 365 ) );

        periodService.addPeriod( periodA );
        periodService.addPeriod( periodB );
        periodService.addPeriod( periodC );
        periodService.addPeriod( periodD );

        orgUnitLevel1 = new OrganisationUnitLevel( 1, "Level 1");
        orgUnitLevel2 = new OrganisationUnitLevel( 2, "Level 2");
        orgUnitLevel3 = new OrganisationUnitLevel( 3, "Level 3");
        orgUnitLevel4 = new OrganisationUnitLevel( 4, "Level 4");

        organisationUnitService.addOrganisationUnitLevel( orgUnitLevel1 );
        organisationUnitService.addOrganisationUnitLevel( orgUnitLevel2 );
        organisationUnitService.addOrganisationUnitLevel( orgUnitLevel3 );
        organisationUnitService.addOrganisationUnitLevel( orgUnitLevel4 );

        //
        // Organisation unit hierarchy:
        //
        //      A
        //      |
        //      B
        //     / \
        //    C   E
        //    |   |
        //    D   F
        //

        organisationUnitA = createOrganisationUnit( 'A' );
        organisationUnitB = createOrganisationUnit( 'B', organisationUnitA );
        organisationUnitC = createOrganisationUnit( 'C', organisationUnitB );
        organisationUnitD = createOrganisationUnit( 'D', organisationUnitC );
        organisationUnitE = createOrganisationUnit( 'E', organisationUnitB );
        organisationUnitF = createOrganisationUnit( 'F', organisationUnitE );

        organisationUnitA.setLevel( 1 );
        organisationUnitB.setLevel( 2 );
        organisationUnitC.setLevel( 3 );
        organisationUnitD.setLevel( 4 );
        organisationUnitE.setLevel( 3 );
        organisationUnitF.setLevel( 4 );

        organisationUnitService.addOrganisationUnit( organisationUnitA );
        organisationUnitService.addOrganisationUnit( organisationUnitB );
        organisationUnitService.addOrganisationUnit( organisationUnitC );
        organisationUnitService.addOrganisationUnit( organisationUnitD );
        organisationUnitService.addOrganisationUnit( organisationUnitE );
        organisationUnitService.addOrganisationUnit( organisationUnitF );

        dataApprovalLevel1 = new DataApprovalLevel( orgUnitLevel1, null );
        dataApprovalLevel2 = new DataApprovalLevel( orgUnitLevel2, null );
        dataApprovalLevel3 = new DataApprovalLevel( orgUnitLevel3, null );
        dataApprovalLevel4 = new DataApprovalLevel( orgUnitLevel4, null );

        userA = createUser( 'A' );
        userB = createUser( 'B' );

        userService.addUser( userA );
        userService.addUser( userB );

        noGroup = null;
    }

    // ---------------------------------------------------------------------
    // Set Up Categories
    // ---------------------------------------------------------------------

    private void setUpCategories() throws Exception
    {
        Collection<CategoryOptionGroup> allGroups;

        optionA = new DataElementCategoryOption( "CategoryOptionA" );
        optionB = new DataElementCategoryOption( "CategoryOptionB" );
        optionC = new DataElementCategoryOption( "CategoryOptionC" );
        optionD = new DataElementCategoryOption( "CategoryOptionD" );
        optionE = new DataElementCategoryOption( "CategoryOptionE" );
        optionF = new DataElementCategoryOption( "CategoryOptionF" );
        optionG = new DataElementCategoryOption( "CategoryOptionG" );
        optionH = new DataElementCategoryOption( "CategoryOptionH" );

        categoryService.addDataElementCategoryOption( optionA );
        categoryService.addDataElementCategoryOption( optionB );
        categoryService.addDataElementCategoryOption( optionC );
        categoryService.addDataElementCategoryOption( optionD );
        categoryService.addDataElementCategoryOption( optionE );
        categoryService.addDataElementCategoryOption( optionF );
        categoryService.addDataElementCategoryOption( optionG );
        categoryService.addDataElementCategoryOption( optionH );

        categoryA = createDataElementCategory( 'A', optionA, optionB, optionC, optionD );
        categoryB = createDataElementCategory( 'B', optionE, optionF, optionG, optionH );

        categoryService.addDataElementCategory( categoryA );
        categoryService.addDataElementCategory( categoryB );

        categoryComboA = createCategoryCombo( 'A', categoryA, categoryB );

        categoryService.addDataElementCategoryCombo( categoryComboA );

        optionComboA = createCategoryOptionCombo( 'A', categoryComboA, optionA, optionE );
        optionComboB = createCategoryOptionCombo( 'B', categoryComboA, optionA, optionF );
        optionComboC = createCategoryOptionCombo( 'C', categoryComboA, optionA, optionG );
        optionComboD = createCategoryOptionCombo( 'D', categoryComboA, optionA, optionH );
        optionComboE = createCategoryOptionCombo( 'E', categoryComboA, optionB, optionE );
        optionComboF = createCategoryOptionCombo( 'F', categoryComboA, optionB, optionF );
        optionComboG = createCategoryOptionCombo( 'G', categoryComboA, optionB, optionG );
        optionComboH = createCategoryOptionCombo( 'H', categoryComboA, optionB, optionH );
        optionComboI = createCategoryOptionCombo( 'I', categoryComboA, optionC, optionE );
        optionComboJ = createCategoryOptionCombo( 'J', categoryComboA, optionC, optionF );
        optionComboK = createCategoryOptionCombo( 'K', categoryComboA, optionC, optionG );
        optionComboL = createCategoryOptionCombo( 'L', categoryComboA, optionC, optionH );
        optionComboM = createCategoryOptionCombo( 'M', categoryComboA, optionD, optionE );
        optionComboN = createCategoryOptionCombo( 'N', categoryComboA, optionD, optionF );
        optionComboO = createCategoryOptionCombo( 'O', categoryComboA, optionD, optionG );
        optionComboP = createCategoryOptionCombo( 'P', categoryComboA, optionD, optionH );

        categoryService.addDataElementCategoryOptionCombo( optionComboA );
        categoryService.addDataElementCategoryOptionCombo( optionComboB );
        categoryService.addDataElementCategoryOptionCombo( optionComboC );
        categoryService.addDataElementCategoryOptionCombo( optionComboD );
        categoryService.addDataElementCategoryOptionCombo( optionComboE );
        categoryService.addDataElementCategoryOptionCombo( optionComboF );
        categoryService.addDataElementCategoryOptionCombo( optionComboG );
        categoryService.addDataElementCategoryOptionCombo( optionComboH );
        categoryService.addDataElementCategoryOptionCombo( optionComboI );
        categoryService.addDataElementCategoryOptionCombo( optionComboJ );
        categoryService.addDataElementCategoryOptionCombo( optionComboK );
        categoryService.addDataElementCategoryOptionCombo( optionComboL );
        categoryService.addDataElementCategoryOptionCombo( optionComboM );
        categoryService.addDataElementCategoryOptionCombo( optionComboN );
        categoryService.addDataElementCategoryOptionCombo( optionComboO );
        categoryService.addDataElementCategoryOptionCombo( optionComboP );

        groupA = createCategoryOptionGroup( 'A', optionA, optionB );
        groupB = createCategoryOptionGroup( 'B', optionC, optionD );
        groupC = createCategoryOptionGroup( 'C', optionE, optionF );
        groupD = createCategoryOptionGroup( 'D', optionG, optionH );

        categoryService.saveCategoryOptionGroup( groupA );
        categoryService.saveCategoryOptionGroup( groupB );
        categoryService.saveCategoryOptionGroup( groupC );
        categoryService.saveCategoryOptionGroup( groupD );

        groupSetA = createCategoryOptionGroupSet( 'A', groupA, groupB );
        groupSetB = createCategoryOptionGroupSet( 'B', groupC, groupD );

        categoryService.saveCategoryOptionGroupSet( groupSetA );
        categoryService.saveCategoryOptionGroupSet( groupSetB );

        dataApprovalLevel1A = new DataApprovalLevel( orgUnitLevel1, groupSetA );
        dataApprovalLevel1B = new DataApprovalLevel( orgUnitLevel1, groupSetB );
        dataApprovalLevel2A = new DataApprovalLevel( orgUnitLevel2, groupSetA );
        dataApprovalLevel2B = new DataApprovalLevel( orgUnitLevel2, groupSetB );
        dataApprovalLevel3A = new DataApprovalLevel( orgUnitLevel3, groupSetA );
        dataApprovalLevel3B = new DataApprovalLevel( orgUnitLevel3, groupSetB );
        dataApprovalLevel4A = new DataApprovalLevel( orgUnitLevel4, groupSetA );
        dataApprovalLevel4B = new DataApprovalLevel( orgUnitLevel4, groupSetB );
    }

    // -------------------------------------------------------------------------
    // Basic DataApproval
    // -------------------------------------------------------------------------

    @Test
    public void testAddAndGetDataApproval() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, organisationUnitA, noGroup, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetA, periodA, organisationUnitB, noGroup, date, userA );
        DataApproval dataApprovalC = new DataApproval( dataSetA, periodB, organisationUnitA, noGroup, date, userA );
        DataApproval dataApprovalD = new DataApproval( dataSetB, periodA, organisationUnitA, noGroup, date, userA );
        DataApproval dataApprovalE;

        dataApprovalService.addDataApproval( dataApprovalA );
        dataApprovalService.addDataApproval( dataApprovalB );
        dataApprovalService.addDataApproval( dataApprovalC );
        dataApprovalService.addDataApproval( dataApprovalD );

        dataSetA.setApproveData( true );
        dataSetB.setApproveData( true );

        DataApprovalStatus status;
        DataApproval da;
        DataApprovalLevel level;

        status = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noGroup );
        assertEquals( DataApprovalState.APPROVED_HERE, status.getDataApprovalState() );
        da = status.getDataApproval();
        assertNotNull( da );
        assertEquals( dataSetA.getId(), da.getDataSet().getId() );
        assertEquals( periodA, da.getPeriod() );
        assertEquals( organisationUnitA.getId(), da.getOrganisationUnit().getId() );
        assertEquals( date, da.getCreated() );
        assertEquals( userA.getId(), da.getCreator().getId() );
        level = status.getDataApprovalLevel();
        assertNotNull( level );
        assertEquals( dataApprovalLevel1, level );

        status = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noGroup );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, status.getDataApprovalState() );
        da = status.getDataApproval();
        assertNotNull( da );
        assertEquals( dataSetA.getId(), da.getDataSet().getId() );
        assertEquals( periodA, da.getPeriod() );
        assertEquals( organisationUnitA.getId(), da.getOrganisationUnit().getId() );
        assertEquals( date, da.getCreated() );
        assertEquals( userA.getId(), da.getCreator().getId() );
        level = status.getDataApprovalLevel();
        assertNotNull( level );
        assertEquals( dataApprovalLevel1, level );

        status = dataApprovalService.getDataApprovalStatus( dataSetA, periodB, organisationUnitA, noGroup );
        assertEquals( DataApprovalState.APPROVED_HERE, status.getDataApprovalState() );
        da = status.getDataApproval();
        assertNotNull( da );
        assertEquals( dataSetA.getId(), da.getDataSet().getId() );
        assertEquals( periodB, da.getPeriod() );
        assertEquals( organisationUnitA.getId(), da.getOrganisationUnit().getId() );
        assertEquals( date, da.getCreated() );
        assertEquals( userA.getId(), da.getCreator().getId() );
        level = status.getDataApprovalLevel();
        assertNotNull( level );
        assertEquals( dataApprovalLevel1, level );

        status = dataApprovalService.getDataApprovalStatus( dataSetB, periodA, organisationUnitA, noGroup );
        assertEquals( DataApprovalState.APPROVED_HERE, status.getDataApprovalState() );
        da = status.getDataApproval();
        assertNotNull( da );
        assertEquals( dataSetB.getId(), da.getDataSet().getId() );
        assertEquals( periodA, da.getPeriod() );
        assertEquals( organisationUnitA.getId(), da.getOrganisationUnit().getId() );
        assertEquals( date, da.getCreated() );
        assertEquals( userA.getId(), da.getCreator().getId() );
        level = status.getDataApprovalLevel();
        assertNotNull( level );
        assertEquals( dataApprovalLevel1, level );

        status = dataApprovalService.getDataApprovalStatus( dataSetB, periodB, organisationUnitB, noGroup );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, status.getDataApprovalState() );
        assertNull ( status.getDataApproval() );
        assertNull ( status.getDataApprovalLevel() );
    }

    @Test
    public void testAddDuplicateDataApproval() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, organisationUnitA, noGroup, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetA, periodA, organisationUnitA, noGroup, date, userA );

        dataApprovalService.addDataApproval( dataApprovalA );

        try
        {
            dataApprovalService.addDataApproval( dataApprovalB );
            fail("Should give unique constraint violation");
        }
        catch ( Exception e )
        {
            // Expected
        }
    }

    @Test
    public void testDeleteDataApproval() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, organisationUnitA, noGroup, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetA, periodA, organisationUnitB, noGroup, date, userB );
        DataApproval testA;
        DataApproval testB;

        dataApprovalService.addDataApproval( dataApprovalA );
        dataApprovalService.addDataApproval( dataApprovalB );

        dataSetA.setApproveData( true );

        testA = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noGroup ).getDataApproval();
        assertNotNull( testA );

        testB = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noGroup ).getDataApproval();
        assertNotNull( testB );

        dataApprovalService.deleteDataApproval( dataApprovalA ); // Only A should be deleted.

        testA = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noGroup ).getDataApproval();
        assertNull( testA );

        testB = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noGroup ).getDataApproval();
        assertNotNull( testB );

        dataApprovalService.addDataApproval( dataApprovalA );
        dataApprovalService.deleteDataApproval( dataApprovalB ); // A and B should both be deleted.

        testA = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noGroup ).getDataApproval();
        assertNull( testA );

        testB = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noGroup ).getDataApproval();
        assertNull( testB );
    }

    @Test
    public void testGetDataApprovalState() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        // Not enabled.
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, noGroup ).getDataApprovalState() );

        // Enabled for data set, but data set not associated with organisation unit.

        dataSetA.setApproveData( true );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, noGroup ).getDataApprovalState() );

        // Enabled for data set, and associated with organisation unit C.
        organisationUnitC.addDataSet( dataSetA );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, noGroup ).getDataApprovalState() );

        Date date = new Date();

        // Approved for sourceD
        DataApproval dataApprovalD = new DataApproval( dataSetA, periodA, organisationUnitD, noGroup, date, userA );
        dataApprovalService.addDataApproval( dataApprovalD );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, noGroup ).getDataApprovalState() );

        // Also approved for sourceC
        DataApproval dataApprovalC = new DataApproval( dataSetA, periodA, organisationUnitC, noGroup, date, userA );
        dataApprovalService.addDataApproval( dataApprovalC );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, noGroup ).getDataApprovalState() );

        // Also approved for sourceF
        DataApproval dataApprovalF = new DataApproval( dataSetA, periodA, organisationUnitF, noGroup, date, userA );
        dataApprovalService.addDataApproval( dataApprovalF );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, noGroup ).getDataApprovalState() );

        // Also approved also for sourceE
        DataApproval dataApprovalE = new DataApproval( dataSetA, periodA, organisationUnitE, noGroup, date, userA );
        dataApprovalService.addDataApproval( dataApprovalE );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, noGroup ).getDataApprovalState() );

        // Disable approval for dataset.
        dataSetA.setApproveData( false );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noGroup ).getDataApprovalState() );
    }

    @Test
    public void testGetDataApprovalStateWithMultipleChildren() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        dataSetA.setApproveData( true );

        organisationUnitD.addDataSet( dataSetA );
        organisationUnitF.addDataSet( dataSetA );

        Date date = new Date();

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, noGroup ).getDataApprovalState() );

        dataApprovalService.addDataApproval( new DataApproval( dataSetA, periodA, organisationUnitF, noGroup, date, userA ) );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, noGroup ).getDataApprovalState() );

        dataApprovalService.addDataApproval( new DataApproval( dataSetA, periodA, organisationUnitD, noGroup, date, userA ) );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, noGroup ).getDataApprovalState() );

        dataApprovalService.addDataApproval( new DataApproval( dataSetA, periodA, organisationUnitE, noGroup, date, userA ) );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, noGroup ).getDataApprovalState() );

        dataApprovalService.addDataApproval( new DataApproval( dataSetA, periodA, organisationUnitC, noGroup, date, userA ) );

        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, noGroup ).getDataApprovalState() );
    }

    @Test
    public void testGetDataApprovalStateOtherPeriodTypes() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        dataSetA.setApproveData( true );

        organisationUnitA.addDataSet( dataSetA );
        organisationUnitD.addDataSet( dataSetA );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodB, organisationUnitA, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodC, organisationUnitA, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodC, organisationUnitD, noGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodD, organisationUnitD, noGroup ).getDataApprovalState() );
    }

    @Test
    public void testMayApprove() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitC );
        createUserAndInjectSecurityContext( units, false, DataApproval.AUTH_APPROVE );

        assertEquals( false, dataApprovalService.mayApprove( organisationUnitA ) );
        assertEquals( false, dataApprovalService.mayApprove( organisationUnitB ) );
        assertEquals( true, dataApprovalService.mayApprove( organisationUnitC ) );
        assertEquals( false, dataApprovalService.mayApprove( organisationUnitD ) );
    }

    @Test
    public void testMayApproveLowerLevels() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        createUserAndInjectSecurityContext( units, false, DataApproval.AUTH_APPROVE_LOWER_LEVELS );

        assertEquals( false, dataApprovalService.mayApprove( organisationUnitA ) );
        assertEquals( false, dataApprovalService.mayApprove( organisationUnitB ) );
        assertEquals( true, dataApprovalService.mayApprove( organisationUnitC ) );
        assertEquals( true, dataApprovalService.mayApprove( organisationUnitD ) );
    }

    @Test
    public void testMayApproveSameAndLowerLevels() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        createUserAndInjectSecurityContext( units, false, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS );

        assertEquals( false, dataApprovalService.mayApprove( organisationUnitA ) );
        assertEquals( true, dataApprovalService.mayApprove( organisationUnitB ) );
        assertEquals( true, dataApprovalService.mayApprove( organisationUnitC ) );
        assertEquals( true, dataApprovalService.mayApprove( organisationUnitD ) );
    }

    @Test
    public void testMayApproveNoAuthority() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitC );
        createUserAndInjectSecurityContext( units, false );

        assertEquals( false, dataApprovalService.mayApprove( organisationUnitA ) );
        assertEquals( false, dataApprovalService.mayApprove( organisationUnitB ) );
        assertEquals( false, dataApprovalService.mayApprove( organisationUnitC ) );
        assertEquals( false, dataApprovalService.mayApprove( organisationUnitD ) );
    }

    @Test
    public void testMayUnapprove() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        createUserAndInjectSecurityContext( units, false, DataApproval.AUTH_APPROVE );

        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, organisationUnitA, noGroup, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetA, periodA, organisationUnitB, noGroup, date, userA );
        DataApproval dataApprovalC = new DataApproval( dataSetA, periodA, organisationUnitC, noGroup, date, userA );
        DataApproval dataApprovalD = new DataApproval( dataSetA, periodA, organisationUnitD, noGroup, date, userA );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalB ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalC ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalD ) );

        // These approvals will not block un-approving
        dataApprovalService.addDataApproval( dataApprovalB );
        dataApprovalService.addDataApproval( dataApprovalC );
        dataApprovalService.addDataApproval( dataApprovalD );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalB ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalC ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalD ) );

        // Approval at A will block them all
        dataApprovalService.addDataApproval( dataApprovalA );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalB ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalC ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalD ) );
    }

    @Test
    public void testMayUnapproveLowerLevels() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        createUserAndInjectSecurityContext( units, false, DataApproval.AUTH_APPROVE_LOWER_LEVELS );

        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, organisationUnitA, noGroup, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetA, periodA, organisationUnitB, noGroup, date, userA );
        DataApproval dataApprovalC = new DataApproval( dataSetA, periodA, organisationUnitC, noGroup, date, userA );
        DataApproval dataApprovalD = new DataApproval( dataSetA, periodA, organisationUnitD, noGroup, date, userA );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalB ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalC ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalD ) );

        // These approvals will not block un-approving
        dataApprovalService.addDataApproval( dataApprovalC );
        dataApprovalService.addDataApproval( dataApprovalD );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalB ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalC ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalD ) );

        // Approval at B will block them all
        dataApprovalService.addDataApproval( dataApprovalB );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalB ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalC ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalD ) );
    }

    @Test
    public void testMayUnapproveSameAndLowerLevels() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        createUserAndInjectSecurityContext( units, false, DataApproval.AUTH_APPROVE );

        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, organisationUnitA, noGroup, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetA, periodA, organisationUnitB, noGroup, date, userA );
        DataApproval dataApprovalC = new DataApproval( dataSetA, periodA, organisationUnitC, noGroup, date, userA );
        DataApproval dataApprovalD = new DataApproval( dataSetA, periodA, organisationUnitD, noGroup, date, userA );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalB ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalC ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalD ) );

        // These approvals will not block un-approving
        dataApprovalService.addDataApproval( dataApprovalB );
        dataApprovalService.addDataApproval( dataApprovalC );
        dataApprovalService.addDataApproval( dataApprovalD );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalB ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalC ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalD ) );

        // Approval at A will block them all
        dataApprovalService.addDataApproval( dataApprovalA );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalB ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalC ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalD ) );
    }

    @Test
    public void testMayUnapproveNoAuthority() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        createUserAndInjectSecurityContext( units, false );

        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, organisationUnitA, noGroup, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetA, periodA, organisationUnitB, noGroup, date, userA );
        DataApproval dataApprovalC = new DataApproval( dataSetA, periodA, organisationUnitC, noGroup, date, userA );
        DataApproval dataApprovalD = new DataApproval( dataSetA, periodA, organisationUnitD, noGroup, date, userA );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalB ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalC ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalD ) );
    }

    // ---------------------------------------------------------------------
    // Test with Categories
    // ---------------------------------------------------------------------

    @Test
    public void testAddAndGetDataApprovalWithCategories() throws Exception
    {
        setUpCategories();

        dataSetA.setApproveData( true );

        organisationUnitA.addDataSet( dataSetA );
        organisationUnitB.addDataSet( dataSetA );
        organisationUnitC.addDataSet( dataSetA );

        Date date = new Date();

        //
        // Group set A -> Groups A,B
        // Group set B -> Groups C,D
        //
        // Group A -> Options A,B
        // Group B -> Options C,D
        // Group C -> Options E,F
        // Group D -> Options G,H
        //
        // Category A -> Options A,B,C,D
        // Category B -> Options E,F,G,H
        //
        // Option combo A -> Options A,E -> Groups A,C
        // Option combo B -> Options A,F -> Groups A,C
        // Option combo C -> Options A,G -> Groups A,D
        // Option combo D -> Options A,H -> Groups A,D
        // Option combo E -> Options B,E -> Groups B,C
        // Option combo F -> Options B,F -> Groups B,C
        // Option combo G -> Options B,G -> Groups B,D
        // Option combo H -> Options B,H -> Groups B,D
        // Option combo I -> Options C,E -> Groups B,D
        // Option combo J -> Options C,F -> Groups B,D
        // Option combo K -> Options C,G -> Groups B,D

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupA ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupA ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, groupA ).getDataApprovalState() );

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboA ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboA ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, optionComboA ).getDataApprovalState() );

        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2A );

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupA ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupA ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, groupA ).getDataApprovalState() );

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboA ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboB ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, optionComboC ).getDataApprovalState() );

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboI ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboJ ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, optionComboK ).getDataApprovalState() );

        dataApprovalService.addDataApproval( new DataApproval( dataSetA, periodA, organisationUnitB, groupA, date, userA ) );

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupA ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupA ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, groupA ).getDataApprovalState() );

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboA ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboB ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, optionComboC ).getDataApprovalState() );

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboI ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboJ ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, optionComboK ).getDataApprovalState() );
    }
}
