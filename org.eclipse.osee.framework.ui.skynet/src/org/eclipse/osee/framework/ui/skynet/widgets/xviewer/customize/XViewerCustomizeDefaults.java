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
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize;

/**
 * Methods used to load and store user selected default customization
 * 
 * @author Donald G. Dunne
 */
public class XViewerCustomizeDefaults implements IXViewerCustomizeDefaults {

   /**
    * 
    */
   public XViewerCustomizeDefaults() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.defaults.IXViewerDefaults#isDefaultCustomization(org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData)
    */
   public boolean isDefaultCustomization(CustomizeData custData) {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.defaults.IXViewerDefaults#removeDefaultCustomization(org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData)
    */
   public void removeDefaultCustomization(CustomizeData custData) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.defaults.IXViewerDefaults#save()
    */
   public void save() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.defaults.IXViewerDefaults#setDefaultCustomization(org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer, org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData)
    */
   public void setDefaultCustomization(CustomizeData custData) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizeDefaults#isSaveDefaultsEnabled()
    */
   public boolean isSaveDefaultsEnabled() {
      return false;
   }

}
