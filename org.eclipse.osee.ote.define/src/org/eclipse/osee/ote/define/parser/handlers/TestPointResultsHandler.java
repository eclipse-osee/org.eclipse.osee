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
public class TestPointResultsHandler extends AbstractParseHandler {

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ote.ui.define.parser.handlers.AbstractParseHandler#processSaxChunk(org.w3c.dom.Element)
    */
   @Override
   protected void processSaxChunk(Element element) {
      notifyOnDataEvent(TestRunField.TEST_POINTS_PASSED.toString(), element.getAttribute("pass"));
      notifyOnDataEvent(TestRunField.TEST_POINTS_FAILED.toString(), element.getAttribute("fail"));
      notifyOnDataEvent(TestRunField.TOTAL_TEST_POINTS.toString(), element.getAttribute("total"));
      notifyOnDataEvent(TestRunField.TEST_ABORT_STATUS.toString(), element.getAttribute("aborted"));
   }
}
