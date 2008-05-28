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

import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BasicArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * This singleton artifact stores the default customizations for ATS XViewers
 * 
 * @author Donald G. Dunne
 */
public class XViewerCustomizationArtifact extends BasicArtifact {

   public static String ARTIFACT_TYPE_NAME = "XViewer Global Customization";
   public static XViewerCustomizationArtifact xViewerCustomizationArtifact;

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
    * @throws SQLException
    */
   public XViewerCustomizationArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   public static XViewerCustomizationArtifact getAtsCustArtifact() {
      return getAtsCustArtifactOrCreate(false);
   }

   public static XViewerCustomizationArtifact getAtsCustArtifactOrCreate(boolean create) {
      if (xViewerCustomizationArtifact == null) {
         try {
            Collection<Artifact> arts =
                  ArtifactQuery.getArtifactsFromTypeAndName(ARTIFACT_TYPE_NAME, ARTIFACT_TYPE_NAME,
                        BranchPersistenceManager.getCommonBranch());
            if (arts.size() == 1) {
               xViewerCustomizationArtifact = (XViewerCustomizationArtifact) arts.iterator().next();
            } else if (arts.size() == 0 && create) {
               xViewerCustomizationArtifact =
                     (XViewerCustomizationArtifact) ArtifactTypeManager.addArtifact(ARTIFACT_TYPE_NAME,
                           BranchPersistenceManager.getCommonBranch(), ARTIFACT_TYPE_NAME);
               xViewerCustomizationArtifact.persistAttributes();
            } else if (arts.size() != 1) throw new IllegalArgumentException(
                  "Should only be one " + ARTIFACT_TYPE_NAME + ".  Found " + arts.size() + ".  ATS not configured in OSEE?.");
         } catch (SQLException ex) {
            SkynetGuiPlugin.getLogger().log(Level.SEVERE, ex.toString(), ex);
         }
      }
      return xViewerCustomizationArtifact;
   }

}