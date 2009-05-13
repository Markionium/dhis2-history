package org.hisp.dhis.resourcetable.hibernate;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hisp.dhis.hibernate.HibernateSessionManager;
import org.hisp.dhis.jdbc.Statement;
import org.hisp.dhis.jdbc.StatementHolder;
import org.hisp.dhis.jdbc.StatementManager;
import org.hisp.dhis.resourcetable.DataElementCategoryOptionComboName;
import org.hisp.dhis.resourcetable.GroupSetStructure;
import org.hisp.dhis.resourcetable.OrganisationUnitStructure;
import org.hisp.dhis.resourcetable.ResourceTableStore;
import org.hisp.dhis.resourcetable.statement.CreateExclusiveGroupSetTableStatement;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class HibernateResourceTableStore
    implements ResourceTableStore
{
    private static final Log log = LogFactory.getLog( HibernateResourceTableStore.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private HibernateSessionManager sessionManager;

    public void setSessionManager( HibernateSessionManager sessionManager )
    {
        this.sessionManager = sessionManager;
    }
    
    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitStructure
    // -------------------------------------------------------------------------

    public int addOrganisationUnitStructure( OrganisationUnitStructure structure )
    {
        Session session = sessionManager.getCurrentSession();

        return (Integer) session.save( structure );
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<OrganisationUnitStructure> getOrganisationUnitStructures()
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( OrganisationUnitStructure.class );

        return criteria.list();
    }

    public int deleteOrganisationUnitStructures()
    {
        Session session = sessionManager.getCurrentSession();

        Query query = session.createQuery( "DELETE FROM OrganisationUnitStructure" );

        return query.executeUpdate();
    }

    // -------------------------------------------------------------------------
    // GroupSetStructure
    // -------------------------------------------------------------------------

    public int addGroupSetStructure( GroupSetStructure structure )
    {
        Session session = sessionManager.getCurrentSession();

        return (Integer) session.save( structure );
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<GroupSetStructure> getGroupSetStructures()
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( GroupSetStructure.class );

        return criteria.list();
    }

    public int deleteGroupSetStructures()
    {
        Session session = sessionManager.getCurrentSession();

        Query query = session.createQuery( "DELETE FROM GroupSetStructure" );

        return query.executeUpdate();
    }

    // -------------------------------------------------------------------------
    // DataElementCategoryOptionComboName
    // -------------------------------------------------------------------------

    public int addDataElementCategoryOptionComboName( DataElementCategoryOptionComboName name )
    {
        Session session = sessionManager.getCurrentSession();

        return (Integer) session.save( name );
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<DataElementCategoryOptionComboName> getDataElementCategoryOptionComboNames()
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( DataElementCategoryOptionComboName.class );

        return criteria.list();
    }
    
    public int deleteDataElementCategoryOptionComboNames()
    {
        Session session = sessionManager.getCurrentSession();

        Query query = session.createQuery( "DELETE FROM DataElementCategoryOptionComboName" );

        return query.executeUpdate();
    }

    // -------------------------------------------------------------------------
    // ExclusiveGroupSetStructure
    // -------------------------------------------------------------------------

    public void createExclusiveGroupSetStructureTable( Statement statement )
    {
        StatementHolder holder = statementManager.getHolder();
        
        try
        {
            holder.getStatement().executeUpdate( statement.getStatement() );
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Failed to create table: " + statement.getStatement() );
        }
        finally
        {
            holder.close();
        }        
    }
    
    public void removeExclusiveGroupSetStructureTable()
    {
        StatementHolder holder = statementManager.getHolder();
        
        try
        {
            holder.getStatement().executeUpdate( "DROP TABLE " + CreateExclusiveGroupSetTableStatement.TABLE_NAME );
        }
        catch ( Exception ex )
        {
            log.info( "Table " + CreateExclusiveGroupSetTableStatement.TABLE_NAME + " does not exist" );
        }
        finally
        {
            holder.close();
        }        
    }    
}
