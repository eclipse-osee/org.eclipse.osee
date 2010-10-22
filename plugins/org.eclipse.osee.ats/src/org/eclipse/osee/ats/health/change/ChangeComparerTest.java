/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.health.change;

import java.util.logging.Level;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Jeff C. Phillips
 */
public class ChangeComparerTest {

   public static void main(String[] args) {
      String content = "<artId>12535</artId>";
      OseeLog.log(
         AtsPlugin.class,
         Level.SEVERE,
         Integer.valueOf(
            Integer.parseInt(content.substring(content.indexOf("<artId>") + 7, content.indexOf("</artId>")))).toString());
   }

}
