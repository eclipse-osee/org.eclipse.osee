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

package org.eclipse.osee.framework.ui.skynet.widgets.xnavigate;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class XNavigateItemAutoRunAction extends XNavigateItemAction {

   private String autoRunUniqueId;

   /**
    * @param parent
    * @param name
    */
   public XNavigateItemAutoRunAction(XNavigateItem parent, String name) {
      super(parent, name);
   }

   /**
    * @param parent
    * @param name
    * @param promptFirst
    */
   public XNavigateItemAutoRunAction(XNavigateItem parent, String name, boolean promptFirst) {
      super(parent, name, promptFirst);
   }

   /**
    * @param parent
    * @param action
    */
   public XNavigateItemAutoRunAction(XNavigateItem parent, Action action) {
      super(parent, action);
   }

   /**
    * @param parent
    * @param action
    * @param image
    * @param promptFirst
    */
   public XNavigateItemAutoRunAction(XNavigateItem parent, Action action, Image image, boolean promptFirst) {
      super(parent, action, image, promptFirst);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.util.IAutoRunTask#getAutoRunUniqueId()
    */
   public String getAutoRunUniqueId() {
      return autoRunUniqueId;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.util.IAutoRunTask#setAutoRunUniqueId(java.lang.String)
    */
   public void setAutoRunUniqueId(String autoRunUniqueId) {
      this.autoRunUniqueId = autoRunUniqueId;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.util.IAutoRunTask#getNotificationEmailAddresses()
    */
   public String[] getNotificationEmailAddresses() {
      return new String[] {"donald.g.dunne@boeing.com"};
   }

}
