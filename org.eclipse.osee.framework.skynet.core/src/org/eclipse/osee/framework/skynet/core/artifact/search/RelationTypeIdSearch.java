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
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.RELATION_LINK_TYPE_TABLE;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactTypeSearchAttribute;

/**
 * @author Robert A. Fisher
 */
public class RelationTypeIdSearch implements ISearchPrimitive {
   private int typeId;
   private Operator operation;
   private static final String tables = RELATION_LINK_TYPE_TABLE.toString();
   private final static String TOKEN = ";";

   public RelationTypeIdSearch(int typeId, Operator operation) {
      super();
      this.typeId = typeId;
      this.operation = operation;
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
   public String getSql() {
      return RELATION_LINK_TYPE_TABLE.column("rel_link_type_id") + EQUAL + typeId;
   }

   public String getTables() {
      return tables;
   }

   @Override
   public String toString() {
      return "rel_link_type_id: " + typeId;
   }

   public String getStorageString() {
      return Integer.toString(typeId) + TOKEN + operation.name();
   }

   public static RelationTypeIdSearch getPrimitive(String storageString) {
      String[] values = storageString.split(TOKEN);
      if (values.length != 2) throw new IllegalStateException(
            "Value for " + ArtifactTypeSearchAttribute.class.getSimpleName() + " not parsable");

      return new RelationTypeIdSearch(Integer.parseInt(values[0]), Operator.valueOf(values[1]));
   }

   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      return null;
   }

   public String getTableSql(List<Object> dataList, Branch branch) {
      return null;
   }

}
