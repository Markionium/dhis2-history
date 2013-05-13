package org.hisp.dhis.common;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import org.hisp.dhis.common.view.DimensionalView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement( localName = "dimensionalObject", namespace = DxfNamespaces.DXF_2_0)
public class BaseDimensionalObject
    extends BaseIdentifiableObject implements DimensionalObject
{
    /**
     * The type of this dimension.
     */
    private DimensionType type;
    
    /**
     * The dimensional items for this dimension.
     */
    private List<IdentifiableObject> items = new ArrayList<IdentifiableObject>();
    
    /**
     * Indicates whether this object should be considered a data dimension. Assumes
     * true by default.
     */
    private boolean dataDimension = true;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    public BaseDimensionalObject()
    {        
    }
    
    public BaseDimensionalObject( String dimension, DimensionType type, List<? extends IdentifiableObject> items )
    {
        this.uid = dimension;
        this.type = type;
        this.items = new ArrayList<IdentifiableObject>( items );        
    }

    //--------------------------------------------------------------------------
    // Getters and setters
    //--------------------------------------------------------------------------

    @JsonProperty
    @JsonView( {DimensionalView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public DimensionType getType()
    {
        return type;
    }

    public void setType( DimensionType type )
    {
        this.type = type;
    }

    @JsonProperty
    @JsonView( {DimensionalView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getDimension()
    {
        return uid;
    }

    public void setDimension( String dimension )
    {
        this.uid = dimension;
    }

    @Override
    @JsonProperty
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JsonDeserialize( contentAs = BaseIdentifiableObject.class )
    @JsonView( { DimensionalView.class } )
    @JacksonXmlElementWrapper( localName = "items", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "item", namespace = DxfNamespaces.DXF_2_0 )
    public List<IdentifiableObject> getItems()
    {
        return items;
    }

    public void setItems( List<IdentifiableObject> items )
    {
        this.items = items;
    }

    public boolean isDataDimension()
    {
        return dataDimension;
    }

    public void setDataDimension( boolean dataDimension )
    {
        this.dataDimension = dataDimension;
    }
    
    //--------------------------------------------------------------------------
    // Supportive methods
    //--------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "[" + uid + ", " + items + "]";
    }
}
