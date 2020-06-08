package test.foodfactory.orchrestation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import test.foodfactory.domain.Oven;
import test.foodfactory.domain.Product;
import test.foodfactory.exceptions.CapacityExceededException;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


@Data
@EqualsAndHashCode(callSuper = true)
public class OvenOrchestrator extends Thread {


	private List<Oven> ovens;
	private AtomicBoolean keepAlive;
	private static final long pollInterval = 50L;

	/**
	 * Constructor
	 * @param ovens the list of associated {@link Oven}s
	 */
	public OvenOrchestrator(List<Oven> ovens) {
		this.ovens = ovens;
		this.keepAlive = new AtomicBoolean(true);
	}


	@Override
	public synchronized void start() {
		System.out.println("Starting oven orchestrator");
		this.setName("Oven orchestrator");
		super.start();
	}

	@Override
	public void run() {
		super.run();

		while (this.keepAlive.get()) {
				for (Oven oven : ovens) { //should move to a flag based check to avoid overhead
					List<Product> cookedProducts = oven.getCookedProducts();
					if (!cookedProducts.isEmpty()) {
						cookedProducts.forEach(p -> {
							p.getAssociatedLine().putAfter(p);
						});
					}
				}
		}
	}

	public void shutdown() {
		this.keepAlive = new AtomicBoolean(false);
	}

	public void store(Product product) throws CapacityExceededException {

		for (Oven oven : ovens) {
			if (oven.size() >= product.size()) {
				oven.put(product);
				return;
			}
		}
		throw new CapacityExceededException();
	}
}
