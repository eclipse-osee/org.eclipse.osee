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

import org.eclipse.osee.display.api.components.DisplaysErrorComponent.MsgType;
import org.eclipse.osee.display.view.web.CssConstants;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeExceptionDialogComponent extends Window {

   private final Label msgTypeLabel = new Label();
   private final Label shortMsgLabel = new Label();
   private final TextField longMsgTextArea = new TextField();
   private final Button showHideDetailsButton = new Button("Show Details");
   private final Button closeButton = new Button();
   private final Window mainWindow;
   private final int HEIGHT_CLOSED = 100;
   private final int HEIGHT_OPEN = 210;
   private final int WIDTH = 400;
   private final int MARGIN = 15;

   public OseeExceptionDialogComponent(MsgType msgType, String shortMsg, String longMsg, Window mainWindow) {
      this.mainWindow = mainWindow;
      createLayout();

      switch (msgType) {
         case MSGTYPE_ERROR:
            msgTypeLabel.setCaption("Error!");
            msgTypeLabel.setStyleName(CssConstants.OSEE_EXCEPTION_ERROR_TITLE_TEXT);
            break;
         case MSGTYPE_WARNING:
            msgTypeLabel.setCaption("Warning:");
            msgTypeLabel.setStyleName(CssConstants.OSEE_EXCEPTION_WARNING_TITLE_TEXT);
            break;
         default:
            break;
      }

      shortMsgLabel.setCaption(shortMsg);
      longMsgTextArea.setValue(longMsg);

      if (mainWindow != null) {
         mainWindow.addWindow(this);
         moveToCenter();
         focus();
      }
   }

   private void moveToCenter() {
      float width = getApplication().getMainWindow().getWidth();
      float height = getApplication().getMainWindow().getHeight();

      float centerx = width / 2.0f;
      float centery = height / 2.0f;

      float halfwidth = this.getWidth() / 2.0f;
      float halfheight = this.getHeight() / 2.0f;

      float x = centerx - halfwidth;
      float y = centery - halfheight;

      setPositionX((int) x);
      setPositionY((int) y);
   }

   private void createLayout() {
      setWidth(WIDTH, UNITS_PIXELS);
      setHeight(HEIGHT_CLOSED, UNITS_PIXELS);
      setCloseShortcut(KeyCode.ESCAPE);
      setScrollable(false);
      setStyleName(CssConstants.OSEE_EXCEPTION);

      shortMsgLabel.setWidth(null);

      longMsgTextArea.setWidth(WIDTH - MARGIN, UNITS_PIXELS);
      longMsgTextArea.setHeight(HEIGHT_CLOSED, UNITS_PIXELS);
      longMsgTextArea.setVisible(false);
      longMsgTextArea.setStyleName(CssConstants.OSEE_EXCEPTION_LONGMSG);
      showHideDetailsButton.setStyleName("link");
      showHideDetailsButton.addListener(new Button.ClickListener() {
         @Override
         public void buttonClick(Button.ClickEvent event) {
            longMsgTextArea.setVisible(!longMsgTextArea.isVisible());
            if (longMsgTextArea.isVisible()) {
               OseeExceptionDialogComponent.this.setHeight(HEIGHT_OPEN, UNITS_PIXELS);
            } else {
               OseeExceptionDialogComponent.this.setHeight(HEIGHT_CLOSED, UNITS_PIXELS);
            }
         }
      });

      closeButton.setStyleName("link");
      closeButton.setIcon(new ThemeResource("../osee/closebutton.png"));
      closeButton.addListener(new Button.ClickListener() {
         @Override
         public void buttonClick(Button.ClickEvent event) {
            mainWindow.removeWindow(OseeExceptionDialogComponent.this);
         }
      });

      VerticalLayout vLayout_body = new VerticalLayout();
      HorizontalLayout row0 = new HorizontalLayout();
      HorizontalLayout row1 = new HorizontalLayout();
      HorizontalLayout row2 = new HorizontalLayout();
      HorizontalLayout row3 = new HorizontalLayout();

      vLayout_body.setStyleName(CssConstants.OSEE_EXCEPTION);

      row0.setSizeFull();
      row1.setSizeFull();
      row2.setSizeFull();
      row3.setSizeFull();
      vLayout_body.setWidth(WIDTH - MARGIN, UNITS_PIXELS);
      msgTypeLabel.setSizeUndefined();
      closeButton.setSizeUndefined();

      Label vSpacer_AboveDetails = new Label();
      vSpacer_AboveDetails.setHeight(20, UNITS_PIXELS);

      row0.addComponent(msgTypeLabel);
      row0.addComponent(closeButton);
      row1.addComponent(shortMsgLabel);
      row2.addComponent(longMsgTextArea);
      row3.addComponent(showHideDetailsButton);
      vLayout_body.addComponent(row0);
      vLayout_body.addComponent(row1);
      vLayout_body.addComponent(row2);
      vLayout_body.addComponent(vSpacer_AboveDetails);
      vLayout_body.addComponent(row3);
      setContent(vLayout_body);

      row0.setComponentAlignment(closeButton, Alignment.TOP_RIGHT);
      row3.setComponentAlignment(showHideDetailsButton, Alignment.BOTTOM_CENTER);
   }
}
