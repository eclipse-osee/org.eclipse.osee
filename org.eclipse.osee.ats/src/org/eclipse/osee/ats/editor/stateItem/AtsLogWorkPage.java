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
package org.eclipse.osee.ats.editor.stateItem;

import org.eclipse.osee.ats.editor.service.WorkPageService;
import org.eclipse.osee.ats.workflow.AtsWorkPage;

/**
 * @author Donald G. Dunne
 */
public class AtsLogWorkPage extends AtsWorkPage {

   public static String PAGE_ID = "ats.Log";

   /**
    * @param title
    */
   public AtsLogWorkPage(String title) {
      super(title, "ats.Log", null, null);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.workflow.AtsWorkPage#isDisplayService(org.eclipse.osee.ats.editor.service.WorkPageService)
    */
   @Override
   public boolean isDisplayService(WorkPageService service) {
      return false;
   }

}
