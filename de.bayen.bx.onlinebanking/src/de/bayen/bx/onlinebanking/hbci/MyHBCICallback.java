/******************************************************************************
 * Copyright (C) 2014 Thomas Bayen                                            *
 * Copyright (C) 2014 Jakob Bayen KG             							  *
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
package de.bayen.bx.onlinebanking.hbci;

import java.util.Date;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.util.Callback;
import org.adempiere.util.IProcessUI;
import org.compiere.model.MBankStatementLoader;
import org.compiere.util.Util;
import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.callback.AbstractHBCICallback;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.status.HBCIMsgStatus;

/**
 * This class gives answers to questions from the HBCI subsystem. HBCI
 * communicates with the bank in an own communication thread. It works with a
 * callback mechanism to answer questions for the bank server, hbci parameters,
 * passwords, etc. This class works as the callback. It takes answers to the
 * questions of the HBCI subsystem from the database in table
 * M_BankStatementLoader and - if needed - also via a GUI from the user.
 * 
 * @author tbayen
 */
public class MyHBCICallback extends AbstractHBCICallback {

	private StringBuilder log=new StringBuilder();
	
	private MBankStatementLoader m_loader;
	private IProcessUI processUI=null;

	private boolean blocked;

	public MyHBCICallback(MBankStatementLoader loader) {
		this.m_loader = loader;
	}

	/**
	 * This constructor is connected with a gui and can ask questions to the
	 * user if the answers are not in the database (e.g. a password/pin).
	 * 
	 * @param loader
	 * @param processUI
	 */
	public MyHBCICallback(MBankStatementLoader loader, IProcessUI processUI) {
		this.m_loader = loader;
		this.processUI=processUI;
	}


	@Override
	public synchronized void status(HBCIPassport passport, int statusTag, Object[] o) {
		switch (statusTag) {
		case STATUS_INST_BPD_INIT:
			toLog(HBCIUtilsInternal.getLocMsg("STATUS_REC_INST_DATA"));
			break;
		case STATUS_INST_BPD_INIT_DONE:
			toLog(HBCIUtilsInternal.getLocMsg("STATUS_REC_INST_DATA_DONE", passport.getBPDVersion()));
			break;
		case STATUS_INST_GET_KEYS:
			toLog(HBCIUtilsInternal.getLocMsg("STATUS_REC_INST_KEYS"));
			break;
		case STATUS_INST_GET_KEYS_DONE:
			toLog(HBCIUtilsInternal.getLocMsg("STATUS_REC_INST_KEYS_DONE"));
			break;
		case STATUS_SEND_KEYS:
			toLog(HBCIUtilsInternal.getLocMsg("STATUS_SEND_MY_KEYS"));
			break;
		case STATUS_SEND_KEYS_DONE:
			toLog(HBCIUtilsInternal.getLocMsg("STATUS_SEND_MY_KEYS_DONE"));
			toLog("status: " + ((HBCIMsgStatus) o[0]).toString());
			break;
		case STATUS_INIT_SYSID:
			toLog(HBCIUtilsInternal.getLocMsg("STATUS_REC_SYSID"));
			break;
		case STATUS_INIT_SYSID_DONE:
			toLog(HBCIUtilsInternal.getLocMsg("STATUS_REC_SYSID_DONE", o[1].toString()));
			toLog("status: " + ((HBCIMsgStatus) o[0]).toString());
			break;
		case STATUS_INIT_SIGID:
			toLog(HBCIUtilsInternal.getLocMsg("STATUS_REC_SIGID"));
			break;
		case STATUS_INIT_SIGID_DONE:
			toLog(HBCIUtilsInternal.getLocMsg("STATUS_REC_SIGID_DONE", o[1].toString()));
			toLog("status: " + ((HBCIMsgStatus) o[0]).toString());
			break;
		case STATUS_INIT_UPD:
			toLog(HBCIUtilsInternal.getLocMsg("STATUS_REC_USER_DATA"));
			break;
		case STATUS_INIT_UPD_DONE:
			toLog(HBCIUtilsInternal.getLocMsg("STATUS_REC_USER_DATA_DONE", passport.getUPDVersion()));
			break;
		case STATUS_LOCK_KEYS:
			toLog(HBCIUtilsInternal.getLocMsg("STATUS_USR_LOCK"));
			break;
		case STATUS_LOCK_KEYS_DONE:
			toLog(HBCIUtilsInternal.getLocMsg("STATUS_USR_LOCK_DONE"));
			toLog("status: " + ((HBCIMsgStatus) o[0]).toString());
			break;
		case STATUS_DIALOG_INIT:
			toLog(HBCIUtilsInternal.getLocMsg("STATUS_DIALOG_INIT"));
			break;
		case STATUS_DIALOG_INIT_DONE:
			toLog(HBCIUtilsInternal.getLocMsg("STATUS_DIALOG_INIT_DONE", o[1]));
			toLog("status: " + ((HBCIMsgStatus) o[0]).toString());
			break;
		case STATUS_SEND_TASK:
			toLog(HBCIUtilsInternal.getLocMsg("STATUS_DIALOG_NEW_JOB", ((HBCIJob) o[0]).getName()));
			break;
		case STATUS_SEND_TASK_DONE:
			toLog(HBCIUtilsInternal.getLocMsg("STATUS_DIALOG_JOB_DONE", ((HBCIJob) o[0]).getName()));
			break;
		case STATUS_DIALOG_END:
			toLog(HBCIUtilsInternal.getLocMsg("STATUS_DIALOG_END"));
			break;
		case STATUS_DIALOG_END_DONE:
			toLog(HBCIUtilsInternal.getLocMsg("STATUS_DIALOG_END_DONE"));
			toLog("status: " + ((HBCIMsgStatus) o[0]).toString());
			break;
		case STATUS_MSG_CREATE:
			toLog("  " + HBCIUtilsInternal.getLocMsg("STATUS_MSG_CREATE", o[0].toString()));
			break;
		case STATUS_MSG_SIGN:
			toLog("  " + HBCIUtilsInternal.getLocMsg("STATUS_MSG_SIGN"));
			break;
		case STATUS_MSG_CRYPT:
			toLog("  " + HBCIUtilsInternal.getLocMsg("STATUS_MSG_CRYPT"));
			break;
		case STATUS_MSG_SEND:
			toLog("  " + HBCIUtilsInternal.getLocMsg("STATUS_MSG_SEND"));
			break;
		case STATUS_MSG_RECV:
			toLog("  " + HBCIUtilsInternal.getLocMsg("STATUS_MSG_RECV"));
			break;
		case STATUS_MSG_PARSE:
			toLog("  " + HBCIUtilsInternal.getLocMsg("STATUS_MSG_PARSE", o[0].toString() + ")"));
			break;
		case STATUS_MSG_DECRYPT:
			toLog("  " + HBCIUtilsInternal.getLocMsg("STATUS_MSG_DECRYPT"));
			break;
		case STATUS_MSG_VERIFY:
			toLog("  " + HBCIUtilsInternal.getLocMsg("STATUS_MSG_VERIFY"));
			break;
		case STATUS_SEND_INFOPOINT_DATA:
			toLog(HBCIUtilsInternal.getLocMsg("STATUS_SEND_INFOPOINT_DATA"));
			break;

		case STATUS_MSG_RAW_SEND:
			toLog(HBCIUtilsInternal.getLocMsg("STATUS_MSG_RAW_SEND" /*, o[0].toString()*/));
			break;

		case STATUS_MSG_RAW_RECV:
			toLog(HBCIUtilsInternal.getLocMsg("STATUS_MSG_RAW_RECV" /*, o[0].toString()*/));
			break;

		default:
			throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("STATUS_INVALID", Integer.toString(statusTag)));
		}
	}

	@Override
	public void callback(HBCIPassport passport, int reason, String msg, int dataType, StringBuffer retData) {
		// System.out.println("Callback für folgendes Passport: " +
		// passport.getClientData("init").toString() + " --> ["
		// + reason + "] " + msg);
		switch (reason) {
		case HBCICallback.NEED_CONNECTION:  // 24
		case HBCICallback.CLOSE_CONNECTION:  // 25
			break;
		case HBCICallback.HAVE_INST_MSG:
			// TODO das hier irgendwie speichern und evtl. anzeigen oder so
			toLog(msg);
			break;
		case HBCICallback.NEED_COUNTRY:  // 7
			// "DE" ist bereits vorbelegt
			break;
		case HBCICallback.NEED_BLZ:  // 8
			retData.append(m_loader.getC_BankAccount().getC_Bank().getRoutingNo());
			break;
		case HBCICallback.NEED_HOST:  // 9
			String hostAddress = m_loader.getHostAddress();
			if (!Util.isEmpty(hostAddress, true)) {
				retData.delete(0, retData.length()); // ist vorbelegt
				retData.append(hostAddress);
			}
			break;
		case HBCICallback.NEED_PORT:  // 10
			int hostPort = m_loader.getHostPort();
			if (hostPort > 0) {
				retData.delete(0, retData.length()); // "443" ist vorbelegt
				retData.append(hostPort);
			}
			break;
		case HBCICallback.NEED_FILTER:  // 26
			/*
			 * "Base64" ist hier bereits voreingestellt. Gibt laut
			 * HBCI4Java-Doku auch "None", ist mir aber noch nicht
			 * untergekommen, weshalb ich noch keine Einstellungsmöglichkeit
			 * hierfür geschaffen habe.
			 */
			break;
		case HBCICallback.NEED_USERID:  // 11
		{
			String userid = m_loader.getUserID();
			if (userid != null && userid.indexOf(';') > -1)
				userid = userid.substring(0, userid.indexOf(';'));
			retData.append(userid);
			break;
		}
		case HBCICallback.NEED_CUSTOMERID:  // 18
		{
			String userid = m_loader.getUserID();
			if (userid != null && userid.indexOf(';') > -1)
				userid = userid.substring(userid.indexOf(';') + 1);
			retData.delete(0, retData.length()); // USERID ist vorbelegt
			retData.append(userid);
			break;
		}
		case HBCICallback.NEED_PASSPHRASE_LOAD:  // 21
		case HBCICallback.NEED_PASSPHRASE_SAVE:  // 22
			String pw=m_loader.getPassword();
			if(pw==null)
				pw="iDempiere";
			retData.append(pw);
			break;
		case HBCICallback.NEED_PT_PIN:
		{
			String pin=m_loader.getPIN();
			final StringBuffer resultstring=new StringBuffer();
			blocked=true;
			if(pin==null){
				if(processUI==null)
					throw new AdempiereException(
							"no GUI for this HBCI process, but there is a question to the user: " + msg);
				processUI.askForInput(msg, new Callback<String>() {
					@Override
					public void onCallback(String result) {
						resultstring.append(result);
						blocked=false;
					}
				});
				//wait for answer		
				int timeout=2*60*5;  // 2 Minuten
				while (blocked) {
					try {
						Thread.sleep(200);
						if(timeout-- < 0)
							throw new AdempiereException("timeout");
					} catch (InterruptedException e) {}
				}
				retData.append(resultstring);
			}else
				retData.append(pin);
			break;
		}
		case HBCICallback.NEED_PT_SECMECH:  // 27
			/*
			 * Hier wird eine Auswahl an PinTan-Verfahren übergeben, aus denen
			 * der Benutzer eins aussuchen soll. Ich kürze das Verfahren hier
			 * etwas ab, lasse das nicht den Benutzer entscheiden und lege es
			 * einfach auf "chipTAN manuell" fest, wenn das geht.
			 * 
			 * //
			 * "999:Einschritt-Verfahren|900:iTAN|910:chipTAN manuell|911:chipTAN optisch|920:smsTAN|921:pushTAN"
			 */
			if (retData.indexOf("910:chipTAN manuell") != -1) {  // Sparkasse Krefeld
				retData.delete(0, retData.length());
				retData.append("910");
			} else if (retData.indexOf("932:SmartTAN plus") != -1) {  // Volksbank Krefeld
				retData.delete(0, retData.length());
				retData.append("932");
			} else {
				throw new AdempiereException(retData.toString());
			}
			break;
		case HBCICallback.NEED_PT_TAN: // 17
		{
			final StringBuffer resultstring=new StringBuffer();
			blocked=true;
			if(processUI==null)
				throw new AdempiereException(
						"no GUI for this HBCI process, but there is a question to the user: " + msg);
			processUI.askForInput(msg, new Callback<String>() {
				@Override
				public void onCallback(String result) {
					resultstring.append(result);
					blocked=false;
				}
			});
			//wait for answer		
			int timeout=2*60*5;  // 2 Minuten
			while (blocked) {
				try {
					Thread.sleep(200);
					if(timeout-- < 0)
						throw new AdempiereException("timeout");
				} catch (InterruptedException e) {}
			}
			retData.append(resultstring);
			break;
		}
		default:
			throw new AdempiereException("unimplemented HBCI callback " + reason + ": " + msg + " - Data: "+retData.toString());
		}
	}

	private void toLog(String text) {
		if(processUI!=null){
			processUI.statusUpdate(text);
		}
		System.out.println(text);
		log.append(text).append('\n');
	}
	
	public String getLog(){
		return log.toString();
	}
	
	public void cleanLog(){
		log=new StringBuilder();
	}

	@Override
	public void log(String msg, int level, Date date, StackTraceElement trace) {
		toLog(msg);
		System.out.println(msg);
	}
}
