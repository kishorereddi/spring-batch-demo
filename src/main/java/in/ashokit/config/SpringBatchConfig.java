package in.ashokit.config;

import in.ashokit.entity.Customer;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {

	private JobBuilderFactory jobBuilderFactory;
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public FlatFileItemReader<Customer> customReader() {
		FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
		itemReader.setResource(new FileSystemResource("src/main/resources/customers.csv"));
		itemReader.setName("csv-reader");
		itemReader.setLinesToSkip(1);
		itemReader.setLineMapper(lineMapper());
		return itemReader;
	}

	@Bean(name = "duplicateItemWriter")
	public FlatFileItemWriter<Customer> dupItemWriter(){

		return new FlatFileItemWriterBuilder<Customer>()
				.name("duplicateItemWriter")
				.resource(new FileSystemResource("duplicateItem.txt"))
				.lineAggregator(new PassThroughLineAggregator<>())
				.append(true)
				.shouldDeleteIfExists(true)
				.build();
	}

	private LineMapper<Customer> lineMapper() {

		DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");

		BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Customer.class);

		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);

		return lineMapper;
	}

	@Bean
	public CustomProcessor customProcessor() {
		return new CustomProcessor();
	}

	@Bean
	public CustomWriter customWriter() {
		return new CustomWriter();
	}

	@Bean
	public AsyncItemProcessor asyncItemProcessor() throws Exception {
		AsyncItemProcessor<Customer, Customer> asyncItemProcessor = new AsyncItemProcessor();

		asyncItemProcessor.setDelegate(customProcessor());
		asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());
		asyncItemProcessor.afterPropertiesSet();

		return asyncItemProcessor;
	}

	@Bean
	public FlatFileItemWriter itemWriter() {
		return  new FlatFileItemWriterBuilder<Customer>()
				.name("itemWriter")
//				.resource(new FileSystemResource("target/test-outputs/output.txt"))
				.lineAggregator(new PassThroughLineAggregator<>())
				.build();
	}

	@Bean
	public ItemWriter<Customer> csvItemWriter() {
		FlatFileItemWriter<Customer> writer = new FlatFileItemWriter<>();
		writer.setResource(new FileSystemResource("output.csv")); // Output file location

		DelimitedLineAggregator<Customer> lineAggregator = new DelimitedLineAggregator<>();
		lineAggregator.setDelimiter(","); // CSV delimiter
		lineAggregator.setFieldExtractor(customer -> new String[]{customer.getFirstName(), customer.getLastName(), customer.getCountry()});

		writer.setLineAggregator(lineAggregator);
		writer.setHeaderCallback(writer2 -> writer2.write("First Name,Last Name, Status Code")); // CSV header

		return writer;
	}

	@Bean
	public AsyncItemWriter asyncItemWriter() throws Exception {
		AsyncItemWriter<Customer> asyncItemWriter = new AsyncItemWriter<>();

		asyncItemWriter.setDelegate(customWriter());
		asyncItemWriter.afterPropertiesSet();

		return asyncItemWriter;
	}

	public class PassThroughLineAggregator<T> implements LineAggregator<T> {

		public String aggregate(T item) {
			return item.toString();
		}
	}
	
	
	@Bean
	public Step step() throws Exception {
		return stepBuilderFactory.get("step-1").<Customer, Customer>chunk(500)
						  .reader(customReader())
						  .processor(customProcessor())
							.processor(asyncItemProcessor())
//						  .writer(customWriter())
//							.writer(csvItemWriter())
							.writer(asyncItemWriter())
//				.stream(dupItemWriter())
						  .taskExecutor(multiThreadTaskExecutor())
						  .build();
	}

	@Bean
	public TaskExecutor multiThreadTaskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(5); // Set your desired core pool size
		taskExecutor.setMaxPoolSize(100); // Set your desired maximum pool size
		taskExecutor.setQueueCapacity(200); // Set your desired queue capacity
		taskExecutor.afterPropertiesSet(); // Initialize the task executor
		return taskExecutor;
	}


	@Bean
	public Job job() throws Exception {
		return jobBuilderFactory.get("customers-import")
								.flow(step())
								.end()
								.build();
	}
	
	/*@Bean
	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
		taskExecutor.setConcurrencyLimit(100);
		return taskExecutor;
	}
	*/
	
}







