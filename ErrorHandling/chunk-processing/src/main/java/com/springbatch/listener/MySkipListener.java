package com.springbatch.listener;

import java.io.FileWriter;
import java.io.IOException;

import org.springframework.batch.core.SkipListener;

import com.springbatch.domain.OSProduct;
import com.springbatch.domain.Product;

public class MySkipListener implements SkipListener<Product, OSProduct> {

	@Override
	public void onSkipInRead(Throwable t) {
		// TODO Auto-generated method stub
		SkipListener.super.onSkipInRead(t);
	}

	@Override
	public void onSkipInWrite(OSProduct item, Throwable t) {
		// TODO Auto-generated method stub
		SkipListener.super.onSkipInWrite(item, t);
	}

	@Override
	public void onSkipInProcess(Product item, Throwable t) {
		System.out.println("Skipped Item:- ");
		System.out.println(item);
		writeToFile(item.toString());
	}
	
	public void writeToFile(String data) {
		try {
			FileWriter fileWriter = new FileWriter("rejected/Product_Details_Rejected.txt", true);
			fileWriter.write(data + "\n");
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
