package org.hisp.dhis.validation.scheduling;

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

import static org.hisp.dhis.setting.SystemSettingManager.KEY_LAST_SUCCESSFUL_MONITORING;
import static org.hisp.dhis.system.notification.NotificationLevel.ERROR;
import static org.hisp.dhis.system.notification.NotificationLevel.INFO;

import java.util.Date;

import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.scheduling.TaskId;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.notification.Notifier;
import org.hisp.dhis.commons.util.DebugUtils;
import org.hisp.dhis.validation.ValidationRuleService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 * @author Jim Grace
 */
public class MonitoringTask
    implements Runnable
{
    @Autowired
    private ValidationRuleService validationRuleService;

    @Autowired
    private Notifier notifier;

    @Autowired
    private MessageService messageService;
    
    @Autowired
    private SystemSettingManager systemSettingManager;

    private TaskId taskId;

    public void setTaskId( TaskId taskId )
    {
        this.taskId = taskId;
    }

    // -------------------------------------------------------------------------
    // Runnable implementation
    // -------------------------------------------------------------------------

    @Override
    public void run()
    {
        final Date startTime = new Date();
        
        notifier.clear( taskId ).notify( taskId, "Monitoring data" );
        
        try
        {
            validationRuleService.scheduledRun();
            
            notifier.notify( taskId, INFO, "Monitoring process done", true );
        }
        catch ( RuntimeException ex )
        {
            notifier.notify( taskId, ERROR, "Process failed: " + ex.getMessage(), true );
            
            messageService.sendSystemNotification( 
                "Monitoring process failed",
                "Monitoring process failed, please check the logs. Time: " + new DateTime().toString() + ". " +
                "Message: " + ex.getMessage() + " " +
                "Cause: " + DebugUtils.getStackTrace( ex.getCause() ) );
            
            throw ex;
        }
        
        systemSettingManager.saveSystemSetting( KEY_LAST_SUCCESSFUL_MONITORING, startTime );
    }
}
