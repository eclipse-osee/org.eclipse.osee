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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class PurgeArtifactType extends AbstractBlam {
   public static final String DELETE_VALID_REL = "delete from osee_valid_relations where art_type_id = ?";
   public static final String DELETE_VALID_ATTRIBUTE = "delete from osee_valid_attributes where art_type_id = ?";
   public static final String COUNT_ARTIFACT_OCCURRENCE =
         "select count(1) AS artCount FROM osee_artifact where art_type_id = ?";
   public static final String DELETE_ARIFACT_TYPE = "delete from osee_artifact_type where art_type_id = ?";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      ArtifactType artType = variableMap.getArtifactType("Artifact Type");
      int artTypeId = artType.getArtTypeId();

      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      try {
         chStmt.runPreparedQuery(COUNT_ARTIFACT_OCCURRENCE, artTypeId);
         if (chStmt.next() && chStmt.getInt("artCount") != 0) {
            throw new IllegalArgumentException(
                  "Can not delete artifact type " + artType.getName() + " because there are " + chStmt.getInt("artCount") + " existing artifacts of this type.");
         }
      } finally {
         chStmt.close();
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