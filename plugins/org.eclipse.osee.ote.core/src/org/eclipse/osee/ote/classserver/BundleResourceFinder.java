package org.eclipse.osee.ote.classserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.ote.classserver.ResourceFinder;
import org.eclipse.osee.ote.core.BundleInfo;

public class BundleResourceFinder extends ResourceFinder {

   private final List<BundleInfo> bundleInfo;

   public BundleResourceFinder(List<BundleInfo> bundleInfo) {
      super();
      this.bundleInfo = bundleInfo;
   }

   @Override
   public byte[] find(String path) throws IOException {
      for (BundleInfo info : bundleInfo) {
         if (info.getSymbolicName().equals(path) || info.getFile().getName().equals(path)) {
            return Lib.inputStreamToBytes(new FileInputStream(info.getFile()));
         } 
      }
      return null;
   }

   @Override
   public void dispose() {
      //
   }
}
