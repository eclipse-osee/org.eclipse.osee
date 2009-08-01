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

import static org.eclipse.osee.framework.database.sql.SkynetDatabase.ARTIFACT_TABLE;
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

   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      return "NOT EXISTS(SELECT 'x' FROM (" + ArtifactPersistenceManager.getSelectArtIdSql(search, dataList, branch) + ") arts" + " WHERE " + ARTIFACT_TABLE.column("art_id") + "= arts." + search.getArtIdColName() + ")";
   }

   public String getArtIdColName() {
      return "art_id";
   }

   public String getTableSql(List<Object> dataList, Branch branch) {
      return ARTIFACT_TABLE.toString();
   }

   public String getStorageString() {
      return "Not [" + search.getStorageString() + "]";
   }

   @Override
   public String toString() {
      return "Not " + search.toString();
   }
}