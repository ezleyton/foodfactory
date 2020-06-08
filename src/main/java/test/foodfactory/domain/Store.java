package test.foodfactory.domain;

import test.foodfactory.exceptions.CapacityExceededException;

public interface Store {

	String getName();

	void put(Product product) throws CapacityExceededException;

	Product take();

	void take(Product product);

	Product peek();
}
