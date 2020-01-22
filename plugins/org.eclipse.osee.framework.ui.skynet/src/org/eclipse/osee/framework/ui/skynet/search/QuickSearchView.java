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

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.SearchOptions;
import org.eclipse.osee.framework.skynet.core.artifact.search.SearchRequest;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.OseeStatusContributionItemFactory;
import org.eclipse.osee.framework.ui.skynet.access.AccessControlService;
import org.eclipse.osee.framework.ui.skynet.panels.SearchComposite;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterModel;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterModelList;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericViewPart;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidget;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

/**
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class QuickSearchView extends GenericViewPart {
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.QuickSearchView";

   private static final String ENTRY_SEPARATOR = "##";
   private static final String LAST_QUERY_KEY_ID = "lastQuery";
   private static final String LAST_BRANCH_UUID = "lastBranchUuid";
   private static final String QUERY_HISTORY_KEY_ID = "queryHistory";

   private Label branchLabel;
   private XBranchSelectWidget branchSelect;
   private SearchComposite attrSearchComposite;
   private SearchComposite idSearchComposite;
   private QuickSearchOptionComposite optionsComposite;
   private IMemento memento;
   private Button includeDeleted;

   private final AttributeSearchListener attrSearchListener = new AttributeSearchListener();
   private final IdSearchListener idSearchListener = new IdSearchListener();

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);
      if (memento != null) {
         this.memento = memento;
      }
   }

   @Override
   public void saveState(IMemento memento) {
      if (DbConnectionExceptionComposite.dbConnectionIsOk() && memento != null) {
         if (Widgets.isAccessible(attrSearchComposite)) {
            memento.putString(LAST_QUERY_KEY_ID, attrSearchComposite.getQuery());
            BranchId branch = branchSelect.getData();
            if (branch != null) {
               memento.putString(LAST_BRANCH_UUID, branch.getIdString());
            }
            StringBuilder builder = new StringBuilder();
            String[] queries = attrSearchComposite.getQueryHistory();
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
         if (Widgets.isAccessible(optionsComposite)) {
            optionsComposite.saveState(memento);
         }
      }
   }

   private void loadState() {
      if (DbConnectionExceptionComposite.dbConnectionIsOk() && memento != null) {
         if (Widgets.isAccessible(attrSearchComposite)) {
            String lastQuery = memento.getString(LAST_QUERY_KEY_ID);
            List<String> queries = new ArrayList<>();
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
            attrSearchComposite.restoreWidgetValues(queries, lastQuery, null, null);
         }
         if (Widgets.isAccessible(optionsComposite)) {
            optionsComposite.loadState(memento);
         }
         if (branchSelect != null) {
            String uuid = memento.getString(LAST_BRANCH_UUID);
            if (Strings.isValid(uuid) && uuid.matches("\\d+")) {
               try {
                  branchSelect.setSelection(BranchManager.getBranchToken(Long.valueOf(uuid)));
               } catch (OseeCoreException ex) {
                  // do nothing
               }
            }
         }
      }
   }

   @Override
   public void createPartControl(Composite parent) {
      if (DbConnectionExceptionComposite.dbConnectionIsOk(parent)) {

         ScrolledComposite sc = new ScrolledComposite(parent, SWT.V_SCROLL);
         sc.setExpandHorizontal(true);
         sc.setExpandVertical(true);
         sc.setMinSize(300, 300);

         Composite group = new Composite(sc, SWT.NONE);
         sc.setContent(group);
         group.setLayout(new GridLayout());
         GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
         gridData.widthHint = 300;
         group.setLayoutData(gridData);

         branchSelect = new XBranchSelectWidget("");
         branchSelect.setDisplayLabel(false);
         branchSelect.createWidgets(group, 2);
         branchSelect.addListener(attrSearchListener);
         branchSelect.addListener(idSearchListener);
         // allow user to double click the branch text area to select the branch
         if (Widgets.isAccessible(branchSelect.getSelectComposite())) {
            if (Widgets.isAccessible(branchSelect.getSelectComposite().getBranchSelectText())) {
               branchSelect.getSelectComposite().getBranchSelectText().setDoubleClickEnabled(true);
            }
         }
         OseeStatusContributionItemFactory.addTo(this, true);

         Composite panel = new Composite(group, SWT.NONE);
         GridLayout gL = new GridLayout();
         gL.marginHeight = 0;
         gL.marginWidth = 0;
         panel.setLayout(gL);
         panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

         if (AccessControlManager.isOseeAdmin()) {
            idSearchComposite = new SearchComposite(panel, SWT.NONE, "Search", "Search by ID:");
            idSearchComposite.addListener(idSearchListener);
         }

         Group attrSearchGroup = new Group(panel, SWT.NONE);
         attrSearchGroup.setLayout(new GridLayout());
         attrSearchGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
         attrSearchGroup.setText("Search by Attributes:");

         attrSearchComposite = new SearchComposite(attrSearchGroup, SWT.NONE, "Search", null);
         attrSearchComposite.addListener(attrSearchListener);

         optionsComposite = new QuickSearchOptionComposite(attrSearchGroup, SWT.NONE);
         optionsComposite.setLayout(ALayout.getZeroMarginLayout());
         optionsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
         attrSearchComposite.setOptionsComposite(optionsComposite);
         optionsComposite.setAttrSearchComposite(attrSearchComposite);

         if (!AccessControlManager.isOseeAdmin()) {
            idSearchComposite = new SearchComposite(panel, SWT.NONE, "Search", "Search by ID:");
            idSearchComposite.addListener(idSearchListener);
         }

         addContextMenu(idSearchComposite.getSearchArea());

         includeDeleted = new Button(group, SWT.CHECK);
         includeDeleted.setToolTipText("When selected, does not filter out deleted artifacts from search results.");
         includeDeleted.setText("Include Deleted");

         loadState();
         compositeEnablement(attrSearchComposite, false);
         attrSearchComposite.setHelpContext(OseeHelpContext.QUICK_SEARCH);

         branchLabel = new Label(group, SWT.NONE);
         branchLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
         branchLabel.setText("");

         createClearHistoryAction();

         setFocusWidget(attrSearchComposite);

         group.layout(true);
         sc.layout(true);
      }
   }

   private void createClearHistoryAction() {
      Action action = new Action("Clear Search History") {
         @Override
         public void run() {
            if (attrSearchComposite != null) {
               attrSearchComposite.clearHistory();
            }
         }
      };
      action.setToolTipText("Clears search history");
      action.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.REMOVE));
      getViewSite().getActionBars().getMenuManager().add(action);
   }

   private void compositeEnablement(SearchComposite composite, boolean enable) {
      if (Widgets.isAccessible(composite)) {
         for (Control cntrl : composite.getSearchChildren()) {
            cntrl.setEnabled(enable);
         }
      }
   }

   public void setBranch(BranchId branch) {
      if (branchSelect != null) {
         branchSelect.setSelection(branch);
         // branch has been selected; allow user to set up search string
         compositeEnablement(attrSearchComposite, true);
      }
   }

   private boolean isIncludeDeletedEnabled() {
      return includeDeleted.getSelection();
   }

   private class AttributeSearchListener implements Listener {

      @Override
      public void handleEvent(Event event) {
         if (Widgets.isAccessible(branchLabel) && branchSelect != null) {
            branchLabel.setText("");
            final BranchId branch = branchSelect.getData();
            if (branch == null) {
               branchLabel.setText("Error: Must Select a Branch");
            } else if (!AccessControlService.getAccessService().hasPermission(branch, PermissionEnum.READ)) {
               AWorkbench.popup(String.format("Access Denied for branch [%s]", branch));
            } else if (Widgets.isAccessible(attrSearchComposite) && attrSearchComposite.isExecuteSearchEvent(
               event) && Widgets.isAccessible(optionsComposite)) {

               // if just search artifact type without attribute value
               if (optionsComposite.getArtifactTypeFilter().length > 0 && optionsComposite.getAttributeTypeFilter().length == 0) {
                  NewSearchUI.activateSearchResultView();
                  FilterModelList filterModelList = new FilterModelList();

                  List<ArtifactTypeToken> artTypes = new LinkedList<>();
                  for (ArtifactTypeToken artType : optionsComposite.getArtifactTypeFilter()) {
                     artTypes.add(artType);
                  }
                  ISearchPrimitive primitive = new ArtifactTypeSearch(artTypes);
                  FilterModel model = new FilterModel(primitive,
                     String.format("All of Artifact Type [%s]", Collections.toString(";", artTypes)),
                     artTypes.toString(), "");

                  filterModelList.addFilter(model, true);
                  AbstractArtifactSearchQuery searchQuery = new FilterArtifactSearchQuery(filterModelList, branch);
                  NewSearchUI.runQueryInBackground(searchQuery);

               }
               // else search for attribute type values as well
               else {
                  DeletionFlag allowDeleted = isIncludeDeletedEnabled() ? INCLUDE_DELETED : EXCLUDE_DELETED;
                  NewSearchUI.activateSearchResultView();

                  ISearchQuery query;
                  SearchOptions options = new SearchOptions();
                  options.setDeletedIncluded(allowDeleted);
                  options.setAttributeTypeFilter(optionsComposite.getAttributeTypeFilter());
                  options.setArtifactTypeFilter(optionsComposite.getArtifactTypeFilter());
                  options.setCaseSensive(optionsComposite.isCaseSensitiveEnabled());
                  options.setMatchWordOrder(optionsComposite.isMatchWordOrderEnabled());
                  options.setExactMatch(optionsComposite.isExactMatchEnabled());

                  SearchRequest searchRequest = new SearchRequest(branch, attrSearchComposite.getQuery(), options);
                  query = new RemoteArtifactSearch(searchRequest);
                  NewSearchUI.runQueryInBackground(query);
               }

            } else {
               // branch has been selected; allow user to set up search string
               compositeEnablement(attrSearchComposite, true);
            }
         }
      }
   }

   private class IdSearchListener implements Listener {

      @Override
      public void handleEvent(Event event) {
         if (Widgets.isAccessible(branchLabel) && branchSelect != null) {
            branchLabel.setText("");
            final BranchId branch = branchSelect.getData();
            if (branch == null) {
               branchLabel.setText("Error: Must Select a Branch");
            } else if (!AccessControlService.getAccessService().hasPermission(branch, PermissionEnum.READ)) {
               // since AttributeSearchListener is called when Select Branch is actioned, only display if this is the guid search button
               if (event.widget instanceof Button && ((Button) event.widget).getText().equals("Search")) {
                  AWorkbench.popup(String.format("Access Denied for branch [%s]", branch));
               }
            } else if (Widgets.isAccessible(idSearchComposite) && idSearchComposite.isExecuteSearchEvent(event)) {
               String searchString = idSearchComposite.getQuery();
               List<String> invalids = new LinkedList<>();
               for (String id : Arrays.asList(searchString.split("[\\s,]+"))) {
                  if (!Strings.isValid(id) || !(GUID.isValid(id) || Strings.isNumeric(id))) {
                     invalids.add(id);
                  }
               }

               if (invalids.isEmpty()) {
                  NewSearchUI.activateSearchResultView();

                  ISearchQuery query = new IdArtifactSearch(searchString, branch, isIncludeDeletedEnabled());
                  NewSearchUI.runQueryInBackground(query);
               } else {
                  String message =
                     String.format("The following IDs are invalid: %s", Collections.toString(",", invalids));
                  MessageDialog.openError(Displays.getActiveShell(), "Invalid ID(s)", message);
               }

            } else {
               // branch has been selected; allow user to set up search string
               compositeEnablement(attrSearchComposite, true);
            }
         }
      }
   }

   /**
    * Since adding new menu replaces the default menu, we must re-create the default copy/paste options
    */
   private void addContextMenu(final Combo searchArea) {
      Menu menu = new Menu(searchArea);
      MenuItem item = new MenuItem(menu, SWT.PUSH);
      item.setText("Cut");
      item.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event event) {
            searchArea.cut();
         }
      });
      item = new MenuItem(menu, SWT.PUSH);
      item.setText("Copy");
      item.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event event) {
            searchArea.copy();
         }
      });
      item = new MenuItem(menu, SWT.PUSH);
      item.setText("Paste");
      item.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event event) {
            searchArea.paste();
         }
      });
      // Add Paste-and-Go menu option
      item = new MenuItem(menu, SWT.PUSH);
      item.setText("Paste-and-Go");
      item.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event event) {
            searchArea.setText("");
            searchArea.paste();
            idSearchComposite.getExecuteSearch().notifyListeners(SWT.Selection, null);
         }
      });

      searchArea.setMenu(menu);
   }

}
