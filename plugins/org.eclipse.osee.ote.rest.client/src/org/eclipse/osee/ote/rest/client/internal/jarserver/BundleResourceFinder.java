package org.eclipse.osee.ote.rest.client.internal.jarserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.server.ResourceFinder;
import org.eclipse.osee.ote.core.BundleInfo;

public class BundleResourceFinder extends ResourceFinder {

   private List<BundleInfo> bundleInfo;
	
   public BundleResourceFinder(List<BundleInfo> bundleInfo) {
      super();
      this.bundleInfo = bundleInfo;
   }

   @Override
   public byte[] find(String path) throws IOException {
         for (BundleInfo info : bundleInfo) {
            if (info.getSymbolicName().equals(path)) {
               return Lib.inputStreamToBytes(new FileInputStream(info.getFile()));
            }
         }
      return null;
   }

   @Override
   public void dispose() {
   }
}
