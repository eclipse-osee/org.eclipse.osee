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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Robert A. Fisher
 */
public class FromArtifactsSearch implements ISearchPrimitive {
   private static final String tables = ARTIFACT_TABLE.toString();
   private static final String FROM_ARTIFACT_ELEMENT = "FromArtifact";

   private List<ISearchPrimitive> criteria;
   private boolean all;

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.artifact.search.ISearchPrimitive#getArtIdColName()
    */
   public String getArtIdColName() {
      return "art_id";
   }

   /**
    * 
    */
   public FromArtifactsSearch(List<ISearchPrimitive> criteria, boolean all) {
      this.criteria = criteria;
      this.all = all;
   }

   public FromArtifactsSearch(ISearchPrimitive primitive) {
      this.criteria = new ArrayList<ISearchPrimitive>(1);
      this.criteria.add(primitive);
      // all doesn't matter for just one primitive, so assume true
      this.all = true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.search.ISearchPrimitive#getSql()
    */
   public String getCriteriaSql(List<Object> dataList, Branch branch) throws SQLException {
      return "art_id in (" + ArtifactPersistenceManager.getIdSql(criteria, all, dataList, branch) + ")";
   }

   public String getTableSql(List<Object> dataList, Branch branch) {
      return tables;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();

      sb.append("(");

      for (ISearchPrimitive primitive : criteria)
         sb.append(primitive);

      sb.append(")");

      return sb.toString();
   }
}
