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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.DefaultBranchChangedEvent;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.TagSearch;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.tagging.Tagger;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.skynet.SkynetContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.panels.SearchComposite;
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

   private static final String ENTRY_SEPARATOR = "##";
   private static final String LAST_QUERY_KEY_ID = "lastQuery";
   private static final String QUERY_HISTORY_KEY_ID = "queryHistory";
   private static final String OPTIONS_KEY_ID = "searchOption";

   private static final String[] SEARCH_OPTIONS = new String[] {"Include Deleted"};

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

   private SearchComposite searchComposite;
   private IMemento memento;

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);
      if (memento != null) {
         this.memento = memento;
         initialSearchText = memento.getString(PRIOR_SEARCH_TEXT);

         initialSearchType = memento.getString(PRIOR_SEARCH_TYPE);

         String boolStr = memento.getString(PRIOR_CASE_SENSITIVE);
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
      if (txtSearch != null && chkCaseSensitive != null && chkPartialSearch != null && grpSearchType != null) {
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
      if (OseeProperties.isDeveloper() && searchComposite != null && memento != null) {
         memento.putString(LAST_QUERY_KEY_ID, searchComposite.getQuery());
         Map<String, Boolean> options = searchComposite.getOptions();
         for (String option : options.keySet()) {
            memento.putString(OPTIONS_KEY_ID + option.replaceAll(" ", ""), options.get(option).toString());
         }
         StringBuilder builder = new StringBuilder();
         String[] queries = searchComposite.getQueryHistory();
         for (int index = 0; index < queries.length; index++) {
            try {
               builder.append(URLEncoder.encode(queries[index], "UTF-8"));
               if (index + 1 < queries.length) {
                  builder.append(ENTRY_SEPARATOR);
               }
            } catch (UnsupportedEncodingException ex) {
               // DO NOTHING
            }
         }
         memento.putString(QUERY_HISTORY_KEY_ID, builder.toString());
      }
   }

   private void loadState() {
      if (memento != null) {
         if (OseeProperties.isDeveloper() && searchComposite != null && memento != null) {
            String lastQuery = memento.getString(LAST_QUERY_KEY_ID);

            Map<String, Boolean> options = new HashMap<String, Boolean>();
            for (String option : SEARCH_OPTIONS) {
               options.put(option, new Boolean(memento.getString(OPTIONS_KEY_ID + option.replaceAll(" ", ""))));
            }

            List<String> queries = new ArrayList<String>();
            String rawHistory = memento.getString(QUERY_HISTORY_KEY_ID);
            if (rawHistory != null) {
               String[] values = rawHistory.split(ENTRY_SEPARATOR);
               for (String value : values) {
                  try {
                     queries.add(URLDecoder.decode(value, "UTF-8"));
                  } catch (UnsupportedEncodingException ex) {
                     // DO NOTHING
                  }
               }
            }
            searchComposite.restoreWidgetValues(queries, lastQuery, options);
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

      if (OseeProperties.isDeveloper()) {
         Group group = new Group(parent, SWT.NONE);
         group.setLayout(new GridLayout());
         group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
         group.setText("EXPERIMENTAL - Only Visible to Developers");

         searchComposite = new SearchComposite(group, SWT.NONE, SEARCH_OPTIONS);
         searchComposite.addListener(this);
         loadState();
      }
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

      // If a prior didn't pan out, then default to name based search
      radNameSearch.setSelection(true);
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
      historicalSearch.setText("Historical");
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
         searchQuery =
               new HistoricalArtifactSearchQuery(txtSearch.getText().replace('*', '%'),
                     BranchPersistenceManager.getInstance().getDefaultBranch());
      } else if (radNameSearch.getSelection()) {
         searchQuery =
               new ArtifactNameSearchQuery(txtSearch.getText().replace('*', '%'),
                     BranchPersistenceManager.getInstance().getDefaultBranch());
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
      if (radIndexSearch != null && radIndexSearch.isDisposed() != true) {
         boolean radIndexSearchValue = radIndexSearch.getSelection();

         if (chkCaseSensitive != null && chkCaseSensitive.isDisposed() != true) {
            chkCaseSensitive.setEnabled(radIndexSearchValue);
         }
         if (chkPartialSearch != null && chkPartialSearch.isDisposed() != true) {
            chkPartialSearch.setEnabled(radIndexSearchValue);
         }
      }
      if (txtSearch != null && txtSearch.isDisposed() != true && btnSearch != null && btnSearch.isDisposed() != true) {
         String value = txtSearch.getText();
         if (value != null) {
            value = value.trim();
         }
         btnSearch.setEnabled(Strings.isValid(value));
      }
      if (branchLabel != null && branchLabel.isDisposed() != true) {
         branchLabel.setText("Searching on current default branch \"" + BranchPersistenceManager.getInstance().getDefaultBranch() + "\"");
      }
   }

   public void handleEvent(Event event) {
      updateWidgetEnablements();
      if (searchComposite != null && searchComposite.isExecuteSearchEvent(event)) {
         NewSearchUI.activateSearchResultView();
         NewSearchUI.runQueryInBackground(new RemoteArtifactSearch(searchComposite.getQuery(),
               searchComposite.getOptions()));
      }
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
