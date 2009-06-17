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
public class SystemInfoHandler extends AbstractParseHandler {

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ote.ui.define.parser.handlers.AbstractParseHandler#processSaxChunk(org.w3c.dom.Element)
    */
   @Override
   protected void processSaxChunk(Element element) {
      /*
       * <RuntimeVersions> <Version>B3_V1_FTB1.PROPOSED.2007_08_28_14:12:23</Version>
       * <Version>VER1__13-September-2007_03:19:29</Version> </RuntimeVersions> <SystemInfo
       * osArch="i386" osName="Linux" osVersion="2.6.11.12" oseeVersion="Development"/>
       */
      notifyOnDataEvent(TestRunField.SYSTEM_OS_ARCH.toString(), element.getAttribute("osArch"));
      notifyOnDataEvent(TestRunField.SYSTEM_OS_NAME.toString(), element.getAttribute("osName"));
      notifyOnDataEvent(TestRunField.SYSTEM_OS_VERSION.toString(), element.getAttribute("osVersion"));
      notifyOnDataEvent(TestRunField.SYSTEM_OSEE_VERSION.toString(), element.getAttribute("oseeVersion"));
      notifyOnDataEvent(TestRunField.SYSTEM_OSEE_SERVER_TITLE.toString(), element.getAttribute("oseeServerTitle"));
   }
}
