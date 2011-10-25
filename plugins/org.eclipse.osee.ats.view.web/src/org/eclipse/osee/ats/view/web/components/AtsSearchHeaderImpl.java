/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.view.web.components;

import org.eclipse.osee.ats.api.components.AtsSearchHeaderComponent;
import org.eclipse.osee.ats.api.data.AtsSearchParameters;
import org.eclipse.osee.ats.api.search.AtsSearchPresenter;
import org.eclipse.osee.ats.view.web.AtsNavigator;
import org.eclipse.osee.ats.view.web.AtsUiApplication;
import org.eclipse.osee.ats.view.web.search.AtsSearchResultsView;
import org.eclipse.osee.display.api.data.ViewId;
import org.eclipse.osee.display.view.web.CssConstants;
import org.eclipse.osee.display.view.web.components.ComponentUtility;
import org.eclipse.osee.display.view.web.components.OseeExceptionDialogComponent;
import org.eclipse.osee.display.view.web.components.OseeLeftMarginContainer;
import org.eclipse.osee.display.view.web.components.OseeLogoLink;
import org.eclipse.osee.display.view.web.components.OseeSearchHeaderComponent;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class AtsSearchHeaderImpl extends OseeSearchHeaderComponent implements AtsSearchHeaderComponent, Handler {

   private boolean isLayoutComplete = false;
   private final ComboBox programCombo = new ComboBox("Program:");
   private final ComboBox buildCombo = new ComboBox("Build:");
   private final CheckBox nameOnlyCheckBox = new CheckBox("Name Only", true);
   private final TextField searchTextField = new TextField();
   private final Button searchButton = new Button("Search");
   private boolean lockProgramCombo = false;
   Panel searchTextPanel = new Panel();

   @Override
   public void attach() {
      if (!isLayoutComplete) {
         createLayout();
      }
      isLayoutComplete = true;
   }

   private void validateSearchAndEnableSearchButton() {
      ViewId program = (ViewId) programCombo.getValue();
      ViewId build = (ViewId) buildCombo.getValue();
      if (program != null && build != null) {
         searchButton.setEnabled(true);
      } else {
         searchButton.setEnabled(false);
      }
   }

   private void selectProgram() {
      if (ComponentUtility.isAccessible(programCombo)) {
         ViewId program = (ViewId) programCombo.getValue();
         getPresenter().selectProgram(program, this);
      }
   }

   public AtsSearchHeaderImpl() {
      if (ComponentUtility.isAccessible(programCombo)) {
         programCombo.setNullSelectionAllowed(false);
         programCombo.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
               if (!lockProgramCombo) {
                  selectProgram();
               }
               validateSearchAndEnableSearchButton();
            }
         });
         programCombo.setImmediate(true);

         buildCombo.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
               validateSearchAndEnableSearchButton();
            }
         });
         buildCombo.setImmediate(true);
      }
      if (ComponentUtility.isAccessible(buildCombo)) {
         buildCombo.setNullSelectionAllowed(false);
      }

      searchTextField.setImmediate(true);
      validateSearchAndEnableSearchButton();
   }

   protected void selectSearch() {
      if (searchButton.isEnabled()) {
         if (ComponentUtility.isAccessible(programCombo, buildCombo, nameOnlyCheckBox, searchTextField)) {
            ViewId program = (ViewId) programCombo.getValue();
            ViewId build = (ViewId) buildCombo.getValue();
            boolean nameOnly = nameOnlyCheckBox.toString().equalsIgnoreCase("true");
            String searchPhrase = (String) searchTextField.getValue();
            AtsSearchParameters params = new AtsSearchParameters(searchPhrase, nameOnly, build, program);
            getPresenter().selectSearch(getRequestedDataId(), params, getNavigator());
         } else {
            System.out.println("AtsSearchHeaderComponent.selectSearch - WARNING: null value detected.");
         }
      }
   }

   public void createLayout() {
      setWidth(100, UNITS_PERCENTAGE);
      setStyleName(CssConstants.OSEE_SEARCH_HEADER_COMPONENT_SMALL);

      HorizontalLayout hLayout_ProgBuildName = new HorizontalLayout();
      HorizontalLayout hLayout_SearchTextBtn = new HorizontalLayout();
      hLayout_ProgBuildName.setSizeUndefined();
      hLayout_SearchTextBtn.setSizeUndefined();

      Label hSpacer_ProgBuild = new Label("");
      hSpacer_ProgBuild.setHeight(null);
      hSpacer_ProgBuild.setWidth(30, UNITS_PIXELS);
      Label hSpacer_BuildName = new Label("");
      hSpacer_BuildName.setHeight(null);
      hSpacer_BuildName.setWidth(30, UNITS_PIXELS);

      searchTextField.setStyleName(CssConstants.OSEE_SEARCH_TEXTFIELD);

      Label hSpacer_SearchTextBtn = new Label("");
      hSpacer_SearchTextBtn.setHeight(null);
      hSpacer_SearchTextBtn.setWidth(30, UNITS_PIXELS);
      searchButton.addListener(new Button.ClickListener() {
         @Override
         public void buttonClick(Button.ClickEvent event) {
            String msg = "";
            for (int i = 0; i < 1000; i++) {
               msg += this.toString();
               msg += "\n";
            }
            selectSearch();
         }
      });

      OseeLogoLink oseeLogoImg =
         new OseeLogoLink(getNavigator(), CssConstants.OSEE_TITLE_MEDIUM_TEXT, AtsSearchResultsView.class);
      Label hSpacer_LogoRight = new Label("");
      oseeLogoImg.setSizeUndefined();

      hSpacer_LogoRight.setWidth(15, UNITS_PIXELS);

      VerticalLayout vLayout_SearchCrit = new VerticalLayout();
      vLayout_SearchCrit.setSizeUndefined();

      OseeLeftMarginContainer leftMarginContainer = new OseeLeftMarginContainer();

      HorizontalLayout hLayout_SearchText = new HorizontalLayout();
      searchTextPanel.setScrollable(false);
      searchTextPanel.addActionHandler(this);
      searchTextPanel.setContent(hLayout_SearchText);

      hLayout_SearchText.addComponent(searchTextField);

      hLayout_ProgBuildName.addComponent(programCombo);
      hLayout_ProgBuildName.addComponent(hSpacer_ProgBuild);
      hLayout_ProgBuildName.addComponent(buildCombo);
      hLayout_ProgBuildName.addComponent(hSpacer_BuildName);
      hLayout_ProgBuildName.addComponent(nameOnlyCheckBox);

      hLayout_SearchTextBtn.addComponent(searchTextPanel);
      hLayout_SearchTextBtn.addComponent(hSpacer_SearchTextBtn);
      hLayout_SearchTextBtn.addComponent(searchButton);

      vLayout_SearchCrit.addComponent(hLayout_ProgBuildName);
      vLayout_SearchCrit.addComponent(hLayout_SearchTextBtn);

      leftMarginContainer.addComponent(oseeLogoImg);
      leftMarginContainer.addComponent(hSpacer_LogoRight);
      leftMarginContainer.addComponent(vLayout_SearchCrit);

      setCompositionRoot(leftMarginContainer);

      hLayout_ProgBuildName.setComponentAlignment(programCombo, Alignment.MIDDLE_LEFT);
      hLayout_ProgBuildName.setComponentAlignment(buildCombo, Alignment.MIDDLE_CENTER);
      hLayout_ProgBuildName.setComponentAlignment(nameOnlyCheckBox, Alignment.BOTTOM_RIGHT);

      //      hLayout_SearchTextBtn.setComponentAlignment(searchTextField, Alignment.MIDDLE_LEFT);
      hLayout_SearchTextBtn.setComponentAlignment(searchButton, Alignment.MIDDLE_RIGHT);
   }

   @Override
   public void addProgram(ViewId program) {
      if (ComponentUtility.isAccessible(programCombo)) {
         lockProgramCombo = true;
         programCombo.addItem(program);
         lockProgramCombo = false;
      }
   }

   @Override
   public void clearBuilds() {
      if (ComponentUtility.isAccessible(buildCombo) && !lockProgramCombo) {
         buildCombo.removeAllItems();
      }
   }

   @Override
   public void addBuild(ViewId build) {
      if (ComponentUtility.isAccessible(buildCombo)) {
         buildCombo.addItem(build);
      }
   }

   @Override
   public void setSearchCriteria(AtsSearchParameters params) {
      if (params != null) {
         if (ComponentUtility.isAccessible(programCombo)) {
            //            lockProgramCombo = true;
            programCombo.setValue(params.getProgram());
            //            lockProgramCombo = false;
         }
         if (ComponentUtility.isAccessible(buildCombo)) {
            buildCombo.setValue(params.getBuild());
         }
         if (ComponentUtility.isAccessible(nameOnlyCheckBox)) {
            nameOnlyCheckBox.setValue(params.isNameOnly());
         }
         if (ComponentUtility.isAccessible(searchTextField)) {
            searchTextField.setValue(params.getSearchString());
         }
      }
   }

   @Override
   public void clearAll() {
      if (ComponentUtility.isAccessible(programCombo)) {
         //         programCombo.removeAllItems();
         programCombo.setValue(null);
      }
      if (ComponentUtility.isAccessible(buildCombo)) {
         //         buildCombo.removeAllItems();
         buildCombo.setValue(null);
      }
      if (ComponentUtility.isAccessible(nameOnlyCheckBox)) {
         nameOnlyCheckBox.setValue(true);
      }
      if (ComponentUtility.isAccessible(searchTextField)) {
         searchTextField.setValue("");
      }
   }

   @Override
   public void setErrorMessage(String shortMsg, String longMsg, MsgType msgType) {
      OseeExceptionDialogComponent dlg =
         new OseeExceptionDialogComponent(msgType, shortMsg, longMsg, getApplication().getMainWindow());
   }

   //TODO: None of this works because Vaadin only supports key actions for Windows and Panel Objects. (this is
   // a Component)
   private final Action action_enter = new ShortcutAction("Enter key", ShortcutAction.KeyCode.ENTER, null);
   private final Action[] actions = new Action[] {action_enter};

   @Override
   public Action[] getActions(Object target, Object sender) {
      return actions;
   }

   @Override
   public void handleAction(Action action, Object sender, Object target) {
      if (sender == searchTextPanel && action == action_enter) {
         selectSearch();
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   public AtsUiApplication<AtsSearchHeaderComponent, AtsSearchParameters> getApplication() {
      return (AtsUiApplication<AtsSearchHeaderComponent, AtsSearchParameters>) super.getApplication();
   }

   private AtsNavigator getNavigator() {
      return getApplication().getNavigator();
   }

   private AtsSearchPresenter<AtsSearchHeaderComponent, AtsSearchParameters> getPresenter() {
      return getApplication().getPresenter();
   }

   private String getRequestedDataId() {
      return getApplication().getUrl();
   }
}
