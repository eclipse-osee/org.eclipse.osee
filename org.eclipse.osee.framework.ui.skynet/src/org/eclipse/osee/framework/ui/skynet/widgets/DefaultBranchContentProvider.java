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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;

/**
 * @author Robert A. Fisher
 */
public class DefaultBranchContentProvider implements ITreeContentProvider, IBranchEventListener {
   private final ITreeContentProvider provider;
   private Viewer viewer;
   private final Branch branch;

   /**
    * @param provider
    */
   public DefaultBranchContentProvider(final ITreeContentProvider provider, Branch branch) {
      if (provider == null) throw new IllegalArgumentException("provider can not be null");

      this.branch = branch;
      this.provider = provider;
      this.viewer = null;

   }

   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      this.viewer = viewer;
   }

   public void dispose() {
      OseeEventManager.removeListener(this);
   }

   public Object[] getChildren(Object parentElement) {
      return provider.getChildren(parentElement);
   }

   public Object getParent(Object element) {
      return provider.getParent(element);
   }

   public boolean hasChildren(Object element) {
      return provider.hasChildren(element);
   }

   public Object[] getElements(Object inputElement) {
      return provider.getElements(branch);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleBranchEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, org.eclipse.osee.framework.skynet.core.artifact.BranchModType, int)
    */
   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, final int branchId) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleLocalBranchToArtifactCacheUpdateEvent(org.eclipse.osee.framework.ui.plugin.event.Sender)
    */
   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
   }
}
