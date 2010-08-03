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
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.NamedIdentity;

/**
 * @author Donald G. Dunne
 */
public class WorkItemAttributes extends NamedIdentity implements IAttributeType {

   public static final WorkItemAttributes WORK_DATA = new WorkItemAttributes("AAMFEbyhslOoQf3+hyAA", "Work Data");
   public static final WorkItemAttributes WORK_ID = new WorkItemAttributes("AAMFEb0hXw933Zrje2QA", "Work Id");
   public static final WorkItemAttributes WORK_TYPE = new WorkItemAttributes("AAMFEb2f9nh42sjMHfAA", "Work Type");
   public static final WorkItemAttributes WORK_DESCRIPTION = new WorkItemAttributes("AAMFEb57TkhPHyzOLDwA",
      "Work Description");
   public static final WorkItemAttributes WORK_PARENT_ID = new WorkItemAttributes("AAMFEb8R5y9WcjD5hcwA",
      "Work Parent Id");
   public static final WorkItemAttributes WORK_PAGE_NAME = new WorkItemAttributes("AAMFEb+vCyCjKbzzHoQA",
      "Work Page Name");
   public static final WorkItemAttributes WORK_TRANSITION =
      new WorkItemAttributes("AAMFEcBLGB5U+55hJrQA", "Transition");
   public static final WorkItemAttributes WORK_START_PAGE =
      new WorkItemAttributes("AAMFEcDfggQLaAdLlpQA", "Start Page");

   private WorkItemAttributes(String guid, String name) {
      super(guid, "osee.wi." + name);
   }

}
