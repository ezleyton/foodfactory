package test.foodfactory.domain.impl;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import test.foodfactory.domain.AssemblyLineStage;
import test.foodfactory.domain.Product;
import test.foodfactory.domain.Store;

import java.time.Duration;

/**
 * Implementation for a generic product
 */
@Data
public class GenericProduct implements Product {

	@Getter(AccessLevel.NONE) //ignoring because it's enforced in the interface as per tests's specs
	private Double size;

	@Getter(AccessLevel.NONE) //ignoring because it's enforced in the interface as per tests's specs
	private Duration cookTime;

	private String name;

	//stores the name of the originating line
	private AssemblyLineStage associatedLine;

	private ProductStatus status;
	private Store associatedStore;

	/**
	 * Constructor
	 * @param name the product's name
	 * @param size the size it takes in an oven
	 * @param cooktime the time it takes to get cooked
	 * @param assemblyLineStage the assembly line that created the product.
	 */
	public GenericProduct(String name, Double size, Duration cooktime, AssemblyLineStage assemblyLineStage) {
		this.name = name;
		this.size = size;
		this.cookTime = cooktime;
		this.status = ProductStatus.PRODUCING;
		this.associatedLine = assemblyLineStage;
	}

	public GenericProduct(String name, Double size, Duration cooktime, AssemblyLineStage assemblyLineStage, ProductStatus productStatus) {
		this.name = name;
		this.size = size;
		this.cookTime = cooktime;
		this.status = productStatus;
		this.associatedLine = assemblyLineStage;
	}

	@Override
	public double size() {
		return this.size;
	}

	@Override
	public Duration cookTime() {
		return this.cookTime;
	}

	@Override
	public AssemblyLineStage getAssociatedLine() {
		return this.associatedLine;
	}

	public String getName() {
		return this.name;
	}
}
