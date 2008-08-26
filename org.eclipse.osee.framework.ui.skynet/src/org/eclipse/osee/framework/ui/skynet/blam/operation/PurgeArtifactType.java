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
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Ryan D. Brooks
 */
public class PurgeArtifactType extends AbstractBlam {
   public static final String DELETE_VALID_REL = "delete from osee_define_valid_relations where art_type_id = ?";
   public static final String DELETE_VALID_ATTRIBUTE = "delete from osee_define_valid_attributes where art_type_id = ?";
   public static final String COUNT_ARTIFACT_OCCURRENCE =
         "select count(1) AS artCount FROM osee_define_artifact where art_type_id = ?";
   public static final String DELETE_ARIFACT_TYPE = "delete from osee_define_artifact_type where art_type_id = ?";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      ArtifactType artType = variableMap.getArtifactSubtypeDescriptor("Artifact Type");
      int artTypeId = artType.getArtTypeId();

      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt = ConnectionHandler.runPreparedQuery(COUNT_ARTIFACT_OCCURRENCE, artTypeId);
         ResultSet rSet = chStmt.getRset();
         if (rSet.next() && rSet.getInt("artCount") != 0) {
            throw new IllegalArgumentException(
                  "Can not delete artifact type " + artType.getName() + " because there are " + rSet.getInt("artCount") + " existing artifacts of this type.");
         }
      } finally {
         DbUtil.close(chStmt);
      }

      ConnectionHandler.runPreparedUpdate(DELETE_VALID_REL, artTypeId);
      ConnectionHandler.runPreparedUpdate(DELETE_VALID_ATTRIBUTE, artTypeId);
      ConnectionHandler.runPreparedUpdate(DELETE_ARIFACT_TYPE, artTypeId);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return "<XWidget xwidgetType=\"XArtifactTypeListViewer\" displayName=\"Artifact Type\" />";
   }
}