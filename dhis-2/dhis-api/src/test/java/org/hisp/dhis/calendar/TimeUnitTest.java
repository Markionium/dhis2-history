package org.hisp.dhis.calendar;

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

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.GregorianCalendar;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class TimeUnitTest
{
    @Test
    public void testFromDateTime()
    {
        DateTime dateTime = new DateTime( 2014, 1, 1, 12, 13, 14 );
        TimeUnit timeUnit = TimeUnit.fromDateTime( dateTime );

        Assert.assertEquals( 12, timeUnit.getHour() );
        Assert.assertEquals( 13, timeUnit.getMinutes() );
        Assert.assertEquals( 14, timeUnit.getSeconds() );
    }

    @Test
    public void testFromJdkCalendar()
    {
        java.util.Calendar calendar = new GregorianCalendar( 2014, 0, 1, 12, 13, 14 );
        TimeUnit timeUnit = TimeUnit.fromJdkCalendar( calendar );

        Assert.assertEquals( 12, timeUnit.getHour() );
        Assert.assertEquals( 13, timeUnit.getMinutes() );
        Assert.assertEquals( 14, timeUnit.getSeconds() );
    }

    @Test
    public void testFromJdkDate()
    {
        java.util.Calendar calendar = new GregorianCalendar( 2014, 0, 1, 12, 13, 14 );
        TimeUnit timeUnit = TimeUnit.fromJdkDate( calendar.getTime() );

        Assert.assertEquals( 12, timeUnit.getHour() );
        Assert.assertEquals( 13, timeUnit.getMinutes() );
        Assert.assertEquals( 14, timeUnit.getSeconds() );
    }
}
