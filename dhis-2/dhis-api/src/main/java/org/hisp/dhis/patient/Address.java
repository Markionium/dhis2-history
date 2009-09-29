/*
 * Copyright (c) 2004-2009, University of Oslo
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

package org.hisp.dhis.patient;


/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class Address
{
    private Integer id;

    private String address1;

    private String address2;

    private String landMark;

    private String cityVillage;

    private String stateProvince;

    private String country;

    private String postalCode;

    private Boolean preferred = false;

    private Patient patient;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Address()
    {
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    public int hashCode()
    {
        return id.hashCode();
    }

    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }

        if ( obj == null )
        {
            return false;
        }

        if ( !(obj instanceof Address) )
        {
            return false;
        }

        final Address other = (Address) obj;

        return id.equals( other.getId() );
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public Integer getId()
    {
        return id;
    }
    
    public void setId( Integer id )
    {
        this.id = id;
    }
    
    public String getAddress1()
    {
        return address1;
    }

    public void setAddress1( String address1 )
    {
        this.address1 = address1;
    }

    public String getAddress2()
    {
        return address2;
    }
    
    public void setAddress2( String address2 )
    {
        this.address2 = address2;
    }

    public String getCityVillage()
    {
        return cityVillage;
    }

    public void setCityVillage( String cityVillage )
    {
        this.cityVillage = cityVillage;
    }

    public String getStateProvince()
    {
        return stateProvince;
    }

    public void setStateProvince( String stateProvince )
    {
        this.stateProvince = stateProvince;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry( String country )
    {
        this.country = country;
    }

    public String getPostalCode()
    {
        return postalCode;
    }

    public void setPostalCode( String postalCode )
    {
        this.postalCode = postalCode;
    }

    public Boolean getPreferred()
    {
        return preferred;
    }
    
    public void setPreferred( Boolean preferred )
    {
        this.preferred = preferred;
    }

    public Patient getPatient()
    {
        return patient;
    }

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    public void setLandMark( String landMark )
    {
        this.landMark = landMark;
    }

    public String getLandMark()
    {
        return landMark;
    }
}
