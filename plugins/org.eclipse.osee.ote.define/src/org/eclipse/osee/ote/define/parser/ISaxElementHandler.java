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

package org.eclipse.osee.ote.define.parser;

public interface ISaxElementHandler {

   public void processSaxChunkCollectorData(String currentLocalName, String xmlData);

   public void addListener(IDataListener listener);

   public void removeListener(IDataListener listener);
}
