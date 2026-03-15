package com.infybuzz.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.infybuzz.mode.StudentXml;

@Component
public class FirstItemWriter implements ItemWriter<StudentXml> {

	@Override
	public void write(List<? extends StudentXml> items) throws Exception {
		System.out.println("inside item writer");
		items.stream().forEach(System.out::println);
		
	}

}
