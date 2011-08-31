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
package org.eclipse.osee.framework.branch.management.exchange.export;

import org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractDbExportItem extends AbstractXmlExportItem {

   private int joinQueryId;

   public AbstractDbExportItem(ExportItem id) {
      super(id);
      this.joinQueryId = -1;
   }

   public void setJoinQueryId(int joinQueryId) {
      this.joinQueryId = joinQueryId;
   }

   protected int getJoinQueryId() {
      return this.joinQueryId;
   }

   @Override
   public void cleanUp() {
      this.joinQueryId = -1;
      super.cleanUp();
   }
}