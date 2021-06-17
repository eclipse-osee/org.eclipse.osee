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
package org.eclipse.osee.ats.ide.workflow.cr.sibling;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author Donald G. Dunne
 */
public class XSiblingActionBar {

   private Label extraInfoLabel;
   private ToolItem refreshItem;
   private ToolItem addItem;
   private final XSiblingWorldWidget siblingWorldWidget;

   public XSiblingActionBar(XSiblingWorldWidget siblingWorldWidget) {
      this.siblingWorldWidget = siblingWorldWidget;
   }

   public ToolBar createTaskActionBar(Composite parent) {

      Composite bComp = new Composite(parent, SWT.NONE);
      bComp.setLayout(new GridLayout(2, false));
      bComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      Composite leftComp = new Composite(bComp, SWT.NONE);
      leftComp.setLayout(new GridLayout());
      leftComp.setLayoutData(new GridData(GridData.BEGINNING | GridData.FILL_HORIZONTAL));

      extraInfoLabel = new Label(leftComp, SWT.NONE);
      extraInfoLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      extraInfoLabel.setText("");
      extraInfoLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
      siblingWorldWidget.setExtraInfoLabel(extraInfoLabel);

      Composite rightComp = new Composite(bComp, SWT.NONE);
      rightComp.setLayout(new GridLayout());
      rightComp.setLayoutData(new GridData(GridData.END));

      ToolBar toolBar = new ToolBar(rightComp, SWT.FLAT | SWT.RIGHT);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      toolBar.setLayoutData(gd);

      addItem = new ToolItem(toolBar, SWT.PUSH);
      addItem.setImage(ImageManager.getImage(AtsImage.GLOBE));
      addItem.setToolTipText("Open in ATS World Editor");
      addItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            Collection<IAtsWorkItem> items = siblingWorldWidget.getItems();
            WorldEditorSimpleProvider provider =
               new WorldEditorSimpleProvider("Sibling Workflows", Collections.castAll(items));
            WorldEditor.open(provider);
         }

      });

      addItem = new ToolItem(toolBar, SWT.PUSH);
      addItem.setImage(ImageManager.getImage(AtsImage.GLOBE_SELECT));
      addItem.setToolTipText("Open Selected in ATS World Editor");
      addItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            Collection<IAtsWorkItem> items =
               Collections.castAll(siblingWorldWidget.getxWorldViewer().getSelectedTeamWorkflowArtifacts());
            WorldEditorSimpleProvider provider =
               new WorldEditorSimpleProvider("Selected Sibling Workflows", Collections.castAll(items));
            WorldEditor.open(provider);
         }

      });

      new ToolItem(toolBar, SWT.SEPARATOR);

      refreshItem = new ToolItem(toolBar, SWT.PUSH);
      refreshItem.setImage(ImageManager.getImage(PluginUiImage.REFRESH));
      refreshItem.setToolTipText("Refresh");
      refreshItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            siblingWorldWidget.loadTable();
         }
      });

      new ToolItem(toolBar, SWT.SEPARATOR);

      return toolBar;
   }

}
