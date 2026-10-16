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

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * Abstract base for all Artifact Editor tabs. Provides common header (title, image, info row, toolbar, section label)
 * via {@link ArtEdTabHeader}. Subclasses implement {@link #getTabName()}, {@link #createTabBody(Composite, FormToolkit)}
 * and optionally {@link #createSectionToolbar(Composite)}.
 *
 * @author Donald G. Dunne
 */
public abstract class ArtEdBaseTab extends FormPage {

   private ArtEdTabHeader tabHeader;
   private final ArtifactEditor artEditor;

   public ArtEdBaseTab(ArtifactEditor editor, String id, String tabTitle) {
      super(editor, id, tabTitle);
      this.artEditor = editor;
   }

   /**
    * Constructor for non-ArtifactEditor contexts. The common header won't be available.
    */
   protected ArtEdBaseTab(org.eclipse.ui.forms.editor.FormEditor editor, String id, String tabTitle) {
      super(editor, id, tabTitle);
      this.artEditor = editor instanceof ArtifactEditor ? (ArtifactEditor) editor : null;
   }

   /**
    * @return the label shown inside the content area border (e.g., "Attributes", "Relations", "Details")
    */
   protected abstract String getTabName();

   /**
    * Create the tab-specific content inside the content area (below the label/separator).
    *
    * @param parent the content area composite (has GridLayout 1-column, white background, bordered)
    * @param toolkit the form toolkit
    */
   protected abstract void createTabBody(Composite parent, FormToolkit toolkit);

   /**
    * Optionally add section-specific toolbar actions next to the section label. Default does nothing. The parent is a
    * 2-column composite: [label (left, grab)] [toolbar area (right)].
    *
    * @param toolbarParent the label row composite — add a ToolBarManager control here
    */
   protected void createSectionToolbar(Composite toolbarParent) {
      // default: no section toolbar
   }

   /**
    * Called on refresh. Subclasses should override to refresh their content. Default does nothing.
    */
   protected void refreshTabBody() {
      // default: nothing
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);
      initializeTab(managedForm);
   }

   /**
    * Creates the common header and tab body. Subclasses that need async loading (like ArtAttrTab) can override
    * createFormContent and call initializeTab() later after data is loaded.
    */
   protected void initializeTab(IManagedForm managedForm) {
      ScrolledForm form = managedForm.getForm();
      Composite body = form.getBody();
      body.setLayout(new GridLayout(1, false));
      body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      FormToolkit toolkit = managedForm.getToolkit();

      // Common header
      tabHeader = new ArtEdTabHeader(artEditor);
      Composite contentArea = tabHeader.createHeader(form, toolkit, getTabName());

      // Tab body first (so widgets are available for toolbar)
      createTabBody(contentArea, toolkit);

      // Section-specific toolbar (after body, so body widgets exist)
      createSectionToolbar(tabHeader.getLabelRow());
   }

   @Override
   public ArtifactEditor getEditor() {
      return artEditor;
   }

   public Artifact getArtifact() {
      return artEditor != null ? artEditor.getArtifactFromEditorInput() : null;
   }

   public ArtEdTabHeader getTabHeader() {
      return tabHeader;
   }

   public void refresh() {
      if (tabHeader != null) {
         tabHeader.refresh();
      }
      refreshTabBody();
   }
}
