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

import java.io.InputStream;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
public class ServerStatusTop {

   public String get() {
      StringBuilder sb = new StringBuilder();
      if (Lib.isWindows()) {
         sb.append("<h3>Top is not available for windows (example below)</h3>");
         String str = OseeInf.getResourceContents("web/health/top.txt", ServerStatusTop.class);
         str = String.format("<pre>%s</pre>", str);
         sb.append(str);
      } else {
         sb.append("<h3>Machine \"top\" results</h3>");
         try {
            ProcessBuilder pb = new ProcessBuilder("top", "-n");
            pb.redirectError();
            Process p = pb.start();
            InputStream is = p.getInputStream();
            int value = -1;
            while ((value = is.read()) != -1) {
               sb.append((char) value + "</br>");
            }
            int exitCode = p.waitFor();
            sb.append("Top exited with " + exitCode);
            is.close();
         } catch (Exception ex) {
            sb.append(Lib.exceptionToString(ex));
         }
      }
      return AHTML.simplePage(sb.toString());
   }

}
