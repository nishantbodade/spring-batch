package com.infybuzz.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.infybuzz.mode.StudentJdbc;
import com.infybuzz.mode.StudentJson;

@Component
public class FirstItemProcessor implements ItemProcessor<StudentJdbc, StudentJson>{

	@Override
	public StudentJson process(StudentJdbc item) throws Exception {
		System.out.println("Inside item processor");
		StudentJson studentJson = new StudentJson();
		studentJson.setId(item.getId());
		studentJson.setFirstName(item.getFirstName());
		studentJson.setLastName(item.getLastName());
		studentJson.setEmail(item.getEmail());
		return studentJson;
		
	}

}
