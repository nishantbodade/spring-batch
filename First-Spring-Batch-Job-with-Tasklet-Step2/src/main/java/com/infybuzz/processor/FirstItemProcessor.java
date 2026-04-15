package com.infybuzz.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.infybuzz.postgresql.Student;

@Component
public class FirstItemProcessor implements ItemProcessor<Student, com.infybuzz.mysql.Student>{

	@Override
	public com.infybuzz.mysql.Student process(Student item) throws Exception {
		
		System.out.println(item.getId());
		
		com.infybuzz.mysql.Student student = new 
				com.infybuzz.mysql.Student();
		
		student.setId(item.getId());
		student.setFirstName(item.getFirstName());
		student.setLastName(item.getLastName());
		student.setEmail(item.getEmail());
		student.setDeptId(item.getDeptId());
		student.setIsActive(item.getIsActive() != null ? 
				Boolean.valueOf(item.getIsActive()) : false);
		
		return student;
		
	}

}
