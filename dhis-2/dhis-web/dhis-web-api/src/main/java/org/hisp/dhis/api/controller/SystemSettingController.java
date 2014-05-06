package org.hisp.dhis.api.controller;

/*
 * Copyright (c) 2004-2014, University of Oslo
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

import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.setting.SystemSetting;
import org.hisp.dhis.setting.SystemSettingManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping("/systemSettings")
public class SystemSettingController
{
    @Autowired
    private SystemSettingManager systemSettingManager;

    @RequestMapping(value = "/{key}", method = RequestMethod.POST, consumes = { ContextUtils.CONTENT_TYPE_TEXT, ContextUtils.CONTENT_TYPE_HTML })
    @PreAuthorize("hasRole('ALL') or hasRole('F_SYSTEM_SETTING')")
    public void setSystemSetting(
        @PathVariable String key,
        @RequestParam(required = false) String value,
        @RequestBody(required = false) String valuePayload, HttpServletResponse response )
    {
        if ( key == null )
        {
            ContextUtils.conflictResponse( response, "Key must be specified" );
            return;
        }

        if ( value == null && valuePayload == null )
        {
            ContextUtils.conflictResponse( response, "Value must be specified as query param or as payload" );
        }

        value = value != null ? value : valuePayload;

        systemSettingManager.saveSystemSetting( key, value );

        ContextUtils.okResponse( response, "System setting " + key + " set as value '" + value + "'." );
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.GET, produces = ContextUtils.CONTENT_TYPE_TEXT)
    public @ResponseBody String getSystemSetting( @PathVariable("key") String key )
    {
        Serializable setting = systemSettingManager.getSystemSetting( key );

        return setting != null ? String.valueOf( setting ) : null;
    }

    @RequestMapping( method = RequestMethod.GET, produces = ContextUtils.CONTENT_TYPE_JSON )
    public @ResponseBody Map<String, Serializable> getSystemSetting()
    {
        Map<String, Serializable> settingsMap = new HashMap<String, Serializable>();

        Collection<SystemSetting> systemSettings = systemSettingManager.getAllSystemSettings();
        for ( SystemSetting systemSetting : systemSettings )
        {
            settingsMap.put( systemSetting.getName(), systemSetting.getValue() );
        }

        return settingsMap;
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ALL') or hasRole('F_SYSTEM_SETTING')")
    public void removeSystemSetting( @PathVariable("key") String key )
    {
        systemSettingManager.deleteSystemSetting( key );
    }
}
