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
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase;

/**
 * @author Robert A. Fisher
 */
public class RelatedToAndOfIDSearch implements ISearchPrimitive {
   private int artId;
   private int relType;

   /**
    * @param artId The type of relation for the artifact to be in
    */
   public RelatedToAndOfIDSearch(int artId, int relType) {
      super();
      this.artId = artId;
      this.relType = relType;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.artifact.search.ISearchPrimitive#getArtIdColName()
    */
   public String getArtIdColName() {
      return "artLinkJoin.art_id";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.search.ISearchPrimitive#getSql()
    */
   public String getSql() {
      return "artLinkJoin.rel_link_type_id = " + relType;
   }

   public String getTables() {
      return "(SELECT * FROM " + SkynetDatabase.ARTIFACT_TABLE + " INNER JOIN " + SkynetDatabase.RELATION_LINK_VERSION_TABLE + "	ON (( b_art_id=" + artId + " AND art_id=a_art_id) or (a_art_id=" + artId + " and art_id = b_art_id))) artLinkJoin";
   }

   public String toString() {
      return "Related to art_id: " + artId + " With rel type: " + relType;
   }

   public String getStorageString() {
      return Integer.toString(artId) + "," + Integer.toString(relType);
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
}
