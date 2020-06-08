package test.foodfactory.domain.impl;

import lombok.Data;
import test.foodfactory.domain.Oven;
import test.foodfactory.domain.Product;
import test.foodfactory.exceptions.CapacityExceededException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 *
 * A very basic implementation of {@link Oven}
 *
 */
public class GasOven implements Oven {

	private AtomicReference<Double> size = new AtomicReference<>();
	private List<CookingProductDecorator> cookingProducts = Collections.synchronizedList(new ArrayList<>());
	private AtomicBoolean isActive = new AtomicBoolean(false);
	private String name;

	/**
	 * Constructor
	 * @param size the size of the oven
	 * @param name it's name
	 */
	public GasOven(Double size, String name) {
		this.size.set(size);
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Double size() {
		return size.get();
	}

	@Override
	public synchronized void put(Product product) throws CapacityExceededException {
		Double availableSize = this.size.get();
		if (availableSize >= product.size()) {
			availableSize -= product.size();
			this.size.set(availableSize);
			if (!isActive.get()) {
				this.turnOn(product.cookTime());
			} else {
				//TODO: update oven duration
			}
			//we calculate the timestamp for the cooking process
			product.setStatus(ProductStatus.COOKING);
			Long cookingEnd = System.currentTimeMillis() + product.cookTime().toMillis();
			this.cookingProducts.add(new CookingProductDecorator(cookingEnd, product));
			System.out.println(String.format("OVEN: Added product [%s] to oven [%s] - cooktime: [%d] - current space left [%f]",
					product.getName(), this.name, product.cookTime().toMillis(), this.size()));

		} else {
			System.out.println(String.format("OVEN: Not enough space [%f] for product [%s]-[%f]", this.size.get(), product.getName(), product.size()));
			throw new CapacityExceededException();
		}
	}

	@Override
	public void take(Product product) { //no return ??
		cookingProducts.removeIf(p -> p.getProduct().equals(product));
	}

	@Override
	public void turnOn() {
		this.isActive = new AtomicBoolean(true);
	}

	@Override
	public void turnOn(Duration duration) {
		//TODO implement
	}

	@Override
	public synchronized  List<Product> getCookedProducts() {
		List<Product> result = new ArrayList<>();
		List<CookingProductDecorator> cProducts = this.cookingProducts.parallelStream().filter(p -> p.getCookingEnd() <= System.currentTimeMillis()).collect(Collectors.toList());

		double spaceTofree = cProducts.stream().mapToDouble(p -> p.getProduct().size()).sum();

		if (spaceTofree > 0) {
			this.size = new AtomicReference<>(this.size.get() + spaceTofree);
			this.cookingProducts.removeAll(cProducts);
			result = cProducts.stream().map(CookingProductDecorator::getProduct).collect(Collectors.toList());
			result.forEach(p -> p.setStatus(ProductStatus.COOKED));
			System.out.println(String.format("OVEN: Found [%d] cooked products in oven [%s]. Current oven space is [%.2f]", result.size(), this.name, this.size()));
		}
		return result;
	}

	/**
	 * Decorator class for a {@link Product} being coocked
	 */
	@Data
	private class CookingProductDecorator {

		private Long cookingEnd;
		private Product product;

		public CookingProductDecorator(Long cookingEnd, Product product) {
			this.cookingEnd = cookingEnd;
			this.product = product;
		}

	}
}
