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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.core.schema.Table;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Ryan D. Brooks
 */
public class PurgeArtifactType extends AbstractBlam {
   public static final String DELETE_VALID_REL_GAMMAS =
         "delete from osee_define_txs txs3 where exists (select * from osee_define_valid_relations vrl1, osee_define_txs txs2 where vrl1.art_type_id = ? and vrl1.gamma_id = txs2.gamma_id AND txs2.gamma_id = txs3.gamma_id)";
   public static final String DELETE_VALID_ATTRIBUTE_GAMMAS =
         "delete from osee_define_txs txs3 where exists (select * from osee_define_valid_attributes vat1, osee_define_txs txs2 where vat1.art_type_id = ? and vat1.gamma_id = txs2.gamma_id AND txs2.gamma_id = txs3.gamma_id)";
   public static final String DELETE_VALID_REL = "delete from osee_define_valid_relations where art_type_id = ?";
   public static final String DELETE_VALID_ATTRIBUTE = "delete from osee_define_valid_attributes where art_type_id = ?";
   public static final String COUNT_ARTIFACT_OCCURRENCE =
         "select " + Table.alias("count(*)", "artCount") + " from osee_define_artifact where art_type_id = ?";
   public static final String DELETE_ARIFACT_TYPE_GAMMAS =
         "delete from osee_define_txs txs3 where exists (select * from osee_define_artifact_type ary1, osee_define_txs txs2 where ary1.art_type_id = ? and ary1.gamma_id = txs2.gamma_id AND txs2.gamma_id = txs3.gamma_id)";
   public static final String DELETE_ARIFACT_TYPE = "delete from osee_define_artifact_type where art_type_id = ?";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      ArtifactType artType = variableMap.getArtifactSubtypeDescriptor("Artifact Type");
      int artTypeId = artType.getArtTypeId();

      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt = ConnectionHandler.runPreparedQuery(COUNT_ARTIFACT_OCCURRENCE, SQL3DataType.INTEGER, artTypeId);
         ResultSet rSet = chStmt.getRset();
         if (rSet.next() && rSet.getInt("artCount") != 0) {
            throw new IllegalArgumentException(
                  "Can not delete artifact type " + artType.getName() + " because there are " + rSet.getInt("artCount") + " existing artifacts of this type.");
         }
      } finally {
         DbUtil.close(chStmt);
      }

      ConnectionHandler.runPreparedUpdate(DELETE_VALID_REL_GAMMAS, SQL3DataType.INTEGER, artTypeId);
      ConnectionHandler.runPreparedUpdate(DELETE_VALID_ATTRIBUTE_GAMMAS, SQL3DataType.INTEGER, artTypeId);
      ConnectionHandler.runPreparedUpdate(DELETE_VALID_REL, SQL3DataType.INTEGER, artTypeId);
      ConnectionHandler.runPreparedUpdate(DELETE_VALID_ATTRIBUTE, SQL3DataType.INTEGER, artTypeId);
      ConnectionHandler.runPreparedUpdate(DELETE_ARIFACT_TYPE_GAMMAS, SQL3DataType.INTEGER, artTypeId);
      ConnectionHandler.runPreparedUpdate(DELETE_ARIFACT_TYPE, SQL3DataType.INTEGER, artTypeId);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return "<XWidget xwidgetType=\"XArtifactTypeListViewer\" displayName=\"Artifact Type\" />";
   }
}