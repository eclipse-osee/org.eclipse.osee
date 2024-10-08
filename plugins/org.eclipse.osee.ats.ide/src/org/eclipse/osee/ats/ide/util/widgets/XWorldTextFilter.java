/*********************************************************************
 * Copyright (c) 2015 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.util.widgets;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerTextFilter;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;

/**
 * @author Megumi Telles
 */
public class XWorldTextFilter extends XViewerTextFilter {

   public XWorldTextFilter(XViewer xViewer) {
      super(xViewer);
   }

   @Override
   public boolean select(Viewer viewer, Object parentElement, Object element) {
      if (parentElement instanceof Artifact) {
         Artifact parent = AtsApiService.get().getQueryServiceIde().getArtifact(parentElement);
         if (element instanceof Artifact) {
            Artifact elem = AtsApiService.get().getQueryServiceIde().getArtifact(element);
            for (RelationLink relation : parent.getRelationsAll(DeletionFlag.EXCLUDE_DELETED)) {
               // BArtifact of parentElement
               if (relation.getArtifactIdB().equals(elem)) {
                  return true;
               }
            }
         }
      }
      return super.select(viewer, parentElement, element);
   }
}
