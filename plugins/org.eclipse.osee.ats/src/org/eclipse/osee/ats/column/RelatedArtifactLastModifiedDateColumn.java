/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.column;

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Morgan E. Cook
 */
public class RelatedArtifactLastModifiedDateColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static RelatedArtifactLastModifiedDateColumn instance = new RelatedArtifactLastModifiedDateColumn();

   public static RelatedArtifactLastModifiedDateColumn getInstance() {
      return instance;
   }

   private RelatedArtifactLastModifiedDateColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".RelatedArtifactLastModifiedDateColumn",
         "Related Artifact Last Modified Date", 75, XViewerAlign.Left, false, SortDataType.String, false,
         "Date of last time the related artifact was modified");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public RelatedArtifactLastModifiedDateColumn copy() {
      RelatedArtifactLastModifiedDateColumn newXCol = new RelatedArtifactLastModifiedDateColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            ArtifactId refArtId = ((Artifact) element).getSoleAttributeValue(
               AtsAttributeTypes.TaskToChangedArtifactReference, ArtifactId.SENTINEL);
            if (refArtId.isValid()) {
               Artifact refArt = (Artifact) AtsClientService.get().getQueryService().getArtifact(refArtId);
               if (refArt != null) {
                  return DateUtil.getMMDDYYHHMM(refArt.getLastModified());
               }
            }
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return "";
   }
}
