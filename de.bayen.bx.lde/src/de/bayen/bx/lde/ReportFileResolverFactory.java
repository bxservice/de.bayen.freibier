package de.bayen.bx.lde;

import de.bayen.freibier.report.IReportFileResolverFactory;
import de.bayen.freibier.report.ReportFileResolver;

public class ReportFileResolverFactory implements IReportFileResolverFactory {

	public ReportFileResolverFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ReportFileResolver getFileResolver(String prefix) {
		return new MyReportFileResolverClasspath();
	}

}
