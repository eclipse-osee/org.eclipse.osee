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
package org.eclipse.osee.ats.health.change;

/**
 * @author Jeff C. Phillips
 */
public abstract class DataChangeReportComparer implements Comparable<Object> {
   private final String content;

   public DataChangeReportComparer(String content) {
      this.content = content;
      processContent(content);
   }

   public abstract void processContent(String content);

   public String getContent() {
      return content;
   }
}
