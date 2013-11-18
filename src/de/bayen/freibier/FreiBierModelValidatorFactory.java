package de.bayen.freibier;

import org.adempiere.base.IModelValidatorFactory;
import org.compiere.model.ModelValidator;

import de.bayen.freibier.model.ProductValidator;

/**
 * Dieses Interface wird beim Login eines Users zweimal aufgerufen. Einmal mit
 * einer Liste von Klassen, die im AD_Client-Datensatz definiert sind und einmal
 * mit einer Liste von Klassen, die im AD_ModelValidator Fenster definiert sind.
 * Diese meine Klasse für FreiBier habe ich in der Tabelle AD_ModelValidator
 * eingerichtet.
 * 
 * @author tbayen
 */
public class FreiBierModelValidatorFactory implements IModelValidatorFactory {

	@Override
	public ModelValidator newModelValidatorInstance(String className) {
		System.out.println("FreiBierModelValidatorFactory: " + className);
		if (ProductValidator.class.getName().equals(className))
			return new ProductValidator();
		return null;
	}

}
