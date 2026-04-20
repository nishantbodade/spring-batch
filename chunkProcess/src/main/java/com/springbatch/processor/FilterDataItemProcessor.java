package com.springbatch.processor;

import org.springframework.batch.item.ItemProcessor;

import com.springbatch.domain.Product;

public class FilterDataItemProcessor implements ItemProcessor<Product, Product> {

	@Override
	public Product process(Product item) throws Exception {
		System.out.println("FilterDataItemProcessor processing: " + item);
		if(item.getProductPrice() <= 100) {
			return null; // Filter out products with price less than 100
		}else {
			return item; // Pass through products with price 100 or more
		}
		
	}

}
