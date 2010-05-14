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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.conflict.ArtifactConflict;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.ui.plugin.util.AbstractSelectionEnabledHandler;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

class DiffHandler extends AbstractSelectionEnabledHandler {
      private final int diffToShow;
      private AttributeConflict attributeConflict;
      private ArtifactConflict artifactConflict;
      private List<Artifact> artifacts;
      private MergeXWidget mergeXWidget;

      public DiffHandler(MenuManager menuManager, int diffToShow, MergeXWidget mergeXWidget) {
         super(menuManager);
         this.diffToShow = diffToShow;
         this.mergeXWidget = mergeXWidget;
      }

      @Override
      public Object execute(ExecutionEvent event) throws ExecutionException {
         try {
            if (attributeConflict != null) {
               switch (diffToShow) {
                  case 1:
                     MergeUtility.showCompareFile(
                           MergeUtility.getStartArtifact(attributeConflict),
                           attributeConflict.getSourceArtifact(),
                           "Source_Diff_For_" + attributeConflict.getArtifact().getSafeName() + new Date().toString().replaceAll(
                                 ":", ";") + ".xml");
                     break;
                  case 2:
                     MergeUtility.showCompareFile(
                           MergeUtility.getStartArtifact(attributeConflict),
                           attributeConflict.getDestArtifact(),
                           "Destination_Diff_For_" + attributeConflict.getArtifact().getSafeName() + new Date().toString().replaceAll(
                                 ":", ";") + ".xml");
                     break;
                  case 3:
                     MergeUtility.showCompareFile(
                           attributeConflict.getSourceArtifact(),
                           attributeConflict.getDestArtifact(),
                           "Source_Destination_Diff_For_" + attributeConflict.getArtifact().getSafeName() + new Date().toString().replaceAll(
                                 ":", ";") + ".xml");
                     break;
                  case 4:
                     if (attributeConflict.wordMarkupPresent()) {
                        throw new OseeCoreException(AttributeConflict.DIFF_MERGE_MARKUP);
                     }
                     MergeUtility.showCompareFile(
                           attributeConflict.getSourceArtifact(),
                           attributeConflict.getArtifact(),
                           "Source_Merge_Diff_For_" + attributeConflict.getArtifact().getSafeName() + new Date().toString().replaceAll(
                                 ":", ";") + ".xml");
                     break;
                  case 5:
                     if (attributeConflict.wordMarkupPresent()) {
                        throw new OseeCoreException(AttributeConflict.DIFF_MERGE_MARKUP);
                     }
                     MergeUtility.showCompareFile(
                           attributeConflict.getDestArtifact(),
                           attributeConflict.getArtifact(),
                           "Destination_Merge_Diff_For_" + attributeConflict.getArtifact().getSafeName() + new Date().toString().replaceAll(
                                 ":", ";") + ".xml");
                     break;
               }
            } else if (artifactConflict != null) {
               if (diffToShow == 1) {
                  MergeUtility.showCompareFile(
                        artifactConflict.getSourceArtifact(),
                        MergeUtility.getStartArtifact(artifactConflict),
                        "Source_Diff_For_" + artifactConflict.getArtifact().getSafeName() + new Date().toString().replaceAll(
                              ":", ";") + ".xml");
               }
               if (diffToShow == 2) {
                  MergeUtility.showCompareFile(
                        artifactConflict.getDestArtifact(),
                        MergeUtility.getStartArtifact(artifactConflict),
                        "Destination_Diff_For_" + artifactConflict.getArtifact().getSafeName() + new Date().toString().replaceAll(
                              ":", ";") + ".xml");
               }
            }
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
         return null;
      }

      @Override
      public boolean isEnabledWithException(IStructuredSelection structuredSelection) throws OseeCoreException {
         artifacts = new LinkedList<Artifact>();
         List<Conflict> conflicts = mergeXWidget.getSelectedConflicts();
         if (conflicts.size() != 1) {
            return false;
         }
         if (conflicts.get(0) instanceof AttributeConflict) {
            attributeConflict = (AttributeConflict) conflicts.get(0);
            artifactConflict = null;
            try {
               switch (diffToShow) {
                  case 1:
                     if (attributeConflict.getSourceArtifact() != null && MergeUtility.getStartArtifact(attributeConflict) != null) {
                        artifacts.add(attributeConflict.getSourceArtifact());
                        artifacts.add(MergeUtility.getStartArtifact(attributeConflict));
                     } else {
                        return false;
                     }
                     break;
                  case 2:
                     if (attributeConflict.getDestArtifact() != null && MergeUtility.getStartArtifact(attributeConflict) != null) {
                        artifacts.add(attributeConflict.getDestArtifact());
                        artifacts.add(MergeUtility.getStartArtifact(attributeConflict));
                     } else {
                        return false;
                     }
                     break;
                  case 3:
                     if (attributeConflict.getDestArtifact() != null && attributeConflict.getSourceArtifact() != null) {
                        artifacts.add(attributeConflict.getSourceArtifact());
                        artifacts.add(attributeConflict.getDestArtifact());
                     } else {
                        return false;
                     }
                     break;
                  case 4:
                     if (attributeConflict.getSourceArtifact() != null && attributeConflict.getArtifact() != null) {
                        artifacts.add(attributeConflict.getSourceArtifact());
                        artifacts.add(attributeConflict.getArtifact());
                     } else {
                        return false;
                     }
                     break;
                  case 5:
                     if (attributeConflict.getDestArtifact() != null && attributeConflict.getArtifact() != null) {
                        artifacts.add(attributeConflict.getDestArtifact());
                        artifacts.add(attributeConflict.getArtifact());
                     } else {
                        return false;
                     }
                     break;
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }

         } else if (conflicts.get(0) instanceof ArtifactConflict) {
            attributeConflict = null;
            artifactConflict = (ArtifactConflict) conflicts.get(0);
            try {
               switch (diffToShow) {
                  case 1:
                     if (artifactConflict.getSourceArtifact() != null && conflicts.get(0).statusNotResolvable() && MergeUtility.getStartArtifact(artifactConflict) != null) {
                        artifacts.add(artifactConflict.getSourceArtifact());
                        artifacts.add(MergeUtility.getStartArtifact(artifactConflict));
                     } else {
                        return false;
                     }
                     break;
                  case 2:
                     if (artifactConflict.getDestArtifact() != null && conflicts.get(0).statusInformational() && MergeUtility.getStartArtifact(artifactConflict) != null) {
                        artifacts.add(artifactConflict.getDestArtifact());
                        artifacts.add(MergeUtility.getStartArtifact(artifactConflict));
                     } else {
                        return false;
                     }
                     break;
                  case 3:
                     return false;
                  case 4:
                     return false;
                  case 5:
                     return false;
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }

         }
         return AccessControlManager.checkObjectListPermission(artifacts, PermissionEnum.READ);
      }
   }