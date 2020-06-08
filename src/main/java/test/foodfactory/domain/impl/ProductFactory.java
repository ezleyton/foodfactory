package test.foodfactory.domain.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import test.foodfactory.domain.AssemblyLineStage;
import test.foodfactory.domain.Product;

import java.time.Duration;

/**
 * This class works as pseudo factory creating instances of {@link GenericProduct} with the specified parameters.
 * Used by the main startup thread to delegate generic products to workers.
 *
 */
@Data
@AllArgsConstructor
public class ProductFactory {

	private final String productName;
	private final Double size;
	private final Duration cookTime;

	/**
	 * Constructor
	 * @param productStatus The {@link ProductStatus} of the product
	 * @param associatedLine The {@link AssemblyLineStart} that created this product
	 * @return an instance of {@link Product}
	 */
	public Product createProduct(ProductStatus productStatus,AssemblyLineStage associatedLine) {
		return new GenericProduct(this.productName, this.size, this.cookTime, associatedLine, productStatus);
	}


}
