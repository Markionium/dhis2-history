package org.hisp.dhis.indicator;

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
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.common.AbstractIdentifiableObject;

/**
 * An IndicatorGroupSet is a set of IndicatorGroups. It is by default exclusive,
 * in the sense that an Indicator can only be a member of one or zero of the
 * IndicatorGroups in a IndicatorGroupSet.
 * 
 * @author Lars Helge Overland
 */
public class IndicatorGroupSet
    extends AbstractIdentifiableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 3051446168246358150L;

    private String description;

    private Boolean compulsory = false;

    private List<IndicatorGroup> members = new ArrayList<IndicatorGroup>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public IndicatorGroupSet()
    {
    }

    public IndicatorGroupSet( String name )
    {
        this.name = name;
        this.compulsory = false;
    }

    public IndicatorGroupSet( String name, Boolean compulsory )
    {
        this.name = name;
        this.compulsory = compulsory;
    }

    public IndicatorGroupSet( String name, String description, Boolean compulsory )
    {
        this.name = name;
        this.description = description;
        this.compulsory = compulsory;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public Collection<Indicator> getIndicators()
    {
        List<Indicator> indicators = new ArrayList<Indicator>();

        for ( IndicatorGroup group : members )
        {
            indicators.addAll( group.getMembers() );
        }

        return indicators;
    }

    public IndicatorGroup getGroup( Indicator indicator )
    {
        for ( IndicatorGroup group : members )
        {
            if ( group.getMembers().contains( indicator ) )
            {
                return group;
            }
        }

        return null;
    }

    public Boolean isMemberOfIndicatorGroups( Indicator indicator )
    {
        for ( IndicatorGroup group : members )
        {
            if ( group.getMembers().contains( indicator ) )
            {
                return true;
            }
        }

        return false;
    }

    public Boolean hasIndicatorGroups()
    {
        return members != null && members.size() > 0;
    }

    // -------------------------------------------------------------------------
    // equals and hashCode
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof IndicatorGroupSet) )
        {
            return false;
        }

        final IndicatorGroupSet other = (IndicatorGroupSet) o;

        return name.equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public Boolean isCompulsory()
    {
        return compulsory;
    }

    public void setCompulsory( Boolean compulsory )
    {
        this.compulsory = compulsory;
    }

    public List<IndicatorGroup> getMembers()
    {
        return members;
    }

    public void setMembers( List<IndicatorGroup> members )
    {
        this.members = members;
    }
}
