package com.springbatch.processor;

import org.springframework.batch.item.ItemProcessor;

import com.springbatch.domain.Product;

public class MyProductItemProcessor implements ItemProcessor<Product, Product> {

	@Override
	public Product process(Product item) throws Exception {
		System.out.println("processor executed..!");
		return item;
	}

}
