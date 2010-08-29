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
package org.eclipse.osee.ats.health.change;

/**
 * @author Jeff C. Phillips
 */
public class RelationChangeReportComparer extends DataChangeReportComparer {
   private static String REL_TAG_START_ID = "<relId>";
   private static String REL_TAG_END_ID = "</relId>";
   private int relId;

   public RelationChangeReportComparer(String content) {
      super(content);
   }

   @Override
   public void processContent(String content) {
      relId =
         Integer.parseInt(content.substring(content.indexOf(REL_TAG_START_ID) + REL_TAG_START_ID.length(),
            content.indexOf(REL_TAG_END_ID)));
   }

   @Override
   public int compareTo(Object obj) {
      int compareResults = -1;
      if (obj instanceof RelationChangeReportComparer) {
         RelationChangeReportComparer comparer = (RelationChangeReportComparer) obj;

         if (this.relId == comparer.relId) {
            compareResults = 0;
         } else if (this.relId > comparer.relId) {
            compareResults = 1;
         }
      }
      return compareResults;
   }

}
