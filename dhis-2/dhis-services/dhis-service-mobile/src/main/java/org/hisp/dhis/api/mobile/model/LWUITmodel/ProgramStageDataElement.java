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
package org.hisp.dhis.api.mobile.model.LWUITmodel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import org.hisp.dhis.api.mobile.model.Model;
import org.hisp.dhis.api.mobile.model.ModelList;
import org.hisp.dhis.api.mobile.model.OptionSet;

 /**
 * @author Nguyen Kim Lai
 *
 * @version ProgramStageDataElement.java 11:21:39 AM Jan 30, 2013 $
 */
public class ProgramStageDataElement extends Model
{
    private String clientVersion;
    
    private String type;
    
    private boolean compulsory;

    private ModelList categoryOptionCombos;

    private OptionSet optionSet;
    
    private String value;
    
    @Override
    public void serialize( DataOutputStream dout )
        throws IOException
    {
        dout.writeUTF( this.getType() );
        dout.writeBoolean( this.isCompulsory() );
        
        List<Model> cateOptCombos = this.getCategoryOptionCombos().getModels();
        if ( cateOptCombos == null || cateOptCombos.size() <= 0 )
        {
            dout.writeInt( 0 );
        }
        else
        {
            dout.writeInt( cateOptCombos.size() );
            for ( Model each : cateOptCombos )
            {
                each.setClientVersion( TWO_POINT_NINE );
                each.serialize( dout );
            }
        }
        
        if ( optionSet == null || optionSet.getOptions().size() <= 0 )
        {
            dout.writeInt( 0 );
        }
        else
        {
            optionSet.setClientVersion( TWO_POINT_NINE );
            optionSet.serialize( dout );
        }
        
        dout.writeUTF( this.getValue() );
    }
    
    @Override
    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        // TODO Auto-generated method stub
        super.deSerialize( dataInputStream );
    }
    
    
    public String getClientVersion()
    {
        return clientVersion;
    }

    public void setClientVersion( String clientVersion )
    {
        this.clientVersion = clientVersion;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }
    
    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public boolean isCompulsory()
    {
        return compulsory;
    }

    public void setCompulsory( boolean compulsory )
    {
        this.compulsory = compulsory;
    }

    public ModelList getCategoryOptionCombos()
    {
        return categoryOptionCombos;
    }

    public void setCategoryOptionCombos( ModelList categoryOptionCombos )
    {
        this.categoryOptionCombos = categoryOptionCombos;
    }

    public OptionSet getOptionSet()
    {
        return optionSet;
    }

    public void setOptionSet( OptionSet optionSet )
    {
        this.optionSet = optionSet;
    }
}
