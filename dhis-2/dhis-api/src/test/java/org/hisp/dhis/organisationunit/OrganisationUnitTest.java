package org.hisp.dhis.organisationunit;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import static junit.framework.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 */
public class OrganisationUnitTest
{
    private List<CoordinatesTuple> coordinatesList = new ArrayList<CoordinatesTuple>();
    
    private String coordinates = "[[[[11.11,22.22],[33.33,44.44],[55.55,66.66]]],[[[77.77,88.88],[99.99,11.11],[22.22,33.33]]],[[[44.44,55.55],[66.66,77.77],[88.88,99.99]]]]";
    
    private CoordinatesTuple tupleA;
    private CoordinatesTuple tupleB;
    private CoordinatesTuple tupleC;
    
    @Before
    public void before()
    {
        tupleA = new CoordinatesTuple();
        tupleA.addCoordinates( "11.11,22.22" );
        tupleA.addCoordinates( "33.33,44.44" );
        tupleA.addCoordinates( "55.55,66.66" );
        
        tupleB = new CoordinatesTuple();
        tupleB.addCoordinates( "77.77,88.88" );
        tupleB.addCoordinates( "99.99,11.11" );
        tupleB.addCoordinates( "22.22,33.33" );

        tupleC = new CoordinatesTuple();
        tupleC.addCoordinates( "44.44,55.55" );
        tupleC.addCoordinates( "66.66,77.77" );
        tupleC.addCoordinates( "88.88,99.99" );
        
        coordinatesList.add( tupleA );
        coordinatesList.add( tupleB );
        coordinatesList.add( tupleC );
    }

    @Test
    public void testSetCoordinatesFromCollection()
    {
        OrganisationUnit unit = new OrganisationUnit();
        unit.setCoordinatesFromList( coordinatesList );
        
        assertEquals( coordinates, unit.getCoordinates() );
    }
    
    @Test
    public void testGetCoordinatesAsCollection()
    {   
        OrganisationUnit unit = new OrganisationUnit();
        unit.setCoordinates( coordinates );
        
        assertEquals( 3, unit.getCoordinatesAsList().size() );
        
        assertEquals( tupleA, unit.getCoordinatesAsList().get( 0 ) );
        assertEquals( tupleB, unit.getCoordinatesAsList().get( 1 ) );
        assertEquals( tupleC, unit.getCoordinatesAsList().get( 2 ) );
    }
}
