package org.eclipse.osee.framework.logging;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class SevereLoggingMonitor implements ILoggerListener {

	private List<IHealthStatus> status = new ArrayList<IHealthStatus>();
	private ILoggerFilter filter = new ILoggerFilter(){

		@Override
		public Pattern bundleId() {
			return null;
		}

		@Override
		public Level getLoggerLevel() {
			return Level.SEVERE;
		}

		@Override
		public Pattern name() {
			return null;
		}
		
	};
	
	@Override
	public ILoggerFilter getFilter() {
		return filter;
	}

	@Override
	public void log(String loggerName, String bundleId, Level level,
			String message, Throwable th) {
		status.add(new BaseStatus(level, message, th));
	}

	public List<IHealthStatus> getSevereLogs(){
		return status;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(status.size());
		sb.append(" Severe logs captured.\n");
		for(IHealthStatus health:status){
			sb.append(health.getException().getMessage());
			sb.append("\n");
		}
		return sb.toString();
	}
	
}
