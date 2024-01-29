package com.kishore.config;

import com.kishore.GetApiExample;
import com.kishore.entity.Customer;
import org.springframework.batch.item.ItemProcessor;

public class CustomProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer item) {

//		System.out.println("Processing: " + item);
        item.setCountry(GetApiExample.callAPI());

        return item;
    }

}
