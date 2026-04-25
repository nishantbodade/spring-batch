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
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.springbatch.domain.OsProduct;
import com.springbatch.domain.Product;
import com.springbatch.domain.ProductFieldSetMapper;
import com.springbatch.domain.ProductRowMapper;
import com.springbatch.domain.ProductValidator;
import com.springbatch.listener.MyChunkListener;
import com.springbatch.listener.MyItemProcessListener;
import com.springbatch.listener.MyItemReadListener;
import com.springbatch.listener.MyItemWriteListener;
import com.springbatch.processor.FilterDataItemProcessor;
import com.springbatch.processor.TransformProductItemProcessor;
import com.springbatch.reader.ProductNameItemReader;

@Configuration
public class BatchConfiguration {

	@Autowired
	public DataSource dataSource;

	@Bean
	public ItemReader<String> itemReader() {
		List<String> list = new ArrayList<String>();
		list.add("Product 1");
		list.add("Product 2");
		list.add("Product 3");
		list.add("Product 4");
		list.add("Product 5");
		return new ProductNameItemReader(list);
	}

	public ItemReader<Product> flatFileItemReader() {
		FlatFileItemReader<Product> fileItemReader = new FlatFileItemReader<Product>();
		fileItemReader.setLinesToSkip(1);
		fileItemReader.setResource(new ClassPathResource("/data/Product_Details.csv"));

		DefaultLineMapper<Product> lineMapper = new DefaultLineMapper<Product>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setNames("product_id", "product_name", "product_category", "product_price");
		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(new ProductFieldSetMapper());
		fileItemReader.setLineMapper(lineMapper);
		return fileItemReader;

	}

	@Bean
	public ItemReader<Product> jdbcCursorItemReader() {
		JdbcCursorItemReader<Product> itemReader = new JdbcCursorItemReader<Product>();
		itemReader.setDataSource(dataSource);
		itemReader.setSql("select * from product_details order by product_id");
		itemReader.setRowMapper(new ProductRowMapper());
		return itemReader;

	}

	@Bean
	public ItemReader<Product> jdbcPagingItemReader() throws Exception {
		JdbcPagingItemReader<Product> itemReader = new JdbcPagingItemReader<Product>();
		itemReader.setDataSource(dataSource);
		SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();
		factoryBean.setDataSource(dataSource);
		factoryBean.setSelectClause("select PRODUCT_ID,PRODUCT_NAME,PRODUCT_CATEGORY,PRODUCT_PRICE");
		factoryBean.setFromClause("from PRODUCT_DETAILS");
		factoryBean.setSortKey("PRODUCT_ID");
		itemReader.setQueryProvider(factoryBean.getObject());
		itemReader.setRowMapper(new ProductRowMapper());
		itemReader.setPageSize(2);

		return itemReader;

	}
	@Bean
	public ItemWriter<Product> flatFileItemWriter(){
		FlatFileItemWriter<Product> itemWriter =new FlatFileItemWriter<Product>();
		itemWriter.setResource(new FileSystemResource("output/product.csv"));
		
		DelimitedLineAggregator<Product> lineAggregator=new DelimitedLineAggregator<Product>();
		lineAggregator.setDelimiter(",");
		
		BeanWrapperFieldExtractor<Product> extractor=new BeanWrapperFieldExtractor<Product>();
		extractor.setNames(new String[] {"productId","productName","productCategory","productPrice"});
		
		lineAggregator.setFieldExtractor(extractor);
		
		itemWriter.setLineAggregator(lineAggregator);
		
		return itemWriter;
	}
	
	
	/*
	 * @Bean public JdbcBatchItemWriter<Product> jdbcBatchItemWriter() {
	 * JdbcBatchItemWriter<Product> itemWriter = new JdbcBatchItemWriter<Product>();
	 * itemWriter.setDataSource(dataSource); itemWriter.setSql(
	 * "insert into product_details_output values(:productId,:productName,:productCategory,:productPrice)"
	 * ); itemWriter.setItemSqlParameterSourceProvider(new
	 * BeanPropertyItemSqlParameterSourceProvider()); return itemWriter;
	 * 
	 * }
	 * 
	 * 
	 */
	  @Bean public JdbcBatchItemWriter<OsProduct> jdbcBatchItemWriter(){
	  JdbcBatchItemWriter<OsProduct> itemWriter=new
	  JdbcBatchItemWriter<OsProduct>(); itemWriter.setDataSource(dataSource);
	  itemWriter.
	  setSql("insert into OS_PRODUCT_DETAILS values(:productId,:productName,:productCategory,:productPrice,:taxPercent,:sku,:shippingRate)"
	  ); itemWriter.setItemSqlParameterSourceProvider(new
	  BeanPropertyItemSqlParameterSourceProvider()); return itemWriter;
	  
	  }
	 
	
	@Bean
	public ItemProcessor<Product, OsProduct> itemProcessor(){
		return new TransformProductItemProcessor();
	}
	
	@Bean
	public ItemProcessor<Product, Product> filterDataItemProcessor(){
		return new FilterDataItemProcessor();
	}
	
	@Bean
	public ValidatingItemProcessor<Product> itemValidationProcessor() {
		ValidatingItemProcessor<Product> processor = new ValidatingItemProcessor(new ProductValidator());
		processor.setFilter(true); // Filter out invalid items instead of throwing an exception
		return processor;
	}
	
	public CompositeItemProcessor<Product, OsProduct> compositeItemProcessor(){
		CompositeItemProcessor<Product, OsProduct> processor=new CompositeItemProcessor<Product, OsProduct>();
		List<ItemProcessor<?,?>> itemProcessors=new ArrayList<ItemProcessor<?,?>>();
		itemProcessors.add(itemValidationProcessor());
		itemProcessors.add(itemProcessor());
		processor.setDelegates(itemProcessors);
		return processor;
	}

	@Bean
	public Step step1(JobRepository jobRepository,PlatformTransactionManager transactionManager) throws Exception {
		return new StepBuilder("chunkBaseStep1",jobRepository)
				.<Product, OsProduct>chunk(2,transactionManager)
				.reader(jdbcPagingItemReader())
				.processor(compositeItemProcessor())
				.writer(jdbcBatchItemWriter())
				.listener(myChunkListener())
				.listener(myItemReadListener())
				.listener(myItemProcessListener())
				.listener(myItemWriteListener())
				.build();
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
	public Job firstJob(JobRepository jobRepository,Step step1) throws Exception {
		return new JobBuilder("job1",jobRepository).start(step1)
				.build();
	}
}
