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
package org.eclipse.osee.framework.ui.skynet.dbHealth;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseHealthOpsExtensionManager {
   private static final String EXTENSION_ELEMENT = "DatabaseHealthOperation";
   private static final String CLASS_ATTRIBUTE_NAME = "className";
   private static final String EXTENSION_POINT = Activator.PLUGIN_ID + "." + EXTENSION_ELEMENT;

   private static final Map<String, DatabaseHealthOperation> fixOps = new TreeMap<>();
   private static final Map<String, DatabaseHealthOperation> verifyOps = new TreeMap<>();

   public static Set<String> getFixOperationNames() {
      return getOperationNames(true);
   }

   public static Set<String> getVerifyOperationNames() {
      return getOperationNames(false);
   }

   private static Set<String> getOperationNames(boolean isFix) {
      checkExtensionsLoaded();
      return isFix ? fixOps.keySet() : verifyOps.keySet();
   }

   public static Collection<DatabaseHealthOperation> getFixOperations() {
      return getOperations(true);
   }

   public static Collection<DatabaseHealthOperation> getVerifyOperations() {
      return getOperations(false);
   }

   private static Collection<DatabaseHealthOperation> getOperations(boolean isFix) {
      checkExtensionsLoaded();
      return isFix ? fixOps.values() : verifyOps.values();
   }

   public static DatabaseHealthOperation getFixOperationByName(String name) {
      return getOperationByName(name, true);
   }

   public static DatabaseHealthOperation getVerifyOperationByName(String name) {
      return getOperationByName(name, false);
   }

   private static DatabaseHealthOperation getOperationByName(String name, boolean isFix) {
      checkExtensionsLoaded();
      return isFix ? fixOps.get(name) : verifyOps.get(name);
   }

   private static void checkExtensionsLoaded() {
      if (verifyOps.isEmpty() || fixOps.isEmpty()) {
         ExtensionDefinedObjects<DatabaseHealthOperation> extensionDefinedObjects =
            new ExtensionDefinedObjects<>(EXTENSION_POINT, EXTENSION_ELEMENT,
               CLASS_ATTRIBUTE_NAME);
         for (DatabaseHealthOperation operation : extensionDefinedObjects.getObjects()) {
            if (Strings.isValid(operation.getVerifyTaskName())) {
               verifyOps.put(operation.getVerifyTaskName(), operation);
            }
            if (Strings.isValid(operation.getFixTaskName())) {
               fixOps.put(operation.getFixTaskName(), operation);
            }
         }
      }
   }
}
