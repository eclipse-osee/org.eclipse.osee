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
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BasicArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.factory.IArtifactFactory;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
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
   public XViewerCustomizationArtifact(IArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch) throws SQLException {
      super(parentFactory, guid, humanReadableId, branch);
   }

   public static XViewerCustomizationArtifact getAtsCustArtifact() {
      return getAtsCustArtifactOrCreate(false);
   }

   public static XViewerCustomizationArtifact getAtsCustArtifactOrCreate(boolean create) {
      if (xViewerCustomizationArtifact == null) {
         try {
            Collection<Artifact> arts =
                  ArtifactPersistenceManager.getInstance().getArtifactsFromSubtypeName(ARTIFACT_TYPE_NAME,
                        BranchPersistenceManager.getInstance().getCommonBranch());
            if (arts.size() == 1) {
               xViewerCustomizationArtifact = (XViewerCustomizationArtifact) arts.iterator().next();
            } else if (arts.size() == 0 && create) {
               xViewerCustomizationArtifact =
                     (XViewerCustomizationArtifact) ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(
                           XViewerCustomizationArtifact.ARTIFACT_TYPE_NAME).makeNewArtifact(
                           branchManager.getCommonBranch());
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