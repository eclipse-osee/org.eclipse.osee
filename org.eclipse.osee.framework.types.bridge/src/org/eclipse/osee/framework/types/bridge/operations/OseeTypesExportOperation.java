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
package org.eclipse.osee.framework.types.bridge.operations;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilderClient;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.types.bridge.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class OseeTypesExportOperation extends AbstractOperation {
   private final File folder;

   public OseeTypesExportOperation(File folder) {
      super("Export Osee Types Model", Activator.PLUGIN_ID);
      this.folder = folder;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("sessionId", ClientSessionManager.getSessionId());

      String url =
            HttpUrlBuilderClient.getInstance().getOsgiServletServiceUrl(OseeServerContext.OSEE_MODEL_CONTEXT, parameters);

      OutputStream outputStream = null;
      try {
         outputStream = new BufferedOutputStream(new FileOutputStream(new File(folder, getOseeFileName())));
         AcquireResult results = HttpProcessor.acquire(new URL(url), outputStream);
         if (!results.wasSuccessful()) {
            throw new OseeCoreException("Error exporting osee types");
         }
      } finally {
         Lib.close(outputStream);
      }
   }

   private String getOseeFileName() {
      return "OseeTypes_" + Lib.getDateTimeString() + ".osee";
   }
}
