package banking.customers.application;

import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import banking.common.application.Notification;
import banking.customers.application.dto.CustomerDto;
import banking.customers.domain.entity.Customer;
import banking.customers.domain.repository.CustomerRepository;

@Service
public class CustomerApplicationService {
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	private ModelMapper mapper;
	
	@Value("${maxPageSize}")
	private int maxPageSize;
	
	@Autowired
	MessageSource messageSource;
    
	public CustomerDto create(CustomerDto customerDto) {
		Customer customer = mapper.map(customerDto, Customer.class);
		customer.setIsActive(true);
		customer.setAuditCreate(new Date());
		customer.setAuditUpdate(new Date());
		customer = customerRepository.save(customer);
		customerDto = mapper.map(customer, CustomerDto.class);
        return customerDto;
    }
	
	public List<CustomerDto> get(int page, int pageSize) {
		Notification notification = this.getValidation(page, pageSize);
        if (notification.hasErrors()) {
            throw new IllegalArgumentException(notification.errorMessage());
        }
		List<Customer> customers = this.customerRepository.get(page, pageSize);
		List<CustomerDto> customersDto = mapper.map(customers, new TypeToken<List<CustomerDto>>() {}.getType());
        return customersDto;
    }
	
	private Notification getValidation(int page, int pageSize) {
		Notification notification = new Notification();
		if (pageSize > maxPageSize) {
			notification.addError(messageSource.getMessage("page.size.cannot.greate", null, LocaleContextHolder.getLocale()));
		}
		return notification;
	}
}
