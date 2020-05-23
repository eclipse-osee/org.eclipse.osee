/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.util.widgets.dialog;

import java.util.Collection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.core.enums.Active;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemTreeContentProvider implements ITreeContentProvider {

   private final Active active;

   public ActionableItemTreeContentProvider() {
      super();
      this.active = null;
   }

   public ActionableItemTreeContentProvider(Active active) {
      super();
      this.active = active;
   }

   @Override
   @SuppressWarnings("rawtypes")
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Collection) {
         return ((Collection) parentElement).toArray();
      } else if (parentElement instanceof IAtsActionableItem && active != null) {
         try {
            IAtsActionableItem aia = (IAtsActionableItem) parentElement;
            return AtsClientService.get().getActionableItemService().getActive(
               AtsClientService.get().getActionableItemService().getChildren(aia, false), active).toArray();
         } catch (Exception ex) {
            // do nothing
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

}
