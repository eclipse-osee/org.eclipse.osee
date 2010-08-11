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
import org.eclipse.osee.ats.access.AtsAccessUtil;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslProvider;
import org.eclipse.osee.framework.core.dsl.integration.util.ModelUtil;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Roberto E. Escobar
 */
public class AtsAccessOseeDslProvider implements OseeDslProvider {

   private OseeDsl oseeDsl;

   private Artifact getStorageArtifact() throws OseeCoreException {
      return ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.AccessControlModel,
         AtsAccessUtil.ATS_ACCESS_MODEL_NAME, BranchManager.getCommonBranch());
   }

   @Override
   public void loadDsl() throws OseeCoreException {
      Artifact artifact = getStorageArtifact();
      String accessModel = artifact.getSoleAttributeValue(CoreAttributeTypes.GeneralStringData);
      oseeDsl = ModelUtil.loadModel("ats:/xtext/cm.access.osee", accessModel);
   }

   @Override
   public OseeDsl getDsl() throws OseeCoreException {
      if (oseeDsl == null) {
         loadDsl();
      }
      return oseeDsl;
   }

   @Override
   public void storeDsl(OseeDsl dsl) throws OseeCoreException {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      try {
         ModelUtil.saveModel(dsl, "ats:/xtext/cm.access.osee", outputStream, false);
         Artifact artifact = getStorageArtifact();
         artifact.setSoleAttributeFromString(CoreAttributeTypes.GeneralStringData, outputStream.toString("UTF-8"));
         artifact.persist();
         loadDsl();
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }
}
