package test.foodfactory.orchrestation;

import test.foodfactory.domain.Product;
import test.foodfactory.domain.Store;
import test.foodfactory.exceptions.CapacityExceededException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static test.foodfactory.orchrestation.TimeHelper.intervalFromTimestamp;

public class StorageOrchestrator extends Thread {

	private List<Store> stores;
	private OvenOrchestrator ovenOrchestrator;
	private AssemblyLineOrchestrator lineOrchestrator;
	private AtomicBoolean keepAlive;
	private static final long pollInterval = 500L;

	/**
	 * Constructor
	 * @param ovenOrchestrator a reference to the associated {@link OvenOrchestrator}
	 * @param lineOrchestrator a reference to the associated {@link AssemblyLineOrchestrator}
	 */
	public StorageOrchestrator(OvenOrchestrator ovenOrchestrator, AssemblyLineOrchestrator lineOrchestrator, List<Store> stores) {
		this.stores = Collections.synchronizedList(new ArrayList<>());
		this.stores.addAll(stores);
		this.ovenOrchestrator = ovenOrchestrator;
		this.lineOrchestrator = lineOrchestrator;
		this.keepAlive = new AtomicBoolean(true);
	}

	public void setLineOrchestrator(AssemblyLineOrchestrator lineOrchestrator) {
		this.lineOrchestrator = lineOrchestrator;
	}

	@Override
	public void run() {
		super.run();

		long nextPoll = intervalFromTimestamp(pollInterval);
		while (keepAlive.get()) {
			if (nextPoll <= System.currentTimeMillis()) {

				Product product = this.peekOne();

				if (product != null) {
					try {
						this.ovenOrchestrator.store(product);
						//if the product was accepted by an oven, the delete it from the store
						Store associated = product.getAssociatedStore();
						associated.take(product);
					} catch (CapacityExceededException e) {
						//do nothing
					}
				}

				nextPoll = intervalFromTimestamp(pollInterval);
			}
		}
	}

	public synchronized void store(Product product) throws CapacityExceededException {
		for (Store store : stores) {
			try {
				store.put(product);
				return;
			} catch (CapacityExceededException e) {
				//go on
			}
		}
		throw new CapacityExceededException();
	}

	public Product peekOne() {
		for (Store store : stores) {
			Product p = store.peek();
			if(p != null) {
				return p;
			}
		}
		return null;
	}
}
