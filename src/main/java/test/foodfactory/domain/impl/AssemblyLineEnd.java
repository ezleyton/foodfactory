package test.foodfactory.domain.impl;

import lombok.Data;
import test.foodfactory.domain.AssemblyLineStage;
import test.foodfactory.domain.Product;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * An implementation of {@link AssemblyLineStage} representing the end of an assembly line, used to store coocked objects.
 *
 */
@Data
public class AssemblyLineEnd implements AssemblyLineStage {

	private ConcurrentLinkedQueue<Product> line;

	private String name;

	/**
	 *
	 * @param name the name of the assembly line
	 *
	 */
	public AssemblyLineEnd(String name) {
		this.name = name;
		line = new ConcurrentLinkedQueue<>();
	}

	@Override
	public void putAfter(Product product) {
		System.out.printf("LINE_END: [%s] received product [%s] with status [%s]%n", this.name, product.getName(), product.getStatus().name());
		line.add(product);
	}

	@Override
	public Product take() {
		return line.poll();
	}
}
