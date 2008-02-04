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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.FACTORY_ID_SEQ;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.FACTORY_TABLE;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.PersistenceManager;
import org.eclipse.osee.framework.skynet.core.PersistenceManagerInit;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;
import org.eclipse.osee.framework.ui.plugin.util.db.Query;
import org.osgi.framework.Bundle;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactFactoryCache implements PersistenceManager {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ArtifactFactoryCache.class);
   private static final String SELECT_FROM_FACTORY = "SELECT * FROM " + FACTORY_TABLE;
   private final HashMap<String, String> factoryBundleMap;
   private final HashMap<String, IArtifactFactory> factoryNameMap;
   private final HashMap<Integer, IArtifactFactory> factoryIdMap;
   private static final ArtifactFactoryCache instance = new ArtifactFactoryCache();

   private ArtifactFactoryCache() {
      this.factoryNameMap = new HashMap<String, IArtifactFactory>();
      this.factoryIdMap = new HashMap<Integer, IArtifactFactory>();
      this.factoryBundleMap = new HashMap<String, String>();
   }

   public static ArtifactFactoryCache getInstance() {
      PersistenceManagerInit.initManagerWeb(instance);
      return instance;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.PersistenceManager#onManagerWebInit()
    */
   public void onManagerWebInit() throws Exception {
      loadFactories();
   }

   public IArtifactFactory getFactoryFromId(int factoryId) {
      return factoryIdMap.get(factoryId);
   }

   public void reInitialize() throws SQLException {
      factoryNameMap.clear();
      factoryIdMap.clear();
      factoryBundleMap.clear();

      loadFactories();
   }

   private void loadFactories() throws SQLException {
      loadFactoryBundleMap();
      createFactoriesFromDB();
      registerNewFactories();
   }

   public IArtifactFactory getFactoryFromName(String factoryName) throws IllegalStateException {
      IArtifactFactory factory = factoryNameMap.get(factoryName);
      if (factory == null) {
         throw new IllegalStateException("Failed to retrieve factory: " + factoryName + " from artifact factory cache");
      }
      return factory;
   }

   public Artifact checkArtifactCache(int artId, TransactionId transactionId) {
      Artifact artifact = null;

      for (IArtifactFactory factory : factoryNameMap.values()) {
         artifact = factory.getArtifact(artId, transactionId);
         if (artifact != null) break;
      }
      return artifact;
   }

   public Artifact checkArtifactCache(String guid, TransactionId transactionId) {
      Artifact artifact = null;

      for (IArtifactFactory factory : factoryNameMap.values()) {
         artifact = factory.getArtifact(guid, transactionId);
         if (artifact != null) break;
      }
      return artifact;
   }

   /**
    * The method should only be used in circumstances where the factory class is not known until runtime. If it is known
    * at compile time, then just explicitly call the factory's getInstance.
    */
   private void createFactory(String factoryClassName, int factoryId) {
      try {
         String bundleSymbolicName = factoryBundleMap.get(factoryClassName);
         if (bundleSymbolicName == null) {
            logger.log(Level.WARNING, "No bundle associated with the factory class: " + factoryClassName);
            return;
         }

         Bundle bundle = Platform.getBundle(bundleSymbolicName);
         Method getInstance = bundle.loadClass(factoryClassName).getMethod("getInstance", new Class[] {int.class});
         IArtifactFactory factory = (IArtifactFactory) getInstance.invoke(null, new Object[] {factoryId});

         factoryNameMap.put(factoryClassName, factory);
         factoryIdMap.put(factoryId, factory);
      } catch (Exception ex) {
         logger.log(Level.SEVERE, "Unable to create factory: " + factoryClassName, ex);
      }
   }

   /**
    * calls getInstance for all the factories that are already regeistered with the DB
    */
   private void createFactoriesFromDB() throws SQLException {
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
      } finally {
         DbUtil.close(chStmt);
      }
   }

   /**
    * must be called after createFactoriesFromDB so we can determine if any of the factories extension points refer to a
    * factory that was not loaded from the DB
    * 
    * @throws SQLException
    */
   private void registerNewFactories() throws SQLException {
      for (String factoryClassName : factoryBundleMap.keySet()) {
         if (!factoryNameMap.containsKey(factoryClassName)) {

            int factoryId = Query.getNextSeqVal(null, FACTORY_ID_SEQ);

            ConnectionHandler.runPreparedUpdate(
                  "INSERT INTO " + FACTORY_TABLE + " (factory_id, factory_class) VALUES (?, ?)", SQL3DataType.INTEGER,
                  factoryId, SQL3DataType.VARCHAR, factoryClassName);

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