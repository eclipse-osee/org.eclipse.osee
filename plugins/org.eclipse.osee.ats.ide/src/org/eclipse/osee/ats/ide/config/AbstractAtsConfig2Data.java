/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.config;

import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsConfig2Data {

   private final String configName;

   public AbstractAtsConfig2Data(String configName) {
      this.configName = configName;
   }

   private XResultData xResultData;

   public String getConfigName() {
      return configName;
   }

   public XResultData validate() {
      return getResultData();
   }

   public XResultData getResultData() {
      if (xResultData == null) {
         xResultData = new XResultData(false);
      }
      return xResultData;
   }

   public abstract void performPostConfig(IAtsChangeSet changes, AbstractAtsConfig2Data data);

}
