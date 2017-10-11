package de.bayen.bx.lde;

import de.bxservice.report.IReportFileResolverFactory;
import de.bxservice.report.ReportFileResolver;

public class ReportFileResolverFactory implements IReportFileResolverFactory {

	public ReportFileResolverFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ReportFileResolver getFileResolver(String prefix) {
		return new MyReportFileResolverClasspath();
	}

}
