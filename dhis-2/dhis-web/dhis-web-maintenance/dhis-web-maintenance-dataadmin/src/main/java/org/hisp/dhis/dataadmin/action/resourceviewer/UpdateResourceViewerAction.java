package org.hisp.dhis.dataadmin.action.resourceviewer;

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

import org.hisp.dhis.resourceviewer.ResourceViewer;
import org.hisp.dhis.resourceviewer.ResourceViewerService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Updates a existing resource viewer in database.
 * 
 * @author Dang Duy Hieu
 * @version $Id$
 * @since 2010-07-06
 */
public class UpdateResourceViewerAction
    extends ActionSupport
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ResourceViewerService resourceViewerService;

    public void setResourceViewerService( ResourceViewerService resourceViewerService )
    {
        this.resourceViewerService = resourceViewerService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }

    private String sqlquery;

    public void setSqlquery( String sqlquery )
    {
        this.sqlquery = sqlquery;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {

        if ( id == null || (id.intValue() == -1) )
        {
            return ERROR;
        }

        ResourceViewer resourceViewerInstance = resourceViewerService.getResourceViewer( id );

        resourceViewerInstance.setDescription( description.replaceAll( "\\s+", " " ).trim() );
        resourceViewerInstance.setSqlQuery( resourceViewerService.makeUpForQueryStatement( sqlquery ) );

        resourceViewerService.updateResourceViewer( resourceViewerInstance );

        return SUCCESS;
    }
}
