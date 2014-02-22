package org.hisp.dhis.dataapproval;

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

import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Jim Grace
 */
@Transactional
public class DefaultDataApprovalLevelService
        implements DataApprovalLevelService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataApprovalLevelStore dataApprovalLevelStore;

    public void setDataApprovalLevelStore( DataApprovalLevelStore dataApprovalLevelStore )
    {
        this.dataApprovalLevelStore = dataApprovalLevelStore;
    }

    // -------------------------------------------------------------------------
    // DataApprovalLevel
    // -------------------------------------------------------------------------

    public List<DataApprovalLevel> getAllDataApprovalLevels()
    {
        return dataApprovalLevelStore.getAllDataApprovalLevels();
    }

    public boolean canMoveDown( int level )
    {
        List<DataApprovalLevel> dataApprovalLevels = getAllDataApprovalLevels();

        int index = level - 1;

        if ( index < 0 || index + 1 >= dataApprovalLevels.size() )
        {
            return false;
        }

        DataApprovalLevel test = dataApprovalLevels.get( index );
        DataApprovalLevel next = dataApprovalLevels.get( index + 1 );

        if ( test.getOrganisationUnitLevel().getLevel() == next.getOrganisationUnitLevel().getLevel()
                && test.getCategoryOptionGroupSet() != null )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean canMoveUp( int level )
    {
        List<DataApprovalLevel> dataApprovalLevels = getAllDataApprovalLevels();

        int index = level - 1;

        if ( index <= 0 || index >= dataApprovalLevels.size() )
        {
            return false;
        }

        DataApprovalLevel test = dataApprovalLevels.get( index );
        DataApprovalLevel previous = dataApprovalLevels.get( index - 1 );

        if ( test.getOrganisationUnitLevel().getLevel() == previous.getOrganisationUnitLevel().getLevel()
                && previous.getCategoryOptionGroupSet() != null )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void moveDown( int level )
    {
        if ( canMoveDown( level ) )
        {
            swapWithNextLevel( level );
        }
    }

    public void moveUp( int level )
    {
        if ( canMoveUp( level ) )
        {
            swapWithNextLevel( level - 1 );
        }
    }

    public boolean exists( DataApprovalLevel testLevel )
    {
        List<DataApprovalLevel> dataApprovalLevels = getAllDataApprovalLevels();

        for ( DataApprovalLevel dataApprovalLevel : dataApprovalLevels )
        {
            if ( testLevel.getOrganisationUnitLevel() == dataApprovalLevel.getOrganisationUnitLevel()
                    && testLevel.getCategoryOptionGroupSet() == dataApprovalLevel.getCategoryOptionGroupSet() )
            {
                return true;
            }
        }

        return false;
    }

    public boolean add( DataApprovalLevel newLevel )
    {
        List<DataApprovalLevel> dataApprovalLevels = getAllDataApprovalLevels();

        if ( newLevel.getOrganisationUnitLevel() == null )
        {
            return false;
        }

        int index = getInsertIndex( dataApprovalLevels, newLevel );

        if ( index < 0 )
        {
            return false;
        }

        System.out.println( "Addding new dataApproval at index " + index );

        dataApprovalLevels.add( index, newLevel );

        //
        // Move down from end to here, to avoid duplicate level in database.
        //
        for (int i = dataApprovalLevels.size() - 1; i > index; i-- )
        {
            update( dataApprovalLevels.get( i ), i );
        }

        newLevel.setLevel( index + 1 );
        newLevel.setCreated( new Date() );

        dataApprovalLevelStore.addDataApproval( newLevel );

        return true;
    }

    public void remove( int level )
    {
        List<DataApprovalLevel> dataApprovalLevels = getAllDataApprovalLevels();

        int index = level - 1;

        if ( index >= 0 & index < dataApprovalLevels.size() )
        {
            dataApprovalLevelStore.deleteDataApprovalLevel( dataApprovalLevels.get( index ) );

            dataApprovalLevels.remove( index );

            //
            // Move up from here to end, to avoid duplicate level in database.
            //
            for (int i = index; i < dataApprovalLevels.size(); i++ )
            {
                update( dataApprovalLevels.get( i ), i );
            }
        }
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Swaps a data approval level with the next higher level.
     *
     * @param level lower level to swap.
     */
    private void swapWithNextLevel( int level )
    {
        List<DataApprovalLevel> dataApprovalLevels = getAllDataApprovalLevels();

        int index = level - 1;

        DataApprovalLevel d2 = dataApprovalLevels.get( index );
        DataApprovalLevel d1  = dataApprovalLevels.get( index + 1 );

        dataApprovalLevels.set( index, d1 );
        dataApprovalLevels.set( index + 1, d2 );

        update( d1, index );
        update( d2, index + 1 );
    }

    /**
     * Updates a data approval level object by setting the level to
     * correspond with the list index, setting the updated date to now,
     * and updating the object on disk.
     *
     * @param dataApprovalLevel data approval level to update
     * @param index index of the object (used to set the level.)
     */
    private void update( DataApprovalLevel dataApprovalLevel, int index )
    {
        dataApprovalLevel.setLevel( index + 1 );

        dataApprovalLevel.setCreated( new Date() );

        dataApprovalLevelStore.updateDataApprovalLevel( dataApprovalLevel );
    }

    /**
     * Finds the right index at which to insert a new data approval level.
     * Returns -1 if the new data approval level is a duplicate.
     *
     * @param dataApprovalLevels list of all levels.
     * @param newLevel new level to find the insertion point for.
     * @return index where the new approval level should be inserted,
     * or -1 if the new level is a duplicate.
     */
    private int getInsertIndex( List<DataApprovalLevel> dataApprovalLevels, DataApprovalLevel newLevel )
    {
        int i = dataApprovalLevels.size() - 1;

        while ( i >= 0 )
        {
            DataApprovalLevel test = dataApprovalLevels.get( i );

            int orgLevelDifference = newLevel.getOrganisationUnitLevel().getLevel() - test.getOrganisationUnitLevel().getLevel();

            if ( orgLevelDifference > 0 )
            {
                break;
            }

            if ( orgLevelDifference == 0 )
            {
                if ( newLevel.getCategoryOptionGroupSet() == test.getCategoryOptionGroupSet() )
                {
                    return -1;
                }

                if ( test.getCategoryOptionGroupSet() == null )
                {
                    break;
                }
            }

            i--;
        }
        return i + 1;
    }
}
