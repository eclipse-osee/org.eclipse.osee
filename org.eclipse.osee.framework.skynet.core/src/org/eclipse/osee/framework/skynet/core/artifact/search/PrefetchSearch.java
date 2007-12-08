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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.IntegerRsetProcessor;
import org.eclipse.osee.framework.ui.plugin.util.db.Query;

/**
 * @author Robert A. Fisher
 */
public class PrefetchSearch implements ISearchPrimitive {
   private static final IntegerRsetProcessor artIdProcessor = new IntegerRsetProcessor("art_id");
   private Collection<Integer> artIds;

   public PrefetchSearch(Collection<Integer> artIds) {
      this.artIds = new HashSet<Integer>(artIds);
   }

   public PrefetchSearch(ISearchPrimitive primitive, Branch branch) throws SQLException {
      this(getCriteria(primitive), true, branch);
   }

   public PrefetchSearch(List<ISearchPrimitive> criteria, boolean all, Branch branch) throws SQLException {
      artIds = new HashSet<Integer>();
      List<Object> dataList = new LinkedList<Object>();
      Query.acquireCollection(artIds, artIdProcessor, ArtifactPersistenceManager.getIdSql(criteria, all, dataList,
            branch), dataList.toArray());
   }

   private static final List<ISearchPrimitive> getCriteria(ISearchPrimitive primitive) {
      List<ISearchPrimitive> criteria = new ArrayList<ISearchPrimitive>(1);
      criteria.add(primitive);

      return criteria;
   }

   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      StringBuilder sb = new StringBuilder(5 + (2 * artIds.size()));
      boolean notFirst = false;

      sb.append("art_id IN (");
      if (artIds.isEmpty()) {
         sb.append("-1");
      } else {
         for (Integer artId : artIds) {
            if (notFirst) sb.append(",");
            notFirst = true;

            sb.append("?");
            dataList.add(SQL3DataType.INTEGER);
            dataList.add(artId);
         }
      }
      sb.append(")");

      return sb.toString();
   }

   public String getArtIdColName() {
      return "art_id";
   }

   public String getTableSql(List<Object> dataList, Branch branch) {
      return ARTIFACT_TABLE.toString();
   }

   public String getStorageString() {
      throw new UnsupportedOperationException();
   }
}
