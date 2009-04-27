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
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Donald G. Dunne
 */
public class GroupListDialog extends ArtifactListDialog {
   BranchSelectComposite branchSelect;
   
   public GroupListDialog(Shell parent) {
      super(parent, null);
      setTitle("Select group");
      setMessage("Select group");
      setLabelProvider(new GroupsDescriptiveLabelProvider());

      setGroupsForSelection();
   }

   private void setGroupsForSelection() {
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
      try {
         for (Artifact art : UniversalGroup.getGroups(BranchManager.getCommonBranch())) {
            if (!art.getDescriptiveName().equals(ArtifactQuery.ROOT_ARTIFACT_TYPE_NAME)) {
               arts.add(art);
            }
         }
         if (branchSelect != null && branchSelect.getSelectedBranch() != null && !branchSelect.getSelectedBranch().equals(
               BranchManager.getCommonBranch())) {
            for (Artifact art : UniversalGroup.getGroups(branchSelect.getSelectedBranch())) {
               if (!art.getDescriptiveName().equals(ArtifactQuery.ROOT_ARTIFACT_TYPE_NAME)) {
                  arts.add(art);
               }
            }
         }
            } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      if (getTableViewer() == null) {
         setArtifacts(arts);
      } else {
         updateArtifacts(arts);
      }
      
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

      @Override
   protected Control createDialogArea(Composite container) {
      Control c = super.createDialogArea(container);
      branchSelect = new BranchSelectComposite(container, SWT.NONE, false);
      branchSelect.setDefaultSelectedBranch(BranchManager.getLastBranch());
      branchSelect.addListener(new Listener() {
         @Override
         public void handleEvent(Event event) {
            setGroupsForSelection();
         }
      });
      return c;
   }


}
