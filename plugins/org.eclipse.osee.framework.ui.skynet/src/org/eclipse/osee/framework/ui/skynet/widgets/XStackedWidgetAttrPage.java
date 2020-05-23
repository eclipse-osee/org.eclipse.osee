/*********************************************************************
 * Copyright (c) 2017 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;

/**
 * @author Donald G. Dunne
 */
public class XStackedWidgetAttrPage extends XStackedWidgetPage {

   private final Attribute<?> attribute;
   private boolean loaded;

   public XStackedWidgetAttrPage() {
      this(null);
   }

   public XStackedWidgetAttrPage(Attribute<?> attribute) {
      this.attribute = attribute;
      this.setObjectId(attribute);
      this.setObject(attribute);
   }

   public Attribute<?> getAttribute() {
      return attribute;
   }

   @Override
   public Object getObject() {
      return attribute.getArtifact();
   }

   @Override
   public Id getObjectId() {
      return attribute;
   }

   public boolean isLoaded() {
      return loaded;
   }

   public void setLoaded(boolean loaded) {
      this.loaded = loaded;
   }

}
