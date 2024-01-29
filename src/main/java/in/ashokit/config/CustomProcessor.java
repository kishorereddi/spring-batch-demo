package in.ashokit.config;

import in.ashokit.GetApiExample;
import in.ashokit.entity.Customer;
import org.springframework.batch.item.ItemProcessor;

public class CustomProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer item) {

//		System.out.println("Processing: " + item);
        item.setCountry(GetApiExample.callAPI());

        return item;
    }

}
