package org.hisp.dhis.jdbc.batchhandler;

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

import javax.sql.DataSource;

import org.amplecode.quick.StatementDialect;
import org.amplecode.quick.batchhandler.AbstractBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

/**
 * @author Lars Helge Overland
 * @version $Id: OrganisationUnitGroupBatchHandler.java 5062 2008-05-01 18:10:35Z larshelg $
 */
public class OrganisationUnitGroupBatchHandler
    extends AbstractBatchHandler<OrganisationUnitGroup>
{
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    public OrganisationUnitGroupBatchHandler( DataSource dataSource, StatementDialect dialect )
    {
        super( dataSource, dialect, false, false );
    }

    // -------------------------------------------------------------------------
    // AbstractBatchHandler implementation
    // -------------------------------------------------------------------------

    protected void setTableName()
    {
        statementBuilder.setTableName( "orgunitgroup" );
    }
    
    @Override
    protected void setAutoIncrementColumn()
    {
        statementBuilder.setAutoIncrementColumn( "orgunitgroupid" );
    }
    
    @Override
    protected void setIdentifierColumns()
    {
        statementBuilder.setIdentifierColumn( "orgunitgroupid" );
    }
    
    @Override
    protected void setIdentifierValues( OrganisationUnitGroup group )
    {        
        statementBuilder.setIdentifierValue( group.getId() );
    }
    
    protected void setUniqueColumns()
    {
        statementBuilder.setUniqueColumn( "name" );
    }
    
    protected void setUniqueValues( OrganisationUnitGroup group )
    {        
        statementBuilder.setUniqueValue( group.getName() );
    }
    
    protected void setColumns()
    {
        statementBuilder.setColumn( "uuid" );
        statementBuilder.setColumn( "name" );
    }
    
    protected void setValues( OrganisationUnitGroup group )
    {        
        statementBuilder.setValue( group.getUuid() );
        statementBuilder.setValue( group.getName() );
    }
}
