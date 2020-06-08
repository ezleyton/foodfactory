package test.foodfactory.orchrestation;

import jdk.jshell.spi.ExecutionControl;
import test.foodfactory.domain.impl.AssemblyLineStart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Assembly line orchestrator thread.
 * This thread holds 1..n {@link AssemblyLineStart} acting as subthreads.
 */
public class AssemblyLineOrchestrator extends Thread {

	List<AssemblyLineStart> assemblyLines;
	OvenOrchestrator ovenOrchestrator;
	StorageOrchestrator storageOrchestrator;

	/**
	 * Constructor for a single associated assembly line
	 * @param assemblyLine the {@link AssemblyLineStart} for this orchestrator
	 * @param ovenOrchestrator a reference to the associated {@link OvenOrchestrator}
	 * @param storageOrchestrator a reference to the associated {@link StorageOrchestrator}
	 */
	public AssemblyLineOrchestrator(AssemblyLineStart assemblyLine, OvenOrchestrator ovenOrchestrator, StorageOrchestrator storageOrchestrator) {
		this.assemblyLines = Collections.synchronizedList(new ArrayList<>());
		this.assemblyLines.add(assemblyLine);
		this.storageOrchestrator = storageOrchestrator;
		this.ovenOrchestrator = ovenOrchestrator;
	}

	/**
	 * Constructor for a list of associated assembly lines
	 * @param assemblyStartLines a list of {@link AssemblyLineStart} for this orchestrator
	 * @param ovenOrchestrator a reference to the associated {@link OvenOrchestrator}
	 * @param storageOrchestrator a reference to the associated {@link StorageOrchestrator}
	 */
	public AssemblyLineOrchestrator(List<AssemblyLineStart> assemblyStartLines, OvenOrchestrator ovenOrchestrator, StorageOrchestrator storageOrchestrator) {
		this.assemblyLines = Collections.synchronizedList(new ArrayList<>());
		this.storageOrchestrator = storageOrchestrator;
		this.ovenOrchestrator = ovenOrchestrator;
		this.assemblyLines.addAll(assemblyStartLines);
	}

	@Override
	public void run() {
		System.out.println("Starting assembly lines");

		assemblyLines.forEach(as -> {
			as.setOvenOrchestrator(this.ovenOrchestrator);
			as.setStorageOrchestrator(this.storageOrchestrator);
			as.start();
		});

		System.out.println("assembly lines started");
	}

	//TODO: implement
	public synchronized void pauseAll() throws ExecutionControl.NotImplementedException {
		throw new ExecutionControl.NotImplementedException("iou");
	}

	//TODO: implement
	public synchronized void resumeAll() throws ExecutionControl.NotImplementedException {
		throw new ExecutionControl.NotImplementedException("iou");
	}

	public void addAssemblyLine(List<AssemblyLineStart> assemblyLines) {
		this.assemblyLines.addAll(assemblyLines);
	}
	public void addAssemblyLine(AssemblyLineStart assemblyLine) {
		assemblyLines.add(assemblyLine);
	}
}
