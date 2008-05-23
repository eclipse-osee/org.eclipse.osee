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
package org.eclipse.osee.framework.skynet.core.artifact.search;

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ARTIFACT_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ARTIFACT_TYPE_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTRIBUTE_TYPE_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.VALID_ATTRIBUTES_TABLE;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Ryan D. Brooks
 */
public class CorruptedArtifactSearch implements ISearchPrimitive {
   private static final String tableSql =
         ATTRIBUTE_VERSION_TABLE + " t0," + ARTIFACT_TABLE + " t1," + ARTIFACT_TYPE_TABLE + " t4," + ATTRIBUTE_TYPE_TABLE + " t5";
   private static final String criteriaSql =
         "t0.art_id = t1.art_id AND NOT EXISTS" + " (SELECT NULL FROM " + VALID_ATTRIBUTES_TABLE + " t2" + " WHERE t0.attr_type_id = t2.attr_type_id AND t1.art_type_id = t2.art_type_id)" + " AND t4.art_type_id = t1.art_type_id AND t5.attr_type_id = t0.attr_type_id";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive#getSql(java.util.List, org.eclipse.osee.framework.skynet.core.artifact.Branch)
    */
   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      return criteriaSql;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive#getArtIdColName()
    */
   public String getArtIdColName() {
      return "t1.art_id";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive#getTables(java.util.List)
    */
   public String getTableSql(List<Object> dataList, Branch branch) {
      return tableSql;
   }

   @Override
   public String toString() {
      return getClass().getName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive#getStorageString()
    */
   public String getStorageString() {
      return "";
   }
}
