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

import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.ote.define.TestRunField;
import org.w3c.dom.Element;

/**
 * @author Roberto E. Escobar
 */
public class ExecutionStatusHandler extends AbstractParseHandler {

   @Override
   protected void processSaxChunk(Element element) {
      notifyOnDataEvent(TestRunField.SCRIPT_EXECUTION_TIME.toString(), Jaxp.getChildText(element, "Time"));
      notifyOnDataEvent(TestRunField.SCRIPT_EXECUTION_RESULTS.toString(),
         Jaxp.getChildText(element, "ExecutionResult"));
      notifyOnDataEvent(TestRunField.SCRIPT_EXECUTION_ERRORS.toString(),
         Jaxp.getChildText(element, "ExecutionDetails"));
   }
}
