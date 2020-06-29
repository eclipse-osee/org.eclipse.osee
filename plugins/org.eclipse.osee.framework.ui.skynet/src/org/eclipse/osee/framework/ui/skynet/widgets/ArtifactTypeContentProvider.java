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

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.logging.Level;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactTypeContentProvider implements ITreeContentProvider {
   @Override
   public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
   }

   @Override
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof BranchId) {
         try {
            return ServiceUtil.getTokenService().getArtifactTypes().toArray();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return null;
   }

   @Override
   public Object getParent(Object element) {
      return null;
   }

   @Override
   public boolean hasChildren(Object element) {
      return false;
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
