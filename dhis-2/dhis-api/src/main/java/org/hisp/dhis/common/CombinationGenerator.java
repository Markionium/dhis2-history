package org.hisp.dhis.common;

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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lars Helge Overland
 */
public class CombinationGenerator
{
    private IdentifiableObject[][] objects; // Array of object arrays
    private int[] indexes; // Current index for each array
    private int no; // No of arrays
    
    public CombinationGenerator( IdentifiableObject[]... objects )
    {
        this.objects = objects;
        this.indexes = new int[objects.length];
        this.no = objects.length;        
        indexes[no-1]--; // Rewind last index to simplify looping
    }
    
    /**
     * Returns an List of Lists with combinations of IdentifiableObjects.
     */
    public List<List<IdentifiableObject>> getCombinations()
    {
        List<List<IdentifiableObject>> combinations = new ArrayList<List<IdentifiableObject>>();
        
        while ( hasNext() )
        {
            combinations.add( getNext() );
        }
        
        return combinations;
    }
    
    /**
     * Indicates whether there are more combinations to be returned.
     */
    public boolean hasNext()
    {
        for ( int i = no - 1; i >= 0; i-- )
        {
            if ( indexes[i] < objects[i].length - 1 ) // Not at last position in array
            {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Returns the next combination. Returns null if there are no more combinations.
     */
    public List<IdentifiableObject> getNext()
    {
        List<IdentifiableObject> current = null;
        
        for ( int i = no - 1; i >= 0; i-- )
        {
            if ( indexes[i] < objects[i].length - 1 ) // Not at last position in array, increment index and break
            {
                indexes[i]++;
                current = getCurrent();
                break;
            }
            else // At last position in array, reset index to 0 and continue to increment next array
            {
                if ( hasNext() )
                {
                    indexes[i] = 0;
                }
            }
        }
        
        return current;
    }
    
    /**
     * Generates a String representation from the List of IdentifiableObject arrays.
     */
    public static String toString( List<IdentifiableObject[]> objects )
    {
        StringBuilder builder = new StringBuilder( "[" );
        
        for ( IdentifiableObject[] array : objects )
        {
            builder.append( "[" );
            
            for ( IdentifiableObject object : array )
            {
                builder.append( "[" ).append( object.getId() ).append( "," ).append( object.getName() ).append( "]" );
            }
            
            builder.append( "]" );
        }
        
        return builder.append( "]" ).toString();
    }
        
    /**
     * Returns an array with values from the current index of each array in ranges.
     */
    private List<IdentifiableObject> getCurrent()
    {
        List<IdentifiableObject> current = new ArrayList<IdentifiableObject>( no );
        
        for ( int i = 0; i < no; i++ )
        {
            current.add( objects[i][indexes[i]] );
        }
        
        return current;
    }
}
