package org.hisp.dhis.options;

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

import java.io.Serializable;
import java.util.Collection;
import java.util.SortedMap;

/**
 * @author Stian Strandli
 * @version $Id: SystemSettingManager.java 4910 2008-04-15 17:55:02Z larshelg $
 */
public interface SystemSettingManager
{
    final String ID = SystemSettingManager.class.getName();
    
    final String KEY_ZERO_VALUE_SAVE_MODE = "zeroValueSaveMode";
    final String KEY_APPLICATION_TITLE = "applicationTitle";
    final String KEY_FLAG = "flag";
    final String KEY_START_MODULE = "startModule";
    final String KEY_FORUM_INTEGRATION = "forumIntegration";
    final String KEY_OMIT_INDICATORS_ZERO_NUMERATOR_DATAMART = "omitIndicatorsZeroNumeratorDataMart";
    final String KEY_REPORT_TEMPLATE_DIRECTORY = "reportTemplateDirectory";
    
    final String KEY_CHR_IMAGE_DIRECTORY = "chrImageDirectory";
    final String KEY_CHR_NUMBER_OF_RECORDS = "chrNumberOfRecords";

    void saveSystemSetting( String name, Serializable value );   

    Serializable getSystemSetting( String name );   
    
    Serializable getSystemSetting( String name, Serializable defaultValue );

    Collection<SystemSetting> getAllSystemSettings();

    void deleteSystemSetting( String name );
    
    SortedMap<String, String> getFlags();
}
