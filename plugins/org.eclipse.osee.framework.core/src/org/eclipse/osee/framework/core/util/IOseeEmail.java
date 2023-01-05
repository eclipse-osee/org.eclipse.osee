/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.framework.core.util;

import java.io.File;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

public interface IOseeEmail {

   /**
    * Adds a single address to the recipient list
    *
    * @param addresses - a valid address to send the message TO
    */
   void addRecipients(String addresses) throws MessagingException;

   /**
    * Adds a list of addresses to the recipient list
    *
    * @param addresses - a list of valid addresses to send the message TO
    */
   void addRecipients(String[] addresses) throws MessagingException;

   /**
    * Adds a list of addresses to the corresponding recipient list
    *
    * @param type - specifies which field the address should be put in
    * @param addresses - a list of valid addresses to send the message
    */
   void addRecipients(Message.RecipientType type, String[] addresses) throws MessagingException;

   /**
    * Sets the recipient TO field
    *
    * @param addresses - a valid address to send the message TO
    */
   void setRecipients(String addresses) throws MessagingException;

   /**
    * Sets a list of addresses to the recipient list
    *
    * @param addresses - a list of valid addresses to send the message TO
    */
   void setRecipients(String[] addresses) throws MessagingException;

   /**
    * Sets a list of addresses to the corresponding recipient list
    *
    * @param type - specifies which field the address should be put in
    * @param addresses - a list of valid addresses to send the message
    */
   void setRecipients(Message.RecipientType type, String[] addresses) throws MessagingException;

   /**
    * Sets the from address
    *
    * @param address - the user name the message is from
    */
   // Set all the From Values
   void setFrom(String address) throws AddressException, MessagingException;

   /**
    * Sets the address to reply to (if different than the from addresss)
    *
    * @param address - a valid address to reply to
    */
   void setReplyTo(String address) throws MessagingException;

   /**
    * Gets the current Body Type of the message. NULL if one is not selected yet.
    *
    * @return A String representation of the current Body Type
    */
   String getBodyType();

   /**
    * Sets the text in the body of the message.
    *
    * @param text - the text to for the body of the message
    */
   void setBody(String text);

   /**
    * Adds text to the body if the Body Type is "plain". If the body doesn't exist yet, then calls setBody.
    *
    * @param text - the text to add to the body
    */
   void addBody(String text);

   /**
    * Sets the text in the body of the HTML message. This will already add the &lthtml&gt&ltbody&gt and
    * &lt/body&gt&lt/html&gt tags.
    *
    * @param htmlText - the text for the body of the HTML message
    */
   void setHTMLBody(String htmlText);

   /**
    * Adds text to the HTML body if the Body Type is "html". If the body doesn't exist yet, then calls setHTMLBody.
    *
    * @param htmlText - the text to add to the HTML body
    */
   void addHTMLBody(String htmlText);

   /**
    * Sends the message.
    */
   void send();

   XResultData sendLocalThread();

   /**
    * Adds an attachment to an email
    */
   void addAttachment(DataSource source, String attachmentName) throws MessagingException;

   void addAttachment(File file) throws MessagingException;

   void addAttachment(String contents, String attachmentName) throws MessagingException;

   IOseeEmail create();

}