package test.foodfactory.domain;

import test.foodfactory.exceptions.CapacityExceededException;

import java.time.Duration;
import java.util.List;

public interface Oven {

	String getName();

	Double size();

	void put(Product product) throws CapacityExceededException;

	void take(Product product);

	void turnOn();

	void turnOn(Duration duration);

	List<Product> getCookedProducts();


}
