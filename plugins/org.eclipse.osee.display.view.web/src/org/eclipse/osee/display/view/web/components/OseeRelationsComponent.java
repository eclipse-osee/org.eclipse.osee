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
import org.eclipse.osee.display.view.web.OseeAppData;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeRelationsComponent extends VerticalLayout implements RelationComponent {

   private final ListSelect relationTypesListSelect = new ListSelect("Relation Types:");
   private final ListSelect relationsListSelect = new ListSelect("Relations:");
   SearchPresenter searchPresenter = OseeAppData.getSearchPresenter();
   SearchNavigator navigator = OseeAppData.getNavigator();
   private boolean lockRelTypesListener = false;
   private boolean lockRelsListener = false;

   private void createLayout() {
      removeAllComponents();

      final HorizontalLayout listBoxesLayout = new HorizontalLayout();
      final Button showHideButton = new Button("- Relations");
      showHideButton.addListener(new Button.ClickListener() {
         @Override
         public void buttonClick(ClickEvent event) {
            listBoxesLayout.setVisible(!listBoxesLayout.isVisible());
            if (listBoxesLayout.isVisible()) {
               showHideButton.setCaption("- Relations");
            } else {
               showHideButton.setCaption("+ Relations");
            }
         }
      });

      relationTypesListSelect.setNullSelectionAllowed(false);
      relationTypesListSelect.setImmediate(true);
      Label spacer = new Label();
      spacer.setWidth(15, UNITS_PIXELS);

      relationsListSelect.setNullSelectionAllowed(false);
      relationsListSelect.setImmediate(true);

      relationTypesListSelect.addListener(new Property.ValueChangeListener() {
         @Override
         public void valueChange(ValueChangeEvent event) {
            if (!lockRelTypesListener) {
               WebId relationType = (WebId) relationTypesListSelect.getValue();
               searchPresenter.selectRelationType(relationType, OseeRelationsComponent.this);
            }
         }
      });

      relationsListSelect.addListener(new Property.ValueChangeListener() {
         @Override
         public void valueChange(ValueChangeEvent event) {
            if (!lockRelsListener) {
               WebArtifact artifact = (WebArtifact) relationsListSelect.getValue();
               searchPresenter.selectArtifact(artifact, navigator);
            }
         }
      });

      listBoxesLayout.addComponent(relationTypesListSelect);
      listBoxesLayout.addComponent(spacer);
      listBoxesLayout.addComponent(relationsListSelect);

      addComponent(showHideButton);
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
}
