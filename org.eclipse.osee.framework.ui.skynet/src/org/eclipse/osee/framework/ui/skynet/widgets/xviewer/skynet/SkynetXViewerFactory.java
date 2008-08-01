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
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.XViewerCustomizations;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerAttributeColumn;
import org.eclipse.swt.SWT;

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
   @Override
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

   public void registerAllAttributeColumns() {
      try {
         for (AttributeType attributeType : AttributeTypeManager.getTypes()) {
            XViewerAttributeColumn newCol =
                  new XViewerAttributeColumn("attribute." + attributeType.getName(), attributeType.getName(),
                        attributeType.getName(), 75, SWT.LEFT, false, XViewerAttributeSortDataType.get(attributeType),
                        false, null);
            registerColumn(newCol);
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, false);
      }
   }
}
