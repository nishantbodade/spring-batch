package com.springbatch.processor;

import org.springframework.batch.item.ItemProcessor;

import com.springbatch.domain.OsProduct;
import com.springbatch.domain.Product;

public class MyProductItemProcessor implements ItemProcessor<Product, OsProduct> {

	@Override
	public OsProduct process(Product item) throws Exception {
		OsProduct osProduct=new OsProduct();
		osProduct.setProductId(item.getProductId());
		osProduct.setProductName(item.getProductName());
		osProduct.setProductCategory(item.getProductCategory());
		osProduct.setProductPrice(item.getProductPrice());
		osProduct.setTaxPercent(item.getProductPrice() > 500 ? 18 : 12);
		osProduct.setShippingRate(item.getProductPrice() > 500 ? 100 : 50);
		osProduct.setSku(item.getProductCategory().substring(0, 3).toUpperCase() + item.getProductId());
		return osProduct;
	}

}
