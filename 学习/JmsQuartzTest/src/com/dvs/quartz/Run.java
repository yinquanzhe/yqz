/**
 * 
 */
package com.dvs.quartz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;


public class Run {

	/**
	 * @param args
	 */
	public static void main(String[] args)  throws IOException, SchedulerException {
		ApplicationContext context = new FileSystemXmlApplicationContext("src/*.xml");
		
		System.out.println("context success!");
		
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        while(true) {
            if(reader.readLine().equals("exit")) {
                break;
            }
        }
        
        Scheduler scheduler = (Scheduler) context.getBean("schedulerFactoryBean");
        scheduler.shutdown();
	}
}





