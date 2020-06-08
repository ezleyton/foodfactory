package test.foodfactory.domain;

import test.foodfactory.domain.impl.ProductStatus;

import java.time.Duration;

public interface Product {

	String getName();

	double size();

	Duration cookTime();

	//reference to the originating line
	AssemblyLineStage getAssociatedLine();

	Store getAssociatedStore();
	void setAssociatedStore(Store store);

	//record used for control
	ProductStatus getStatus();
	void setStatus(ProductStatus productStatus);
}
