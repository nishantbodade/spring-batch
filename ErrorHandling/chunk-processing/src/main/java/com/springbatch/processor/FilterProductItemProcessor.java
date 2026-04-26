package com.springbatch.processor;

import org.springframework.batch.item.ItemProcessor;

import com.springbatch.domain.Product;
import com.springbatch.exception.MyException;

public class FilterProductItemProcessor implements ItemProcessor<Product, Product> {

	@Override
	public Product process(Product item) throws Exception {
		System.out.println("filterProductItemProcessor() executed for product " + item.getProductId());
//		if(item.getProductPrice() >  100) {
//			return item;
//		} else {
//			return null;
//		}
		if(item.getProductPrice() == 500) {
			System.out.println("Exception Thrown");
			throw new MyException("Test Exception");
		}
		return item;
	}

}
