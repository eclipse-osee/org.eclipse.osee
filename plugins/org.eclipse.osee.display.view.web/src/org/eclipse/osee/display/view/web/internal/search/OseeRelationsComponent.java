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

import org.eclipse.osee.display.api.data.WebArtifact;
import org.eclipse.osee.display.api.search.SearchPresenter;
import org.eclipse.osee.vaadin.widgets.Navigator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeRelationsComponent extends VerticalLayout {
   private final Navigator navigator;
   private final SearchPresenter webBackend;
   private WebArtifact artifact;

   private void initLayout() {
      if (artifact != null) {
         this.removeAllComponents();

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

         //         final ListSelect relationTypesListSelect = new ListSelect("Relation Types:", artifact.getRelationTypes());
         //         relationTypesListSelect.setNullSelectionAllowed(false);
         //         relationTypesListSelect.setImmediate(true);
         //         Label spacer = new Label();
         //         spacer.setWidth(15, UNITS_PIXELS);
         //         final ListSelect relationsListSelect = new ListSelect("Relations:", new ArrayList<String>());
         //         relationsListSelect.setNullSelectionAllowed(false);
         //         relationsListSelect.setImmediate(true);
         //
         //         relationTypesListSelect.addListener(new Property.ValueChangeListener() {
         //            @Override
         //            public void valueChange(ValueChangeEvent event) {
         //               RelationType relationType = (RelationType) relationTypesListSelect.getValue();
         //               Collection<Artifact> relations = artifact.getRelationsWithRelationType(relationType);
         //               relationsListSelect.removeAllItems();
         //               for (Artifact artifact : relations) {
         //                  relationsListSelect.addItem(artifact);
         //               }
         //            }
         //         });
         //
         //         listBoxesLayout.addComponent(relationTypesListSelect);
         //         listBoxesLayout.addComponent(spacer);
         //         listBoxesLayout.addComponent(relationsListSelect);

         addComponent(showHideButton);
         addComponent(listBoxesLayout);
         setExpandRatio(listBoxesLayout, 1.0f);
      }
   }

   public OseeRelationsComponent(Navigator navigator, SearchPresenter webBackend, WebArtifact artifact) {
      this.navigator = navigator;
      this.webBackend = webBackend;
      this.artifact = artifact;

      initLayout();
   }

   public void setArtifact(WebArtifact artifact) {
      this.artifact = artifact;
      initLayout();
   }
}
