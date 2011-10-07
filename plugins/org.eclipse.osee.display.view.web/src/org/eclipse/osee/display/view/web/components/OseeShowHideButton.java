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

import java.io.Serializable;
import java.lang.reflect.Method;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeShowHideButton extends HorizontalLayout {

   private final Button plusMinusButton = new Button("-");
   private boolean isStateShow = true;

   public OseeShowHideButton(String caption) {
      final Button showHideButton = new Button(caption);
      showHideButton.setStyleName("link");
      showHideButton.addListener(new Button.ClickListener() {
         @Override
         public void buttonClick(Button.ClickEvent event) {
            toggleStateShow();
         }
      });

      plusMinusButton.setStyleName("link");
      plusMinusButton.addListener(new Button.ClickListener() {

         @Override
         public void buttonClick(Button.ClickEvent event) {
            toggleStateShow();
         }
      });

      this.addComponent(plusMinusButton);
      this.addComponent(showHideButton);

      this.setComponentAlignment(plusMinusButton, Alignment.MIDDLE_LEFT);
      this.setComponentAlignment(showHideButton, Alignment.MIDDLE_RIGHT);
   }

   private void toggleStateShow() {
      setStateShow(!isStateShow);
      fireClick();
   }

   public void setStateShow(boolean isStateShow) {
      this.isStateShow = isStateShow;
      if (isStateShow) {
         plusMinusButton.setCaption("-");
      } else {
         plusMinusButton.setCaption("+");
      }
   }

   public boolean isStateShow() {
      return isStateShow;
   }

   /**
    * Fires a click event to all listeners without any event details.
    */
   protected void fireClick() {
      fireEvent(new OseeShowHideButton.ClickEvent(this));
   }

   private static final Method SHOWHIDE_BUTTON_CLICK_METHOD;

   static {
      try {
         SHOWHIDE_BUTTON_CLICK_METHOD =
            ClickListener.class.getDeclaredMethod("buttonClick", new Class[] {ClickEvent.class});
      } catch (final java.lang.NoSuchMethodException e) {
         // This should never happen
         throw new java.lang.RuntimeException("Internal error finding methods in Button");
      }
   }

   /**
    * Interface for listening for a {@link ClickEvent} fired by a {@link Component}.
    */
   public interface ClickListener extends Serializable {

      /**
       * Called when a {@link Button} has been clicked. A reference to the button is given by
       * {@link ClickEvent#getButton()}.
       *
       * @param event An event containing information about the click.
       */
      public void buttonClick(ClickEvent event);

   }

   /**
    * Adds the button click listener.
    *
    * @param listener the Listener to be added.
    */
   public void addListener(ClickListener listener) {
      addListener(ClickEvent.class, listener, SHOWHIDE_BUTTON_CLICK_METHOD);
   }

   /**
    * Removes the button click listener.
    *
    * @param listener the Listener to be removed.
    */
   public void removeListener(ClickListener listener) {
      removeListener(ClickEvent.class, listener, SHOWHIDE_BUTTON_CLICK_METHOD);
   }

   /**
    * Click event. This event is thrown, when the button is clicked.
    */
   public class ClickEvent extends Component.Event {

      public ClickEvent(Component source) {
         super(source);
      }

   }
}
