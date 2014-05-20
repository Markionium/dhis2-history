package org.hisp.dhis.period;

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

import com.google.common.collect.Lists;
import org.hisp.dhis.calendar.DateUnit;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * PeriodType for quarterly Periods. A valid quarterly Period has startDate set
 * to the first day of a calendar quarter, and endDate set to the last day of
 * the same quarter.
 *
 * @author Torgeir Lorange Ostby
 * @version $Id: QuarterlyPeriodType.java 2971 2007-03-03 18:54:56Z torgeilo $
 */
public class QuarterlyPeriodType
    extends CalendarPeriodType
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -5973809094923012052L;

    private static final String ISO_FORMAT = "yyyyQn";

    /**
     * The name of the QuarterlyPeriodType, which is "Quarterly".
     */
    public static final String NAME = "Quarterly";

    public static final int FREQUENCY_ORDER = 91;

    // -------------------------------------------------------------------------
    // PeriodType functionality
    // -------------------------------------------------------------------------

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public Period createPeriod()
    {
        return createPeriod( createCalendarInstance() );
    }

    @Override
    public Period createPeriod( Date date )
    {
        return createPeriod( createCalendarInstance( date ) );
    }

    @Override
    public Period createPeriod( Calendar cal )
    {
        return createPeriod( getCalendar().fromIso( DateUnit.fromJdkCalendar( cal ) ) );
    }

    @Override
    public Period createPeriod( DateUnit dateUnit )
    {
        DateUnit start = new DateUnit( dateUnit );

        start.setMonth( (dateUnit.getMonth() - (dateUnit.getMonth() % 3)) + 1 );
        start.setDay( 1 );

        DateUnit end = new DateUnit( start );
        end = getCalendar().plusMonths( end, 2 );
        end.setDay( getCalendar().daysInMonth( end.getYear(), end.getMonth() ) );

        return new Period( this, getCalendar().toIso( start ).toJdkDate(), getCalendar().toIso( end ).toJdkDate() );
    }

    @Override
    public int getFrequencyOrder()
    {
        return FREQUENCY_ORDER;
    }

    // -------------------------------------------------------------------------
    // CalendarPeriodType functionality
    // -------------------------------------------------------------------------

    @Override
    public Period getNextPeriod( Period period )
    {
        DateUnit dateUnit = createDateUnitInstance( period.getStartDate() );
        dateUnit = getCalendar().plusMonths( dateUnit, 3 );

        return createPeriod( dateUnit );
    }

    @Override
    public Period getPreviousPeriod( Period period )
    {
        DateUnit dateUnit = createDateUnitInstance( period.getStartDate() );
        dateUnit = getCalendar().minusMonths( dateUnit, 3 );

        return createPeriod( dateUnit );
    }

    /**
     * Generates quarterly Periods for the whole year in which the given
     * Period's startDate exists.
     */
    @Override
    public List<Period> generatePeriods( Date date )
    {
        DateUnit dateUnit = createDateUnitInstance( date );
        dateUnit.setMonth( 1 );
        dateUnit.setDay( 1 );

        int year = dateUnit.getYear();
        List<Period> periods = Lists.newArrayList();

        while ( year == dateUnit.getYear() )
        {
            periods.add( createPeriod( dateUnit ) );
            dateUnit = getCalendar().plusMonths( dateUnit, 3 );
        }

        return periods;
    }

    /**
     * Generates the last 4 quarters where the last one is the quarter which the
     * given date is inside.
     */
    @Override
    public List<Period> generateRollingPeriods( Date date )
    {
        // get current quarter start date
        date = createPeriod( date ).getStartDate();

        DateUnit dateUnit = createDateUnitInstance( date );
        dateUnit.setDay( 1 );

        dateUnit = getCalendar().minusMonths( dateUnit, 9 );

        List<Period> periods = Lists.newArrayList();

        for ( int i = 0; i < 4; i++ )
        {
            periods.add( createPeriod( dateUnit ) );
            dateUnit = getCalendar().plusMonths( dateUnit, 3 );
        }

        return periods;
    }

    @Override
    public String getIsoDate( Period period )
    {
        Calendar cal = createCalendarInstance( period.getStartDate() );
        int year = cal.get( Calendar.YEAR );
        int month = cal.get( Calendar.MONTH );

        return year + Quarter.getByMonth( month ).name();
    }

    /**
     * n refers to the quarter, can be [1-4].
     */
    @Override
    public String getIsoFormat()
    {
        return ISO_FORMAT;
    }

    public enum Quarter
    {
        Q1( Calendar.JANUARY ), Q2( Calendar.APRIL ), Q3( Calendar.JULY ), Q4( Calendar.OCTOBER );

        private final int month;

        Quarter( int month )
        {
            this.month = month;
        }

        public int getMonth()
        {
            return month;
        }

        public static Quarter getByMonth( int month )
        {
            switch ( month )
            {
                case Calendar.JANUARY:
                    return Q1;
                case Calendar.APRIL:
                    return Q2;
                case Calendar.JULY:
                    return Q3;
                case Calendar.OCTOBER:
                    return Q4;
                default:
                    throw new IllegalArgumentException( "Not a valid quarterly starting month" );
            }
        }
    }

    @Override
    public Date getRewindedDate( Date date, Integer rewindedPeriods )
    {
        date = date != null ? date : new Date();
        rewindedPeriods = rewindedPeriods != null ? rewindedPeriods : 1;

        DateUnit dateUnit = createDateUnitInstance( date );
        dateUnit = getCalendar().minusMonths( dateUnit, rewindedPeriods * 3 );

        return getCalendar().toIso( dateUnit ).toJdkDate();
    }
}
