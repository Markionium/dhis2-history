package org.hisp.dhis.common;

/*
 * Copyright (c) 2004-2015, University of Oslo
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

import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementDomain;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.legend.LegendSet;
import org.hisp.dhis.program.ProgramIndicator;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;

/**
* @author Lars Helge Overland
*/
public class DataDimension
{
    public static final Set<Class<IdentifiableObject>> DATA_DIMENSION_CLASSES = IdentifiableObjectUtils.asTypedClassSet(   
        Indicator.class, DataElement.class, DataElementOperand.class, DataSet.class, ProgramIndicator.class, TrackedEntityAttribute.class );
    
    private int id;
    
    // -------------------------------------------------------------------------
    // Data dimension objects
    // -------------------------------------------------------------------------

    private Indicator indicator;
    
    private DataElement dataElement;
    
    private DataSet dataSet;
    
    private ProgramIndicator programIndicator;
    
    private TrackedEntityAttribute trackedEntityAttribute;

    // -------------------------------------------------------------------------
    // Context properties
    // -------------------------------------------------------------------------

    private LegendSet legendSet;
    
    private String filter;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public DataDimension()
    {
    }

    public static DataDimension create( NameableObject object )
    {
        DataDimension dimension = new DataDimension();
        
        if ( object.getClass().isAssignableFrom( Indicator.class ) )
        {
            dimension.setIndicator( (Indicator) object );
        }
        else if ( object.getClass().isAssignableFrom( DataElement.class ) )
        {
            dimension.setDataElement( (DataElement) object );
        }
        else if ( object.getClass().isAssignableFrom( DataSet.class ) )
        {
            dimension.setDataSet( (DataSet) object );
        }
        else if ( object.getClass().isAssignableFrom( ProgramIndicator.class ) )
        {
            dimension.setProgramIndicator( (ProgramIndicator) object );
        }
        else if ( object.getClass().isAssignableFrom( TrackedEntityAttribute.class ) )
        {
            dimension.setTrackedEntityAttribute( (TrackedEntityAttribute) object );
        }
        
        return dimension;
    }

    public static DataDimension create( NameableObject object, LegendSet legendSet, String filter )
    {
        DataDimension dimension = DataDimension.create( object );
        
        dimension.setLegendSet( legendSet );
        dimension.setFilter( filter );
        
        return dimension;
    }
    
    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public NameableObject getNameableObject()
    {
        if ( indicator != null )
        {
            return indicator;
        }
        else if ( dataElement != null )
        {
            return dataElement;
        }
        else if ( dataSet != null )
        {
            return dataSet;
        }
        else if ( programIndicator != null )
        {
            return programIndicator;
        }
        else if ( trackedEntityAttribute != null )
        {
            return trackedEntityAttribute;
        }
        
        throw new IllegalStateException( "Data dimension is null" );
    }
    
    public DimensionType getDimensionType()
    {
        if ( indicator != null )
        {
            return DimensionType.INDICATOR;
        }
        else if ( dataElement != null && DataElementDomain.AGGREGATE.equals( dataElement.getDomainType() ) )
        {
            return DimensionType.DATAELEMENT;
        }
        else if ( dataSet != null )
        {
            return DimensionType.DATASET;
        }
        else if ( programIndicator != null )
        {
            return DimensionType.PROGRAM_INDICATOR;
        }
        else if ( trackedEntityAttribute != null )
        {
            return DimensionType.PROGRAM_ATTRIBUTE;
        }
        else if ( dataElement != null && DataElementDomain.TRACKER.equals( dataElement.getDomainType() ) )
        {
            return DimensionType.PROGRAM_DATAELEMENT;
        }
        
        throw new IllegalStateException( "Data dimension is null" );
    }
    
    public String getUid()
    {
        NameableObject object = getNameableObject();
        
        return object != null ? object.getUid() : null;
    }
    
    // -------------------------------------------------------------------------
    // Get and set methods
    // -------------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public Indicator getIndicator()
    {
        return indicator;
    }

    public void setIndicator( Indicator indicator )
    {
        this.indicator = indicator;
    }

    public DataElement getDataElement()
    {
        return dataElement;
    }

    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
    }

    public DataSet getDataSet()
    {
        return dataSet;
    }

    public void setDataSet( DataSet dataSet )
    {
        this.dataSet = dataSet;
    }

    public ProgramIndicator getProgramIndicator()
    {
        return programIndicator;
    }

    public void setProgramIndicator( ProgramIndicator programIndicator )
    {
        this.programIndicator = programIndicator;
    }

    public TrackedEntityAttribute getTrackedEntityAttribute()
    {
        return trackedEntityAttribute;
    }

    public void setTrackedEntityAttribute( TrackedEntityAttribute trackedEntityAttribute )
    {
        this.trackedEntityAttribute = trackedEntityAttribute;
    }

    public LegendSet getLegendSet()
    {
        return legendSet;
    }

    public void setLegendSet( LegendSet legendSet )
    {
        this.legendSet = legendSet;
    }

    public String getFilter()
    {
        return filter;
    }

    public void setFilter( String filter )
    {
        this.filter = filter;
    }
}
