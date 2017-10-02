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
