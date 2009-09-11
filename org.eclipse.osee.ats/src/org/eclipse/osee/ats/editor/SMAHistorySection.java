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
package org.eclipse.osee.ats.editor;

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultsComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Donald G. Dunne
 */
public class SMAHistorySection extends SectionPart {

   private final SMAEditor editor;

   public SMAHistorySection(SMAEditor editor, Composite parent, FormToolkit toolkit, int style) {
      super(parent, toolkit, style | Section.TWISTIE | Section.TITLE_BAR);
      this.editor = editor;
   }

   @Override
   public void initialize(IManagedForm form) {
      super.initialize(form);
      Section section = getSection();
      section.setText("History");
      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      updateText(true);
   }

   private void updateText(boolean isCreate) {
      if (isCreate) {
         SMAManager smaMgr = editor.getSmaMgr();
         final FormToolkit toolkit = getManagedForm().getToolkit();
         Composite composite = toolkit.createComposite(getSection(), toolkit.getBorderStyle() | SWT.WRAP);
         composite.setLayout(new GridLayout());
         composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

         XResultsComposite xResultsComp = new XResultsComposite(composite, SWT.BORDER);
         xResultsComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
         GridData gd = new GridData(GridData.FILL_BOTH);
         gd.heightHint = 500;
         xResultsComp.setLayoutData(gd);
         try {
            xResultsComp.setHtmlText(smaMgr.getLog().getHtml(true), smaMgr.getSma().getArtifactTypeName() + " History");
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
         }

         Label button = toolkit.createLabel(composite, "   ", SWT.NONE);
         button.setText("    ");
         final SMAManager fSmaMgr = smaMgr;
         button.addListener(SWT.MouseDoubleClick, new Listener() {
            @Override
            public void handleEvent(Event event) {
               try {
                  ArtifactEditor.editArtifact(fSmaMgr.getSma());
               } catch (OseeCoreException ex) {
                  OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });

         getSection().setClient(composite);
         toolkit.paintBordersFor(composite);
      }

   }

   @Override
   public void dispose() {
      super.dispose();
   }

   @Override
   public void refresh() {
      super.refresh();
      updateText(false);
   }

}
