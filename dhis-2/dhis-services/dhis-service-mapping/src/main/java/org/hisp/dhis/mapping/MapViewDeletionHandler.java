package org.hisp.dhis.mapping;

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

import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.deletion.DeletionHandler;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class MapViewDeletionHandler
    extends DeletionHandler
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private MappingService mappingService;

    public void setMappingService( MappingService mappingService )
    {
        this.mappingService = mappingService;
    }

    // -------------------------------------------------------------------------
    // DeletionHandler implementation
    // -------------------------------------------------------------------------

    @Override
    protected String getClassName()
    {
        return MapView.class.getName();
    }
    
    @Override
    public boolean allowDeletePeriod( Period period )
    {
        for ( MapView mapView : mappingService.getAllMapViews() )
        {
            if ( mapView.getPeriod().equals( period ) )
            {
                return false;
            }
        }
        
        return true;
    }
        
    @Override
    public void deleteIndicatorGroup( IndicatorGroup indicatorGroup )
    {
        for ( MapView mapView : mappingService.getAllMapViews() )
        {
            if ( mapView.getIndicatorGroup().equals( indicatorGroup ) )
            {
                mappingService.deleteMapView( mapView );
            }
        }
    }
    
    @Override
    public void deleteIndicator( Indicator indicator )
    {
        for ( MapView mapView : mappingService.getAllMapViews() )
        {
            if ( mapView.getIndicator().equals( indicator ) )
            {
                mappingService.deleteMapView( mapView );
            }
        }
    }
    
    @Override
    public void deleteMap( Map map )
    {
        for ( MapView mapView : mappingService.getAllMapViews() )
        {
            if ( mapView.getMapSource().equals( map.getMapLayerPath() ) )
            {
                mappingService.deleteMapView( mapView );
            }
        }
    }
    
    @Override
    public void deleteMapLegendSet( MapLegendSet mapLegendSet )
    {
        for ( MapView mapView : mappingService.getAllMapViews() )
        {
            if ( mapView.getMapLegendSet() != null && mapView.getMapLegendSet().equals( mapLegendSet ) ) // MapLegendSet is optional
            {
                mappingService.deleteMapView( mapView );
            }
        }
    }
}
