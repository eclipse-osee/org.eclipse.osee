/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.config;

import java.util.Collection;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionSheet;
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

   public abstract Collection<WorkDefinitionSheet> getTeamsAiSheets();

   public abstract Collection<WorkDefinitionSheet> getWorkDefSheets();

   public XResultData getResultData() {
      if (xResultData == null) {
         xResultData = new XResultData(false);
      }
      return xResultData;
   }

   public abstract void performPostConfig(IAtsChangeSet changes, AbstractAtsConfig2Data data);

}
