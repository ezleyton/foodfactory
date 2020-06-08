package test.foodfactory.domain.impl;

import test.foodfactory.domain.Product;

import java.util.TimerTask;

/**
 * Timed Task for injecting new Products into an {@link AssemblyLineStart}
 *
 */
public class TimedProductInjector extends TimerTask {

	AssemblyLineStart assemblyLine;
	ProductFactory productFactory;

	/**
	 * Constructor
	 * @param line the {@link AssemblyLineStart} to associate created products
	 * @param productFactory the {@link ProductFactory} for the product
	 */
	public TimedProductInjector(AssemblyLineStart line, ProductFactory productFactory) {
		this.assemblyLine = line;
		this.productFactory = productFactory;
	}

	@Override
	public void run() {

		if (assemblyLine.getIsActive().get()) { //small check to avoid inserting unneeded products
			Product product = productFactory.createProduct(ProductStatus.PRODUCING, assemblyLine);
			assemblyLine.putAfter(product);
		}

	}
}
