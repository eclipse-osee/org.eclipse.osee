/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util;

import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageAdapter;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;

public class SimpleTeamState extends WorkPageAdapter {
   private final String name;

   public SimpleTeamState(String name, WorkPageType workPageType) {
      super(SimpleTeamState.class, name, workPageType);
      this.name = name;
   }

   @Override
   public String getPageName() {
      return name;
   }

}
