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

import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase;

public class SkynetData {
   private static SkynetData instance = null;
   private SkynetRelational skynetRelationSql;
   private SkynetMetaData skynetMetaDataSql;

   private SkynetData() {
      skynetRelationSql = SkynetRelational.getInstance();
      skynetMetaDataSql = SkynetMetaData.getInstance();
   }

   static SkynetData getInstance() {
      if (instance == null) {
         instance = new SkynetData();
      }
      return instance;
   }

   private String getArtifactsNoDeletedCheck(int branchId, int revision) {
      String rcArtifactAlias = "rev_Artifact";
      String rcArtifactTypeAlias = "rev_Artifact_Type";
      return "\n\nSELECT " + rcArtifactAlias + ".guid, " + rcArtifactAlias + ".art_id, " + rcArtifactAlias + ".human_readable_id, " + rcArtifactTypeAlias + ".* " + " FROM " + "(" + "SELECT * FROM " + SkynetDatabase.ARTIFACT_TABLE.toString() + ") " + rcArtifactAlias + " INNER JOIN " + "(" + skynetMetaDataSql.getArtifactTypes(
            branchId, revision) + ") " + rcArtifactTypeAlias + " ON " + " (" + rcArtifactAlias + ".art_type_id = " + rcArtifactTypeAlias + ".art_type_id) \n\n";
   }

   public String getArtifacts(int branchId, int revision) {
      String rcArtifactAlias = "rev_Art_Data";
      String rcDeletedArtifactAlias = "rev_Deleted_Artifact";
      return "\n\nSELECT " + rcArtifactAlias + ".* " + " FROM " + "(" + getArtifactsNoDeletedCheck(branchId, revision) + ") " + rcArtifactAlias + " WHERE art_id NOT IN " + " ( SELECT " + rcDeletedArtifactAlias + ".art_id" + " FROM " + "(" + skynetRelationSql.getValidTableView(
            SkynetDatabase.DELETED_ARTIFACTS_TABLE.toString(), branchId, revision) + ") " + rcDeletedArtifactAlias + " ) " + "\n\n";
   }

   private String getAttributesNoDeletedCheck(int branchId, int revision) {
      String rcAttributeAlias = "rev_Attribute";
      String rcAttributeTypeAlias = "rev_Attribute_Type";
      return "\n\nSELECT " + rcAttributeAlias + ".art_id, " + rcAttributeAlias + ".attr_id, " + rcAttributeAlias + ".value, " + rcAttributeAlias + ".content, " + rcAttributeTypeAlias + ".* " + " FROM " + "(" + skynetRelationSql.getValidTableView(
            SkynetDatabase.ATTRIBUTE_VERSION_TABLE.toString(), branchId, revision) + ") " + rcAttributeAlias + " INNER JOIN " + "(" + skynetMetaDataSql.getAttributeTypes(
            branchId, revision) + ") " + rcAttributeTypeAlias + " ON " + "(" + rcAttributeAlias + ".attr_type_id = " + rcAttributeTypeAlias + ".attr_type_id )\n\n";
   }

   public String getAttributes(int branchId, int revision) {
      String rcAttributeAlias = "rev_Attributes";
      String rcDeletedAttributesAlias = "rev_Deleted_Attributes";
      return "\n\nSELECT " + rcAttributeAlias + ".* " + " FROM " + "(" + getAttributesNoDeletedCheck(branchId, revision) + ") " + rcAttributeAlias + " WHERE attr_id NOT IN " + " ( SELECT " + rcDeletedAttributesAlias + ".attr_id" + " FROM " + "(" + skynetRelationSql.getValidTableView(
            SkynetDatabase.DELETED_ATTRIBUTES_TABLE.toString(), branchId, revision) + ") " + rcDeletedAttributesAlias + " )\n\n";
   }

   private String getRelationsNoDeletedCheck(int branchId, int revision) {
      String rcRelationLinksAlias = "rev_Rel_Link";
      return "\n\nSELECT " + rcRelationLinksAlias + ".rel_link_id, " + rcRelationLinksAlias + ".b_art_id, " + rcRelationLinksAlias + ".a_art_id, " + rcRelationLinksAlias + ".rationale " + " FROM " + "(" + skynetRelationSql.getValidTableView(
            SkynetDatabase.RELATION_LINK_VERSION_TABLE.toString(), branchId, revision) + ") " + rcRelationLinksAlias;
   }

   public String getRelations(int branchId, int revision) {
      String rcRelationLinksAlias = "rev_Rel_Links";
      String rcDeletedRelationLinksAlias = "rev_Deleted_Rel_links";
      return "\n\nSELECT " + rcRelationLinksAlias + ".* " + " FROM " + "(" + getRelationsNoDeletedCheck(branchId,
            revision) + ") " + rcRelationLinksAlias + " WHERE rel_link_id NOT IN " + " ( SELECT " + rcDeletedRelationLinksAlias + ".rel_link_id" + " FROM " + "(" + skynetRelationSql.getValidTableView(
            SkynetDatabase.DELETED_RELATION_LINKS_TABLE.toString(), branchId, revision) + ") " + rcDeletedRelationLinksAlias + " )\n\n";
   }

   public String getArtifactsWithAttributes(int branchId, int revision) {
      String attributeData = "attributeData";
      String artifactData = "artifactData";
      return "\n\nSELECT " + artifactData + ".guid, " + artifactData + ".art_id, " + artifactData + ".human_readable_id, " + artifactData + ".art_type_id, " + artifactData + ".name as art_type_name, " + artifactData + ".factory_key, " + artifactData + ".factory_class, " + artifactData + ".factory_id, " + attributeData + ".attr_id, " + attributeData + ".value, " + attributeData + ".content, " + attributeData + ".attr_type_id, " + attributeData + ".tip_text, " + attributeData + ".max_occurence, " + attributeData + ".name as attr_type_name, " + attributeData + ".validity_xml, " + attributeData + ".min_occurence, " + attributeData + ".default_value, " + attributeData + ".attr_base_type_id, " + attributeData + ".attribute_class " + " FROM " + "( " + getArtifacts(
            branchId, revision) + ") " + artifactData + ", " + "( " + getAttributes(branchId, revision) + ") " + attributeData + " WHERE  " + "( " + artifactData + ".art_id = " + attributeData + ".art_id" + " )" + " AND " + "( " + artifactData + ".art_type_id = " + attributeData + ".art_type_id" + " )" + "\n\n";
   }

   public String getArtifactsWithAttributesBy(String field, String fieldValue, int branchId, int revision) {
      String artData = "artifactData";
      return "\n\nSELECT * FROM " + "(" + getArtifactsWithAttributes(branchId, revision) + ") " + artData + " WHERE " + "( " + artData + "." + field + " = " + fieldValue + ")\n\n";
   }

   public String getRelationsWithGuids(int branchId, int revision) {
      String rcRelationAlias = "rev_Guid_Relation";
      String rcArtifactSideA = "a_art_guid";
      String rcArtifactSideB = "b_art_guid";
      String rcInnerArtifacts = "innerArtifacts";
      return "\n\nSELECT " + rcRelationAlias + ".*, " + "( " + " SELECT guid " + " FROM ( " + getArtifacts(branchId,
            revision) + ") " + rcInnerArtifacts + " WHERE " + "( " + rcInnerArtifacts + ".art_id = " + rcRelationAlias + ".a_art_id )" + ") " + rcArtifactSideA + ", " + "( " + " SELECT guid " + " FROM ( " + getArtifacts(
            branchId, revision) + ") " + rcInnerArtifacts + " WHERE " + "( " + rcInnerArtifacts + ".art_id = " + rcRelationAlias + ".b_art_id )" + ") " + rcArtifactSideB + " FROM " + " ( " + getRelations(
            branchId, revision) + ") " + rcRelationAlias + "\n\n";
   }

   public static void main(String[] args) {
      System.out.println("SELECT * FROM (" + SkynetData.getInstance().getRelationsWithGuids(1, 5958) + ") artifacts");

      System.exit(1);
   }
}
