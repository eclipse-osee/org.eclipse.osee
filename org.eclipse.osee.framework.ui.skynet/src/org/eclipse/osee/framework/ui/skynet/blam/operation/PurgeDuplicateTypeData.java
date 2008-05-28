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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Ryan D. Brooks
 */
public class PurgeDuplicateTypeData extends AbstractBlam {
   private static final String UPDATE_RELATION_VALIDITY_TXS =
         "UPDATE osee_define_txs txs1 SET txs1.gamma_id = ? WHERE txs1.gamma_id IN (SELECT var2.gamma_id FROM osee_define_valid_relations var2 WHERE var2.rel_link_type_id = ? AND var2.art_type_id = ? AND var2.gamma_id <> ? AND NOT EXISTS (SELECT 'x' FROM osee_define_txs txs3 WHERE txs1.transaction_id = txs3.transaction_id AND txs3.gamma_id = ?))";
   private static final String DELETE_RELATION_VALIDITY =
         "DELETE FROM osee_define_valid_relations where rel_link_type_id = ? and art_type_id = ? and gamma_id <> ?";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      //purgeDuplicateAttributeValidity();

      purgeRelationValidityAndGammas();

      for (ArtifactType artifactType : new HashSet<ArtifactType>(ArtifactTypeManager.getAllTypes())) {
         purgeArtifactTypeAndGammas(artifactType.getName());
      }

      for (AttributeType attributeType : new HashSet<AttributeType>(AttributeTypeManager.getTypes(null))) {
         purgeAttributeTypeAndGammas(attributeType.getName());
      }
   }

   private void purgeDuplicateAttributeValidity() throws SQLException {
      int updateCount =
            ConnectionHandler.runPreparedUpdateReturnCount("delete from osee_define_txs where gamma_id in (select vat1.gamma_id from osee_define_valid_attributes vat1 where vat1.gamma_id <> (SELECT min(gamma_id) FROM osee_define_valid_attributes vat2 where vat1.art_type_id = vat2.art_type_id and vat1.attr_type_id = vat2.attr_type_id group by vat2.art_type_id, vat2.attr_type_id))");
      appendResultLine("number of txs rows deleted " + updateCount);
      updateCount =
            ConnectionHandler.runPreparedUpdateReturnCount("delete from osee_define_valid_attributes vat1 where vat1.gamma_id <> (SELECT min(gamma_id) FROM osee_define_valid_attributes vat2 where vat1.art_type_id = vat2.art_type_id and vat1.attr_type_id = vat2.attr_type_id group by vat2.art_type_id, vat2.attr_type_id)");
      appendResultLine("number of osee_define_valid_attributes rows deleted " + updateCount);
   }

   private void purgeArtifactTypeAndGammas(String artifactType) throws SQLException {
      ConnectionHandlerStatement chStmt = null;
      try {
         int smallestGammaId = -1;
         int newArtifactId = -1;
         chStmt =
               ConnectionHandler.runPreparedQuery(
                     "SELECT gamma_id, art_type_id FROM osee_define_artifact_type WHERE name = ? ORDER BY gamma_id",
                     SQL3DataType.VARCHAR, artifactType);
         ResultSet rSet = chStmt.getRset();
         if (rSet.next()) {
            smallestGammaId = rSet.getInt("gamma_id");
            newArtifactId = rSet.getInt("art_type_id");
         } else {
            return;
         }
         while (rSet.next()) {
            eliminateArtifactTypeAndGamma(rSet.getInt("art_type_id"), newArtifactId, rSet.getInt("gamma_id"),
                  smallestGammaId);
         }
      } finally {
         DbUtil.close(chStmt);
      }
   }

   private void purgeAttributeTypeAndGammas(String attributeTypeName) throws SQLException {
      ConnectionHandlerStatement chStmt = null;
      try {
         int smallestGammaId = -1;
         int newAttributeId = -1;
         chStmt =
               ConnectionHandler.runPreparedQuery(
                     "SELECT gamma_id, attr_type_id FROM osee_define_attribute_type WHERE name = ? ORDER BY gamma_id",
                     SQL3DataType.VARCHAR, attributeTypeName);
         ResultSet rSet = chStmt.getRset();
         if (rSet.next()) {
            smallestGammaId = rSet.getInt("gamma_id");
            newAttributeId = rSet.getInt("attr_type_id");
         } else {
            return;
         }
         while (rSet.next()) {
            eliminateAttributeTypeAndGamma(rSet.getInt("attr_type_id"), newAttributeId, rSet.getInt("gamma_id"),
                  smallestGammaId);
         }
      } finally {
         DbUtil.close(chStmt);
      }
   }

   private void eliminateAttributeTypeAndGamma(int oldAttributeTypeId, int newAttributeTypeId, int oldGammaId, int newGammaId) throws SQLException {
      updateGamma(oldGammaId, newGammaId);

      ConnectionHandler.runPreparedUpdate(
            "UPDATE osee_define_valid_attributes set attr_type_id = ? where attr_type_id = ?", SQL3DataType.INTEGER,
            newAttributeTypeId, SQL3DataType.INTEGER, oldAttributeTypeId);

      ConnectionHandler.runPreparedUpdate("UPDATE osee_define_attribute set attr_type_id = ? where attr_type_id = ?",
            SQL3DataType.INTEGER, newAttributeTypeId, SQL3DataType.INTEGER, oldAttributeTypeId);

      ConnectionHandler.runPreparedUpdate("DELETE FROM osee_define_attribute_type where attr_type_id = ?",
            SQL3DataType.INTEGER, oldAttributeTypeId);
   }

   private void updateGamma(int oldGammaId, int newGammaId) throws SQLException {
      ConnectionHandler.runPreparedUpdate(
            "UPDATE osee_define_txs txs1 set gamma_id = ? where gamma_id = ? and not exists (select 'x' from osee_define_txs txs2 where txs1.transaction_id = txs2.transaction_id and txs2.gamma_id = ?)",
            SQL3DataType.INTEGER, newGammaId, SQL3DataType.INTEGER, oldGammaId, SQL3DataType.INTEGER, newGammaId);
   }

   private void eliminateArtifactTypeAndGamma(int oldArtifactTypeId, int newArtifactTypeId, int oldGammaId, int newGammaId) throws SQLException {
      updateGamma(oldGammaId, newGammaId);

      ConnectionHandler.runPreparedUpdate(
            "UPDATE osee_define_valid_relations set art_type_id = ? where art_type_id = ?", SQL3DataType.INTEGER,
            newArtifactTypeId, SQL3DataType.INTEGER, oldArtifactTypeId);

      ConnectionHandler.runPreparedUpdate(
            "UPDATE osee_define_valid_attributes set art_type_id = ? where art_type_id = ?", SQL3DataType.INTEGER,
            newArtifactTypeId, SQL3DataType.INTEGER, oldArtifactTypeId);

      ConnectionHandler.runPreparedUpdate("UPDATE osee_define_artifact set art_type_id = ? where art_type_id = ?",
            SQL3DataType.INTEGER, newArtifactTypeId, SQL3DataType.INTEGER, oldArtifactTypeId);

      ConnectionHandler.runPreparedUpdate("DELETE FROM osee_define_artifact_type where art_type_id = ?",
            SQL3DataType.INTEGER, oldArtifactTypeId);
   }

   private void purgeRelationValidityAndGammas() throws SQLException {
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt =
               ConnectionHandler.runPreparedQuery("SELECT min(gamma_id) minGamma, rel_link_type_id, art_type_id FROM osee_define_valid_relations group by rel_link_type_id, art_type_id order by rel_link_type_id, art_type_id");
         ResultSet rSet = chStmt.getRset();
         while (rSet.next()) {
            eliminateDuplicateRelationValidityAndGammas(rSet.getInt("minGamma"), rSet.getInt("rel_link_type_id"),
                  rSet.getInt("art_type_id"));
         }
      } finally {
         DbUtil.close(chStmt);
      }
   }

   private void eliminateDuplicateRelationValidityAndGammas(int newGamma, int relationTypeId, int artifactTypeId) throws SQLException {
      int updateCount =
            ConnectionHandler.runPreparedUpdateReturnCount(UPDATE_RELATION_VALIDITY_TXS, SQL3DataType.INTEGER,
                  newGamma, SQL3DataType.INTEGER, relationTypeId, SQL3DataType.INTEGER, artifactTypeId,
                  SQL3DataType.INTEGER, newGamma, SQL3DataType.INTEGER, newGamma);

      appendResultLine("number of txs rows updated " + updateCount);

      updateCount =
            ConnectionHandler.runPreparedUpdateReturnCount(DELETE_RELATION_VALIDITY, SQL3DataType.INTEGER,
                  relationTypeId, SQL3DataType.INTEGER, artifactTypeId, SQL3DataType.INTEGER, newGamma);

      appendResultLine("number of relation validity rows deleted " + updateCount);
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return emptyXWidgetsXml;
   }
}