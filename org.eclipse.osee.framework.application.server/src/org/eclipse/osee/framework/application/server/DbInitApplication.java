package org.eclipse.osee.framework.application.server;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osee.framework.database.initialize.LaunchOseeDbConfigClient;


public class DbInitApplication  implements IApplication {

	@Override
	public Object start(IApplicationContext context) throws Exception {
		LaunchOseeDbConfigClient.main(null);
		return EXIT_OK;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

}
