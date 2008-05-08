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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Ryan D. Brooks
 */
public class ShowAttributeAction extends Action {
   private AttributeCheckListDialog attributeDialog;
   private final StructuredViewer viewer;
   private String preferenceKey;
   private ViewPart viewPart;

   public ShowAttributeAction(StructuredViewer viewer, String preferenceKey) {
      super("Show Attributes", SkynetGuiPlugin.getInstance().getImageDescriptor("filter.gif"));
      setToolTipText("Show Attributes");
      this.viewer = viewer;
      this.preferenceKey = preferenceKey;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.action.Action#run()
    */
   @Override
   public void run() {
      int result = attributeDialog.open();
      if (result == 0) {
         viewer.refresh(true);
      }
   }

   public void addToView(ViewPart viewPart) {
      this.viewPart = viewPart;
      IMenuManager toolbarManager = viewPart.getViewSite().getActionBars().getMenuManager();
      toolbarManager.add(this);
   }

   public String getSelectedAttributeData(Artifact artifact) throws Exception {
      return attributeDialog.getSelectedAttributeData(artifact);
   }

   public boolean noneSelected() {
      return attributeDialog.noneSelected();
   }

   public Collection<AttributeType> getSelectedAttributes() {
      return attributeDialog.getSelectedAttributes();
   }

   public void setValidAttributeTypes(Collection<AttributeType> attrTypes) {
      attributeDialog = new AttributeCheckListDialog(viewPart.getSite().getShell(), attrTypes, preferenceKey);
   }
}
