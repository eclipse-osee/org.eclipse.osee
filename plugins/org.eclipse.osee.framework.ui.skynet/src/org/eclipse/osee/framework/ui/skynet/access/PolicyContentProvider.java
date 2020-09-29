/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.ui.skynet.access;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.core.access.AccessControlData;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.ui.skynet.access.internal.OseeApiService;

public class PolicyContentProvider implements ITreeContentProvider {

   private final Map<ArtifactToken, AccessControlData> accessControlList;
   private final Collection<AccessControlData> deleteControlList;
   private final Object accessControlledObject;

   public PolicyContentProvider(Map<ArtifactToken, AccessControlData> accessControlList, Object accessControlledObject, Collection<AccessControlData> deleteControlList) {
      this.accessControlList = accessControlList;
      this.accessControlledObject = accessControlledObject;
      this.deleteControlList = deleteControlList;
   }

   @Override
   public void inputChanged(Viewer v, Object oldInput, Object newInput) {
      // do nothing
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public Object[] getElements(Object object) {

      Collection<AccessControlData> data =
         OseeApiService.get().getAccessControlService().getAccessControlList(accessControlledObject);
      for (AccessControlData entry : data) {
         if (!deleteControlList.contains(entry)) {
            accessControlList.put(entry.getSubject(), entry);
         }
      }

      for (AccessControlData lockData : OseeApiService.get().getAccessControlService().getAccessControlList(
         accessControlledObject)) {
         accessControlList.put(lockData.getSubject(), lockData);
      }

      Object[] accessControlListArray = accessControlList.values().toArray();
      Arrays.sort(accessControlListArray);
      return accessControlListArray;
   }

   @Override
   public Object[] getChildren(Object parentElement) {
      return getElements(parentElement);
   }

   @Override
   public Object getParent(Object element) {
      return null;
   }

   @Override
   public boolean hasChildren(Object element) {
      return false;
   }
}