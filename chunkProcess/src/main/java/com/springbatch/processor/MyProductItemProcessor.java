package com.springbatch.processor;

import org.springframework.batch.item.ItemProcessor;

import com.springbatch.domain.Product;

public class MyProductItemProcessor implements ItemProcessor<Product, Product> {

	@Override
	public Product process(Product item) throws Exception {
		System.out.println("processor executed..!");
		Integer price=item.getProductPrice();
		item.setProductPrice((int)(price-((0.1)*1)));
		return item;
	}

}
