package org.eclipse.osee.framework.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class SimpleOseeHandler extends Handler {

	public SimpleOseeHandler(){
		setFormatter(new SimpleOseeFormatter());
	}
	
	@Override
	public void close() throws SecurityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void publish(LogRecord record) {
		if(isLoggable(record)){
			if(record.getLevel().intValue() >= Level.SEVERE.intValue()){
				System.err.println(getFormatter().format(record));
			} else {
				System.out.println(getFormatter().format(record));
			}
		}
	}

}
