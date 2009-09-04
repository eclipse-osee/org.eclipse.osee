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
package org.eclipse.osee.framework.ui.skynet.artifact.editor.pages;

import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.RelationsComposite;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.parts.MessageSummaryNote;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.sections.AttributesFormSection;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.sections.DetailsFormSection;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.sections.RelationsFormSection;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessage;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public class ArtifactFormPage extends FormPage {

   private enum SectionEnum {
      Attributes, Relations, Details;
   }

   private final Map<SectionEnum, SectionPart> sectionParts;
   private FormText infoText;

   public ArtifactFormPage(FormEditor editor, String id, String title) {
      super(editor, id, title);
      this.sectionParts = new LinkedHashMap<SectionEnum, SectionPart>();
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);
      sectionParts.clear();

      final ScrolledForm form = managedForm.getForm();
      final FormToolkit toolkit = managedForm.getToolkit();

      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      layout.marginHeight = 10;
      layout.marginWidth = 6;
      layout.horizontalSpacing = 20;
      form.getBody().setLayout(layout);
      form.getBody().setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

      updateTitle(form);
      updateImage(form);
      updateArtifactInfoArea(toolkit, form, true);
      addToolBar(toolkit, form, true);
      FormsUtil.addHeadingGradient(toolkit, form, true);
      addMessageDecoration(form);

      int sectionStyle = Section.TITLE_BAR | Section.TWISTIE;

      sectionParts.put(SectionEnum.Attributes, new AttributesFormSection(getEditor(), form.getBody(), toolkit,
            sectionStyle | Section.EXPANDED));
      sectionParts.put(SectionEnum.Relations, new RelationsFormSection(getEditor(), form.getBody(), toolkit,
            sectionStyle));
      sectionParts.put(SectionEnum.Details, new DetailsFormSection(getEditor(), form.getBody(), toolkit, sectionStyle));

      for (SectionPart part : sectionParts.values()) {
         managedForm.addPart(part);
         Section section = part.getSection();
         section.marginWidth = 0;
         section.marginHeight = 2;
      }
      form.layout();
   }

   @Override
   public ArtifactEditor getEditor() {
      return (ArtifactEditor) super.getEditor();
   }

   private void addMessageDecoration(ScrolledForm form) {
      form.getForm().addMessageHyperlinkListener(new HyperlinkAdapter() {

         @Override
         public void linkActivated(HyperlinkEvent e) {
            String title = e.getLabel();
            Object href = e.getHref();
            if (href instanceof IMessage[]) {
               Point noteLocation = ((Control) e.widget).toDisplay(0, 0);
               noteLocation.x += 10;
               noteLocation.y += 10;

               MessageSummaryNote note = new MessageSummaryNote(getManagedForm(), title, (IMessage[]) href);
               note.setLocation(noteLocation);
               note.open();
            }
         }

      });
   }

   private void addToolBar(FormToolkit toolkit, ScrolledForm form, boolean add) {
      IToolBarManager manager = form.getToolBarManager();
      if (add) {
         manager.add(new RefreshAction());
         manager.add(new Separator());
         getEditor().getActionBarContributor().contributeToToolBar(manager);
         manager.update(true);
      } else {
         manager.removeAll();
      }
      form.reflow(true);
   }

   private void updateTitle(ScrolledForm form) {
      form.setText(getEditorInput().getName());
   }

   private void updateImage(ScrolledForm form) {
      form.setImage(getEditor().getEditorInput().getImage());
   }

   private String getArtifactShortInfo() {
      String description;
      try {
         Artifact artifact = getEditor().getEditorInput().getArtifact();
         description =
               String.format("<form><p>%s<b>Branch:</b> %s <b>Type:</b> %s <b>GUID:</b> %s</p></form>",
                     !artifact.isDeleted() ? "" : "<b>ARTIFACT DELETED - </b> ", artifact.getBranch().getShortName(),
                     artifact.getArtifactTypeName(), artifact.getGuid());
      } catch (Exception ex) {
         description = Lib.exceptionToString(ex);
      }
      return description;
   }

   private void updateArtifactInfoArea(FormToolkit toolkit, ScrolledForm form, boolean add) {
      if (add) {
         Composite infoArea = toolkit.createComposite(form.getForm().getBody(), SWT.WRAP);
         infoArea.setLayout(ALayout.getZeroMarginLayout(2, false));
         infoArea.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true, false));

         Label label = toolkit.createLabel(infoArea, "", SWT.WRAP);
         Image image;
         try {
            Artifact artifact = getEditor().getEditorInput().getArtifact();
            if (artifact.isDeleted()) {
               image = ImageManager.getImage(FrameworkImage.TRASH);
            } else {
               image = MessageDialog.getImage(MessageDialog.DLG_IMG_MESSAGE_INFO);
            }
         } catch (Exception ex) {
            image = MessageDialog.getImage(MessageDialog.DLG_IMG_MESSAGE_ERROR);
         }
         label.setImage(image);

         infoText = toolkit.createFormText(infoArea, false);
         infoText.setText(getArtifactShortInfo(), true, false);
         infoText.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
         infoText.setToolTipText("The human readable id and database id for this artifact");
      } else {
         infoText.setText(getArtifactShortInfo(), true, false);
      }
   }

   @Override
   public void dispose() {
      for (SectionPart part : sectionParts.values()) {
         part.dispose();
      }
      super.dispose();
   }

   public RelationsComposite getRelationsComposite() {
      SectionPart section = sectionParts.get(SectionEnum.Relations);
      if (section instanceof RelationsFormSection) {
         return ((RelationsFormSection) section).getRelationComposite();
      }
      return null;
   }

   public void refresh() {
      final ScrolledForm sForm = getManagedForm().getForm();
      updateTitle(sForm);
      updateImage(sForm);
      updateArtifactInfoArea(getManagedForm().getToolkit(), sForm, false);
      for (SectionPart part : sectionParts.values()) {
         part.refresh();
      }
      sForm.getBody().layout(true);
      sForm.reflow(true);
      getManagedForm().refresh();
   }

   @Override
   public void showBusy(boolean busy) {
      super.showBusy(busy);
      if (Widgets.isAccessible(getManagedForm().getForm())) {
         getManagedForm().getForm().getForm().setBusy(busy);
      }
   }

   private final class RefreshAction extends Action {

      public RefreshAction() {
         super();
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.REFRESH));
         setToolTipText("Refresh Editor");
      }

      @Override
      public void run() {
         refresh();
      }
   }
}
