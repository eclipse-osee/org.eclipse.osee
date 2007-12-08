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

public class SkynetMetaData {

   private static SkynetMetaData instance = null;
   private SkynetRelational skynetRelationSql;

   public SkynetMetaData() {
      skynetRelationSql = SkynetRelational.getInstance();
   }

   static SkynetMetaData getInstance() {
      if (instance == null) {
         instance = new SkynetMetaData();
      }
      return instance;
   }

   public String getArtifactTypes(int branchId, int revision) {
      String rcArtifactTypeAlias = "rev_Artifact_Type";
      String rcFactoryAlias = "rev_Factory";
      return "\n\nSELECT " + rcArtifactTypeAlias + ".name, " + rcArtifactTypeAlias + ".image, " + rcArtifactTypeAlias + ".art_type_id, " + rcArtifactTypeAlias + ".factory_key, " + rcFactoryAlias + ".factory_class, " + rcFactoryAlias + ".factory_id " + " FROM " + "(" + skynetRelationSql.getValidTableView(
            SkynetDatabase.ARTIFACT_TYPE_TABLE.toString(), branchId, revision) + ") " + rcArtifactTypeAlias + " INNER JOIN " + "(" + skynetRelationSql.getValidTableView(
            SkynetDatabase.FACTORY_TABLE.toString(), branchId, revision) + ") " + rcFactoryAlias + " ON " + "( " + rcFactoryAlias + ".factory_id = " + rcArtifactTypeAlias + ".factory_id " + ")\n\n";
   }

   public String getArtifactTypeBy(String field, String fieldValue, int branchId, int revision) {
      String rcAvailableArtifactTypesAlias = "rev_Avail_Artifact_Types";
      return "\n\nSELECT * FROM " + "(" + getArtifactTypes(branchId, revision) + ") " + rcAvailableArtifactTypesAlias + " WHERE " + "( " + rcAvailableArtifactTypesAlias + "." + field + " = " + fieldValue + ")\n\n";
   }

   public String getAttributeTypes(int branchId, int revision) {
      String rcAttributeTypeAlias = "rev_Attribute_Type";
      String rcAttributeBaseTypeAlias = "rev_Attr_Base_Type";
      String rcValidAttributesAlias = "rev_Valid_Attributes";
      return "\n\nSELECT " + rcAttributeTypeAlias + ".attr_type_id, " + rcAttributeTypeAlias + ".tip_text, " + rcAttributeTypeAlias + ".max_occurence, " + rcAttributeTypeAlias + ".name, " + rcAttributeTypeAlias + ".validity_xml, " + rcAttributeTypeAlias + ".min_occurence, " + rcAttributeTypeAlias + ".default_value, " + rcAttributeBaseTypeAlias + ".attr_base_type_id, " + rcAttributeBaseTypeAlias + ".attribute_class, " + rcValidAttributesAlias + ".art_type_id " + " FROM " + "(" + skynetRelationSql.getValidTableView(
            SkynetDatabase.ATTRIBUTE_TYPE_TABLE.toString(), branchId, revision) + ") " + rcAttributeTypeAlias + ", " + "(" + skynetRelationSql.getValidTableView(
            SkynetDatabase.ATTRIBUTE_BASE_TYPE_TABLE.toString(), branchId, revision) + ") " + rcAttributeBaseTypeAlias + ", " + "(" + skynetRelationSql.getValidTableView(
            SkynetDatabase.VALID_ATTRIBUTES_TABLE.toString(), branchId, revision) + ") " + rcValidAttributesAlias + " WHERE " + "( " + rcAttributeBaseTypeAlias + ".attr_base_type_id = " + rcAttributeTypeAlias + ".attr_base_type_id )" + " AND " + "( " + rcValidAttributesAlias + ".attr_type_id = " + rcAttributeTypeAlias + ".attr_type_id )\n\n";
   }

   public String getAttributeTypeBy(String field, String fieldValue, int branchId, int revision) {
      String rcAvailableAttributeTypesAlias = "rev_Avail_Attribute_Types";
      return "\n\nSELECT * FROM " + "(" + getAttributeTypes(branchId, revision) + ") " + rcAvailableAttributeTypesAlias + " WHERE " + "( " + rcAvailableAttributeTypesAlias + "." + field + " = " + fieldValue + ")\n\n";
   }

   public String getRelationTypes(int branchId, int revision) {
      String rcRelationLinkTypeAlias = "rev_Relation_Link_Type";
      String rcValidRelationAlias = "rev_Valid_Relation";
      return "\n\nSELECT " + rcRelationLinkTypeAlias + ".rel_link_type_id, " + rcRelationLinkTypeAlias + ".type_name, " + rcRelationLinkTypeAlias + ".short_name, " + rcRelationLinkTypeAlias + ".a_name, " + rcRelationLinkTypeAlias + ".b_name, " + rcRelationLinkTypeAlias + ".ab_phrasing, " + rcRelationLinkTypeAlias + ".ba_phrasing, " + rcValidRelationAlias + ".art_type_id, " + rcValidRelationAlias + ".side_a_max, " + rcValidRelationAlias + ".side_b_max " + " FROM " + "(" + skynetRelationSql.getValidTableView(
            SkynetDatabase.RELATION_LINK_TYPE_TABLE.toString(), branchId, revision) + ") " + rcRelationLinkTypeAlias + " INNER JOIN " + "(" + skynetRelationSql.getValidTableView(
            SkynetDatabase.VALID_RELATIONS_TABLE.toString(), branchId, revision) + ") " + rcValidRelationAlias + " ON " + "( " + rcRelationLinkTypeAlias + ".rel_link_type_id = " + rcValidRelationAlias + ".rel_link_type_id )\n\n";
   }

   public String getRelationTypeBy(String field, String fieldValue, int branchId, int revision) {
      String rcAvailableRelationTypesAlias = "rev_Avail_Relation_Types";
      return "\n\nSELECT * FROM " + "(" + getRelationTypes(branchId, revision) + ") " + rcAvailableRelationTypesAlias + " WHERE " + "( " + rcAvailableRelationTypesAlias + "." + field + " = " + fieldValue + ")\n\n";
   }

   public String getArtifactTypesWithRelationTypes(int branchId, int revision) {
      String relationLinkType = "relationLinkTypesForId";
      String artifactTypes = "validArtifactTypes";
      return "\n\nSELECT " + artifactTypes + ".name, " + artifactTypes + ".image, " + artifactTypes + ".art_type_id, " + artifactTypes + ".factory_key, " + artifactTypes + ".factory_class, " + artifactTypes + ".factory_id, " + relationLinkType + ".rel_link_type_id, " + relationLinkType + ".type_name, " + relationLinkType + ".short_name, " + relationLinkType + ".a_name, " + relationLinkType + ".b_name, " + relationLinkType + ".ab_phrasing, " + relationLinkType + ".ba_phrasing, " + relationLinkType + ".side_a_max, " + relationLinkType + ".side_b_max " + " FROM " + "( " + getArtifactTypes(
            branchId, revision) + ") " + artifactTypes + " INNER JOIN " + "( " + getRelationTypes(branchId, revision) + ") " + relationLinkType + " ON " + "( " + artifactTypes + ".art_type_id = " + relationLinkType + ".art_type_id" + " )\n\n";
   }

   public String getArtifactTypesWithRelationTypesById(String field, String fieldValue, int branchId, int revision) {
      String rcAvailableArtifactRelationTypesAlias = "rev_Avail_Art_Rel_Types";
      return "\n\nSELECT * FROM " + "(" + getArtifactTypesWithRelationTypes(branchId, revision) + ") " + rcAvailableArtifactRelationTypesAlias + " WHERE " + "( " + rcAvailableArtifactRelationTypesAlias + "." + field + " = " + fieldValue + ")\n\n";
   }

   public String getArtifactTypesWithAttributeTypes(int branchId, int revision) {
      String attributeTypes = "validAttributeTypes";
      String artifactTypes = "validArtifactTypes";
      return "\n\nSELECT " + artifactTypes + ".name as art_type_name, " + artifactTypes + ".art_type_id, " + artifactTypes + ".factory_key, " + artifactTypes + ".factory_class, " + artifactTypes + ".factory_id, " + attributeTypes + ".attr_type_id, " + attributeTypes + ".tip_text, " + attributeTypes + ".max_occurence, " + attributeTypes + ".name as attr_type_name, " + attributeTypes + ".validity_xml, " + attributeTypes + ".min_occurence, " + attributeTypes + ".default_value, " + attributeTypes + ".attr_base_type_id, " + attributeTypes + ".attribute_class " + " FROM " + "( " + getArtifactTypes(
            branchId, revision) + ") " + artifactTypes + " INNER JOIN " + "( " + getAttributeTypes(branchId, revision) + ") " + attributeTypes + " ON " + "( " + artifactTypes + ".art_type_id = " + attributeTypes + ".art_type_id" + " )\n\n";
   }

   public String getArtifactTypesWithAttributeTypesById(String field, String fieldValue, int branchId, int revision) {
      String rcAvailableArtifactAttributeTypesAlias = "rev_Avail_Art_Attr_Types";
      return "\n\nSELECT * FROM " + "(" + getArtifactTypesWithAttributeTypes(branchId, revision) + ") " + rcAvailableArtifactAttributeTypesAlias + " WHERE " + "( " + rcAvailableArtifactAttributeTypesAlias + "." + field + " = " + fieldValue + ")\n\n";
   }

   public static void main(String[] args) {
      System.out.println(SkynetMetaData.getInstance().getArtifactTypesWithAttributeTypes(1, 4));
      System.exit(1);
   }
}
