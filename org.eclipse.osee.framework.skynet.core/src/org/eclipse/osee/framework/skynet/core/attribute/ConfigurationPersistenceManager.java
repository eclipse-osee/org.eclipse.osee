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

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ARTIFACT_TYPE_TABLE;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.core.query.Query;
import org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.PersistenceManager;
import org.eclipse.osee.framework.skynet.core.PersistenceManagerInit;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeValidityCache;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactoryCache;
import org.eclipse.osee.framework.ui.plugin.util.InputStreamImageDescriptor;

/**
 * @author Jeff C. Phillips
 */
public class ConfigurationPersistenceManager implements PersistenceManager {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ConfigurationPersistenceManager.class);

   private static final String INSERT_ARTIFACT_TYPE =
         "INSERT INTO osee_define_artifact_type (art_type_id, factory_id, namespace, name, factory_key, image) VALUES (?,?,?,?,?,?)";
   private static final String INSERT_VALID_ATTRIBUTE =
         "INSERT INTO osee_define_valid_attributes (art_type_id, attr_type_id) VALUES (?, ?)";

   private final ArtifactSubtypeDescriptorCache cacheArtifactSubtypeDescriptors;
   private final AttributeTypeValidityCache cacheAttributeTypeValidity;
   private final ArtifactTypeValidityCache artifactTypeValidityCache;

   private ArtifactFactoryCache artifactFactoryCache;
   private HashMap<String, Pair<String, String>> imageMap;
   private static Pair<String, String> defaultIconLocation =
         new Pair<String, String>("org.eclipse.osee.framework.skynet.core", "images/laser_16_16.gif");

   private static final ConfigurationPersistenceManager instance = new ConfigurationPersistenceManager();

   private ConfigurationPersistenceManager() {
      super();
      cacheArtifactSubtypeDescriptors = new ArtifactSubtypeDescriptorCache();
      cacheAttributeTypeValidity = new AttributeTypeValidityCache();
      artifactTypeValidityCache = new ArtifactTypeValidityCache();
   }

   public static ConfigurationPersistenceManager getInstance() {
      PersistenceManagerInit.initManagerWeb(instance);
      return instance;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.PersistenceManager#setRelatedManagers()
    */
   public void onManagerWebInit() throws Exception {
      artifactFactoryCache = ArtifactFactoryCache.getInstance();
   }

   public void makeSubtypePersistent(String factoryName, String namespace, String artifactTypeName, String factoryKey) throws SQLException {
      if (!cacheArtifactSubtypeDescriptors.typeExists(namespace, artifactTypeName)) {
         int artTypeId = Query.getNextSeqVal(null, SkynetDatabase.ART_TYPE_ID_SEQ);
         InputStreamImageDescriptor imageDescriptor = getDefaultImageDescriptor(artifactTypeName);
         ArtifactFactory factory = artifactFactoryCache.getFactoryFromName(factoryName);

         ConnectionHandler.runPreparedUpdate(INSERT_ARTIFACT_TYPE, SQL3DataType.INTEGER, artTypeId,
               SQL3DataType.INTEGER, factory.getFactoryId(), SQL3DataType.VARCHAR, namespace, SQL3DataType.VARCHAR,
               artifactTypeName, SQL3DataType.VARCHAR, factoryKey, SQL3DataType.BLOB, new ByteArrayInputStream(
                     imageDescriptor.getData()));

         new ArtifactType(cacheArtifactSubtypeDescriptors, artTypeId, factoryKey, factory, namespace,
               artifactTypeName, imageDescriptor);
      } else {
         // Check if anything valuable is different
         ArtifactType artifactType =
               cacheArtifactSubtypeDescriptors.getDescriptor(namespace, artifactTypeName);
         if (!artifactType.getFactoryKey().equals(factoryKey) || !artifactType.getFactory().getClass().getCanonicalName().equals(
               factoryName)) {
            // update factory information
         }
      }
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
            logger.log(Level.WARNING, "No image was defined for art type [" + typeName + "]");
            imagelocation = defaultIconLocation;
         }
         URL url = getUrl(imagelocation);
         if (url == null) {
            logger.log(Level.WARNING,
                  "Unable to get url for type [" + typeName + "] bundle path " + imagelocation.getValue());
            url = getUrl(defaultIconLocation);
         }
         imageDescriptor = new InputStreamImageDescriptor(url.openStream());
      } catch (Exception ex) {
         logger.log(Level.SEVERE, "Icon for Artifact type " + typeName + " not found.", ex);
         imageDescriptor = new InputStreamImageDescriptor(new byte[0]);
      }

      return imageDescriptor;
   }

   private URL getUrl(Pair<String, String> location) {
      return Platform.getBundle(location.getKey()).getEntry(location.getValue());
   }

   public static void updateArtifactTypeImage(ArtifactType descriptor, InputStreamImageDescriptor imageDescriptor) throws SQLException {
      // Update DB
      ConnectionHandler.runPreparedUpdate("UPDATE " + ARTIFACT_TYPE_TABLE + " SET image = ? where art_type_id = ?",
            SQL3DataType.BLOB, new ByteArrayInputStream(imageDescriptor.getData()), SQL3DataType.INTEGER,
            descriptor.getArtTypeId());

      // TODO Update descriptor's cached copy of image
      descriptor.setImageDescriptor(imageDescriptor);
   }

   /**
    * Persists that a particular user defined attribute is valid for some artifact type.
    * 
    * @param attributeType
    * @param artifactType
    * @throws Exception
    */
   public void persistAttributeValidity(ArtifactType artifactType, AttributeType attributeType) throws Exception {
      if (!cacheAttributeTypeValidity.isValid(artifactType, attributeType)) {
         ConnectionHandler.runPreparedUpdate(INSERT_VALID_ATTRIBUTE, SQL3DataType.INTEGER, artifactType.getArtTypeId(),
               SQL3DataType.INTEGER, attributeType.getAttrTypeId());

         cacheAttributeTypeValidity.add(artifactType, attributeType);
      }
   }

   public Collection<ArtifactType> getArtifactTypesFromAttributeType(AttributeType attributeType) throws SQLException {
      return cacheAttributeTypeValidity.getArtifactTypesFromAttributeType(attributeType);
   }

   public Collection<AttributeType> getAttributeTypesFromArtifactType(String artifactTypeName, Branch branch) throws SQLException {
      return cacheAttributeTypeValidity.getAttributeTypesFromArtifactType(
            getArtifactSubtypeDescriptor(artifactTypeName), branch);
   }

   public Collection<AttributeType> getAttributeTypesFromArtifactType(ArtifactType descriptor, Branch branch) throws SQLException {
      return cacheAttributeTypeValidity.getAttributeTypesFromArtifactType(descriptor, branch);
   }

   public Collection<ArtifactType> getValidArtifactTypes(Branch branch) throws SQLException {
      return artifactTypeValidityCache.getValidArtifactTypes(branch);
   }

   public ArtifactType getArtifactSubtypeDescriptor(String name) throws SQLException {
      return cacheArtifactSubtypeDescriptors.getDescriptor(name);
   }

   public boolean artifactTypeExists(String namespace, String name) throws SQLException {
      return cacheArtifactSubtypeDescriptors.typeExists(namespace, name);
   }

   @Deprecated
   public Collection<ArtifactType> getArtifactSubtypeDescriptors() throws SQLException {
      return cacheArtifactSubtypeDescriptors.getAllDescriptors();
   }

   public Set<String> getValidEnumerationAttributeValues(String attributeName, Branch branch) {
      Set<String> names = new HashSet<String>();
      try {
         AttributeType dad = AttributeTypeManager.getType(attributeName);
         String str = dad.getValidityXml();
         Matcher m = Pattern.compile("<Enum>(.*?)</Enum>").matcher(str);
         while (m.find())
            names.add(m.group(1));
      } catch (Exception ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, "Error getting valid enumeration values", ex);
      }
      return names;
   }

   public ArtifactType getArtifactSubtypeDescriptor(int artTypeId) throws SQLException {
      return cacheArtifactSubtypeDescriptors.getDescriptor(artTypeId);
   }

   public ArtifactFactory getFactoryFromId(int factoryId) {
      return artifactFactoryCache.getFactoryFromId(factoryId);
   }

   public ArtifactFactory getFactoryFromName(String factoryName) throws IllegalStateException {
      return artifactFactoryCache.getFactoryFromName(factoryName);
   }
}
