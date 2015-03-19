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
package org.eclipse.osee.framework.skynet.core.internal;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.model.OseeImportModelRequest;
import org.eclipse.osee.framework.core.model.OseeImportModelResponse;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.HttpClientMessage;

/**
 * @author Roberto E. Escobar
 */
public class OseeTypesImportOperation extends AbstractOperation {
   private final IOseeCachingService cacheService;
   private final URI model;

   public OseeTypesImportOperation(IOseeCachingService cacheService, URI model) {
      super("Import Osee Types Model", Activator.PLUGIN_ID);
      this.cacheService = cacheService;
      this.model = model;
   }

   private String getModel(URL url) throws IOException {
      InputStream inputStream = null;
      try {
         inputStream = new BufferedInputStream(url.openStream());
         return Lib.inputStreamToString(inputStream);
      } finally {
         Lib.close(inputStream);
      }
   }

   private String getName(URI uri) {
      String name = uri.toASCIIString();
      int index = name.lastIndexOf("/");
      if (index > 0) {
         name = name.substring(index + 1, name.length());
      }
      return name;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Map<String, String> parameters = new HashMap<String, String>();

      OseeImportModelRequest modelRequest =
         new OseeImportModelRequest(getName(model), getModel(model.toURL()), false, false);

      OseeImportModelResponse response =
         HttpClientMessage.send(OseeServerContext.OSEE_MODEL_CONTEXT, parameters,
            CoreTranslatorId.OSEE_IMPORT_MODEL_REQUEST, modelRequest, CoreTranslatorId.OSEE_IMPORT_MODEL_RESPONSE);

      if (response.wasPersisted()) {
         cacheService.getEnumTypeCache().reloadCache();
         cacheService.getAttributeTypeCache().reloadCache();
         cacheService.getArtifactTypeCache().reloadCache();
         cacheService.getRelationTypeCache().reloadCache();
      }

   }

}
