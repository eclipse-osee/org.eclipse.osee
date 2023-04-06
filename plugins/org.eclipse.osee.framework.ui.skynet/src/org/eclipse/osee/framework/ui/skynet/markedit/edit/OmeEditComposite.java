/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.framework.ui.skynet.markedit.edit;

import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.markedit.model.AbstractOmeData;
import org.eclipse.osee.framework.ui.skynet.markedit.model.ArtOmeData;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextOseeLinkListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class OmeEditComposite extends Composite {

   protected Browser browser;
   private XText textWidget;
   private final AbstractOmeData omeData;

   public OmeEditComposite(Composite parent, int style, OmeEditTab omeEditTab, AbstractOmeData omeData) {
      super(parent, style);
      this.omeData = omeData;

      setLayout(new GridLayout(omeData.isEditable() ? 1 : 2, false));
      setLayoutData(new GridData(GridData.FILL_BOTH));

      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      layout.verticalSpacing = 0;
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      parent.setLayout(layout);
      parent.setLayoutData(new GridData(GridData.FILL_BOTH));

      try {
         boolean enabled = omeData.isEditable();

         if (!enabled) {
            Label lockLabel = new Label(this, SWT.PUSH);
            lockLabel.setImage(ImageManager.getImage(FrameworkImage.LOCK_LOCKED));
         }

         textWidget = omeData.createXText(enabled);
         textWidget.setVerticalLabel(true);
         textWidget.setFillHorizontally(true);
         textWidget.setFillVertically(true);
         textWidget.createWidgets(this, 1);
         textWidget.setEditable(enabled);
         omeData.uponCreate(textWidget);
         omeData.load();
         GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
         textWidget.getStyledText().setLayoutData(gd);
         XTextOseeLinkListener linkListener =
            new XTextOseeLinkListener(textWidget, ((ArtOmeData) omeData).getArtifact().getBranch());
         textWidget.getStyledText().addModifyListener(linkListener);
         textWidget.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               omeData.setMdContent(textWidget.get());
               ((IDirtiableEditor) omeEditTab.getEditor()).onDirtied();
            }
         });
         refresh();
      } catch (SWTError e) {
         // do nothing
      }

   }

   public void refresh() {
      omeData.load();
   }

   public boolean isDirty() {
      return omeData.isDirty();
   }

   public void doSave() {
      omeData.doSave();
   }

   public XText getText() {
      return textWidget;
   }

   public void appendText(String text) {
      textWidget.set(omeData.getMdContent() + "\n\n" + text);
   }
}
