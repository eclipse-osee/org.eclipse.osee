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

package org.eclipse.osee.framework.ui.skynet.blam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class BlamOperations {
   private static List<BlamOperation> blamsSortedByName;
   private static Map<String, BlamOperation> blamMap;

   public static BlamOperation getBlamOperation(String operationId) {
      return blamMap.get(operationId);
   }

   public static Collection<BlamOperation> getBlamOperationsNameSort() {
      if (blamsSortedByName == null) {
         blamsSortedByName = new ArrayList<BlamOperation>();
         blamMap = new HashMap<String, BlamOperation>();
         for (BlamOperation blam : getBlamOperations()) {
            blamMap.put(blam.getClass().getSimpleName(), blam);
         }
         String names[] = blamMap.keySet().toArray(new String[blamMap.keySet().size()]);
         Arrays.sort(names);
         for (String name : names)
            blamsSortedByName.add(blamMap.get(name));
      }
      return blamsSortedByName;
   }

   public static Collection<BlamOperation> getBlamOperations() {
      List<BlamOperation> blamOperations = new ArrayList<BlamOperation>();
      for (IConfigurationElement iConfigurationElement : ExtensionPoints.getExtensionElements(
            "org.eclipse.osee.framework.ui.skynet.BlamOperation", "Operation")) {

         String classname = iConfigurationElement.getAttribute("className");
         String bundleName = iConfigurationElement.getContributor().getName();
         if (classname != null && bundleName != null) {
            Bundle bundle = Platform.getBundle(bundleName);
            try {
               Class<?> taskClass = bundle.loadClass(classname);
               Object obj = taskClass.newInstance();
               BlamOperation task = (BlamOperation) obj;
               blamOperations.add(task);
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, "Error loading BlamOperation extension", ex, false);
            }
         }
      }
      return blamOperations;
   }

}
