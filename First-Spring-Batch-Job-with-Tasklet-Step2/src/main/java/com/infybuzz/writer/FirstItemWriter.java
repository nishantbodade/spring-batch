package com.infybuzz.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.infybuzz.mode.StudentCsv;

@Component
public class FirstItemWriter implements ItemWriter<StudentCsv> {

	@Override
	public void write(List<? extends StudentCsv> items) throws Exception {
		System.out.println("inside item writer");
		items.stream().forEach(System.out::println);
		
	}

}
