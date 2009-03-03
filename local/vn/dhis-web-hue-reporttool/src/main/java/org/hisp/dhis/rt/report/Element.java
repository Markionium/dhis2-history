package org.hisp.dhis.rt.report;

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

/**
 * @author Lars Helge Overland
 * @version $Id: Element.java 2871 2007-02-20 16:04:11Z andegje $
 */
public class Element
{
    public static final String SEPARATOR = ":";
    
    private String id;
    
    private String type;
    
    private int elementId;
    
    private String elementName;
    
    public Element( String type, int elementId, String elementName )
    {
        this.id = type + SEPARATOR + elementId;
        this.type = type;
        this.elementId = elementId;
        this.elementName = elementName;
    }

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public int getElementId()
    {
        return elementId;
    }

    public void setElementId( int elementId )
    {
        this.elementId = elementId;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }
    
    public String getElementName()
    {
        return elementName;
    }

    public void setElementName( String elementName )
    {
        this.elementName = elementName;
    }

    public String getElementKey()
    {
        return this.type + SEPARATOR + this.elementName;        
    }

    public boolean equals( Object object )
    {
        if( this == object )
        {
            return true;
        }
        
        if( ( object == null) || ( object.getClass() != this.getClass() ) )
        {
            return false;
        }
        
        Element element = (Element) object;
        
        return ( id == element.id && type == element.type && 
            elementId == element.elementId );            
    }

    public String getElementKeyForChild(String shortNameOfChild)
    {
        return this.type + SEPARATOR + shortNameOfChild + SEPARATOR + this.elementName;        
    }
}
