package org.hisp.dhis.interceptor;

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

import java.util.Map;

import ognl.NoSuchPropertyException;
import ognl.Ognl;

import org.hisp.dhis.options.displayproperty.DefaultDisplayPropertyHandler;
import org.hisp.dhis.options.displayproperty.DisplayPropertyManager;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * @author Lars Helge Overland
 * @version $Id: WebWorkDisplayPropertyInterceptor.java 6335 2008-11-20 11:11:26Z larshelg $
 */
public class DisplayPropertyInterceptor
    implements Interceptor
{
    private static final String KEY_DISPLAY_PROPERTY_HANDLER = "displayPropertyHandler";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DisplayPropertyManager displayPropertyManager;

    public void setDisplayPropertyManager( DisplayPropertyManager displayPropertyManager )
    {
        this.displayPropertyManager = displayPropertyManager;
    }

    // -------------------------------------------------------------------------
    // Interface implementation
    // -------------------------------------------------------------------------

    public void destroy()
    {
    }

    public void init()
    {
    }

    public String intercept( ActionInvocation actionInvocation )
        throws Exception
    {
        DefaultDisplayPropertyHandler handler = displayPropertyManager.getDisplayPropertyHandler();

        Action action = (Action) actionInvocation.getAction();
        Map<?, ?> contextMap = actionInvocation.getInvocationContext().getContextMap();

        try
        {
            Ognl.setValue( KEY_DISPLAY_PROPERTY_HANDLER, contextMap, action, handler );
        }
        catch ( NoSuchPropertyException e )
        {
        }

        return actionInvocation.invoke();
    }
}
