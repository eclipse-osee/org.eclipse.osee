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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ARTIFACT_TABLE;
import java.sql.SQLException;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Robert A. Fisher
 */
public class NotSearch implements ISearchPrimitive {
   private final ISearchPrimitive search;

   /**
    * @param search
    */
   public NotSearch(ISearchPrimitive search) {
      super();
      this.search = search;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive#getCriteriaSql(java.util.List, org.eclipse.osee.framework.skynet.core.artifact.Branch)
    */
   public String getCriteriaSql(List<Object> dataList, Branch branch) throws SQLException {
      return "NOT EXISTS(SELECT 'x' FROM (" + ArtifactPersistenceManager.getSelectArtIdSql(search, dataList, branch) + ") arts" + " WHERE " + ARTIFACT_TABLE.column("art_id") + "= arts." + search.getArtIdColName() + ")";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive#getArtIdColName()
    */
   public String getArtIdColName() {
      return "art_id";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive#getTableSql(java.util.List)
    */
   public String getTableSql(List<Object> dataList, Branch branch) {
      return ARTIFACT_TABLE.toString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive#getStorageString()
    */
   public String getStorageString() {
      return null;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return "Not " + search.toString();
   }

}
