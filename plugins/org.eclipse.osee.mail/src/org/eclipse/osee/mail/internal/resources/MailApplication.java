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
package org.eclipse.osee.mail.internal.resources;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.mail.api.MailService;

/**
 * @author Roberto E. Escobar
 */
@ApplicationPath("mail")
public class MailApplication extends Application {

   private final Set<Object> singletons = new HashSet<>();
   private MailService mailService;

   public void setMailService(MailService mailService) {
      this.mailService = mailService;
   }

   public void start() {
      singletons.add(new MailResource(mailService));
   }

   public void stop() {
      singletons.clear();
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }
}
