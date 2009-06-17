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
package org.eclipse.osee.ote.define.parser.handlers;

import org.eclipse.osee.ote.define.TestRunField;
import org.w3c.dom.Element;

/**
 * @author Roberto E. Escobar
 */
public class ElapsedTimeHandler extends AbstractParseHandler {

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ote.ui.define.parser.handlers.AbstractParseHandler#processSaxChunk(org.w3c.dom.Element)
    */
   @Override
   protected void processSaxChunk(Element element) {
      /*
       * <RuntimeVersions> <Version>B3_V1_FTB1.PROPOSED.2007_08_28_14:12:23</Version>
       * <Version>VER1__13-September-2007_03:19:29</Version> </RuntimeVersions>
       * 
       * <ElapsedTime elapsed="0:01:28" endDate="Thu Sep 13 15:50:24 MDT 2007" milliseconds="88768"
       * startDate="Thu Sep 13 15:48:55 MDT 2007"/> <TestPointResults fail="41" pass="167"
       * total="208"/>
       * 
       */
      notifyOnDataEvent(TestRunField.SCRIPT_END_DATE.toString(), element.getAttribute("endDate"));
      notifyOnDataEvent(TestRunField.SCRIPT_START_DATE.toString(), element.getAttribute("startDate"));
      notifyOnDataEvent(TestRunField.SCRIPT_ELAPSED_TIME.toString(), element.getAttribute("elapsed"));
      // callback.addOverviewData("Time Info", String.format("Elapsed [ %s ] Start [ %s ] Stop [
      // %s ]", elapsed, startDate, endDate));
   }
}
