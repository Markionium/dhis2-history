/*
 * Copyright (c) 2004-2013, University of Oslo
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

package org.hisp.dhis.dataelement;

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.Collection;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.i18n.I18nService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chau Thu Tran
 * 
 * @version $ DefaultCategoryOptionGroupSetService.java Feb 12, 2014 11:38:43 PM
 *          $
 */
@Transactional
public class DefaultCategoryOptionGroupSetService
    implements CategoryOptionGroupSetService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericIdentifiableObjectStore<CategoryOptionGroupSet> categoryOptionGroupSetStore;

    public void setCategoryOptionGroupSetStore(
        GenericIdentifiableObjectStore<CategoryOptionGroupSet> categoryOptionGroupSetStore )
    {
        this.categoryOptionGroupSetStore = categoryOptionGroupSetStore;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // CategoryOptionGroupSet
    // -------------------------------------------------------------------------

    @Override
    public int addCategoryOptionGroupSet( CategoryOptionGroupSet categoryOptionGroupSet )
    {
        return categoryOptionGroupSetStore.save( categoryOptionGroupSet );
    }

    @Override
    public void deleteCategoryOptionGroupSet( CategoryOptionGroupSet categoryOptionGroupSet )
    {
        categoryOptionGroupSetStore.delete( categoryOptionGroupSet );
    }

    @Override
    public void updateCategoryOptionGroupSet( CategoryOptionGroupSet categoryOptionGroupSet )
    {
        categoryOptionGroupSetStore.update( categoryOptionGroupSet );
    }

    @Override
    public CategoryOptionGroupSet getCategoryOptionGroupSet( int id )
    {
        return i18n( i18nService, categoryOptionGroupSetStore.get( id ) );
    }

    @Override
    public CategoryOptionGroupSet getCategoryOptionGroupSetByUid( String uid )
    {
        return i18n( i18nService, categoryOptionGroupSetStore.getByUid( uid ) );
    }

    @Override
    public CategoryOptionGroupSet getCategoryOptionGroupSetByName( String name )
    {
        return i18n( i18nService, categoryOptionGroupSetStore.getByName( name ) );
    }

    @Override
    public Collection<CategoryOptionGroupSet> getAllCategoryOptionGroupSets()
    {
        return i18n( i18nService, categoryOptionGroupSetStore.getAll() );
    }

    @Override
    public Collection<CategoryOptionGroupSet> getCategoryOptionGroupSetsBetweenByName( String name, int first, int max )
    {
        return i18n( i18nService, categoryOptionGroupSetStore.getAllLikeNameOrderedName( name, first, max ) );
    }

    @Override
    public int getCategoryOptionGroupSetCount()
    {
        return categoryOptionGroupSetStore.getCount();
    }

    @Override
    public Collection<CategoryOptionGroupSet> getCategoryOptionGroupSetsBetween( int first, int max )
    {
        return i18n( i18nService, categoryOptionGroupSetStore.getAllOrderedName( first, max ) );
    }

    @Override
    public int getCategoryOptionGroupSetCountByName( String name )
    {
        return categoryOptionGroupSetStore.getCountLikeName( name );
    }

}
