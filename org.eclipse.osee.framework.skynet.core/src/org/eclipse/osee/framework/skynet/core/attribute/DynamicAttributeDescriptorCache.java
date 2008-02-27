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
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;

/**
 * Caches all attribute descriptors during persistence manager startup.
 * 
 * @see org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor
 * @author Robert A. Fisher
 */
public class DynamicAttributeDescriptorCache {
   private static final String SELECT_ATTRIBUTE_TYPES =
         "SELECT * FROM osee_define_attribute_type aty1, osee_define_attr_base_type aby2 WHERE aty1.attr_base_type_id = aby2.attr_base_type_id";

   private final HashMap<String, DynamicAttributeDescriptor> nameToTypeMap;
   private final HashMap<Integer, DynamicAttributeDescriptor> idToTypeMap;

   protected DynamicAttributeDescriptorCache() {
      nameToTypeMap = new HashMap<String, DynamicAttributeDescriptor>();
      idToTypeMap = new HashMap<Integer, DynamicAttributeDescriptor>();
   }

   private synchronized void ensurePopulated() throws SQLException {
      if (idToTypeMap.size() == 0) {
         populateCache();
      }
   }

   private void populateCache() throws SQLException {
      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt = ConnectionHandler.runPreparedQuery(SELECT_ATTRIBUTE_TYPES);

         ResultSet rSet = chStmt.getRset();
         while (rSet.next()) {
            String baseClassString = rSet.getString("attribute_class");

            try {
               Class<? extends Attribute> baseClass = Class.forName(baseClassString).asSubclass(Attribute.class);
               new DynamicAttributeDescriptor(this, rSet.getInt("attr_type_id"), baseClass,
                     rSet.getString("namespace"), rSet.getString("name"), rSet.getString("default_value"),
                     rSet.getString("validity_xml"), rSet.getInt("min_occurence"), rSet.getInt("max_occurence"),
                     rSet.getString("tip_text"));

            } catch (IllegalStateException ex) {
               SkynetActivator.getLogger().log(Level.WARNING, ex.getLocalizedMessage(), ex);
            } catch (ClassNotFoundException ex) {
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
   //use attribute validitiy to get attributes by branch
   public Collection<DynamicAttributeDescriptor> getAllDescriptors(Branch branch) throws SQLException {
      ensurePopulated();
      return idToTypeMap.values();
   }

   public boolean descriptorExists(String namespace, String name) throws SQLException {
      ensurePopulated();
      return nameToTypeMap.get(namespace + name) != null;
   }

   /**
    * @return Returns the descriptor with a particular namespace and name, null if it does not exist.
    * @throws SQLException
    */
   public DynamicAttributeDescriptor getDescriptor(String namespace, String name) throws SQLException {
      ensurePopulated();
      DynamicAttributeDescriptor attributeType = nameToTypeMap.get(namespace + name);
      if (attributeType == null) {
         throw new IllegalArgumentException(
               "Attribute with namespace \"" + namespace + "\" and name \"" + name + "\" is not available.");
      }
      return attributeType;
   }

   /**
    * @return Returns the descriptor with a particular name, null if it does not exist.
    * @throws SQLException
    */
   public DynamicAttributeDescriptor getDescriptor(int attrTypeId) throws SQLException {
      ensurePopulated();
      DynamicAttributeDescriptor attributeType = idToTypeMap.get(attrTypeId);
      if (attributeType == null) {
         throw new IllegalArgumentException("Attribute type: " + attrTypeId + " is not available.");
      }
      return attributeType;
   }

   /**
    * @return Returns the descriptor with a particular name, null if it does not exist.
    * @throws SQLException
    */
   public DynamicAttributeDescriptor getDescriptor(String name) throws SQLException {
      return getDescriptor("", name);
   }

   /**
    * Cache a newly created descriptor.
    * 
    * @param descriptor The descriptor to cache
    * @throws SQLException
    * @throws IllegalArgumentException if descriptor is null.
    */
   public void cache(DynamicAttributeDescriptor descriptor) throws SQLException {
      nameToTypeMap.put(descriptor.getNamespace() + descriptor.getName(), descriptor);
      idToTypeMap.put(descriptor.getAttrTypeId(), descriptor);
   }
}