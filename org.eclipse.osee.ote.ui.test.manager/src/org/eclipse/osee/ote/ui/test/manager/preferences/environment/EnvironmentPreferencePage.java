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
package org.eclipse.osee.ote.ui.test.manager.preferences.environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.jdk.core.type.TreeObject;
import org.eclipse.osee.framework.jdk.core.type.TreeParent;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.ote.ui.test.manager.OteTestManagerImage;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

/**
 * @author Roberto E. Escobar
 */
public class EnvironmentPreferencePage {
   public static final String CHECKED = "selected";
   public static final String NAME = "name";
   public static final String NUMBER_OF_VALUES = "numberOfValues";
   public static final String PAGE_KEY = "org.eclipse.osee.ote.ui.test.manager.EnvironmentPreferencePage";
   public static final String VALUE = "value";

   public static Map<String, String> getSelectedItems() {
      Map<String, String> environmentVariables = new HashMap<String, String>();

      ArrayList<EnvironmentPreferenceNode> envList = loadVariables();

      for (EnvironmentPreferenceNode node : envList) {
         if (node.isChecked()) {
            String name = node.getEnvName();
            String value = node.getValue();
            if (name != null && !name.equals("")) {
               environmentVariables.put(name, value != null ? value : "");
            }
         }
      }
      return environmentVariables;
   }

   private static ArrayList<EnvironmentPreferenceNode> loadVariables() {
      ArrayList<EnvironmentPreferenceNode> list = new ArrayList<EnvironmentPreferenceNode>();
      IPreferenceStore prefStore = TestManagerPlugin.getInstance().getPreferenceStore();

      int numberOfValues = prefStore.getInt(PAGE_KEY + "." + NUMBER_OF_VALUES);
      for (int index = 0; index < numberOfValues; index++) {
         String name = prefStore.getString(PAGE_KEY + "." + NAME + "_" + index);
         String value = prefStore.getString(PAGE_KEY + "." + VALUE + "_" + index);
         String selectedString = prefStore.getString(PAGE_KEY + "." + CHECKED + "_" + index);
         boolean selected = Boolean.parseBoolean(selectedString);

         if (name != null && name != "") {
            EnvironmentPreferenceNode node = new EnvironmentPreferenceNode(name);
            node.setValue((value != null ? value : ""));
            node.setChecked(selected);
            list.add(node);
         }
      }
      return list;
   }

   private Button addButton;

   private Composite buttonComposite;
   private EnvironmentPageEventHandler environmentPageEventHandler;
   private Button removeButton;

   private final ArrayList<EnvironmentPreferenceNode> treeInputList;

   private CheckboxTreeViewer treeViewer;

   public EnvironmentPreferencePage(Composite parent) {
      this.treeInputList = loadVariables();
      this.createTreeArea(parent);
   }

   public ISelection getSelection() {
      return treeViewer.getSelection();
   }

   public void refresh() {
      treeViewer.refresh();
   }

   public void storeVariables() {
      IPreferenceStore prefStore = TestManagerPlugin.getInstance().getPreferenceStore();
      prefStore.setValue(PAGE_KEY + "." + NUMBER_OF_VALUES, treeInputList.size());
      int index = 0;
      for (EnvironmentPreferenceNode node : treeInputList) {
         index = treeInputList.indexOf(node);
         String name = node.getEnvName();
         if (name != null && name != "") {
            prefStore.putValue(PAGE_KEY + "." + NAME + "_" + index, name);
            String value = node.getValue();
            prefStore.putValue(PAGE_KEY + "." + VALUE + "_" + index, (value != null ? value : ""));
            prefStore.putValue(PAGE_KEY + "." + CHECKED + "_" + index, Boolean.toString(node.isChecked()));
         }
      }
   }

   private void attachListeners() {
      treeViewer.getTree().addKeyListener(new KeyListener() {
         public void keyPressed(KeyEvent e) {
         }

         public void keyReleased(KeyEvent e) {
            if (e.character == SWT.DEL) {
               environmentPageEventHandler.handleRemoveSelectedViewEvent();
            }
         }
      });

      treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
         public void selectionChanged(SelectionChangedEvent event) {
            environmentPageEventHandler.handleTreeSelectionEvent(event);
         }
      });

      treeViewer.getTree().addMouseListener(new MouseListener() {
         public void mouseDoubleClick(MouseEvent e) {
            environmentPageEventHandler.handleEditVariableEvent();
         }

         public void mouseDown(MouseEvent e) {
         }

         public void mouseUp(MouseEvent e) {
         }
      });

      treeViewer.addCheckStateListener(new ICheckStateListener() {

         public void checkStateChanged(CheckStateChangedEvent event) {
            environmentPageEventHandler.handleCheckStateChangeEvent(event);
         }

      });
   }

   private Control createButtonArea(Composite parent) {

      buttonComposite = new Composite(parent, SWT.NONE);
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 1;
      GridData gd = new GridData(SWT.FILL);
      buttonComposite.setLayout(gridLayout);
      buttonComposite.setLayoutData(gd);

      addButton = new Button(buttonComposite, SWT.PUSH);
      addButton.setText("Add");
      addButton.addSelectionListener(new SelectionListener() {

         public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
         }

         public void widgetSelected(SelectionEvent e) {
            environmentPageEventHandler.handleAddEnvironmentVariableEvent();
         }
      });

      removeButton = new Button(buttonComposite, SWT.PUSH);
      removeButton.setText("Remove");
      removeButton.addSelectionListener(new SelectionListener() {

         public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
         }

         public void widgetSelected(SelectionEvent e) {
            environmentPageEventHandler.handleRemoveSelectedViewEvent();
         }

      });
      return parent;
   }

   private Control createTreeArea(Composite parent) {

      Group defaultEnvironmentVariablesGroup = new Group(parent, SWT.NONE);
      defaultEnvironmentVariablesGroup.setText("Select Default Environment Variables to Use");
      GridLayout gL = new GridLayout();
      gL.numColumns = 2;
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.grabExcessHorizontalSpace = true;
      gd.grabExcessVerticalSpace = true;
      defaultEnvironmentVariablesGroup.setLayout(gL);
      defaultEnvironmentVariablesGroup.setLayoutData(gd);

      Composite areaComposite = new Composite(defaultEnvironmentVariablesGroup, SWT.NONE);
      GridLayout treeLayout = new GridLayout();
      treeLayout.numColumns = 1;
      GridData gd1 = new GridData();
      gd1.horizontalAlignment = GridData.FILL;
      gd1.verticalAlignment = GridData.FILL;
      gd1.grabExcessHorizontalSpace = true;
      gd1.grabExcessVerticalSpace = true;
      areaComposite.setLayout(treeLayout);
      areaComposite.setLayoutData(gd1);

      GridData treeGridData = new GridData();
      treeGridData.grabExcessHorizontalSpace = true;
      treeGridData.grabExcessVerticalSpace = true;
      treeGridData.horizontalAlignment = GridData.FILL;
      treeGridData.verticalAlignment = GridData.FILL;

      treeViewer =
            new CheckboxTreeViewer(areaComposite, SWT.MULTI | SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
      treeViewer.getTree().setLayoutData(treeGridData);
      treeViewer.setContentProvider(new ITreeContentProvider() {

         public void dispose() {

         }

         public Object[] getChildren(Object parentElement) {
            if (parentElement != null && parentElement instanceof TreeParent) {
               TreeParent parent = (TreeParent) parentElement;
               if (parent.hasChildren()) {
                  return parent.getChildren();
               }
            }
            return new Object[0];
         }

         public Object[] getElements(Object inputElement) {
            if (inputElement != null && inputElement instanceof ArrayList<?>) {
               ArrayList<?> elementArray = (ArrayList<?>) inputElement;
               return elementArray.toArray();
            }
            return new Object[0];
         }

         public Object getParent(Object element) {
            if (element != null && element instanceof TreeObject) {
               TreeObject child = (TreeObject) element;
               return child.getParent();
            }
            return new Object();
         }

         public boolean hasChildren(Object element) {
            if (element instanceof TreeParent) {
               TreeParent parent = (TreeParent) element;
               return parent.hasChildren();
            }
            return false;
         }

         public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

         }
      });
      treeViewer.setLabelProvider(new LabelProvider() {

         @Override
         public Image getImage(Object obj) {
            return ImageManager.getImage(OteTestManagerImage.ENVIRONMENT);
         }

         @Override
         public String getText(Object obj) {
            return obj.toString();
         }
      });
      treeViewer.setInput(treeInputList);
      treeViewer.getTree().setToolTipText("Double click on an item to edit.\nClick once to preview content.");

      environmentPageEventHandler = new EnvironmentPageEventHandler(parent, treeViewer, treeInputList);

      createButtonArea(defaultEnvironmentVariablesGroup);

      attachListeners();

      for (EnvironmentPreferenceNode parentNode : treeInputList) {
         treeViewer.setChecked(parentNode, parentNode.isChecked());
      }

      return parent;
   }
}
