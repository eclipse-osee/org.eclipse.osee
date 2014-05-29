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
package org.eclipse.osee.mail.rest.internal;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.mail.MailService;

/**
 * @author Roberto E. Escobar
 */
@ApplicationPath("mail")
public class MailApplication extends Application {

   private static MailService mailService;

   public void setMailService(MailService mailService) {
      MailApplication.mailService = mailService;
   }

   public static MailService getMailService() {
      return mailService;
   }

   @Override
   public Set<Class<?>> getClasses() {
      Set<Class<?>> resources = new HashSet<Class<?>>();
      resources.add(MailConfigResource.class);
      resources.add(MailResource.class);
      return resources;
   }

}
