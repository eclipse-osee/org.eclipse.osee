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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;

/**
 * @author Donald G. Dunne
 */
public class XCheckBoxesWithTaskGenExample extends XCheckBoxesWithTaskGen {

   public static final Object WIDGET_ID = XCheckBoxesWithTaskGenExample.class.getSimpleName();

   public XCheckBoxesWithTaskGenExample() {
      super("Create Estimating Task(s)", DemoWorkDefinitions.WorkDef_Task_Demo_For_CR_Estimating, 6);
   }

   @Override
   protected String getTaskNameFormat() {
      return "Estimates for [%s]";
   }

   @Override
   protected List<XCheckBoxesWithTaskGenData> getCheckBoxeWithTaskDatas() {
      List<XCheckBoxesWithTaskGenData> tgds = new ArrayList<>();
      tgds.add(new XCheckBoxesWithTaskGenData("TaskReq", "Requirements", false));
      tgds.add(new XCheckBoxesWithTaskGenData("TaskCode", "Code", false));
      tgds.add(new XCheckBoxesWithTaskGenData("TaskSwTest", "SW Test", true));
      tgds.add(new XCheckBoxesWithTaskGenData("TaskIntTest", "Integration Test", false));
      tgds.add(new XCheckBoxesWithTaskGenData("TaskTools", "Tools", false));
      return tgds;
   }

}
