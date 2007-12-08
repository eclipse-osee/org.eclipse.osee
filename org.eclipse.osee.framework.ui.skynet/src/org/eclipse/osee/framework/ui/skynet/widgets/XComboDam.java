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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.widgets.Composite;

public class XComboDam extends XCombo implements IDamWidget {

   private Artifact artifact;
   private String attrName;

   public XComboDam(String displayLabel) {
      super(displayLabel);
   }

   XModifiedListener modifyListener = new XModifiedListener() {
      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener#widgetModified(org.eclipse.osee.framework.ui.skynet.widgets.XWidget)
       */
      public void widgetModified(XWidget widget) {
         try {
            save();
         } catch (IllegalStateException ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         } catch (SQLException ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }
      }
   };

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XCombo#createWidgets(org.eclipse.swt.widgets.Composite, int,
    *      java.lang.String[])
    */
   @Override
   public void createWidgets(Composite composite, int horizontalSpan, String[] inDataStrings) {
      super.createWidgets(composite, horizontalSpan, inDataStrings);
      super.addXModifiedListener(modifyListener);
   }

   public void createWidgets(Composite composite, int horizontalSpan) {
      super.createWidgets(composite, horizontalSpan);
      super.addXModifiedListener(modifyListener);
   }

   public void setArtifact(Artifact artifact, String attrName) throws IllegalStateException, SQLException {
      this.artifact = artifact;
      this.attrName = attrName;

      super.set(artifact.getSoleAttributeValue(attrName));
   }

   @Override
   public void set(String text) throws IllegalStateException, SQLException {
      super.set(text);
      artifact.setSoleAttributeValue(attrName, text);
   }

   @Override
   public void save() throws IllegalStateException, SQLException {
      if (isDirty()) {
         artifact.setSoleAttributeValue(attrName, get());
      }
   }

   @Override
   public boolean isDirty() {
      return !artifact.getSoleAttributeValue(attrName).equals(get());
   }

}
