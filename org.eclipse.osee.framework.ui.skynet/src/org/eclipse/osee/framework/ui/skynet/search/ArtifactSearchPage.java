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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;
import org.eclipse.osee.framework.skynet.core.relation.IRelationLinkDescriptor;
import org.eclipse.osee.framework.skynet.core.relation.RelationPersistenceManager;
import org.eclipse.osee.framework.ui.skynet.ArtifactSearchViewPage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterTableViewer;
import org.eclipse.search.ui.IReplacePage;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

/**
 * @author Michael S. Rodgers
 */
public class ArtifactSearchPage extends DialogPage implements ISearchPage, IReplacePage {
   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(ArtifactSearchPage.class);
   private static final ConfigurationPersistenceManager configurationPersistenceManager =
         ConfigurationPersistenceManager.getInstance();
   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();
   private static ISearchPageContainer aContainer;

   private Button addButton;
   private Button allButton;
   private Button atLeastOneButton;
   private ComboViewer searchTypeList;
   private Button notButton;

   private StackLayout selectionLayout;
   private static FilterTableViewer filterviewer;
   private Composite artifactTypeControls;
   private ListViewer artifactTypeList;
   private Text indexText;

   private SearchFilter HRID_VALUE_FILTER;
   private SearchFilter ATTRIBUTE_VALUE_FILTER;
   private SearchFilter INDEX_SEARCH_FILTER;

   public void createControl(Composite parent) {
      initializeDialogUnits(parent);

      Composite mainComposite = new Composite(parent, SWT.NONE);
      mainComposite.setFont(parent.getFont());
      mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      mainComposite.setLayout(new GridLayout());

      Label label = new Label(mainComposite, SWT.NONE);
      label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      label.setText("Searching on current default branch \"" + branchManager.getDefaultBranch() + "\"");

      addFilterControls(mainComposite);
      addTableControls(mainComposite);
      addSearchScope(mainComposite);
      addFilterListeners();

      setControl(parent);
      aContainer.setPerformActionEnabled(false);

      SkynetGuiPlugin.getInstance().setHelp(mainComposite, "artifact_search");

      updateWidgets();
   }

   /**
    * Controls to allow the user to select wether all the filters are combined using AND or OR
    */
   private void addSearchScope(Composite composite) {
      Group allSelectionGroup = new Group(composite, SWT.NONE);
      allSelectionGroup.setText("Artifacts that match");
      allSelectionGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      allSelectionGroup.setLayout(new GridLayout(2, false));

      allButton = new Button(allSelectionGroup, SWT.RADIO);
      allButton.setText("All filters (AND)");
      allButton.setSelection(true);

      atLeastOneButton = new Button(allSelectionGroup, SWT.RADIO);
      atLeastOneButton.setText("At least one filter (OR)");
   }

   private void createIndexSearchControls(Composite optionsComposite) {
      Composite composite = new Composite(optionsComposite, SWT.NONE);
      composite.setLayout(new GridLayout());

      indexText = new Text(composite, SWT.BORDER);
      indexText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

      Button caseSensitiveChk = new Button(composite, SWT.CHECK);
      caseSensitiveChk.setText("Case sensitive");

      Button partialMatchChk = new Button(composite, SWT.CHECK);
      partialMatchChk.setText("Partial Match");

      INDEX_SEARCH_FILTER = new IndexSearchFilter(composite, indexText, caseSensitiveChk, partialMatchChk);
      addToSearchTypeList(INDEX_SEARCH_FILTER);

      indexText.addModifyListener(new ModifyListener() {
         public void modifyText(ModifyEvent e) {
            addButton.setEnabled(INDEX_SEARCH_FILTER.isValid());
         }
      });
   }

   private void createArtifactTypeSearchControls(Composite optionsComposite) {
      artifactTypeControls = new Composite(optionsComposite, SWT.NONE);
      artifactTypeControls.setLayout(new GridLayout(1, true));

      artifactTypeList = new ListViewer(artifactTypeControls);
      GridData gd = new GridData();
      gd.heightHint = 100;
      artifactTypeList.getList().setLayoutData(gd);
      artifactTypeList.setContentProvider(new SearchContentProvider());
      artifactTypeList.setLabelProvider(new SearchLabelProvider());
      artifactTypeList.setSorter(new SearchSorter());

      try {
         for (ArtifactSubtypeDescriptor descriptor : configurationPersistenceManager.getArtifactSubtypeDescriptors(branchManager.getDefaultBranch())) {
            artifactTypeList.add(descriptor.getName());
            artifactTypeList.setData(descriptor.getName(), descriptor);
         }
      } catch (Exception ex) {
         logger.log(Level.SEVERE, "Error encountered while getting list of artifact types", ex);
      }
      artifactTypeList.getList().select(0);
      addToSearchTypeList(new ArtifactTypeFilter(artifactTypeControls, artifactTypeList));
   }

   private void createOrphanSearchControls(Composite optionsComposite) {
      //uses the artifactTypeList from Artifact_type_filter
      artifactTypeList.getList().select(0);
      addToSearchTypeList(new OrphanSearchFilter("Orphan Search", artifactTypeControls, artifactTypeList));
   }

   private void addToSearchTypeList(SearchFilter filter) {
      searchTypeList.add(filter.filterName);
      searchTypeList.setData(filter.filterName, filter);
   }

   private void createRelationSearchControls(Composite optionsComposite) {
      Composite relationControls = new Composite(optionsComposite, SWT.NONE);
      relationControls.setLayout(new GridLayout(2, true));

      final ComboViewer relationTypeList = new ComboViewer(relationControls, SWT.DROP_DOWN | SWT.READ_ONLY);
      relationTypeList.setContentProvider(new SearchContentProvider());
      relationTypeList.setLabelProvider(new SearchLabelProvider());
      relationTypeList.setSorter(new SearchSorter());
      final ComboViewer relationSideList = new ComboViewer(relationControls, SWT.DROP_DOWN | SWT.READ_ONLY);
      relationSideList.setContentProvider(new SearchContentProvider());
      relationSideList.setLabelProvider(new SearchLabelProvider());
      relationSideList.setSorter(new SearchSorter());

      for (IRelationLinkDescriptor linkDescriptor : RelationPersistenceManager.getInstance().getIRelationLinkDescriptors(
            branchManager.getDefaultBranch())) {
         relationTypeList.add(linkDescriptor.getName());
         relationTypeList.setData(linkDescriptor.getName(), linkDescriptor);
      }

      relationTypeList.getCombo().addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            relationSideList.getCombo().removeAll();
            IRelationLinkDescriptor linkDescriptor =
                  (IRelationLinkDescriptor) relationTypeList.getData(relationTypeList.getCombo().getText());
            relationSideList.add(linkDescriptor.getSideAName());
            relationSideList.add(linkDescriptor.getSideBName());
            relationSideList.getCombo().select(0);
         }
      });
      relationTypeList.getCombo().setVisibleItemCount(Math.min(relationTypeList.getCombo().getItemCount(), 15));

      if (relationTypeList.getCombo().getItemCount() > 0) { // ensure we don't get a null pointer
         // exception when there are no relation types in the db
         relationTypeList.getCombo().select(0);
         IRelationLinkDescriptor linkDescriptor =
               (IRelationLinkDescriptor) relationTypeList.getData(relationTypeList.getCombo().getText());
         relationSideList.add(linkDescriptor.getSideAName());
         relationSideList.add(linkDescriptor.getSideBName());
         relationSideList.getCombo().select(0);
      }

      addToSearchTypeList(new InRelationFilter(relationControls, relationTypeList, relationSideList));
   }

   private void createHridSearchControls(Composite optionsComposite) {
      Composite hridControls = new Composite(optionsComposite, SWT.NONE);
      hridControls.setLayout(new GridLayout(2, false));

      Label typeLabel = new Label(hridControls, SWT.HORIZONTAL);
      typeLabel.setText("Human Readable ID:");
      Text hridValue = new Text(hridControls, SWT.BORDER);
      hridValue.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

      hridValue.addModifyListener(new ModifyListener() {
         public void modifyText(ModifyEvent e) {
            addButton.setEnabled(HRID_VALUE_FILTER.isValid());
         }
      });

      new Label(hridControls, SWT.NONE);

      Label wildLabel = new Label(hridControls, SWT.NONE);
      wildLabel.setText("(* = any string, \\* = literal *)");

      HRID_VALUE_FILTER = new HridValueFilter(hridControls, hridValue);
      addToSearchTypeList(HRID_VALUE_FILTER);
   }

   private void createAttributeSearchControls(Composite optionsComposite) {
      Composite attributeControls = new Composite(optionsComposite, SWT.NONE);
      attributeControls.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      attributeControls.setLayout(new GridLayout(2, false));

      Label typeLabel = new Label(attributeControls, SWT.HORIZONTAL);
      typeLabel.setText("Attribute Type:");

      ComboViewer attributeTypeList = new ComboViewer(attributeControls, SWT.DROP_DOWN | SWT.READ_ONLY);
      attributeTypeList.setContentProvider(new SearchContentProvider());
      attributeTypeList.setLabelProvider(new SearchLabelProvider());
      attributeTypeList.setSorter(new SearchSorter());

      Label valueLabel = new Label(attributeControls, SWT.HORIZONTAL);
      valueLabel.setText("Attribute Value:");

      Text attributeValue = new Text(attributeControls, SWT.BORDER);
      attributeValue.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      try {
         for (DynamicAttributeDescriptor type : configurationPersistenceManager.getDynamicAttributeDescriptors(branchManager.getDefaultBranch())) {
            attributeTypeList.add(type.getName());
            attributeTypeList.setData(type.getName(), type);
         }
      } catch (Exception ex) {
         logger.log(Level.SEVERE, "Error encountered while getting list of attribute types", ex);
      }
      attributeTypeList.getCombo().setVisibleItemCount(Math.min(attributeTypeList.getCombo().getItemCount(), 15));
      attributeTypeList.getCombo().select(0);

      attributeValue.addModifyListener(new ModifyListener() {
         public void modifyText(ModifyEvent e) {
            addButton.setEnabled(ATTRIBUTE_VALUE_FILTER.isValid());
         }
      });

      new Label(attributeControls, SWT.NONE); // spacerLabelSoTheNextOneWillBeInColumnTwo

      Label wildLabel = new Label(attributeControls, SWT.NONE);
      wildLabel.setText("(* = any string, \\* = literal *)");

      ATTRIBUTE_VALUE_FILTER = new AttributeValueFilter(attributeControls, attributeTypeList, attributeValue);
      addToSearchTypeList(ATTRIBUTE_VALUE_FILTER);
   }

   private void addFilterControls(Composite mainComposite) {
      Group filterGroup = new Group(mainComposite, SWT.NONE);
      filterGroup.setText("Create a Filter");
      filterGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      filterGroup.setLayout(new GridLayout());

      Composite composite = new Composite(filterGroup, SWT.BORDER);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      composite.setLayout(new GridLayout(2, false));

      searchTypeList = new ComboViewer(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
      searchTypeList.setContentProvider(new SearchContentProvider());
      searchTypeList.setLabelProvider(new SearchLabelProvider());
      searchTypeList.setSorter(new SearchSorter());

      notButton = new Button(composite, SWT.CHECK);
      notButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true, false));
      notButton.setText("Not Equal");

      selectionLayout = new StackLayout();

      Composite optionsComposite = new Composite(filterGroup, SWT.BORDER);
      optionsComposite.setLayout(new GridLayout());
      optionsComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

      optionsComposite.setLayout(selectionLayout);
      createIndexSearchControls(optionsComposite);
      createAttributeSearchControls(optionsComposite);
      createArtifactTypeSearchControls(optionsComposite);
      createOrphanSearchControls(optionsComposite);
      createRelationSearchControls(optionsComposite);
      createHridSearchControls(optionsComposite);
      addToSearchTypeList(new CorruptedArtifactSearchFilter(optionsComposite));

      searchTypeList.getCombo().select(5);
      searchTypeList.getCombo().setVisibleItemCount(7);

      addButton = new Button(filterGroup, SWT.PUSH);
      addButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false));
      addButton.setText("Add Filter");
   }

   private void addFilterListeners() {
      addButton.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            SearchFilter searchFilter = (SearchFilter) searchTypeList.getData(searchTypeList.getCombo().getText());
            searchFilter.setNot(notButton.getSelection());
            searchFilter.addFilterTo(filterviewer);
            updateOKStatus();
         }
      });

      searchTypeList.getCombo().addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            updateWidgets();
         }
      });
   }

   private void updateWidgets() {
      SearchFilter searchFilter = (SearchFilter) searchTypeList.getData(searchTypeList.getCombo().getText());
      addButton.setEnabled(searchFilter.isValid());
      selectionLayout.topControl = searchFilter.optionsControl;
      selectionLayout.topControl.getParent().layout();
   }

   private void addTableControls(Composite composite) {
      Label tableLabel = new Label(composite, SWT.FILL);
      tableLabel.setText("Filters");

      Table table = new Table(composite, SWT.BORDER | SWT.V_SCROLL | SWT.HIDE_SELECTION);
      filterviewer = new FilterTableViewer(table);
      GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
      gridData.heightHint = 100;
      gridData.widthHint = 500;
      table.setLayoutData(gridData);
   }

   public void setContainer(ISearchPageContainer container) {
      ArtifactSearchPage.aContainer = container;
   }

   /**
    * @return Returns the aContainer.
    */
   public static ISearchPageContainer getContainer() {
      return aContainer;
   }

   public boolean performAction() {
      NewSearchUI.activateSearchResultView();
      filterviewer.getFilterList().setAllSelected(allButton.getSelection());
      AbstractArtifactSearchQuery searchQuery =
            new FilterArtifactSearchQuery(filterviewer.getFilterList(), branchManager.getDefaultBranch());
      NewSearchUI.runQueryInBackground(searchQuery);
      return true;
   }

   public boolean performReplace() {
      filterviewer.getFilterList().setAllSelected(allButton.getSelection());
      AbstractArtifactSearchQuery searchQuery =
            new FilterArtifactSearchQuery(filterviewer.getFilterList(), branchManager.getDefaultBranch());

      IStatus status = NewSearchUI.runQueryInForeground(getContainer().getRunnableContext(), searchQuery);
      if (status.matches(IStatus.CANCEL)) {
         return false;
      }

      ISearchResultViewPart view = NewSearchUI.activateSearchResultView();
      if (view != null) {
         final ISearchResultPage page = view.getActivePage();
         if (page instanceof ArtifactSearchViewPage) {
            runAttributeFindReplaceDialog(page);
         }
      }
      return true;
   }

   private void runAttributeFindReplaceDialog(final ISearchResultPage page) {
      Display.getCurrent().asyncExec(new Runnable() {
         public void run() {
            if (page instanceof ArtifactSearchViewPage) {
               ArtifactSearchViewPage artifactPage = (ArtifactSearchViewPage) page;
               List<Artifact> artifacts = artifactPage.getInput().getArtifactResults();
               new AttributeFindReplaceDialog(page.getSite().getShell(), artifacts).open();
            }
         }
      });
   }

   /*
    * Implements method from IDialogPage
    */
   public void setVisible(boolean visible) {
      if (visible) {
         indexText.setFocus();
      }
      updateOKStatus();
      super.setVisible(visible);
   }

   public static void updateOKStatus() {
      if (filterviewer.getFilterList().getFilters().isEmpty())
         getContainer().setPerformActionEnabled(false);
      else
         getContainer().setPerformActionEnabled(true);
   }

   public class SearchLabelProvider implements ILabelProvider {

      public Image getImage(Object arg0) {
         return null;
      }

      public String getText(Object arg0) {
         return (String) arg0;
      }

      public void addListener(ILabelProviderListener arg0) {
      }

      public void dispose() {
      }

      public boolean isLabelProperty(Object arg0, String arg1) {
         return false;
      }

      public void removeListener(ILabelProviderListener arg0) {
      }
   }

   public class SearchContentProvider implements IStructuredContentProvider {
      @SuppressWarnings("unchecked")
      public Object[] getElements(Object arg0) {
         return ((ArrayList) arg0).toArray();
      }

      public void dispose() {
      }

      public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
      }
   }

   public class SearchSorter extends ViewerSorter {
      @SuppressWarnings("unchecked")
      @Override
      public int compare(Viewer viewer, Object e1, Object e2) {
         return getComparator().compare((String) e1, (String) e2);
      }
   }
}
