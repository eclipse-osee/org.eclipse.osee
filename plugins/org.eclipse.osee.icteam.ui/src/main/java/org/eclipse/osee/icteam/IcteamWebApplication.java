/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author Ajay Chandrahasan
 */
@SpringBootApplication
public class IcteamWebApplication extends SpringBootServletInitializer {

   /**
    * @param args
    */
   public static void main(final String[] args) {
      SpringApplication.run(IcteamWebApplication.class, args);
   }

   @Override
   protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
      return application.sources(IcteamWebApplication.class);
   }
}
