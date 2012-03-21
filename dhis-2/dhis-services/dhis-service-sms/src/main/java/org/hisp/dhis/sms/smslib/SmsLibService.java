package org.hisp.dhis.sms.smslib;

/*
 * Copyright (c) 2004-2011, University of Oslo
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.sms.SmsServiceException;
import org.hisp.dhis.sms.config.BulkSmsGatewayConfig;
import org.hisp.dhis.sms.config.ClickatellGatewayConfig;
import org.hisp.dhis.sms.config.GenericHttpGatewayConfig;
import org.hisp.dhis.sms.config.SmsConfiguration;
import org.hisp.dhis.sms.config.SmsGatewayConfig;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.sms.outbound.OutboundSmsTransportService;
import org.smslib.AGateway;
import org.smslib.GatewayException;
import org.smslib.IOutboundMessageNotification;
import org.smslib.OutboundMessage;
import org.smslib.SMSLibException;
import org.smslib.Service;
import org.smslib.Service.ServiceStatus;

public class SmsLibService
    implements OutboundSmsTransportService
{
    private static final Log log = LogFactory.getLog( SmsLibService.class );

    private Map<String, String> gatewayMap = new HashMap<String, String>();

    private GateWayFactory gatewayFactory = new GateWayFactory();

    private SmsConfiguration config;

    private final String BULK_GATEWAY = "bulk_gw";

    private final String CLICKATELL_GATEWAY = "clickatell_gw";

    private final String HTTP_GATEWAY = "http_gw";

    private final String MODEM_GATEWAY = "modem_gw";

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    public boolean isEnabled()
    {
        return config != null && config.isEnabled();
    }

    @Override
    public Map<String, String> getGatewayMap()
    {
        if ( gatewayMap == null || gatewayMap.isEmpty() )
        {
            reloadConfig();
        }

        return gatewayMap;
    }

    @Override
    public void sendMessage( OutboundSms sms, String gatewayId )
        throws SmsServiceException
    {
        String recipient;

        Set<String> recipients = sms.getRecipients();

        if ( recipients.size() == 0 )
        {
            log.warn( "Trying to send sms without recipients: " + sms );
            return;
        }
        else if ( recipients.size() == 1 )
        {
            recipient = recipients.iterator().next();
        }
        else
        {
            recipient = createTmpGroup( recipients );
        }

        OutboundMessage message = new OutboundMessage( recipient, sms.getMessage() );

        String longNumber = config.getLongNumber();

        if ( longNumber != null && !longNumber.isEmpty() )
        {
            message.setFrom( longNumber );
        }

        boolean sent = false;

        try
        {
            log.debug( "Sending message " + sms );

            if ( gatewayId == null || gatewayId.isEmpty() )
            {
                sent = getService().sendMessage( message );
            }
            else
            {
                sent = getService().sendMessage( message, gatewayId );
            }
        }
        catch ( SMSLibException e )
        {
            log.warn( "Unable to send message: " + sms, e );
            throw new SmsServiceException( "Unable to send message: " + sms, e );
        }
        catch ( IOException e )
        {
            log.warn( "Unable to send message: " + sms, e );
            throw new SmsServiceException( "Unable to send message: " + sms, e );
        }
        catch ( InterruptedException e )
        {
            log.warn( "Unable to send message: " + sms, e );
            throw new SmsServiceException( "Unable to send message: " + sms, e );
        }
        finally
        {
            if ( recipients.size() > 1 )
            {
                // Make sure we delete tmp. group
                removeGroup( recipient );
            }
        }

        if ( !sent )
        {
            log.warn( "Message not sent" );
        }
    }

    @Override
    public void initialize( SmsConfiguration smsConfiguration )
        throws SmsServiceException
    {
        // FIXME: Implement a decent equals..
        // if (smsConfiguration.equals( config )) {
        // // nothing to do
        // return;
        // }

        log.debug( "Initializing SmsLib" );

        this.config = smsConfiguration;

        ServiceStatus status = getService().getServiceStatus();

        if ( status == ServiceStatus.STARTED || status == ServiceStatus.STARTING )
        {
            log.debug( "Stopping SmsLib" );
            stopService();
        }

        log.debug( "Loading configuration" );

        if ( config.isEnabled() && reloadConfig() )
        {
            log.debug( "Starting SmsLib" );
            startService();
        }
        else
        {
            log.debug( "Sms not enabled or there is no any gateway, won't start service" );
        }
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String createTmpGroup( Set<String> recipients )
    {
        String groupName = Thread.currentThread().getName();

        getService().createGroup( groupName );

        for ( String recepient : recipients )
        {
            getService().addToGroup( groupName, recepient );
        }

        return groupName;
    }

    private void removeGroup( String groupName )
    {
        getService().removeGroup( groupName );
    }

    private void startService()
    {
        try
        {
            getService().startService();
        }
        catch ( SMSLibException e )
        {
            log.warn( "Unable to start smsLib service", e );
            throw new SmsServiceException( "Unable to start smsLib service", e );
        }
        catch ( IOException e )
        {
            log.warn( "Unable to start smsLib service", e );
            throw new SmsServiceException( "Unable to start smsLib service", e );
        }
        catch ( InterruptedException e )
        {
            log.warn( "Unable to start smsLib service", e );
            throw new SmsServiceException( "Unable to start smsLib service", e );
        }
    }

    @Override
    public String stopService()
    {
        String status = "success";

        try
        {
            getService().stopService();
        }
        catch ( SMSLibException e )
        {
            status = "Unable to stop smsLib service" + e.getCause().getMessage();

            log.warn( "Unable to stop smsLib service", e );
            throw new SmsServiceException( "Unable to stop smsLib service", e );
        }
        catch ( IOException e )
        {
            status = "Unable to stop smsLib service" + e.getCause().getMessage();

            log.warn( "Unable to stop smsLib service", e );
            throw new SmsServiceException( "Unable to stop smsLib service", e );
        }
        catch ( InterruptedException e )
        {
            status = "Unable to stop smsLib service" + e.getCause().getMessage();

            log.warn( "Unable to stop smsLib service", e );
            throw new SmsServiceException( "Unable to stop smsLib service", e );
        }

        return status;
    }

    private Service getService()
    {
        return Service.getInstance();
    }

    private boolean reloadConfig()
        throws SmsServiceException
    {
        Service service = Service.getInstance();

        service.setOutboundMessageNotification( new OutboundNotification() );

        service.getGateways().clear();

        AGateway gateway = null;

        boolean reloaded = false;

        // Add gateways
        for ( SmsGatewayConfig gatewayConfig : config.getGateways() )
        {
            try
            {
                gateway = gatewayFactory.create( gatewayConfig );

                service.addGateway( gateway );

                if ( gatewayConfig instanceof BulkSmsGatewayConfig )
                {
                    gatewayMap.put( BULK_GATEWAY, gateway.getGatewayId() );
                }
                else if ( gatewayConfig instanceof ClickatellGatewayConfig )
                {
                    gatewayMap.put( CLICKATELL_GATEWAY, gateway.getGatewayId() );
                }
                else if ( gatewayConfig instanceof GenericHttpGatewayConfig )
                {
                    gatewayMap.put( HTTP_GATEWAY, gateway.getGatewayId() );
                }
                else
                {
                    gatewayMap.put( MODEM_GATEWAY, gateway.getGatewayId() );
                }

                reloaded = true;

                log.debug( "Added gateway " + gatewayConfig.getName() );
            }
            catch ( GatewayException e )
            {
                reloaded = false;

                log.warn( "Unable to load gateway " + gatewayConfig.getName(), e );
                throw new SmsServiceException( "Unable to load gateway" + gatewayConfig.getName(), e );
            }
        }

        return reloaded;
    }

    private class OutboundNotification
        implements IOutboundMessageNotification
    {
        @Override
        public void process( AGateway gateway, OutboundMessage msg )
        {
            log.debug( "Sent message through gateway " + gateway.getGatewayId() + ": " + msg );
        }
    }
}
