/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.navigate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.review.ReviewDefectData;
import org.eclipse.osee.ats.api.review.ReviewDefectItem;
import org.eclipse.osee.ats.api.review.ReviewDefectPeerReviewData;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.Disposition;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.InjectionActivity;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.Severity;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.core.review.PeerReviewDefectXViewerColumns;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.defect.DefectDispositionToImage;
import org.eclipse.osee.ats.ide.util.widgets.defect.DefectSeverityToImage;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab.IResultsEditorLabelProvider;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.IResultsEditorTableListener;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewer;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewerLabelProvider;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

/**
 * Results editor for the user's open peer review defect items.
 *
 * @author Donald G. Dunne
 */
public final class ReviewDefectResultsEditor {

   private ReviewDefectResultsEditor() {
      // Utility class
   }

   public static void open(ReviewDefectData data) {
      open(data, "My Open Defect Items (Read-Only)");
   }

   public static void open(ReviewDefectData data, String editorName) {
      List<IResultsXViewerRow> rows = new ArrayList<>();
      for (ReviewDefectItem defect : data.getReviewDefectItems()) {
         ArtifactId peerReviewId = data.getDefectIdToPeerReviewId().get(defect.getId());
         Artifact peerReview =
            peerReviewId == null ? null : AtsApiService.get().getQueryServiceIde().getArtifact(peerReviewId);
         rows.add(new ResultsXViewerRow(toValues(defect, data.getDefectIdToPeerReviewData().get(defect.getId())),
            new ReviewDefectRow(peerReview, defect)));
      }
      ResultsEditorTableTab tableTab =
         new ResultsEditorTableTab("Defect Items", getColumns(), rows, null, getLabelProvider(),
            Arrays.asList(getListener()));
      tableTab.setHeaderText("Read-Only view of Defects. Double-click to open Review and Defect.");
      tableTab.setHeaderForeground(SWT.COLOR_RED);
      List<IResultsEditorTab> tabs = Arrays.asList(tableTab);
      Displays.ensureInDisplayThread(() -> ResultsEditor.open(new IResultsEditorProvider() {

         @Override
         public String getEditorName() {
            return editorName;
         }

         @Override
         public AtsImage getEditorImage() {
            return AtsImage.DEFECT;
         }

         @Override
         public List<IResultsEditorTab> getResultsEditorTabs() {
            return tabs;
         }
      }));
   }

   private static List<XViewerColumn> getColumns() {
      return Arrays.asList(PeerReviewDefectXViewerColumns.Peer_Review_Ats_Id_Col.copy(),
         PeerReviewDefectXViewerColumns.Peer_Review_Due_Date_Col.copy(),
         PeerReviewDefectXViewerColumns.Peer_Review_Title_Col.copy(), PeerReviewDefectXViewerColumns.DefectId_Col.copy(),
         PeerReviewDefectXViewerColumns.Severity_Col.copy(), PeerReviewDefectXViewerColumns.Disposition_Col.copy(),
         PeerReviewDefectXViewerColumns.Closed_Col.copy(), PeerReviewDefectXViewerColumns.Closed_By_Col.copy(),
         PeerReviewDefectXViewerColumns.User_Col.copy(), PeerReviewDefectXViewerColumns.Created_Date_Col.copy(),
         PeerReviewDefectXViewerColumns.Injection_Activity_Col.copy(),
         PeerReviewDefectXViewerColumns.Description_Col.copy(), PeerReviewDefectXViewerColumns.Location_Col.copy(),
         PeerReviewDefectXViewerColumns.Resolution_Col.copy(), PeerReviewDefectXViewerColumns.Resolved_By_Col.copy(),
         PeerReviewDefectXViewerColumns.Notes_Col.copy());
   }

   private static String[] toValues(ReviewDefectItem defect, ReviewDefectPeerReviewData reviewData) {
      return new String[] {reviewData == null ? "" : reviewData.getReviewAtsId(),
         reviewData == null || reviewData.getNeedByDate() == null ? "" : DateUtil.getMMDDYYHHMM(reviewData.getNeedByDate()),
         reviewData == null ? "" : reviewData.getTitle(), String.valueOf(defect.getId()), enumValue(defect.getSeverity(), Severity.None),
         enumValue(defect.getDisposition(), Disposition.None), String.valueOf(defect.isClosed()),
         getUserName(defect.getClosedUserId()), getUserName(defect.getUserId()), DateUtil.getMMDDYYHHMM(defect.getDate()),
         enumValue(defect.getInjectionActivity(), InjectionActivity.None), defect.getDescription(), defect.getLocation(),
         defect.getResolution(), getUserName(defect.getResolvedUserId()), defect.getNotes()};
   }

   private static String enumValue(Enum<?> value, Enum<?> noneValue) {
      return value == null || value.equals(noneValue) ? "" : value.name();
   }

   private static String getUserName(String userId) {
      if (Strings.isInValid(userId)) {
         return "";
      }
      AtsUser user = AtsApiService.get().getUserService().getUserByUserId(userId);
      return user == null ? "Unknown - " + userId : user.getName();
   }

   private static IResultsEditorTableListener getListener() {
      return new IResultsEditorTableListener() {

         @Override
         public void handleDoubleClick(ArrayList<ResultsXViewerRow> selectedRows) {
            if (selectedRows.isEmpty()) {
               return;
            }
            ReviewDefectRow row = (ReviewDefectRow) selectedRows.get(0).getData();
            if (row.peerReview == null) {
               return;
            }
            Jobs.startJob(new Job("Opening Peer Review Defect") {

               @Override
               protected IStatus run(IProgressMonitor monitor) {
                  IAtsWorkItem workItem = AtsApiService.get().getWorkItemService().getWorkItem(row.peerReview);
                  if (workItem != null) {
                     Displays.ensureInDisplayThread(() -> WorkflowEditor.editAndSelectDefect(workItem, row.defect));
                  }
                  return Status.OK_STATUS;
               }
            }, true);
         }
      };
   }

   private static IResultsEditorLabelProvider getLabelProvider() {
      return ResultsDefectLabelProvider::new;
   }

   private static class ResultsDefectLabelProvider extends ResultsXViewerLabelProvider {

      public ResultsDefectLabelProvider(ResultsXViewer xViewer) {
         super(xViewer);
      }

      @Override
      public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) {
         return ((ResultsXViewerRow) element).getValue(columnIndex);
      }

      @Override
      public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
         ReviewDefectItem defect = ((ReviewDefectRow) ((ResultsXViewerRow) element).getData()).defect;
         if (xCol.equals(PeerReviewDefectXViewerColumns.DefectId_Col)) {
            return ImageManager.getImage(defect.isClosed() ? AtsImage.DEFECT_COMPLETE : AtsImage.DEFECT);
         } else if (xCol.equals(PeerReviewDefectXViewerColumns.Severity_Col)) {
            return DefectSeverityToImage.getImage(defect.getSeverity());
         } else if (xCol.equals(PeerReviewDefectXViewerColumns.Injection_Activity_Col)) {
            return ImageManager.getImage(CoreImage.INFO_SM);
         } else if (xCol.equals(PeerReviewDefectXViewerColumns.Disposition_Col)) {
            return DefectDispositionToImage.getImage(defect.getDisposition());
         } else if (xCol.equals(PeerReviewDefectXViewerColumns.Closed_Col)) {
            return ImageManager.getImage(
               defect.isClosed() ? PluginUiImage.CHECKBOX_ENABLED : PluginUiImage.CHECKBOX_DISABLED);
         } else if (xCol.equals(PeerReviewDefectXViewerColumns.User_Col)) {
            return getUserImage(defect.getUserId());
         } else if (xCol.equals(PeerReviewDefectXViewerColumns.Closed_By_Col)) {
            return getUserImage(defect.getClosedUserId());
         } else if (xCol.equals(PeerReviewDefectXViewerColumns.Resolved_By_Col)) {
            return getUserImage(defect.getResolvedUserId());
         }
         return null;
      }

      private Image getUserImage(String userId) {
         if (Strings.isInValid(userId)) {
            return null;
         }
         try {
            UserToken user = OseeApiService.userSvc().getUserByUserId(userId);
            return ArtifactImageManager.getImage(user);
         } catch (OseeCoreException ex) {
            return null;
         }
      }
   }

   private static class ReviewDefectRow {
      private final Artifact peerReview;
      private final ReviewDefectItem defect;

      public ReviewDefectRow(Artifact peerReview, ReviewDefectItem defect) {
         this.peerReview = peerReview;
         this.defect = defect;
      }
   }
}
