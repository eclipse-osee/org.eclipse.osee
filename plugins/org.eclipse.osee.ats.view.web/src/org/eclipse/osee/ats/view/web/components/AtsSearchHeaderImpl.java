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
import org.eclipse.osee.ats.view.web.search.AtsSearchHomeView;
import org.eclipse.osee.display.api.data.WebId;
import org.eclipse.osee.display.view.web.CssConstants;
import org.eclipse.osee.display.view.web.components.OseeLogoLink;
import org.eclipse.osee.display.view.web.search.OseeSearchHeaderComponent;
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
   private final ComboBox programCombo;
   private final ComboBox buildCombo;
   private final CheckBox nameOnlyCheckBox;
   private final TextField searchTextField;
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
      }
      createLayout();
      populated = true;
   }

   private void selectProgram() {
      if (programCombo != null) {
         WebId program = (WebId) programCombo.getValue();
         searchPresenter.selectProgram(program, this);
      }
   }

   public AtsSearchHeaderImpl(boolean showOseeTitleAbove) {
      this.showOseeTitleAbove = showOseeTitleAbove;

      programCombo = new ComboBox("Program:");
      buildCombo = new ComboBox("Build:");
      nameOnlyCheckBox = new CheckBox("Name Only", false);
      searchTextField = new TextField();

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
         WebId program = (WebId) programCombo.getValue();
         WebId build = (WebId) buildCombo.getValue();
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
      this.removeAllComponents();
      setHeight(null);
      setWidth(100, UNITS_PERCENTAGE);
      setStyleName(CssConstants.OSEE_SEARCH_HEADER_COMPONENT);

      HorizontalLayout hLayoutRow0 = new HorizontalLayout();
      HorizontalLayout hLayoutRow1 = new HorizontalLayout();
      HorizontalLayout hLayoutRow2 = new HorizontalLayout();

      //      Embedded oseeTitleLabel = new Embedded("", new ThemeResource("../osee/osee_large.png"));
      //      oseeTitleLabel.setType(Embedded.TYPE_IMAGE);

      Label spacer1 = new Label("");
      spacer1.setHeight(null);
      spacer1.setWidth(30, UNITS_PIXELS);
      Label spacer2 = new Label("");
      spacer2.setHeight(null);
      spacer2.setWidth(30, UNITS_PIXELS);
      hLayoutRow1.addComponent(programCombo);
      hLayoutRow1.addComponent(spacer1);
      hLayoutRow1.addComponent(buildCombo);
      hLayoutRow1.addComponent(spacer2);
      hLayoutRow1.addComponent(nameOnlyCheckBox);
      hLayoutRow1.setComponentAlignment(programCombo, Alignment.MIDDLE_LEFT);
      hLayoutRow1.setComponentAlignment(buildCombo, Alignment.MIDDLE_CENTER);
      hLayoutRow1.setComponentAlignment(nameOnlyCheckBox, Alignment.BOTTOM_RIGHT);

      searchTextField.setStyleName(CssConstants.OSEE_SEARCH_TEXTFIELD);
      Label spacer3 = new Label("");
      spacer3.setHeight(null);
      spacer3.setWidth(30, UNITS_PIXELS);
      Button searchButton = new Button("Search", new Button.ClickListener() {
         @Override
         public void buttonClick(ClickEvent event) {
            selectSearch();
         }
      });
      hLayoutRow2.addComponent(searchTextField);
      hLayoutRow2.addComponent(spacer3);
      hLayoutRow2.addComponent(searchButton);
      hLayoutRow2.setComponentAlignment(searchTextField, Alignment.MIDDLE_LEFT);
      hLayoutRow2.setComponentAlignment(searchButton, Alignment.MIDDLE_RIGHT);

      if (showOseeTitleAbove) {
         OseeLogoLink oseeTitleLabel =
            new OseeLogoLink(navigator, CssConstants.OSEE_TITLE_LARGE_TEXT, AtsSearchHomeView.class);
         hLayoutRow0.addComponent(oseeTitleLabel);
         hLayoutRow0.setComponentAlignment(oseeTitleLabel, Alignment.MIDDLE_CENTER);
         oseeTitleLabel.setStyleName(CssConstants.OSEE_TITLE_LARGE_TEXT);
         hLayoutRow0.setHeight(oseeTitleLabel.getHeight(), UNITS_PIXELS);

         addComponent(hLayoutRow0);
         addComponent(hLayoutRow1);
         addComponent(hLayoutRow2);

         hLayoutRow1.setMargin(true);

         setComponentAlignment(hLayoutRow0, Alignment.MIDDLE_CENTER);
         setComponentAlignment(hLayoutRow1, Alignment.MIDDLE_CENTER);
         setComponentAlignment(hLayoutRow2, Alignment.MIDDLE_CENTER);
      } else {
         OseeLogoLink oseeTitleLabel =
            new OseeLogoLink(navigator, CssConstants.OSEE_TITLE_MEDIUM_TEXT, AtsSearchHomeView.class);
         Label spacer4 = new Label("");
         spacer4.setWidth(15, UNITS_PIXELS);
         oseeTitleLabel.setHeight(70, UNITS_PIXELS);
         oseeTitleLabel.setWidth(70, UNITS_PIXELS);

         VerticalLayout rightSideVLayout = new VerticalLayout();
         rightSideVLayout.addComponent(hLayoutRow1);
         rightSideVLayout.addComponent(hLayoutRow2);
         rightSideVLayout.setSizeUndefined();

         //TODO: Make the title logo a hyper link to the search home

         hLayoutRow0.addComponent(oseeTitleLabel);
         hLayoutRow0.addComponent(spacer4);
         hLayoutRow0.addComponent(rightSideVLayout);
         hLayoutRow0.setComponentAlignment(oseeTitleLabel, Alignment.TOP_CENTER);
         hLayoutRow0.setComponentAlignment(rightSideVLayout, Alignment.TOP_CENTER);

         Label spacer5 = new Label("");
         spacer5.setHeight(15, UNITS_PIXELS);
         spacer5.setStyleName(CssConstants.OSEE_HORIZONTAL_LINE);

         addComponent(hLayoutRow0);
         addComponent(spacer5);
      }
   }

   @Override
   public void addProgram(WebId program) {
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
   public void addBuild(WebId build) {
      if (buildCombo != null) {
         buildCombo.addItem(build);
      }
   }

   @Override
   public void setSearchCriteria(WebId program, WebId build, boolean nameOnly, String searchPhrase) {
      if (programCombo != null) {
         lockProgramCombo = true;
         programCombo.setValue(program);
         lockProgramCombo = false;
      }
      if (buildCombo != null) {
         buildCombo.setValue(build);
      }
      if (nameOnlyCheckBox != null) {
         nameOnlyCheckBox.setValue(nameOnly);
      }
      if (searchTextField != null) {
         searchTextField.setValue(searchPhrase);
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
         nameOnlyCheckBox.setValue(false);
      }
      if (searchTextField != null) {
         searchTextField.setValue("");
      }
   }

   @Override
   public void setProgram(WebId program) {
      if (programCombo != null) {
         programCombo.setValue(program);
      }
   }

   @Override
   public void setBuild(WebId build) {
      if (buildCombo != null) {
         buildCombo.setValue(build);
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
