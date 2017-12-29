package de.bayen.freibier.process;

import java.math.BigDecimal;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;

import de.bayen.freibier.util.OrderDotMatrixFormat;

public class OrderPrintDotMatrix extends SvrProcess {

	private int orderID = -1;

	@Override
	protected void prepare() {
		for (ProcessInfoParameter para : getParameter()) {
			String name = para.getParameterName();
			if (name.equals("C_Order_ID"))
				orderID = ((BigDecimal)para.getParameter()).intValue();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		if (orderID <= 0) {
			orderID  = getRecord_ID();
		}
	}

	@Override
	protected String doIt() throws Exception {
		if (orderID <= 0) {
			throw new AdempiereException("@Mandatory@ @C_Order_ID@");
		}
		String fileName = new OrderDotMatrixFormat().print(getCtx(), orderID, get_TrxName());
		return "@File@ @Generated@ " + fileName;
	}

}
