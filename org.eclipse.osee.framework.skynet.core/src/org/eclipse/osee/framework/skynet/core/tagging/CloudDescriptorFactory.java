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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TAG_CLOUD_TYPE_TABLE;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.Query;
import org.eclipse.osee.framework.ui.plugin.util.db.RsetProcessor;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase;

/**
 * Cloud descriptor factory.
 * 
 * @author Robert A. Fisher
 */
/*default*/class CloudDescriptorFactory {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(CloudDescriptorFactory.class);
   private static final String SELECT_CLOUD_TYPES =
         "SELECT " + TAG_CLOUD_TYPE_TABLE.columns("tag_cloud_name", "cloud_type_id") + " FROM " + TAG_CLOUD_TYPE_TABLE;
   private static final String INSERT_CLOUD_TYPE =
         "INSERT INTO " + TAG_CLOUD_TYPE_TABLE + " (tag_cloud_name, cloud_type_id) VALUES (?, ?)";
   private Map<String, CloudDescriptor> descriptors;

   /**
    * 
    */
   public CloudDescriptorFactory() {
      this.descriptors = null;
   }

   /**
    * @return Returns the descriptors.
    */
   public Collection<CloudDescriptor> getDescriptors() {
      checkPopulated();

      return descriptors.values();
   }

   /**
    * @param descriptorName
    * @return Returns a descriptor if it exists otherwise null.
    */
   public CloudDescriptor getDescriptor(String descriptorName) {
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
   public CloudDescriptor createDescriptor(String descriptorName) {
      checkPopulated();

      if (descriptors.containsKey(descriptorName)) throw new IllegalArgumentException(
            descriptorName + " cloud descriptor already exists.");

      CloudDescriptor cloudDescriptor = null;

      try {
         cloudDescriptor =
               new CloudDescriptor(descriptorName, Query.getNextSeqVal(ConnectionHandler.getConnection(),
                     SkynetDatabase.CLOUD_TYPE_ID_SEQ));
         ConnectionHandler.runPreparedQuery(INSERT_CLOUD_TYPE, SQL3DataType.VARCHAR, cloudDescriptor.getName(),
               SQL3DataType.INTEGER, cloudDescriptor.getCloudTypeId());

         cache(cloudDescriptor);
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
      return cloudDescriptor;
   }

   private void checkPopulated() {
      if (descriptors == null) {
         descriptors = new HashMap<String, CloudDescriptor>();

         try {
            List<CloudDescriptor> tempDescriptors = new LinkedList<CloudDescriptor>();
            Query.acquireCollection(tempDescriptors, SELECT_CLOUD_TYPES, new CloudDescriptorProcessor());

            for (CloudDescriptor cloudDescriptor : tempDescriptors) {
               cache(cloudDescriptor);
            }
         } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
         }
      }
   }

   private void cache(CloudDescriptor cloudDescriptor) {
      descriptors.put(cloudDescriptor.getName(), cloudDescriptor);
   }

   private static class CloudDescriptorProcessor implements RsetProcessor<CloudDescriptor> {

      public CloudDescriptor process(ResultSet set) throws SQLException {
         return new CloudDescriptor(set.getString("tag_cloud_name"), set.getInt("cloud_type_id"));
      }

      public boolean validate(CloudDescriptor item) {
         return item != null;
      }
   }
}
