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
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.skywalker.ISkyWalkerOptionsChangeListener.ModType;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabItem;

/**
 * @author Donald G. Dunne
 */
@SuppressWarnings("deprecation")
public class SkyWalkerShowAttributeTabItem {

   private CheckboxTreeViewer treeViewer;
   private final SkyWalkerOptions options;

   public SkyWalkerShowAttributeTabItem(org.eclipse.swt.widgets.TabFolder tabFolder, SkyWalkerOptions options) {

      this.options = options;
      TabItem item = new TabItem(tabFolder, SWT.NONE);
      item.setText("Show Attribute");

      Composite comp = new Composite(tabFolder, SWT.BORDER);
      comp.setLayout(ALayout.getZeroMarginLayout());
      comp.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));

      treeViewer = new CheckboxTreeViewer(comp, SWT.MULTI | SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
      treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      treeViewer.setContentProvider(new ArrayTreeContentProvider());
      treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            storeSelected();
         }
      });
      treeViewer.setLabelProvider(new LabelProvider() {

         @Override
         public Image getImage(Object obj) {
            return null;
         }

         @Override
         public String getText(Object obj) {
            return obj.toString();
         }
      });
      treeViewer.setSorter(new ViewerSorter());

      options.addSkyWalkerOptionsChangeListener(new ISkyWalkerOptionsChangeListener() {
         @Override
         public void modified(ModType... modTypes) {
            handleOptionModified(modTypes);
         }
      });
      // Set UI to defaults
      handleOptionModified(ModType.FilterEnabled);
      handleOptionModified(ModType.Show_Attribute);
      item.setControl(comp);
   }

   public void storeSelected() {
      options.setSelectedShowAttributes(treeViewer.getCheckedElements());
   }

   public void handleOptionModified(ModType... modTypes) {
      List<ModType> modList = Arrays.asList(modTypes);
      if (modList.contains(ModType.Show_Attribute)) {
         if (treeViewer != null) {
            treeViewer.setCheckedElements(options.getSelectedShowAttributeTypes().toArray());
         }
      }
      if (modList.contains(ModType.Artifact)) {
         if (treeViewer.getInput() == null && options.getAllShowAttributes() != null && options.getAllShowAttributes().size() > 0) {
            treeViewer.setInput(options.getAllShowAttributes());
         }
      }
   }
}
