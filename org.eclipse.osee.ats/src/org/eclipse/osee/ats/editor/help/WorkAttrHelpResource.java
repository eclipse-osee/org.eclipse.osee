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

import java.io.File;
import java.io.IOException;
import org.eclipse.help.IHelpResource;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;

/**
 * @author Donald G. Dunne
 */
public class WorkAttrHelpResource implements IHelpResource {

   private final DynamicXWidgetLayoutData layoutData;

   public WorkAttrHelpResource(DynamicXWidgetLayoutData layoutData) {
      this.layoutData = layoutData;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.help.IHelpResource#getHref()
    */
   public String getHref() {
      if (layoutData != null) {
         File file = AtsPlugin.getInstance().getPluginStoreFile(layoutData.getStorageName() + ".html");
         String absFile = "file:\\/\\/" + file.getAbsolutePath();
         StringBuffer sb = new StringBuffer();
         sb.append(AHTML.heading(1, layoutData.getName()));
         if (layoutData.getToolTip() != null)
            sb.append(AHTML.para(layoutData.getToolTip()));
         else
            sb.append(AHTML.para("Enter the " + layoutData.getName()));
         String html = AHTML.simplePage(sb.toString());

         try {
            Lib.writeStringToFile(html, file);
         } catch (IOException ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }

         return absFile;
      }
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.help.IHelpResource#getLabel()
    */
   public String getLabel() {
      if (layoutData != null) return layoutData.getName();
      return "";
   }

}
