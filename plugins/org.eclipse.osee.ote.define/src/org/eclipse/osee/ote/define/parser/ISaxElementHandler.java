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
package org.eclipse.osee.ote.define.parser;

public interface ISaxElementHandler {

   public void processSaxChunkCollectorData(String currentLocalName, String xmlData);

   public void addListener(IDataListener listener);

   public void removeListener(IDataListener listener);
}
