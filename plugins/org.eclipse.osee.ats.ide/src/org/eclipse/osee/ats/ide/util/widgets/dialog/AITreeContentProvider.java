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

package org.eclipse.osee.ats.ide.util.widgets.dialog;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class AITreeContentProvider implements ITreeContentProvider {

   private final Active active;
   private boolean showChildren = true;

   public AITreeContentProvider(Active active) {
      this.active = active;
   }

   @Override
   @SuppressWarnings("rawtypes")
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Collection) {
         return ((Collection) parentElement).toArray();
      } else if (parentElement instanceof IAtsActionableItem) {
         if (showChildren) {
            try {
               IAtsActionableItem ai = (IAtsActionableItem) parentElement;
               List<IAtsActionableItem> aias =
                  ActionableItems.getActive(ActionableItems.getChildren(ai, false), active);
               return aias.toArray();
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }
      return new Object[] {};
   }

   @Override
   public Object getParent(Object element) {
      if (element instanceof IAtsActionableItem) {
         return ((IAtsActionableItem) element).getParentActionableItem();
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

   public void setShowChildren(boolean showChildren) {
      this.showChildren = showChildren;
   }

}
