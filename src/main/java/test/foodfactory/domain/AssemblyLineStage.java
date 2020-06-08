package test.foodfactory.domain;

/**
 * This represents an assembly line stage of the factory. Implementations of this class should be thread-safe
 */
public interface AssemblyLineStage {

	/**
	 * Put the specified product to the assembly line to continue in the next stage.
	 * ​
	 */
	void putAfter(Product product);

	/**
	 * Takes the next product available from the assembly line.
	 */
	Product take();
}
