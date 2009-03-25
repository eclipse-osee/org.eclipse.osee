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
package org.eclipse.osee.framework.ui.skynet.util;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IBranchProvider;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.widgets.Display;

/**
 * @author Ryan D. Brooks
 */
public class ShowAttributeAction extends Action {
   private AttributeCheckListDialog attributeDialog;
   private StructuredViewer viewer;
   private String preferenceKey;
   private IBranchProvider branchProvider;

   public ShowAttributeAction(StructuredViewer viewer, String preferenceKey) {
      super("Show Attributes", SkynetGuiPlugin.getInstance().getImageDescriptor("filter.gif"));
      setToolTipText("Show Attributes");
      this.viewer = viewer;
      this.preferenceKey = preferenceKey;
   }

   public void setViewer(StructuredViewer viewer) {
      this.viewer = viewer;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.action.Action#run()
    */
   @Override
   public void run() {
      if (branchProvider != null && branchProvider.getBranch() != null) {
         try {
            attributeDialog =
                  new AttributeCheckListDialog(Display.getCurrent().getActiveShell(),
                        SkynetViews.loadAttrTypesFromPreferenceStore(preferenceKey, branchProvider.getBranch()),
                        preferenceKey);
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
      if (attributeDialog != null) {
      int result = attributeDialog.open();
      if (result == 0) {
         viewer.refresh(true);
      }
      }
   }

   public String getSelectedAttributeData(Artifact artifact) throws Exception {
      return attributeDialog != null ? attributeDialog.getSelectedAttributeData(artifact) : "";
   }

   public boolean noneSelected() {
      return attributeDialog != null ? attributeDialog.noneSelected() : false;
   }

   public Collection<AttributeType> getSelectedAttributes() {
      return attributeDialog != null ? attributeDialog.getSelectedAttributes() : null;
   }

   public void setValidAttributeTypes(Collection<AttributeType> attrTypes) {
      attributeDialog = new AttributeCheckListDialog(Display.getCurrent().getActiveShell(), attrTypes, preferenceKey);
   }
   
   public void setBranchForAttributeTypes(IBranchProvider branchProvider) throws OseeCoreException {
      this.branchProvider = branchProvider;
      if (branchProvider != null && branchProvider.getBranch() != null) {
            attributeDialog =
                  new AttributeCheckListDialog(Display.getCurrent().getActiveShell(),
                        SkynetViews.loadAttrTypesFromPreferenceStore(preferenceKey, branchProvider.getBranch()),
                        preferenceKey);
      }
   }
}
