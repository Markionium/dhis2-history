package org.hisp.dhis.i18n;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import static org.hisp.dhis.system.util.ReflectionUtils.getClassName;
import static org.hisp.dhis.system.util.ReflectionUtils.getId;
import static org.hisp.dhis.system.util.ReflectionUtils.getProperty;
import static org.hisp.dhis.system.util.ReflectionUtils.isCollection;
import static org.hisp.dhis.system.util.ReflectionUtils.setProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.common.GenericNameableObjectStore;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.i18n.locale.I18nLocale;
import org.hisp.dhis.translation.Translation;
import org.hisp.dhis.translation.TranslationService;
import org.hisp.dhis.user.UserSettingService;

/**
 * @author Oyvind Brucker
 */
public class DefaultI18nService
    implements I18nService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private TranslationService translationService;

    public void setTranslationService( TranslationService translationService )
    {
        this.translationService = translationService;
    }

    private UserSettingService userSettingService;

    public void setUserSettingService( UserSettingService userSettingService )
    {
        this.userSettingService = userSettingService;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    /*
    private List<I18nLocale> locales = new ArrayList<I18nLocale>();

    public void setLocales( List<String> localeStrings )
    {
        for ( String string : localeStrings )
        {            
            // TODO: Need to find out the source of this call and find the format used.
            I18nLocale locale = getI18nLocaleByName( string); //LocaleUtils.getLocale( string );

            if ( locale != null )
            {
                locales.add( locale );
            }
        }

        Collections.sort( locales, new Comparator<I18nLocale>()
        {
            public int compare( I18nLocale l1, I18nLocale l2 )
            {
                return l1.getDisplayName().compareTo( l2.getDisplayName() );
            }
        } );
    }
    */
    
    // -------------------------------------------------------------------------
    // I18nLocale Properties
    // -------------------------------------------------------------------------
    
    public I18nLocale getCurrentLocale()
    {
        I18nLocale i18nLocale = null;

        try
        {
            String i18nLocaleId = (String) userSettingService.getUserSetting( UserSettingService.KEY_DB_LOCALE );

            if ( i18nLocaleId != "None" )
            {
                i18nLocale = getI18nLocale( Integer.valueOf( i18nLocaleId ) );
            }
        }
        catch ( Exception ex )
        {
        }

        return i18nLocale;
    }

    public boolean currentLocaleIsBase()
    {
        return getCurrentLocale() == null;
    }

    public List<I18nLocale> getAvailableLocales()
    {
        return i18nLocaleStore.getAll();
    }
    
    private GenericNameableObjectStore<I18nLocale> i18nLocaleStore;

    public void setI18nLocaleStore( GenericNameableObjectStore<I18nLocale> i18nLocaleStore )
    {
        this.i18nLocaleStore = i18nLocaleStore;
    }

    private Map<String, String> languages;

    public void setLanguages( Map<String, String> languages )
    {
        this.languages = languages;
    }
   
    private Map<String, String> countries;

    public void setCountries( Map<String, String> countries )
    {
        this.countries = countries;
    }
    
    // -------------------------------------------------------------------------
    // Internationalise
    // -------------------------------------------------------------------------

    public void internationalise( Object object )
    {
        if ( isCollection( object ) )
        {
            internationaliseCollection( (Collection<?>) object, getCurrentLocale() );
        }
        else
        {
            internationaliseObject( object, getCurrentLocale() );
        }
    }

    public void internationalise( Object object, I18nLocale locale )
    {
        if ( isCollection( object ) )
        {
            internationaliseCollection( (Collection<?>) object, locale );
        }
        else
        {
            internationaliseObject( object, locale );
        }
    }

    private void internationaliseObject( Object object, I18nLocale locale )
    {
        if ( locale == null || object == null )
        {
            return;
        }

        List<String> properties = getObjectPropertyNames( object );
        
        Collection<Translation> translations = translationService.getTranslations( getClassName( object ),
            getId( object ), locale );

        Map<String, String> translationMap = convertTranslations( translations );

        for ( String property : properties )
        {
            String value = translationMap.get( property );
            
            if ( value != null && !value.isEmpty() )
            {
                setProperty( object, "display", property, value );
            }
        }
    }

    private void internationaliseCollection( Collection<?> objects, I18nLocale locale )
    {
        if ( locale == null || objects == null || objects.size() == 0 )
        {
            return;
        }

        Object peek = objects.iterator().next();

        List<String> properties = getObjectPropertyNames( peek );

        Collection<Translation> translations = translationService.getTranslations( getClassName( peek ), locale );

        for ( Object object : objects )
        {
            Map<String, String> translationMap = getTranslationsForObject( translations, getId( object ) );

            for ( String property : properties )
            {
                String value = translationMap.get( property );

                if ( value != null && !value.isEmpty() )
                {
                    setProperty( object, "display", property, value );
                }
            }
        }
    }

    public Map<String, String> getObjectPropertyValues( Object object )
    {
        List<String> properties = getObjectPropertyNames( object );

        Map<String, String> translations = new HashMap<String, String>();

        for ( String property : properties )
        {
            translations.put( property, getProperty( object, property ) );
        }

        return translations;
    }

    public List<String> getObjectPropertyNames( Object object )
    {
        if ( !(object instanceof IdentifiableObject) )
        {
            throw new IllegalArgumentException( "I18n object must be identifiable: " + object );
        }
        
        if ( object instanceof DataElement )
        {
            return Arrays.asList( DataElement.I18N_PROPERTIES );
        }

        return (object instanceof NameableObject) ? Arrays.asList( NameableObject.I18N_PROPERTIES ) : Arrays
            .asList( IdentifiableObject.I18N_PROPERTIES );
    }

    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    public void removeObject( Object object )
    {
        if ( object != null )
        {
            translationService.deleteTranslations( getClassName( object ), getId( object ) );
        }
    }

    // -------------------------------------------------------------------------
    // Translation
    // -------------------------------------------------------------------------

    public void updateTranslation( String className, int id, I18nLocale locale, Map<String, String> translations )
    {
        if ( locale != null && className != null )
        {
            for ( Map.Entry<String, String> translationEntry : translations.entrySet() )
            {
                String key = translationEntry.getKey();
                String value = translationEntry.getValue();
                
                Translation translation = translationService.getTranslationWithoutDefault( className, id, locale, key );

                if ( value != null && !value.trim().isEmpty() )
                {
                    if ( translation != null )
                    {
                        translation.setValue( value );
                        translationService.updateTranslation( translation );
                    }
                    else
                    {
                        translation = new Translation( className, id, locale.getLanguage(), locale.getCountry(), key, value );

                        translationService.addTranslation( translation );
                    }
                }
                else if ( translation != null )
                {
                    translationService.deleteTranslation( translation );
                }
            }
        }
    }

    public Map<String, String> getTranslations( String className, int id )
    {
        return getTranslations( className, id, getCurrentLocale() );
    }
    
    public Map<String, String> getTranslations( String className, I18nLocale locale )
    {
        if ( locale != null && className != null )
        {
            return convertTranslations( translationService.getTranslations( className, locale ) );
        }

        return new HashMap<String, String>();
    }    
        
    public Map<String, String> getTranslations( String className, int id, I18nLocale locale )
    {
        if ( locale != null && className != null )
        {
            return convertTranslations( translationService.getTranslations( className, id, locale ) );
        }

        return new HashMap<String, String>();
    }
    
    public Map<String, String> getTranslationsWithoutDefault( String className, int id )
    {
        return getTranslationsWithoutDefault( className, id, getCurrentLocale() );
    }

    public Map<String, String> getTranslationsWithoutDefault( String className, int id, I18nLocale locale )
    {
        if ( locale != null && className != null )
        {
            return convertTranslations( translationService.getTranslationsWithoutDefault( className, id, locale ) );
        }

        return new HashMap<String, String>();
    }

    // -------------------------------------------------------------------------
    // I18nLocale Implementation methods
    // -------------------------------------------------------------------------

    public int saveI18nLocale( I18nLocale i18nlocale )
    {
        return i18nLocaleStore.save( i18nlocale );
    }

    public void updateI18nLocale( I18nLocale i18nlocale )
    {
        i18nLocaleStore.update( i18nlocale );
    }

    public void deleteI18nLocale( I18nLocale i18nlocale )
    {
        i18nLocaleStore.delete( i18nlocale );
    }

    public List<I18nLocale> getAllI18nLocales()
    {        
        return i18nLocaleStore.getAll();
    }

    public I18nLocale getI18nLocale( int id )
    {
        return i18nLocaleStore.get( id );
    }

    public I18nLocale getI18nLocaleByName( String name )
    {
        return i18nLocaleStore.getByName( name );
    }

    public Map<String, String> getAvailableLanguages()
    {
        return languages;
    }

    public Map<String, String> getAvailableCountries()
    {
        return countries;
    }

    public int getI18nLocaleCount()
    {
        return i18nLocaleStore.getCount();
    }
    
    public Collection<I18nLocale> getI18nLocales(int min, int max)
    {
        return i18nLocaleStore.getAllOrderedLastUpdated( min, max );
    }    

    public int getI18nLocaleCountByName(String key)
    {
        return i18nLocaleStore.getCountLikeName( key );
    }
    
    public Collection<I18nLocale> getI18nLocalesByName(String key, int min, int max)
    {
        return i18nLocaleStore.getAllLikeNameOrderedName( key, min, max );        
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Returns a map representing Translations for an object matching the given
     * id where the key is the translation property and the value is the
     * translation value.
     * 
     * @param translations Collection to search.
     * @param id the object id.
     * @return Map of property/value pairs.
     */
    private Map<String, String> getTranslationsForObject( Collection<Translation> translations, int id )
    {
        Collection<Translation> objectTranslations = new ArrayList<Translation>();

        for ( Translation translation : translations )
        {
            if ( translation.getId() == id )
            {
                objectTranslations.add( translation );
            }
        }

        return convertTranslations( objectTranslations );
    }

    /**
     * Returns a map for a collection of Translations where the key is the
     * translation property and the value is the translation value.
     * 
     * @param translations the Collection of translations.
     * @return Map containing translations.
     */
    private Map<String, String> convertTranslations( Collection<Translation> translations )
    {
        Map<String, String> translationMap = new Hashtable<String, String>();

        for ( Translation translation : translations )
        {
            if ( translation.getProperty() != null && translation.getValue() != null )
            {
                translationMap.put( translation.getProperty(), translation.getValue() );
            }
        }

        return translationMap;
    }
}
