/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.api.agile;

import org.eclipse.osee.framework.jdk.core.result.XResultData;

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
