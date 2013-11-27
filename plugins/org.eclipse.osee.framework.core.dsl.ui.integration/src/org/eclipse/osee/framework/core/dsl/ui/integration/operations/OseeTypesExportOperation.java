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
package org.eclipse.osee.framework.core.dsl.ui.integration.operations;

import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilderClient;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.dsl.ui.integration.internal.DslUiIntegrationConstants;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.HttpProcessor;
import org.eclipse.osee.framework.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class OseeTypesExportOperation extends AbstractOperation {
   private final OutputStream outputStream;

   public OseeTypesExportOperation(OutputStream outputStream) {
      super("Export Osee Types Model", DslUiIntegrationConstants.PLUGIN_ID);
      this.outputStream = outputStream;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Conditions.checkNotNull(outputStream, "outputStream");
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("sessionId", ClientSessionManager.getSessionId());

      String url =
         HttpUrlBuilderClient.getInstance().getOsgiServletServiceUrl(OseeServerContext.OSEE_MODEL_CONTEXT, parameters);

      AcquireResult results = HttpProcessor.acquire(new URL(url), outputStream);
      if (!results.wasSuccessful()) {
         throw new OseeCoreException("Error exporting osee types");
      }
   }
}
