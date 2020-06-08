package test.foodfactory.domain.impl;

import lombok.Data;
import test.foodfactory.domain.Product;
import test.foodfactory.domain.Store;
import test.foodfactory.exceptions.CapacityExceededException;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A generic store implementation of {@link Store}
 */
@Data
public class GenericStore implements Store {

	private AtomicReference<Double> availableSize;
	private ConcurrentLinkedQueue<Product> products;
	private String name;

	/**
	 * Constructor
	 * @param availableSize the size of the store
	 * @param name it's name
	 */
	public GenericStore(Double availableSize, String name) {
		this.availableSize = new AtomicReference<>(availableSize);
		products = new ConcurrentLinkedQueue<>();
		this.name = name;
	}


	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public synchronized void put(Product product) throws CapacityExceededException {

		if (this.availableSize.get() >= product.size()) {
			this.availableSize.updateAndGet(aDouble -> aDouble -= product.size());
			product.setAssociatedStore(this);
			this.products.add(product);
			System.out.printf("STORAGE: store [%s] received product [%s] with status [%s] - storage space left [%.2f]%n",
					this.name, product.getName(), product.getStatus().name(), this.availableSize.get());
			return;
		}
		throw new CapacityExceededException();
	}

	@Override
	public synchronized Product take() {
		Product product = products.poll();
		this.availableSize.getAndUpdate(aDouble -> aDouble += product.size());
		System.out.printf("STORAGE: store [%s] is returning product [%s] with status [%s] - storage space left [%.2f]%n",
				this.name, product.getName(), product.getStatus().name(), this.availableSize.get());
		return product;
	}

	@Override
	public synchronized void take(Product product) { //i'm assuming that returning void means to remove the product from the list
		this.availableSize.getAndUpdate(aDouble -> aDouble += product.size());
		System.out.printf("STORAGE: store [%s] is removing product [%s] - storage space left [%.2f]%n",
				this.name, product.getName(), this.availableSize.get());
		this.products.remove(product);
	}

	@Override
	public synchronized Product peek() {
		return this.products.peek();
	}
}
