/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.access;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.access.AccessControlData;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.enums.PermissionEnum;

public class PolicyContentProvider implements ITreeContentProvider {

   private final Map<String, AccessControlData> accessControlList;
   private final Object accessControlledObject;

   public PolicyContentProvider(Map<String, AccessControlData> accessControlList, Object accessControlledObject) {
      this.accessControlList = accessControlList;
      this.accessControlledObject = accessControlledObject;
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

      Collection<AccessControlData> data = AccessControlManager.getAccessControlList(accessControlledObject);
      for (AccessControlData entry : data) {
         if (isUniqueUnlockedEntry(entry)) {
            accessControlList.put(entry.getSubject().getGuid(), entry);
         }
      }

      Object[] accessControlListArray = accessControlList.values().toArray();
      Arrays.sort(accessControlListArray);
      return accessControlListArray;
   }

   private boolean isUniqueUnlockedEntry(AccessControlData entry) {
      String subjectGuid = entry.getSubject().getGuid();
      boolean isUnique = !accessControlList.containsKey(subjectGuid);
      boolean isUnlocked = entry.getPermission() != PermissionEnum.LOCK;
      return isUnique && isUnlocked;
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