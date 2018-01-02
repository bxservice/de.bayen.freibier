package de.bayen.freibier.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MOrder;
import org.compiere.model.MSysConfig;
import org.compiere.model.MUser;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Util;

public class OrderDotMatrixFormat {

    /* decimal ascii values for epson ESC/P commands */
    private static final char ESC = 27; //escape
    private static final char LINE_FEED = 10; //line feed/new line
    private static final char CR = 13; //carriage return
    private static final char FF = 12; //form feed
    private static final char E = 69; //bold font on
    private static final char F = 70; //bold font off

	// TODO: make configurable DB messages
    private static String Line01Leer = "01 GASTRO-FASS  ohne Pfd.";
    private static String Line02Leer = "26 PFAND-FASS   EUR 30.00";
    private static String Line03Leer = "02 PFAND-FASS   EUR 25.00";
    private static String Line04Leer = "                         ";
    private static String Line05Leer = "03 KASTEN       EUR  1.50";
    private static String Line06Leer = "                         ";
    private static String Line07Leer = "04 FLASCHEN     EUR  0.08";
    private static String Line08Leer = "05 FLASCHEN     EUR  0.10";
    private static String Line09Leer = "06 FLASCHEN     EUR  0.15";
    private static String Line10Leer = "09 FLASCHEN     EUR  0.35";
    private static String Line11Leer = "08 FLA/DOSE     EUR  0.25";
    private static String Line12Leer = "15 EUROPALETTE  EUR  7.50";
    private static String Line13Leer = "07 PAL./CONT.   EUR 10.00";

	private String DocumentTitle;

    private PageControl pageControl = new PageControl();
    private HeaderData headerData = new HeaderData();
	private List<DetailData> detailsData = new ArrayList<DetailData>();
	private int FooterLine;

	private static int TopMargin = 2;
	private static int MaxLineDetail = 60;

	public String print(Properties ctx, int orderID, String trxName) {
		MOrder order = new MOrder(ctx, orderID, trxName);
		MUser user = MUser.get(ctx);

		// define the filename
		// Order[DocumentNo]_[UserValue].txt
		String printerName = Env.getContext(ctx, "P|BAY_DotMatrixPrinter");
		if (Util.isEmpty(printerName, true)) {
			printerName = "VersandForm";
		}
		String fileName = "Order" + order.getDocumentNo() + "_" + user.getValue() + "_" + printerName + ".txt";
		String tmpFolder = MSysConfig.getValue("BAY_TMP_FOLDER", "/tmp/preparing");
		String definitiveFolder = MSysConfig.getValue("BAY_DEFINITIVE_FOLDER", "/tmp/to_print");
		try {
			Files.createDirectories(Paths.get(tmpFolder));
			Files.createDirectories(Paths.get(definitiveFolder));

			// create a file in temporary folder
			Files.deleteIfExists(Paths.get(tmpFolder, fileName));
			Path file = Files.createFile(Paths.get(tmpFolder, fileName));

			// get data
			getData(ctx, orderID, trxName, file);

			// fill the file contents
			if (MOrder.PAYMENTRULE_Cash.equals(order.getPaymentRule())) {
				fillFile(ctx, orderID, trxName, file, true);
			} else {
				fillFile(ctx, orderID, trxName, file, false);
			}

			// move the file to definitive folder
			Files.move(file, Paths.get(definitiveFolder, fileName), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new AdempiereException(e);
		}

		// return the filename
		return fileName;
	}

	private void getData(Properties ctx, int orderID, String trxName, Path file) {
		// get header data
		/* WARNING: The next query doesn't work with multiple taxes, assuming just one tax */
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = DB.prepareStatement(
					"SELECT "
					+ " rpad(substring(coalesce(h.DocumentNo,''),1,6),6,' ') AS DocumentNo,"
					+ " coalesce(h.TaxId,'') AS TaxId,"
					+ " coalesce(h.DocumentType,'') AS DocumentType,"
					+ " h.Name AS Name,"
					+ " coalesce(h.Name2,'') AS Name2,"
					+ " to_char(h.DateOrdered,'dd.mm.yy') AS DateOrdered,"
					+ " to_char(coalesce(CASE WHEN h.IsTaxIncluded='Y' THEN ROUND(h.TotalLines/COALESCE(1+t.Rate/100,1),2) ELSE h.TotalLines END,0),'99999990.00') AS TotalLines,"
					+ " to_char(coalesce(h.GrandTotal,0),'99999990.00') AS GrandTotal,"
					+ " to_char(coalesce(CASE WHEN h.IsTaxIncluded='Y' THEN h.GrandTotal-ROUND(h.TotalLines/COALESCE(1+t.Rate/100,1),2) ELSE h.GrandTotal-h.TotalLines END,0),'99999990.00') AS TotalTax,"
					+ " coalesce(h.Address1,'') AS Address1,"
					+ " coalesce(h.Address2,'') AS Address2,"
					+ " coalesce(l.Postal,'') AS Postal,"
					+ " coalesce(h.City,'') AS City,"
					+ " rpad(substring(coalesce(h.BPValue,''),1,5),5,' ') AS BPValue,"
					+ " rpad(substring(coalesce(r.Value,''),1,4),4,' ') AS DeliveryRoute,"
					+ " to_char(coalesce((SELECT SUM(lf.QtyEntered) FROM C_OrderLine lf JOIN C_UOM uf USING (C_UOM_ID) WHERE lf.C_Order_ID=o.C_Order_ID AND lf.bay_masterorderline_id IS NULL AND UOMSymbol='Faß'),0),'9999990') AS QtyFaesser,"
					+ " to_char(coalesce((SELECT SUM(lf.QtyEntered) FROM C_OrderLine lf JOIN C_UOM uf USING (C_UOM_ID) WHERE lf.C_Order_ID=o.C_Order_ID AND lf.bay_masterorderline_id IS NULL AND UOMSymbol='Ka'),0),'9999990') AS QtyKisten,"
					+ " to_char(coalesce((SELECT SUM(ROUND(lp.LineNetAmt/(CASE WHEN h.IsTaxIncluded='Y' THEN COALESCE(1+t.Rate/100,1) ELSE 1 END),2)) FROM C_OrderLine lp JOIN M_Product pp USING (M_Product_ID) WHERE lp.C_Order_ID=o.C_Order_ID AND lp.bay_masterorderline_id IS NULL AND pp.IsDeposit='N'),0),'99999990.00') AS TotalLinesLGAusgleich,"
					+ " to_char(coalesce((SELECT SUM(ROUND(lp.LineNetAmt*(CASE WHEN h.IsTaxIncluded='Y' THEN 1 ELSE 1+t.Rate/100 END),2)) FROM C_OrderLine lp JOIN M_Product pp USING (M_Product_ID) WHERE lp.C_Order_ID=o.C_Order_ID AND lp.bay_masterorderline_id IS NULL AND pp.IsDeposit='N'),0),'99999990.00') AS GrandTotalLGAusgleich,"
					+ " to_char(coalesce((SELECT SUM(ROUND(lp.LineNetAmt*(CASE WHEN h.IsTaxIncluded='Y' THEN (1-1/(1+t.Rate/100)) ELSE t.Rate/100 END),2)) FROM C_OrderLine lp JOIN M_Product pp USING (M_Product_ID) WHERE lp.C_Order_ID=o.C_Order_ID AND lp.bay_masterorderline_id IS NULL AND pp.IsDeposit='N'),0),'99999990.00') AS TotalTaxLGAusgleich,"
					+ " to_char(coalesce(h.TotalLines-(SELECT sum(lp.LineNetAmt) FROM C_OrderLine lp JOIN M_Product pp USING (M_Product_ID) WHERE lp.C_Order_ID=o.C_Order_ID AND lp.bay_masterorderline_id IS NULL AND pp.IsDeposit='N'),0),'99999990.00') AS LeerGut,"
					+ " coalesce(h.Description,'') AS Description,"
					+ " coalesce(pt.DocumentNote,'') AS PaymentTermNote,"
					+ " coalesce(d.DocumentNote,'') AS TargetDocumentTypeNote,"
					+ " to_char(coalesce(t.Rate,0),'90.0%') AS TaxRate,"
					+ " h.IsTaxIncluded AS IsTaxIncluded,"
					+ " h.C_Order_ID"
					+ " FROM C_Order_Header_V h"
					+ " JOIN C_Order o ON (h.C_Order_ID=o.C_Order_ID)"
					+ " LEFT JOIN C_Tax t ON (t.C_Tax_ID=(SELECT MAX(C_Tax_ID) FROM C_OrderTax ot WHERE ot.C_Order_ID=o.C_Order_ID AND ot.TaxAmt!=0))"
					+ " JOIN C_BPartner bp ON (o.C_BPartner_ID=bp.C_BPartner_ID)"
					+ " LEFT JOIN C_PaymentTerm pt ON (bp.C_PaymentTerm_ID=pt.C_PaymentTerm_ID)"
					+ " JOIN C_BPartner_Location bpl ON (bpl.C_BPartner_Location_ID=o.C_BPartner_Location_ID)"
					+ " JOIN C_Location l ON (bpl.C_Location_ID=l.C_Location_ID)"
					+ " LEFT JOIN C_DocType d ON (d.C_DocType_ID=o.C_DocTypeTarget_ID)"
					+ " LEFT JOIN BAY_Route r ON (o.BAY_Route_ID=r.BAY_Route_ID)"
					+ " WHERE h.C_Order_ID=?", trxName);
			pstmt.setInt(1, orderID);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				headerData.DocumentNo = rs.getString("DocumentNo");
				headerData.TaxID = rs.getString("TaxID");
				headerData.DocumentType = rs.getString("DocumentType");
				headerData.Name = rs.getString("Name");
				headerData.Name2 = rs.getString("Name2");
				headerData.DateOrdered = rs.getString("DateOrdered");
				headerData.TotalLines = rs.getString("TotalLines");
				headerData.GrandTotal = rs.getString("GrandTotal");
				headerData.TotalTax = rs.getString("TotalTax");
				headerData.Address1 = rs.getString("Address1");
				headerData.Address2 = rs.getString("Address2");
				headerData.Postal = rs.getString("Postal");
				headerData.City = rs.getString("City");
				headerData.BPValue = rs.getString("BPValue");
				headerData.DeliveryRoute = rs.getString("DeliveryRoute");
				headerData.QtyFaesser = rs.getString("QtyFaesser");
				headerData.QtyKisten = rs.getString("QtyKisten");
				headerData.TotalLinesLGAusgleich = rs.getString("TotalLinesLGAusgleich");
				headerData.GrandTotalLGAusgleich = rs.getString("GrandTotalLGAusgleich");
				headerData.TotalTaxLGAusgleich = rs.getString("TotalTaxLGAusgleich");
				headerData.LeerGut = rs.getString("LeerGut");
				headerData.Description = rs.getString("Description");
				headerData.PaymentTermNote = rs.getString("PaymentTermNote");
				headerData.TargetDocumentTypeNote = rs.getString("TargetDocumentTypeNote");
				headerData.TaxRate = rs.getString("TaxRate");
				headerData.IsTaxIncluded = rs.getString("IsTaxIncluded");
				// add newlines if needed
				headerData.Description = sanitizeCRLF(headerData.Description);
				headerData.PaymentTermNote = sanitizeCRLF(headerData.PaymentTermNote);
				headerData.TargetDocumentTypeNote = sanitizeCRLF(headerData.TargetDocumentTypeNote);
			}
		} catch (SQLException e) {
			throw new AdempiereException(e);
		} finally {
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		// get detail data
		try {
			pstmt = DB.prepareStatement("SELECT "
					+ " coalesce(d.UOMSymbol,'') AS UOMSymbol,"
					+ " CASE WHEN p.Description IS NOT NULL AND t.Name IS NOT NULL THEN rpad(substring(p.Description,1,44-length(REPLACE(t.Name,' x ','x'))),44-length(REPLACE(t.Name,' x ','x')),' ') || ' ' || REPLACE(t.Name,' x ','x') ELSE rpad(substring(coalesce(d.Name,''),1,45),45,' ') END AS Name,"
					+ " rpad(substring(coalesce(d.ProductValue,''),1,10),10,' ') AS ProductValue,"
					+ " to_char(coalesce(d.QtyOrdered,0),'99990') AS QtyOrdered,"
					+ " to_char(CASE WHEN p.IsDeposit='Y' THEN coalesce(d.LineNetAmt,0) ELSE 0 END+coalesce((SELECT sum(LineNetAmt) FROM C_OrderLine p WHERE l.c_orderline_id=p.bay_masterorderline_id),0),'99999990.00') AS Pfand,"
					+ " to_char(CASE WHEN p.IsDeposit!='Y' THEN coalesce(d.LineNetAmt,0) ELSE 0 END,'99999990.00') AS LineNetAmt,"
					+ " coalesce(d.Description,'') AS Description,"
					+ " d.C_OrderLine_ID"
					+ " FROM C_Order_LineTax_V d"
					+ " JOIN C_OrderLine l ON (d.C_OrderLine_ID=l.C_OrderLine_ID)"
					+ " LEFT JOIN M_Product p ON (l.M_Product_ID=p.M_Product_ID)"
					+ " LEFT JOIN BAY_TradingUnit t ON (t.BAY_TradingUnit_ID=p.BAY_TradingUnit_ID)"
					+ " WHERE d.C_Order_ID=? AND d.Line < 999998 AND l.bay_masterorderline_id IS NULL"
					+ " ORDER BY d.Line", trxName);
			pstmt.setInt(1, orderID);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				DetailData detailData = new DetailData();
				detailData.UOMSymbol = rs.getString("UOMSymbol");
				detailData.Name = rs.getString("Name");
				detailData.ProductValue = rs.getString("ProductValue");
				detailData.QtyOrdered = rs.getString("QtyOrdered");
				detailData.Pfand = rs.getString("Pfand");
				detailData.LineNetAmt = rs.getString("LineNetAmt");
				detailData.Description = rs.getString("Description");
				// add newlines if needed
				detailData.Description = sanitizeCRLF(detailData.Description);
				detailsData.add(detailData);
			}
		} catch (SQLException e) {
			throw new AdempiereException(e);
		} finally {
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
	}

	private String sanitizeCRLF(String str) {
		if (str.indexOf(CR) >= 0 && str.indexOf(LINE_FEED) < 0) {
			// string has CR but no LF
			str = str.replaceAll("\r", "\r\n");
		}
		if (str.indexOf(LINE_FEED) >= 0 && str.indexOf(CR) < 0) {
			// string has CR but no LF
			str = str.replaceAll("\n", "\r\n");
		}
		return str;
	}

	public static class HeaderData {
		public String DocumentNo;
		public String TaxID;
		public String DocumentType;
		public String Name;
		public String Name2;
		public String DateOrdered;
		public String TotalLines;
		public String GrandTotal;
		public String TotalTax;
		public String Address1;
		public String Address2;
		public String Postal;
		public String City;
		public String BPValue;
		public String DeliveryRoute;
		public String QtyFaesser;
		public String QtyKisten;
		public String TotalLinesLGAusgleich;
		public String GrandTotalLGAusgleich;
		public String TotalTaxLGAusgleich;
		public String LeerGut;
		public String Description;
		public String PaymentTermNote;
		public String TargetDocumentTypeNote;
		public String TaxRate;
		public String IsTaxIncluded;
	}

	public static class DetailData {
		public String UOMSymbol;
		public String Name;
		public String ProductValue;
		public String QtyOrdered;
		public String Pfand;
		public String LineNetAmt;
		public String Description;
	}

	private void fillFile(Properties ctx, int orderID, String trxName, Path file, boolean isRechnung) {
		// open file
		File fout;
		FileOutputStream fos;
		BufferedWriter bw = null;
		try {
			fout = file.toFile();
			fos = new FileOutputStream(fout);
			bw = new BufferedWriter(new OutputStreamWriter(fos));
		} catch (FileNotFoundException e) {
			throw new AdempiereException(e);
		}

		pageControl.Line = 1;
		pageControl.Page = 1;

		if (isRechnung) {
			FooterLine = 36;
		} else {
			FooterLine = 41;
		}
		if (isRechnung) {
			evaluateFooterLines(headerData.PaymentTermNote);
		}
		evaluateFooterLines(headerData.Description);
		evaluateFooterLines(headerData.TargetDocumentTypeNote);

		if (isRechnung) {
			DocumentTitle = "LIEFERSCHEIN/RECHNUNG";
		} else {
			DocumentTitle = "LIEFERSCHEIN         ";
		}

		try {

			printHeader(bw, DocumentTitle);

			lineFeed(bw);
			if (isRechnung) {
				bw.write("Art.Nr.    Menge  Artikelbezeichnung                                 Pfand         Ware");
			} else {
				bw.write("Art.Nr.    Menge  Artikelbezeichnung");
			}
			carriageReturn(bw); lineFeed(bw);
			if (isRechnung) {
				bw.write("----------+------+---------------------------------------------+-----------+-----------");
			} else {
				bw.write("----------+------+---------------------------------------------");
			}
			carriageReturn(bw); lineFeed(bw);

			// print detail rechnung
			for (DetailData detailData : detailsData) {
				int numLinesThisDetail = 1;
				if (!Util.isEmpty(detailData.Description,true)) {
					int count = countCR(detailData.Description);
					numLinesThisDetail = numLinesThisDetail + count;
				}

				verifySkipPage(bw, MaxLineDetail-numLinesThisDetail, isRechnung);
				bw.write(detailData.ProductValue);
				bw.write(detailData.QtyOrdered);
				bw.write("  ");
				bw.write(detailData.Name);
				if (isRechnung) {
					bw.write(detailData.Pfand);
					bw.write(detailData.LineNetAmt);
				}
				carriageReturn(bw); lineFeed(bw);
				if (!Util.isEmpty(detailData.Description,true)) {
					bw.write("                  ");
					bw.write(detailData.Description);
					carriageReturn(bw); lineFeed(bw);
					pageControl.Line = pageControl.Line + numLinesThisDetail - 1;
				}
			}

			final String zeroQtyAsString = "       0";
			// print group total rechnung
			if (   ! zeroQtyAsString.equals(headerData.QtyFaesser)
				|| ! zeroQtyAsString.equals(headerData.QtyKisten)) {
				verifySkipPage(bw, MaxLineDetail-1, isRechnung);
				carriageReturn(bw); lineFeed(bw);
				if (! zeroQtyAsString.equals(headerData.QtyFaesser)) {
					bw.write("F\u00e4sser:");
					bw.write(headerData.QtyFaesser);
					bw.write("     ");
				}
				if (! zeroQtyAsString.equals(headerData.QtyKisten)) {
					bw.write("Kisten:");
					bw.write(headerData.QtyKisten);
				}
				carriageReturn(bw); lineFeed(bw);
				lineFeed(bw);
			}

			// print footer rechnung
			// verify the position and skip page if not enough
			if (pageControl.Line > FooterLine) {
				// skip page - reprint header and position in footer line
				formFeed(bw);
				printHeader(bw, DocumentTitle);
			}
			if (pageControl.Line < FooterLine) {
				// fill lines until footer margin
				int start = pageControl.Line;
				for (int i = start; i < FooterLine; i++)
					lineFeed(bw);
			}
			if (isRechnung) {
				bw.write("LEERGUTR\u00dcCKGABE:        ANZAHL:                     ");
				if ("Y".equals(headerData.IsTaxIncluded)) {
					bw.write("Brutto");
					bw.write(headerData.TaxRate);
					bw.write("           ");
					bw.write(headerData.GrandTotalLGAusgleich);
				} else {
					bw.write("Ware");
					bw.write(headerData.TaxRate);
					bw.write("             ");
					bw.write(headerData.TotalLinesLGAusgleich);
				}
			} else {
				bw.write("LEERGUTR\u00dcCKGABE:        ANZAHL:");
			}
			carriageReturn(bw); lineFeed(bw);
			bw.write("----------------");
			carriageReturn(bw); lineFeed(bw);
			String anzahlMsg;
			if (isRechnung) {
				anzahlMsg = " ...... EUR ............";
			} else {
				anzahlMsg = " ...........";
			}
			bw.write(Line01Leer);
			if (isRechnung) {
				bw.write(" ......");
			} else {
				bw.write(" ...........");
			}
			if (isRechnung) {
				bw.write(".                   + Leergut              ");
				bw.write(headerData.LeerGut);
			}
			carriageReturn(bw); lineFeed(bw);
			bw.write(Line02Leer);
			bw.write(anzahlMsg);
			if (isRechnung) bw.write("   -----------------------------------");
			carriageReturn(bw); lineFeed(bw);
			bw.write(Line03Leer);
			bw.write(anzahlMsg);
			if (isRechnung) {
				if ("Y".equals(headerData.IsTaxIncluded)) {
					bw.write("   SUMME");
					bw.write(headerData.TaxRate);
					bw.write("            ");
					bw.write(headerData.GrandTotal);
				} else {
					bw.write("   ZWISCHENSUMME          ");
					bw.write(headerData.TotalLines);
				}
			}
			carriageReturn(bw); lineFeed(bw);
			bw.write(Line04Leer);
			if (isRechnung && !	"Y".equals(headerData.IsTaxIncluded)) {
				bw.write("                        ");
				bw.write("   - Leergutr\u00fcckgabe");
			}
			carriageReturn(bw); lineFeed(bw);
			bw.write(Line05Leer);
			bw.write(anzahlMsg);
			if (isRechnung) {
				bw.write("   -----------------------------------");
			}
			carriageReturn(bw); lineFeed(bw);
			bw.write(Line06Leer);
			if (isRechnung) {
				bw.write("                        ");
				if ("Y".equals(headerData.IsTaxIncluded)) {
					bw.write("   ZWISCHENSUMME          ");
					bw.write(headerData.GrandTotal);
				} else {
					bw.write("   NETTO ");
					bw.write(headerData.TaxRate);
				}
			}
			carriageReturn(bw); lineFeed(bw);
			bw.write(Line07Leer);
			bw.write(anzahlMsg);
			if (isRechnung) {
				if ("Y".equals(headerData.IsTaxIncluded)) {
					bw.write("   - Leergutr\u00fcckgabe");
				} else {
					bw.write("   + MwSt");
					bw.write(headerData.TaxRate);
				}
			} else {
				bw.write("                            Ware empfangen");
			}
			carriageReturn(bw); lineFeed(bw);
			bw.write(Line08Leer);
			bw.write(anzahlMsg);
			if (isRechnung && "Y".equals(headerData.IsTaxIncluded)) {
				bw.write("   -----------------------------------");
			}
			carriageReturn(bw); lineFeed(bw);
			bw.write(Line09Leer);
			bw.write(anzahlMsg);
			if (isRechnung) {
				if ("Y".equals(headerData.IsTaxIncluded)) {
					boldOn(bw);
					bw.write("   RECHNUNGSBETRAG");
					boldOff(bw);
				} else {
					bw.write("   -----------------------------------");
				}
			}
			carriageReturn(bw); lineFeed(bw);
			bw.write(Line10Leer);
			bw.write(anzahlMsg);
			if (isRechnung) {
				if ("Y".equals(headerData.IsTaxIncluded)) {
					bw.write("   Netto");
				} else {
					boldOn(bw);
					bw.write("   RECHNUNGSBETRAG");
					boldOff(bw);
				}
			}
			carriageReturn(bw); lineFeed(bw);
			bw.write(Line11Leer);
			bw.write(anzahlMsg);
			if (isRechnung) {
				if ("Y".equals(headerData.IsTaxIncluded)) {
					bw.write("   MwSt 19.0%                _________");
				} else {
					bw.write("                             =========");
				}
			} else {
				bw.write("                            .........................");
			}
			carriageReturn(bw); lineFeed(bw);
			bw.write(Line12Leer);
			bw.write(anzahlMsg);
			if ( ! isRechnung) {
				bw.write("                                  Unterschrift");
			}
			carriageReturn(bw); lineFeed(bw);
			bw.write(Line13Leer);
			bw.write(anzahlMsg);
			if (isRechnung) {
				bw.write("   Ware erhalten _____________________");
			}
			carriageReturn(bw); lineFeed(bw);
			if (isRechnung) {
				bw.write("                                 ----------------");
				carriageReturn(bw); lineFeed(bw);
				bw.write("00 GESAMT                        EUR ............   Betrag erhalten ___________________");
				carriageReturn(bw); lineFeed(bw);
				bw.write("Betr\u00e4ge ohne R\u00fcckgabe ");
				if ("Y".equals(headerData.IsTaxIncluded)) {
					bw.write("RB: ");
					bw.write(headerData.GrandTotal);
					bw.write("  M1:");
					bw.write(headerData.TotalTax);
				} else {
					bw.write("NET");
					bw.write(headerData.TaxRate);
					bw.write(":");
					bw.write(headerData.TotalLines);
					bw.write("  M1: ");
					bw.write(headerData.TotalTax);
					bw.write("  RB:");
					bw.write(headerData.GrandTotal);
				}
				carriageReturn(bw); lineFeed(bw);
				bw.write("Betr\u00e4ge LG-Ausgleich  ");
				if ("Y".equals(headerData.IsTaxIncluded)) {
					bw.write("RB: ");
					bw.write(headerData.GrandTotalLGAusgleich);
					bw.write("  M1:");
					bw.write(headerData.TotalTaxLGAusgleich);
				} else {
					bw.write("NET");
					bw.write(headerData.TaxRate);
					bw.write(":");
					bw.write(headerData.TotalLinesLGAusgleich);
					bw.write("  M1: ");
					bw.write(headerData.TotalTaxLGAusgleich);
					bw.write("  RB:");
					bw.write(headerData.GrandTotalLGAusgleich);
				}
				carriageReturn(bw);
			} else {
				bw.write("--");
			}
			if (isRechnung) {
				if (! Util.isEmpty(headerData.PaymentTermNote, true)) {
					lineFeed(bw);
					lineFeed(bw);
					bw.write(headerData.PaymentTermNote);
					carriageReturn(bw);
				}
			}
			if (! Util.isEmpty(headerData.Description, true)) {
				lineFeed(bw);
				lineFeed(bw);
				bw.write(headerData.Description);
				carriageReturn(bw);
			}
			if (! Util.isEmpty(headerData.TargetDocumentTypeNote, true)) {
				lineFeed(bw);
				lineFeed(bw);
				boldOn(bw);
				bw.write(headerData.TargetDocumentTypeNote);
				boldOff(bw);
				carriageReturn(bw);
			}
			formFeed(bw);

		} catch (IOException e) {
			throw new AdempiereException(e);
		} finally {
			// close file
			try {
				if (bw != null)
					bw.close();
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				throw new AdempiereException(e);
			}
		}
	}

	private void evaluateFooterLines(String str) {
		if (Util.isEmpty(str, true)) {
			FooterLine = FooterLine + 2;
		} else {
			int count = countCR(str);
			FooterLine = FooterLine - count;
		}
	}

	private void verifySkipPage(BufferedWriter bw, int i, boolean isRechnung) throws IOException {
		if (pageControl.Line > i) {
			// skip page - reprint header
			formFeed(bw);
			printHeader(bw, DocumentTitle);
			lineFeed(bw);
			if (isRechnung) {
				bw.write("Art.Nr.    Menge  Artikelbezeichnung                         Pfand            Ware");
			} else {
				bw.write("Art.Nr.    Menge  Artikelbezeichnung");
			}
			carriageReturn(bw); lineFeed(bw);
			if (isRechnung) {
				bw.write("----------+------+------------------------------+---------+--------+-----+---------");
			} else {
				bw.write("----------+------+------------------------------");
			}
			carriageReturn(bw); lineFeed(bw);
		}
	}

	// count the number of Carriage Return CR
	private int countCR(String str) {
		int count = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == CR) {
				count++;
			}
		}
		return count;
	}

	private void printHeader(BufferedWriter bw, String doc) throws IOException {
		// print header rechnung
		bw.write(ESC);
		bw.write("!C");
		carriageReturn(bw);
		lineFeed(bw);
		for (int i = 0; i < TopMargin; i++)
			lineFeed(bw);
		bw.write("      ");
		boldOn(bw);
		bw.write(headerData.Name);
		carriageReturn(bw); lineFeed(bw);
		bw.write("      ");
		bw.write(headerData.Name2);
		carriageReturn(bw); lineFeed(bw);
		bw.write("      ");
		bw.write(headerData.Address1);
		carriageReturn(bw); lineFeed(bw);
		bw.write("      ");
		bw.write(headerData.Address2);
		carriageReturn(bw); lineFeed(bw);
		bw.write("      ");
		bw.write(headerData.Postal);
		bw.write(" ");
		bw.write(headerData.City);
		carriageReturn(bw); lineFeed(bw);
		lineFeed(bw);
		lineFeed(bw);
		lineFeed(bw);
		bw.write(doc);
		boldOff(bw);
		String pageStr = String.valueOf(pageControl.Page);
		if (pageControl.Page > 1) {
			bw.write("    (Seite ");
			bw.write(pageStr);
			bw.write(")");
		} else {
			bw.write("            ");
			for (int i = 0; i < pageStr.length(); i++)
				bw.write(" ");
		}
		bw.write("                 Bel-Nr Kd-Nr  Datum   Kz Tour Fahrer");
		carriageReturn(bw); lineFeed(bw);
		bw.write("                                                   ------+-----+--------+--+----+------");
		carriageReturn(bw); lineFeed(bw);
		bw.write("                                                   ");
		boldOn(bw);
		bw.write(headerData.DocumentNo);
		bw.write(" ");
		bw.write(headerData.BPValue);
		bw.write(" ");
		bw.write(headerData.DateOrdered);
		bw.write("    ");
		bw.write(headerData.DeliveryRoute);
		boldOff(bw);
		carriageReturn(bw); lineFeed(bw);
	}

	private void carriageReturn(BufferedWriter bw) throws IOException {
		bw.write(CR);
	}

	private void lineFeed(BufferedWriter bw) throws IOException {
		bw.write(LINE_FEED);
		pageControl.Line++;
	}

	private void formFeed(BufferedWriter bw) throws IOException {
		bw.write(FF);
		pageControl.Line = 1;
		pageControl.Page++;
	}

	private void boldOn(BufferedWriter bw) throws IOException {
		bw.write(ESC);
		bw.write(E);
	}

	private void boldOff(BufferedWriter bw) throws IOException {
		bw.write(ESC);
		bw.write(F);
	}

	public static class PageControl {
		public int Line;
		public int Page;
	}

}
