package com.infybuzz.listener;

import java.io.FileWriter;

import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

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

	public void createFile(String path, String data) {
		try (FileWriter fileWriter = new FileWriter(path, true)) {
			fileWriter.write(data + "\n");

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
