
package com.infybuzz.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.infybuzz.mode.StudentCsv;
import com.infybuzz.mode.StudentJson;
import com.infybuzz.mode.StudentResponse;

@Service
public class StudentService {
	List<StudentResponse> list;

	public List<StudentResponse> getAllStudents() {

		RestTemplate restTemplate = new RestTemplate();
		StudentResponse[] students = restTemplate.getForObject("http://localhost:8081/api/v1/students",
				StudentResponse[].class);
		list = new ArrayList<StudentResponse>();
		for (StudentResponse student : students) {
			list.add(student);
		}

		return list;

	}

	public StudentResponse getStuResponse(int i) {
		System.out.println("inside getStuResponse" + i);
		if (list == null) {
			getAllStudents();
		}
		if (list != null && !list.isEmpty()) {
			return list.remove(0);
		}
		return null;
	}
	
	public StudentResponse resrCallToCreateStudent(StudentCsv studentCsv) {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.postForObject("http://localhost:8081/api/v1/createStudents", studentCsv,StudentResponse.class);
	}

}
