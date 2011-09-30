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
package org.eclipse.osee.ats.view.web.components;

import org.eclipse.osee.ats.api.components.AtsSearchHeaderComponentInterface;
import org.eclipse.osee.ats.api.search.AtsWebSearchPresenter;
import org.eclipse.osee.ats.view.web.AtsAppData;
import org.eclipse.osee.ats.view.web.AtsNavigator;
import org.eclipse.osee.ats.view.web.search.AtsSearchHomeView;
import org.eclipse.osee.display.api.data.WebId;
import org.eclipse.osee.display.view.web.CssConstants;
import org.eclipse.osee.display.view.web.components.OseeLogoLink;
import org.eclipse.osee.display.view.web.search.OseeSearchHeaderComponent;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class AtsSearchHeaderComponent extends OseeSearchHeaderComponent implements AtsSearchHeaderComponentInterface {

   private boolean populated;
   private final ComboBox programCombo = new ComboBox("Program:");
   private final ComboBox buildCombo = new ComboBox("Build:");
   private final CheckBox nameOnlyCheckBox = new CheckBox("Name Only", false);
   private final TextField searchTextField = new TextField();
   private final boolean showOseeTitleAbove;
   private final AtsWebSearchPresenter atsBackend = AtsAppData.getAtsWebSearchPresenter();
   private final AtsNavigator atsNavigator = AtsAppData.getAtsNavigator();

   public AtsSearchHeaderComponent(boolean showOseeTitleAbove) {
      this.showOseeTitleAbove = showOseeTitleAbove;
      if (programCombo != null) {
         programCombo.setNullSelectionAllowed(false);
         final AtsSearchHeaderComponentInterface me = this;
         programCombo.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
               WebId program = (WebId) programCombo.getValue();
               atsBackend.selectProgram(program, me);
            }
         });
         programCombo.setImmediate(true);
      }
      if (buildCombo != null) {
         buildCombo.setNullSelectionAllowed(false);
      }

      searchTextField.setImmediate(true);
   }

   @Override
   protected void createLayout() {
      if (populated) {
         // Only populate the layout once
         return;
      }

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
            WebId program = (WebId) programCombo.getValue();
            WebId build = (WebId) buildCombo.getValue();
            boolean nameOnly = nameOnlyCheckBox.toString().equalsIgnoreCase("true");
            String searchPhrase = (String) searchTextField.getValue();
            atsBackend.selectSearch(program, build, nameOnly, searchPhrase, atsNavigator);
         }
      });
      hLayoutRow2.addComponent(searchTextField);
      hLayoutRow2.addComponent(spacer3);
      hLayoutRow2.addComponent(searchButton);
      hLayoutRow2.setComponentAlignment(searchTextField, Alignment.MIDDLE_LEFT);
      hLayoutRow2.setComponentAlignment(searchButton, Alignment.MIDDLE_RIGHT);

      if (showOseeTitleAbove) {
         OseeLogoLink oseeTitleLabel =
            new OseeLogoLink(atsNavigator, CssConstants.OSEE_TITLE_LARGE_TEXT, AtsSearchHomeView.class);
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
            new OseeLogoLink(atsNavigator, CssConstants.OSEE_TITLE_MEDIUM_TEXT, AtsSearchHomeView.class);
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

      populated = true;
   }

   @Override
   public void addProgram(WebId program) {
      if (programCombo != null) {
         programCombo.addItem(program);
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
         programCombo.setValue(program);
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
   }

}
