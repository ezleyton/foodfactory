package test.foodfactory;

import test.foodfactory.domain.Oven;
import test.foodfactory.domain.Store;
import test.foodfactory.domain.impl.AssemblyLineStart;
import test.foodfactory.domain.impl.GasOven;
import test.foodfactory.domain.impl.GenericStore;
import test.foodfactory.domain.impl.ProductFactory;
import test.foodfactory.orchrestation.AssemblyLineOrchestrator;
import test.foodfactory.orchrestation.OvenOrchestrator;
import test.foodfactory.orchrestation.StorageOrchestrator;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

public class Main {

	public static Boolean quit = Boolean.FALSE;

	public static void main(String[] args) {


		System.out.println("starting food factory");
		// OBSERVATION: my original idea was to make the factory layout configurable from the command line but I ran out of time, ideally I'd offer a default layout
		// or offer the user an interactive menu to set up the application.
		// Anyway, the solution can handle more elements (stores, lines and ovens) if they are configured and added here

		// OBSERVATION: simulator product factory setup, these creates generic products each N seconds to simulate new products into a line
		// needs to be injencted into a line
		ProductFactory pizzaFactory = new ProductFactory("pizza", 25d, Duration.ofSeconds(15));
		ProductFactory pastaFactory = new ProductFactory("pasta", 10d, Duration.ofSeconds(20));

		//assembly line setup
		AssemblyLineStart assemblyLinePizza = new AssemblyLineStart("pizza_line",
				TimeUnit.SECONDS.toMillis(1),null,null, TimeUnit.SECONDS.toMillis(2), pizzaFactory);

		AssemblyLineStart assemblyLinePasta = new AssemblyLineStart("pasta_line",
				TimeUnit.SECONDS.toMillis(1),null,null, TimeUnit.SECONDS.toMillis(10), pastaFactory);


		List<AssemblyLineStart> assemblyLines = Arrays.asList(assemblyLinePasta, assemblyLinePizza);


		//ovens configuration
		List<Oven> ovens = new ArrayList<>();
		ovens.add(new GasOven(50d, "oven A"));
		ovens.add(new GasOven(25d, "oven B"));
		OvenOrchestrator ovenOrchestrator = new OvenOrchestrator(ovens);


		List<Store> stores = new ArrayList<>();
		stores.add(new GenericStore(25d, "Small storage A"));
		StorageOrchestrator storageOrchestrator = new StorageOrchestrator(ovenOrchestrator, null, stores); //circular dependency here


		AssemblyLineOrchestrator assemblyLineOrchestrator = new AssemblyLineOrchestrator(assemblyLines, ovenOrchestrator, storageOrchestrator);
		storageOrchestrator.setLineOrchestrator(assemblyLineOrchestrator);

		storageOrchestrator.start();
		ovenOrchestrator.start();
		assemblyLineOrchestrator.start();

		Scanner scanner = new Scanner(System.in);
		boolean exit = Boolean.FALSE;

		//System.out.println("type help for command list");
		while (!exit) {
			String command = (scanner.nextLine());
			if (command.equalsIgnoreCase("help")) {
				System.out.println("ran out of time sorry :)");
			}
		}

	}
}
