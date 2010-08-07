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
package org.eclipse.osee.ats.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslProvider;
import org.eclipse.osee.framework.core.dsl.integration.util.ModelUtil;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public class AtsAccessOseeDslProvider implements OseeDslProvider {

   private OseeDsl oseeDsl;

   private Artifact getStorageArtifact() {
      return null;
   }

   @Override
   public void loadDsl() throws OseeCoreException {
      Artifact artifact = getStorageArtifact();
      String accessModel = "";
      //      artifact.getSoleAttributeValue(CoreAttributeTypes.ACCESS_CONTEXT_ID);
      oseeDsl = ModelUtil.loadModel("ats:/xtext/cm.access.osee", accessModel);
   }

   @Override
   public OseeDsl getDsl() throws OseeCoreException {
      return oseeDsl;
   }

   @Override
   public void storeDsl(OseeDsl dsl) throws OseeCoreException {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      try {
         ModelUtil.saveModel(dsl, "ats:/xtext/cm.access.osee", outputStream, false);
         //         Artifact artifact = getStorageArtifact();
         //         artifact.setSoleAttributeFromString(CoreAttributeTypes.ACCESS_CONTEXT_ID, outputStream.toString("UTF-8"));
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }
}
