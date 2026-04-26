package com.springbatch.processor;

import org.springframework.batch.item.ItemProcessor;

import com.springbatch.domain.Product;

public class FilterProductItemProcessor implements ItemProcessor<Product, Product> {

	@Override
	public Product process(Product item) throws Exception {
		System.out.println("filterProductItemProcessor() executed");
		if(item.getProductPrice() >  100) {
			return item;
		} else {
			return null;
		}
	}

}
