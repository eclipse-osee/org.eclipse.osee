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
package org.eclipse.osee.framework.types.bridge.wizards;

import org.eclipse.emf.common.util.URI;

/**
 * @author Roberto E. Escobar
 */
public class LinkMessage extends LinkNode {
   private final String importEntry;

   public LinkMessage(URI nodeURI, String importEntry) {
      super(nodeURI);
      this.importEntry = importEntry;
   }

   public String getImportEntry() {
      return importEntry;
   }
}
