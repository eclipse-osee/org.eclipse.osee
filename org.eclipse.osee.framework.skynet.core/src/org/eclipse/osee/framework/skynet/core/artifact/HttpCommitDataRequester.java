/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.IDataTranslationService;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilder;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchCommitData;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.Function;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.skynet.core.User;

/**
 * @author Megumi Telles
 */
public class HttpCommitDataRequester {

   public static void commitBranch(IProgressMonitor monitor, User user, Branch sourceBranch, Branch destinationBranch, boolean isArchiveAllowed) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", Function.BRANCH_COMMIT.name());

      BranchCommitData data = new BranchCommitData(user, sourceBranch, destinationBranch, isArchiveAllowed);
      AcquireResult response = post(parameters, data);
      if (response.wasSuccessful()) {
         // Kick commit event?
         //OseeEventManager.kickBranchEvent(HttpBranchCreation.class, , branch.getId());
      }
   }

   private static AcquireResult post(Map<String, String> parameters, BranchCommitData data) throws OseeCoreException {
      IDataTranslationService service = null;
      PropertyStore propertyStore = service.convert(data, BranchCommitData.class);
      try {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         propertyStore.save(buffer);
         String urlString =
               HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeServerContext.BRANCH_CONTEXT, parameters);
         return HttpProcessor.post(new URL(urlString), new ByteArrayInputStream(buffer.toByteArray()), "text/xml",
               "UTF-8", new ByteArrayOutputStream());
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }
}
