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
public class ConfigHandler extends AbstractParseHandler {

   @Override
   protected void processSaxChunk(Element element) {
      Element user = Jaxp.getChildDirect(element, "User");
      if (user != null) {
         notifyOnDataEvent(TestRunField.USER_ID.toString(), user.getAttribute("id"));
         notifyOnDataEvent(TestRunField.USER_NAME.toString(), user.getAttribute("name"));
      }
      notifyOnDataEvent(TestRunField.SCRIPT_NAME.toString(), Jaxp.getChildTextTrim(element, "ScriptName"));
      Element versionElement = Jaxp.getChild(element, "ScriptVersion");
      if (versionElement != null) {
         notifyOnDataEvent(TestRunField.SCRIPT_REVISION.toString(), versionElement.getAttribute("revision"));
         notifyOnDataEvent(TestRunField.SCRIPT_MODIFIED_FLAG.toString(), versionElement.getAttribute("modifiedFlag"));
         notifyOnDataEvent(TestRunField.SCRIPT_LAST_AUTHOR.toString(), versionElement.getAttribute("lastAuthor"));
         notifyOnDataEvent(TestRunField.SCRIPT_LAST_MODIFIED.toString(), versionElement.getAttribute("lastModified"));
         notifyOnDataEvent(TestRunField.SCRIPT_URL.toString(), versionElement.getAttribute("url"));
      }
   }
}
