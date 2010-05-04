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

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jeff C. Phillips
 */
public class ValidateChangeReportParser {
   private static final String ART = "(<ArtChg>.*?</ArtChg>)";
   private static final String ATTR = "(<AttrChg>.*?</AttrChg>)";
   private static final String REL = "(<RelChg>.*?</RelChg>)";

   /**
    * @param changeReportString 
    * @return Returns Three ArrayLists. 0 index for artifact changes, 1 index for attribute changes and 2 index for
    *         relation changes.
    */
   public ArrayList<ArrayList<DataChangeReportComparer>> parse(String changeReportString) {
      ArrayList<ArrayList<DataChangeReportComparer>> changeLists =
            new ArrayList<ArrayList<DataChangeReportComparer>>(3);
      ArrayList<DataChangeReportComparer> artifactChanges = new ArrayList<DataChangeReportComparer>();
      ArrayList<DataChangeReportComparer> attrChanges = new ArrayList<DataChangeReportComparer>();
      ArrayList<DataChangeReportComparer> relChanges = new ArrayList<DataChangeReportComparer>();

      Matcher artChangeMatch = Pattern.compile(ART).matcher(changeReportString);

      while (artChangeMatch.find()) {
         artifactChanges.add(new ArtifactChangeReportComparer(artChangeMatch.group(0)));
      }

      Matcher attrChangeMatch = Pattern.compile(ATTR).matcher(changeReportString);

      while (attrChangeMatch.find()) {
         attrChanges.add(new AttributeChangeReportComparer(attrChangeMatch.group(0)));
      }

      Matcher relChangeMatch = Pattern.compile(REL).matcher(changeReportString);

      while (relChangeMatch.find()) {
         relChanges.add(new RelationChangeReportComparer(relChangeMatch.group(0)));
      }
      Collections.sort(artifactChanges);
      Collections.sort(attrChanges);
      Collections.sort(relChanges);

      changeLists.add(artifactChanges);
      changeLists.add(attrChanges);
      changeLists.add(relChanges);

      return changeLists;
   }
}
