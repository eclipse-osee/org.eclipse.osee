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
package org.eclipse.osee.ote.ui.test.manager.configuration;

import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract interface ISaveConfig {

   public abstract Element toXml(Document doc);
   public abstract void saveConfig(File fileName) throws Exception;
   public abstract void printXmlTree();
}
