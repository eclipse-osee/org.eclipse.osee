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
package org.eclipse.osee.ats.ide.column;

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Morgan E. Cook
 */
public class RelatedArtifactLastModifiedByColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static RelatedArtifactLastModifiedByColumn instance = new RelatedArtifactLastModifiedByColumn();

   public static RelatedArtifactLastModifiedByColumn getInstance() {
      return instance;
   }

   private RelatedArtifactLastModifiedByColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".RelatedArtifactLastModifiedByColumn",
         "Related Artifact Last Modified By", 75, XViewerAlign.Left, false, SortDataType.String, false,
         "Shows the last person to modify the related artifact");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public RelatedArtifactLastModifiedByColumn copy() {
      RelatedArtifactLastModifiedByColumn newXCol = new RelatedArtifactLastModifiedByColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            ArtifactId refArtId =
               AtsClientService.get().getQueryServiceClient().getArtifact(element).getSoleAttributeValue(
                  AtsAttributeTypes.TaskToChangedArtifactReference, ArtifactId.SENTINEL);
            if (refArtId.isValid()) {
               Artifact refArt = AtsClientService.get().getQueryServiceClient().getArtifact(refArtId);
               if (refArt != null) {
                  return refArt.getLastModifiedBy().toString();
               }
            }
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return "";

   }

}
