package com.tssoftgroup.tmobile.utils;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;

public final class RestartSample extends Application
{
    public static void main(String[] args)
    {
            RestartSample theApp = new RestartSample();
            theApp.enterEventDispatcher();
    }

    public RestartSample()
    {
       System.out.println("Application started...");

        //Get the current application descriptor.
        ApplicationDescriptor current = ApplicationDescriptor.currentApplicationDescriptor();
        System.out.println("Scheduling the restart in 1 minute...");
        
        //Schedules are rounded to the nearest minute so ensure the application is scheduled for at least 1 minute in the future.
ApplicationManager.getApplicationManager().scheduleApplication(current, System.currentTimeMillis() + 60001, true);

        System.out.println("Application is exiting...");
        System.exit(0);
    }
}