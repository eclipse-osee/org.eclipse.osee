/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.dsl.ui.integration.operations;

import java.io.ByteArrayOutputStream;
import org.eclipse.osee.framework.core.dsl.OseeDslResourceUtil;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslProvider;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractOseeDslProvider implements OseeDslProvider {

   private OseeDsl oseeDsl;
   private final String locationUri;

   protected AbstractOseeDslProvider(String locationUri) {
      this.locationUri = locationUri;
   }

   protected abstract String getModelFromStorage() ;

   protected abstract void saveModelToStorage(String model) ;

   @Override
   public void loadDsl()  {
      String accessModel = getModelFromStorage();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      IOperation op = ArtifactTypeManager.newExportTypesOp(outputStream);
      Operations.executeWorkAndCheckStatus(op);
      try {
         outputStream.write(accessModel.getBytes("utf-8"));
         oseeDsl = OseeDslResourceUtil.loadModel(locationUri, outputStream.toString("utf-8")).getModel();
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   @Override
   public OseeDsl getDsl()  {
      if (oseeDsl == null) {
         loadDsl();
      }
      return oseeDsl;
   }

   @Override
   public void storeDsl(OseeDsl dsl)  {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      try {
         OseeDslResourceUtil.saveModel(dsl, locationUri, outputStream, false);
         saveModelToStorage(outputStream.toString("UTF-8"));
         loadDsl();
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

}