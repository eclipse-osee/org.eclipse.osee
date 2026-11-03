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
package org.eclipse.osee.framework.ui.skynet.artifact.editor.header;

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.util.ReservedCharacters;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXCopyIdHyperlinkWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * Creates the common header for all Artifact Editor tabs: title, image, info row (View Applicability + Branch/Type/ID),
 * and toolbar.
 *
 * @author Donald G. Dunne
 */
public class ArtEdTabHeader {

   private final ArtifactEditor editor;
   private final Artifact artifact;
   private ArtEdViewApplicability applPart;
   private org.eclipse.ui.forms.widgets.FormText infoText;
   private Composite labelRow;

   public ArtEdTabHeader(ArtifactEditor editor) {
      this.editor = editor;
      this.artifact = editor.getArtifactFromEditorInput();
   }

   /**
    * Creates the common header on the form: artifact title, image, info row, and section label.
    *
    * @param form the scrolled form
    * @param toolkit the form toolkit
    * @param sectionLabel the label for this tab's section (e.g., "Attributes", "Relations", "Details")
    * @return the body composite below the header where tab content should be placed
    */
   public Composite createHeader(ScrolledForm form, FormToolkit toolkit, String sectionLabel) {
      Composite body = form.getBody();
      GridLayout layout = new GridLayout(1, false);
      layout.marginHeight = 10;
      layout.marginWidth = 6;
      body.setLayout(layout);
      body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      // Title and image
      form.setText(artifact.getName());
      Image image = ArtifactImageManager.getImage(artifact);
      if (image != null) {
         form.setImage(image);
      }

      // Info row: View Applicability (left) | Branch/Type (center) | ID + hyperlink (right)
      Composite infoRow = toolkit.createComposite(body, SWT.NONE);
      GridLayout infoLayout = new GridLayout(4, false);
      infoLayout.marginHeight = 2;
      infoLayout.marginWidth = 5;
      infoRow.setLayout(infoLayout);
      infoRow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

      // View Applicability on left (if applicable)
      BranchId branchId = artifact.getBranch();
      try {
         if (branchId.equals(CoreBranches.COMMON) || BranchManager.getBranchCategories(branchId).stream().anyMatch(
            c -> c.getName().contains("PLE"))) {
            applPart = new ArtEdViewApplicability(editor, toolkit, form, infoRow);
            applPart.create();
         }
      } catch (Exception ex) {
         // Skip applicability if unavailable
      }

      // Branch/Type in center
      infoText = toolkit.createFormText(infoRow, false);
      infoText.setText(getBranchTypeInfo(), true, false);
      infoText.setForeground(Displays.getSystemColor(SWT.COLOR_DARK_GRAY));
      infoText.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

      // Copy ID hyperlink + ID value on right
      XXCopyIdHyperlinkWidget.addTo(artifact, toolkit, infoRow);

      // Common toolbar actions (shared across all tabs)
      org.eclipse.jface.action.IToolBarManager toolBarMgr = form.getToolBarManager();
      toolBarMgr.removeAll();
      editor.getActionBarContributor().contributeToToolBar(toolBarMgr);
      toolBarMgr.update(true);
      form.reflow(true);

      // Content area with outer border
      Composite contentArea = new Composite(body, SWT.BORDER);
      GridLayout contentLayout = new GridLayout(1, false);
      contentLayout.marginWidth = 0;
      contentLayout.marginHeight = 0;
      contentLayout.verticalSpacing = 0;
      contentArea.setLayout(contentLayout);
      contentArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      contentArea.setBackground(body.getDisplay().getSystemColor(SWT.COLOR_WHITE));

      // Section label row with optional section-specific toolbar
      labelRow = new Composite(contentArea, SWT.NONE);
      GridLayout labelLayout = new GridLayout(2, false);
      labelLayout.marginHeight = 4;
      labelLayout.marginWidth = 8;
      labelRow.setLayout(labelLayout);
      labelRow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      labelRow.setBackground(body.getDisplay().getSystemColor(SWT.COLOR_WHITE));

      Label sectionLbl = new Label(labelRow, SWT.NONE);
      sectionLbl.setText(sectionLabel);
      sectionLbl.setFont(FontManager.getFont("", 11, SWT.BOLD));
      sectionLbl.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
      sectionLbl.setBackground(body.getDisplay().getSystemColor(SWT.COLOR_WHITE));

      // Separator below label
      Label separator = new Label(contentArea, SWT.SEPARATOR | SWT.HORIZONTAL);
      separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

      // Middle border
      Composite middleBorder = new Composite(contentArea, SWT.BORDER);
      GridLayout middleLayout = new GridLayout(1, false);
      middleLayout.marginWidth = 0;
      middleLayout.marginHeight = 0;
      middleBorder.setLayout(middleLayout);
      middleBorder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      middleBorder.setBackground(body.getDisplay().getSystemColor(SWT.COLOR_WHITE));

      // Inner border — this is what tabs put their content into
      Composite bodyArea = new Composite(middleBorder, SWT.BORDER);
      GridLayout bodyLayout = new GridLayout(1, false);
      bodyLayout.marginWidth = 0;
      bodyLayout.marginHeight = 0;
      bodyArea.setLayout(bodyLayout);
      bodyArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      bodyArea.setBackground(body.getDisplay().getSystemColor(SWT.COLOR_WHITE));

      return bodyArea;
   }

   /**
    * @return the label row composite (2-column) for adding section-specific toolbar actions on the right side
    */
   public Composite getLabelRow() {
      return labelRow;
   }

   public void refresh() {
      if (infoText != null && !infoText.isDisposed()) {
         infoText.setText(getBranchTypeInfo(), true, false);
      }
      if (applPart != null) {
         applPart.refresh();
      }
   }

   private String getBranchTypeInfo() {
      try {
         String lockedStr = getLockedString();
         String deletedStr = !artifact.isDeleted() ? "" : "<b>ARTIFACT DELETED - </b> ";
         return String.format("<form><p>%s%s<b>Branch:</b> %s <b>Type:</b> %s</p></form>", lockedStr,
            deletedStr, ReservedCharacters.encodeXmlEntities(artifact.getBranchToken().getShortName()),
            artifact.getArtifactTypeName());
      } catch (Exception ex) {
         return "<form><p>Error loading info</p></form>";
      }
   }

   private String getIdInfo() {
      try {
         return String.format("<form><p><b>ID:</b> %s</p></form>", artifact.getIdString());
      } catch (Exception ex) {
         return "<form><p></p></form>";
      }
   }

   private String getLockedString() {
      ArtifactToken subject = ServiceUtil.accessControlService().getSubjectFromLockedObject(artifact);
      if (subject.isValid()) {
         return "<b>LOCKED:</b> " + subject.getName() + " ";
      }
      return "";
   }

}
