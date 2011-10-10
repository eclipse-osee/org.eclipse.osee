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
package org.eclipse.osee.display.view.web.components;

import org.eclipse.osee.display.api.components.RelationComponent;
import org.eclipse.osee.display.api.data.WebArtifact;
import org.eclipse.osee.display.api.data.WebId;
import org.eclipse.osee.display.api.search.SearchNavigator;
import org.eclipse.osee.display.api.search.SearchPresenter;
import org.eclipse.osee.display.view.web.CssConstants;
import org.eclipse.osee.display.view.web.OseeUiApplication;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeRelationsComponent extends VerticalLayout implements RelationComponent {

   private boolean populated = false;
   private final ListSelect relationTypesListSelect = new ListSelect();
   private final ListSelect relationsListSelect = new ListSelect();
   private SearchPresenter searchPresenter = null;
   private SearchNavigator navigator = null;
   private boolean lockRelTypesListener = false;
   private boolean lockRelsListener = false;

   @Override
   public void attach() {
      if (!populated) {
         try {
            OseeUiApplication app = (OseeUiApplication) this.getApplication();
            searchPresenter = app.getSearchPresenter();
            navigator = app.getNavigator();
         } catch (Exception e) {
            System.out.println("OseeRelationsComponent.attach - CRITICAL ERROR: (OseeUiApplication) this.getApplication() threw an exception.");
         }
      }
      populated = true;

      createLayout();
   }

   private void createLayout() {
      removeAllComponents();

      final HorizontalLayout listBoxesLayout = new HorizontalLayout();

      //      final OseeShowHideButton showHideButton = new OseeShowHideButton("Relations");
      //      showHideButton.addListener(new OseeShowHideButton.ClickListener() {
      //
      //         @Override
      //         public void buttonClick(OseeShowHideButton.ClickEvent event) {
      //            listBoxesLayout.setVisible(showHideButton.isStateShow());
      //         }
      //      });
      //      //Initialize listBoxesLayout visibility
      //      listBoxesLayout.setVisible(showHideButton.isStateShow());

      Label titleLabel = new Label("Relationships");
      titleLabel.setStyleName(CssConstants.OSEE_ATTRIBUTESTITLELABEL);

      relationTypesListSelect.setNullSelectionAllowed(false);
      relationTypesListSelect.setImmediate(true);

      relationsListSelect.setNullSelectionAllowed(false);
      relationsListSelect.setImmediate(true);

      relationTypesListSelect.addListener(new Property.ValueChangeListener() {
         @Override
         public void valueChange(ValueChangeEvent event) {
            if (!lockRelTypesListener) {
               try {
                  WebId relationType = (WebId) relationTypesListSelect.getValue();
                  if (relationType != null) {
                     searchPresenter.selectRelationType(relationType, OseeRelationsComponent.this);
                  }
               } catch (Exception e) {
                  System.out.println("OseeRelationsComponent.createLayout - CRITICAL ERROR: (WebArtifact) relationsListSelect.getValue() threw an exception.");
               }
            }
         }
      });

      relationsListSelect.addListener(new Property.ValueChangeListener() {
         @Override
         public void valueChange(ValueChangeEvent event) {
            if (!lockRelsListener) {
               try {
                  WebArtifact artifact = (WebArtifact) relationsListSelect.getValue();
                  if (artifact != null) {
                     searchPresenter.selectArtifact(artifact, navigator);
                  }
               } catch (Exception e) {
                  System.out.println("OseeRelationsComponent.createLayout - CRITICAL ERROR: (WebArtifact) relationsListSelect.getValue() threw an exception.");
               }
            }
         }
      });

      Label spacer = new Label();
      spacer.setWidth(15, UNITS_PIXELS);
      Label spacer2 = new Label();
      spacer2.setWidth(15, UNITS_PIXELS);
      listBoxesLayout.addComponent(spacer);
      listBoxesLayout.addComponent(relationTypesListSelect);
      listBoxesLayout.addComponent(spacer2);
      listBoxesLayout.addComponent(relationsListSelect);

      addComponent(titleLabel);
      //      addComponent(showHideButton);
      addComponent(listBoxesLayout);
      setExpandRatio(listBoxesLayout, 1.0f);

      //Fixed width lists make for a prettier layout
      relationTypesListSelect.setWidth(200, UNITS_PIXELS);
      relationsListSelect.setWidth(200, UNITS_PIXELS);
   }

   public OseeRelationsComponent() {
      createLayout();
   }

   @Override
   public void clearAll() {
      if (relationTypesListSelect != null) {
         lockRelTypesListener = true;
         relationTypesListSelect.removeAllItems();
         lockRelTypesListener = false;
      }
      if (relationsListSelect != null) {
         lockRelsListener = true;
         relationsListSelect.removeAllItems();
         lockRelsListener = false;
      }
   }

   @Override
   public void addRelationType(WebId id) {
      if (relationTypesListSelect != null) {
         lockRelTypesListener = true;
         relationTypesListSelect.addItem(id);
         lockRelTypesListener = false;
      }
   }

   @Override
   public void clearRelations() {
      if (relationsListSelect != null) {
         lockRelsListener = true;
         relationsListSelect.removeAllItems();
         lockRelsListener = false;
      }
   }

   @Override
   public void addRelation(WebArtifact id) {
      if (relationsListSelect != null) {
         lockRelsListener = true;
         relationsListSelect.addItem(id);
         lockRelsListener = false;
      }
   }

   @Override
   public void setErrorMessage(String message) {
      //TODO: 
   }
}
