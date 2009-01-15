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
package org.eclipse.nebula.widgets.xviewer.customize;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class XViewerCustomizations implements IXViewerCustomizations {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations#deleteCustomization(org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData)
    */
   public void deleteCustomization(CustomizeData custData) throws Exception {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations#getCustDatas()
    */
   public List<CustomizeData> getSavedCustDatas() {
      return new ArrayList<CustomizeData>();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations#getUserDefaultCustData()
    */
   public CustomizeData getUserDefaultCustData() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations#isCustomizationUserDefault(org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData)
    */
   public boolean isCustomizationUserDefault(CustomizeData custData) {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations#saveCustomization(org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData)
    */
   public void saveCustomization(CustomizeData custData) throws Exception {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations#setUserDefaultCustData(org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData, boolean)
    */
   public void setUserDefaultCustData(CustomizeData newCustData, boolean set) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations#isCustomizationPersistAvailable()
    */
   public boolean isCustomizationPersistAvailable() {
      return false;
   }

}
