package com.infybuzz.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class FirstStepListener implements StepExecutionListener {

	@Override
	public void beforeStep(StepExecution stepExecution) {
		System.out.println("Before step "+stepExecution.getStepName());
		System.out.println("job context "+stepExecution.getJobExecution().getExecutionContext());
		System.out.println("step context "+stepExecution.getExecutionContext());
		stepExecution.getExecutionContext().put("step", "step value");
		
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		System.out.println("after step "+stepExecution.getStepName());
		System.out.println("job context "+stepExecution.getJobExecution().getExecutionContext());
		System.out.println("step context "+stepExecution.getExecutionContext());
		return null;
	}

}
