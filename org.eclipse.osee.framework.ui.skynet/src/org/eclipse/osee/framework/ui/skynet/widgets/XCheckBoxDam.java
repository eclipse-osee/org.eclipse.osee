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

/**
 * @author Donald G. Dunne
 */
public class XCheckBoxDam extends XCheckBox implements IDamWidget {

   private Artifact artifact;
   private String attrName;

   /**
    * @param displayLabel
    * @param xmlRoot
    */
   public XCheckBoxDam(String displayLabel, String xmlRoot) {
      super(displayLabel, xmlRoot);
   }

   /**
    * @param displayLabel
    */
   public XCheckBoxDam(String displayLabel) {
      this(displayLabel, "");
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

   @Override
   public void createWidgets(Composite parent, int horizontalSpan) {
      super.createWidgets(parent, horizontalSpan);
      super.addXModifiedListener(modifyListener);
   }

   public void set(boolean selected) {
      super.set(selected);
      try {
         save();
      } catch (IllegalStateException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      } catch (SQLException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }

   public void setArtifact(Artifact artifact, String attrName) throws IllegalStateException, SQLException {
      this.artifact = artifact;
      this.attrName = attrName;
      super.set(artifact.getSoleBooleanAttributeValue(attrName));
   }

   @Override
   public void save() throws IllegalStateException, SQLException {
      if (isDirty()) {
         artifact.setSoleBooleanAttributeValue(attrName, get());
      }
   }

   @Override
   public boolean isDirty() throws IllegalStateException, SQLException {
      return artifact.getSoleBooleanAttributeValue(attrName) != get();
   }

}
