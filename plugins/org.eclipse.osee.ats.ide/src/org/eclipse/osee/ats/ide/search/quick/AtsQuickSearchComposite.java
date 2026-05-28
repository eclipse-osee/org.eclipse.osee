/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.search.quick;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;

/**
 * @author Donald G. Dunne
 */
public class AtsQuickSearchComposite extends Composite {

   private static final int MAX_HISTORY = 10;
   private static final String MEMENTO_KEY_SEARCH_HISTORY = "atsQuickSearchHistory";
   private static final String MEMENTO_KEY_SEARCH_ITEM = "searchItem";
   private static final String MEMENTO_KEY_INCLUDE_COMPLETED = "includeCompleted";
   private static final String MEMENTO_KEY_USE_SEARCH_VIEW = "useSearchView";

   private Combo searchCombo;
   private XCheckBox completeCancelledCheck;
   private XCheckBox useSearchViewCheck;
   private final List<String> searchHistory = new LinkedList<>();

   public AtsQuickSearchComposite(Composite parent, int style) {
      super(parent, style);

      GridLayout layout = new GridLayout(4, false);
      layout.marginHeight = 2;
      layout.marginWidth = 2;
      layout.horizontalSpacing = 4;
      setLayout(layout);
      setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

      // Search button
      Button searchButton = new Button(this, SWT.PUSH);
      searchButton.setText("Search");
      searchButton.setToolTipText("Execute ATS Quick Search");
      searchButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleSearch();
         }
      });

      // Search combo with history
      searchCombo = new Combo(this, SWT.DROP_DOWN);
      searchCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      searchCombo.setToolTipText("Quick Search all ATS fields");
      searchCombo.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent event) {
            if (event.character == '\r') {
               handleSearch();
            }
         }
      });

      // Checkboxes composite
      Composite checkComp = new Composite(this, SWT.NONE);
      checkComp.setLayout(ALayout.getZeroMarginLayout(2, false));

      completeCancelledCheck = new XCheckBox("IC");
      completeCancelledCheck.createWidgets(checkComp, 2);
      completeCancelledCheck.setToolTip("Include completed/cancelled ATS Artifacts");

      Composite checkComp2 = new Composite(this, SWT.NONE);
      checkComp2.setLayout(ALayout.getZeroMarginLayout(2, false));

      useSearchViewCheck = new XCheckBox("SV");
      useSearchViewCheck.createWidgets(checkComp2, 2);
      useSearchViewCheck.setToolTip("Use Eclipse Search View (tree mode shows attribute match locations); World View otherwise");
   }

   private void handleSearch() {
      String searchText = searchCombo.getText().trim();
      if (!Strings.isValid(searchText)) {
         AWorkbench.popup("Please enter search string");
         return;
      }

      addToHistory(searchText);

      AtsQuickSearchData data =
         new AtsQuickSearchData("ATS Quick Search", searchText, completeCancelledCheck.isChecked());

      // Check if user wants to use Eclipse Search View
      if (useSearchViewCheck.isChecked()) {
         data.setUseEclipseSearchView(true);
         AtsQuickSearchOperation.runInEclipseSearchView(data);
         return;
      }

      Job srchJob = new Job("ATS - Quick Search") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            AtsQuickSearchOperation operation = new AtsQuickSearchOperation(data);
            Collection<Artifact> artifacts = operation.performSearch();
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  if (artifacts.isEmpty()) {
                     AWorkbench.popup(data.toString(), data.toString() + "\n\nNo Results Found");
                  } else {
                     if (artifacts.size() == 1 && artifacts.iterator().next().isOfType(
                        AtsArtifactTypes.AbstractWorkflowArtifact)) {
                        WorkflowEditor.editArtifact(artifacts.iterator().next());
                     } else {
                        WorldEditor.open(new WorldEditorSimpleProvider(data.toString(), artifacts));
                     }
                  }
               }
            });
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(srchJob);
   }

   private void addToHistory(String searchText) {
      searchHistory.remove(searchText);
      searchHistory.add(0, searchText);
      while (searchHistory.size() > MAX_HISTORY) {
         searchHistory.remove(searchHistory.size() - 1);
      }
      updateComboItems();
   }

   private void updateComboItems() {
      String current = searchCombo.getText();
      searchCombo.setItems(searchHistory.toArray(new String[0]));
      searchCombo.setText(current);
   }

   public void saveState(IMemento memento) {
      if (memento == null) {
         return;
      }
      IMemento child = memento.createChild(MEMENTO_KEY_SEARCH_HISTORY);
      for (String item : searchHistory) {
         child.createChild(MEMENTO_KEY_SEARCH_ITEM).putTextData(item);
      }
      child.putBoolean(MEMENTO_KEY_INCLUDE_COMPLETED, completeCancelledCheck.isChecked());
      child.putBoolean(MEMENTO_KEY_USE_SEARCH_VIEW, useSearchViewCheck.isChecked());
   }

   public void restoreState(IMemento memento) {
      if (memento == null) {
         return;
      }
      IMemento child = memento.getChild(MEMENTO_KEY_SEARCH_HISTORY);
      if (child != null) {
         searchHistory.clear();
         for (IMemento item : child.getChildren(MEMENTO_KEY_SEARCH_ITEM)) {
            String text = item.getTextData();
            if (Strings.isValid(text)) {
               searchHistory.add(text);
            }
         }
         updateComboItems();

         // Populate text with last search
         if (!searchHistory.isEmpty()) {
            searchCombo.setText(searchHistory.get(0));
         }

         Boolean includeCompleted = child.getBoolean(MEMENTO_KEY_INCLUDE_COMPLETED);
         if (includeCompleted != null) {
            completeCancelledCheck.set(includeCompleted);
         }
         Boolean useSearchView = child.getBoolean(MEMENTO_KEY_USE_SEARCH_VIEW);
         if (useSearchView != null) {
            useSearchViewCheck.set(useSearchView);
         }
      }
   }

   public List<String> getSearchHistory() {
      return new ArrayList<>(searchHistory);
   }
}
