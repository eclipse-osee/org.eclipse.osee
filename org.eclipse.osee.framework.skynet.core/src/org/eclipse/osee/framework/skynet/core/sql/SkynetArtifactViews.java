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
package org.eclipse.osee.framework.skynet.core.sql;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.User.Attributes;

public class SkynetArtifactViews {

   private static SkynetArtifactViews instance = null;
   private static SkynetSql skynetSql;

   private SkynetArtifactViews() {
      skynetSql = SkynetSql.getInstance();
   }

   static SkynetArtifactViews getInstance() {
      if (instance == null) {
         instance = new SkynetArtifactViews();
      }
      return instance;
   }

   private String getAttributeColumnAlias(String attribute) {
      return attribute.replaceAll(" ", "_");
   }

   private String getColumn(String artTypeName, String artId, int branchId, int revision) {
      String sourceAlias = "sourceTable";
      return "SELECT value " + " FROM " + "(" + skynetSql.getDataSql().getArtifactsWithAttributes(branchId, revision) + ") " + sourceAlias + " WHERE " + "(" + sourceAlias + ".attr_type_name = " + "'" + artTypeName + "'" + ")" + " AND " + "(" + sourceAlias + ".art_id = " + artId + ")";
   }

   private String artifactBuilder(String artifactName, List<String> attributes, int branchId, int revision) {
      String outerTable = "outerTable";
      String artifactTable = "artifactData";
      String toReturn = " SELECT "
      //+ outerTable + ".*";
      + outerTable + ".art_id" + ", " + outerTable + ".human_readable_id" + ", " + outerTable + ".guid" + " ";

      for (int index = 0; index < attributes.size(); index++) {
         String tempAttribute = attributes.get(index);
         String columnAlias = getAttributeColumnAlias(tempAttribute);
         toReturn += ",\n";
         toReturn +=
               "(" + getColumn(tempAttribute, outerTable + ".art_id", branchId, revision) + ")" + " as " + columnAlias;
      }
      toReturn += " FROM ";
      toReturn += "(";
      toReturn +=
            "SELECT " + artifactTable + ".* FROM " + "(" + skynetSql.getDataSql().getArtifacts(branchId, revision) + ")" + artifactTable + " WHERE " + "(" + artifactTable + ".name = '" + artifactName + "'" + ")";
      toReturn += ")";
      toReturn += outerTable;
      return toReturn;
   }

   public String getUserArtifact(int branchId, int revision) {
      List<String> attributeList = new ArrayList<String>();
      Attributes[] attributes = User.Attributes.values();
      for (Attributes attribute : attributes) {
         if (!attribute.equals(Attributes.Policy)) {
            attributeList.add(attribute.name());
         }
      }
      attributeList.add("Name");
      return artifactBuilder(User.ARTIFACT_NAME, attributeList, branchId, revision);
   }
}
