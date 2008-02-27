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
package org.eclipse.osee.framework.skynet.core.attribute;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.factory.IArtifactFactory;
import org.eclipse.osee.framework.ui.plugin.util.InputStreamImageDescriptor;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;

/**
 * Caches artifact subtype descriptors.
 * 
 * @see org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor
 * @author Robert A. Fisher
 */
public class ArtifactSubtypeDescriptorCache {
   private static final String SELECT_ARTIFACT_TYPES =
         "SELECT * FROM osee_define_artifact_type aty1, osee_define_factory fac2 WHERE aty1.factory_id = fac2.factory_id ORDER BY aty1.namespace, aty1.name";

   private final HashMap<String, ArtifactSubtypeDescriptor> nameToTypeMap;
   private final HashMap<Integer, ArtifactSubtypeDescriptor> idToartifactTypeMap;

   protected ArtifactSubtypeDescriptorCache() {
      this.nameToTypeMap = new HashMap<String, ArtifactSubtypeDescriptor>();
      this.idToartifactTypeMap = new HashMap<Integer, ArtifactSubtypeDescriptor>();
   }

   private synchronized void ensurePopulated() throws SQLException {
      if (idToartifactTypeMap.size() == 0) {
         populateCache();
      }
   }

   private void populateCache() throws SQLException {
      ConfigurationPersistenceManager configurationManager = ConfigurationPersistenceManager.getInstance();
      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt = ConnectionHandler.runPreparedQuery(SELECT_ARTIFACT_TYPES);

         ResultSet rSet = chStmt.getRset();
         while (rSet.next()) {
            try {
               IArtifactFactory factory = configurationManager.getFactoryFromName(rSet.getString("factory_class"));
               new ArtifactSubtypeDescriptor(this, rSet.getInt("art_type_id"), rSet.getString("factory_key"), factory,
                     rSet.getString("namespace"), rSet.getString("name"), new InputStreamImageDescriptor(
                           rSet.getBinaryStream("image")));
            } catch (IllegalStateException ex) {
               SkynetActivator.getLogger().log(Level.WARNING, ex.getLocalizedMessage(), ex);
            }
         }
      } finally {
         DbUtil.close(chStmt);
      }
   }

   /**
    * @return Returns all of the descriptors.
    * @throws SQLException
    */
   @Deprecated
   // should use ArtifactTypeValidityCache
   public Collection<ArtifactSubtypeDescriptor> getAllDescriptors() throws SQLException {
      ensurePopulated();
      return idToartifactTypeMap.values();
   }

   public boolean descriptorExists(String namespace, String name) throws SQLException {
      ensurePopulated();
      return nameToTypeMap.get(namespace + name) != null;
   }

   /**
    * @return Returns the descriptor with a particular namespace and name
    * @throws SQLException
    */
   public ArtifactSubtypeDescriptor getDescriptor(String namespace, String name) throws SQLException {
      ensurePopulated();
      ArtifactSubtypeDescriptor artifactType = nameToTypeMap.get(namespace + name);

      if (artifactType == null) {
         throw new IllegalArgumentException(
               "Atrifact type with namespace \"" + namespace + "\" and name \"" + name + "\" is not available.");
      }
      return artifactType;
   }

   /**
    * @return Returns the descriptor with a particular name (uses null for namespace), null if it does not exist.
    * @throws SQLException
    */
   public ArtifactSubtypeDescriptor getDescriptor(String name) throws SQLException {
      return getDescriptor("", name);
   }

   /**
    * @return Returns the descriptor with a particular name, null if it does not exist.
    * @throws SQLException
    */
   public ArtifactSubtypeDescriptor getDescriptor(int artTypeId) throws SQLException {
      ensurePopulated();

      ArtifactSubtypeDescriptor artifactType = idToartifactTypeMap.get(artTypeId);

      if (artifactType == null) {
         throw new IllegalArgumentException("Atrifact type: " + artTypeId + " is not available.");
      }
      return artifactType;
   }

   /**
    * Cache a newly created descriptor.
    * 
    * @param descriptor The descriptor to cache
    * @throws IllegalArgumentException if descriptor is null.
    */
   public void cache(ArtifactSubtypeDescriptor descriptor) {
      nameToTypeMap.put(descriptor.getNamespace() + descriptor.getName(), descriptor);
      idToartifactTypeMap.put(descriptor.getArtTypeId(), descriptor);
   }
}