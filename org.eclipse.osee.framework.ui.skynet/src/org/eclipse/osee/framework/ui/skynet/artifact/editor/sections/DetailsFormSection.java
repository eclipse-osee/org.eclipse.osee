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
package org.eclipse.osee.framework.ui.skynet.artifact.editor.sections;

import java.util.Date;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Roberto E. Escobar
 */
public class DetailsFormSection extends ArtifactEditorFormSection {

   private FormText formText;

   public DetailsFormSection(ArtifactEditor editor, Composite parent, FormToolkit toolkit, int style) {
      super(editor, parent, toolkit, style);
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.AbstractFormPart#initialize(org.eclipse.ui.forms.IManagedForm)
    */
   @Override
   public void initialize(IManagedForm form) {
      super.initialize(form);
      Section section = getSection();
      section.setText("Details");
      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      updateText(true);
   }

   private void updateText(boolean isCreate) {
      if (isCreate) {
         final FormToolkit toolkit = getManagedForm().getToolkit();
         Composite composite = toolkit.createComposite(getSection(), toolkit.getBorderStyle() | SWT.WRAP);
         composite.setLayout(new GridLayout());
         composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

         formText = toolkit.createFormText(composite, false);
         GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
         gd.widthHint = 200;
         formText.setLayoutData(gd);

         getSection().setClient(composite);
         toolkit.paintBordersFor(composite);
      }

      if (Widgets.isAccessible(formText)) {
         try {
            formText.setText(getDetailsText(getEditorInput().getArtifact()), true, true);
         } catch (Exception ex) {
            formText.setText(Lib.exceptionToString(ex), false, false);
         }
         getManagedForm().reflow(true);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.AbstractFormPart#dispose()
    */
   @Override
   public void dispose() {
      if (formText != null && !formText.isDisposed()) {
         formText.dispose();
      }
      super.dispose();
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.AbstractFormPart#refresh()
    */
   @Override
   public void refresh() {
      super.refresh();
      updateText(false);
   }

   private String getDetailsText(Artifact artifact) {
      String template = "<p><b>%s:</b> %s</p>";
      StringBuilder sb = new StringBuilder();
      sb.append("<form>");

      if (artifact != null) {
         sb.append(String.format(template, "GUID", Xml.escape(artifact.getGuid())));
         sb.append(String.format(template, "HRID", Xml.escape(artifact.getHumanReadableId())));
         sb.append(String.format(template, "Branch", Xml.escape(artifact.getBranch().toString())));
         sb.append(String.format(template, "Branch Id", artifact.getBranch().getBranchId()));
         sb.append(String.format(template, "Artifact Id", artifact.getArtId()));
         sb.append(String.format(template, "Artifact Type Name", Xml.escape(artifact.getArtifactTypeName())));
         sb.append(String.format(template, "Artifact Type Id", artifact.getArtTypeId()));
         sb.append(String.format(template, "Gamma Id", artifact.getGammaId()));
         sb.append(String.format(template, "Historical", artifact.isHistorical()));
         sb.append(String.format(template, "Deleted", artifact.isDeleted()));
         sb.append(String.format(template, "Revision", artifact.getTransactionNumber()));
         sb.append(String.format(template, "Read Only", artifact.isReadOnly()));
         Date lastModified = null;
         try {
            lastModified = artifact.getLastModified();
         } catch (Exception ex) {

         }
         sb.append(String.format(template, "Last Modified",
               lastModified != null ? String.valueOf(lastModified) : "Error - unknown"));
         User lastAuthor = null;
         try {
            lastAuthor = artifact.getLastModifiedBy();
         } catch (Exception ex) {

         }
         sb.append(String.format(template, "Last Modified By", lastAuthor != null ? lastAuthor : "Error - unknown"));
      } else {
         sb.append(String.format(template, "Artifact", "null"));
      }
      sb.append("</form>");
      return sb.toString();
   }
}
