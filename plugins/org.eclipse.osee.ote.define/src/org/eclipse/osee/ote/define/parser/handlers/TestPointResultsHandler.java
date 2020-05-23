/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ote.define.parser.handlers;

import org.eclipse.osee.ote.define.TestRunField;
import org.w3c.dom.Element;

/**
 * @author Roberto E. Escobar
 */
public class TestPointResultsHandler extends AbstractParseHandler {

   @Override
   protected void processSaxChunk(Element element) {
      notifyOnDataEvent(TestRunField.TEST_POINTS_PASSED.toString(), element.getAttribute("pass"));
      notifyOnDataEvent(TestRunField.TEST_POINTS_FAILED.toString(), element.getAttribute("fail"));
      notifyOnDataEvent(TestRunField.TOTAL_TEST_POINTS.toString(), element.getAttribute("total"));
      notifyOnDataEvent(TestRunField.TEST_ABORT_STATUS.toString(), element.getAttribute("aborted"));
   }
}
