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
package org.eclipse.osee.ote.ui.navigate;

import java.util.List;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class OteExtensionPointData {
   private String category;
   private IOteNavigateItem navigateItem;
   
   public OteExtensionPointData(String category, IOteNavigateItem navigateItem){
      this.category = category;
      this.navigateItem = navigateItem;
   }
   
   public String[] getItemPath(){
      if(category != null && category.length()>0){
         return category.split("\\.");
      } else {
         return new String[0];
      }
   }
   
   public List<XNavigateItem> getNavigateItems(){
      return navigateItem.getNavigateItems();
   }

   public String getCategory() {
      return category;
   }
   
}
