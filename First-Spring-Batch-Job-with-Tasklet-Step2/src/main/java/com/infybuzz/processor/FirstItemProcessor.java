package com.infybuzz.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.infybuzz.mode.StudentCsv;
import com.infybuzz.mode.StudentJdbc;
import com.infybuzz.mode.StudentJson;

@Component
public class FirstItemProcessor implements ItemProcessor<StudentCsv, StudentJson>{

	@Override
	public StudentJson process(StudentCsv item) throws Exception {
		System.out.println("Inside item processor");
		if(item.getId() == 5) {
			throw new NullPointerException();
		}
		StudentJson studentJson = new StudentJson();
		studentJson.setId(item.getId());
		studentJson.setFirstName(item.getFirstName());
		studentJson.setLastName(item.getLastName());
		studentJson.setEmail(item.getEmail());
		return studentJson;
		
	}

}
