package com.springbatch.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.springbatch.reader.ProductNameItemReader;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public ItemReader<String> itemReader(){
		List<String> list=new ArrayList<String>();
		list.add("Product 1");
		list.add("Product 2");
		list.add("Product 3");
		list.add("Product 4");
		list.add("Product 5");
		return new ProductNameItemReader(list);
	}

	@Bean
	public Step step1() {
		return this.stepBuilderFactory.get("chunkBaseStep1")
				.<String,String>chunk(2)
				.reader(itemReader())
				.writer(new ItemWriter<String>(){

					@Override
					public void write(List<? extends String> items) throws Exception {
						System.out.println("chunk-processing started");
						items.forEach(System.out::println);
						System.out.println("chunk-processing stop");
						
					}
					
				}).build();
	}

	@Bean
	public Job firstJob() {
		return this.jobBuilderFactory.get("job1").start(step1())

				.build();
	}
}
