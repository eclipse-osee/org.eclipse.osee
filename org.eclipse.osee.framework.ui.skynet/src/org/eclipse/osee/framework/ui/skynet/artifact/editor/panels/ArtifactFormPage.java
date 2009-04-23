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
package org.eclipse.osee.framework.ui.skynet.artifact.editor.panels;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.RelationsComposite;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.implementations.NewArtifactEditor;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessage;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactFormPage extends FormPage {

   private List<SectionPart> sectionParts;

   public ArtifactFormPage(FormEditor editor, String id, String title) {
      super(editor, id, title);
      this.sectionParts = new ArrayList<SectionPart>();
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
    */
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

      updateTitle(form, true);
      updateImage(form, true);
      addArtifactInfoArea(toolkit, form, true);
      addToolBar(toolkit, form, true);
      addHeadingGradient(toolkit, form, true);
      addMessageDecoration(form);

      int sectionStyle = Section.TITLE_BAR | Section.TWISTIE | Section.CLIENT_INDENT | SWT.WRAP;

      sectionParts.add(new AttributesFormSection(getEditor(), form.getBody(), toolkit, sectionStyle));
      sectionParts.add(new RelationsFormSection(getEditor(), form.getBody(), toolkit, sectionStyle));
      sectionParts.add(new DetailsFormSection(getEditor(), form.getBody(), toolkit, sectionStyle));

      for (SectionPart part : sectionParts) {
         managedForm.addPart(part);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.editor.FormPage#getEditor()
    */
   @Override
   public NewArtifactEditor getEditor() {
      return (NewArtifactEditor) super.getEditor();
   }

   private void addMessageDecoration(ScrolledForm form) {
      form.getForm().addMessageHyperlinkListener(new HyperlinkAdapter() {

         public void linkActivated(HyperlinkEvent e) {
            String title = e.getLabel();
            Object href = e.getHref();
            if (href instanceof IMessage[]) {
               Point noteLocation = ((Control) e.widget).toDisplay(0, 0);
               noteLocation.x += 10;
               noteLocation.y += 10;

               MessageWithLinksNote note = new MessageWithLinksNote(getManagedForm(), title, (IMessage[]) href);
               note.setLocation(noteLocation);
               note.open();
            }
         }

      });
   }

   private void addToolBar(FormToolkit toolkit, ScrolledForm form, boolean add) {
      if (add) {
         ((NewArtifactEditor) getEditor()).getActionBarContributor().contributeToToolBar(form.getToolBarManager());
         form.getToolBarManager().update(true);
      } else {
         form.getToolBarManager().removeAll();
      }
      form.reflow(true);
   }

   private void updateTitle(ScrolledForm form, boolean addTitle) {
      if (addTitle) {
         form.setText(getEditorInput().getName());
      } else {
         form.setText(null);
      }
   }

   private void updateImage(ScrolledForm form, boolean addImage) {
      if (addImage)
         form.setImage(getEditor().getEditorInput().getImage());
      else
         form.setImage(null);
   }

   private String getArtifactShortInfo() {
      Artifact artifact = getEditor().getEditorInput().getArtifact();
      String description =
            String.format("<form><p><b>Branch:</b> %s <b>Type:</b> %s <b>HRID:</b> %s</p></form>",
                  artifact.getBranch().getBranchShortName(), artifact.getArtifactTypeName(),
                  artifact.getHumanReadableId());
      return description;
   }

   private void addArtifactInfoArea(FormToolkit toolkit, ScrolledForm form, boolean add) {
      if (add) {
         //         toolkit.getBorderStyle()
         Composite infoArea = toolkit.createComposite(form.getForm().getBody(), SWT.WRAP);
         infoArea.setLayout(ALayout.getZeroMarginLayout(2, false));
         infoArea.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true, false));

         Label label = toolkit.createLabel(infoArea, "", SWT.WRAP);
         label.setImage(MessageDialog.getImage(MessageDialog.DLG_IMG_MESSAGE_INFO));

         FormText text = toolkit.createFormText(infoArea, false);
         text.setText(getArtifactShortInfo(), true, false);
         text.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
         text.setToolTipText("The human readable id and database id for this artifact");

         //         toolkit.paintBordersFor(infoArea);
      } else {
      }
   }

   private void addHeadingGradient(FormToolkit toolkit, ScrolledForm form, boolean add) {
      FormColors colors = toolkit.getColors();
      Color top = colors.getColor(IFormColors.H_GRADIENT_END);
      Color bot = colors.getColor(IFormColors.H_GRADIENT_START);
      if (add)
         form.getForm().setTextBackground(new Color[] {top, bot}, new int[] {100}, true);
      else {
         form.getForm().setTextBackground(null, null, false);
         form.getForm().setBackground(colors.getBackground());
      }
      form.getForm().setHeadColor(IFormColors.H_BOTTOM_KEYLINE1,
            add ? colors.getColor(IFormColors.H_BOTTOM_KEYLINE1) : null);
      form.getForm().setHeadColor(IFormColors.H_BOTTOM_KEYLINE2,
            add ? colors.getColor(IFormColors.H_BOTTOM_KEYLINE2) : null);
      form.getForm().setHeadColor(IFormColors.H_HOVER_LIGHT, add ? colors.getColor(IFormColors.H_HOVER_LIGHT) : null);
      form.getForm().setHeadColor(IFormColors.H_HOVER_FULL, add ? colors.getColor(IFormColors.H_HOVER_FULL) : null);
      form.getForm().setHeadColor(IFormColors.TB_TOGGLE, add ? colors.getColor(IFormColors.TB_TOGGLE) : null);
      form.getForm().setHeadColor(IFormColors.TB_TOGGLE_HOVER,
            add ? colors.getColor(IFormColors.TB_TOGGLE_HOVER) : null);
      form.getForm().setSeparatorVisible(add);
      form.reflow(true);
      form.redraw();
   }

   public RelationsComposite getRelationsComposite() {
      return null;
   }

   public void refresh() {
   }

   private final class MessageWithLinksNote {
      private Shell shell;

      public MessageWithLinksNote(IManagedForm managedForm, String title, IMessage[] messages) {
         final ScrolledForm form = managedForm.getForm();
         final FormToolkit toolkit = managedForm.getToolkit();

         shell = new Shell(form.getShell(), SWT.ON_TOP | SWT.TOOL);
         shell.setImage(getImage(form.getMessageType()));
         shell.setText(title);
         shell.setLayout(new FillLayout());

         FormText text = toolkit.createFormText(shell, true);
         configureFormText(form.getForm(), text);
         text.setText(getMessageSummary(messages), true, false);
         shell.setLocation(0, 0);
      }

      public void setLocation(Point point) {
         shell.setLocation(point);
      }

      public void open() {
         shell.pack();
         shell.open();
      }

      private void configureFormText(final Form form, FormText text) {
         text.addHyperlinkListener(new HyperlinkAdapter() {
            public void linkActivated(HyperlinkEvent e) {
               String is = (String) e.getHref();
               try {
                  int index = Integer.parseInt(is);
                  IMessage[] messages = form.getChildrenMessages();
                  IMessage message = messages[index];
                  Control c = message.getControl();
                  ((FormText) e.widget).getShell().dispose();
                  if (c != null) c.setFocus();
               } catch (NumberFormatException ex) {
               }
            }
         });
         text.setImage("error", getImage(IMessageProvider.ERROR));
         text.setImage("warning", getImage(IMessageProvider.WARNING));
         text.setImage("info", getImage(IMessageProvider.INFORMATION));
      }

      private Image getImage(int type) {
         switch (type) {
            case IMessageProvider.ERROR:
               return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
            case IMessageProvider.WARNING:
               return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK);
            case IMessageProvider.INFORMATION:
               return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
         }
         return null;
      }

      private String getMessageSummary(IMessage[] messages) {
         StringWriter sw = new StringWriter();
         PrintWriter pw = new PrintWriter(sw);
         pw.println("<form>");
         for (int i = 0; i < messages.length; i++) {
            IMessage message = messages[i];
            pw.print("<li vspace=\"false\" style=\"image\" indent=\"16\" value=\"");
            switch (message.getMessageType()) {
               case IMessageProvider.ERROR:
                  pw.print("error");
                  break;
               case IMessageProvider.WARNING:
                  pw.print("warning");
                  break;
               case IMessageProvider.INFORMATION:
                  pw.print("info");
                  break;
            }
            pw.print("\"> <a href=\"");
            pw.print(i + "");
            pw.print("\">");
            if (message.getPrefix() != null) pw.print(message.getPrefix());
            pw.print(message.getMessage());
            pw.println("</a></li>");
         }
         pw.println("</form>");
         pw.flush();
         return sw.toString();
      }
   }
}
