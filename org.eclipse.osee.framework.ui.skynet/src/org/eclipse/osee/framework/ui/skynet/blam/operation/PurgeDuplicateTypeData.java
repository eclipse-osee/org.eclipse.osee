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
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Ryan D. Brooks
 */
public class PurgeDuplicateTypeData extends AbstractBlam {
   ConfigurationPersistenceManager configurationManager = ConfigurationPersistenceManager.getInstance();

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      for (ArtifactSubtypeDescriptor artifactType : new HashSet<ArtifactSubtypeDescriptor>(
            configurationManager.getArtifactSubtypeDescriptors())) {
         purgeArtifactTypeAndGammas(artifactType.getName());
      }

      for (DynamicAttributeDescriptor attributeType : new HashSet<DynamicAttributeDescriptor>(
            configurationManager.getDynamicAttributeDescriptors(null))) {
         purgeAttributeTypeAndGammas(attributeType.getName());
      }
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

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return emptyXWidgetsXml;
   }
}