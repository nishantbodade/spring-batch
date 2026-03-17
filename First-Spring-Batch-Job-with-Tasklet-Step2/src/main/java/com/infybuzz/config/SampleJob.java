package com.infybuzz.config;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.infybuzz.mode.StudentCsv;
import com.infybuzz.mode.StudentJdbc;
import com.infybuzz.mode.StudentJson;
import com.infybuzz.mode.StudentXml;
import com.infybuzz.processor.FirstItemProcessor;
import com.infybuzz.reader.FirstItemReader;
import com.infybuzz.writer.FirstItemWriter;

@Configuration
public class SampleJob {

	/*
	 * @Autowired private StudentService studentService;
	 */

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

	@Autowired
	private DataSource dataSource;

	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource datasource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.eazyschooldatasource")
	public DataSource eazyschooldatasource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	public Job chunkJob() {
		return jobBuilderFactory.get("Chunk Job").incrementer(new RunIdIncrementer()).start(firstChunkStep())
				.build();
	}

	@Bean
	public Step firstChunkStep() {

		return stepBuilderFactory.get("First Chunk Step").<StudentCsv, StudentCsv>chunk(5)
				.reader(flatFileItemReader(null))
				// .reader(jsonItemReader(null))
				// .reader(jdbcCursorItemReader())
				// .reader(staxEventItemReader(null))
				// .reader(itemReaderAdapter())
				// .processor(firstItemProcessor)
				// .writer(flatFileItemWriter(null)).build();
				// .writer(jsonFileItemWriter(null)).build();
				//.writer(staxEventItemWriter(null)).build();
				.writer(jdbcBatchItemWriter2()).build();

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

		jsonItemReader.setCurrentItemCount(2);
		jsonItemReader.setMaxItemCount(8);
		return jsonItemReader;
	}

	public JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader() {

		JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader = new JdbcCursorItemReader<StudentJdbc>();

		jdbcCursorItemReader.setDataSource(eazyschooldatasource());

		jdbcCursorItemReader.setSql("SELECT id, first_name as firstName, last_name as lastName, email FROM student");
		jdbcCursorItemReader.setRowMapper(new BeanPropertyRowMapper<StudentJdbc>() {
			{
				setMappedClass(StudentJdbc.class);

			}
		});

		jdbcCursorItemReader.setCurrentItemCount(2);
		jdbcCursorItemReader.setMaxItemCount(8);

		return jdbcCursorItemReader;

	}

	@StepScope
	@Bean
	public StaxEventItemReader<StudentXml> staxEventItemReader(
			@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
		StaxEventItemReader<StudentXml> staxEventItemReader = new StaxEventItemReader<StudentXml>();

		staxEventItemReader.setResource(fileSystemResource);
		staxEventItemReader.setFragmentRootElementName("student");
		staxEventItemReader.setUnmarshaller(new Jaxb2Marshaller() {
			{
				setClassesToBeBound(StudentXml.class);
			}
		});

		return staxEventItemReader;
	}

	/*
	 * public ItemReaderAdapter<StudentResponse> itemReaderAdapter(){
	 * ItemReaderAdapter<StudentResponse> itemReaderAdapter= new
	 * ItemReaderAdapter<StudentResponse>();
	 * 
	 * itemReaderAdapter.setTargetObject(studentService);
	 * itemReaderAdapter.setTargetMethod("getStuResponse");
	 * itemReaderAdapter.setArguments(new Object[] {1}); return itemReaderAdapter;
	 * 
	 * }
	 */

	@Bean
	@StepScope
	public FlatFileItemWriter<StudentJdbc> flatFileItemWriter(
			@Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource) {
		FlatFileItemWriter<StudentJdbc> flatFileItemWriter = new FlatFileItemWriter<StudentJdbc>();
		flatFileItemWriter.setResource(fileSystemResource);
		flatFileItemWriter.setHeaderCallback(new FlatFileHeaderCallback() {

			@Override
			public void writeHeader(Writer writer) throws IOException {

				writer.write("Id,First Name,Last Name,Email");

			}
		});

		flatFileItemWriter.setLineAggregator(new DelimitedLineAggregator<StudentJdbc>() {

			{
				setDelimiter("|");
				setFieldExtractor(new BeanWrapperFieldExtractor<StudentJdbc>() {
					{
						setNames(new String[] { "id", "firstName", "lastName", "email" });
					}
				});
			}
		});

		flatFileItemWriter.setFooterCallback(new FlatFileFooterCallback() {

			@Override
			public void writeFooter(Writer writer) throws IOException {

				writer.write("Craeted @ " + new Date());

			}
		});

		return flatFileItemWriter;
	}

	@Bean
	@StepScope
	public JsonFileItemWriter<StudentJson> jsonFileItemWriter(
			@Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource) {
		JsonFileItemWriter<StudentJson> jsonFileItemWriter = new JsonFileItemWriter(fileSystemResource,
				new JacksonJsonObjectMarshaller<StudentJson>());

		return jsonFileItemWriter;
	}

	@Bean
	@StepScope
	public StaxEventItemWriter<StudentJdbc> staxEventItemWriter(
			@Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource) {

		StaxEventItemWriter<StudentJdbc> staxEventItemWriter = new StaxEventItemWriter<StudentJdbc>();
		staxEventItemWriter.setResource(fileSystemResource);
		staxEventItemWriter.setRootTagName("students");
		staxEventItemWriter.setMarshaller(new Jaxb2Marshaller() {
			{
				setClassesToBeBound(StudentJdbc.class);
			}
		});

		return staxEventItemWriter;
	}
	
	@Bean
	public JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter2() {
		JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter = new JdbcBatchItemWriter<StudentCsv>();
		jdbcBatchItemWriter.setDataSource(eazyschooldatasource());
		jdbcBatchItemWriter.setSql("INSERT INTO student (id, first_name, last_name, email) VALUES (:id, :firstName, :lastName, :email)");
		jdbcBatchItemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<StudentCsv>());
		return jdbcBatchItemWriter;
	}
}
