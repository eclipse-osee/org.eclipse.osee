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
public class ArtifactTypeSearch implements ISearchPrimitive {
   private final String typeName;
   private final DeprecatedOperator operation;
   private static final String tables = "osee_artifact, osee_artifact_type";
   private final static String TOKEN = ";";

   /**
    * @param typeName
    */
   public ArtifactTypeSearch(String typeName, DeprecatedOperator operation) {
      super();
      this.typeName = typeName;
      this.operation = operation;
   }

   @Override
   public String getArtIdColName() {
      return "art_id";
   }

   @Override
   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      String sql =
         "osee_artifact_type.name" + operation + " ? AND osee_artifact_type.art_type_id = osee_artifact.art_type_id";
      dataList.add(typeName);

      return sql;
   }

   @Override
   public String getTableSql(List<Object> dataList, Branch branch) {
      return tables;
   }

   @Override
   public String toString() {
      return "Artifact type: " + typeName;
   }

   @Override
   public String getStorageString() {
      return typeName + TOKEN + operation.name();
   }

   public static ArtifactTypeSearch getPrimitive(String storageString) {
      String[] values = storageString.split(TOKEN);
      if (values.length != 2) {
         throw new IllegalStateException("Value for " + ArtifactTypeSearch.class.getSimpleName() + " not parsable");
      }

      return new ArtifactTypeSearch(values[0], DeprecatedOperator.valueOf(values[1]));
   }

}
