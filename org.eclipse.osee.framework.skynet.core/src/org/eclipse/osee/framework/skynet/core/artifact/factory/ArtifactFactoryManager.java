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
package org.eclipse.osee.framework.skynet.core.artifact.factory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.osgi.framework.Bundle;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactFactoryManager {
   private static final String SELECT_FROM_FACTORY = "SELECT * FROM osee_artifact_factory";
   private final HashMap<String, String> factoryBundleMap = new HashMap<String, String>();
   private final HashMap<String, ArtifactFactory> factoryNameMap = new HashMap<String, ArtifactFactory>();
   private final HashMap<Integer, ArtifactFactory> factoryIdMap = new HashMap<Integer, ArtifactFactory>();
   private static final ArtifactFactoryManager instance = new ArtifactFactoryManager();

   private ArtifactFactoryManager() {
   }

   public static ArtifactFactory getFactoryFromName(String factoryName) throws OseeDataStoreException {
      ensurePopulated();
      ArtifactFactory factory = instance.factoryNameMap.get(factoryName);
      if (factory == null) {
         throw new OseeDataStoreException("Failed to retrieve factory: " + factoryName + " from artifact factory cache");
      }
      return factory;
   }

   public static ArtifactFactory getFactoryFromId(int factoryId) throws OseeDataStoreException {
      ensurePopulated();
      ArtifactFactory factory = instance.factoryIdMap.get(factoryId);
      if (factory == null) {
         throw new OseeDataStoreException(
               "Failed to retrieve factory id: " + factoryId + " from artifact factory cache");
      }
      return instance.factoryIdMap.get(factoryId);
   }

   public static void refreshCache() throws OseeDataStoreException {
      instance.factoryNameMap.clear();
      instance.factoryIdMap.clear();
      instance.factoryBundleMap.clear();

      instance.populateCache();
   }

   private static synchronized void ensurePopulated() throws OseeDataStoreException {
      if (instance.factoryIdMap.size() == 0) {
         instance.populateCache();
      }
   }

   private void populateCache() throws OseeDataStoreException {
      loadFactoryBundleMap();
      createFactoriesFromDB();
      if (SkynetDbInit.isDbInit()) {
         registerNewFactories();
      }
   }

   /**
    * The method should only be used in circumstances where the factory class is not known until runtime. If it is known
    * at compile time, then just explicitly call the factory's getInstance.
    */
   private void createFactory(String factoryClassName, int factoryId) {
      try {
         String bundleSymbolicName = factoryBundleMap.get(factoryClassName);
         if (bundleSymbolicName == null) {
            OseeLog.log(SkynetActivator.class, Level.WARNING,
                  "No bundle associated with the factory class: " + factoryClassName);
            return;
         }

         Bundle bundle = Platform.getBundle(bundleSymbolicName);
         Method getInstance = bundle.loadClass(factoryClassName).getMethod("getInstance", int.class);
         ArtifactFactory factory = (ArtifactFactory) getInstance.invoke(null, new Object[] {factoryId});

         factoryNameMap.put(factoryClassName, factory);
         factoryIdMap.put(factoryId, factory);
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, "Unable to create factory: " + factoryClassName, ex);
      }
   }

   /**
    * calls getInstance for all the factories that are already registered with the DB
    * 
    * @throws OseeDataStoreException
    */
   private void createFactoriesFromDB() throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      try {
         chStmt.runPreparedQuery(SELECT_FROM_FACTORY);
         while (chStmt.next()) {
            String factoryClassName = null;
            factoryClassName = chStmt.getString("factory_class");
            int factoryId = chStmt.getInt("factory_id");
            createFactory(factoryClassName, factoryId);
         }
      } finally {
         chStmt.close();
      }
   }

   /**
    * must be called after createFactoriesFromDB so we can determine if any of the factories extension points refer to a
    * factory that was not loaded from the DB
    * 
    * @throws OseeDataStoreException
    */
   private void registerNewFactories() throws OseeDataStoreException {
      for (String factoryClassName : factoryBundleMap.keySet()) {
         if (!factoryNameMap.containsKey(factoryClassName)) {

            int factoryId = SequenceManager.getNextFactoryId();

            ConnectionHandler.runPreparedUpdate(
                  "INSERT INTO osee_artifact_factory (factory_id, factory_class) VALUES (?, ?)", factoryId,
                  factoryClassName);

            createFactory(factoryClassName, factoryId);
         }
      }
   }

   private void loadFactoryBundleMap() {
      List<IConfigurationElement> elements =
            ExtensionPoints.getExtensionElements("org.eclipse.osee.framework.skynet.core.ArtifactFactory",
                  "ArtifactFactory");

      for (IConfigurationElement element : elements) {
         factoryBundleMap.put(element.getAttribute("classname"), element.getContributor().getName());
      }
   }
}