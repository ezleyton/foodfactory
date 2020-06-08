package test.foodfactory.domain.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import test.foodfactory.domain.AssemblyLineStage;
import test.foodfactory.domain.Product;
import test.foodfactory.exceptions.CapacityExceededException;
import test.foodfactory.orchrestation.OvenOrchestrator;
import test.foodfactory.orchrestation.StorageOrchestrator;
import test.foodfactory.orchrestation.TimeHelper;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Assembly line head, this stage is the one creating new {@link Product}(s).
 * It has an associated {@link AssemblyLineEnd} to store finished products
 */
@SuppressWarnings("BusyWait")
@Data
@EqualsAndHashCode(callSuper = true)
public class AssemblyLineStart extends Thread implements AssemblyLineStage {

	private String lineName;
	private AtomicBoolean isActive;
	private AtomicBoolean end = new AtomicBoolean(false);
	private ConcurrentLinkedQueue<Product> productLine = new ConcurrentLinkedQueue<>(); //thread-safe implementation of a FIFO queue
	private ProductFactory factory;
	private Integer initialProducts;

	private OvenOrchestrator ovenOrchestrator;
	private StorageOrchestrator storageOrchestrator;
	private AssemblyLineEnd assemblyLineEnd; //reference to the "continuation" of this assembly line

	//SIMULATION PART
	//this value is used to simulate the process of readying a product once it's in the line (fifo queue), each X amount of seconds the process will ready the next product in line
	private Long preparationInterval;

	//this value is used to simulate the production rate of the assembly line (ie: if 5 seconds, every 5 seconds a new product with status PRODUCING will be added to the fifo queue).
	private Long productionRate;


	private ScheduledExecutorService generatorScheduler;

	/**
	 * Constructor
	 * @param lineName the name of the line
	 * @param preparationInterval the amount of time (millis) it takes to create a READY_TO_COOK product. (used for simulation)
	 * @param ovenOrchestrator the associated {@link OvenOrchestrator}
	 * @param storageOrchestrator the associated {@link StorageOrchestrator}
	 * @param productionRate the amout of time (millis) it takes to produce new products. (used for simulation)
	 * @param factory the {@link ProductFactory} for this line. (used for simulation).
	 */
	public AssemblyLineStart(String lineName, Long preparationInterval, OvenOrchestrator ovenOrchestrator, StorageOrchestrator storageOrchestrator, Long productionRate, ProductFactory factory) {
		this.lineName = lineName;
		this.preparationInterval = preparationInterval;
		this.ovenOrchestrator = ovenOrchestrator;
		this.storageOrchestrator = storageOrchestrator;
		this.productionRate = productionRate;
		this.factory = factory;
	}

	@Override
	public synchronized void start() {
		this.isActive = new AtomicBoolean(true);
		this.setName(lineName + "_thread");
		System.out.println(String.format("Assembly line [%s] running", this.lineName));

		this.assemblyLineEnd = new AssemblyLineEnd(lineName + "_end");

		super.start();
	}

	@Override
	public void run() {
		//this starts a scheduler to create new products each <productionRate> seconds, to simulate new products in the line
		TimedProductInjector productGenerator = new TimedProductInjector(this, factory);
		System.out.printf("Simulator for [%s] warming up%n", this.lineName);
		this.generatorScheduler = Executors.newScheduledThreadPool(1);
		this.generatorScheduler.scheduleAtFixedRate(productGenerator, 5, TimeUnit.MILLISECONDS.toSeconds(productionRate), TimeUnit.SECONDS);

		//in the following lines we simulate the READYing of a product
		long nextInterval = System.currentTimeMillis() + this.preparationInterval;
		long sleepInterval = 0L;
		while (!end.get()) {

			if (!isActive.get()) {
				if (sleepInterval < System.currentTimeMillis()) {
					this.isActive.set(true);
				} else {
					continue;
				}
			}

			//if the next interval is less than the current system milis, we update the next product in line and recalculate the following cycle timestamp
			if (nextInterval < System.currentTimeMillis()) {
				//TODO: implement dynamic assignment
				synchronized (productLine) {
					if (productLine.peek() != null) {
						productLine.peek().setStatus(ProductStatus.READY_TO_COOK);
						//System.out.printf("LINE_START: [%s] item ready to cook%n", this.lineName);
					}
				}

				nextInterval = System.currentTimeMillis() + this.preparationInterval;
			}

			//first we check if there's something waiting to be coocked
			if (productLine.peek() != null && productLine.peek().getStatus().equals(ProductStatus.READY_TO_COOK)) {
				Product p = productLine.peek();
				boolean allocated = false;
				//first we query the oven orchestrator
				try {
					ovenOrchestrator.store(p);
					allocated = true;
				} catch (CapacityExceededException e) {
					//System.out.println("no space left in ovens");
				}

				//if the product couldn't be allocated within an oven, we check if we can store it
				if (!allocated) {
					try {
						storageOrchestrator.store(p);
						allocated = true;
					} catch (CapacityExceededException e) {

						System.out.printf("LINE_START: line [%s] unable to allocate product [%s] - pausing line %n", this.lineName, p.getName());
						sleepInterval = TimeHelper.intervalFromTimestamp(TimeUnit.SECONDS.toMillis(10));
						this.isActive.set(false);
					}
				}

				//finally we check if we could allocate the product anywhere
				if (allocated) {
					productLine.remove(); //if the product was stored somewhere else (oven or store), we remove the product from this line
				}
			}
		}
	}

	@Override
	public void putAfter(Product product) {
		if (product.getStatus().equals(ProductStatus.COOKED)) {
			assemblyLineEnd.putAfter(product);
		} else {
			productLine.add(product);
		}
	}

	@Override
	public Product take() {
		return productLine.poll();
	}
}
