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
package org.eclipse.osee.framework.ui.skynet.util;

import java.util.Date;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;

/**
 * @author Donald G. Dunne
 */
public class ElapsedTime {

   Date startDate;
   Date endDate;
   private String name;

   public ElapsedTime(String name) {
      start(name);
   }

   public void start(String name) {
      this.name = name;
      startDate = new Date();
      System.out.println("\n" + name + " - start " + XDate.getTimeStamp());
   }

   public void end() {
      endDate = new Date();
      long diff = endDate.getTime() - startDate.getTime();
      String str =
         String.format("%s - elapsed %d sec - start %s - end %s", name, (diff / 1000),
            XDate.getDateStr(startDate, XDate.HHMMSSSS), XDate.getDateStr(endDate, XDate.HHMMSSSS));
      System.out.println(str);
   }
}
