package com.kishore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//@SpringBootApplication
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Application {

	public static void main(String[] args) {

		// Record the start time
		long startTime = System.nanoTime();

		SpringApplication.run(Application.class, args);

		long endTime = System.nanoTime();

		long runtime = endTime - startTime;

		double seconds = (double) runtime / 1_000_000_000;

		System.out.println("Total Execution time: " + seconds + " seconds");
	}
}
