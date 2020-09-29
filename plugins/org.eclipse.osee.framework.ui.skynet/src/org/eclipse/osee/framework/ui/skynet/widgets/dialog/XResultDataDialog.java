/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.plugin.util.IShellCloseEvent;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.HyperLinkLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * Dialog showing message with a details button that will open XResultDataUi with provided XResultData if chosen
 *
 * @author Donald G. Dunne
 */
public class XResultDataDialog extends MessageDialog {

   protected Composite areaComposite;
   protected Button okButton;
   protected Button detailsButton;

   private final List<IShellCloseEvent> closeEventListeners = new ArrayList<>();
   private final String dialogTitle;
   private XResultData detailsResultsData;

   /**
    * @param detailsResultsData - Will open if Show Details is selected
    */
   public XResultDataDialog(String dialogTitle, String dialogMessage, XResultData detailsResultsData) {
      this(Displays.getActiveShell(), dialogTitle, null, dialogMessage, MessageDialog.QUESTION,
         new String[] {"OK", "Cancel"}, 0);
      this.detailsResultsData = detailsResultsData;
   }

   public XResultDataDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
      super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
         defaultIndex);
      this.dialogTitle = dialogTitle;
   }

   protected Composite customAreaParent;

   @Override
   protected Control createCustomArea(Composite parent) {
      this.customAreaParent = parent;
      areaComposite = new Composite(parent, SWT.NONE);
      areaComposite.setLayout(new GridLayout(2, false));
      GridData gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
      gd.widthHint = 600;
      areaComposite.setLayoutData(gd);

      createOpenInEditorHyperlink(parent);
      createExtendedArea(areaComposite);
      areaComposite.layout();
      parent.layout();
      return areaComposite;
   }

   protected void createOpenInEditorHyperlink(Composite parent) {
      HyperLinkLabel edit = new HyperLinkLabel(parent, SWT.None);
      edit.setText("Show Details");
      edit.addListener(SWT.MouseUp, new Listener() {

         @Override
         public void handleEvent(Event event) {
            XResultDataUI.report(detailsResultsData, dialogTitle);
            close();
         }
      });
   }

   protected void createExtendedArea(Composite parent) {
      // provided for subclass implementation
   }

   public void setSelectionListener(SelectionListener listener) {
      for (int i = 0; i < getButtonLabels().length; i++) {
         Button button = getButton(i);
         button.addSelectionListener(listener);
      }
   }

   @Override
   protected void handleShellCloseEvent() {
      super.handleShellCloseEvent();
      for (IShellCloseEvent event : closeEventListeners) {
         event.onClose();
      }
   }

   public void addShellCloseEventListeners(IShellCloseEvent event) {
      closeEventListeners.add(event);
   }

   public static void open(XResultData rd, String title, String format, Object... data) {
      XResultDataDialog diag = new XResultDataDialog(title, String.format(format, data), rd);
      diag.open();
   }

}
