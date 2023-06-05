package de.bayen.freibier.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MSysConfig;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Util;

public class OrderLineSQLCreator {
	
	private static CLogger log = CLogger.getCLogger(OrderLineSQLCreator.class);
	
	private static final String SYSCONGIF_FUNCTION_NAME = "BAY_ORDERLINES_SQL";
	private static final String DEFAULT_FUNCTION_NAME = "bay_additional_order_lines";
	private static final int LINE_NO = 1;
	private static final int M_PRODUCT_ID = 2;
	private static final int QTY_ENTERED = 3;
	private static final int PRICE_ENTERED = 4;
	
	private MOrder order = null;
	
	public OrderLineSQLCreator(MOrder order) {
		this.order = order;
	}
	
	public void createLinesFromSQLFunction() {
		String orderLinesSQLFunction = MSysConfig.getValue(SYSCONGIF_FUNCTION_NAME, DEFAULT_FUNCTION_NAME, order.getAD_Client_ID());
		
		if (!Util.isEmpty(orderLinesSQLFunction)) {
			String sql = "SELECT * FROM " + orderLinesSQLFunction + "(?)";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = DB.prepareStatement(sql, order.get_TrxName());
				pstmt.setInt(1, order.getC_Order_ID());
				rs = pstmt.executeQuery();
				while (rs.next()) {
					createOrderLine(rs.getInt(LINE_NO), rs.getInt(M_PRODUCT_ID), rs.getBigDecimal(QTY_ENTERED), rs.getBigDecimal(PRICE_ENTERED));
				}
			} catch (Exception e) {
				log.log(Level.SEVERE, sql, e);
				throw new AdempiereException(e);
			} finally {
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
		}
	}
	
	private void createOrderLine(int lineNo, int productID, BigDecimal qtyEntered, BigDecimal priceEntered) {
		MOrderLine orderLine = new MOrderLine(order);
		orderLine.setLine(lineNo);
		orderLine.setM_Product_ID(productID);
		orderLine.setQty(qtyEntered);
		orderLine.setPrice(priceEntered);
		
		orderLine.saveEx();
	}
}
