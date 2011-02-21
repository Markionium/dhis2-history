package org.hisp.dhis.web.api.resources;

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
import java.util.Collections;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.web.api.model.ActivityPlan;
import org.hisp.dhis.web.api.model.ActivityValue;
import org.hisp.dhis.web.api.model.DataSet;
import org.hisp.dhis.web.api.model.DataSetList;
import org.hisp.dhis.web.api.model.DataSetValue;
import org.hisp.dhis.web.api.model.MobileModel;
import org.hisp.dhis.web.api.model.ModelList;
import org.hisp.dhis.web.api.model.OrgUnit;
import org.hisp.dhis.web.api.service.ActivityReportingService;
import org.hisp.dhis.web.api.service.ActivityReportingServiceImpl;
import org.hisp.dhis.web.api.service.FacilityReportingService;
import org.hisp.dhis.web.api.service.IProgramService;
import org.hisp.dhis.web.api.service.NotAllowedException;
import org.springframework.beans.factory.annotation.Required;

@Produces( { DhisMediaType.MOBILE_SERIALIZED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
@Consumes( { DhisMediaType.MOBILE_SERIALIZED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
@Path( "/orgUnits/{id}" )
public class OrgUnitResource
{
    private OrganisationUnitService organisationUnitService;
    
    private static Log log = LogFactory.getLog( ActivityReportingServiceImpl.class );

    private static final boolean DEBUG = log.isDebugEnabled();

    private IProgramService programService;

    private ActivityReportingService activityReportingService;

    private FacilityReportingService facilityReportingService;

    @PathParam( "id" ) private int id;

    @Context UriInfo uriInfo;
    
    private OrganisationUnit getUnit() {
        return organisationUnitService.getOrganisationUnit( id );
    }
    
    @GET
    public OrgUnit getOrgUnit() {
        return OrgUnitResource.getOrgUnit( getUnit(), uriInfo );
    }
    
    /**
     * Get activity plan, program forms and facility forms wrapped in a
     * {@link MobileModel}
     * 
     * @param locale - localize for the given locale
     */
    @GET
    @Path( "all" )
    public MobileModel getAllDataForOrgUnit( @HeaderParam( "accept-language" ) String locale )
    {
        MobileModel mobileModel = new MobileModel();

        if ( DEBUG )
            log.debug( "Getting all resources for org unit " + getUnit().getName() );

        mobileModel.setActivityPlan( activityReportingService.getCurrentActivityPlan( getUnit(), locale ) );
        mobileModel.setPrograms( programService.getPrograms( getUnit(), locale ) );
        mobileModel.setDatasets( facilityReportingService.getMobileDataSetsForUnit( getUnit(), locale ) );

        return mobileModel;
    }

    @POST
    @Path( "updateDataSets" )
    public DataSetList checkUpdatedDataSet( DataSetList dataSetList, @HeaderParam( "accept-language" ) String locale )
    {
        if ( DEBUG )
            log.debug( "Checking updated datasets for org unit " + getUnit().getName() );
        DataSetList updatedDataSetList = new DataSetList();
        List<DataSet> dataSets = facilityReportingService.getMobileDataSetsForUnit( getUnit(), locale );
        List<DataSet> currentDataSets = dataSetList.getCurrentDataSets();
        // List<DataSet> copyCurrentDataSets = new
        // ArrayList<DataSet>(currentDataSets.size());
        // Collections.copy( copyCurrentDataSets, currentDataSets );
        // check added dataset
        for ( DataSet dataSet : dataSets )
        {
            if ( !currentDataSets.contains( dataSet ) )
            {
                if ( updatedDataSetList.getAddedDataSets() == null )
                    updatedDataSetList.setAddedDataSets( new ArrayList<DataSet>() );
                updatedDataSetList.getAddedDataSets().add( dataSet );
                currentDataSets.add( dataSet );
            }
        }

        // check deleted dataset
        for ( DataSet dataSet : currentDataSets )
        {
            if ( !dataSets.contains( dataSet ) )
            {
                if ( updatedDataSetList.getDeletedDataSets() == null )
                    updatedDataSetList.setDeletedDataSets( new ArrayList<DataSet>() );
                updatedDataSetList.getDeletedDataSets().add( new DataSet( dataSet ) );
            }
        }
        if ( updatedDataSetList.getDeletedDataSets() != null )
        {
            for ( DataSet dataSet : updatedDataSetList.getDeletedDataSets() )
            {
                currentDataSets.remove( dataSet );
            }
        }

        // check modified dataset
        Collections.sort( dataSets );
        Collections.sort( currentDataSets );

        for ( int i = 0; i < dataSets.size(); i++ )
        {
            if ( dataSets.get( i ).getVersion() != currentDataSets.get( i ).getVersion() )
            {
                if ( updatedDataSetList.getModifiedDataSets() == null )
                    updatedDataSetList.setModifiedDataSets( new ArrayList<DataSet>() );
                updatedDataSetList.getModifiedDataSets().add( dataSets.get( i ) );
            }
        }
        
        if ( DEBUG )
            log.debug( "Returning updated datasets for org unit " + getUnit().getName() );

        return updatedDataSetList;
    }

    /**
     * Save a facility report for unit
     * 
     * @param dataSetValue - the report to save
     * @throws NotAllowedException if the {@link DataSetValue} is invalid
     */
    @POST
    @Path( "dataSets" )
    public void saveDataSetValues( DataSetValue dataSetValue )
        throws NotAllowedException
    {
        facilityReportingService.saveDataSetValues( getUnit(), dataSetValue );
    }

    /**
     * Save activity report for unit
     * 
     * @param activityValue - the report to save
     * @throws NotAllowedException if the {@link ActivityValue activity value}
     *         is invalid
     */
    @POST
    @Path( "activities" )
    public void saveActivityReport( ActivityValue activityValue )
        throws NotAllowedException
    {
        activityReportingService.saveActivityReport( getUnit(), activityValue );
    }

    @POST
    @Path( "activitiyplan" )
    public MobileModel updatePrograms( @HeaderParam( "accept-language" ) String locale, ModelList programsFromClient )
    {
        MobileModel model = new MobileModel();
        model.setPrograms( programService.updateProgram( programsFromClient, locale, getUnit() ) );
		model.setActivityPlan(activityReportingService.getCurrentActivityPlan( getUnit(), locale ));
        return model;
    }

    public static OrgUnit getOrgUnit( OrganisationUnit unit, UriInfo uriInfo )
    {
        OrgUnit orgUnit = new OrgUnit();

        orgUnit.setId( unit.getId() );
        orgUnit.setName( unit.getShortName() );

        orgUnit.setDownloadAllUrl( getOrgUnitUrlBuilder(uriInfo).path( "all" )
            .build( unit.getId() ).toString() );
        orgUnit.setUpdateActivityPlanUrl( getOrgUnitUrlBuilder(uriInfo).path( "activitiyplan" )
            .build( unit.getId() ).toString() );
        orgUnit.setUploadFacilityReportUrl( getOrgUnitUrlBuilder(uriInfo).path( "dataSets" )
            .build( unit.getId() ).toString() );
        orgUnit.setUploadActivityReportUrl( getOrgUnitUrlBuilder(uriInfo).path( "activities" )
            .build( unit.getId() ).toString() );
        orgUnit.setUpdateDataSetUrl( getOrgUnitUrlBuilder(uriInfo).path( "updateDataSets" )
            .build( unit.getId() ).toString() );
        return orgUnit;
    }

    private static UriBuilder getOrgUnitUrlBuilder(UriInfo uriInfo)
    {
        return uriInfo.getBaseUriBuilder().path( "/orgUnits/{id}" );
    }

    
    
    @Required
    public void setProgramService( IProgramService programService )
    {
        this.programService = programService;
    }

    @Required
    public void setActivityReportingService( ActivityReportingService activityReportingService )
    {
        this.activityReportingService = activityReportingService;
    }

    @Required
    public void setFacilityReportingService( FacilityReportingService facilityReportingService )
    {
        this.facilityReportingService = facilityReportingService;
    }

    @Required
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

}
