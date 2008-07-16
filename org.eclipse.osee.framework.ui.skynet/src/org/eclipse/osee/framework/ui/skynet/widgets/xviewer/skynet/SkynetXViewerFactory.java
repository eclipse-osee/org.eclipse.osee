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
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizeDefaults;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.XViewerCustomizations;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.XViewerCustomizeDefaults;

/**
 * @author Donald G. Dunne
 */
public class SkynetXViewerFactory implements IXViewerFactory {

   private IXViewerCustomizeDefaults xViewerCustDefaults;
   private IXViewerCustomizations xViewerCustomizations;

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory#createNewXSorter(org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer)
    */
   public XViewerSorter createNewXSorter(XViewer viewer) {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory#getDefaultTableCustomizeData()
    */
   public CustomizeData getDefaultTableCustomizeData(XViewer xViewer) {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory#getDefaultXViewerColumn(java.lang.String)
    */
   public XViewerColumn getDefaultXViewerColumn(String name) {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory#getXViewerCustomizeDefaults()
    */
   public IXViewerCustomizeDefaults getXViewerCustomizeDefaults() {
      if (ConnectionHandler.isConnected()) {
         if (xViewerCustDefaults == null) {
            xViewerCustDefaults =
                  new SkynetCustomizeDefaults(SkynetAuthentication.getUser());
         }
         return xViewerCustDefaults;
      }
      return new XViewerCustomizeDefaults();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory#getXViewerCustomizations()
    */
   public IXViewerCustomizations getXViewerCustomizations(XViewer xViewer) {
      try {
         if (ConnectionHandler.isConnected()) {
            if (xViewerCustomizations == null) {
               xViewerCustomizations = new SkynetCustomizations(xViewer, getXViewerCustomizeDefaults());
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
