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
package org.eclipse.osee.framework.skynet.core.transaction;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilderClient;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.HttpProcessor;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTaggingOperation extends AbstractOperation {
   private static final String XML_START = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><AttributeTag>";
   private static final String XML_FINISH = "</AttributeTag>";
   private static final String PREFIX = "<entry gammaId=\"";
   private static final String POSTFIX = "\"/>\n";
   private final Collection<Integer> gammaIds;

   public AttributeTaggingOperation(Collection<Integer> gammaIds) {
      super(AttributeTaggingOperation.class.getSimpleName(), Activator.PLUGIN_ID);
      this.gammaIds = gammaIds;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      long start = System.currentTimeMillis();
      StringBuilder response = new StringBuilder();
      ByteArrayInputStream inputStream = null;
      try {
         Map<String, String> parameters = new HashMap<String, String>();
         parameters.put("sessionId", ClientSessionManager.getSessionId());
         if (DbUtil.isDbInit()) {
            parameters.put("wait", "true");
         }
         StringBuilder payload = new StringBuilder(XML_START);
         for (int data : gammaIds) {
            payload.append(PREFIX);
            payload.append(data);
            payload.append(POSTFIX);
         }
         payload.append(XML_FINISH);

         inputStream = new ByteArrayInputStream(payload.toString().getBytes("UTF-8"));
         String url =
            HttpUrlBuilderClient.getInstance().getOsgiServletServiceUrl(OseeServerContext.SEARCH_TAGGING_CONTEXT,
               parameters);
         response.append(HttpProcessor.put(new URL(url), inputStream, "application/xml", "UTF-8"));
         OseeLog.logf(Activator.class, Level.FINEST, "Transmitted to Tagger in [%d ms]",
            System.currentTimeMillis() - start);
      } catch (Exception ex) {
         if (response.length() > 0) {
            response.append("\n");
         }
         response.append(ex.getLocalizedMessage());
         OseeLog.log(Activator.class, Level.SEVERE, response.toString(), ex);
      } finally {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (IOException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }

   }
}
