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
import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeRelationsComponent extends VerticalLayout implements RelationComponent {

   private boolean populated = false;
   private final ListSelect relationTypesListSelect = new ListSelect();
   private final ListSelect relationsListSelect = new ListSelect();
   private final ListSelect sideBSelect = new ListSelect();
   private SearchPresenter<?> searchPresenter = null;
   private SearchNavigator navigator = null;
   private boolean lockRelTypesListener = false;
   private boolean lockRelsListener = false;
   private WebArtifact artifact = null;
   private final int LISTBOX_MINWIDTH = 100;

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
                     searchPresenter.selectRelationType(artifact, relationType, OseeRelationsComponent.this);
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

      Label rightArrow = new Label();
      rightArrow.setStyleName(CssConstants.OSEE_RIGHTARROW);

      listBoxesLayout.addComponent(spacer);
      listBoxesLayout.addComponent(relationTypesListSelect);
      listBoxesLayout.addComponent(rightArrow);
      listBoxesLayout.setComponentAlignment(rightArrow, Alignment.MIDDLE_CENTER);
      listBoxesLayout.addComponent(relationsListSelect);
      listBoxesLayout.addComponent(sideBSelect);

      addComponent(titleLabel);
      addComponent(listBoxesLayout);
      setExpandRatio(listBoxesLayout, 1.0f);

      //Fixed width lists make for a prettier layout
      relationTypesListSelect.setWidth(LISTBOX_MINWIDTH, UNITS_PIXELS);
      relationsListSelect.setWidth(LISTBOX_MINWIDTH, UNITS_PIXELS);
      sideBSelect.setWidth(LISTBOX_MINWIDTH, UNITS_PIXELS);
   }

   public OseeRelationsComponent() {
      createLayout();
   }

   @Override
   public void clearAll() {
      if (relationTypesListSelect != null) {
         lockRelTypesListener = true;
         relationTypesListSelect.removeAllItems();
         relationTypesListSelect.setWidth(LISTBOX_MINWIDTH, UNITS_PIXELS);
         lockRelTypesListener = false;
      }
      if (relationsListSelect != null) {
         lockRelsListener = true;
         relationsListSelect.removeAllItems();
         relationsListSelect.setWidth(LISTBOX_MINWIDTH, UNITS_PIXELS);
         lockRelsListener = false;
      }
   }

   @Override
   public void addRelationType(WebId id) {
      if (relationTypesListSelect != null) {
         lockRelTypesListener = true;
         relationTypesListSelect.addItem(id);
         relationTypesListSelect.setWidth(null);
         lockRelTypesListener = false;
      }
   }

   @Override
   public void clearRelations() {
      if (relationsListSelect != null) {
         lockRelsListener = true;
         relationsListSelect.removeAllItems();
         relationsListSelect.setWidth(LISTBOX_MINWIDTH, UNITS_PIXELS);
         lockRelsListener = false;
      }
   }

   public void addRelation(WebArtifact id) {
      if (relationsListSelect != null) {
         lockRelsListener = true;
         relationsListSelect.addItem(id);
         relationsListSelect.setWidth(null);
         lockRelsListener = false;
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
            System.out.println("OseeRelationsComponent.setErrorMessage - ERROR: Application.getMainWindow() returns null value.");
         }
      } else {
         System.out.println("OseeRelationsComponent.setErrorMessage - ERROR: getApplication() returns null value.");
      }
   }

   @Override
   public void setArtifact(WebArtifact artifact) {
      this.artifact = artifact;
   }

   @Override
   public void addLeftRelated(WebArtifact id) {
   }

   @Override
   public void addRightRelated(WebArtifact id) {
   }

   @Override
   public void setLeftName(String name) {
   }

   @Override
   public void setRightName(String name) {
   }
}
