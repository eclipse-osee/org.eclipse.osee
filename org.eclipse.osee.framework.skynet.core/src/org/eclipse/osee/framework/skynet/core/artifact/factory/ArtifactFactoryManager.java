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

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.FACTORY_ID_SEQ;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.core.query.Query;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.exception.OseeDataStoreException;
import org.osgi.framework.Bundle;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactFactoryManager {
   private static final String SELECT_FROM_FACTORY = "SELECT * FROM osee_define_factory";
   private final HashMap<String, String> factoryBundleMap = new HashMap<String, String>();
   private final HashMap<String, ArtifactFactory> factoryNameMap = new HashMap<String, ArtifactFactory>();
   private final HashMap<Integer, ArtifactFactory> factoryIdMap = new HashMap<Integer, ArtifactFactory>();
   private static final ArtifactFactoryManager instance = new ArtifactFactoryManager();

   private ArtifactFactoryManager() {
   }

   public static ArtifactFactory getFactoryFromName(String factoryName) throws IllegalStateException, OseeDataStoreException {
      ensurePopulated();
      ArtifactFactory factory = instance.factoryNameMap.get(factoryName);
      if (factory == null) {
         throw new IllegalStateException("Failed to retrieve factory: " + factoryName + " from artifact factory cache");
      }
      return factory;
   }

   public static ArtifactFactory getFactoryFromId(int factoryId) throws OseeDataStoreException {
      ensurePopulated();
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
            SkynetActivator.getLogger().log(Level.WARNING,
                  "No bundle associated with the factory class: " + factoryClassName);
            return;
         }

         Bundle bundle = Platform.getBundle(bundleSymbolicName);
         Method getInstance = bundle.loadClass(factoryClassName).getMethod("getInstance", int.class);
         ArtifactFactory factory = (ArtifactFactory) getInstance.invoke(null, new Object[] {factoryId});

         factoryNameMap.put(factoryClassName, factory);
         factoryIdMap.put(factoryId, factory);
      } catch (Exception ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, "Unable to create factory: " + factoryClassName, ex);
      }
   }

   /**
    * calls getInstance for all the factories that are already registered with the DB
    * 
    * @throws OseeDataStoreException
    */
   private void createFactoriesFromDB() throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt = ConnectionHandler.runPreparedQuery(SELECT_FROM_FACTORY);
         ResultSet rset = chStmt.getRset();
         while (rset.next()) {
            String factoryClassName = null;
            factoryClassName = rset.getString("factory_class");
            int factoryId = rset.getInt("factory_id");
            createFactory(factoryClassName, factoryId);
         }
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      } finally {
         DbUtil.close(chStmt);
      }
   }

   /**
    * must be called after createFactoriesFromDB so we can determine if any of the factories extension points refer to a
    * factory that was not loaded from the DB
    * 
    * @throws OseeDataStoreException
    * @throws SQLException
    */
   private void registerNewFactories() throws OseeDataStoreException {
      try {
         for (String factoryClassName : factoryBundleMap.keySet()) {
            if (!factoryNameMap.containsKey(factoryClassName)) {

               int factoryId = Query.getNextSeqVal(FACTORY_ID_SEQ);

               ConnectionHandler.runPreparedUpdate(
                     "INSERT INTO osee_define_factory (factory_id, factory_class) VALUES (?, ?)", SQL3DataType.INTEGER,
                     factoryId, SQL3DataType.VARCHAR, factoryClassName);

               createFactory(factoryClassName, factoryId);
            }
         }
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
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