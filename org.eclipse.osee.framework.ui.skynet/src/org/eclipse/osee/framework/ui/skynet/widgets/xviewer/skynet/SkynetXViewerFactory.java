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
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet;

import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.XViewerCustomizations;

/**
 * @author Donald G. Dunne
 */
public class SkynetXViewerFactory extends XViewerFactory {

   /**
    * @param namespace
    */
   public SkynetXViewerFactory(String namespace) {
      super(namespace);
   }

   private IXViewerCustomizations xViewerCustomizations;

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory#getXViewerCustomizations()
    */
   public IXViewerCustomizations getXViewerCustomizations() {
      try {
         if (ConnectionHandler.isConnected()) {
            if (xViewerCustomizations == null) {
               xViewerCustomizations = new SkynetCustomizations(this);
            }
            return xViewerCustomizations;
         }
      } catch (IllegalStateException ex) {
         OSEELog.logException(SkynetXViewerFactory.class,
               "Failed to retrieve XViewer customizations from the persistence layer.", ex, false);
      }
      return new XViewerCustomizations();
   }

}
