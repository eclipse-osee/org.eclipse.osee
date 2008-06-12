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
package org.eclipse.osee.framework.skynet.core.tagging;

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TAG_TYPE_TABLE;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.core.RsetProcessor;
import org.eclipse.osee.framework.db.connection.core.query.Query;
import org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;

/**
 * Tag descriptor factory.
 * 
 * @author Robert A. Fisher
 */
/*default*/class TagDescriptorFactory {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(TagDescriptorFactory.class);
   private static final String SELECT_TAG_TYPES =
         "SELECT " + TAG_TYPE_TABLE.aliasAs("t1").columns("tag_type_name", "tag_type_id") + " FROM " + TAG_TYPE_TABLE.aliasAs("t1");
   private static final String INSERT_TAG_TYPE =
         "INSERT INTO " + TAG_TYPE_TABLE + " (tag_type_name, tag_type_id) VALUES (?, ?)";
   private Map<String, TagDescriptor> descriptors;

   /**
    * 
    */
   public TagDescriptorFactory() {
      this.descriptors = null;
   }

   /**
    * @return Returns the descriptors.
    */
   public Collection<TagDescriptor> getDescriptors() {
      checkPopulated();

      return descriptors.values();
   }

   /**
    * @param descriptorName
    * @return Returns a descriptor if it exists otherwise null.
    */
   public TagDescriptor getDescriptor(String descriptorName) {
      checkPopulated();

      return descriptors.get(descriptorName);
   }

   /**
    * (
    * 
    * @param descriptorName
    * @return Returns a new descriptor.
    * @throws IllegalArgumentException if the descriptor already exists.
    */
   public TagDescriptor createDescriptor(String descriptorName) {
      checkPopulated();

      if (descriptors.containsKey(descriptorName)) throw new IllegalArgumentException(
            descriptorName + " tag descriptor already exists.");

      TagDescriptor tagDescriptor = null;

      try {
         tagDescriptor = new TagDescriptor(descriptorName, Query.getNextSeqVal(SkynetDatabase.TAG_TYPE_ID_SEQ));
         ConnectionHandler.runPreparedUpdate(true, INSERT_TAG_TYPE, SQL3DataType.VARCHAR, tagDescriptor.getName(),
               SQL3DataType.INTEGER, tagDescriptor.getTagTypeId());

         cache(tagDescriptor);
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
      return tagDescriptor;
   }

   private void checkPopulated() {
      if (descriptors == null) {
         descriptors = new HashMap<String, TagDescriptor>();

         try {
            List<TagDescriptor> tempDescriptors = new LinkedList<TagDescriptor>();
            Query.acquireCollection(tempDescriptors, SELECT_TAG_TYPES, new CloudDescriptorProcessor());

            for (TagDescriptor tagDescriptor : tempDescriptors) {
               cache(tagDescriptor);
            }
         } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
         }
      }
   }

   private void cache(TagDescriptor tagDescriptor) {
      descriptors.put(tagDescriptor.getName(), tagDescriptor);
   }

   private static class CloudDescriptorProcessor implements RsetProcessor<TagDescriptor> {

      public TagDescriptor process(ResultSet set) throws SQLException {
         return new TagDescriptor(set.getString("tag_type_name"), set.getInt("tag_type_id"));
      }

      public boolean validate(TagDescriptor item) {
         return item != null;
      }
   }
}
