package org.hisp.dhis.jdbc.batchhandler;

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

import org.amplecode.quick.JdbcConfiguration;
import org.amplecode.quick.batchhandler.AbstractBatchHandler;
import org.hisp.dhis.importexport.GroupMemberAssociation;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class CategoryCategoryOptionAssociationBatchHandler
    extends AbstractBatchHandler<GroupMemberAssociation>
{
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    public CategoryCategoryOptionAssociationBatchHandler( JdbcConfiguration config )
    {
        super( config, true, true );
    }

    // -------------------------------------------------------------------------
    // AbstractBatchHandler implementation
    // -------------------------------------------------------------------------

    @Override
    protected void setTableName()
    {
        statementBuilder.setTableName( "categories_categoryoptions" );
    }
    
    @Override
    protected void setUniqueColumns()
    {
        statementBuilder.setUniqueColumn( "categoryid" );
        statementBuilder.setUniqueColumn( "sort_order" );
    }
    
    @Override
    protected void setUniqueValues( GroupMemberAssociation association )
    {
        statementBuilder.setUniqueValue( association.getGroupId() );
        statementBuilder.setUniqueValue( association.getSortOrder() );
    }
    
    @Override
    protected void setColumns()
    {
        statementBuilder.setColumn( "categoryid" );
        statementBuilder.setColumn( "categoryoptionid" );
        statementBuilder.setColumn( "sort_order" );
    }
    
    @Override
    protected void setValues( GroupMemberAssociation association )
    {        
        statementBuilder.setValue( association.getGroupId() );
        statementBuilder.setValue( association.getMemberId() );
        statementBuilder.setValue( association.getSortOrder() );
    }
}
