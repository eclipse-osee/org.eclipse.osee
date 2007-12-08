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
package org.eclipse.osee.ats.editor.help;

import org.eclipse.help.IContext;
import org.eclipse.help.IHelpResource;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;

/**
 * @author Donald G. Dunne
 */
public class AtsHelpContext implements IContext {

   private final DynamicXWidgetLayoutData layoutData;

   public AtsHelpContext(DynamicXWidgetLayoutData layoutData) {
      this.layoutData = layoutData;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.help.IContext#getRelatedTopics()
    */
   public IHelpResource[] getRelatedTopics() {
      return new IHelpResource[] {new WorkAttrHelpResource(layoutData)};
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.help.IContext#getText()
    */
   public String getText() {
      return "The Action Tracking System (ATS) editor provides a common editor for any workflow.";
   }

}
