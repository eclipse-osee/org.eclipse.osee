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

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.ToStringViewerSorter;
import org.eclipse.osee.framework.ui.skynet.access.AccessControlService;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiUtil;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterModel;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterModelList;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterTableViewer;
import org.eclipse.osee.framework.ui.skynet.search.page.AbstractArtifactSearchViewPage;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.util.NamedLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTree;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTree;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.HyperLinkLabel;
import org.eclipse.osee.framework.ui.swt.ToStringContainsPatternFilter;
import org.eclipse.search.ui.IReplacePage;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

/**
 * @author Michael S. Rodgers
 */
public class ArtifactSearchPage extends DialogPage implements ISearchPage, IReplacePage {
   private static final Pattern storageStringPattern = Pattern.compile("(.*?);(.*?);(.*?);(.*)");
   private static final Pattern notSearchPrimitivePattern = Pattern.compile("Not \\[(.*)\\]");
   private static final String FILTERS_STORAGE_KEY = ".filters";

   private static ISearchPageContainer aContainer;

   private Button addButton;
   private ComboViewer searchTypeList;

   private StackLayout selectionLayout;
   private static FilterTableViewer filterviewer;
   private Composite artifactTypeControls;
   private FilteredCheckboxTree artifactTypeList;

   private Composite attributeTypeControls;
   private FilteredCheckboxTree attributeTypeList;
   private FilteredTree attributeValueList;

   private XBranchSelectWidget branchSelect;

   private SearchFilter ATTRIBUTE_VALUE_FILTER;
   private static int lastSearchTypeListSelected = 2; // Attribute

   private final Matcher storageStringMatcher = storageStringPattern.matcher("");
   private final Matcher notSearchPrimitiveMatcher = notSearchPrimitivePattern.matcher("");
   private StyledText textDescription;

   @Override
   public void createControl(Composite parent) {
      initializeDialogUnits(parent);
      if (DbConnectionExceptionComposite.dbConnectionIsOk(parent)) {
         Composite mainComposite = new Composite(parent, SWT.NONE);
         mainComposite.setFont(parent.getFont());
         mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
         mainComposite.setLayout(new GridLayout());

         branchSelect = new XBranchSelectWidget("Branch To Search");
         branchSelect.setDisplayLabel(false);
         branchSelect.setSelection(BranchManager.getLastBranch());
         branchSelect.createWidgets(mainComposite, 2);
         branchSelect.addListener(new BranchSelectListener(branchSelect));

         addFilterControls(mainComposite);
         addTableControls(mainComposite);
         addFilterListeners();

         setControl(parent);
         aContainer.setPerformActionEnabled(false);

         HelpUtil.setHelp(mainComposite, OseeHelpContext.ARTIFACT_SEARCH);

         updateWidgets();

         loadState();
      } else {
         setControl(parent);
      }
   }

   private static class BranchSelectListener implements Listener {

      private final XBranchSelectWidget branchSelect;

      public BranchSelectListener(XBranchSelectWidget branchSelect) {
         this.branchSelect = branchSelect;
      }

      @Override
      public void handleEvent(Event event) {
         BranchId branch = branchSelect.getSelection();
         if (!isBranchReadable(branch)) {
            AWorkbench.popup(String.format("Read Access Denied for branch [%s]", branch));
         }
      }
   }

   private BranchId getSelectedBranch() {
      BranchId branch = branchSelect.getData();
      if (branch == null) {
         branch = BranchManager.getLastBranch();
      }
      try {
         if (branch == null) {
            branch = COMMON;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return branch;
   }

   @SuppressWarnings("deprecation")
   private void createArtifactTypeSearchControls(Composite optionsComposite) {
      artifactTypeControls = new Composite(optionsComposite, SWT.MULTI);
      artifactTypeControls.setLayout(new GridLayout(1, true));

      Label typeLabel = new Label(artifactTypeControls, SWT.HORIZONTAL);
      typeLabel.setText("Artifact Types:");
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.heightHint = 125;

      artifactTypeList = new FilteredCheckboxTree(artifactTypeControls,
         SWT.CHECK | SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
      artifactTypeList.getViewer().getTree().setLayoutData(gd);
      artifactTypeList.getViewer().setContentProvider(new ArrayTreeContentProvider());
      artifactTypeList.getViewer().setLabelProvider(new StringLabelProvider());
      artifactTypeList.getViewer().setSorter(new ToStringViewerSorter());
      artifactTypeList.getViewer().setInput(ArtifactTypeManager.getValidArtifactTypes(getSelectedBranch()));
      try {
         for (ArtifactTypeToken artType : ArtifactTypeManager.getValidArtifactTypes(getSelectedBranch())) {
            artifactTypeList.getViewer().add(artifactTypeControls, artType);
            artifactTypeList.getViewer().setData(artType.getName(), artType);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Error encountered while getting list of artifact types",
            ex);
      }
      addToSearchTypeList(new ArtifactTypeFilter(artifactTypeControls, artifactTypeList));
   }

   private void addToSearchTypeList(SearchFilter filter) {
      searchTypeList.add(filter.getFilterName());
      searchTypeList.setData(filter.getFilterName(), filter);
   }

   @SuppressWarnings("deprecation")
   private void createRelationSearchControls(Composite optionsComposite) {
      Composite relationControls = new Composite(optionsComposite, SWT.NONE);
      relationControls.setLayout(new GridLayout(2, true));

      final ComboViewer relationTypeList = new ComboViewer(relationControls, SWT.DROP_DOWN | SWT.READ_ONLY);
      relationTypeList.setContentProvider(new SearchContentProvider());
      relationTypeList.setLabelProvider(new NamedLabelProvider());
      relationTypeList.setSorter(new ToStringViewerSorter());
      final ComboViewer relationSideList = new ComboViewer(relationControls, SWT.DROP_DOWN | SWT.READ_ONLY);
      relationSideList.setContentProvider(new SearchContentProvider());
      relationSideList.setLabelProvider(new StringLabelProvider());

      try {
         for (RelationType linkDescriptor : RelationTypeManager.getValidTypes(getSelectedBranch())) {
            relationTypeList.add(linkDescriptor);
            relationTypeList.setData(linkDescriptor.getName(), linkDescriptor);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      relationTypeList.getCombo().addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            relationSideList.getCombo().removeAll();
            RelationType linkDescriptor =
               (RelationType) relationTypeList.getData(relationTypeList.getCombo().getText());
            relationSideList.add(linkDescriptor.getSideAName());
            relationSideList.add(linkDescriptor.getSideBName());
            relationSideList.add("-Either-");
            relationSideList.getCombo().select(0);
         }
      });
      relationTypeList.getCombo().setVisibleItemCount(Math.min(relationTypeList.getCombo().getItemCount(), 15));

      if (relationTypeList.getCombo().getItemCount() > 0) { // ensure we don't get a null pointer
         // exception when there are no relation types in the db
         relationTypeList.getCombo().select(0);
         RelationType linkDescriptor = (RelationType) relationTypeList.getData(relationTypeList.getCombo().getText());
         relationSideList.add(linkDescriptor.getSideAName());
         relationSideList.add(linkDescriptor.getSideBName());
         relationSideList.add("-Either-");
         relationSideList.getCombo().select(0);
      }

      addToSearchTypeList(new InRelationFilter(relationControls, relationTypeList, relationSideList));
      addToSearchTypeList(new NotInRelationFilter(relationControls, relationTypeList, relationSideList));
   }

   private void createOrphanSearchControls(Composite optionsComposite) {
      Composite comp = new Composite(optionsComposite, SWT.NONE);
      comp.setLayout(new GridLayout(2, true));

      addToSearchTypeList(new OrphanSearchFilter(comp));
   }

   @SuppressWarnings("deprecation")
   private void createAttributeSearchControls(Composite optionsComposite) {
      attributeTypeControls = new Composite(optionsComposite, SWT.MULTI);
      attributeTypeControls.setLayout(new GridLayout(1, true));

      Label typeLabel = new Label(attributeTypeControls, SWT.HORIZONTAL);
      typeLabel.setText("Attribute Types:");
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.heightHint = 125;

      attributeValueList = new FilteredTree(attributeTypeControls,
         SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, new ToStringContainsPatternFilter(), true);
      attributeValueList.getViewer().getTree().setLayoutData(gd);
      attributeValueList.getViewer().setContentProvider(new ArrayTreeContentProvider());
      attributeValueList.getViewer().setLabelProvider(new StringLabelProvider());
      attributeValueList.getViewer().setSorter(new ToStringViewerSorter());

      Label valueLabel = new Label(attributeTypeControls, SWT.HORIZONTAL);
      valueLabel.setText("Attribute Value:");
      Text attributeValue = new Text(attributeTypeControls, SWT.BORDER);
      attributeValue.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      Collection<AttributeTypeId> taggableTypes = AttributeTypeManager.getTaggableTypes();
      attributeValueList.getViewer().setInput(taggableTypes);
      attributeValue.addModifyListener(new ModifyListener() {

         @Override
         public void modifyText(ModifyEvent e) {
            addButton.setEnabled(ATTRIBUTE_VALUE_FILTER.isValid());
         }
      });

      attributeValueList.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {

         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            addButton.setEnabled(ATTRIBUTE_VALUE_FILTER.isValid());
         }
      });

      new Label(attributeTypeControls, SWT.NONE); // spacerLabelSoTheNextOneWillBeInColumnTwo
      ATTRIBUTE_VALUE_FILTER = new AttributeValueFilter(attributeTypeControls, attributeValueList, attributeValue);

      addToSearchTypeList(ATTRIBUTE_VALUE_FILTER);
   }

   @SuppressWarnings("deprecation")
   private void createAttributeExistsControls(Composite optionsComposite) {
      attributeTypeControls = new Composite(optionsComposite, SWT.MULTI);
      attributeTypeControls.setLayout(new GridLayout(1, true));

      Label typeLabel = new Label(attributeTypeControls, SWT.HORIZONTAL);
      typeLabel.setText("Attribute Type:");
      attributeTypeList = new FilteredCheckboxTree(attributeTypeControls,
         SWT.CHECK | SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.heightHint = 125;
      attributeTypeList.getViewer().getTree().setLayoutData(gd);
      attributeTypeList.getViewer().setContentProvider(new ArrayTreeContentProvider());
      attributeTypeList.getViewer().setLabelProvider(new StringLabelProvider());
      attributeTypeList.getViewer().setSorter(new ToStringViewerSorter());
      List<AttributeTypeToken> list = new ArrayList<>(AttributeTypeManager.getValidAttributeTypes(getSelectedBranch()));
      attributeTypeList.getViewer().setInput(list);
      try {
         for (AttributeTypeToken type : AttributeTypeManager.getValidAttributeTypes(getSelectedBranch())) {
            attributeTypeList.getViewer().add(attributeTypeControls, type);
            attributeTypeList.getViewer().setData(type.getName(), type);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Error encountered while getting list of attribute types",
            ex);
      }
      addToSearchTypeList(new AttributeExistsFilter(attributeTypeControls, attributeTypeList));
      addToSearchTypeList(new AttributeNotExistsFilter(attributeTypeControls, attributeTypeList));
   }

   @SuppressWarnings("deprecation")
   private void addFilterControls(Composite mainComposite) {
      Group filterGroup = new Group(mainComposite, SWT.NONE);
      filterGroup.setText("Create a Filter");
      filterGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      filterGroup.setLayout(new GridLayout());

      Composite composite = new Composite(filterGroup, SWT.BORDER);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      composite.setLayout(new GridLayout(2, false));

      Composite text = new Composite(filterGroup, SWT.NONE);
      text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      text.setLayout(new GridLayout());

      searchTypeList = new ComboViewer(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
      searchTypeList.setContentProvider(new SearchContentProvider());
      searchTypeList.setLabelProvider(new StringLabelProvider());
      searchTypeList.setSorter(new ToStringViewerSorter());

      selectionLayout = new StackLayout();

      Composite optionsComposite = new Composite(filterGroup, SWT.BORDER);
      optionsComposite.setLayout(new GridLayout());
      optionsComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
      optionsComposite.setLayout(selectionLayout);

      createAttributeSearchControls(optionsComposite);
      createAttributeExistsControls(optionsComposite);
      createArtifactTypeSearchControls(optionsComposite);
      createRelationSearchControls(optionsComposite);
      createOrphanSearchControls(optionsComposite);

      searchTypeList.getCombo().setVisibleItemCount(7);
      searchTypeList.getCombo().select(lastSearchTypeListSelected);
      searchTypeList.addSelectionChangedListener(new ISelectionChangedListener() {
         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            lastSearchTypeListSelected = searchTypeList.getCombo().getSelectionIndex();
         }
      });

      textDescription = new StyledText(text, SWT.NONE);
      textDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      textDescription.setEditable(true);
      SearchFilter searchFilter = (SearchFilter) searchTypeList.getData(searchTypeList.getCombo().getText());
      addTextDescription(searchFilter);

      addButton = new Button(filterGroup, SWT.PUSH);
      addButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false));
      addButton.setText("Add Filter");
   }

   private void addFilterListeners() {
      addButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            String text = searchTypeList.getCombo().getText();
            SearchFilter searchFilter = (SearchFilter) searchTypeList.getData(text);
            searchFilter.addFilterTo(filterviewer);
            attributeTypeList.clearChecked();
            artifactTypeList.clearChecked();
            updateOKStatus();
         }
      });

      searchTypeList.getCombo().addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            updateWidgets();
         }
      });

      artifactTypeList.getCheckboxTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {

         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            updateWidgets();
         }
      });
   }

   private void updateWidgets() {
      SearchFilter searchFilter = (SearchFilter) searchTypeList.getData(searchTypeList.getCombo().getText());
      addButton.setEnabled(searchFilter.isValid());
      selectionLayout.topControl = searchFilter.optionsControl;
      selectionLayout.topControl.getParent().layout();
      addTextDescription(searchFilter);
   }

   private void addTextDescription(SearchFilter searchFilter) {
      String searchDesc = searchFilter.getSearchDescription();
      if (searchDesc == null) {
         textDescription.setText(" ");
      } else {
         textDescription.setText(searchDesc);
      }
   }

   private void addTableControls(Composite composite) {
      Composite filterComposite = new Composite(composite, SWT.NONE);
      filterComposite.setFont(composite.getFont());
      filterComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      filterComposite.setLayout(new GridLayout(2, false));

      Label tableLabel = new Label(filterComposite, SWT.FILL);
      tableLabel.setText("Filters    ");

      HyperLinkLabel clearAllLabel = new HyperLinkLabel(filterComposite, SWT.NONE);
      clearAllLabel.setText("clear all");
      clearAllLabel.addListener(SWT.MouseUp, new Listener() {
         @Override
         public void handleEvent(Event event) {
            for (FilterModel filterModel : new CopyOnWriteArrayList<>(
               filterviewer.getFilterList().getFilters())) {
               filterviewer.removeFilter(filterModel);
            }
            filterviewer.refresh();
         }
      });

      Table table = new Table(composite, SWT.BORDER | SWT.V_SCROLL | SWT.HIDE_SELECTION);
      filterviewer = new FilterTableViewer(table, this);
      GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
      gridData.heightHint = 100;
      gridData.widthHint = 500;
      table.setLayoutData(gridData);
   }

   @Override
   public void setContainer(ISearchPageContainer container) {
      ArtifactSearchPage.aContainer = container;
   }

   /**
    * @return Returns the aContainer.
    */
   public static ISearchPageContainer getContainer() {
      return aContainer;
   }

   @Override
   public boolean performAction() {

      BranchId searchBranch = getSelectedBranch();
      if (ChangeUiUtil.permissionsDeniedWithDialog(searchBranch)) {
         return false;
      }

      NewSearchUI.activateSearchResultView();
      filterviewer.getFilterList().setAllSelected(true);
      AbstractArtifactSearchQuery searchQuery =
         new FilterArtifactSearchQuery(filterviewer.getFilterList(), getSelectedBranch());
      NewSearchUI.runQueryInBackground(searchQuery);
      saveState();
      return true;
   }

   @Override
   public boolean performReplace() {
      BranchId searchBranch = getSelectedBranch();
      if (ChangeUiUtil.permissionsDeniedWithDialog(searchBranch)) {
         return false;
      }

      filterviewer.getFilterList().setAllSelected(true);
      AbstractArtifactSearchQuery searchQuery =
         new FilterArtifactSearchQuery(filterviewer.getFilterList(), getSelectedBranch());

      IStatus status = NewSearchUI.runQueryInForeground(getContainer().getRunnableContext(), searchQuery);
      if (status.matches(IStatus.CANCEL)) {
         return false;
      }

      ISearchResultViewPart view = NewSearchUI.activateSearchResultView();
      if (view != null) {
         final ISearchResultPage page = view.getActivePage();
         if (page instanceof AbstractArtifactSearchViewPage) {
            runAttributeFindReplaceDialog(page);
         }
      }
      return true;
   }

   private void runAttributeFindReplaceDialog(final ISearchResultPage page) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (page instanceof AbstractArtifactSearchViewPage) {
               AbstractArtifactSearchViewPage artifactPage = (AbstractArtifactSearchViewPage) page;
               List<Artifact> artifacts = artifactPage.getInput().getArtifactResults();
               new AttributeFindReplaceDialog(page.getSite().getShell(), artifacts).open();
            }
         }
      });
   }

   /*
    * Implements method from IDialogPage
    */
   @Override
   public void setVisible(boolean visible) {
      updateOKStatus();
      super.setVisible(visible);
   }

   public void updateOKStatus() {
      if (isBranchReadable(
         getSelectedBranch()) && filterviewer == null || filterviewer.getFilterList().getFilters().isEmpty()) {
         getContainer().setPerformActionEnabled(false);
      } else {
         getContainer().setPerformActionEnabled(true);
      }
   }

   private static boolean isBranchReadable(BranchId branch) {
      boolean read = false;
      if (branch != null) {
         read = AccessControlService.getAccessService().hasPermission(branch, PermissionEnum.READ);
      }
      return read;
   }

   private String asString(FilterModel model) {
      StringBuilder builder = new StringBuilder();
      builder.append(model.getSearch());
      builder.append(";");
      builder.append(model.getType());
      builder.append(";");
      builder.append(model.getValue());
      builder.append(";");
      builder.append(model.getSearchPrimitive().getStorageString());
      return builder.toString();
   }

   private void processStoredFilter(String entry) {
      storageStringMatcher.reset(entry);
      if (storageStringMatcher.find()) {
         String searchPrimitive = storageStringMatcher.group(1);
         String type = storageStringMatcher.group(2);
         String value = storageStringMatcher.group(3);
         String storageString = storageStringMatcher.group(4);
         boolean isNotEnabled = false;
         notSearchPrimitiveMatcher.reset(storageString);
         if (notSearchPrimitiveMatcher.find()) {
            isNotEnabled = true;
            storageString = notSearchPrimitiveMatcher.group(1);
         }
         SearchFilter searchFilter = (SearchFilter) searchTypeList.getData(searchPrimitive);
         searchFilter.loadFromStorageString(filterviewer, type, value, storageString, isNotEnabled);
         searchFilter.getFilterName();
      }
   }

   protected void saveState() {
      IDialogSettings dialogSettings = Activator.getInstance().getDialogSettings();
      if (dialogSettings != null) {

         List<String> filterString = new ArrayList<>();
         FilterModelList filterList = filterviewer.getFilterList();
         for (FilterModel model : filterList.getFilters()) {
            filterString.add(asString(model));
         }
         dialogSettings.put(Activator.PLUGIN_ID + FILTERS_STORAGE_KEY,
            filterString.toArray(new String[filterString.size()]));
      }
   }

   protected void loadState() {
      IDialogSettings dialogSettings = Activator.getInstance().getDialogSettings();
      if (dialogSettings != null) {
         String[] filters = dialogSettings.getArray(Activator.PLUGIN_ID + FILTERS_STORAGE_KEY);
         if (filters != null) {
            for (String entry : filters) {
               processStoredFilter(entry);
            }
         }
      }
   }

}
