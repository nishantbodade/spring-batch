package com.infybuzz.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleJob {
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public Job firstJob() {
		return jobBuilderFactory.get("First Job").start(firstStep())
				.next(secondStep())
		.build();
	}
	
	@Bean
	public Step firstStep() {
		
		return stepBuilderFactory.get("First Step")
		.tasklet(firstTask())
		.build();
		
	}
	
	
	@Bean
	public Step secondStep() {
		
		return stepBuilderFactory.get("Second Step")
		.tasklet(secondTask())
		.build();
		
	}
	
	public Tasklet firstTask() {
		return new Tasklet() {
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("This is first Tasklet Step");
				return RepeatStatus.FINISHED;
			}
		};
		
	}
	
	public Tasklet secondTask() {
		return new Tasklet() {
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("This is second Tasklet Step");
				return RepeatStatus.FINISHED;
			}
		};
		
	}
	

	

}
