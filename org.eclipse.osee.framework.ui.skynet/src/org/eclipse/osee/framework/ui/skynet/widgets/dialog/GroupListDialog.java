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
package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.sql.SQLException;
import java.util.ArrayList;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Donald G. Dunne
 */
public class GroupListDialog extends ArtifactListDialog {

   public GroupListDialog(Shell parent) {
      super(parent, null);
      setTitle("Select group");
      setMessage("Select group");
      setLabelProvider(new GroupsDescriptiveLabelProvider());
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
      try {
         for (Artifact art : UniversalGroup.getGroups(BranchPersistenceManager.getCommonBranch())) {
            if (!art.getDescriptiveName().equals(ArtifactPersistenceManager.ROOT_ARTIFACT_TYPE_NAME)) {
               arts.add(art);
            }
         }
         if (!BranchPersistenceManager.getDefaultBranch().equals(
               BranchPersistenceManager.getCommonBranch())) {
            for (Artifact art : UniversalGroup.getGroups(BranchPersistenceManager.getDefaultBranch())) {
               if (!art.getDescriptiveName().equals(ArtifactPersistenceManager.ROOT_ARTIFACT_TYPE_NAME)) {
                  arts.add(art);
               }
            }
         }
      } catch (SQLException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, false);
      }
      setArtifacts(arts);
   }

   public class GroupsDescriptiveLabelProvider implements ILabelProvider {

      public Image getImage(Object arg0) {
         return null;
      }

      public String getText(Object obj) {
         if (obj instanceof Artifact) {
            Artifact art = (Artifact) obj;
            if (art.getArtifactTypeName().equals(UniversalGroup.ARTIFACT_TYPE_NAME)) {
               return art.toString() + " (" + art.getBranch().getBranchShortName() + ")";
            }
            art.toString();
         }
         return "";
      }

      public void addListener(ILabelProviderListener arg0) {
      }

      public void dispose() {
      }

      public boolean isLabelProperty(Object arg0, String arg1) {
         return false;
      }

      public void removeListener(ILabelProviderListener arg0) {
      }

   }

}
