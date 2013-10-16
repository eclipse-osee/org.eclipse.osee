/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.column;

import java.util.Date;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class EstimatedReleaseDateColumn extends AbstractWorkflowVersionDateColumn {

   public static EstimatedReleaseDateColumn instance = new EstimatedReleaseDateColumn();

   public static EstimatedReleaseDateColumn getInstance() {
      return instance;
   }

   private EstimatedReleaseDateColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".estimatedReleaseDate", AtsAttributeTypes.EstimatedReleaseDate);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public EstimatedReleaseDateColumn copy() {
      EstimatedReleaseDateColumn newXCol = new EstimatedReleaseDateColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   public static Date getDateFromWorkflow(Object object) throws OseeCoreException {
      return AbstractWorkflowVersionDateColumn.getDateFromWorkflow(AtsAttributeTypes.EstimatedReleaseDate, object);
   }

   public static Date getDateFromTargetedVersion(Object object) throws OseeCoreException {
      return AbstractWorkflowVersionDateColumn.getDateFromTargetedVersion(AtsAttributeTypes.EstimatedReleaseDate,
         object);
   }

   public static String getDateStrFromWorkflow(AbstractWorkflowArtifact artifact) throws OseeCoreException {
      return AbstractWorkflowVersionDateColumn.getDateStrFromWorkflow(AtsAttributeTypes.EstimatedReleaseDate, artifact);
   }

   public static String getDateStrFromTargetedVersion(AbstractWorkflowArtifact artifact) throws OseeCoreException {
      return AbstractWorkflowVersionDateColumn.getDateStrFromTargetedVersion(AtsAttributeTypes.EstimatedReleaseDate,
         artifact);
   }

   public static String getDateStr(AbstractWorkflowArtifact artifact) throws OseeCoreException {
      return AbstractWorkflowVersionDateColumn.getDateStr(AtsAttributeTypes.EstimatedReleaseDate, artifact);
   }
}
