package de.bayen.bx.util;

import java.sql.ResultSet;
import java.util.Properties;

import org.adempiere.base.IBankStatementLoaderFactory;
import org.adempiere.base.IModelFactory;
import org.adempiere.base.IModelValidatorFactory;
import org.adempiere.base.IProcessFactory;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.impexp.BankStatementLoaderInterface;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.process.ProcessCall;
import org.compiere.util.Env;

/**
 * This class implements some different OSGi Factories. It gives access to all
 * classes that implement the needed Factory interface in the configured
 * packages of my own bundle.
 * 
 * @author tbayen
 */
abstract public class AbstractServiceFactory implements IProcessFactory,
		IBankStatementLoaderFactory, IModelFactory, IModelValidatorFactory {

	protected String[] processPackages = null;

	/**
	 * You should to overload this constructor and you should define your own packages.
	 */
	protected AbstractServiceFactory() {
		processPackages = new String[] {
				getClass().getPackage().getName(),
				getClass().getPackage().getName() + ".process." 
			};
	}

	// IProcessFactory
	
	/**
	 * implements {@link IProcessFactory#newProcessInstance(String)}
	 */
	@Override
	public ProcessCall newProcessInstance(String className) {
		return searchClass(ProcessCall.class, className);
	}

	// PBankStatementLoaderFactory
	
	@Override
	public BankStatementLoaderInterface newBankStatementLoaderInstance(
			String className) {
		return searchClass(BankStatementLoaderInterface.class, className);
	}

	// IModelValidatorFactory
	
	@Override
	public ModelValidator newModelValidatorInstance(String className) {
		return searchClass(ModelValidator.class, className);
	}

	// IModelFactory
	
	/**
	 * This method has to be overloaded if I want to use this abstract base
	 * class as a IModelFactory.
	 * 
	 * @param tableName
	 * @return
	 */
	@Override
	public Class<?> getClass(String tableName) {
		return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {
		Class<?> clazz = getClass(tableName);
		if (clazz==null)
			return null;
		try {
			return (PO) clazz
					.getConstructor(new Class[]{Properties.class, int.class, String.class})
					.newInstance(Env.getCtx(), Record_ID, trxName);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {
		Class<?> clazz = getClass(tableName);
		if (clazz==null)
			return null;
		try {
			return (PO) clazz
					.getConstructor(new Class[]{Properties.class, ResultSet.class, String.class})
					.newInstance(Env.getCtx(), rs, trxName);
		} catch (Exception e) {
			return null;
		}
	}

	// methods used by all factory methods
	
	@SuppressWarnings("unchecked")
	protected <T> T searchClass(Class<T> iface, String className){
		if (className == null)
			return null;

		for (String packageName : processPackages) {
			if (className.startsWith(packageName)) {
				ClassLoader cl = getClass().getClassLoader();
				try {
					Class<?> clazz = cl.loadClass(className);
					if(clazz==null)
						continue;
					if (iface.isAssignableFrom(clazz)) {
						return (T) clazz.newInstance();
					}
				} catch (Exception e) {
					throw new AdempiereException(e);
				}
			}
		}
		return null;
	}
	
}
