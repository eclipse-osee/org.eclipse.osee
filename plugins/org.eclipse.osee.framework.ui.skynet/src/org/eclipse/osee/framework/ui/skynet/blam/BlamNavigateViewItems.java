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

package org.eclipse.osee.framework.ui.skynet.blam;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.access.UserServiceImpl;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemBlam;

/**
 * @author Donald G. Dunne
 */
public class BlamNavigateViewItems implements XNavigateItemProvider {

   private static TreeMap<String, AbstractBlam> blams;

   public synchronized static Map<String, AbstractBlam> getBlamMap() {
      if (blams == null) {
         blams = new TreeMap<>();
         ExtensionDefinedObjects<AbstractBlam> definedObjects = new ExtensionDefinedObjects<>(
            "org.eclipse.osee.framework.ui.skynet.BlamOperation", "Operation", "className");
         for (AbstractBlam blam : definedObjects.getObjects()) {
            blams.put(blam.getName(), blam);
         }
      }
      return blams;
   }

   @Override
   public boolean isApplicable() {
      return true;
   }

   @Override
   public List<XNavigateItem> getNavigateItems(List<XNavigateItem> items) {
      Collection<IUserGroupArtifactToken> userGroups = UserServiceImpl.getUserGrps();
      for (AbstractBlam blam : BlamNavigateViewItems.getBlamOperations()) {
         if (blam.isApplicable()) {
            Collection<IUserGroupArtifactToken> blamUserGroups = blam.getUserGroups();
            if (blamUserGroups.isEmpty() || !Collections.setIntersection(blamUserGroups, userGroups).isEmpty()) {
               XNavigateItemBlam blamItem = new XNavigateItemBlam(blam);
               items.add(blamItem);
            }
         }
      }
      return items;
   }

   public static Collection<AbstractBlam> getBlamOperations() {
      return getBlamMap().values();
   }
}