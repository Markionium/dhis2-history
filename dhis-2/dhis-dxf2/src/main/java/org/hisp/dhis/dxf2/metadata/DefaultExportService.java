package org.hisp.dhis.dxf2.metadata;

/*
 * Copyright (c) 2012, University of Oslo
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

import java.io.IOException;
import java.util.*;

import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.filter.Filter;
import org.hisp.dhis.filter.FilterService;
import org.hisp.dhis.scheduling.TaskId;
import org.hisp.dhis.system.notification.NotificationLevel;
import org.hisp.dhis.system.notification.Notifier;
import org.hisp.dhis.system.util.ReflectionUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Service
public class DefaultExportService
        implements ExportService
{
    private static final Log log = LogFactory.getLog( DefaultExportService.class );
    //-------------------------------------------------------------------------------------------------------
    // Dependencies
    //-------------------------------------------------------------------------------------------------------
    @Autowired
    protected IdentifiableObjectManager manager;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private Notifier notifier;

    @Autowired
    private FilterService filterService;

    @Autowired
    private DataElementService dataElementService;

    //-------------------------------------------------------------------------------------------------------
    // ExportService Implementation - MetaData
    //-------------------------------------------------------------------------------------------------------

    @Override
    public MetaData getMetaData( Options options )
    {
        return getMetaData( options, null );
    }

    @Override
    public MetaData getMetaData( Options options, TaskId taskId )
    {
        MetaData metaData = new MetaData();
        metaData.setCreated( new Date() );

        log.info( "User '" + currentUserService.getCurrentUsername() + "' started export at " + new Date() );

        Date lastUpdated = options.getLastUpdated();

        if ( taskId != null )
        {
            notifier.notify( taskId, "Exporting meta-data" );
        }

        for ( Map.Entry<Class<? extends IdentifiableObject>, String> entry : ExchangeClasses.getExportMap().entrySet() )
        {
            if ( !options.isEnabled( entry.getValue() ) )
            {
                continue;
            }

            Class<? extends IdentifiableObject> idObjectClass = entry.getKey();

            Collection<? extends IdentifiableObject> idObjects;

            if ( lastUpdated != null )
            {
                idObjects = manager.getByLastUpdated( idObjectClass, lastUpdated );
            } else
            {
                idObjects = manager.getAll( idObjectClass );
            }

            if ( idObjects.isEmpty() )
            {
                continue;
            }

            String message = "Exporting " + idObjects.size() + " " + StringUtils.capitalize( entry.getValue() );

            log.info( message );

            if ( taskId != null )
            {
                notifier.notify( taskId, message );
            }

            ReflectionUtils.invokeSetterMethod( entry.getValue(), metaData, new ArrayList<IdentifiableObject>( idObjects ) );
        }

        log.info( "Export done at " + new Date() );

        if ( taskId != null )
        {
            notifier.notify( taskId, NotificationLevel.INFO, "Export done", true );
        }

        return metaData;
    }

    //-------------------------------------------------------------------------------------------------------
    // ExportService Implementation - Detailed MetaData
    //-------------------------------------------------------------------------------------------------------

    // Ovidiu
    @Override
    public MetaData getFilteredMetaData( FilterOptions filterOptions )
    {
        return getFilteredMetaData( filterOptions, null );
    }

    @Override
    public MetaData getFilteredMetaData( FilterOptions filterOptions, TaskId taskId )
    {
        MetaData metaData = new MetaData();
        metaData.setCreated( new Date() );

        log.info( "User '" + currentUserService.getCurrentUsername() + "' started export at " + new Date() );

        if ( taskId != null )
        {
            notifier.notify( taskId, "Exporting meta-data" );
        }

        for ( Map.Entry<String, List<String>> restrictionEntry : filterOptions.getRestrictions().entrySet() )
        {
            String className = restrictionEntry.getKey();
            for ( Map.Entry<Class<? extends IdentifiableObject>, String> entry : ExchangeClasses.getExportMap().entrySet() )
            {
                if ( entry.getValue().equals( className ) )
                {
                    Class<? extends IdentifiableObject> idObjectClass = entry.getKey();
                    Collection<? extends IdentifiableObject> idObjects;
                    idObjects = manager.getByUid( idObjectClass, restrictionEntry.getValue() );

                    if ( idObjects.isEmpty() )
                    {
                        continue;
                    }

                    String message = "Exporting " + idObjects.size() + " " + StringUtils.capitalize( entry.getValue() );
                    log.info( message );

                    if ( taskId != null )
                    {
                        notifier.notify( taskId, message );
                    }

                    ReflectionUtils.invokeSetterMethod( entry.getValue(), metaData, new ArrayList<IdentifiableObject>( idObjects ) );

                    log.info( "Export done at " + new Date() );

                    if ( taskId != null )
                    {
                        notifier.notify( taskId, NotificationLevel.INFO, "Export done", true );
                    }
                }
            }
        }

        return metaData;
    }

    @Override
    public List<Filter> getFilters()
    {
        return (List<Filter>) filterService.getAllFilters();
    }

    @Override
    public void saveFilter( JSONObject json ) throws IOException
    {
        Filter filter = new Filter( json.getString( "name" ) );
        filter.setCode( json.getString( "code" ) );
        filter.setMetaDataUids( json.getString( "metaDataUids" ) );
        filter.setAutoFields();

        filterService.saveFilter( filter );
    }

    @Override
    public void updateFilter( JSONObject json ) throws IOException
    {
        Filter filter = filterService.getFilterByUid( json.getString( "uid" ) );
        filter.setName( json.getString( "name" ) );
        filter.setCode( json.getString( "code" ) );
        filter.setMetaDataUids( json.getString( "metaDataUids" ) );
        filter.setLastUpdated( new Date() );

        filterService.updateFilter( filter );
    }

    @Override
    public void deleteFilter( JSONObject json ) throws IOException
    {
//        List<DataElementGroup> dataElementGroups = new ArrayList<DataElementGroup>();
//
//        dataElementGroups.add( dataElementService.getDataElementGroup( "oDkJh5Ddh7d" ) );
//        dataElementGroups.add( dataElementService.getDataElementGroup( "qfxEYY9xAl6" ) );
//
//        MetaDataDependencies metaDataDependencies = new MetaDataDependencies();
//
//        Map<Class<? extends IdentifiableObject>, Set<String>> uids = metaDataDependencies.getAllDependencyUids( dataElementGroups );
//
//        System.out.println("Break");
        Filter filter = filterService.getFilterByUid( json.getString( "uid" ) );

        filterService.deleteFilter( filter );
    }
}
