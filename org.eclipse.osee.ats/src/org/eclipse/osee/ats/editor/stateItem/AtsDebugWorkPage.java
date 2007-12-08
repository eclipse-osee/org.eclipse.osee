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

import org.eclipse.osee.ats.workflow.AtsWorkPage;

/**
 * @author Donald G. Dunne
 */
public class AtsDebugWorkPage extends AtsWorkPage {

   public static String PAGE_ID = "ats.Debug";

   /**
    * @param name
    * @param id
    * @param widgetsXml
    * @param optionResolver
    */
   public AtsDebugWorkPage() {
      super("ATS Admin Debug", "ats.Debug", null, null);
   }

}
