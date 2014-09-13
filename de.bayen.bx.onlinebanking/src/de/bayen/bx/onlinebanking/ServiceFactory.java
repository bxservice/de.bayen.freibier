package de.bayen.bx.onlinebanking;

import de.bayen.bx.onlinebanking.hbci.MT940Import;
import de.bayen.bx.onlinebanking.model.X_BAY_MT940;
import de.bayen.bx.onlinebanking.process.SaveMatcherInfo;
import de.bayen.bx.util.AbstractServiceFactory;

public class ServiceFactory extends AbstractServiceFactory {

	public ServiceFactory() {
		processPackages = new String[] {
				MT940Import.class.getPackage().getName(),
				SaveMatcherInfo.class.getPackage().getName()
				};
	}

	@Override
	public Class<?> getClass(String tableName) {
		if (X_BAY_MT940.Table_Name.equals(tableName))
			return X_BAY_MT940.class;
		else
			return null;
	}
}
