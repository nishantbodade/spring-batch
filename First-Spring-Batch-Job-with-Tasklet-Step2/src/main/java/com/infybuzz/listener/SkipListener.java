package com.infybuzz.listener;

import java.io.FileWriter;

import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.core.annotation.OnSkipInWrite;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

import com.infybuzz.mode.StudentCsv;
import com.infybuzz.mode.StudentJson;

@Component
public class SkipListener {

	@OnSkipInRead
	public void onSkipInRead(Throwable t) {
		if (t instanceof FlatFileParseException) {
			createFile(
					"C:\\DataDrive\\Tutorial\\Spring batch\\Practice\\First-Spring-Batch-Job-with-Tasklet-Step2\\Chunk Job\\First Chunk Step\\reader\\skipInRead.txt",
					((FlatFileParseException)t).getInput());
		}
	}
	
	@OnSkipInProcess
	public void onSkipInRead(StudentCsv studentCsv,Throwable t) {
		
			createFile(
					"C:\\DataDrive\\Tutorial\\Spring batch\\Practice\\First-Spring-Batch-Job-with-Tasklet-Step2\\Chunk Job\\First Chunk Step\\processor\\skipInRead.txt",
					studentCsv.toString());
		
	}
	
	@OnSkipInWrite
	public void onSkipInWrite(StudentJson studentJson,Throwable t) {
		
			createFile(
					"C:\\DataDrive\\Tutorial\\Spring batch\\Practice\\First-Spring-Batch-Job-with-Tasklet-Step2\\Chunk Job\\First Chunk Step\\writer\\skipInWrite.txt",
					studentJson.toString());
		
	}

	public void createFile(String path, String data) {
		try (FileWriter fileWriter = new FileWriter(path, true)) {
			fileWriter.write(data + "\n");

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
