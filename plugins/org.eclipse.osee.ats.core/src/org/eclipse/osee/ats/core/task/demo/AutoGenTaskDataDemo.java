/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.task.demo;

import org.eclipse.osee.ats.api.task.related.AutoGenTaskDataVer2ViaAttrs;
import org.eclipse.osee.ats.api.task.related.AutoGenVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTask;

/**
 * @author Donald G. Dunne
 */
public class AutoGenTaskDataDemo extends AutoGenTaskDataVer2ViaAttrs {

   String description;

   public AutoGenTaskDataDemo(IAtsTask task) {
      super(task);
   }

   @Override
   public AutoGenVersion getAutoGenVer() {
      return AutoGenVersionDemo.Demo;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

}
