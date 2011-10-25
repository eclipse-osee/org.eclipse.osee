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
import org.eclipse.osee.display.view.web.OseeUiApplication;
import org.eclipse.osee.display.view.web.components.OseeLeftMarginContainer;
import org.eclipse.osee.display.view.web.components.OseeLogoLink;
import org.eclipse.osee.display.view.web.components.OseeSearchHeaderComponent;
import com.vaadin.Application;
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
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

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
   private AtsSearchPresenter searchPresenter;
   private AtsNavigator navigator;
   private boolean lockProgramCombo = false;
   Panel searchTextPanel = new Panel();

   @Override
   public void attach() {
      if (!isLayoutComplete) {
         try {
            AtsUiApplication app = (AtsUiApplication) this.getApplication();
            searchPresenter = app.getAtsWebSearchPresenter();
            navigator = app.getAtsNavigator();
         } catch (Exception e) {
            System.out.println("OseeArtifactNameLinkComponent.attach - CRITICAL ERROR: (AtsUiApplication) this.getApplication() threw an exception.");
         }
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
      if (programCombo != null) {
         ViewId program = (ViewId) programCombo.getValue();
         searchPresenter.selectProgram(program, this);
      }
   }

   public AtsSearchHeaderImpl() {
      if (programCombo != null) {
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
      if (buildCombo != null) {
         buildCombo.setNullSelectionAllowed(false);
      }

      searchTextField.setImmediate(true);
      validateSearchAndEnableSearchButton();
   }

   protected void selectSearch() {
      if (searchButton.isEnabled()) {
         if (searchPresenter != null && programCombo != null && buildCombo != null && nameOnlyCheckBox != null && searchTextField != null) {
            ViewId program = (ViewId) programCombo.getValue();
            ViewId build = (ViewId) buildCombo.getValue();
            boolean nameOnly = nameOnlyCheckBox.toString().equalsIgnoreCase("true");
            String searchPhrase = (String) searchTextField.getValue();
            AtsSearchParameters params = new AtsSearchParameters(searchPhrase, nameOnly, build, program);
            OseeUiApplication app = (OseeUiApplication) getApplication();
            searchPresenter.selectSearch(app.getRequestedDataId(), params, navigator);
         } else {
            System.out.println("AtsSearchHeaderComponent.selectSearch - WARNING: null value detected.");
         }
      }
   }

   @Override
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
            selectSearch();
         }
      });

      OseeLogoLink oseeLogoImg =
         new OseeLogoLink(navigator, CssConstants.OSEE_TITLE_MEDIUM_TEXT, AtsSearchResultsView.class);
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
      if (programCombo != null) {
         lockProgramCombo = true;
         programCombo.addItem(program);
         lockProgramCombo = false;
      }
   }

   @Override
   public void clearBuilds() {
      if (buildCombo != null && !lockProgramCombo) {
         buildCombo.removeAllItems();
      }
   }

   @Override
   public void addBuild(ViewId build) {
      if (buildCombo != null) {
         buildCombo.addItem(build);
      }
   }

   @Override
   public void setSearchCriteria(AtsSearchParameters params) {
      if (params != null) {
         if (programCombo != null) {
            //            lockProgramCombo = true;
            programCombo.setValue(params.getProgram());
            //            lockProgramCombo = false;
         }
         if (buildCombo != null) {
            buildCombo.setValue(params.getBuild());
         }
         if (nameOnlyCheckBox != null) {
            nameOnlyCheckBox.setValue(params.isNameOnly());
         }
         if (searchTextField != null) {
            searchTextField.setValue(params.getSearchString());
         }
      }
   }

   @Override
   public void clearAll() {
      if (programCombo != null) {
         //         programCombo.removeAllItems();
         programCombo.setValue(null);
      }
      if (buildCombo != null) {
         //         buildCombo.removeAllItems();
         buildCombo.setValue(null);
      }
      if (nameOnlyCheckBox != null) {
         nameOnlyCheckBox.setValue(true);
      }
      if (searchTextField != null) {
         searchTextField.setValue("");
      }
   }

   @Override
   public void setErrorMessage(String message) {
      System.out.println(message);
      Application app = this.getApplication();
      if (app != null) {
         Window mainWindow = app.getMainWindow();
         if (mainWindow != null) {
            mainWindow.showNotification(message, Notification.TYPE_ERROR_MESSAGE);
         } else {
            System.out.println("AtsSearchHeaderComponent.setErrorMessage - ERROR: Application.getMainWindow() returns null value.");
         }
      } else {
         System.out.println("AtsSearchHeaderComponent.setErrorMessage - ERROR: getApplication() returns null value.");
      }
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

}
