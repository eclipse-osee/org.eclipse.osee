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
package org.eclipse.osee.framework.ui.skynet.skywalker;

import java.util.Arrays;
import java.util.List;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.skywalker.ISkyWalkerOptionsChangeListener.ModType;
import org.eclipse.osee.framework.ui.skynet.skywalker.SkyWalkerOptions.LinkName;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XRadioButtons;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;

/**
 * @author Donald G. Dunne
 */
public class SkyWalkerLayoutTabItem {

   private TreeViewer treeViewer;
   private final Spinner levelSpinner;
   private final SkyWalkerOptions options;
   private final XRadioButtons radioButtons;

   public SkyWalkerLayoutTabItem(org.eclipse.swt.widgets.TabFolder tabFolder, final SkyWalkerOptions options) {

      this.options = options;
      TabItem item = new TabItem(tabFolder, SWT.NONE);
      item.setText("Layout");

      Composite comp = new Composite(tabFolder, SWT.BORDER);
      comp.setLayout(ALayout.getZeroMarginLayout());
      comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      Composite levelComp = new Composite(comp, SWT.BORDER);
      levelComp.setLayout(ALayout.getZeroMarginLayout(2, false));
      levelComp.setLayoutData(new GridData());

      (new Label(levelComp, SWT.NONE)).setText("Level:  ");
      levelSpinner = new Spinner(levelComp, SWT.BORDER);
      levelSpinner.setMinimum(0);
      levelSpinner.setMaximum(4);
      levelSpinner.setIncrement(1);
      levelSpinner.setPageIncrement(1);
      levelSpinner.pack();
      levelSpinner.addModifyListener(new ModifyListener() {
         public void modifyText(ModifyEvent e) {
            options.setLevels(levelSpinner.getSelection());
         }
      });

      Composite treeComp = new Composite(comp, SWT.BORDER);
      treeComp.setLayout(ALayout.getZeroMarginLayout());
      treeComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      treeViewer = new TreeViewer(treeComp, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
      treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      treeViewer.setContentProvider(new ArrayTreeContentProvider());
      treeViewer.setLabelProvider(new LabelProvider() {

         public Image getImage(Object obj) {
            return null;
         }

         public String getText(Object obj) {
            return options.getLayoutName((AbstractLayoutAlgorithm) obj);
         }
      });
      treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
         public void selectionChanged(SelectionChangedEvent event) {
            if (treeViewer.getSelection().isEmpty()) return;
            AbstractLayoutAlgorithm layout =
                  (AbstractLayoutAlgorithm) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement();
            if (layout != null) options.setLayout(layout);
         }
      });
      treeViewer.setInput(options.getLayouts());

      radioButtons = new XRadioButtons("Link Naming", "");
      radioButtons.setVertical(true, 1);
      radioButtons.setVerticalLabel(true);
      for (LinkName linkName : LinkName.values())
         radioButtons.addButton(linkName.name());
      radioButtons.createWidgets(comp, 1);
      radioButtons.setSelected(options.getLinkName().name());
      radioButtons.addXModifiedListener(new XModifiedListener() {
         public void widgetModified(org.eclipse.osee.framework.ui.skynet.widgets.XWidget widget) {
            if (radioButtons.getSelectedNames().size() > 0) if (!options.getLinkName().equals(
                  radioButtons.getSelectedNames().iterator().next())) options.setLinkName(LinkName.valueOf(radioButtons.getSelectedNames().iterator().next()));
         };
      });

      options.addSkyWalkerOptionsChangeListener(new ISkyWalkerOptionsChangeListener() {
         public void modified(ModType... modTypes) {
            handleOptionModified(modTypes);
         }
      });

      // Set UI to defaults
      handleOptionModified(ModType.FilterEnabled);
      handleOptionModified(ModType.Level);
      handleOptionModified(ModType.Layout);
      handleOptionModified(ModType.Link_Name);
      item.setControl(comp);
   }

   public AbstractLayoutAlgorithm getSelected() {
      return (AbstractLayoutAlgorithm) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement();
   }

   public void handleOptionModified(ModType... modTypes) {
      List<ModType> modList = Arrays.asList(modTypes);
      if (modList.contains(ModType.Level)) {
         if (levelSpinner != null) {
            if (levelSpinner.getSelection() != options.getLevels()) levelSpinner.setSelection(options.getLevels());
         }
      }
      if (modList.contains(ModType.Link_Name)) {
         if (radioButtons != null) {
            if (options.getLinkName().equals(radioButtons.getSelectedNames().iterator().next())) radioButtons.setSelected(options.getLinkName().name());
         }
      }
      if (modList.contains(ModType.Layout)) {
         if (treeViewer != null) {
            if (treeViewer.getSelection() != options.getLayout()) treeViewer.setSelection(new StructuredSelection(
                  new Object[] {options.getLayout()}));
         }
      }
      if (modList.contains(ModType.FilterEnabled)) {
         if (levelSpinner != null) levelSpinner.setEnabled(options.isFilterEnabled());
         if (treeViewer != null) treeViewer.getTree().setEnabled(options.isFilterEnabled());
      }
   }

}
