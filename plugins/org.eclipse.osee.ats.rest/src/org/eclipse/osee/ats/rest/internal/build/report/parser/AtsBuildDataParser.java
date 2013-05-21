/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.build.report.parser;

import java.io.File;
import org.eclipse.osee.ats.rest.internal.build.report.model.AtsBuildData;
import org.eclipse.osee.ats.rest.internal.build.report.model.AtsElementData;
import org.xml.sax.Attributes;

/**
 * @author Megumi Telles
 */
public class AtsBuildDataParser extends AtsAbstractSAXParser<AtsBuildData> {

   private AtsBuildData atsBuildData;
   private boolean inBuildName = false, inBuildId = false, inBuildProgramId = false;

   public AtsBuildDataParser(File buildDataFileName, AtsDataHandler<AtsBuildData> handler) {
      super(buildDataFileName, handler);
   }

   @Override
   public void characters(char[] ch, int start, int length) {
      if (inBuildName) {
         atsBuildData.setBuildName(new String(ch, start, length));
      } else if (inBuildId) {
         atsBuildData.setBuildId(new String(ch, start, length));
      } else if (inBuildProgramId) {
         atsBuildData.setBuildProgramId(new String(ch, start, length));
      }
   }

   @Override
   public void startElement(String s, String s1, String elementName, Attributes attributes) {
      if (elementName.equals(AtsElementData.BUILD)) {
         atsBuildData = new AtsBuildData();
      } else if (elementName.equals(AtsElementData.BUILD_NAME)) {
         inBuildName = true;
      } else if (elementName.equals(AtsElementData.BUILD_ID)) {
         inBuildId = true;
      } else if (elementName.equals(AtsElementData.BUILD_PROGRAM_ID)) {
         inBuildProgramId = true;
      }
   }

   @Override
   public void endElement(String s, String s1, String element) {
      if (element.equals(AtsElementData.BUILD)) {
         handleData(atsBuildData);
      } else if (element.equals(AtsElementData.BUILD_NAME)) {
         inBuildName = false;
      } else if (element.equals(AtsElementData.BUILD_ID)) {
         inBuildId = false;
      } else if (element.equals(AtsElementData.BUILD_PROGRAM_ID)) {
         inBuildProgramId = false;
      }
   }

}
