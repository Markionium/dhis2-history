package org.hisp.dhis.dataelement;

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

import java.util.Collection;

import org.hisp.dhis.hierarchy.HierarchyViolationException;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public interface DataElementCategoryService 
{
    String ID = DataElementCategoryService.class.getName();    

    // -------------------------------------------------------------------------
    // Category
    // -------------------------------------------------------------------------

    /**
     * Adds a DataElementCategory.
     * 
     * @param dataElementCategory the DataElementCategory to add.
     * @return a generated unique id of the added Category.
     */
    int addDataElementCategory( DataElementCategory dataElementCategory );

    /**
     * Updates a DataElementCategory.
     * 
     * @param dataElementCategory the DataElementCategory to update.
     */
    void updateDataElementCategory( DataElementCategory dataElementCategory );

    /**
     * Deletes a DataElementCategory. The DataElementCategory is also removed from any
     * DataElementCategoryCombos if it is a member of. It is not possible to delete a
     * DataElementCategory with options.
     * 
     * @param dataElementCategory the DataElementCategory to delete.
     * @throws HierarchyViolationException if the DataElementCategory has children.
     */
    void deleteDataElementCategory( DataElementCategory dataElementCategory );
 
    /**
     * Returns a DataElementCategory.
     * 
     * @param id the id of the DataElementCTEGORY to return.
     * @return the DataElementCategory with the given id, or null if no match.
     */
    DataElementCategory getDataElementCategory( int id );
    
    /**
     * Retrieves the DataElementCategories with the given identifiers.
     * 
     * @param identifiers the identifiers of the DataElementCategories to retrieve.
     * @return a collection of DataElementCategories.
     */
    Collection<DataElementCategory> getDataElementCategories( Collection<Integer> identifiers );
    
    /**
     * Retrieves the DataElementCategory with the given name.
     * 
     * @param name the name of the DataElementCategory to retrieve.
     * @return the DataElementCategory.
     */
    DataElementCategory getDataElementCategoryByName( String name );
    
    /**
     * Returns all DataElementCategories.
     * 
     * @return a collection of all DataElementCategories, or an empty collection if there
     *         are no DataElementCategories.
     */
    Collection<DataElementCategory> getAllDataElementCategories();    

    // -------------------------------------------------------------------------
    // CategoryOption
    // -------------------------------------------------------------------------

    /**
     * Adds a DataElementCategoryOption.
     * 
     * @param dataElemtnCategoryOption the DataElementCategoryOption to add.
     * @return a generated unique id of the added DataElementCategoryOption.
     */
    int addDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption );

    /**
     * Updates a DataElementCategoryOption.
     * 
     * @param dataElementCategoryOption the DataElementCategoryOption to update.
     */
    void updateDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption );
    
    /**
     * Deletes a DataElementCategoryOption.
     * 
     * @param dataElementCategoryOption
     */
    void deleteDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption );

    /**
     * Returns a DataElementCategoryOption.
     * 
     * @param id the id of the DataElementCategoryOption to return.
     * @return the DataElementCategoryOption with the given id, or null if no match.
     */
    DataElementCategoryOption getDataElementCategoryOption( int id );
    
    /**
     * Retrieves the DataElementCategoryOptions with the given identifiers.
     * 
     * @param identifiers the identifiers of the DataElementCategoryOption to retrieve.
     * @return a Collection of DataElementCategoryOptions.
     */
    Collection<DataElementCategoryOption> getDataElementCategoryOptions( Collection<Integer> identifiers );
    
    /**
     * Retrieves the DataElementCategoryOption with the given name.
     * @param name the name.
     * @return the DataElementCategoryOption with the given name.
     */
    DataElementCategoryOption getDataElementCategoryOptionByName( String name );
    
    /**
     * Returns all DataElementCategoryOptions.
     * 
     * @return a collection of all DataElementCategoryOptions, or an empty collection if there
     *         are no DataElementCategoryOptions.
     */
    Collection<DataElementCategoryOption> getAllDataElementCategoryOptions();

    // -------------------------------------------------------------------------
    // CategoryCombo
    // -------------------------------------------------------------------------

    /**
     * Adds a DataElementCategoryCombo.
     * 
     * @param dataElementCategoryCombo the DataElementCategoryCombo to add.
     * @return the generated identifier.
     */
    int addDataElementCategoryCombo( DataElementCategoryCombo dataElementCategoryCombo );

    /**
     * Updates a DataElementCategoryCombo.
     * 
     * @param dataElementCategoryCombo the DataElementCategoryCombo to update.
     */
    void updateDataElementCategoryCombo( DataElementCategoryCombo dataElementCategoryCombo );

    /**
     * Deletes a DataElementCategoryCombo.
     * 
     * @param dataElementCategoryCombo the DataElementCategoryCombo to delete.
     */
    void deleteDataElementCategoryCombo( DataElementCategoryCombo dataElementCategoryCombo );
 
    /**
     * Retrieves a DataElementCategoryCombo with the given identifier.
     * 
     * @param id the identifier of the DataElementCategoryCombo to retrieve.
     * @return the DataElementCategoryCombo.
     */
    DataElementCategoryCombo getDataElementCategoryCombo( int id );
    
    /**
     * Retrieves the DataElementCategoryCombo with the given identifiers.
     * 
     * @param identifiers the identifiers.
     * @return a collection of DataElementCategoryCombos.
     */
    Collection<DataElementCategoryCombo> getDataElementCategoryCombos( Collection<Integer> identifiers );
    
    /**
     * Retrieves the DataElementCategoryCombo with the given name.
     * 
     * @param name the name of the DataElementCategoryCombo to retrieve.
     * @return the DataElementCategoryCombo.
     */
    DataElementCategoryCombo getDataElementCategoryComboByName( String name );
    
    /**
     * Retrieves all DataElementCategoryCombos.
     * 
     * @return a collection of DataElementCategoryCombos.
     */
    Collection<DataElementCategoryCombo> getAllDataElementCategoryCombos();

    // -------------------------------------------------------------------------
    // CategoryOptionCombo
    // -------------------------------------------------------------------------

    /**
     * Adds a DataElementCategoryOptionCombo.
     * 
     * @param dataElementCategoryOptionCombo the DataElementCategoryOptionCombo
     *        to add.
     * @return the generated identifier.
     */
    int addDataElementCategoryOptionCombo( DataElementCategoryOptionCombo dataElementCategoryOptionCombo );

    /**
     * Updates a DataElementCategoryOptionCombo.
     * 
     * @param dataElementCategoryOptionCombo the DataElementCategoryOptionCombo
     *        to update.
     */
    void updateDataElementCategoryOptionCombo( DataElementCategoryOptionCombo dataElementCategoryOptionCombo );

    /**
     * Deletes a DataElementCategoryOptionCombo.
     * 
     * @param dataElementCategoryOptionCombo the DataElementCategoryOptionCombo
     *        to delete.
     */
    void deleteDataElementCategoryOptionCombo( DataElementCategoryOptionCombo dataElementCategoryOptionCombo );

    /**
     * Retrieves a DataElementCategoryOptionCombo with the given identifier.
     * 
     * @param id the identifier of the DataElementCategoryOptionCombo.
     * @return the DataElementCategoryOptionCombo.
     */
    DataElementCategoryOptionCombo getDataElementCategoryOptionCombo( int id );

    /**
     * Retrieves the DataElementCategoryOptionCombos with the given identifiers.
     * 
     * @param identifiers the identifiers of the DataElementCategoryOptionCombos.
     * @return a Collection of DataElementCategoryOptionCombos.
     */
    Collection<DataElementCategoryOptionCombo> getDataElementCategoryOptionCombos( Collection<Integer> identifiers );

    /**
     * Retrieves the DataElementCategoryOptionCombo with the given Collection
     * of DataElementCategoryOptions.
     * 
     * @param categoryOptions
     * @return
     */
    DataElementCategoryOptionCombo getDataElementCategoryOptionCombo( Collection<DataElementCategoryOption> categoryOptions );
    
    /**
     * Retrieves a DataElementCategoryOptionCombo.
     * 
     * @param categoryOptionCombo the DataElementCategoryOptionCombo to
     *        retrieve.
     * @return a DataElementCategoryOptionCombo.
     */
    DataElementCategoryOptionCombo getDataElementCategoryOptionCombo( DataElementCategoryOptionCombo categoryOptionCombo );

    /**
     * Retrieves all DataElementCategoryOptionCombos.
     * 
     * @return a Collection of DataElementCategoryOptionCombos.
     */
    Collection<DataElementCategoryOptionCombo> getAllDataElementCategoryOptionCombos();

    /**
     * Sorts the DataElementCategoryOptionCombos in the given
     * DataElementCategoryCombo.
     * 
     * @param categoryCombo the DataElementCategoryCombo.
     * 
     */
    Collection<DataElementCategoryOptionCombo> sortOptionCombos( DataElementCategoryCombo categoryCombo );

    /**
     * Generates and persists a default DataElementCategory,
     * DataElmentCategoryOption, DataElementCategoryCombo and
     * DataElementCategoryOptionCombo.
     */
    void generateDefaultDimension();

    /**
     * Retrieves the default DataElementCategoryOptionCombo.
     * 
     * @return the DataElementCategoryOptionCombo.
     */
    DataElementCategoryOptionCombo getDefaultDataElementCategoryOptionCombo();

    /**
     * Generates and persists DataElementCategoryOptionCombos for the given
     * DataElementCategoryCombo.
     * 
     * @param categoryCombo the DataElementCategoryCombo.
     */
    void generateOptionCombos( DataElementCategoryCombo categoryCombo );

    /**
     * Populates all transient properties on each Operand in the given collection.
     * 
     * @param operands the collection of Operands.
     * @return a collection of Operands.
     */
    public Collection<DataElementOperand> populateOperands( Collection<DataElementOperand> operands );
    
    /**
     * Gets the Operands for the given Collection of DataElements.
     * 
     * @param dataElements the Collection of DataElements.
     * @return the Operands for the given Collection of DataElements.
     */
    Collection<DataElementOperand> getOperands( Collection<DataElement> dataElements );

    /**
     * Gets the Operands for the given Collection of DataElements.
     * 
     * @param dataElements the Collection of DataElements.
     * @param includeTotals whether to include DataElement totals in the Collection of Operands.
     * @return the Operands for the given Collection of DataElements.
     */
    Collection<DataElementOperand> getOperands( Collection<DataElement> dataElements, boolean includeTotals );
    
    /**
     * Gets the Operands for the given Collection of DataElements. Operands will contain DataElement and CategoryOptionCombo object  
     * 
     * @param dataElements the Collection of DataElements.
     * @return the Operands for the given Collection of DataElements.
     */
    
    Collection<DataElementOperand> getFullOperands( Collection<DataElement> dataElements );
    
    Collection<DataElementCategory> getDataElementCategorysBetween( int first, int max );
    
    Collection<DataElementCategory> getDataElementCategorysBetweenByName( String name, int first, int max );
    
    int getDataElementCategoryCount();
    
    int getDataElementCategoryCountByName( String name );

    Collection<DataElementCategoryCombo> getDataElementCategoryCombosBetween( int first, int max );
    
    Collection<DataElementCategoryCombo> getDataElementCategoryCombosBetweenByName( String name, int first, int max );
    
    int getDataElementCategoryComboCount();
    
    int getDataElementCategoryComboCountByName( String name );
}
