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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;

/**
 * @author Robert A. Fisher
 */
public class DefaultBranchContentProvider implements ITreeContentProvider, IBranchEventListener {
   private final ITreeContentProvider provider;
   private final BranchId branch;

   public DefaultBranchContentProvider(final ITreeContentProvider provider, BranchId branch) {
      if (provider == null) {
         throw new IllegalArgumentException("provider can not be null");
      }

      this.branch = branch;
      this.provider = provider;
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      // do nothing
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
   }

   @Override
   public Object[] getChildren(Object parentElement) {
      return provider.getChildren(parentElement);
   }

   @Override
   public Object getParent(Object element) {
      return provider.getParent(element);
   }

   @Override
   public boolean hasChildren(Object element) {
      return provider.hasChildren(element);
   }

   @Override
   public Object[] getElements(Object inputElement) {
      return provider.getElements(branch);
   }

   @Override
   public void handleBranchEvent(Sender sender, BranchEvent branchEvent) {
      // do nothing
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      if (branch != null) {
         return OseeEventManager.getEventFiltersForBranch(branch);
      }
      return null;
   }

}
