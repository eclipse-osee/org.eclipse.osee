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
 * @author Robert A. Fisher
 */
public class IsArtifactSearch implements ISearchPrimitive {
   private int artId;
   private static final String tables = ARTIFACT_TABLE.toString();

   public IsArtifactSearch(int artId) {
      super();
      this.artId = artId;
   }

   /* (non-Javadoc)
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
      String sql = ARTIFACT_TABLE.column("art_id") + EQUAL + "?";
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(artId);

      return sql;
   }

   public String getTableSql(List<Object> dataList, Branch branch) {
      return tables;
   }

   @Override
   public String toString() {
      return "Artifact id: " + artId;
   }

   public String getStorageString() {
      return Integer.toString(artId);
   }

   public static IsArtifactSearch getPrimitive(String storageString) {
      return new IsArtifactSearch(Integer.parseInt(storageString));
   }

}
