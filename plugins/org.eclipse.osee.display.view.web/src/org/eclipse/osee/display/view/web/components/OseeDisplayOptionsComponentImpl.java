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

import org.eclipse.osee.display.api.components.DisplayOptionsComponent;
import org.eclipse.osee.display.api.data.DisplayOptions;
import org.eclipse.osee.display.api.search.SearchNavigator;
import org.eclipse.osee.display.api.search.SearchPresenter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeDisplayOptionsComponentImpl extends VerticalLayout implements DisplayOptionsComponent {

   private final CheckBox showVerboseCheckBox = new CheckBox("Show Detailed Results", false);
   private boolean isLayoutComplete = false;
   private boolean lockOptions = false;

   @Override
   public void attach() {
      if (!isLayoutComplete) {
         createLayout();
         isLayoutComplete = true;
      }
   }

   private void createLayout() {
      setSizeFull();

      showVerboseCheckBox.setImmediate(true);
      showVerboseCheckBox.addListener(new Property.ValueChangeListener() {
         @Override
         public void valueChange(ValueChangeEvent event) {
            if (!lockOptions) {
               lockOptions = true;
               boolean showVerbose = showVerboseCheckBox.toString().equalsIgnoreCase("true");
               onBoxChecked(showVerbose);
               lockOptions = false;
            }
         }
      });

      addComponent(showVerboseCheckBox);
   }

   @Override
   public void clearAll() {
      showVerboseCheckBox.setValue(false);
   }

   @Override
   public void setDisplayOptions(DisplayOptions options) {
      if (options != null) {
         lockOptions = true;
         boolean verboseResults = options.getVerboseResults();
         showVerboseCheckBox.setValue(verboseResults);
         lockOptions = false;
      }
   }

   private void onBoxChecked(boolean isShowVerbose) {
      DisplayOptions options = new DisplayOptions(isShowVerbose);

      String url = ComponentUtility.getUrl(OseeDisplayOptionsComponentImpl.this);
      SearchNavigator navigator = ComponentUtility.getNavigator(OseeDisplayOptionsComponentImpl.this);
      SearchPresenter presenter = ComponentUtility.getPresenter(OseeDisplayOptionsComponentImpl.this);
      if (presenter != null) {
         presenter.selectDisplayOptions(url, options, navigator);
      } else {
         System.out.println("Presenter was null");
      }
   }
}
