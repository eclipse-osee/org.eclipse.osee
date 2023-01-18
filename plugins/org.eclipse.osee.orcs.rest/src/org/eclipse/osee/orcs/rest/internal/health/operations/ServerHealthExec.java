/*******************************************************************************
 * Copyright (c) 2022 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.health.operations;

import java.util.List;
import java.util.Scanner;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
public class ServerHealthExec {

   private final UriInfo uriInfo;

   public ServerHealthExec(UriInfo uriInfo) {
      this.uriInfo = uriInfo;
   }

   public String getHtml() {
      MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters(true);
      List<String> cmds = queryParameters.get("cmd");
      if (cmds == null || cmds.isEmpty()) {
         return AHTML.simplePage("Need cmd parameter.");
      }
      String cmd = cmds.iterator().next();
      System.err.println(String.format("exec cmd [%s]", cmd));
      try {
         try (Scanner s = new Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A")) {
            String results = s.hasNext() ? s.next() : "";
            results = String.format("cmd [%s]<br/><br/>%s", cmd, results.replaceAll("\n", "<br/>"));
            return AHTML.simplePage(results);
         }
      } catch (Exception ex) {
         return AHTML.simplePage(String.format("cmd [%s]<br/><br/>Exception: %s", cmd, Lib.exceptionToString(ex)));
      }
   }

}
