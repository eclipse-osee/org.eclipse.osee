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

package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet;

import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * This singleton artifact stores the default customizations for ATS XViewers
 * 
 * @author Donald G. Dunne
 */
public class GlobalXViewerSettings {

   private static final String GLOBAL_XVIEWER_ID = "global.xViewer.settings";
   private static final String ARTIFACT_TYPE_NAME = "XViewer Global Customization";

   private GlobalXViewerSettings() {
      super();
   }

   public static Artifact getAtsCustArtifact() throws OseeCoreException {
      return getAtsCustArtifactOrCreate(false, null);
   }

   public static Artifact getAtsCustArtifactOrCreate(boolean isCreationAllowed, SkynetTransaction transaction) throws OseeCoreException {
      Artifact globalSettings = null;

      globalSettings = ArtifactCache.getByTextId(GLOBAL_XVIEWER_ID, BranchManager.getCommonBranch());
      if (globalSettings == null) {
         try {
            globalSettings =
                  ArtifactQuery.getArtifactFromTypeAndName(ARTIFACT_TYPE_NAME, ARTIFACT_TYPE_NAME,
                        BranchManager.getCommonBranch());
            ArtifactCache.cacheByTextId(GLOBAL_XVIEWER_ID, globalSettings);
         } catch (ArtifactDoesNotExist ex) {
            if (isCreationAllowed) {
               globalSettings =
                     ArtifactTypeManager.addArtifact(ARTIFACT_TYPE_NAME, BranchManager.getCommonBranch(),
                           ARTIFACT_TYPE_NAME);
               globalSettings.persistAttributes(transaction);
               ArtifactCache.cacheByTextId(GLOBAL_XVIEWER_ID, globalSettings);
            } else {
               throw ex;
            }
         }
      }
      return globalSettings;
   }
}