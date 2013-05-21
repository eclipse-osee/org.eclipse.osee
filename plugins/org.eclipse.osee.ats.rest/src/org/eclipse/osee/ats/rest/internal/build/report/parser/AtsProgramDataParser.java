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
import org.eclipse.osee.ats.rest.internal.build.report.model.AtsElementData;
import org.eclipse.osee.ats.rest.internal.build.report.model.AtsProgramData;
import org.xml.sax.Attributes;

/**
 * @author Megumi Telles
 */

public class AtsProgramDataParser extends AtsAbstractSAXParser<AtsProgramData> {

   private AtsProgramData atsProgramData;
   private boolean inProgramName = false;
   private boolean inProgramId = false;

   public AtsProgramDataParser(File programDataFileName, AtsDataHandler<AtsProgramData> handler) {
      super(programDataFileName, handler);
   }

   @Override
   public void characters(char[] ch, int start, int length) {
      if (inProgramName) {
         atsProgramData.setProgramName(new String(ch, start, length));
      } else if (inProgramId) {
         atsProgramData.setProgramId(new String(ch, start, length));
      }
   }

   @Override
   public void startElement(String s, String s1, String elementName, Attributes attributes) {
      if (elementName.equals(AtsElementData.PROGRAM)) {
         atsProgramData = new AtsProgramData();
      } else if (elementName.equals(AtsElementData.PROGRAM_NAME)) {
         inProgramName = true;
      } else if (elementName.equals(AtsElementData.PROGRAM_ID)) {
         inProgramId = true;
      }
   }

   @Override
   public void endElement(String s, String s1, String element) {
      if (element.equals(AtsElementData.PROGRAM)) {
         handleData(atsProgramData);
      } else if (element.equals(AtsElementData.PROGRAM_NAME)) {
         inProgramName = false;
      } else if (element.equals(AtsElementData.PROGRAM_ID)) {
         inProgramId = false;
      }
   }

}
