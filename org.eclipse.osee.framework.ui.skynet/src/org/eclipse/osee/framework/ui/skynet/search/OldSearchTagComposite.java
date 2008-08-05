/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.search;

import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.TagSearch;
import org.eclipse.osee.framework.skynet.core.tagging.Tagger;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterModel;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterModelList;
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
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;

public class OldSearchTagComposite extends Composite implements Listener {

   private static final String PRIOR_SEARCH_TEXT = "searchText";
   private static final String PRIOR_CASE_SENSITIVE = "caseSensitive";
   private static final String PRIOR_PARTIAL_MATCH = "partialMatch";
   private static final String PRIOR_SEARCH_TYPE = "searchType";
   private Text txtSearch;
   private Group grpSearchType;
   private Button btnSearch;
   private Button chkCaseSensitive;
   private Button chkPartialSearch;
   private Button radIndexSearch;
   private Button radNameSearch;
   private Button historicalSearch;
   private boolean initialCaseSensitive;
   private boolean initialPartialSearch;
   private String initialSearchText;
   private String initialSearchType;

   public OldSearchTagComposite(Composite parent, int style) {
      super(parent, style);
      parent.setLayout(new GridLayout());
      parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
   }

   public void restoreWidget(IMemento memento) {
      if (memento != null) {
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

   public void saveWidget(IMemento memento) {
      if (memento != null) {
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
      }
   }

   public void createControl(Composite parent) {
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

   public void handleEvent(Event event) {
      updateWidgetEnablements();
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
   }

   public void setHelpContexts() {
      SkynetGuiPlugin.getInstance().setHelp(btnSearch, "quick_search_text");
      SkynetGuiPlugin.getInstance().setHelp(grpSearchType, "quick_search_text");
      SkynetGuiPlugin.getInstance().setHelp(txtSearch, "quick_search_text");

      SkynetGuiPlugin.getInstance().setHelp(radIndexSearch, "quick_search_index_radio");
      SkynetGuiPlugin.getInstance().setHelp(radNameSearch, "quick_search_name_radio");
   }

   private void performSearch() {
      NewSearchUI.activateSearchResultView();
      FilterModelList filterList = new FilterModelList();
      filterList.setAllSelected(true);

      AbstractArtifactSearchQuery searchQuery;
      if (historicalSearch.getSelection()) {
         searchQuery =
               new HistoricalArtifactSearchQuery(txtSearch.getText().replace('*', '%'),
                     BranchPersistenceManager.getDefaultBranch());
      } else if (radNameSearch.getSelection()) {
         searchQuery =
               new ArtifactNameSearchQuery(txtSearch.getText().replace('*', '%'),
                     BranchPersistenceManager.getDefaultBranch());
      } else if (radIndexSearch.getSelection()) {
         for (String tag : Tagger.tokenizeAndSplit(txtSearch.getText())) {
            ISearchPrimitive primitive =
                  new TagSearch(tag, chkCaseSensitive.getSelection(), chkPartialSearch.getSelection());

            filterList.addFilter(new FilterModel(primitive, "", "", ""), false);
         }
         searchQuery = new FilterArtifactSearchQuery(filterList, BranchPersistenceManager.getDefaultBranch());
      } else {
         throw new IllegalStateException("unexpected search type radio button state");
      }

      NewSearchUI.runQueryInBackground(searchQuery);
   }
}
