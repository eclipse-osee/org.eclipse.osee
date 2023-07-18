/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.framework.ui.skynet.mdeditor.model;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractOmeData {

   protected Boolean editable = null;

   public String mdContent;

   public String htmlContent;

   public abstract boolean isEditable();

   public abstract String getEditorName();

   public abstract boolean isDirty();

   public abstract void setWidget(XText editText);

   public abstract void doSave();

   public abstract void onSaveException(OseeCoreException ex);

   public abstract void dispose();

   public abstract void load();

   public abstract XText createXText(boolean enabled);

   public abstract void uponCreate(XText editText);

   public String getMdContent() {
      return mdContent;
   }

   public void setMdContent(String mdContent) {
      this.mdContent = mdContent;
   }

   public String getHtmlContent() {
      return htmlContent;
   }

   public void setHtmlContent(String htmlContent) {
      this.htmlContent = htmlContent;
   }

}
