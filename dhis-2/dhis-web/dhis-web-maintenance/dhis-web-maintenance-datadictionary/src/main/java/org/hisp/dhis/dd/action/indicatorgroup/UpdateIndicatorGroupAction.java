package org.hisp.dhis.dd.action.indicatorgroup;

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
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.system.util.CodecUtils;

import com.opensymphony.xwork.ActionSupport;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: UpdateIndicatorGroupAction.java 3305 2007-05-14 18:55:52Z
 *          larshelg $
 */
public class UpdateIndicatorGroupAction
    extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    /*
     * This property use when using submit
     */

    private Collection<String> groupMembers;

    public void setGroupMembers( Collection<String> groupMembers )
    {
        this.groupMembers = groupMembers;
    }

    /*
     * This property use when using ajax
     */

    private String selectedIndicators[] = new String[0];

    public void setSelectedIndicators( String[] selectedIndicators )
    {
        this.selectedIndicators = selectedIndicators;
    }

    /*
     * This property is mode of user interface (normal or editor)
     */

    private String mode;

    public void setMode( String mode )
    {
        this.mode = mode;
    }

    private IndicatorGroup indicatorGroup;

    public IndicatorGroup getIndicatorGroup()
    {
        return indicatorGroup;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        indicatorGroup = indicatorService.getIndicatorGroup( id );

        if ( mode != null )
        {
            if ( mode.equalsIgnoreCase( "editor" ) )
            {
                if(name!=null){
                    if(name.length() > 0){
                        indicatorGroup.setName( CodecUtils.unescape( name ) );
                    }
                }                

            }
            else
            {
                indicatorGroup.setName( name );
            }
        }
        else
        {

            indicatorGroup.setName( name );

        }

        Set<Indicator> members = new HashSet<Indicator>();

        if ( groupMembers != null )
        {
            for ( String memberId : groupMembers )
            {
                members.add( indicatorService.getIndicator( Integer.parseInt( memberId ) ) );
            }
        }

        if ( selectedIndicators != null )
        {
            if ( selectedIndicators.length > 0 )
            {
                for ( int i = 0; i < selectedIndicators.length; i++ )
                {
                    Indicator indicator = indicatorService.getIndicator( Integer.parseInt( selectedIndicators[i] ) );
                    members.add( indicator );                   
                }

            }

        }

        indicatorGroup.setMembers( members );

        indicatorService.updateIndicatorGroup( indicatorGroup );

        return SUCCESS;
    }
}
