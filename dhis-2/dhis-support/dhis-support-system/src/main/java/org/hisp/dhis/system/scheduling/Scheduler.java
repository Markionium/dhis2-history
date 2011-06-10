package org.hisp.dhis.system.scheduling;

public interface Scheduler
{
    final String CRON_NIGHTLY_0AM = "0 0 0 * * ?";
    final String CRON_NIGHTLY_1AM = "0 0 1 * * ?";
    final String CRON_TEST = "0 * * * * ?";
    
    final String STATUS_RUNNING = "running";
    final String STATUS_DONE = "done";
    final String STATUS_STOPPED  = "stopped";
    final String STATUS_NOT_STARTED = "not_started";
    
    void executeTask( Runnable task );
    
    boolean scheduleTask( Runnable task, String cronExpr );
    
    boolean stopTask( Class<? extends Runnable> taskClass );
    
    String getTaskStatus( Class<? extends Runnable> taskClass );
}
