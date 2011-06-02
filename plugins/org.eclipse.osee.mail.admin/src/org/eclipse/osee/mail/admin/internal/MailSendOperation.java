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
package org.eclipse.osee.mail.admin.internal;

import java.util.HashSet;
import java.util.Set;
import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.TransportListener;
import javax.mail.internet.MimeMessage;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.mail.MailMessage;
import org.eclipse.osee.mail.SendMailOperation;

/**
 * @author Roberto E. Escobar
 */
public final class MailSendOperation extends AbstractOperation implements SendMailOperation {
   private final MailMessage email;
   private final MailMessageFactory factory;
   private final Set<TransportListener> listeners = new HashSet<TransportListener>();

   public MailSendOperation(String opName, String plugin, MailMessageFactory factory, MailMessage email) {
      super(opName, plugin);
      this.factory = factory;
      this.email = email;
   }

   @Override
   public void addListener(TransportListener listener) {
      if (listener != null) {
         listeners.add(listener);
      }
   }

   @Override
   public void removeListener(TransportListener listener) {
      if (listener != null) {
         listeners.remove(listener);
      }
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
      mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
      mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
      mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
      mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
      mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
      CommandMap.setDefaultCommandMap(mc);

      //ClassLoader original = Thread.currentThread().getContextClassLoader();
      // // Set class loader so can find the mail handlers
      //         Thread.currentThread().setContextClassLoader(
      //            new ExportClassLoader(SkynetGuiPlugin.getInstance().getPackageAdmin()));

      final Session session = factory.createSession();
      final MimeMessage message = factory.createMimeMessage(session, email);
      final Transport transport = factory.createTransport(session);

      for (TransportListener listener : listeners) {
         transport.addTransportListener(listener);
      }
      try {
         message.saveChanges();
         transport.sendMessage(message, message.getAllRecipients());
      } finally {
         for (TransportListener listener : listeners) {
            transport.removeTransportListener(listener);
         }
         transport.close();
         //         Thread.currentThread().setContextClassLoader(original);
      }
   }

}
