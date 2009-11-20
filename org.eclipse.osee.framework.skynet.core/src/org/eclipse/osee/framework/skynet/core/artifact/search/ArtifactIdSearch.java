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

import static org.eclipse.osee.framework.skynet.core.artifact.search.DeprecatedOperator.EQUAL;
import static org.eclipse.osee.framework.skynet.core.artifact.search.SkynetDatabase.ARTIFACT_TABLE;
import java.util.List;
import org.eclipse.osee.framework.core.model.Branch;

/**
 * @author Donald G. Dunne
 */
public class ArtifactIdSearch implements ISearchPrimitive {
   private int artId;
   private DeprecatedOperator operator;

   /**
    * @param humanReadableId The human readable id to search for
    */
   public ArtifactIdSearch(int artId, DeprecatedOperator operator) {
      super();
      this.operator = operator;
      this.artId = artId;
   }

   /**
    * @param humanReadableId The human readable id to search for
    */
   public ArtifactIdSearch(int artId) {
      this(artId, EQUAL);
   }

   public String getArtIdColName() {
      return "art_id";
   }

   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      String sql = ARTIFACT_TABLE.column("art_id") + operator + "?";
      dataList.add(artId);

      return sql;
   }

   public String getTableSql(List<Object> dataList, Branch branch) {
      return ARTIFACT_TABLE.toString();
   }

   @Override
   public String toString() {
      return "Artifact Id: " + artId;
   }

   public String getStorageString() {
      return Integer.toString(artId);
   }
}
