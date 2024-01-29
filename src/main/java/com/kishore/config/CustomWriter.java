package com.kishore.config;

import com.kishore.entity.Customer;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.LineAggregator;

import java.util.List;

public class CustomWriter implements ItemWriter<Customer> {

    @Override
    public void write(List<? extends Customer> items) throws Exception {

        System.out.println("Number of Processed Items: " + items.size());

        /*FlatFileItemWriter<Customer> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("src/main/resources/testoutput.csv"));
        writer.setShouldDeleteIfEmpty(true);
        writer.setShouldDeleteIfExists(true);


        // Create a LineAggregator for CSV format
        LineAggregator<Customer> lineAggregator = createCustomerLineAggregator();
        writer.setLineAggregator(lineAggregator);

//        ExecutionContext stepContext = this.stepExecution.getExecutionContext();
//        stepContext.put("customers", items);

        // Write the items to the CSV file
        writer.open(new ExecutionContext());//TODO: Get stepContext
        writer.write(items);
        writer.close();*/
    }

    @BeforeStep
    private LineAggregator<Customer> createCustomerLineAggregator() {
        DelimitedLineAggregator<Customer> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(","); // CSV delimiter
        lineAggregator.setFieldExtractor(customer -> new String[]{customer.getFirstName(), customer.getLastName()});
        return lineAggregator;
    }
}
