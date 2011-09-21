package org.hisp.dhis.user.action;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.attribute.comparator.AttributeNameComparator;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.system.filter.UserAuthorityGroupCanIssueFilter;
import org.hisp.dhis.system.util.AttributeUtils;
import org.hisp.dhis.system.util.FilterUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Nguyen Hong Duc
 * @version $Id: SetupTreeAction.java 5556 2008-08-20 11:36:20Z abyot $
 */
public class SetupTreeAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private AttributeService attributeService;

    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private UserCredentials userCredentials;

    public UserCredentials getUserCredentials()
    {
        return userCredentials;
    }

    private Collection<UserAuthorityGroup> userAuthorityGroups;

    public Collection<UserAuthorityGroup> getUserAuthorityGroups()
    {
        return userAuthorityGroups;
    }

    private List<OrganisationUnitGroup> organisationUnitGroups;

    public List<OrganisationUnitGroup> getOrganisationUnitGroups()
    {
        return organisationUnitGroups;
    }

    private List<Attribute> attributes;

    public List<Attribute> getAttributes()
    {
        return attributes;
    }

    public Map<Integer, String> attributeValues = new HashMap<Integer, String>();

    public Map<Integer, String> getAttributeValues()
    {
        return attributeValues;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        userAuthorityGroups = new ArrayList<UserAuthorityGroup>( userService.getAllUserAuthorityGroups() );

        FilterUtils.filter( userAuthorityGroups,
            new UserAuthorityGroupCanIssueFilter( currentUserService.getCurrentUser() ) );

        if ( id != null )
        {
            User user = userService.getUser( id );

            if ( user.getOrganisationUnits().size() > 0 )
            {
                selectionTreeManager.setSelectedOrganisationUnits( user.getOrganisationUnits() );
            }

            userCredentials = userService.getUserCredentials( userService.getUser( id ) );

            userAuthorityGroups.removeAll( userCredentials.getUserAuthorityGroups() );

            attributeValues = AttributeUtils.getAttributeValueMap( user.getAttributeValues() );
        }
        else
        {
            if ( selectionManager.getSelectedOrganisationUnits().size() > 0 )
            {
                selectionTreeManager.setSelectedOrganisationUnits( selectionManager.getSelectedOrganisationUnits() );
            }
        }

        attributes = new ArrayList<Attribute>( attributeService.getDataElementAttributes() );

        Collections.sort( attributes, new AttributeNameComparator() );

        return SUCCESS;
    }
}
