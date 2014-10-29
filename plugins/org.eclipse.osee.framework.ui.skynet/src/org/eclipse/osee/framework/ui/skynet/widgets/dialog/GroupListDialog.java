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

import java.util.ArrayList;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectComposite;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class GroupListDialog extends FilteredTreeArtifactDialog {
   BranchSelectComposite branchSelect;

   public GroupListDialog() {
      super("Select group", "Select group", getGroupsForSelection(), new GroupsDescriptiveLabelProvider());
   }

   private static ArrayList<Artifact> getGroupsForSelection() {
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
      try {
         for (Artifact art : UniversalGroup.getGroups(BranchManager.getCommonBranch())) {
            if (!art.getName().equals(OseeSystemArtifacts.ROOT_ARTIFACT_TYPE_NAME)) {
               arts.add(art);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return arts;
   }

   private static class GroupsDescriptiveLabelProvider implements ILabelProvider {

      @Override
      public Image getImage(Object arg0) {
         return null;
      }

      @Override
      public String getText(Object obj) {
         if (obj instanceof Artifact) {
            Artifact art = (Artifact) obj;
            try {
               if (art.isOfType(CoreArtifactTypes.UniversalGroup)) {
                  return art.toString() + " (" + art.getFullBranch().getShortName() + ")";
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
            art.toString();
         }
         return "";
      }

      @Override
      public void addListener(ILabelProviderListener arg0) {
         // do nothing
      }

      @Override
      public void dispose() {
         // do nothing
      }

      @Override
      public boolean isLabelProperty(Object arg0, String arg1) {
         return false;
      }

      @Override
      public void removeListener(ILabelProviderListener arg0) {
         // do nothing
      }

   }

}
