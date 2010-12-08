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

package org.eclipse.osee.ats.util.widgets.dialog;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

public class AITreeContentProvider implements ITreeContentProvider {

   private final Active active;
   private boolean showChildren = true;

   public AITreeContentProvider(Active active) {
      super();
      this.active = active;
   }

   @Override
   @SuppressWarnings("rawtypes")
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Collection) {
         return ((Collection) parentElement).toArray();
      } else if (parentElement instanceof ActionableItemArtifact) {
         if (showChildren) {
            try {
               ActionableItemArtifact ai = (ActionableItemArtifact) parentElement;
               return AtsUtil.getActive(Artifacts.getChildrenOfTypeSet(ai, ActionableItemArtifact.class, false),
                  active, ActionableItemArtifact.class).toArray();
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
         }
      }
      return new Object[] {};
   }

   @Override
   public Object getParent(Object element) {
      try {
         if (element instanceof ActionableItemArtifact) {
            return ((ActionableItemArtifact) element).getParent();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return null;
   }

   @Override
   public boolean hasChildren(Object element) {
      return getChildren(element).length > 0;
   }

   @Override
   public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      // do nothing
   }

   public boolean isShowChildren() {
      return showChildren;
   }

   public void setShowChildren(boolean showChildren) {
      this.showChildren = showChildren;
   }

}
