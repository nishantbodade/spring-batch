package com.infybuzz.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SecondJobScheduler {
	@Autowired
	JobLauncher jobLauncher;
	
	
	@Qualifier("secondJob")
	@Autowired
	Job secondJob;
	
	@Scheduled(cron = "0 0/1 * 1/1 * ?")
	public void startJob() throws Exception{
		
		
		Map<String, JobParameter> params=new HashMap<String, JobParameter>();
		params.put("currentTime", new JobParameter(System.currentTimeMillis()));
		
	
		
		JobParameters jobParameter=new JobParameters(params);
		
		
		
		JobExecution	execution1=jobLauncher.run(secondJob, jobParameter);
		
		
		System.out.println("job execution id: "+execution1.getId());

		
	}

}
