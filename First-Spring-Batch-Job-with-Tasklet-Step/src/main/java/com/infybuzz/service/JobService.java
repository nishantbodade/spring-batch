package com.infybuzz.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.infybuzz.request.JobParamsRequest;

@Service
public class JobService {
	
	@Autowired
	JobLauncher jobLauncher;
	
	@Qualifier("firstJob")
	@Autowired
	Job firstJob;
	
	@Qualifier("secondJob")
	@Autowired
	Job secondJob;
	
	@Async
	public void startJob(String jobName, List<JobParamsRequest> request) throws Exception {
		JobExecution execution=null;
		Map<String, JobParameter> params=new HashMap<String, JobParameter>();
		params.put("currentTime", new JobParameter(System.currentTimeMillis()));
		
		request.stream().forEach(r ->{
			params.put(r.getKey(), new JobParameter(r.getValue()));
		});
		
		JobParameters jobParameter=new JobParameters(params);
		
		
		if(jobName.equals("First Job")) {
			execution=jobLauncher.run(firstJob, jobParameter);
		}else {
			execution=jobLauncher.run(secondJob, jobParameter);
		}
		
		System.out.println("job execution id: "+execution.getId());
	}

}
