package com.infybuzz.config;

import java.io.File;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.infybuzz.mode.StudentCsv;
import com.infybuzz.mode.StudentJson;
import com.infybuzz.processor.FirstItemProcessor;
import com.infybuzz.reader.FirstItemReader;
import com.infybuzz.writer.FirstItemWriter;

@Configuration
public class SampleJob {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private FirstItemReader firstItemReader;

	@Autowired
	private FirstItemProcessor firstItemProcessor;

	@Autowired
	private FirstItemWriter firstItemWriter;

	@Bean
	public Job chunkJob() {
		return jobBuilderFactory.get("Chunk Job").incrementer(new RunIdIncrementer()).start(firstChunkStep())
				.next(firstChunkStep()).build();
	}

	@Bean
	public Step firstChunkStep() {

		return stepBuilderFactory.get("First Chunk Step").<StudentJson, StudentJson>chunk(3)
				//.reader(flatFileItemReader(null))
				.reader(jsonItemReader(null))
				// .processor(firstItemProcessor)
				.writer(firstItemWriter).build();

	}

	@StepScope
	@Bean
	public FlatFileItemReader<StudentCsv> flatFileItemReader(

			@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource

	) {
		FlatFileItemReader<StudentCsv> flatFileItemReader = new FlatFileItemReader<StudentCsv>();

		flatFileItemReader.setResource(fileSystemResource);

		/*
		 * flatFileItemReader.setLineMapper(new DefaultLineMapper<StudentCsv>() { {
		 * setLineTokenizer(new DelimitedLineTokenizer("|") {
		 * 
		 * { setNames("Id", "First Name", "Last Name", "Email"); //setDelimiter("|"); }
		 * 
		 * });
		 * 
		 * setFieldSetMapper(new BeanWrapperFieldSetMapper<StudentCsv>() { {
		 * setTargetType(StudentCsv.class); } }); }
		 * 
		 * });
		 */

		DefaultLineMapper<StudentCsv> defaultLineMapper = new DefaultLineMapper<StudentCsv>();
		DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer("|");
		delimitedLineTokenizer.setNames("Id", "First Name", "Last Name", "Email");

		defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
		BeanWrapperFieldSetMapper<StudentCsv> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<StudentCsv>();
		beanWrapperFieldSetMapper.setTargetType(StudentCsv.class);
		defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
		flatFileItemReader.setLineMapper(defaultLineMapper);

		flatFileItemReader.setLinesToSkip(1);

		return flatFileItemReader;

	}

	@Bean
	@StepScope
	public JsonItemReader<StudentJson> jsonItemReader(
			@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource

	) {

		JsonItemReader<StudentJson> jsonItemReader = new JsonItemReader<StudentJson>();
		jsonItemReader.setResource(fileSystemResource);
		
		jsonItemReader.setJsonObjectReader(new JacksonJsonObjectReader<StudentJson>(StudentJson.class));

		return jsonItemReader;
	}
}
