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

import static org.eclipse.osee.framework.skynet.core.artifact.search.Operator.EQUAL;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ARTIFACT_TABLE;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;

/**
 * @author Donald G. Dunne
 */
public class ArtifactGuidSearch implements ISearchPrimitive {
   private String guid;
   private Operator operator;

   /**
    * @param guid The guid to search for
    */
   public ArtifactGuidSearch(String guid, Operator operator) {
      super();
      this.operator = operator;
      this.guid = guid;
   }

   /**
    * @param guid The human readable id to search for
    */
   public ArtifactGuidSearch(String guid) {
      this(guid, EQUAL);
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
      String sql = ARTIFACT_TABLE.column("guid") + operator + "?";
      dataList.add(SQL3DataType.VARCHAR);
      dataList.add(guid);

      return sql;
   }

   public String getTableSql(List<Object> dataList, Branch branch) {
      return ARTIFACT_TABLE.toString();
   }

   @Override
   public String toString() {
      return "Artifact Guid: " + guid;
   }

   public String getStorageString() {
      return guid;
   }

   public static ArtifactGuidSearch getPrimitive(String storageString) {
      return new ArtifactGuidSearch(storageString, EQUAL);
   }
}
