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
import org.eclipse.osee.framework.skynet.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.widgets.Composite;

public abstract class XMultiXWidgetDam extends XMultiXWidget implements IArtifactWidget {

   protected Artifact artifact;
   protected String attributeTypeName;

   public XMultiXWidgetDam(String label) {
      super(label, null);
      super.setXMultiXWidgetFactory(xMultiXWidgetFactory);
   }

   XMultiXWidgetFactory xMultiXWidgetFactory = new XMultiXWidgetFactory() {
      /* (non-Javadoc)
        * @see org.eclipse.osee.framework.ui.skynet.widgets.XMultiXWidgetFactory#addXWidget()
        */
      @Override
      public XWidget addXWidget() {
         return addXWidgetDam();
      }
   };

   public void setArtifact(Artifact artifact, String attributeTypeName) throws SQLException, MultipleAttributesExist, AttributeDoesNotExist {
      this.artifact = artifact;
      this.attributeTypeName = attributeTypeName;
   }

   public abstract void saveToArtifact() throws OseeCoreException, SQLException;

   public abstract Result isDirty() throws OseeCoreException, SQLException;

   @Override
   public void revert() throws OseeCoreException, SQLException {
      setArtifact(artifact, attributeTypeName);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XMultiXWidget#createWidgets(org.eclipse.swt.widgets.Composite, int)
    */
   @Override
   public void createWidgets(Composite parent, int horizontalSpan) {
      try {
         createXWidgets();
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
      super.createWidgets(parent, horizontalSpan);
   }

   /**
    * Creates the xWidgets widgets off artifact's set attributes that will be used in createWidgets
    */
   public abstract void createXWidgets() throws Exception;

   /**
    * Create new XWidget with default value in response to new attribute request
    * 
    * @param artifact
    * @return
    */
   public abstract XWidget addXWidgetDam();

   public abstract void handleUndo() throws Exception;

}