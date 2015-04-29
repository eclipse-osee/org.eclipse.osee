/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.agile;

import org.eclipse.osee.framework.core.util.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class AgileWriterResult {

   private JaxAgileItem jaxAgileItem;
   private final XResultData results = new XResultData();

   public AgileWriterResult() {
   }

   public JaxAgileItem getJaxAgileItem() {
      return jaxAgileItem;
   }

   public void setJaxAgileItem(JaxAgileItem jaxAgileItem) {
      this.jaxAgileItem = jaxAgileItem;
   }

   public XResultData getResults() {
      return results;
   }

   public boolean isErrors() {
      return results.isErrors();
   }

}
