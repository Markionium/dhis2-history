package org.hisp.dhis.importexport.importer;

import org.amplecode.quick.BatchHandler;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.Importer;
import org.hisp.dhis.importexport.mapping.NameMappingUtil;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;

import java.util.List;

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

public class OrganisationUnitGroupImporter
    extends AbstractImporter<OrganisationUnitGroup> implements Importer<OrganisationUnitGroup>
{
    protected OrganisationUnitGroupService organisationUnitGroupService;

    public OrganisationUnitGroupImporter()
    {
    }

    public OrganisationUnitGroupImporter( BatchHandler<OrganisationUnitGroup> batchHandler, OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.batchHandler = batchHandler;
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    @Override
    public void importObject( OrganisationUnitGroup object, ImportParams params )
    {
        NameMappingUtil.addOrganisationUnitGroupMapping( object.getId(), object.getName() );

        read( object, GroupMemberType.NONE, params );
    }

    @Override
    protected void importUnique( OrganisationUnitGroup object )
    {
        batchHandler.addObject( object );
    }

    @Override
    protected void importMatching( OrganisationUnitGroup object, OrganisationUnitGroup match )
    {
        match.setName( object.getName() );

        organisationUnitGroupService.updateOrganisationUnitGroup( match );
    }

    @Override
    protected OrganisationUnitGroup getMatching( OrganisationUnitGroup object )
    {
        List<OrganisationUnitGroup> organisationUnitGroupByName = organisationUnitGroupService.getOrganisationUnitGroupByName( object.getName() );

        return organisationUnitGroupByName.isEmpty() ? null : organisationUnitGroupByName.get( 0 );
    }

    @Override
    protected boolean isIdentical( OrganisationUnitGroup object, OrganisationUnitGroup existing )
    {
        return object.getName().equals( existing.getName() );
    }
}
