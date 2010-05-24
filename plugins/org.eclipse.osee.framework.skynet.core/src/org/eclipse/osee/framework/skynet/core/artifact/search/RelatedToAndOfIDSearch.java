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

import java.util.List;
import org.eclipse.osee.framework.core.model.Branch;

/**
 * @author Robert A. Fisher
 */
public class RelatedToAndOfIDSearch implements ISearchPrimitive {
   private final int artId;
   private final int relType;

   /**
    * @param artId The type of relation for the artifact to be in
    */
   public RelatedToAndOfIDSearch(int artId, int relType) {
      super();
      this.artId = artId;
      this.relType = relType;
   }

   public String getArtIdColName() {
      return "artLinkJoin.art_id";
   }

   public String getSql() {
      return "artLinkJoin.rel_link_type_id = " + relType;
   }

   public String getTables() {
      return "(SELECT * FROM osee_artifact INNER JOIN osee_relation_link ON (( b_art_id=" + artId + " AND art_id=a_art_id) or (a_art_id=" + artId + " and art_id = b_art_id))) artLinkJoin";
   }

   @Override
   public String toString() {
      return "Related to art_id: " + artId + " With rel type: " + relType;
   }

   public static RelatedToAndOfIDSearch getPrimitive(String storageString) {
      String[] args = storageString.split(",");
      return new RelatedToAndOfIDSearch(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
   }

   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      return null;
   }

   public String getTableSql(List<Object> dataList, Branch branch) {
      return null;
   }

   public String getStorageString() {
      return Integer.toString(artId) + "," + Integer.toString(relType);
   }
}
