package org.hisp.dhis.organisationunit;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.hisp.dhis.hierarchy.HierarchyViolationException;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitLevelComparator;
import org.hisp.dhis.source.SourceStore;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.hisp.dhis.system.util.UUIdUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: DefaultOrganisationUnitService.java 5951 2008-10-16 17:41:34Z larshelg $
 */
@Transactional
public class DefaultOrganisationUnitService
    implements OrganisationUnitService
{
    private static final String LEVEL_PREFIX = "Level ";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SourceStore sourceStore;

    public void setSourceStore( SourceStore sourceStore )
    {
        this.sourceStore = sourceStore;
    }

    private OrganisationUnitStore organisationUnitStore;

    public void setOrganisationUnitStore( OrganisationUnitStore organisationUnitStore )
    {
        this.organisationUnitStore = organisationUnitStore;
    }

    // -------------------------------------------------------------------------
    // OrganisationUnit
    // -------------------------------------------------------------------------

    public int addOrganisationUnit( OrganisationUnit organisationUnit )
    {
        if ( organisationUnit.getUuid() == null )
        {
            organisationUnit.setUuid( UUIdUtils.getUUId() );
        }

        organisationUnit.setLastUpdated( new Date() );
        
        int id = sourceStore.addSource( organisationUnit );

        addOrganisationUnitHierarchy( organisationUnit.getOpeningDate() );
        
        return id;
    }

    public void updateOrganisationUnit( OrganisationUnit organisationUnit )
    {
        organisationUnit.setLastUpdated( new Date() );
        
        sourceStore.updateSource( organisationUnit );
    }
    
    public void updateOrganisationUnit( OrganisationUnit organisationUnit, boolean updateHierarchy )
    {
        updateOrganisationUnit( organisationUnit );
        
        if ( updateHierarchy )
        {
            addOrganisationUnitHierarchy( new Date() );
        }
    }

    public void deleteOrganisationUnit( OrganisationUnit organisationUnit )
        throws HierarchyViolationException
    {
        if ( !organisationUnit.getChildren().isEmpty() )
        {
            throw new HierarchyViolationException( "Cannot delete an OrganisationUnit with children" );
        }

        OrganisationUnit parent = organisationUnit.getParent();
        
        if ( parent != null )
        {
            parent.getChildren().remove( organisationUnit );
            
            sourceStore.updateSource( parent );
        }
        
        sourceStore.deleteSource( organisationUnit );
        
        addOrganisationUnitHierarchy( organisationUnit.getClosedDate() != null ? organisationUnit.getClosedDate() : new Date() );
    }

    public OrganisationUnit getOrganisationUnit( int id )
    {
        return sourceStore.getSource( id );
    }

    public Collection<OrganisationUnit> getAllOrganisationUnits()
    {
        return sourceStore.getAllSources();
    }
    
    public Collection<OrganisationUnit> getOrganisationUnits( final Collection<Integer> identifiers )
    {
        Collection<OrganisationUnit> objects = getAllOrganisationUnits();
        
        return identifiers == null ? objects : FilterUtils.filter( objects, new Filter<OrganisationUnit>()
            {
                public boolean retain( OrganisationUnit object )
                {
                    return identifiers.contains( object.getId() );
                }
            } );
    }

    public OrganisationUnit getOrganisationUnit( String uuid )
    {
        return organisationUnitStore.getOrganisationUnit( uuid );
    }

    public OrganisationUnit getOrganisationUnitByName( String name )
    {
        return organisationUnitStore.getOrganisationUnitByName( name );
    }

    public OrganisationUnit getOrganisationUnitByShortName( String shortName )
    {
        return organisationUnitStore.getOrganisationUnitByShortName( shortName );
    }

    public OrganisationUnit getOrganisationUnitByCode( String code )
    {
        return organisationUnitStore.getOrganisationUnitByCode( code );
    }

    public Collection<OrganisationUnit> getRootOrganisationUnits()
    {
        return organisationUnitStore.getRootOrganisationUnits();
    }

    public Collection<OrganisationUnit> getOrganisationUnitWithChildren( int id )
    {
        OrganisationUnit organisationUnit = getOrganisationUnit( id );

        if ( organisationUnit == null )
        {
            return Collections.emptySet();
        }

        List<OrganisationUnit> result = new ArrayList<OrganisationUnit>();

        int rootLevel = 1;
        
        organisationUnit.setLevel( rootLevel );
        
        result.add( organisationUnit );

        addOrganisationUnitChildren( organisationUnit, result, rootLevel );

        return result;
    }

    /**
     * Support method for getOrganisationUnitWithChildren(). Adds all
     * OrganisationUnit children to a result collection.
     */
    private void addOrganisationUnitChildren( OrganisationUnit parent, List<OrganisationUnit> result, int level )
    {
        if ( parent.getChildren() != null && parent.getChildren().size() > 0 )
        {
            level++;
        }
        
        for ( OrganisationUnit child : parent.getChildren() )
        {
            child.setLevel( level );
            
            result.add( child );

            addOrganisationUnitChildren( child, result, level );
        }
        
        level--;
    }

    public List<OrganisationUnit> getOrganisationUnitBranch( int id )
    {
        OrganisationUnit organisationUnit = getOrganisationUnit( id );

        if ( organisationUnit == null )
        {
            return Collections.emptyList();
        }

        ArrayList<OrganisationUnit> result = new ArrayList<OrganisationUnit>();

        result.add( organisationUnit );

        OrganisationUnit parent = organisationUnit.getParent();

        while ( parent != null )
        {
            result.add( parent );

            parent = parent.getParent();
        }

        Collections.reverse( result ); // From root to target

        return result;
    }

    public Collection<OrganisationUnit> getOrganisationUnitsAtLevel( int level )
    {
        if ( level < 1 )
        {
            throw new IllegalArgumentException( "Level must be greater than zero" );
        }

        if ( level == 1 )
        {
            return getRootOrganisationUnits();
        }

        HashSet<OrganisationUnit> result = new HashSet<OrganisationUnit>();

        for ( OrganisationUnit root : organisationUnitStore.getRootOrganisationUnits() )
        {
            addOrganisationUnitChildrenAtLevel( root, 2, level, result );
        }

        return result;
    }
    
    public Collection<OrganisationUnit> getOrganisationUnitsAtLevel( int level, OrganisationUnit parent )
    {
        if ( level < 1 )
        {
            throw new IllegalArgumentException( "Level must be greater than zero" );
        }
        
        int parentLevel = getLevelOfOrganisationUnit( parent );
        
        if ( level < parentLevel )
        {
            throw new IllegalArgumentException( "Level must be greater than or equal to level of parent OrganisationUnit" );
        }

        HashSet<OrganisationUnit> result = new HashSet<OrganisationUnit>();
        
        if ( level == parentLevel )
        {
            result.add( parent );
        }
        else
        {        
            addOrganisationUnitChildrenAtLevel( parent, parentLevel + 1, level, result );
        }
        
        return result;
    }

    /**
     * Support method for getOrganisationUnitsAtLevel(). Adds all children at a
     * given targetLevel to a result collection. The parent's children are at
     * the current level.
     */
    private void addOrganisationUnitChildrenAtLevel( OrganisationUnit parent, int currentLevel, int targetLevel,
        HashSet<OrganisationUnit> result )
    {
        if ( currentLevel == targetLevel )
        {
            result.addAll( parent.getChildren() );
        }
        else
        {
            for ( OrganisationUnit child : parent.getChildren() )
            {
                addOrganisationUnitChildrenAtLevel( child, currentLevel + 1, targetLevel, result );
            }
        }
    }

    public int getLevelOfOrganisationUnit( int id )
    {
        return getLevelOfOrganisationUnit( getOrganisationUnit( id ) );
    }
    
    public int getLevelOfOrganisationUnit( OrganisationUnit organisationUnit )
    {
        int level = 1;

        OrganisationUnit parent = organisationUnit.getParent();

        while ( parent != null )
        {
            ++level;

            parent = parent.getParent();
        }

        return level;
    }

    public int getNumberOfOrganisationalLevels()
    {
        int maxDepth = 0;
        int depth;

        for ( OrganisationUnit root : getRootOrganisationUnits() )
        {
            depth = getDepth( root, 1 );

            if ( depth > maxDepth )
            {
                maxDepth = depth;
            }
        }

        return maxDepth;
    }

    /**
     * Support method for getNumberOfOrganisationalLevels(). Finds the depth of
     * a given subtree. The parent is at the current level.
     */
    private int getDepth( OrganisationUnit parent, int currentLevel )
    {
        int maxDepth = currentLevel;
        int depth;

        for ( OrganisationUnit child : parent.getChildren() )
        {
            depth = getDepth( child, currentLevel + 1 );

            if ( depth > maxDepth )
            {
                maxDepth = depth;
            }
        }

        return maxDepth;
    }

    public void updateOrganisationUnitParent( int organisationUnitId, int parentId )
    {
        organisationUnitStore.updateOrganisationUnitParent( organisationUnitId, parentId );
    }
    
    public Collection<OrganisationUnit> getOrganisationUnitsWithoutGroups()
    {
        return organisationUnitStore.getOrganisationUnitsWithoutGroups();
    }
    
    // -------------------------------------------------------------------------
    // OrganisationUnitHierarchy
    // -------------------------------------------------------------------------

    public int addOrganisationUnitHierarchy( Date date )
    {
        return organisationUnitStore.addOrganisationUnitHierarchy( date );
    }

    public OrganisationUnitHierarchy getOrganisationUnitHierarchy( int id )
    {
        return organisationUnitStore.getOrganisationUnitHierarchy( id );
    }

    public OrganisationUnitHierarchy getLatestOrganisationUnitHierarchy()
    {
        return organisationUnitStore.getLatestOrganisationUnitHierarchy();
    }

    public void removeOrganisationUnitHierarchies( Date date )
    {
        organisationUnitStore.removeOrganisationUnitHierarchies( date );
    }
    
    public Collection<OrganisationUnitHierarchy> getOrganisationUnitHierarchies( Date startDate, Date endDate )
    {
        return organisationUnitStore.getOrganisationUnitHierarchies( startDate, endDate );
    }

    public void clearOrganisationUnitHierarchyHistory()
    {
        Calendar calendar = Calendar.getInstance();
        
        calendar.clear();
        calendar.set( 1970, Calendar.JANUARY, 1 );
        
        organisationUnitStore.deleteOrganisationUnitHierarchies();
        organisationUnitStore.addOrganisationUnitHierarchy( calendar.getTime() );
    }

    public Collection<Integer> getChildren( int organisationUnitHierarchyId, int parentId )
    {
        OrganisationUnitHierarchy hierarchy = getOrganisationUnitHierarchy( organisationUnitHierarchyId );
        
        Set<Entry<Integer, Integer>> structureEntries = hierarchy.getStructure().entrySet();

        List<Integer> children = new ArrayList<Integer>();
        children.add( 0, parentId );

        int childCounter = 1;

        // ---------------------------------------------------------------------
        // Sorts out the children of parent organisation unit from structure
        // and adds them to children
        // ---------------------------------------------------------------------

        for ( int i = 0; i < childCounter; i++ )
        {
            for ( Entry<Integer, Integer> entry : structureEntries )
            {
                if ( entry.getValue().intValue() == children.get( i ).intValue() )
                {
                    children.add( childCounter++, entry.getKey() );
                }
            }
        }
        
        return children;
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitLevel
    // -------------------------------------------------------------------------

    public int addOrganisationUnitLevel( OrganisationUnitLevel level )
    {
        return organisationUnitStore.addOrganisationUnitLevel( level );
    }
    
    public void updateOrganisationUnitLevel( OrganisationUnitLevel level )
    {
        organisationUnitStore.updateOrganisationUnitLevel( level );
    }
    
    public OrganisationUnitLevel getOrganisationUnitLevel( int id )
    {
        return organisationUnitStore.getOrganisationUnitLevel( id );
    }
    
    public Collection<OrganisationUnitLevel> getOrganisationUnitLevels( final Collection<Integer> identifiers )
    {
        Collection<OrganisationUnitLevel> objects = getOrganisationUnitLevels();
        
        return identifiers == null ? objects : FilterUtils.filter( objects, new Filter<OrganisationUnitLevel>()
            {
                public boolean retain( OrganisationUnitLevel object )
                {
                    return identifiers.contains( object.getId() );
                }
            } );
    }
    
    public void deleteOrganisationUnitLevel( OrganisationUnitLevel level )
    {
        organisationUnitStore.deleteOrganisationUnitLevel( level );
    }

    public void deleteOrganisationUnitLevels()
    {
        organisationUnitStore.deleteOrganisationUnitLevels();
    }
    
    public List<OrganisationUnitLevel> getOrganisationUnitLevels()
    {
        List<OrganisationUnitLevel> levels = new ArrayList<OrganisationUnitLevel>( organisationUnitStore.getOrganisationUnitLevels() );
        
        Collections.sort( levels, new OrganisationUnitLevelComparator() );
        
        return levels;
    }

    public OrganisationUnitLevel getOrganisationUnitLevelByLevel( int level )
    {
        return organisationUnitStore.getOrganisationUnitLevelByLevel( level );
    }
    
    public OrganisationUnitLevel getOrganisationUnitLevelByName( String name )
    {
        return organisationUnitStore.getOrganisationUnitLevelByName( name );
    }
    
    public List<OrganisationUnitLevel> getFilledOrganisationUnitLevels()
    {
        Map<Integer, OrganisationUnitLevel> levelMap = getOrganisationUnitLevelMap();
        
        List<OrganisationUnitLevel> levels = new ArrayList<OrganisationUnitLevel>();
        
        for ( int i = 0; i < getNumberOfOrganisationalLevels(); i++ )
        {
            int level = i + 1;
            
            levels.add( levelMap.get( level ) != null ? 
            levelMap.get( level ) : new OrganisationUnitLevel( level, LEVEL_PREFIX + level ) );
        }
        
        return levels;
    }
    
    private Map<Integer, OrganisationUnitLevel> getOrganisationUnitLevelMap()
    {
        Map<Integer, OrganisationUnitLevel> levelMap = new HashMap<Integer, OrganisationUnitLevel>();
        
        for ( OrganisationUnitLevel level : organisationUnitStore.getOrganisationUnitLevels() )
        {
            levelMap.put( level.getLevel(), level );
        }
        
        return levelMap;
    }
}
