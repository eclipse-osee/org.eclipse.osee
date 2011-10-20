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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class AtsSearchHeaderImpl extends OseeSearchHeaderComponent implements AtsSearchHeaderComponent, Handler {

   private boolean populated = false;
   private final ComboBox programCombo = new ComboBox("Program:");
   private final ComboBox buildCombo = new ComboBox("Build:");
   private final CheckBox nameOnlyCheckBox = new CheckBox("Name Only", true);
   private final TextField searchTextField = new TextField();
   private AtsSearchPresenter searchPresenter;
   private AtsNavigator navigator;
   private boolean lockProgramCombo = false;

   @Override
   public void attach() {
      if (!populated) {
         try {
            AtsUiApplication app = (AtsUiApplication) this.getApplication();
            searchPresenter = app.getAtsWebSearchPresenter();
            navigator = app.getAtsNavigator();
         } catch (Exception e) {
            System.out.println("OseeArtifactNameLinkComponent.attach - CRITICAL ERROR: (AtsUiApplication) this.getApplication() threw an exception.");
         }
         createLayout();
      }
      populated = true;
   }

   private void selectProgram() {
      if (programCombo != null) {
         ViewId program = (ViewId) programCombo.getValue();
         searchPresenter.selectProgram(program, this);
      }
   }

   public AtsSearchHeaderImpl(boolean showOseeTitleAbove) {
      this.showOseeTitleAbove = showOseeTitleAbove;

      if (programCombo != null) {
         programCombo.setNullSelectionAllowed(false);
         programCombo.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
               if (!lockProgramCombo) {
                  selectProgram();
               }
            }
         });
         programCombo.setImmediate(true);
      }
      if (buildCombo != null) {
         buildCombo.setNullSelectionAllowed(false);
      }

      searchTextField.setImmediate(true);
   }

   public AtsSearchHeaderImpl() {
      this(true);
   }

   protected void selectSearch() {
      if (searchPresenter != null && programCombo != null && buildCombo != null && nameOnlyCheckBox != null && searchTextField != null) {
         ViewId program = (ViewId) programCombo.getValue();
         ViewId build = (ViewId) buildCombo.getValue();
         boolean nameOnly = nameOnlyCheckBox.toString().equalsIgnoreCase("true");
         String searchPhrase = (String) searchTextField.getValue();
         AtsSearchParameters params =
            new AtsSearchParameters(searchPhrase, nameOnly, showVerboseSearchResults, build, program);
         searchPresenter.selectSearch(params, navigator);
      } else {
         System.out.println("AtsSearchHeaderComponent.selectSearch - WARNING: null value detected.");
      }
   }

   @Override
   public void createLayout() {
      removeAllComponents();
      setHeight(null);
      setWidth(100, UNITS_PERCENTAGE);
      setStyleName(CssConstants.OSEE_SEARCH_HEADER_COMPONENT_SMALL);

      HorizontalLayout hLayout_Body = new HorizontalLayout();
      HorizontalLayout hLayout_ProgBuildName = new HorizontalLayout();
      HorizontalLayout hLayout_SearchTextBtn = new HorizontalLayout();

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
      Button searchButton = new Button("Search", new Button.ClickListener() {
         @Override
         public void buttonClick(ClickEvent event) {
            selectSearch();
         }
      });

      OseeLogoLink oseeLogoImg =
         new OseeLogoLink(navigator, CssConstants.OSEE_TITLE_MEDIUM_TEXT, AtsSearchResultsView.class);
      Label hSpacer_LogoRight = new Label("");
      hSpacer_LogoRight.setWidth(15, UNITS_PIXELS);

      VerticalLayout vLayout_SearchCrit = new VerticalLayout();
      vLayout_SearchCrit.setSizeUndefined();

      Label hSpacer_LeftMarg = new Label();
      hSpacer_LeftMarg.setWidth(CssConstants.OSEE_LEFTMARGINWIDTH, UNITS_PIXELS);

      Label vSpacer_BotLine = new Label("");
      vSpacer_BotLine.setStyleName(CssConstants.OSEE_SEARCH_HEADER_COMPONENT_FOOTER);

      Label vSpacer_TopLine = new Label("");
      vSpacer_TopLine.setStyleName(CssConstants.OSEE_SEARCH_HEADER_COMPONENT_FOOTER);

      hLayout_ProgBuildName.addComponent(programCombo);
      hLayout_ProgBuildName.addComponent(hSpacer_ProgBuild);
      hLayout_ProgBuildName.addComponent(buildCombo);
      hLayout_ProgBuildName.addComponent(hSpacer_BuildName);
      hLayout_ProgBuildName.addComponent(nameOnlyCheckBox);

      hLayout_SearchTextBtn.addComponent(searchTextField);
      hLayout_SearchTextBtn.addComponent(hSpacer_SearchTextBtn);
      hLayout_SearchTextBtn.addComponent(searchButton);

      vLayout_SearchCrit.addComponent(hLayout_ProgBuildName);
      vLayout_SearchCrit.addComponent(hLayout_SearchTextBtn);

      hLayout_Body.addComponent(hSpacer_LeftMarg);
      hLayout_Body.addComponent(oseeLogoImg);
      hLayout_Body.addComponent(hSpacer_LogoRight);
      hLayout_Body.addComponent(vLayout_SearchCrit);

      addComponent(vSpacer_TopLine);
      addComponent(hLayout_Body);
      addComponent(vSpacer_BotLine);

      hLayout_ProgBuildName.setComponentAlignment(programCombo, Alignment.MIDDLE_LEFT);
      hLayout_ProgBuildName.setComponentAlignment(buildCombo, Alignment.MIDDLE_CENTER);
      hLayout_ProgBuildName.setComponentAlignment(nameOnlyCheckBox, Alignment.BOTTOM_RIGHT);

      hLayout_SearchTextBtn.setComponentAlignment(searchTextField, Alignment.MIDDLE_LEFT);
      hLayout_SearchTextBtn.setComponentAlignment(searchButton, Alignment.MIDDLE_RIGHT);

      hLayout_Body.setComponentAlignment(oseeLogoImg, Alignment.TOP_CENTER);
      hLayout_Body.setComponentAlignment(vLayout_SearchCrit, Alignment.TOP_CENTER);
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
      if (buildCombo != null) {
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
            lockProgramCombo = true;
            programCombo.setValue(params.getProgram());
            lockProgramCombo = false;
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
      if (sender == searchTextField) {
         return actions;
      }
      return null;
   }

   @Override
   public void handleAction(Action action, Object sender, Object target) {
      if (sender == searchTextField && action == action_enter) {
         selectProgram();
      }
   }

}
