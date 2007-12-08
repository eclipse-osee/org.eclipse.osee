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
package org.eclipse.osee.framework.ui.skynet.artifact;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.GlobalPreferences;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.XViewerCustomizationArtifact;

/**
 * @author Ryan D. Brooks
 */
public class SkynetGuiArtifactFactory extends ArtifactFactory<Artifact> {
   private static SkynetGuiArtifactFactory factory = null;

   private SkynetGuiArtifactFactory(int factoryId) {
      super(factoryId);
   }

   public static SkynetGuiArtifactFactory getInstance(int factoryId) {
      if (factory == null) {
         factory = new SkynetGuiArtifactFactory(factoryId);
      }
      return factory;
   }

   public static SkynetGuiArtifactFactory getInstance() {
      return factory;
   }

   public @Override
   Artifact getNewArtifact(String guid, String humandReadableId, String factoryKey, Branch branch) throws SQLException {
      if (factoryKey.equals(XViewerCustomizationArtifact.ARTIFACT_TYPE_NAME)) {
         return new XViewerCustomizationArtifact(this, guid, humandReadableId, branch);
      }
      if (factoryKey.equals(GlobalPreferences.ARTIFACT_NAME)) {
         return new GlobalPreferences(this, guid, humandReadableId, branch);
      }

      throw new IllegalArgumentException("did not recognize the factory key: " + factoryKey);
   }
}