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
public class ArtifactChangeReportComparer extends DataChangeReportComparer {
   private int artId;
   public static String ART_START_TAG = "<artId>";
   public static String ART_END_TAG = "</artId>";

   public ArtifactChangeReportComparer(String content) {
      super(content);
   }

   @Override
   public void processContent(String content) {
      artId =
         Integer.parseInt(content.substring(content.indexOf(ART_START_TAG) + ART_START_TAG.length(),
            content.indexOf(ART_END_TAG)));
   }

   @Override
   public int compareTo(Object obj) {
      int compareResult = -1;
      if (obj instanceof ArtifactChangeReportComparer) {
         ArtifactChangeReportComparer comparer = (ArtifactChangeReportComparer) obj;
         if (this.artId == comparer.artId) {
            compareResult = 0;
         } else if (this.artId > comparer.artId) {
            compareResult = 1;
         }
      }
      return compareResult;
   }
}
