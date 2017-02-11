package com.ak.utils.bean.converter;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;

public class BeanTransformerTest {

	private Map<String, String> personToEmployeeMap = new HashMap<String, String>() {
		{
			put("name", "employeeName");
			put("address", "employeeAddress");
		}
	};

	private Map<String, String> personAddressToEmployeeAddressMap = new HashMap<String, String>() {
		{
			put("city", "employeeCity");
			put("country", "employeeCountry");
		}
	};

	@Test
	public void testPersonToEmployee() throws Exception {

		Person person = new Person();
		person.setName("Abhishek Kumar");
		Address address = new Address();
		address.setCity("Pune");
		address.setCountry("India");
		person.setAddress(address);

		BeanTransformer.addClassPropertyMap(Person.class, Employee.class, personToEmployeeMap);
		BeanTransformer.addClassPropertyMap(Address.class, EmployeeAddress.class, personAddressToEmployeeAddressMap);

		Employee employee = BeanTransformer.convert(person, Employee.class);

		System.out.println(BeanUtils.describe(employee));
		System.out.println(BeanUtils.describe(employee.getEmployeeAddress()));
	}

	@Test
	public void testEmployeeToPerson() throws Exception {

		Employee employee = new Employee();
		employee.setEmployeeName("Abhishek Kumar");
		EmployeeAddress employeeAddress = new EmployeeAddress();
		employeeAddress.setEmployeeCity("Pune");
		employeeAddress.setEmployeeCountry("India");
		employee.setEmployeeAddress(employeeAddress);

		BeanTransformer.addClassPropertyMap(Person.class, Employee.class, personToEmployeeMap);
		BeanTransformer.addClassPropertyMap(Address.class, EmployeeAddress.class, personAddressToEmployeeAddressMap);

		Person person = BeanTransformer.convert(employee, Person.class);

		System.out.println(BeanUtils.describe(person));
		System.out.println(BeanUtils.describe(person.getAddress()));
	}

	
}
