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
import org.eclipse.osee.framework.skynet.core.util.AttributeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.util.MultipleAttributesExist;
import org.eclipse.osee.framework.ui.plugin.util.Result;

public class XMultiXWidgetDam extends XMultiXWidget implements IArtifactWidget {

   private Artifact artifact;
   private String attributeTypeName;
   private final XMultiXWidgetDamFactory xMultiXWidgetDamFactory;

   public XMultiXWidgetDam(String label, XMultiXWidgetDamFactory xMultiXWidgetDamFactory) {
      super(label, null);
      super.setXMultiXWidgetFactory(xMultiXWidgetFactory);
      this.xMultiXWidgetDamFactory = xMultiXWidgetDamFactory;
   }

   XMultiXWidgetFactory xMultiXWidgetFactory = new XMultiXWidgetFactory() {
      /* (non-Javadoc)
        * @see org.eclipse.osee.framework.ui.skynet.widgets.XMultiXWidgetFactory#addXWidget()
        */
      @Override
      public XWidget addXWidget() {
         return xMultiXWidgetDamFactory.addXWidgetDam();
      }
   };

   public void setArtifact(Artifact artifact, String attributeTypeName) throws SQLException, MultipleAttributesExist, AttributeDoesNotExist {
      this.artifact = artifact;
      this.attributeTypeName = attributeTypeName;

      xWidgets = xMultiXWidgetDamFactory.createXWidgets();
   }

   @Override
   public void saveToArtifact() throws SQLException, MultipleAttributesExist {
      xMultiXWidgetDamFactory.saveToArtifact(xWidgets);
   }

   @Override
   public Result isDirty() throws Exception {
      return xMultiXWidgetDamFactory.isDirty(xWidgets);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget#revert()
    */
   @Override
   public void revert() throws Exception {
      setArtifact(artifact, attributeTypeName);
   }
}