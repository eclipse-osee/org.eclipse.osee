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

package org.eclipse.osee.framework.skynet.core.artifact;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.core.query.Query;
import org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactoryManager;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.ui.plugin.util.InputStreamImageDescriptor;

/**
 * Contains methods specific to artifact types. All artifact methods will eventually be moved from the
 * ConfigurationPersistenceManager to here.
 * 
 * @author Donald G. Dunne
 */
public class ArtifactTypeManager {
   private static final String SELECT_ARTIFACT_TYPES = "SELECT * FROM osee_define_artifact_type";
   private static final String INSERT_ARTIFACT_TYPE =
         "INSERT INTO osee_define_artifact_type (art_type_id, factory_id, namespace, name, factory_key, image) VALUES (?,?,?,?,?,?)";
   private HashMap<String, Pair<String, String>> imageMap;

   private static Pair<String, String> defaultIconLocation =
         new Pair<String, String>("org.eclipse.osee.framework.skynet.core", "images/laser_16_16.gif");

   private static final ArtifactTypeManager instance = new ArtifactTypeManager();

   private final HashMap<String, ArtifactType> nameToTypeMap = new HashMap<String, ArtifactType>();
   private final HashMap<Integer, ArtifactType> idToTypeMap = new HashMap<Integer, ArtifactType>();

   private ArtifactTypeManager() {
   }

   public void refreshCache() throws OseeDataStoreException {
      nameToTypeMap.clear();
      idToTypeMap.clear();
      populateCache();
   }

   private static synchronized void ensurePopulated() throws SQLException {
      if (instance.idToTypeMap.size() == 0) {
         try {
            instance.populateCache();
         } catch (OseeDataStoreException ex) {
            throw new SQLException(ex);
         }
      }
   }

   private void populateCache() throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt = ConnectionHandler.runPreparedQuery(SELECT_ARTIFACT_TYPES);

         ResultSet rSet = chStmt.getRset();
         while (rSet.next()) {
            try {
               ArtifactFactory factory = ArtifactFactoryManager.getFactoryFromId(rSet.getInt("factory_id"));
               new ArtifactType(rSet.getInt("art_type_id"), rSet.getString("factory_key"), factory,
                     rSet.getString("namespace"), rSet.getString("name"), new InputStreamImageDescriptor(
                           rSet.getBinaryStream("image")));
            } catch (IllegalStateException ex) {
               SkynetActivator.getLogger().log(Level.WARNING, ex.getLocalizedMessage(), ex);
            }
         }
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
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
   public static Collection<ArtifactType> getAllTypes() throws SQLException {
      ensurePopulated();
      return instance.idToTypeMap.values();
   }

   public static boolean typeExists(String namespace, String name) throws SQLException {
      ensurePopulated();
      return instance.nameToTypeMap.get(namespace + name) != null;
   }

   /**
    * Cache a newly created descriptor.
    * 
    * @param descriptor The descriptor to cache
    * @throws IllegalArgumentException if descriptor is null.
    */
   static void cache(ArtifactType descriptor) {
      instance.nameToTypeMap.put(descriptor.getNamespace() + descriptor.getName(), descriptor);
      instance.idToTypeMap.put(descriptor.getArtTypeId(), descriptor);
   }

   /**
    * @return Returns the descriptor with a particular namespace and name
    * @throws SQLException
    */
   public static ArtifactType getType(String namespace, String name) throws SQLException {
      ensurePopulated();
      ArtifactType artifactType = instance.nameToTypeMap.get(namespace + name);

      if (artifactType == null) {
         throw new IllegalArgumentException(
               "Artifact type with namespace \"" + namespace + "\" and name \"" + name + "\" is not available.");
      }
      return artifactType;
   }

   /**
    * @return Returns the type with a particular name (uses null for namespace), null if it does not exist.
    * @throws SQLException
    */
   public static ArtifactType getType(String artifactTypeName) throws SQLException {
      return getType("", artifactTypeName);
   }

   /**
    * @return Returns the types with a particular name (uses null for namespace), null if it does not exist.
    * @throws SQLException
    */
   public static List<ArtifactType> getTypes(Collection<String> artifactTypeNames) throws SQLException {
      List<ArtifactType> artifactTypes = new ArrayList<ArtifactType>(artifactTypeNames.size());
      for (String artifactTypeName : artifactTypeNames) {
         artifactTypes.add(getType("", artifactTypeName));
      }
      return artifactTypes;
   }

   /**
    * @return Returns the descriptor with a particular name, null if it does not exist.
    * @throws SQLException
    */
   public static ArtifactType getType(int artTypeId) throws SQLException {
      ensurePopulated();

      ArtifactType artifactType = instance.idToTypeMap.get(artTypeId);

      if (artifactType == null) {
         throw new IllegalArgumentException("Atrifact type: " + artTypeId + " is not available.");
      }
      return artifactType;
   }

   /**
    * Get a new instance of type artifactTypeName
    * 
    * @param artifactTypeName
    * @param branch
    * @return
    * @throws SQLException
    * @throws OseeCoreException
    */
   public static Artifact addArtifact(String artifactTypeName, Branch branch) throws SQLException, OseeCoreException {
      return ArtifactTypeManager.getType(artifactTypeName).makeNewArtifact(branch);
   }

   /**
    * Get a new instance of type artifactTypeName and set it's name.
    * 
    * @param artifactTypeName
    * @param branch
    * @param name
    * @return
    * @throws SQLException
    */
   public static Artifact addArtifact(String artifactTypeName, Branch branch, String name) throws SQLException {
      Artifact artifact;
      try {
         artifact = addArtifact(artifactTypeName, branch);
      } catch (OseeCoreException ex) {
         throw new SQLException(ex);
      }
      artifact.setDescriptiveName(name);
      return artifact;
   }

   /**
    * Get a new instance of the type of artifact. This is just a convenience method that calls makeNewArtifact on the
    * known factory with this descriptor for the descriptor parameter, and the supplied branch.
    * 
    * @param branch branch on which artifact will be created
    * @return Return artifact reference
    * @throws SQLException
    * @throws OseeCoreException
    * @see ArtifactFactory#makeNewArtifact(Branch, ArtifactType, String, String, ArtifactProcessor)
    */
   public static Artifact addArtifact(String artifactTypeName, Branch branch, String guid, String humandReadableId) throws SQLException, OseeCoreException {
      return ArtifactTypeManager.getType(artifactTypeName).makeNewArtifact(branch, guid, humandReadableId);
   }

   public static void updateArtifactTypeImage(ArtifactType descriptor, InputStreamImageDescriptor imageDescriptor) throws SQLException {
      ConnectionHandler.runPreparedUpdate("UPDATE osee_define_artifact_type SET image = ? where art_type_id = ?",
            SQL3DataType.BLOB, new ByteArrayInputStream(imageDescriptor.getData()), SQL3DataType.INTEGER,
            descriptor.getArtTypeId());

      // TODO Update descriptor's cached copy of image
      descriptor.setImageDescriptor(imageDescriptor);
   }

   public static ArtifactType createType(String factoryName, String namespace, String artifactTypeName, String factoryKey) throws SQLException, IllegalStateException, OseeDataStoreException {
      ArtifactType artifactType;
      if (!typeExists(namespace, artifactTypeName)) {
         int artTypeId = Query.getNextSeqVal(SkynetDatabase.ART_TYPE_ID_SEQ);
         InputStreamImageDescriptor imageDescriptor = instance.getDefaultImageDescriptor(artifactTypeName);
         ArtifactFactory factory = ArtifactFactoryManager.getFactoryFromName(factoryName);

         ConnectionHandler.runPreparedUpdate(INSERT_ARTIFACT_TYPE, SQL3DataType.INTEGER, artTypeId,
               SQL3DataType.INTEGER, factory.getFactoryId(), SQL3DataType.VARCHAR, namespace, SQL3DataType.VARCHAR,
               artifactTypeName, SQL3DataType.VARCHAR, factoryKey, SQL3DataType.BLOB, new ByteArrayInputStream(
                     imageDescriptor.getData()));

         artifactType = new ArtifactType(artTypeId, factoryKey, factory, namespace, artifactTypeName, imageDescriptor);
      } else {
         // Check if anything valuable is different
         artifactType = getType(namespace, artifactTypeName);
         if (!artifactType.getFactoryKey().equals(factoryKey) || !artifactType.getFactory().getClass().getCanonicalName().equals(
               factoryName)) {
            // update factory information
         }
      }
      return artifactType;
   }

   private InputStreamImageDescriptor getDefaultImageDescriptor(String typeName) {
      if (imageMap == null) {
         imageMap = new HashMap<String, Pair<String, String>>();
         IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
         if (extensionRegistry != null) {
            IExtensionPoint point =
                  extensionRegistry.getExtensionPoint("org.eclipse.osee.framework.skynet.core.ArtifactTypeImage");
            if (point != null) {
               IExtension[] extensions = point.getExtensions();
               for (IExtension extension : extensions) {
                  IConfigurationElement[] elements = extension.getConfigurationElements();
                  String artifact = null;
                  String path = null;
                  String bundle = null;
                  for (IConfigurationElement el : elements) {
                     if (el.getName().equals("ArtifactImage")) {
                        artifact = el.getAttribute("ArtifactTypeName");
                        path = el.getAttribute("ImagePath");
                        bundle = el.getContributor().getName();
                        imageMap.put(artifact, new Pair<String, String>(bundle, path));
                     }
                  }
               }
            }
         }
      }
      InputStreamImageDescriptor imageDescriptor;
      try {
         Pair<String, String> imagelocation = imageMap.get(typeName);
         if (imagelocation == null) {
            SkynetActivator.getLogger().log(Level.WARNING, "No image was defined for art type [" + typeName + "]");
            imagelocation = defaultIconLocation;
         }
         URL url = getUrl(imagelocation);
         if (url == null) {
            SkynetActivator.getLogger().log(Level.WARNING,
                  "Unable to get url for type [" + typeName + "] bundle path " + imagelocation.getValue());
            url = getUrl(defaultIconLocation);
         }
         imageDescriptor = new InputStreamImageDescriptor(url.openStream());
      } catch (Exception ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, "Icon for Artifact type " + typeName + " not found.", ex);
         imageDescriptor = new InputStreamImageDescriptor(new byte[0]);
      }

      return imageDescriptor;
   }

   private URL getUrl(Pair<String, String> location) {
      return Platform.getBundle(location.getKey()).getEntry(location.getValue());
   }
}