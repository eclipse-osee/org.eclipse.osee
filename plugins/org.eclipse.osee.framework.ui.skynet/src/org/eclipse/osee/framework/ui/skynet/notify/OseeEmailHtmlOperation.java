/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.notify;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.notify.OseeEmail.BodyType;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * Generate an email from the html created from a resultData.
 * 
 * @author Donald G. Dunne
 */
public class OseeEmailHtmlOperation extends AbstractOperation {

   private final Collection<String> toAddresses;
   private final String fromAddress;
   private final String subject;
   private final String replyToAddress;
   private final XResultData resultData;

   /**
    * Constructor provided to email when job is completed. Providing toFromAddress will set the to, from and replyTo
    * addresses as the same.
    */
   public OseeEmailHtmlOperation(String toFromAddress, String subject, XResultData resultData) {
      this(Collections.singleton(toFromAddress), toFromAddress, toFromAddress, subject, resultData);
   }

   public OseeEmailHtmlOperation(Collection<String> toAddresses, String fromAddress, String replyToAddress, String subject, XResultData resultData) {
      super(subject, Activator.PLUGIN_ID);
      this.toAddresses = toAddresses;
      this.fromAddress = fromAddress;
      this.replyToAddress = replyToAddress;
      this.subject = subject;
      this.resultData = resultData;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      String htmlBody = XResultDataUI.getReport(resultData, subject).getManipulatedHtml();
      OseeEmail emailMessage =
         new OseeEmail(toAddresses, fromAddress, replyToAddress, subject, htmlBody, BodyType.Html);
      emailMessage.send();
   }

}
