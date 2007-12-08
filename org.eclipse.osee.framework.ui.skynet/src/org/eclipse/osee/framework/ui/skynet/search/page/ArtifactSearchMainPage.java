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
package org.eclipse.osee.framework.ui.skynet.search.page;

import java.sql.SQLException;
import java.util.List;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.osee.framework.ui.skynet.search.page.data.ArtifactTypeNode;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ArtifactSearchMainPage extends DialogPage implements ISearchPage {
   public static final String EXTENSION_POINT_ID = "osee.define.artifact.search.ArtifactSearchMainPage"; //$NON-NLS-1$

   private static ISearchPageContainer aContainer;
   private ArtifactSearchComposite artifactTreeSearchComposite;
   private Composite advancedSearchComposite;
   private Composite stackComposite;
   private StackLayout stackLayout;

   public ArtifactSearchMainPage() {
      super();
   }

   public void createControl(Composite parent) {
      initializeDialogUnits(parent);

      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      createSearchTypeSelectArea(composite);

      stackComposite = new Composite(composite, SWT.NONE);
      stackLayout = new StackLayout();
      stackComposite.setLayout(stackLayout);
      stackComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      artifactTreeSearchComposite = new ArtifactSearchComposite(stackComposite, SWT.NONE);
      advancedSearchComposite = new Composite(stackComposite, SWT.BORDER);

      stackLayout.topControl = artifactTreeSearchComposite;
      stackComposite.layout();

      setControl(parent);
      aContainer.setPerformActionEnabled(false);
   }

   private void createSearchTypeSelectArea(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout(2, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      Button treeSearch = new Button(composite, SWT.RADIO);
      treeSearch.setSelection(true);
      treeSearch.setText("Tree Search");
      treeSearch.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            stackLayout.topControl = artifactTreeSearchComposite;
            stackComposite.layout();
         }
      });

      Button advanceSearch = new Button(composite, SWT.RADIO);
      advanceSearch.setText("Advanced Search");
      advanceSearch.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            stackLayout.topControl = advancedSearchComposite;
            stackComposite.layout();
         }
      });
   }

   // private void addSearchScope(Composite composite) {
   // Group allSelectionGroup = new Group(composite, SWT.NONE);
   // allSelectionGroup.setText("Matches");
   // GridLayout layout = new GridLayout();
   // GridData grid = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
   // layout.numColumns = 2;
   // allSelectionGroup.setLayout(layout);
   // allSelectionGroup.setLayoutData(grid);
   //
   // allButton = new Button(allSelectionGroup, SWT.RADIO);
   // allButton.setText("All filters");
   // allButton.setSelection(true);
   //
   // atLeastOneButton = new Button(allSelectionGroup, SWT.RADIO);
   // atLeastOneButton.setText("At least one filter");
   //
   // allButton.addSelectionListener(new SelectionListener() {
   //
   // public void widgetSelected(SelectionEvent e) {
   // isAllSelected = true;
   // }
   //
   // public void widgetDefaultSelected(SelectionEvent e) {
   //
   // }
   // });
   //
   // atLeastOneButton.addSelectionListener(new SelectionListener() {
   //
   // public void widgetSelected(SelectionEvent e) {
   // isAllSelected = false;
   // }
   //
   // public void widgetDefaultSelected(SelectionEvent e) {
   //
   // }
   // });
   // }

   // private void addFilterControls(Composite composite) {
   // Group filterGroup = new Group(composite, SWT.NONE);
   // filterGroup.setText("Create Filter");
   // GridLayout layout = new GridLayout();
   // GridData grid = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
   // layout.numColumns = 4;
   // filterGroup.setLayout(layout);
   // filterGroup.setLayoutData(grid);
   //
   // searchTypeLabel = new Label(filterGroup, SWT.HORIZONTAL);
   // searchTypeLabel.setText("Filter");
   //
   // aTypeLabel = new Label(filterGroup, SWT.HORIZONTAL);
   // aTypeLabel.setText("Type");
   // aTypeLabel.setVisible(false);
   //
   // aValueLabel = new Label(filterGroup, SWT.HORIZONTAL);
   // aValueLabel.setText("Value");
   // aValueLabel.setVisible(false);
   //
   // new Label(filterGroup, SWT.NONE);
   //
   // searchTypeList = new Combo(filterGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
   // searchTypeList.setItems(searchListArray);
   // searchTypeList.select(0);
   //
   // Composite selectionComp = new Composite(filterGroup, SWT.NONE);
   // selectionLayout = new StackLayout();
   // selectionComp.setLayout(selectionLayout);
   // selectionComp.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));
   //
   // stdSelection = new Composite(selectionComp, SWT.NONE);
   // stdSelection.setLayout(new GridLayout(2, true));
   //
   // aTypeText = new Text(stdSelection, SWT.BORDER);
   // aTypeText.setVisible(false);
   //
   // aValueText = new Text(stdSelection, SWT.BORDER);
   // aValueText.setText("");
   // aValueText.setVisible(false);
   //
   // relSelection = new Composite(selectionComp, SWT.NONE);
   // relSelection.setLayout(new GridLayout(2, true));
   //
   // relationTypeList = new Combo(relSelection, SWT.DROP_DOWN | SWT.READ_ONLY);
   // for (IRelationLinkDescriptor linkDescriptor :
   // RelationPersistenceManager.getInstance().getIRelationLinkDescriptors()) {
   // relationTypeList.add(linkDescriptor.getName());
   // relationTypeList.setData(linkDescriptor.getName(), linkDescriptor);
   // }
   // relationTypeList.addSelectionListener(new SelectionAdapter() {
   // @Override
   // public void widgetSelected(SelectionEvent e) {
   // relationSideList.removeAll();
   // IRelationLinkDescriptor linkDescriptor = (IRelationLinkDescriptor)
   // relationTypeList.getData(relationTypeList.getText());
   // relationSideList.add(linkDescriptor.getSideAName());
   // relationSideList.add(linkDescriptor.getSideBName());
   // relationSideList.select(0);
   // }
   // });
   // relationTypeList.select(0);
   // relationTypeList.setVisible(true);
   //
   // relationSideList = new Combo(relSelection, SWT.DROP_DOWN | SWT.READ_ONLY);
   // IRelationLinkDescriptor linkDescriptor = (IRelationLinkDescriptor)
   // relationTypeList.getData(relationTypeList.getText());
   // relationSideList.add(linkDescriptor.getSideAName());
   // relationSideList.add(linkDescriptor.getSideBName());
   // relationSideList.select(0);
   // relationSideList.setVisible(true);
   //
   // selectionLayout.topControl = stdSelection;
   //
   // addButton = new Button(filterGroup, SWT.PUSH);
   // addButton.setText("Add");
   //
   // new Label(filterGroup, SWT.NONE).setText("search string(* = any string)");
   //
   // wildCardChkBox = new Button(filterGroup, SWT.CHECK);
   // wildCardChkBox.setText("Wild Card");
   // wildCardChkBox.setEnabled(false);
   //
   // updateAddButton();
   // }

   // private void addFilterListeners() {
   // wildCardChkBox.addSelectionListener(new SelectionListener() {
   //
   // public void widgetSelected(SelectionEvent e) {
   // wildCardChecked = !wildCardChecked;
   // }
   //
   // public void widgetDefaultSelected(SelectionEvent e) {
   // }
   //
   // });
   // addButton.addSelectionListener(new SelectionListener() {
   // public void widgetSelected(SelectionEvent e) {
   // addFilterToTable();
   // updateOKStatus();
   // aValueText.setText("");
   // aTypeText.setText("");
   // }
   //
   // public void widgetDefaultSelected(SelectionEvent e) {
   // }
   // });
   //
   // searchTypeList.addSelectionListener(new SelectionListener() {
   //
   // public void widgetSelected(SelectionEvent e) {
   // if (searchTypeList.getText().compareTo(SELECT_FILTER) == 0) {
   // setVisibilityOfWidgets(false, false, false, false, false, false);
   // wildCardChecked = false;
   // }
   // else if (searchTypeList.getText().compareTo(ATTRIBUTE_VALUE_FILTER) == 0) {
   // setVisibilityOfWidgets(true, true, true, true, true, false);
   // }
   // else if (searchTypeList.getText().compareTo(ARTIFACT_TYPE_FILTER) == 0) {
   // setVisibilityOfWidgets(true, true, false, false, true, false);
   // }
   // else if (searchTypeList.getText().compareTo(IN_RELATION_FILTER) == 0) {
   // setVisibilityOfWidgets(true, true, true, true, false, true);
   // wildCardChecked = false;
   // }
   // updateAddButton();
   // }
   //
   // public void widgetDefaultSelected(SelectionEvent e) {
   // }
   // });
   //
   // aTypeText.addModifyListener(new ModifyListener() {
   // public void modifyText(ModifyEvent e) {
   // updateAddButton();
   // }
   // });
   //
   // aValueText.addModifyListener(new ModifyListener() {
   // public void modifyText(ModifyEvent e) {
   // updateAddButton();
   // }
   // });
   // }

   // /**
   // * @param typeLabel
   // * @param typeText
   // * @param valueLabel
   // * @param valueText
   // * @param wildcard
   // * @param relationLists
   // */
   // private void setVisibilityOfWidgets(boolean typeLabel, boolean typeText, boolean valueLabel,
   // boolean valueText,
   // boolean wildcard, boolean relationLists) {
   //
   // aValueLabel.setText((relationLists) ? "Side" : "Value");
   //
   // aTypeLabel.setVisible(typeLabel);
   // aTypeText.setVisible(typeText && !relationLists);
   // aValueLabel.setVisible(valueLabel);
   // aValueText.setVisible(valueText && !relationLists);
   // // relationTypeList.setVisible(typeText && relationLists);
   // // relationSideList.setVisible(valueText && relationLists);
   // // relationTypeList.setVisible(true);
   // // relationSideList.setVisible(true);
   // selectionLayout.topControl = (relationLists) ? relSelection : stdSelection;
   // selectionLayout.topControl.getParent().layout();
   //
   // wildCardChkBox.setEnabled(wildcard);
   // }
   //
   // private void addTableControls(Composite composite) {
   // Label tableLabel = new Label(composite, SWT.HORIZONTAL);
   // tableLabel.setText("Filters");
   //
   // Table table = new Table(composite, SWT.BORDER | SWT.V_SCROLL | SWT.HIDE_SELECTION);
   // viewer = new FilterTableViewer(table);
   // GridData gridData = new GridData();
   // gridData.heightHint = 150;
   // table.setLayoutData(gridData);
   // }
   //
   // private void updateAddButton() {
   // if (selectionLayout.topControl == relSelection)
   // addButton.setEnabled(true);
   // else if (aTypeText.getText().compareTo("") == 0)
   // addButton.setEnabled(false);
   // else if (searchTypeList.getText().compareTo(SELECT_FILTER) == 0)
   // addButton.setEnabled(false);
   // else if (aValueText.isVisible()) {
   // if (aValueText.getText().length() == 0)
   // addButton.setEnabled(false);
   // else
   // addButton.setEnabled(true);
   // }
   // else
   // addButton.setEnabled(true);
   // }

   // private void addFilterToTable() {
   // ISearchPrimitive primitive = null;
   // Operator operator = null;
   // String type = aTypeText.getText();
   // String value = aValueText.getText();
   //
   // // TODO create class that will process querys...
   // if (wildCardChecked) {
   // operator = LIKE;
   // type = type.replace("*", "%");
   // value = value.replace("*", "%");
   // }
   // else {
   // operator = EQUAL;
   // }
   //
   // if (searchTypeList.getText().equals(ATTRIBUTE_VALUE_FILTER)) {
   // primitive = new AttributeValueSearch(type, value, operator);
   // }
   // else if (searchTypeList.getText().equals(ARTIFACT_TYPE_FILTER)) {
   // primitive = new ArtifactTypeSearch(type, operator);
   // }
   // else if (searchTypeList.getText().equals(IN_RELATION_FILTER)) {
   // IRelationLinkDescriptor linkDescriptor = (IRelationLinkDescriptor)
   // relationTypeList.getData(relationTypeList.getText());
   // primitive = new InRelationSearch(relationTypeList.getText(),
   // linkDescriptor.getSideAName().equals(
   // relationSideList.getText()));
   // }
   //
   // if (primitive != null) {
   // String typeTxt, valueTxt;
   // if (searchTypeList.getText().equals(IN_RELATION_FILTER)) {
   // typeTxt = relationTypeList.getText();
   // valueTxt = relationSideList.getText();
   // }
   // else {
   // typeTxt = aTypeText.getText();
   // valueTxt = aValueText.getText();
   // }
   // viewer.addItem(primitive, searchTypeList.getText(), typeTxt, valueTxt);
   // }
   // }

   @Override
   public void dispose() {
      super.dispose();
      this.artifactTreeSearchComposite.dispose();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.search.ui.ISearchPage#setContainer(org.eclipse.search.ui.ISearchPageContainer)
    */
   public void setContainer(ISearchPageContainer container) {
      ArtifactSearchMainPage.aContainer = container;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.search.ui.ISearchPage#performAction()
    */
   public boolean performAction() {
      System.out.println("Performing Query....");

      // NewSearchUI.activateSearchResultView();
      // viewer.getFilterList().setAllSelected(isAllSelected);
      // ArtifactSearchQuery searchQuery = new ArtifactSearchQuery(viewer.getFilterList());
      // NewSearchUI.runQueryInBackground(searchQuery);

      OriginalArtifactSearch search = new OriginalArtifactSearch();
      List<ArtifactTypeNode> treeRoots = artifactTreeSearchComposite.getTreeWidget().getInputManager().getInputList();
      for (ArtifactTypeNode node : treeRoots) {
         try {
            //            Tree<Artifact> artifactTree = 
            search.getArtifactSearch(node);
         } catch (SQLException e) {

            e.printStackTrace();
         }

      }

      return true;
   }

   // /**
   // * Returns the aContainer.
   // */
   // public static ISearchPageContainer getContainer() {
   // return aContainer;
   // }
   // /*
   // * Implements method from IDialogPage
   // */

   /**
    * @param visible - boolean indication to set.
    */
   public void setVisible(boolean visible) {
      // if (visible) {
      // searchTypeList.setFocus();
      // }
      // updateOKStatus();
      super.setVisible(visible);
   }

   // public static void updateOKStatus() {
   // if (viewer.getFilterList().getFilters().isEmpty())
   // getContainer().setPerformActionEnabled(false);
   // else
   // getContainer().setPerformActionEnabled(true);
   // }
}
