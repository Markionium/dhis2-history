package org.hisp.dhis.i18n;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import java.util.Collection;
import java.util.List;
//import java.util.I18nLocale;
import java.util.Map;

import org.hisp.dhis.i18n.locale.I18nLocale;

/**
 * @author Oyvind Brucker
 * @modifier Dang Duy Hieu
 * @since 2010-04-15
 */
public interface I18nService
{
    String ID = I18nService.class.getName();

    // -------------------------------------------------------------------------
    // Internationalise
    // -------------------------------------------------------------------------

    void internationalise( Object object );
    
    void internationalise( Object object, I18nLocale locale );
    
    Map<String, String> getObjectPropertyValues( Object object );
    
    List<String> getObjectPropertyNames( Object object );
    
    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    void removeObject( Object object );

    // -------------------------------------------------------------------------
    // Translation
    // -------------------------------------------------------------------------

    void updateTranslation( String className, int id, I18nLocale thisLocale, Map<String, String> translations );

    Map<String, String> getTranslations( String className, int id );

    Map<String, String> getTranslations( String className, I18nLocale locale );

    Map<String, String> getTranslations( String className, int id, I18nLocale locale );
    
    Map<String, String> getTranslationsWithoutDefault( String className, int id );

    Map<String, String> getTranslationsWithoutDefault( String className, int id, I18nLocale locale );
    
    // -------------------------------------------------------------------------
    // I18nLocale
    // -------------------------------------------------------------------------

    I18nLocale getCurrentLocale();
    
    boolean currentLocaleIsBase();
    
    List<I18nLocale> getAvailableLocales();

    Map<String, String> getAvailableLanguages();

    Map<String, String> getAvailableCountries();
    

    int saveI18nLocale( I18nLocale locale );

    void deleteI18nLocale( I18nLocale locale );

    void updateI18nLocale( I18nLocale locale );

    I18nLocale getI18nLocale( int id );

    I18nLocale getI18nLocaleByName( String name );

    I18nLocale getI18nLocaleByLocale( String language, String country );

    List<I18nLocale> getAllI18nLocales();
        
    int getI18nLocaleCount();
    
    Collection<I18nLocale> getI18nLocales(int min, int max);
    
    int getI18nLocaleCountByName(String key);

    Collection<I18nLocale> getI18nLocalesByName(String key, int min, int max);
}
