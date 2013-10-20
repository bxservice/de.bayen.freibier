package de.bayen.freibier.plugin;

import org.adempiere.plugin.utils.AdempiereActivator;

/**
 * @author tbayen
 */
public class FreiBierActivator extends AdempiereActivator {

	public FreiBierActivator() {
	}

	@Override
	protected void start() {
		System.out.println("FreiBier plugin started.");
		super.start();
	}

	@Override
	protected void stop() {
		super.stop();
		System.out.println("FreiBier plugin stopped.");
	}

}
