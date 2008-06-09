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
import static org.eclipse.osee.framework.skynet.core.artifact.search.DepricatedOperator.EQUAL;
import java.util.List;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Robert A. Fisher
 */
public class ArtifactHridSearch implements ISearchPrimitive {
   private String humanReadableId;
   private DepricatedOperator operator;

   /**
    * @param humanReadableId The human readable id to search for
    */
   public ArtifactHridSearch(String humanReadableId, DepricatedOperator operator) {
      super();
      this.operator = operator;
      this.humanReadableId = humanReadableId;
   }

   /**
    * @param humanReadableId The human readable id to search for
    */
   public ArtifactHridSearch(String humanReadableId) {
      this(humanReadableId, EQUAL);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.artifact.search.ISearchPrimitive#getArtIdColName()
    */
   public String getArtIdColName() {
      return "art_id";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.search.ISearchPrimitive#getSql()
    */
   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      String sql = ARTIFACT_TABLE.column("human_readable_id") + operator + "?";
      dataList.add(SQL3DataType.VARCHAR);
      dataList.add(humanReadableId);

      return sql;
   }

   public String getTableSql(List<Object> dataList, Branch branch) {
      return ARTIFACT_TABLE.toString();
   }

   @Override
   public String toString() {
      return "Artifact Human Readable Id: " + humanReadableId;
   }

   public String getStorageString() {
      return humanReadableId;
   }

   public static ArtifactHridSearch getPrimitive(String storageString) {
      return new ArtifactHridSearch(storageString, EQUAL);
   }
}
