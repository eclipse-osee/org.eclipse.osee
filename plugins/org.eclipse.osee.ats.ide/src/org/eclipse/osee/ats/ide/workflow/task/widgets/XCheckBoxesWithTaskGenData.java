/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workflow.task.widgets;

import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBoxData;

/**
 * @author Donald G. Dunne
 */
public class XCheckBoxesWithTaskGenData extends XCheckBoxData {

   private final String id;
   private IAtsTask task;

   /**
    * @param id Unique id that can have prefix, eg: EST1, EST2, EST3
    */
   public XCheckBoxesWithTaskGenData(String id, String label, boolean checked) {
      super(label, checked);
      this.id = id;
   }

   public String getId() {
      return id;
   }

   public IAtsTask getTask() {
      return task;
   }

   public void setTask(IAtsTask task) {
      this.task = task;
   }

}
