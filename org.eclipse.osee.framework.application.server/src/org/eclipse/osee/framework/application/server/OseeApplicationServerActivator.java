/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.application.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class OseeApplicationServerActivator implements BundleActivator {
   private static final String errorMessage =
         "Error launching application server - did you forget to set the following vmargs ?\n-Dorg.osgi.service.http.port=<port>\n-Dosgi.compatibility.bootdelegation=true\n-Dequinox.ds.debug=true\n-Dosee.application.server.data=<FILE SYSTEM PATH>";

   private enum Operation {
      START, STOP;
   }

   private static final List<String> STOPPABLE_BUNDLE_LIST;
   static {
      STOPPABLE_BUNDLE_LIST = new ArrayList<String>();
      STOPPABLE_BUNDLE_LIST.add("org.eclipse.osee.framework.core.server");
      STOPPABLE_BUNDLE_LIST.add("org.eclipse.osee.framework.artifact.servlet");
      STOPPABLE_BUNDLE_LIST.add("org.eclipse.osee.framework.branch.management");
      STOPPABLE_BUNDLE_LIST.add("org.eclipse.osee.framework.branch.management.servlet");
      STOPPABLE_BUNDLE_LIST.add("org.eclipse.osee.framework.client.info.servlet");
      STOPPABLE_BUNDLE_LIST.add("org.eclipse.osee.framework.resource.locator.attribute");
      STOPPABLE_BUNDLE_LIST.add("org.eclipse.osee.framework.resource.locator.snapshot");
      STOPPABLE_BUNDLE_LIST.add("org.eclipse.osee.framework.resource.management");
      STOPPABLE_BUNDLE_LIST.add("org.eclipse.osee.framework.resource.management.servlet");
      STOPPABLE_BUNDLE_LIST.add("org.eclipse.osee.framework.resource.provider.attribute");
      STOPPABLE_BUNDLE_LIST.add("org.eclipse.osee.framework.resource.provider.common");
      STOPPABLE_BUNDLE_LIST.add("org.eclipse.osee.framework.resource.provider.snapshot");
      STOPPABLE_BUNDLE_LIST.add("org.eclipse.osee.framework.search.engine");
      STOPPABLE_BUNDLE_LIST.add("org.eclipse.osee.framework.search.engine.servlet");
      STOPPABLE_BUNDLE_LIST.add("org.eclipse.osee.framework.server.admin");
      STOPPABLE_BUNDLE_LIST.add("org.eclipse.osee.framework.server.lookup.servlet");
      STOPPABLE_BUNDLE_LIST.add("org.eclipse.osee.framework.session.management.servlet");
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      Map<String, Bundle> bundles = new HashMap<String, Bundle>();
      for (Bundle bundle : context.getBundles()) {
         bundles.put(bundle.getSymbolicName(), bundle);
      }
      try {
         String requiredBundles = (String) context.getBundle().getHeaders().get("Require-Bundle");
         if (OseeClientProperties.isLocalApplicationServerRequired() != false) {
            launchApplicationServer(requiredBundles, bundles);
         } else {
            processBundles(requiredBundles, bundles, Operation.STOP);
         }
      } catch (Exception ex) {
         throw new OseeWrappedException(errorMessage, ex);
      }
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
   }

   private void processBundle(Bundle bundle, String bundleName, boolean isStart) throws BundleException {
      if (!bundleName.equals("org.eclipse.osee.framework.database")) {
         if (isStart && (bundle.getState() != Bundle.ACTIVE || bundle.getState() != Bundle.START_TRANSIENT || bundle.getState() != Bundle.STARTING)) {
            bundle.start();
         } else if (STOPPABLE_BUNDLE_LIST.contains(bundleName) && (bundle.getState() != Bundle.STOP_TRANSIENT || bundle.getState() != Bundle.STOPPING)) {
            bundle.stop();
         }
      }
   }

   private void processBundles(String requiredBundles, Map<String, Bundle> bundles, Operation operation) throws BundleException, InterruptedException {
      Pattern pattern = Pattern.compile("(.*)?;bundle-version=\"(.*)?\"");
      boolean isStart = operation.equals(Operation.START);
      for (String entry : requiredBundles.split(",")) {
         Matcher matcher = pattern.matcher(entry);
         while (matcher.find()) {
            String bundleName = matcher.group(1);
            String requiredVersion = matcher.group(2);
            Bundle bundle = bundles.get(bundleName);
            if (bundle != null && isVersionAllowed(bundle, requiredVersion)) {
               try {
                  processBundle(bundle, bundleName, isStart);
               } catch (Exception ex) {
                  OseeLog.log(OseeApplicationServerActivator.class, Level.SEVERE, ex);
                  Thread.sleep(1000);
                  processBundle(bundle, bundleName, isStart);
               }
            }
         }
      }
   }

   private void launchApplicationServer(String requiredBundles, Map<String, Bundle> bundles) throws BundleException, InterruptedException {
      processBundles(requiredBundles, bundles, Operation.START);
      String message =
            String.format("Osee Application Server - port: [%s] data: [%s]",
                  OseeProperties.getOseeApplicationServerPort(), OseeProperties.getOseeApplicationServerData());
      System.out.println(message);
   }

   private boolean isVersionAllowed(Bundle bundle, String requiredVersion) {
      return true;
   }
}
