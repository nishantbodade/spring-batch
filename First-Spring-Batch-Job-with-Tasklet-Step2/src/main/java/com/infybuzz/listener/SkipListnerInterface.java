package com.infybuzz.listener;

import java.io.FileWriter;

import org.springframework.batch.core.SkipListener;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

import com.infybuzz.mode.StudentCsv;
import com.infybuzz.mode.StudentJson;

@Component
public class SkipListnerInterface implements SkipListener<StudentCsv, StudentJson> {

	@Override
	public void onSkipInRead(Throwable t) {
		if (t instanceof FlatFileParseException) {
			createFile(
					"C:\\DataDrive\\Tutorial\\Spring batch\\Practice\\First-Spring-Batch-Job-with-Tasklet-Step2\\Chunk Job\\First Chunk Step\\reader\\skipInRead.txt",
					((FlatFileParseException)t).getInput());
		}
		
	}

	@Override
	public void onSkipInWrite(StudentJson item, Throwable t) {
		createFile(
				"C:\\DataDrive\\Tutorial\\Spring batch\\Practice\\First-Spring-Batch-Job-with-Tasklet-Step2\\Chunk Job\\First Chunk Step\\writer\\skipInWrite.txt",
				item.toString());
		
	}

	@Override
	public void onSkipInProcess(StudentCsv item, Throwable t) {
		createFile(
				"C:\\DataDrive\\Tutorial\\Spring batch\\Practice\\First-Spring-Batch-Job-with-Tasklet-Step2\\Chunk Job\\First Chunk Step\\processor\\skipInProcess.txt",
				item.toString());
		
	}
	
	public void createFile(String path, String data) {
		try (FileWriter fileWriter = new FileWriter(path, true)) {
			fileWriter.write(data + "\n");

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
