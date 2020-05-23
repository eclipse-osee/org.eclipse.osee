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

import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Donald G. Dunne
 */
public class XStackedWidgetPage extends BaseIdentity<String> {

   Object value;
   Object object;
   Id objectId;
   XWidget widget;

   public XStackedWidgetPage() {
      super(GUID.create());
   }

   public Object getObject() {
      return object;
   }

   public void setObject(Object object) {
      this.object = object;
   }

   public XWidget getWidget() {
      return widget;
   }

   public void setWidget(XWidget widget) {
      this.widget = widget;
   }

   public Object getValue() {
      return value;
   }

   public void setValue(Object value) {
      this.value = value;
   }

   public Id getObjectId() {
      return objectId;
   }

   public void setObjectId(Id objectId) {
      this.objectId = objectId;
   }

   @Override
   public String toString() {
      return "XStackedWidgetPage + guid=" + getGuid() + " object=" + object + ", objectId=" + objectId + ", widget=" + widget + ", value=" + value + "]";
   }

}
