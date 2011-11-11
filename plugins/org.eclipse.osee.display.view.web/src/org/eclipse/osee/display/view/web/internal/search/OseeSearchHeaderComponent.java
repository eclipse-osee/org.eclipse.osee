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
package org.eclipse.osee.display.view.web.internal.search;

import org.eclipse.osee.display.api.search.SearchPresenter;
import org.eclipse.osee.display.api.search.SearchView;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeSearchHeaderComponent extends VerticalLayout implements SearchView {

   private boolean populated;
   private final ComboBox programCombo = new ComboBox("Program:");
   private final ComboBox buildCombo = new ComboBox("Build:");
   final CheckBox nameOnlyCheckBox = new CheckBox("Name Only", false);
   final TextField searchTextField = new TextField();
   private final boolean showOseeTitleAbove;

   //   private ProgramsAndBuilds builds;

   public OseeSearchHeaderComponent(boolean showOseeTitleAbove) {
      this.showOseeTitleAbove = showOseeTitleAbove;
      programCombo.setNullSelectionAllowed(false);
      buildCombo.setNullSelectionAllowed(false);
      SearchPresenter webBackend = new OseeWebBackend();
      //      webBackend.getProgramsAndBuilds(this);

      programCombo.addListener(new Property.ValueChangeListener() {
         @Override
         public void valueChange(ValueChangeEvent event) {
            buildCombo.removeAllItems();
            //            Program program = (Program) programCombo.getValue();
            //            Collection<Build> buildList = builds.getBuilds(program);
            //            for (Build build : buildList) {
            //               buildCombo.addItem(build);
            //            }
         }
      });
      programCombo.setImmediate(true);

      searchTextField.setImmediate(true);
   }

   @Override
   public void attach() {
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

      Embedded oseeTitleLabel = new Embedded("", new ThemeResource("../osee/osee_large.png"));
      oseeTitleLabel.setType(Embedded.TYPE_IMAGE);

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
            //            Program program = (Program) programCombo.getValue();
            //            Build build = (Build) buildCombo.getValue();
            String isNameOnly = nameOnlyCheckBox.toString();
            String searchPhrase = (String) searchTextField.getValue();
            //            SearchCriteria searchCriteria = new SearchCriteria(program, build, isNameOnly.equals("true"), searchPhrase);
            //            OseeRoadMapAndNavigation.navigateToSearchResults(searchCriteria);
         }
      });
      hLayoutRow2.addComponent(searchTextField);
      hLayoutRow2.addComponent(spacer3);
      hLayoutRow2.addComponent(searchButton);
      hLayoutRow2.setComponentAlignment(searchTextField, Alignment.MIDDLE_LEFT);
      hLayoutRow2.setComponentAlignment(searchButton, Alignment.MIDDLE_RIGHT);

      if (showOseeTitleAbove) {
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

      //      SearchCriteria searchCriteria = OseeRoadMapAndNavigation.getSearchCriteria();
      //      if (searchCriteria != null) {
      //         programCombo.setValue(searchCriteria.getProgram());
      //         buildCombo.setValue(searchCriteria.getBuild());
      //         nameOnlyCheckBox.setValue(searchCriteria.isNameOnly());
      //         searchTextField.setValue(searchCriteria.getSearchPhrase());
      //      }

      populated = true;
   }

   //   @Override
   //   public void setSearchResults(Collection<SearchResult> searchResults) {
   //      //Do Nothing
   //   }

   //   @Override
   //   public void setProgramsAndBuilds(ProgramsAndBuilds builds) {
   //      programCombo.removeAllItems();
   //      buildCombo.removeAllItems();
   //
   //      if (builds != null) {
   //         this.builds = builds;
   //         for (Program program : this.builds.getPrograms()) {
   //            programCombo.addItem(program);
   //         }
   //      }
   //   }

   //   @Override
   //   public void setArtifact(Artifact artifact) {
   //      //Do nothing
   //   }
   //
   //   @Override
   //   public void setProgram(Program program) {
   //   }
   //
   //   @Override
   //   public void setBuild(Build build) {
   //   }
   //
   //   @Override
   //   public void setErrorMessage(String message) {
   //   }

}
