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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TAG_STALE_ARTIFACT_TABLE;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;

/**
 * Search primitive for artifacts that have stale tag data.
 * 
 * @author Robert A. Fisher
 */
public class ArtifactHasStaleTags implements ISearchPrimitive {

   public ArtifactHasStaleTags() {
      super();
   }

   public String getArtIdColName() {
      return "art_id";
   }

   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      String sql = TAG_STALE_ARTIFACT_TABLE.column("human_readable_id") + "=?";
      dataList.add(SQL3DataType.VARCHAR);
      dataList.add(branch.getBranchId());

      return sql;
   }

   public String getTableSql(List<Object> dataList, Branch branch) {
      return TAG_STALE_ARTIFACT_TABLE.toString();
   }

   @Override
   public String toString() {
      return "Artifact has stale tag data";
   }

   public String getStorageString() {
      return "";
   }

   public static ArtifactHasStaleTags getPrimitive(String storageString) {
      return new ArtifactHasStaleTags();
   }
}
