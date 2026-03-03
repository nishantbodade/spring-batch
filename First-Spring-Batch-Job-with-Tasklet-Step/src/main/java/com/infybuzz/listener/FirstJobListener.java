package com.infybuzz.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class FirstJobListener implements JobExecutionListener {

	@Override
	public void beforeJob(JobExecution jobExecution) {

		System.out.println("Before job "+jobExecution.getJobInstance().getJobName());
		System.out.println("job param"+jobExecution.getJobParameters());
		System.out.println("job context "+jobExecution.getExecutionContext());
		jobExecution.getExecutionContext().put("jec", "jec value");
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		System.out.println("after job "+jobExecution.getJobInstance().getJobName());
		System.out.println("job param"+jobExecution.getJobParameters());
		System.out.println("job context "+jobExecution.getExecutionContext());

	}

}
