package org.hisp.dhis.dxf2.events.trackedentity;

/*
 * Copyright (c) 2004-2014, University of Oslo
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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.hisp.dhis.dxf2.importsummary.ImportSummaries;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.importexport.ImportStrategy;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Transactional
public class JacksonTrackedEntityInstanceService extends AbstractTrackedEntityInstanceService
{
    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------

    private final static ObjectMapper xmlMapper = new XmlMapper();

    private final static ObjectMapper jsonMapper = new ObjectMapper();

    @SuppressWarnings( "unchecked" )
    private static <T> T fromXml( InputStream inputStream, Class<?> clazz ) throws IOException
    {
        return (T) xmlMapper.readValue( inputStream, clazz );
    }

    @SuppressWarnings( "unchecked" )
    private static <T> T fromXml( String input, Class<?> clazz ) throws IOException
    {
        return (T) xmlMapper.readValue( input, clazz );
    }

    @SuppressWarnings( "unchecked" )
    private static <T> T fromJson( InputStream inputStream, Class<?> clazz ) throws IOException
    {
        return (T) jsonMapper.readValue( inputStream, clazz );
    }

    @SuppressWarnings( "unchecked" )
    private static <T> T fromJson( String input, Class<?> clazz ) throws IOException
    {
        return (T) jsonMapper.readValue( input, clazz );
    }

    static
    {
        xmlMapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true );
        xmlMapper.configure( DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true );
        xmlMapper.configure( DeserializationFeature.WRAP_EXCEPTIONS, true );
        jsonMapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true );
        jsonMapper.configure( DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true );
        jsonMapper.configure( DeserializationFeature.WRAP_EXCEPTIONS, true );
    }

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    @Override
    public ImportSummaries addTrackedEntityInstanceXml( InputStream inputStream, ImportStrategy strategy ) throws IOException
    {
        ImportSummaries importSummaries = new ImportSummaries();
        String input = StreamUtils.copyToString( inputStream, Charset.forName( "UTF-8" ) );

        TrackedEntityInstances create = new TrackedEntityInstances();
        TrackedEntityInstances update = new TrackedEntityInstances();

        try
        {
            TrackedEntityInstances fromXml = fromXml( input, TrackedEntityInstances.class );

            if ( strategy.isCreate() )
            {
                create.getTrackedEntityInstances().addAll( fromXml.getTrackedEntityInstances() );
            }
            else if ( strategy.isCreateAndUpdate() )
            {
                for ( TrackedEntityInstance trackedEntityInstance : fromXml.getTrackedEntityInstances() )
                {
                    if ( StringUtils.isEmpty( trackedEntityInstance.getTrackedEntityInstance() ) )
                    {
                        create.getTrackedEntityInstances().add( trackedEntityInstance );
                    }
                    else
                    {
                        if ( teiService.getTrackedEntityInstance( trackedEntityInstance.getTrackedEntityInstance() ) == null )
                        {
                            create.getTrackedEntityInstances().add( trackedEntityInstance );
                        }
                        else
                        {
                            update.getTrackedEntityInstances().add( trackedEntityInstance );
                        }
                    }
                }
            }

        }
        catch ( Exception ex )
        {
            TrackedEntityInstance fromXml = fromXml( input, TrackedEntityInstance.class );

            if ( strategy.isCreate() )
            {
                create.getTrackedEntityInstances().add( fromXml );
            }
            else if ( strategy.isCreateAndUpdate() )
            {
                if ( StringUtils.isEmpty( fromXml.getTrackedEntityInstance() ) )
                {
                    create.getTrackedEntityInstances().add( fromXml );
                }
                else
                {
                    if ( teiService.getTrackedEntityInstance( fromXml.getTrackedEntityInstance() ) == null )
                    {
                        create.getTrackedEntityInstances().add( fromXml );
                    }
                    else
                    {
                        update.getTrackedEntityInstances().add( fromXml );
                    }
                }
            }
        }

        for ( TrackedEntityInstance trackedEntityInstance : create.getTrackedEntityInstances() )
        {
            importSummaries.addImportSummary( addTrackedEntityInstance( trackedEntityInstance ) );
        }

        for ( TrackedEntityInstance trackedEntityInstance : update.getTrackedEntityInstances() )
        {
            importSummaries.addImportSummary( updateTrackedEntityInstance( trackedEntityInstance ) );
        }

        return importSummaries;
    }

    @Override
    public ImportSummaries addTrackedEntityInstanceJson( InputStream inputStream, ImportStrategy strategy ) throws IOException
    {
        ImportSummaries importSummaries = new ImportSummaries();
        String input = StreamUtils.copyToString( inputStream, Charset.forName( "UTF-8" ) );

        TrackedEntityInstances create = new TrackedEntityInstances();
        TrackedEntityInstances update = new TrackedEntityInstances();

        try
        {
            TrackedEntityInstances fromJson = fromJson( input, TrackedEntityInstances.class );

            if ( strategy.isCreate() )
            {
                create.getTrackedEntityInstances().addAll( fromJson.getTrackedEntityInstances() );
            }
            else if ( strategy.isCreateAndUpdate() )
            {
                for ( TrackedEntityInstance trackedEntityInstance : fromJson.getTrackedEntityInstances() )
                {
                    if ( StringUtils.isEmpty( trackedEntityInstance.getTrackedEntityInstance() ) )
                    {
                        create.getTrackedEntityInstances().add( trackedEntityInstance );
                    }
                    else
                    {
                        if ( teiService.getTrackedEntityInstance( trackedEntityInstance.getTrackedEntityInstance() ) == null )
                        {
                            create.getTrackedEntityInstances().add( trackedEntityInstance );
                        }
                        else
                        {
                            update.getTrackedEntityInstances().add( trackedEntityInstance );
                        }
                    }
                }
            }

        }
        catch ( Exception ex )
        {
            TrackedEntityInstance fromJson = fromJson( input, TrackedEntityInstance.class );

            if ( strategy.isCreate() )
            {
                create.getTrackedEntityInstances().add( fromJson );
            }
            else if ( strategy.isCreateAndUpdate() )
            {
                if ( StringUtils.isEmpty( fromJson.getTrackedEntityInstance() ) )
                {
                    create.getTrackedEntityInstances().add( fromJson );
                }
                else
                {
                    if ( teiService.getTrackedEntityInstance( fromJson.getTrackedEntityInstance() ) == null )
                    {
                        create.getTrackedEntityInstances().add( fromJson );
                    }
                    else
                    {
                        update.getTrackedEntityInstances().add( fromJson );
                    }
                }
            }
        }

        for ( TrackedEntityInstance trackedEntityInstance : create.getTrackedEntityInstances() )
        {
            importSummaries.addImportSummary( addTrackedEntityInstance( trackedEntityInstance ) );
        }

        for ( TrackedEntityInstance trackedEntityInstance : update.getTrackedEntityInstances() )
        {
            importSummaries.addImportSummary( updateTrackedEntityInstance( trackedEntityInstance ) );
        }

        return importSummaries;
    }

    @Override
    public ImportSummaries addTrackedEntityInstanceXml( InputStream inputStream ) throws IOException
    {
        ImportSummaries importSummaries = new ImportSummaries();
        String input = StreamUtils.copyToString( inputStream, Charset.forName( "UTF-8" ) );

        TrackedEntityInstances trackedEntityInstances = new TrackedEntityInstances();

        try
        {
            TrackedEntityInstances fromXml = fromXml( input, TrackedEntityInstances.class );
            trackedEntityInstances.getTrackedEntityInstances().addAll( fromXml.getTrackedEntityInstances() );
        }
        catch ( Exception ex )
        {
            TrackedEntityInstance fromXml = fromXml( input, TrackedEntityInstance.class );
            trackedEntityInstances.getTrackedEntityInstances().add( fromXml );
        }

        for ( TrackedEntityInstance trackedEntityInstance : trackedEntityInstances.getTrackedEntityInstances() )
        {
            importSummaries.addImportSummary( addTrackedEntityInstance( trackedEntityInstance ) );
        }

        return importSummaries;
    }

    @Override
    public ImportSummaries addTrackedEntityInstanceJson( InputStream inputStream ) throws IOException
    {
        ImportSummaries importSummaries = new ImportSummaries();
        String input = StreamUtils.copyToString( inputStream, Charset.forName( "UTF-8" ) );

        TrackedEntityInstances trackedEntityInstances = new TrackedEntityInstances();

        try
        {
            TrackedEntityInstances fromJson = fromJson( input, TrackedEntityInstances.class );
            trackedEntityInstances.getTrackedEntityInstances().addAll( fromJson.getTrackedEntityInstances() );
        }
        catch ( Exception ex )
        {
            TrackedEntityInstance fromJson = fromJson( input, TrackedEntityInstance.class );
            trackedEntityInstances.getTrackedEntityInstances().add( fromJson );
        }

        for ( TrackedEntityInstance trackedEntityInstance : trackedEntityInstances.getTrackedEntityInstances() )
        {
            importSummaries.addImportSummary( addTrackedEntityInstance( trackedEntityInstance ) );
        }

        return importSummaries;
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    @Override
    public ImportSummary updateTrackedEntityInstanceXml( String id, InputStream inputStream ) throws IOException
    {
        TrackedEntityInstance trackedEntityInstance = fromXml( inputStream, TrackedEntityInstance.class );
        trackedEntityInstance.setTrackedEntityInstance( id );

        return updateTrackedEntityInstance( trackedEntityInstance );
    }

    @Override
    public ImportSummary updateTrackedEntityInstanceJson( String id, InputStream inputStream ) throws IOException
    {
        TrackedEntityInstance trackedEntityInstance = fromJson( inputStream, TrackedEntityInstance.class );
        trackedEntityInstance.setTrackedEntityInstance( id );

        return updateTrackedEntityInstance( trackedEntityInstance );
    }
}
