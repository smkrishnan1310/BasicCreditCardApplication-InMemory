package com.credti.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

/** Logging functionality to generate log file and to print in console */
public class Logger {

	private static final LinkedBlockingQueue<String> LOG = new LinkedBlockingQueue<String>();
	private static final File LOG_FILE = new File("BankLog.txt");
	private static OutputStream stream;

	private static final Runnable proc = () -> {
		while (true) {
			try {
				String msg = LOG.take();
				print(msg);
				writeInFile(msg + System.lineSeparator());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};

	private static void print(String msg) {
		System.out.println(msg);
	}

	private static void writeInFile(String msg) {
		try {
			stream.write(msg.getBytes(), 0, msg.length());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Thread logService;

	static {
		System.out.println("Log file generating @ pom file folder location. File name: " + LOG_FILE.getPath());
		stream = getOutputStream();
		logService = new Thread(proc);
		logService.setDaemon(true);
		logService.start();
	}

	private static OutputStream getOutputStream() {
		try {
			OutputStream os = new FileOutputStream(LOG_FILE);
			return os;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static final void log(final String msg) {
		LOG.offer((new Date()) + " :: " + msg);
	}

}
