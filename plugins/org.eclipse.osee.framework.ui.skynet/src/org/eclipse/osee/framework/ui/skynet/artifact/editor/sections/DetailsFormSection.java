/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.artifact.editor.sections;

import java.util.Map;
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Roberto E. Escobar
 */
public class DetailsFormSection extends ArtifactEditorFormSection {

   private Browser browser;
   private boolean sectionCreated = false;
   private Section section;

   public DetailsFormSection(ArtifactEditor editor, Composite parent, FormToolkit toolkit, int style) {
      super(editor, parent, toolkit, style);
   }

   @Override
   public void initialize(IManagedForm form) {
      super.initialize(form);
      section = getSection();
      section.setText("Details");
      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      // Only load when users selects section
      section.addListener(SWT.Activate, new Listener() {

         @Override
         public void handleEvent(Event e) {
            createSection();
         }
      });

   }

   private synchronized void createSection() {
      if (!sectionCreated) {
         final FormToolkit toolkit = getManagedForm().getToolkit();
         Composite composite = toolkit.createComposite(getSection(), toolkit.getBorderStyle() | SWT.WRAP);
         composite.setLayout(ALayout.getZeroMarginLayout());
         composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

         composite.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
               if (Widgets.isAccessible(browser)) {
                  browser.dispose();
               }
            }
         });

         GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
         gd.widthHint = 200;
         gd.heightHint = 250;

         try {
            browser = new Browser(composite, SWT.NONE);
            browser.setLayoutData(gd);
         } catch (SWTError e) {
            // do nothing
         }

         getSection().setClient(composite);
         toolkit.paintBordersFor(composite);
         sectionCreated = true;

         HelpUtil.setHelp(composite, OseeHelpContext.ARTIFACT_EDITOR__DETAILS);
         if (Widgets.isAccessible(browser)) {
            HelpUtil.setHelp(browser, OseeHelpContext.ARTIFACT_EDITOR__DETAILS);
         }
      }

      refresh();
   }

   @Override
   public void refresh() {
      if (Widgets.isAccessible(browser)) {
         try {
            FontData systemFont = browser.getDisplay().getSystemFont().getFontData()[0];
            Map<String, String> detailsKeyValues = Artifacts.getDetailsKeyValues(getEditorInput().getArtifact());
            browser.setText(
               Artifacts.getDetailsFormText(detailsKeyValues, systemFont.getName(), systemFont.getHeight()));
         } catch (Exception ex) {
            browser.setText(Lib.exceptionToString(ex));
         }
         getManagedForm().reflow(true);
      }
   }

}
