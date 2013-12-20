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
import org.eclipse.osee.display.api.data.ViewArtifact;
import org.eclipse.osee.display.api.data.ViewId;
import org.eclipse.osee.display.api.search.SearchNavigator;
import org.eclipse.osee.display.api.search.SearchPresenter;
import org.eclipse.osee.display.view.web.CssConstants;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeRelationsComponent extends VerticalLayout implements RelationComponent {

   private final ListSelect relTypesSelect = new ListSelect();
   private final ListSelect leftSelect = new ListSelect();
   private final ListSelect rightSelect = new ListSelect();
   private boolean lockRelTypesListener = false;
   private boolean lockRelsListener = false;
   private ViewArtifact artifact = null;
   private final int LISTBOX_MINWIDTH = 100;
   private final Label leftTitle = new Label("");
   private final Label relTypesTitle = new Label("Relation Type");
   private final Label rightTitle = new Label("");

   @Override
   public void attach() {
      createLayout();
   }

   private void createLayout() {
      setSizeUndefined();
      removeAllComponents();

      final HorizontalLayout listBoxesLayout = new HorizontalLayout();

      Label titleLabel = new Label("Relations");
      titleLabel.setStyleName(CssConstants.OSEE_ATTRIBUTESTITLELABEL);

      leftTitle.setStyleName(CssConstants.OSEE_ATTRIBUTELABEL);
      relTypesTitle.setStyleName(CssConstants.OSEE_ATTRIBUTELABEL);
      rightTitle.setStyleName(CssConstants.OSEE_ATTRIBUTELABEL);

      //Fixed width lists make for a prettier layout
      relTypesSelect.setWidth(LISTBOX_MINWIDTH, UNITS_PIXELS);
      leftSelect.setWidth(LISTBOX_MINWIDTH, UNITS_PIXELS);
      rightSelect.setWidth(LISTBOX_MINWIDTH, UNITS_PIXELS);

      relTypesSelect.setNullSelectionAllowed(false);
      relTypesSelect.setImmediate(true);

      leftSelect.setNullSelectionAllowed(false);
      leftSelect.setImmediate(true);

      rightSelect.setNullSelectionAllowed(false);
      rightSelect.setImmediate(true);

      relTypesSelect.addListener(new Property.ValueChangeListener() {
         @Override
         public void valueChange(ValueChangeEvent event) {
            if (!lockRelTypesListener) {
               try {
                  ViewId relationType = (ViewId) relTypesSelect.getValue();
                  if (relationType != null) {
                     SearchPresenter<?, ?> presenter = ComponentUtility.getPresenter(OseeRelationsComponent.this);
                     presenter.selectRelationType(artifact, relationType, OseeRelationsComponent.this);
                  }
               } catch (Exception e) {
                  ComponentUtility.logError(
                     "OseeRelationsComponent.createLayout - CRITICAL ERROR: (WebArtifact) relationsListSelect.getValue() threw an exception:" + e.getMessage() + e.getStackTrace(),
                     OseeRelationsComponent.this);
               }
            }
         }
      });

      leftSelect.addListener(new Property.ValueChangeListener() {
         @Override
         public void valueChange(ValueChangeEvent event) {
            if (!lockRelsListener) {
               try {
                  ViewArtifact artifact = (ViewArtifact) leftSelect.getValue();
                  selectArtifact(artifact);
               } catch (Exception e) {
                  ComponentUtility.logError(
                     "OseeRelationsComponent.createLayout - CRITICAL ERROR: (WebArtifact) relationsListSelect.getValue() threw an exception.",
                     OseeRelationsComponent.this);
               }
            }
         }
      });

      rightSelect.addListener(new Property.ValueChangeListener() {
         @Override
         public void valueChange(ValueChangeEvent event) {
            if (!lockRelsListener) {
               try {
                  ViewArtifact artifact = (ViewArtifact) rightSelect.getValue();
                  selectArtifact(artifact);
               } catch (Exception e) {
                  ComponentUtility.logError(
                     "OseeRelationsComponent.createLayout - CRITICAL ERROR: (WebArtifact) relationsListSelect.getValue() threw an exception.",
                     OseeRelationsComponent.this);
               }
            }
         }
      });

      VerticalLayout vLayout_LeftSelect = new VerticalLayout();
      VerticalLayout vLayout_RelTypesSelect = new VerticalLayout();
      VerticalLayout vLayout_RightSelect = new VerticalLayout();

      vLayout_LeftSelect.addComponent(leftTitle);
      vLayout_LeftSelect.addComponent(leftSelect);

      vLayout_RelTypesSelect.addComponent(relTypesTitle);
      vLayout_RelTypesSelect.addComponent(relTypesSelect);

      vLayout_RightSelect.addComponent(rightTitle);
      vLayout_RightSelect.addComponent(rightSelect);

      Label spacer = new Label();
      spacer.setWidth(15, UNITS_PIXELS);

      Label leftArrow = new Label();
      leftArrow.setStyleName(CssConstants.OSEE_LEFTARROW);

      Label rightArrow = new Label();
      rightArrow.setStyleName(CssConstants.OSEE_RIGHTARROW);

      listBoxesLayout.addComponent(spacer);
      listBoxesLayout.addComponent(vLayout_LeftSelect);
      listBoxesLayout.addComponent(leftArrow);
      listBoxesLayout.addComponent(vLayout_RelTypesSelect);
      listBoxesLayout.addComponent(rightArrow);
      listBoxesLayout.addComponent(vLayout_RightSelect);

      addComponent(titleLabel);
      addComponent(listBoxesLayout);

      vLayout_LeftSelect.setComponentAlignment(leftTitle, Alignment.BOTTOM_CENTER);
      vLayout_LeftSelect.setComponentAlignment(leftSelect, Alignment.BOTTOM_CENTER);
      vLayout_RelTypesSelect.setComponentAlignment(relTypesSelect, Alignment.BOTTOM_CENTER);
      vLayout_RelTypesSelect.setComponentAlignment(relTypesTitle, Alignment.MIDDLE_CENTER);
      vLayout_RightSelect.setComponentAlignment(rightTitle, Alignment.BOTTOM_CENTER);
      vLayout_RightSelect.setComponentAlignment(rightSelect, Alignment.BOTTOM_CENTER);
      listBoxesLayout.setComponentAlignment(vLayout_LeftSelect, Alignment.BOTTOM_CENTER);
      listBoxesLayout.setComponentAlignment(vLayout_RelTypesSelect, Alignment.BOTTOM_CENTER);
      listBoxesLayout.setComponentAlignment(vLayout_RightSelect, Alignment.BOTTOM_CENTER);
      listBoxesLayout.setComponentAlignment(rightArrow, Alignment.MIDDLE_CENTER);
      listBoxesLayout.setComponentAlignment(leftArrow, Alignment.MIDDLE_CENTER);
      setExpandRatio(listBoxesLayout, 1.0f);
   }

   private void selectArtifact(ViewArtifact artifact) {
      if (artifact != null) {
         String url = ComponentUtility.getUrl(OseeRelationsComponent.this);
         SearchPresenter<?, ?> presenter = ComponentUtility.getPresenter(OseeRelationsComponent.this);
         SearchNavigator navigator = ComponentUtility.getNavigator(OseeRelationsComponent.this);
         presenter.selectArtifact(url, artifact, navigator);
      } else {
         ComponentUtility.logWarn("OseeRelationsComponent.handleValue - WARNING: null value detected.", this);
      }
   }

   public OseeRelationsComponent() {
      createLayout();
   }

   @Override
   public void clearAll() {
      this.addRelationType(null);
      this.clearRelations();
   }

   @Override
   public void addRelationType(ViewId id) {
      if (id == null) {
         relTypesSelect.removeAllItems();
         relTypesSelect.setWidth(LISTBOX_MINWIDTH, UNITS_PIXELS);
      } else {
         relTypesSelect.setEnabled(true);
         if (relTypesSelect != null) {
            lockRelTypesListener = true;
            relTypesSelect.addItem(id);
            relTypesSelect.setWidth(null);
            lockRelTypesListener = false;
         }
      }
   }

   @Override
   public void clearRelations() {
      this.addLeftRelated(null);
      this.addRightRelated(null);
      this.setLeftName(null);
      this.setRightName(null);
   }

   @Override
   public void setErrorMessage(String shortMsg, String longMsg, MsgType msgType) {
      // do nothing
   }

   @Override
   public void setArtifact(ViewArtifact artifact) {
      this.artifact = artifact;
   }

   @Override
   public void addLeftRelated(ViewArtifact id) {
      if (id == null) {
         leftSelect.removeAllItems();
         leftSelect.setWidth(LISTBOX_MINWIDTH, UNITS_PIXELS);
      } else {
         leftSelect.setEnabled(true);
         if (leftSelect != null) {
            lockRelsListener = true;
            leftSelect.addItem(id);
            leftSelect.setWidth(null);
            lockRelsListener = false;
         }
      }
   }

   @Override
   public void addRightRelated(ViewArtifact id) {
      if (id == null) {
         rightSelect.removeAllItems();
         rightSelect.setWidth(LISTBOX_MINWIDTH, UNITS_PIXELS);
      } else {
         rightSelect.setEnabled(true);
         if (rightSelect != null) {
            lockRelsListener = true;
            rightSelect.addItem(id);
            rightSelect.setWidth(null);
            lockRelsListener = false;
         }
      }
   }

   @Override
   public void setLeftName(String name) {
      if (name == null) {
         leftTitle.setValue("");
      } else {
         leftTitle.setValue(name);
      }
   }

   @Override
   public void setRightName(String name) {
      if (name == null) {
         rightTitle.setValue("");
      } else {
         rightTitle.setValue(name);
      }
   }
}
