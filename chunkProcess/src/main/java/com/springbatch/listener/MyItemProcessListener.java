package com.springbatch.listener;

import org.springframework.batch.core.ItemProcessListener;

import com.springbatch.domain.OsProduct;
import com.springbatch.domain.Product;

public class MyItemProcessListener implements ItemProcessListener<Product, OsProduct> {

	@Override
	public void beforeProcess(Product item) {
		System.out.println("beforeProcess() executed for product " + item.getProductId());
	}

	@Override
	public void afterProcess(Product item, OsProduct result) {
		System.out.println("afterProcess() executed for product " + item.getProductId());
	}

	@Override
	public void onProcessError(Product item, Exception e) {
		System.out.println("onProcessError() executed for product " + item.getProductId());
	}

}
