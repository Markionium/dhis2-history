package org.hisp.dhis.dd.action.indicatorgroup;

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

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.attribute.comparator.AttributeSortOrderComparator;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.system.util.AttributeUtils;

import java.util.*;

/**
 * @author Chau Thu Tran
 * @version $ ShowUpdateIndicatorGroupFormAction.java May 30, 2011 3:01:41 PM $
 */
public class ShowUpdateIndicatorGroupFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private AttributeService attributeService;

    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private IndicatorGroup indicatorGroup;

    public IndicatorGroup getIndicatorGroup()
    {
        return indicatorGroup;
    }

    private List<Indicator> groupMembers = new ArrayList<Indicator>();

    public List<Indicator> getGroupMembers()
    {
        return groupMembers;
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
    {
        indicatorGroup = indicatorService.getIndicatorGroup( id, true );

        groupMembers = new ArrayList<Indicator>( indicatorGroup.getMembers() );

        attributes = new ArrayList<Attribute>( attributeService.getIndicatorGroupAttributes() );

        attributeValues = AttributeUtils.getAttributeValueMap( indicatorGroup.getAttributeValues() );

        Collections.sort( groupMembers, new IdentifiableObjectNameComparator() );
        Collections.sort( attributes, new AttributeSortOrderComparator() );

        return SUCCESS;
    }
}