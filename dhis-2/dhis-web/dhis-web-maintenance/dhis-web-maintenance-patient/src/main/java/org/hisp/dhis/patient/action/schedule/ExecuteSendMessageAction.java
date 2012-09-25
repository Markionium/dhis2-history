/*
 * Copyright (c) 2004-2009, University of Oslo
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

package org.hisp.dhis.patient.action.schedule;

import static org.hisp.dhis.sms.outbound.OutboundSms.DHIS_SYSTEM_SENDER;

import java.util.Collection;

import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.SchedulingProgramObject;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.sms.SmsServiceException;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.sms.outbound.OutboundSmsService;
import org.springframework.jdbc.core.JdbcTemplate;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version ExecuteSendMessageAction.java 9:14:46 AM Sep 17, 2012 $
 */
public class ExecuteSendMessageAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private OutboundSmsService outboundSmsService;

    public void setOutboundSmsService( OutboundSmsService outboundSmsService )
    {
        this.outboundSmsService = outboundSmsService;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
    {

        Collection<SchedulingProgramObject> schedulingProgramObjects = programStageInstanceService
            .getSendMesssageEvents();

        for ( SchedulingProgramObject schedulingProgramObject : schedulingProgramObjects )
        {
            String message = schedulingProgramObject.getMessage();

            String phoneNumber = schedulingProgramObject.getPhoneNumber();

            if ( phoneNumber != null && !phoneNumber.isEmpty() )
            {
                try
                {
                    OutboundSms outboundSms = new OutboundSms( message, phoneNumber );
                    outboundSms.setSender( DHIS_SYSTEM_SENDER );
                    outboundSmsService.sendMessage( outboundSms, null );

                    String sql = "INSERT INTO programstageinstance_outboundsms"
                        + "( programstageinstanceid, outboundsmsid, sort_order) VALUES " + "("
                        + schedulingProgramObject.getProgramStageInstanceId() + ", " + outboundSms.getId() + ","
                        + (System.currentTimeMillis() / 1000) + ") ";

                    jdbcTemplate.execute( sql );
                }
                catch ( SmsServiceException e )
                {
                    message = e.getMessage();
                }
            }
        }

        return SUCCESS;
    }

}
