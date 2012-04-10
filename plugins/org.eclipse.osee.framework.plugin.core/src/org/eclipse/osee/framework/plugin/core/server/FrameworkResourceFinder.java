package org.eclipse.osee.framework.plugin.core.server;

import java.io.IOException;
import java.net.URL;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class FrameworkResourceFinder extends ResourceFinder{

	@Override
	public byte[] find(String path) throws IOException {
		for(Bundle bundle:FrameworkUtil.getBundle(getClass()).getBundleContext().getBundles()){
			URL url = bundle.getResource(path);
			if (url != null) {
				return getBytes(url.openStream());
			}
		}
		return null;
	}

	@Override
	public void dispose() {
		
	}

}
