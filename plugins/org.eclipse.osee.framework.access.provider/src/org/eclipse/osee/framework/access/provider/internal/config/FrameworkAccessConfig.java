/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.access.provider.internal.config;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.eclipse.osee.framework.access.provider.internal.DefaultFrameworkAccessConstants;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author John R. Misinco
 */
public class FrameworkAccessConfig implements IDbInitializationTask {

   @Override
   public void run() {
      importFrameworkAccessModel();
   }

   private static void importFrameworkAccessModel() {
      Bundle bundle = FrameworkUtil.getBundle(FrameworkAccessConfig.class);
      URL url = bundle.getEntry("support/OseeAccess_FrameworkAccess.osee");

      InputStream inputStream = null;
      try {
         inputStream = new BufferedInputStream(url.openStream());
         SkynetTransaction transaction =
            TransactionManager.createTransaction(CoreBranches.COMMON, "Add Framework Access Model");
         Artifact artifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.AccessControlModel, CoreBranches.COMMON);
         artifact.setName(DefaultFrameworkAccessConstants.STORAGE_ARTIFACT_NAME);
         artifact.setSoleAttributeFromStream(CoreAttributeTypes.GeneralStringData, inputStream);
         artifact.persist(transaction);
         transaction.execute();
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      } finally {
         Lib.close(inputStream);
      }
   }
}
