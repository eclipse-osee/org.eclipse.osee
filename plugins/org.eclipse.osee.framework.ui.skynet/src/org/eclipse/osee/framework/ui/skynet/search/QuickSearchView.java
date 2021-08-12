/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.search;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.util.ArtifactSearchOptions;
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlArtifactUtil;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.OseeStatusContributionItemFactory;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.panels.SearchComposite;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericViewPart;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.XResultDataDialog;
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
   private static final String LAST_APPLIC_UUID = "lastApplicUuid";
   private static final String LAST_APPLIC_TEXT = "lastApplicText";
   private static final String LAST_VIEW_UUID = "lastViewUuid";
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
   private QuickSearchViewApplicability view;
   private QuickSearchApplicabilityToken applicability;
   private ApplicabilityToken applicabilityId = ApplicabilityToken.SENTINEL;
   private ArtifactId viewId = ArtifactId.SENTINEL;
   private BranchId branch = BranchId.SENTINEL;

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
            branch = branchSelect.getData();
            if (branch != null) {
               memento.putString(LAST_BRANCH_UUID, branch.getIdString());
            }
            if (viewId.isValid()) {
               memento.putString(LAST_VIEW_UUID, viewId.getIdString());
            }
            if (applicabilityId.isValid()) {
               memento.putString(LAST_APPLIC_UUID, applicabilityId.getIdString());
               memento.putString(LAST_APPLIC_TEXT, applicabilityId.getName());
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

         if (branchSelect != null) {
            String uuid = memento.getString(LAST_BRANCH_UUID);
            String viewUuid = memento.getString(LAST_VIEW_UUID);
            String applicUuid = memento.getString(LAST_APPLIC_UUID);
            String applicText = memento.getString(LAST_APPLIC_TEXT);

            if (Strings.isValid(uuid) && uuid.matches("\\d+")) {
               try {
                  branchSelect.setSelection(BranchManager.getBranchToken(Long.valueOf(uuid)));
                  setBranch(branchSelect.getData());
                  if (Strings.isValid(viewUuid) && viewUuid.matches("\\d+")) {
                     setViewId(ArtifactId.valueOf(viewUuid));
                  }
                  if (Strings.isValid(applicUuid) && applicUuid.matches("\\d+")) {
                     setApplicabilityId(ApplicabilityToken.valueOf(Long.valueOf(applicUuid), applicText));
                  }
                  refreshView();
                  refreshApplicability();

               } catch (OseeCoreException ex) {
                  // do nothing
               }
            }
            if (Widgets.isAccessible(optionsComposite)) {
               optionsComposite.loadState(memento);
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
         sc.setMinSize(500, 500);

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
         branchSelect.addListener(new Listener() {
            @Override
            public void handleEvent(Event event) {
               try {
                  BranchId selectedBranch = branchSelect.getData();
                  if (selectedBranch != null && !BranchId.SENTINEL.equals(selectedBranch)) {
                     branch = selectedBranch;
                     setViewId(ArtifactId.SENTINEL);
                     setApplicabilityId(ApplicabilityToken.SENTINEL);
                     refreshView();
                     refreshApplicability();
                  }
               } catch (Exception ex) {
                  OseeLog.log(getClass(), Level.SEVERE, ex);
               }
            }

         });
         OseeStatusContributionItemFactory.addTo(this, true);
         Composite panel = new Composite(group, SWT.NONE);
         GridLayout gL = new GridLayout();
         gL.marginHeight = 0;
         gL.marginWidth = 0;
         panel.setLayout(gL);
         panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

         if (ServiceUtil.accessControlService().isOseeAdmin()) {
            idSearchComposite = new SearchComposite(panel, SWT.NONE, "Search", "Search by ID:", this, false);
            idSearchComposite.addListener(idSearchListener);
         }

         Group attrSearchGroup = new Group(panel, SWT.NONE);
         attrSearchGroup.setLayout(new GridLayout());
         attrSearchGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
         attrSearchGroup.setToolTipText("Search by attributes, artifact types, or applicability parameters");
         attrSearchComposite = new SearchComposite(attrSearchGroup, SWT.NONE, "Search", null, this, true);
         attrSearchComposite.addListener(attrSearchListener);

         optionsComposite = new QuickSearchOptionComposite(attrSearchGroup, SWT.NONE);
         optionsComposite.setLayout(ALayout.getZeroMarginLayout());
         optionsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
         attrSearchComposite.setOptionsComposite(optionsComposite);
         optionsComposite.setAttrSearchComposite(attrSearchComposite);
         Group appSearchGroup = new Group(attrSearchGroup, SWT.NONE);
         appSearchGroup.setLayout(ALayout.getZeroMarginLayout());

         appSearchGroup.setLayout(new GridLayout());
         appSearchGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
         appSearchGroup.setText("Product Line Options:");
         view = new QuickSearchViewApplicability(appSearchGroup, this);
         view.create();

         applicability = new QuickSearchApplicabilityToken(appSearchGroup, this);
         applicability.create();
         if (!ServiceUtil.accessControlService().isOseeAdmin()) {
            idSearchComposite = new SearchComposite(panel, SWT.NONE, "Search", "Search by ID:", this, false);
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

   public void setBranch(BranchToken branch) {
      if (branchSelect != null) {
         branchSelect.setSelection(branch);
         this.branch = branchSelect.getData();
         // branch has been selected; allow user to set up search string
         compositeEnablement(attrSearchComposite, true);
         refreshView();
         refreshApplicability();
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
            final BranchToken branch = branchSelect.getData();
            if (branch == null) {
               branchLabel.setText("Error: Must Select a Branch");
            }

            XResultData rd = ServiceUtil.accessControlService().hasBranchPermission(branch,
               PermissionEnum.READ, AccessControlArtifactUtil.getXResultAccessHeader("Select Branch", branch));
            if (rd.isErrors()) {
               XResultDataDialog.open(rd, "Branch Select Failed", "Access Denied for branch [%s]", branch);
            } else if (Widgets.isAccessible(attrSearchComposite) && attrSearchComposite.isExecuteSearchEvent(
               event) && Widgets.isAccessible(optionsComposite)) {

               NewSearchUI.activateSearchResultView();

               ISearchQuery query;
               ArtifactSearchOptions options = new ArtifactSearchOptions();
               options.setIncludeDeleted(DeletionFlag.allowDeleted(includeDeleted.getSelection()));
               if (optionsComposite.isAttributeTypeFilterEnabled()) {
                  options.setAttrTypeIds(Arrays.asList(optionsComposite.getAttributeTypeFilter()));
               }
               if (optionsComposite.isArtifactTypeFilterEnabled()) {
                  options.setArtTypeIds(Arrays.asList(optionsComposite.getArtifactTypeFilter()));
               }
               options.setCaseSensitive(optionsComposite.isCaseSensitiveEnabled());
               options.setMatchWordOrder(optionsComposite.isMatchWordOrderEnabled());
               options.setExactMatch(optionsComposite.isExactMatchEnabled());
               if (Strings.isValid(attrSearchComposite.getQuery())) {
                  options.setSearchString(attrSearchComposite.getQuery());
               }
               if (applicability.getCheckBox()) {
                  options.setApplic(getApplicabilityId());
               }
               if (view.getCheckBox()) {
                  options.setView(getViewId());
               }
               query = new ArtifactSearch(branchSelect.getData(), options);
               NewSearchUI.runQueryInBackground(query);

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
            final BranchToken branch = branchSelect.getData();
            if (branch == null) {
               branchLabel.setText("Error: Must Select a Branch");
            } else if (!ServiceUtil.accessControlService().hasBranchPermission(branch, PermissionEnum.READ,
               null).isSuccess()) {
               // since AttributeSearchListener is called when Select Branch is selected, only display if this is the guid search button
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

   public ApplicabilityToken getApplicabilityId() {
      return applicabilityId;
   }

   public void setApplicabilityId(ApplicabilityToken applicabilityId) {
      if (applicabilityId.isValid()) {
         view.refresh();
      }
      this.applicabilityId = applicabilityId;
      updateWidgetEnablements();
   }

   public ArtifactId getViewId() {
      return viewId;
   }

   public void setViewId(ArtifactId viewId) {
      if (viewId.isValid()) {
         applicability.refresh();
      }
      this.viewId = viewId;
      updateWidgetEnablements();
   }

   public void setViewCheckBox(boolean selected) {
      view.setCheckBox(selected);
      updateWidgetEnablements();
   }

   public boolean getViewCheckBox() {
      return view.getCheckBox();

   }

   public void updateWidgetEnablements() {
      attrSearchComposite.updateWidgetEnablements();
   }

   public void setApplicabilityCheckBox(boolean selected) {
      applicability.setCheckBox(selected);
      updateWidgetEnablements();
   }

   public boolean getApplicabilityCheckBox() {
      return applicability.getCheckBox();

   }

   private void refreshView() {
      if (view != null) {
         view.refresh();
      }
   }

   private void refreshApplicability() {
      if (applicability != null) {
         applicability.refresh();
      }
   }

   public BranchId getBranch() {
      return branch;
   }

}
