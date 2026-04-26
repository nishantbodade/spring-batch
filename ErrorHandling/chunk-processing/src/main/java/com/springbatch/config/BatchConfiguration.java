package com.springbatch.config;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.springbatch.domain.OSProduct;
import com.springbatch.domain.Product;
import com.springbatch.domain.ProductFieldSetMapper;
import com.springbatch.domain.ProductRowMapper;
import com.springbatch.domain.ProductValidator;
import com.springbatch.listener.MyChunkListener;
import com.springbatch.listener.MyItemProcessListener;
import com.springbatch.listener.MyItemReadListener;
import com.springbatch.listener.MyItemWriteListener;
import com.springbatch.listener.MySkipListener;
import com.springbatch.processor.FilterProductItemProcessor;
import com.springbatch.processor.TransformProductItemProcessor;
import com.springbatch.reader.ProductNameItemReader;

@Configuration
public class BatchConfiguration {
	
	@Autowired
	public DataSource dataSource;
	
	@Bean
	public ItemReader<String> itemReader() {
		List<String> productList = new ArrayList<>();
		productList.add("Product 1");
		productList.add("Product 2");
		productList.add("Product 3");
		productList.add("Product 4");
		productList.add("Product 5");
		productList.add("Product 6");
		productList.add("Product 7");
		productList.add("Product 8");
		return new ProductNameItemReader(productList);
	}
	
	@Bean
	public ItemReader<Product> flatFileItemReader() {
		FlatFileItemReader<Product> itemReader = new FlatFileItemReader<>();
		itemReader.setLinesToSkip(1);
		itemReader.setResource(new ClassPathResource("/data/Product_Details.csv"));

		DefaultLineMapper<Product> lineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setNames("product_id", "product_name", "product_category", "product_price");

		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(new ProductFieldSetMapper());

		itemReader.setLineMapper(lineMapper);

		return itemReader;
	}
	
	@Bean
	public ItemReader<Product> jdbcCursorItemReader() {
		JdbcCursorItemReader<Product> itemReader = new JdbcCursorItemReader<>();
		itemReader.setDataSource(dataSource);
		itemReader.setSql("select * from product_details order by product_id");
		itemReader.setRowMapper(new ProductRowMapper());
		return itemReader;
	}
	
	@Bean
	public ItemReader<Product> jdbcPagingItemReader() throws Exception {
		JdbcPagingItemReader<Product> itemReader = new JdbcPagingItemReader<>();
		itemReader.setDataSource(dataSource);
		
		SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();
		factory.setDataSource(dataSource);
		factory.setSelectClause("select product_id, product_name, product_category, product_price");
		factory.setFromClause("from product_details");
		factory.setSortKey("product_id");
		
		itemReader.setQueryProvider(factory.getObject());
		itemReader.setRowMapper(new ProductRowMapper());
		itemReader.setPageSize(3);
		
		return itemReader;
	}
	
	@Bean
	public ItemWriter<Product> flatFileItemWriter() {
		FlatFileItemWriter<Product> itemWriter = new FlatFileItemWriter<>();
		itemWriter.setResource(new FileSystemResource("output/Product_Details_Output.csv"));
		
		DelimitedLineAggregator<Product> lineAggregator = new DelimitedLineAggregator<>();
		lineAggregator.setDelimiter(",");
		
		BeanWrapperFieldExtractor<Product> fieldExtractor = new BeanWrapperFieldExtractor<>();
		fieldExtractor.setNames(new String[] { "productId", "productName", "productCategory", "productPrice" });
		
		lineAggregator.setFieldExtractor(fieldExtractor);
		
		itemWriter.setLineAggregator(lineAggregator);
		return itemWriter;
	}
	
//	@Bean
//	public JdbcBatchItemWriter<Product> jdbcBatchItemWriter() {
//		JdbcBatchItemWriter<Product> itemWriter = new JdbcBatchItemWriter<>();
//		itemWriter.setDataSource(dataSource);
//		itemWriter.setSql("insert into product_details_output values (:productId, :productName, :productCategory, :productPrice)");
//		itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
//		return itemWriter;
//	}
	
	@Bean
	public JdbcBatchItemWriter<OSProduct> jdbcBatchItemWriter() {
		JdbcBatchItemWriter<OSProduct> itemWriter = new JdbcBatchItemWriter<>();
		itemWriter.setDataSource(dataSource);
		itemWriter.setSql("insert into os_product_details values (:productId, :productName, :productCategory, :productPrice, :taxPercent, :sku, :shippingRate)");
		itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
		return itemWriter;
	}
	
	@Bean
	public ItemProcessor<Product, Product> filterProductItemProcessor() {
		return new FilterProductItemProcessor();
	}
	
	@Bean
	public ItemProcessor<Product, OSProduct> transformProductItemProcessor() {
		return new TransformProductItemProcessor();
	}
	
//	@Bean
//	public ValidatingItemProcessor<Product> validateProductItemProcessor() {
//		ValidatingItemProcessor<Product> validatingItemProcessor = new ValidatingItemProcessor<>(new ProductValidator());
//		validatingItemProcessor.setFilter(true);
//		return validatingItemProcessor;
//	}
	
	@Bean
	public BeanValidatingItemProcessor<Product> validateProductItemProcessor() {
		BeanValidatingItemProcessor<Product> beanValidatingItemProcessor = new BeanValidatingItemProcessor<>();
		//beanValidatingItemProcessor.setFilter(true);
		return beanValidatingItemProcessor;
	}
	
	@Bean
	public CompositeItemProcessor<Product, OSProduct> itemProcessor() {
		CompositeItemProcessor<Product, OSProduct> itemProcessor = new CompositeItemProcessor<>();
		List itemProcessors = new ArrayList();
		itemProcessors.add(validateProductItemProcessor());
		itemProcessors.add(filterProductItemProcessor());
		itemProcessors.add(transformProductItemProcessor());
		itemProcessor.setDelegates(itemProcessors);
		return itemProcessor;
	}
	
	@Bean
	public MyChunkListener myChunkListener() {
		return new MyChunkListener();
	}
	
	@Bean
	public MyItemReadListener myItemReadListener() {
		return new MyItemReadListener();
	}
	
	@Bean
	public MyItemProcessListener myItemProcessListener() {
		return new MyItemProcessListener();
	}
	
	@Bean
	public MyItemWriteListener myItemWriteListener() {
		return new MyItemWriteListener();
	}
	
	@Bean
	public MySkipListener mySkipListener() {
		return new MySkipListener();
	}
	
	@Bean
	public Step step1(JobRepository jobRespository, PlatformTransactionManager transactionManager) throws Exception {
		return new StepBuilder("chunkBasedStep1", jobRespository)
				.<Product,OSProduct>chunk(3, transactionManager)
				.reader(flatFileItemReader())
				.processor(itemProcessor())
				.writer(jdbcBatchItemWriter())
				.faultTolerant()
				.skip(ValidationException.class)
				.skipLimit(2)
				.listener(mySkipListener())
//				.listener(myChunkListener())
//				.listener(myItemReadListener())
//				.listener(myItemProcessListener())
//				.listener(myItemWriteListener())
				.build();
	}
	
	@Bean
	public Job firstJob(JobRepository jobRepository, Step step1) throws Exception {
		return new JobBuilder("job1", jobRepository)
				.start(step1)
				.build();
	}
}
