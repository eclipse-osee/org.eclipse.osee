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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.osee.display.api.EmailView;
import org.eclipse.osee.display.api.components.DisplaysErrorComponent.MsgType;
import org.eclipse.osee.display.view.web.CssConstants;
import com.vaadin.data.Property;
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
public class OseeExceptionDialogComponent extends Window implements EmailView {

   private final Label msgTypeLabel = new Label();
   private final Button closeButton = new Button();
   private final Label shortMsgLabel = new Label();
   private final TextField longMsgTextArea = new TextField();
   private final Button showHideDetailsButton = new Button("Show Details");
   private final Button emailButton = new Button("Email Support");
   private final Button emailSendButton = new Button("Send");
   private final Label emailSubjectLabel = new Label();
   private final TextField emailRecipTextArea = new TextField();
   private final TextField emailBodyTextArea = new TextField();
   private final Label emailStatusLabel = new Label();
   private final Label emailDisplayMsgLabel = new Label();
   private final VerticalLayout vLayout_Email = new VerticalLayout();
   private final Window mainWindow;
   private final int HEIGHT_CLOSED = 100;
   private final int HEIGHT_OPEN = 210;
   private final int WIDTH = 400;
   private final int MARGIN = 15;
   private String fromEmail = "";
   private Collection<String> replyToEmails = new ArrayList<String>();
   private Collection<String> recipientsEmails = new ArrayList<String>();
   private final Collection<SendListener> sendListeners = new ArrayList<SendListener>();
   private final Collection<Validator> validatorListeners = new ArrayList<Validator>();

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
            ComponentUtility.logWarn("OseeExceptionDialogComponent.[ctor] - WARNING: invalid or unhandled msgType.",
               mainWindow);
            break;
      }

      shortMsgLabel.setCaption(shortMsg);
      StringBuilder sb = new StringBuilder();
      sb.append("[");
      sb.append(shortMsg);
      sb.append("]\n");
      sb.append(longMsg);
      longMsgTextArea.setValue(sb.toString());

      if (mainWindow != null) {
         mainWindow.addWindow(this);
         moveToCenter();
         focus();
      } else {
         ComponentUtility.logWarn("OseeExceptionDialogComponent.[ctor] - WARNING: null value detected.", mainWindow);
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
      setHeight(HEIGHT_OPEN, UNITS_PIXELS);
      setCloseShortcut(KeyCode.ESCAPE);
      setScrollable(false);
      setStyleName(CssConstants.OSEE_EXCEPTION);

      VerticalLayout vLayout_body = new VerticalLayout();
      HorizontalLayout hLayout_Row0 = new HorizontalLayout();
      HorizontalLayout hLayout_Row1 = new HorizontalLayout();
      HorizontalLayout hLayout_Row2 = new HorizontalLayout();
      HorizontalLayout hLayout_Row3_Exception = new HorizontalLayout();
      HorizontalLayout hLayout_Row3_Email = new HorizontalLayout();
      HorizontalLayout hLayout_Row4_Email = new HorizontalLayout();
      HorizontalLayout hLayout_Row5_Email = new HorizontalLayout();
      HorizontalLayout hLayout_Row6_Email = new HorizontalLayout();

      Label vSpacer_AboveButtons = new Label();
      vSpacer_AboveButtons.setHeight(10, UNITS_PIXELS);
      Label vSpacer_AboveDetails = new Label();
      vSpacer_AboveDetails.setHeight(10, UNITS_PIXELS);
      Label vSpacer_Email_1 = new Label();
      vSpacer_Email_1.setHeight(10, UNITS_PIXELS);
      Label vSpacer_Email_2 = new Label();
      vSpacer_Email_2.setHeight(10, UNITS_PIXELS);
      Label hSpacer_EmailSubject = new Label();
      hSpacer_EmailSubject.setWidth(5, UNITS_PIXELS);
      Label hSpacer_EmailRecip = new Label();
      hSpacer_EmailRecip.setWidth(5, UNITS_PIXELS);

      closeButton.setStyleName("link");
      closeButton.setIcon(new ThemeResource("../osee/closebutton.png"));
      closeButton.addListener(new Button.ClickListener() {
         @Override
         public void buttonClick(Button.ClickEvent event) {
            mainWindow.removeWindow(OseeExceptionDialogComponent.this);
         }
      });
      msgTypeLabel.setSizeUndefined();
      closeButton.setSizeUndefined();

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
               showHideDetailsButton.setCaption("Hide Details");
               OseeExceptionDialogComponent.this.setHeight(HEIGHT_OPEN, UNITS_PIXELS);
               emailButton.setCaption("Email Support");
               vLayout_Email.setVisible(false);
               emailSendButton.setVisible(false);
            } else {
               showHideDetailsButton.setCaption("Show Details");
               OseeExceptionDialogComponent.this.setHeight(HEIGHT_CLOSED, UNITS_PIXELS);
            }
         }
      });

      Label emailSubjectTitle = new Label("Subject:");
      Label emailRecipTitle = new Label("Recipients:");
      vLayout_Email.setSizeFull();
      vLayout_Email.setVisible(false);
      emailSubjectTitle.setWidth(70, UNITS_PIXELS);
      emailSubjectTitle.setStyleName(CssConstants.OSEE_EMAILDIALOG_TITLES);
      emailRecipTitle.setWidth(70, UNITS_PIXELS);
      emailRecipTitle.setStyleName(CssConstants.OSEE_EMAILDIALOG_TITLES);
      emailSubjectLabel.setSizeFull();
      emailRecipTextArea.setWidth(100, UNITS_PERCENTAGE);
      emailBodyTextArea.setWidth(WIDTH - MARGIN, UNITS_PIXELS);
      emailBodyTextArea.setHeight(HEIGHT_CLOSED, UNITS_PIXELS);
      emailSendButton.setStyleName("link");
      emailButton.setStyleName("link");
      emailButton.addListener(new Button.ClickListener() {
         @Override
         public void buttonClick(Button.ClickEvent event) {
            vLayout_Email.setVisible(!vLayout_Email.isVisible());
            if (vLayout_Email.isVisible()) {
               emailButton.setCaption("Cancel");
               OseeExceptionDialogComponent.this.setHeight(HEIGHT_OPEN + 70, UNITS_PIXELS);
               emailSendButton.setVisible(true);
               showHideDetailsButton.setCaption("Show Details");
               longMsgTextArea.setVisible(false);
            } else {
               emailButton.setCaption("Email Support");
               emailSendButton.setVisible(false);
               OseeExceptionDialogComponent.this.setHeight(HEIGHT_CLOSED, UNITS_PIXELS);
            }
         }
      });
      emailSendButton.addListener(new Button.ClickListener() {
         @Override
         public void buttonClick(Button.ClickEvent event) {
            //TODO: Send email.
         }
      });
      emailStatusLabel.setSizeFull();
      emailDisplayMsgLabel.setSizeFull();

      vLayout_body.setStyleName(CssConstants.OSEE_EXCEPTION);
      vLayout_body.setWidth(WIDTH - MARGIN, UNITS_PIXELS);

      hLayout_Row0.setSizeFull();
      hLayout_Row1.setSizeFull();
      hLayout_Row2.setSizeFull();
      hLayout_Row3_Exception.setSizeFull();
      hLayout_Row3_Email.setSizeFull();
      hLayout_Row4_Email.setSizeFull();
      hLayout_Row5_Email.setSizeFull();
      vLayout_Email.setSizeFull();

      hLayout_Row0.addComponent(msgTypeLabel);
      hLayout_Row0.addComponent(closeButton);
      hLayout_Row1.addComponent(shortMsgLabel);
      hLayout_Row2.addComponent(showHideDetailsButton);
      //      hLayout_Row2.addComponent(emailButton);
      hLayout_Row2.addComponent(emailSendButton);

      hLayout_Row3_Exception.addComponent(longMsgTextArea);

      hLayout_Row3_Email.addComponent(emailSubjectTitle);
      hLayout_Row3_Email.addComponent(hSpacer_EmailSubject);
      hLayout_Row3_Email.addComponent(emailSubjectLabel);
      hLayout_Row4_Email.addComponent(emailRecipTitle);
      hLayout_Row4_Email.addComponent(hSpacer_EmailRecip);
      hLayout_Row4_Email.addComponent(emailRecipTextArea);
      hLayout_Row5_Email.addComponent(emailBodyTextArea);
      hLayout_Row6_Email.addComponent(emailStatusLabel);
      hLayout_Row6_Email.addComponent(emailDisplayMsgLabel);
      vLayout_Email.addComponent(hLayout_Row3_Email);
      vLayout_Email.addComponent(vSpacer_Email_1);
      vLayout_Email.addComponent(hLayout_Row4_Email);
      vLayout_Email.addComponent(vSpacer_Email_2);
      vLayout_Email.addComponent(hLayout_Row5_Email);
      vLayout_Email.addComponent(hLayout_Row6_Email);

      vLayout_body.addComponent(hLayout_Row0);
      vLayout_body.addComponent(hLayout_Row1);
      vLayout_body.addComponent(vSpacer_AboveButtons);
      vLayout_body.addComponent(hLayout_Row2);
      vLayout_body.addComponent(vSpacer_AboveDetails);
      vLayout_body.addComponent(hLayout_Row3_Exception);
      vLayout_body.addComponent(vLayout_Email);
      setContent(vLayout_body);

      hLayout_Row0.setComponentAlignment(closeButton, Alignment.TOP_RIGHT);
      hLayout_Row3_Email.setExpandRatio(emailSubjectLabel, 1.0f);
      hLayout_Row4_Email.setExpandRatio(emailRecipTextArea, 1.0f);
   }

   private void emailListToPropertyValue(Property property, Collection<String> emails) {
      StringBuilder sb = new StringBuilder();
      Iterator<String> iter = emails.iterator();
      while (iter.hasNext()) {
         String email = iter.next();
         sb.append(email);
         if (iter.hasNext()) {
            sb.append(", ");
         }
      }
   }

   @Override
   public void setFrom(String email) {
      fromEmail = email;
   }

   @Override
   public String getFrom() {
      return fromEmail;
   }

   @Override
   public void setReplyTo(Collection<String> emails) {
      replyToEmails.clear();
      replyToEmails.addAll(emails);
   }

   @Override
   public Collection<String> getReplyTos() {
      return replyToEmails;
   }

   @Override
   public void setRecipients(Collection<String> emails) {
      recipientsEmails.clear();
      recipientsEmails.addAll(emails);
      emailListToPropertyValue(emailRecipTextArea, emails);
   }

   @Override
   public Collection<String> getRecipients() {
      return recipientsEmails;
   }

   @Override
   public void setSubject(String subject) {
      emailSubjectLabel.setValue(subject);
   }

   @Override
   public String getSubject() {
      return (String) emailSubjectLabel.getValue();
   }

   @Override
   public void setBody(String body) {
      emailBodyTextArea.setValue(body);
   }

   @Override
   public String getBody() {
      return (String) emailBodyTextArea.getValue();
   }

   @Override
   public void setEmailStatus(EmailSendStatus status) {
      emailStatusLabel.setValue(status);
   }

   @Override
   public void addOnSendListener(SendListener listener) {
      sendListeners.add(listener);
   }

   @Override
   public void addEmailValidator(Validator validator) {
      validatorListeners.add(validator);
   }

   @Override
   public void displayMessage(String caption) {
      emailDisplayMsgLabel.setValue(caption);
   }

   @Override
   public void displayMessage(String caption, String description) {
      emailDisplayMsgLabel.setValue(caption);
      emailDisplayMsgLabel.setDescription(description);//tooltip
   }
}
