package org.eclipse.osee.framework.plugin.core.server;

import java.io.IOException;

import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.osgi.framework.Bundle;

public class ClassFinder extends ResourceFinder {

    private final ExportClassLoader loader = new ExportClassLoader();
    
    @Override
    public void dispose() {
    }

    @Override
    public byte[] find(String path) throws IOException {
	Bundle bundle = loader.getExportingBundle(path);
	if (bundle != null) {
	    return getBytes(bundle.getResource(path).openStream());
	}
	return null;
    }

}
