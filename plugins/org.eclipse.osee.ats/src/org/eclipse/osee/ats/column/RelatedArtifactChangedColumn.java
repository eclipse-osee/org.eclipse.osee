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

import java.util.Collection;
import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Morgan E. Cook
 */
public class RelatedArtifactChangedColumn extends XViewerAtsColumn implements IAtsXViewerPreComputedColumn {

   public static RelatedArtifactChangedColumn instance = new RelatedArtifactChangedColumn();

   public static RelatedArtifactChangedColumn getInstance() {
      return instance;
   }

   private RelatedArtifactChangedColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".RelatedArtifactChangedColumn", "Related Artifact Changed", 75,
         XViewerAlign.Left, false, SortDataType.String, false,
         "Committed - baseline/committed branch \nUnmodified - Related artifact has not changed " + "\n<date> - Related artifact has been modified after task at the specified date \nEmpty - There is no related artifact");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public RelatedArtifactChangedColumn copy() {
      RelatedArtifactChangedColumn newXCol = new RelatedArtifactChangedColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      for (Object object : objects) {
         String value = "";
         try {
            if (object instanceof Artifact) {
               ArtifactId refArtId =
                  AtsClientService.get().getQueryServiceClient().getArtifact(object).getSoleAttributeValue(
                     AtsAttributeTypes.TaskToChangedArtifactReference, ArtifactId.SENTINEL);
               if (refArtId.isValid()) {
                  Artifact refArt = AtsClientService.get().getQueryServiceClient().getArtifact(refArtId);
                  if (refArt != null) {
                     BranchId refBranch = refArt.getBranch();
                     if (refArt.isDeleted()) {
                        value = "Deleted";
                     } else if (BranchManager.getState(refBranch).isCommitted() || BranchManager.getType(
                        refBranch).isBaselineBranch()) {
                        value = "Commited";
                     } else if (refArt.getLastModified().after(
                        AtsClientService.get().getQueryServiceClient().getArtifact(object).getLastModified())) {
                        value = refArt.getLastModified().toString();
                     } else {
                        value = "Unmodified";
                     }
                  }
               }
            }
         } catch (OseeCoreException ex) {
            value = LogUtil.getCellExceptionString(ex);
         }
         Long key = getKey(object);
         preComputedValueMap.put(key, value);
      }
   }

}