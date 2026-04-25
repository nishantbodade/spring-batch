package com.springbatch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.springbatch.decider.MyJobExecutionDecider;
import com.springbatch.listener.MyJobExecutionListener;
import com.springbatch.listener.MyStepExecutionListener;

@Configuration
public class BatchConfiguration {
	
	@Bean
	public MyJobExecutionListener myJobExecutionListener() {
		return new MyJobExecutionListener();
	}
	
	@Bean
	public StepExecutionListener myStepExecutionListener() {
		return new MyStepExecutionListener();
	}
	
	@Bean
	public JobExecutionDecider decider() {
		return new MyJobExecutionDecider();
	}
	
	@Bean
	public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManger) {
		return new StepBuilder("step1", jobRepository).tasklet(new Tasklet() {
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("step1 executed on thread " + Thread.currentThread().getName());
				return RepeatStatus.FINISHED;
			}
		}, transactionManger).build();
	}
	
	@Bean
	public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManger) {
		return new StepBuilder("step2", jobRepository).tasklet(new Tasklet() {
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("step2 executed on thread " + Thread.currentThread().getName());
				return RepeatStatus.FINISHED;
			}
		}, transactionManger).build();
	}
	
	@Bean
	public Step step3(JobRepository jobRepository, PlatformTransactionManager transactionManger) {
		return new StepBuilder("step3", jobRepository).tasklet(new Tasklet() {
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("step3 executed on thread " + Thread.currentThread().getName());
				return RepeatStatus.FINISHED;
			}
		}, transactionManger).build();
	}
	
	@Bean
	public Step step4(JobRepository jobRepository, PlatformTransactionManager transactionManger) {
		return new StepBuilder("step4", jobRepository).tasklet(new Tasklet() {
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("step4 executed on thread " + Thread.currentThread().getName());
				return RepeatStatus.FINISHED;
			}
		}, transactionManger).build();
	}
	
	@Bean
	public Step step5(JobRepository jobRepository, PlatformTransactionManager transactionManger) {
		return new StepBuilder("step5", jobRepository).tasklet(new Tasklet() {
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				boolean isFailure = false;
				if(isFailure) {
					throw new Exception("Test Exception");
				}
				System.out.println("step5 executed on thread " + Thread.currentThread().getName());
				return RepeatStatus.FINISHED;
			}
		}, transactionManger).build();
	}
	
	@Bean
	public Step step6(JobRepository jobRepository, PlatformTransactionManager transactionManger) {
		return new StepBuilder("step6", jobRepository).tasklet(new Tasklet() {
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("step6 executed on thread " + Thread.currentThread().getName());
				return RepeatStatus.FINISHED;
			}
		}, transactionManger).build();
	}
	
	@Bean
	public Step step7(JobRepository jobRepository, PlatformTransactionManager transactionManger) {
		return new StepBuilder("step7", jobRepository).tasklet(new Tasklet() {
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("step7 executed on thread " + Thread.currentThread().getName());
				return RepeatStatus.FINISHED;
			}
		}, transactionManger).build();
	}
	
	@Bean
	public Step step8(JobRepository jobRepository, PlatformTransactionManager transactionManger) {
		return new StepBuilder("step8", jobRepository).tasklet(new Tasklet() {
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("step8 executed on thread " + Thread.currentThread().getName());
				return RepeatStatus.FINISHED;
			}
		}, transactionManger).build();
	}
	
	@Bean
	public Step job3Step(JobRepository jobRepository, Job job3) {
		return new StepBuilder("job3Step", jobRepository).job(job3).build();
	}
	
	@Bean
	public Flow flow1(Step step3, Step step4) {
		FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flow1");
		flowBuilder.start(step3)
				.next(step4)
				.end();
		return flowBuilder.build();
	}
	
	@Bean
	public Flow flow2(Step step5, Step step6) {
		FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flow2");
		flowBuilder.start(step5)
				.next(step6)
				.end();
		return flowBuilder.build();
	}
	
	@Bean
	public Flow flow3(Step step7, Step step8) {
		FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flow3");
		flowBuilder.start(step7)
				.next(step8)
				.end();
		return flowBuilder.build();
	}
	
	@Bean
	public Flow splitFlow(Flow flow1, Flow flow2, Flow flow3) {
		return new FlowBuilder<Flow>("splitFlow")
				.split(new SimpleAsyncTaskExecutor())
				.add(flow1, flow2, flow3)
				.build();
	}
	
	@Bean
	public Job job1(JobRepository jobRepository, Step step1, Step step2, Flow flow1) {
		return new JobBuilder("job1", jobRepository)
				.start(step1)
				.next(step2)
					.on("COMPLETED").to(flow1)
				.end()
				.build();
	}
	
	@Bean
	public Job job2(JobRepository jobRepository, Step job3Step, Flow splitFlow) {
		return new JobBuilder("job2", jobRepository)
				.listener(myJobExecutionListener())
				.start(splitFlow)
				.end()
				.build();
	}
	
	@Bean
	public Job job3(JobRepository jobRepository, Step step5, Step step6) {
		return new JobBuilder("job3", jobRepository)
				.start(step5)
				.next(step6)
				.build();
	}
}
