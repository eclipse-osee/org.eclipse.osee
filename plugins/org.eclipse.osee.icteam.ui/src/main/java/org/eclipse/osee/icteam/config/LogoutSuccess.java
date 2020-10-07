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
package org.eclipse.osee.icteam.config;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * @author Ajay Chandrahasan
 */

@Component
public class LogoutSuccess implements LogoutSuccessHandler {

   /**
    * {@inheritDoc}
    */
   @Override
   public void onLogoutSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {
      if ((authentication != null) && (authentication.getDetails() != null)) {
         try {
            request.getSession().invalidate();
         } catch (Exception e) {
            e.printStackTrace();
            e = null;
         }
      }
      response.setStatus(HttpServletResponse.SC_OK);
   }

}
