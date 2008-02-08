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
package org.eclipse.osee.framework.ui.skynet.search;

import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.DefaultBranchChangedEvent;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeValueSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.skynet.core.artifact.search.TagSearch;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.tagging.Tagger;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.skynet.SkynetContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterModel;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterModelList;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class QuickSearchView extends ViewPart implements IActionable, Listener, IEventReceiver {
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.QuickSearchView";
   private static final String PRIOR_SEARCH_TEXT = "searchText";
   private static final String PRIOR_CASE_SENSITIVE = "caseSensitive";
   private static final String PRIOR_PARTIAL_MATCH = "partialMatch";
   private static final String PRIOR_SEARCH_TYPE = "searchType";

   private Button btnSearch;
   private Button chkCaseSensitive;
   private Button chkPartialSearch;
   private Button radIndexSearch;
   private Button radNameSearch;
   private Button historicalSearch;

   private Group grpSearchType;
   private Label branchLabel;
   private Text txtSearch;

   private boolean initialCaseSensitive;
   private boolean initialPartialSearch;
   private String initialSearchText;
   private String initialSearchType;

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);
      if (memento != null) {
         initialSearchText = memento.getString(PRIOR_SEARCH_TEXT);

         initialSearchType = memento.getString(PRIOR_SEARCH_TYPE);

         String boolStr;
         boolStr = memento.getString(PRIOR_CASE_SENSITIVE);
         if (boolStr == null) {
            initialCaseSensitive = false;
         } else {
            initialCaseSensitive = Boolean.parseBoolean(boolStr);
         }
         boolStr = memento.getString(PRIOR_PARTIAL_MATCH);
         if (boolStr == null) {
            initialPartialSearch = false;
         } else {
            initialPartialSearch = Boolean.parseBoolean(boolStr);
         }
      }
      if (initialSearchText == null) {
         initialSearchText = "";
      }

   }

   @Override
   public void saveState(IMemento memento) {
      memento.putString(PRIOR_SEARCH_TEXT, txtSearch.getText());
      memento.putString(PRIOR_CASE_SENSITIVE, Boolean.toString(chkCaseSensitive.getSelection()));
      memento.putString(PRIOR_PARTIAL_MATCH, Boolean.toString(chkPartialSearch.getSelection()));

      for (Control control : grpSearchType.getChildren()) {
         if (control instanceof Button) {
            Button button = (Button) control;
            if (button.getSelection()) {
               memento.putString(PRIOR_SEARCH_TYPE, button.getText());
               break;
            }
         }
      }
   }

   @Override
   public void createPartControl(Composite parent) {
      if (!DbConnectionExceptionComposite.dbConnectionIsOk(parent)) return;

      parent.setLayout(new GridLayout());
      parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      branchLabel = new Label(parent, SWT.NONE);
      branchLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      createOptionsArea(parent);
      createSearchTextArea(parent);

      btnSearch = new Button(parent, SWT.PUSH);
      btnSearch.setText("Search");
      btnSearch.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true, false));
      btnSearch.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
            performSearch();
         }
      });

      txtSearch.setText(initialSearchText);
      chkCaseSensitive.setSelection(initialCaseSensitive);
      chkPartialSearch.setSelection(initialPartialSearch);

      initSearchTypeRadioButton();

      SkynetContributionItem.addTo(this, true);

      createActions();
      setHelpContexts();

      SkynetEventManager.getInstance().register(DefaultBranchChangedEvent.class, this);

      updateWidgetEnablements();
   }

   private void initSearchTypeRadioButton() {
      for (Control control : grpSearchType.getChildren()) {
         if (control instanceof Button) {
            Button button = (Button) control;
            if (button.getText().equals(initialSearchType)) {
               button.setSelection(true);
               return;
            }
         }
      }

      // If a prior didn't pan out, then default to index based
      radIndexSearch.setSelection(true);
   }

   private void createSearchTextArea(Composite parent) {
      Group group = new Group(parent, SWT.NONE);
      group.setText("Enter Search String");
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      group.setLayout(new GridLayout());

      txtSearch = new Text(group, SWT.BORDER);
      txtSearch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      txtSearch.addListener(SWT.Modify, this);
      txtSearch.addKeyListener(new KeyAdapter() {

         public void keyPressed(KeyEvent e) {
            if (e.character == '\r') {
               if (btnSearch.getEnabled()) {
                  performSearch();
               }
            }
         }
      });

   }

   private void createOptionsArea(Composite parent) {
      grpSearchType = new Group(parent, SWT.NONE);
      grpSearchType.setText("Search Type");
      grpSearchType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      grpSearchType.setLayout(new GridLayout(3, false));

      radNameSearch = new Button(grpSearchType, SWT.RADIO);
      radNameSearch.setText("Name");
      radNameSearch.addListener(SWT.Selection, this);

      radIndexSearch = new Button(grpSearchType, SWT.RADIO);
      radIndexSearch.setText("Index");
      radIndexSearch.addListener(SWT.Selection, this);

      historicalSearch = new Button(grpSearchType, SWT.RADIO);
      historicalSearch.setText("Multi-branch Historical");
      historicalSearch.addListener(SWT.Selection, this);

      chkCaseSensitive = new Button(grpSearchType, SWT.CHECK);
      chkCaseSensitive.setText("Case-sensitive");

      chkPartialSearch = new Button(grpSearchType, SWT.CHECK);
      chkPartialSearch.setText("Partial Match");
   }

   private void createActions() {
      OseeAts.addBugToViewToolbar(this, this, SkynetGuiPlugin.getInstance(), VIEW_ID, "Quick Search");
   }

   private void setHelpContexts() {
      SkynetGuiPlugin.getInstance().setHelp(btnSearch, "quick_search_text");
      SkynetGuiPlugin.getInstance().setHelp(grpSearchType, "quick_search_text");
      SkynetGuiPlugin.getInstance().setHelp(txtSearch, "quick_search_text");

      SkynetGuiPlugin.getInstance().setHelp(radIndexSearch, "quick_search_index_radio");
      SkynetGuiPlugin.getInstance().setHelp(radNameSearch, "quick_search_name_radio");
   }

   @Override
   public void setFocus() {
      if (txtSearch != null) txtSearch.setFocus();
   }

   public String getActionDescription() {
      return "";
   }

   private void performSearch() {
      NewSearchUI.activateSearchResultView();
      FilterModelList filterList = new FilterModelList();
      filterList.setAllSelected(true);

      AbstractArtifactSearchQuery searchQuery;
      if (historicalSearch.getSelection()) {
         searchQuery = new HistoricalArtifactSearchQuery(txtSearch.getText().replace('*', '%'));
      } else if (radNameSearch.getSelection()) {
         filterList.addFilter(new FilterModel(new AttributeValueSearch("Name", txtSearch.getText().replace('*', '%'),
               Operator.LIKE), "", "", ""), false);
         searchQuery =
               new FilterArtifactSearchQuery(filterList, BranchPersistenceManager.getInstance().getDefaultBranch());
      } else if (radIndexSearch.getSelection()) {
         for (String tag : Tagger.tokenizeAndSplit(txtSearch.getText())) {
            ISearchPrimitive primitive =
                  new TagSearch(tag, chkCaseSensitive.getSelection(), chkPartialSearch.getSelection());

            filterList.addFilter(new FilterModel(primitive, "", "", ""), false);
         }
         searchQuery =
               new FilterArtifactSearchQuery(filterList, BranchPersistenceManager.getInstance().getDefaultBranch());
      } else {
         throw new IllegalStateException("unexpected search type radio button state");
      }

      NewSearchUI.runQueryInBackground(searchQuery);
   }

   private void updateWidgetEnablements() {
      chkCaseSensitive.setEnabled(radIndexSearch.getSelection());
      chkPartialSearch.setEnabled(radIndexSearch.getSelection());
      btnSearch.setEnabled(!txtSearch.getText().trim().equals(""));

      if (historicalSearch.getSelection()) {
         branchLabel.setText("Searching on all branches");
      } else {
         branchLabel.setText("Searching on current default branch \"" + BranchPersistenceManager.getInstance().getDefaultBranch() + "\"");
      }
   }

   public void handleEvent(Event event) {
      updateWidgetEnablements();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.event.IEventReceiver#onEvent(org.eclipse.osee.framework.ui.plugin.event.Event)
    */
   public void onEvent(org.eclipse.osee.framework.ui.plugin.event.Event event) {
      updateWidgetEnablements();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.event.IEventReceiver#runOnEventInDisplayThread()
    */
   public boolean runOnEventInDisplayThread() {
      return true;
   }
}
