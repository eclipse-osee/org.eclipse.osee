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

package org.eclipse.osee.framework.ui.skynet.widgets.xmerge;

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.conflict.ArtifactConflict;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.ui.plugin.util.AbstractSelectionEnabledHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

class DiffHandler extends AbstractSelectionEnabledHandler {
   private final int diffToShow;
   private AttributeConflict attributeConflict;
   private ArtifactConflict artifactConflict;
   private List<Artifact> artifacts;

   public DiffHandler(MenuManager menuManager, int diffToShow, MergeXWidget mergeXWidget) {
      super(menuManager);
      this.diffToShow = diffToShow;
   }

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
      if (attributeConflict != null) {
         switch (diffToShow) {
            case 1:
               MergeUtility.showCompareFile(MergeUtility.getStartArtifact(attributeConflict),
                  attributeConflict.getSourceArtifact(), "Source_Diff_For");
               break;
            case 2:
               MergeUtility.showCompareFile(MergeUtility.getCommonAncestor(attributeConflict),
                  attributeConflict.getDestArtifact(), "Destination_Diff_For");
               break;
            case 3:
               MergeUtility.showCompareFile(attributeConflict.getSourceArtifact(), attributeConflict.getDestArtifact(),
                  "Source_Destination_Diff_For");
               break;
            case 4:
               if (attributeConflict.wordMarkupPresent()) {
                  throw new OseeCoreException(AttributeConflict.DIFF_MERGE_MARKUP);
               }
               MergeUtility.showCompareFile(attributeConflict.getSourceArtifact(), attributeConflict.getArtifact(),
                  "Source_Merge_Diff_For");
               break;
            case 5:
               if (attributeConflict.wordMarkupPresent()) {
                  throw new OseeCoreException(AttributeConflict.DIFF_MERGE_MARKUP);
               }
               MergeUtility.showCompareFile(attributeConflict.getDestArtifact(), attributeConflict.getArtifact(),
                  "Destination_Merge_Diff_For");
               break;
         }
      } else if (artifactConflict != null) {
         if (diffToShow == 1) {
            MergeUtility.showCompareFile(artifactConflict.getSourceArtifact(),
               MergeUtility.getStartArtifact(artifactConflict), "Source_Diff_For");
         }
         if (diffToShow == 2) {
            MergeUtility.showCompareFile(artifactConflict.getDestArtifact(),
               MergeUtility.getStartArtifact(artifactConflict), "Destination_Diff_For");
         }
      }
      return null;
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {

      List<Conflict> conflicts = Handlers.getConflictsFromStructuredSelection(structuredSelection);
      if (conflicts.size() != 1) {
         return false;
      }

      if (conflicts.get(0) instanceof AttributeConflict) {
         attributeConflict = (AttributeConflict) conflicts.get(0);
         artifactConflict = null;
         boolean diffToShowChecker = diffToShowNullChecker();
         if (diffToShowChecker == false) {
            return diffToShowChecker;
         }
      } else if (conflicts.get(0) instanceof ArtifactConflict) {
         attributeConflict = null;
         artifactConflict = (ArtifactConflict) conflicts.get(0);

         boolean oneAndTwoChecker = diffToShowOneAndTwoChecker(conflicts);
         if (oneAndTwoChecker == false) {
            return oneAndTwoChecker;
         }
      }
      return true;
   }

   private boolean diffToShowOneAndTwoChecker(List<Conflict> conflicts) {
      try {

         if (diffToShow <= 5 && diffToShow >= 1) {
            if (diffToShow == 1) {
               if (artifactConflict.getSourceArtifact() == null || MergeUtility.getStartArtifact(
                  artifactConflict) == null) {
                  return false;
               }
            } else if (diffToShow == 2) {
               if (artifactConflict.getDestArtifact() == null || !conflicts.get(
                  0).getStatus().isInformational() || MergeUtility.getStartArtifact(artifactConflict) == null) {
                  return false;
               }
            } else {
               return false;
            }
         }

      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return true;
   }

   private boolean diffToShowNullChecker() {
      try {

         if (diffToShow <= 5 && diffToShow >= 1) {
            if (diffToShow == 1) {
               if (attributeConflict.getSourceArtifact() == null || MergeUtility.getStartArtifact(
                  attributeConflict) == null) {
                  return false;
               }
            } else if (diffToShow == 2) {
               if (attributeConflict.getDestArtifact() == null || MergeUtility.getStartArtifact(
                  attributeConflict) == null) {
                  return false;
               }
            } else if (diffToShow == 3) {
               if (attributeConflict.getDestArtifact() == null || attributeConflict.getSourceArtifact() == null) {
                  return false;
               }
            } else if (diffToShow == 4) {
               if (attributeConflict.getSourceArtifact() == null || attributeConflict.getArtifact() == null) {
                  return false;
               }
            } else {
               if (attributeConflict.getDestArtifact() == null || attributeConflict.getArtifact() == null) {
                  return false;
               }
            }
         }

      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return true;
   }
}