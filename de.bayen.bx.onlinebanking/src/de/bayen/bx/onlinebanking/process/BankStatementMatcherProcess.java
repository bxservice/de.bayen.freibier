/******************************************************************************
 * Copyright (C) 2013 Thomas Bayen                                            *
 * Copyright (C) 2013 Jakob Bayen KG             							  *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package de.bayen.bx.onlinebanking.process;

import java.math.BigDecimal;
import java.util.logging.Level;

import org.compiere.impexp.BankStatementMatchInfo;
import org.compiere.impexp.BankStatementMatcherInterface;
import org.compiere.model.MBankStatement;
import org.compiere.model.MBankStatementLine;
import org.compiere.model.MBankStatementMatcher;
import org.compiere.model.PO;
import org.compiere.model.X_I_BankStatement;

import de.bayen.bx.onlinebanking.BankStatementMatchInfo2;
import de.bayen.bx.onlinebanking.Core;
import de.bayen.bx.util.AbstractRecordProcessor;

/**
 * This class is an extended version of the original
 * {@link org.compiere.process.BankStatementMatcher} from Compiere. It allows to
 * create Charge bookings and it allows to work on a single record or the whole
 * table shown in the window.
 * 
 * When you ask yourself why I did not create an own matcher system and
 * everything here is derived from the legacy matchers: This code evolved from
 * an implementation of the original interfaces. It still works in the original
 * environment. I did this in the most possible non-intrusive and compatible way
 * to make it useful for as many situations as possible. (Said that I can see no
 * real reason to use it in the old environment...)
 * 
 * You can simply use this one instead of the old one but it does only work in
 * one kind of table (Import Bank Statement). We can improve that later if
 * someone needs it.
 * 
 * @author tbayen
 */
public class BankStatementMatcherProcess extends AbstractRecordProcessor<PO> {

	private MBankStatementMatcher[] m_matchers;

	public BankStatementMatcherProcess() {
		super(X_I_BankStatement.Table_ID, MBankStatementLine.Table_ID, MBankStatement.Table_ID);
	}

	@Override
	protected void prepare() {
		super.prepare();
		m_matchers = MBankStatementMatcher.getMatchers(getCtx(), get_TrxName());
	}
	
	/**
	 * This method is a replacement for MBankStatementMatcher.getMatcher() and
	 * extended to use the OSGi service interface to find matchers. I did not
	 * want to touch trunk to do it there.
	 * 
	 * @param ctx
	 * @param trxName
	 * @return
	 */
	private static BankStatementMatcherInterface getMatcher(MBankStatementMatcher matcher) {
		String className=matcher.getClassname();
		if(className==null)
			return null;
		return Core.getBankStatementMatcher(className);
	}


	@Override
	protected String processRecord(PO record) {
		if (m_matchers == null || record == null)
			return "--";
		if (log.isLoggable(Level.FINE))
			log.fine("" + record);

		if (record instanceof X_I_BankStatement)
			return processRecordX_I_BankStatement((X_I_BankStatement) record);
		if (record instanceof MBankStatementLine)
			return processRecordMBankStatementLine((MBankStatementLine) record);
		if (record instanceof MBankStatement) {
			MBankStatementLine[] lines = ((MBankStatement) record).getLines(false);
			for (MBankStatementLine mBankStatementLine : lines) {
				processRecordMBankStatementLine(mBankStatementLine);
			}
		}
		return "";
	}

	private String processRecordMBankStatementLine(MBankStatementLine record) {
		if (record.getC_Payment_ID() != 0)
			return "";
		BankStatementMatchInfo info = null;
		for (int i = 0; i < m_matchers.length; i++) {
			BankStatementMatcherInterface matcher = getMatcher(m_matchers[i]);
			info = matcher==null ? null : matcher.findMatch(record);
			if (info != null && info.isMatched()) {
				if (info.getC_Payment_ID() > 0)
					record.setC_Payment_ID(info.getC_Payment_ID());
				if (info.getC_Invoice_ID() > 0)
					record.setC_Invoice_ID(info.getC_Invoice_ID());
				if (info.getC_BPartner_ID() > 0)
					record.setC_BPartner_ID(info.getC_BPartner_ID());
				if (info instanceof BankStatementMatchInfo2) {
					BankStatementMatchInfo2 info2 = (BankStatementMatchInfo2) info;
					if (info2.getC_Charge_ID() > 0) {
						record.setC_Charge_ID(info2.getC_Charge_ID());
						record.setChargeAmt(record.getStmtAmt());
						record.setTrxAmt(BigDecimal.ZERO);
					}
				}
				record.saveEx();
				return "OK";
			}
		} // for all matchers
		return "";
	}

	private String processRecordX_I_BankStatement(X_I_BankStatement record) {
		if (record.getC_Payment_ID() != 0)
			return "";
		BankStatementMatchInfo info = null;
		for (int i = 0; i < m_matchers.length; i++) {
			BankStatementMatcherInterface matcher = getMatcher(m_matchers[i]);
			info = matcher==null ? null : matcher.findMatch(record);
			if (info != null && info.isMatched()) {
				if (info.getC_Payment_ID() > 0)
					record.setC_Payment_ID(info.getC_Payment_ID());
				if (info.getC_Invoice_ID() > 0)
					record.setC_Invoice_ID(info.getC_Invoice_ID());
				if (info.getC_BPartner_ID() > 0)
					record.setC_BPartner_ID(info.getC_BPartner_ID());
				if (info instanceof BankStatementMatchInfo2) {
					BankStatementMatchInfo2 info2 = (BankStatementMatchInfo2) info;
					if (info2.getC_Charge_ID() > 0) {
						record.setC_Charge_ID(info2.getC_Charge_ID());
						record.setChargeAmt(record.getStmtAmt());
						record.setTrxAmt(BigDecimal.ZERO);
					}
				}
				record.saveEx();
				return "OK";
			}
		} // for all matchers
		return "";
	}
}
