/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.disposition.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Angel Avila
 */
@XmlRootElement(name = "CopySetParams")
public class CopySetParams {

   private CopySetParamOption annotationParam;
   private CopySetParamOption categoryParam;
   private CopySetParamOption noteParam;
   private CopySetParamOption assigneeParam;
   private boolean allowOnlyValidResolutionTypes;

   public CopySetParamOption getAnnotationParam() {
      return annotationParam;
   }

   public CopySetParamOption getCategoryParam() {
      return categoryParam;
   }

   public CopySetParamOption getNoteParam() {
      return noteParam;
   }

   public CopySetParamOption getAssigneeParam() {
      return assigneeParam;
   }

   public boolean getAllowOnlyValidResolutionTypes() {
      return allowOnlyValidResolutionTypes;
   }

   public void setAnnotationParam(CopySetParamOption annotationParam) {
      this.annotationParam = annotationParam;
   }

   public void setCategoryParam(CopySetParamOption categoryParam) {
      this.categoryParam = categoryParam;
   }

   public void setNoteParam(CopySetParamOption noteParam) {
      this.noteParam = noteParam;
   }

   public void setAssigneeParam(CopySetParamOption assigneeParam) {
      this.assigneeParam = assigneeParam;
   }

   public void setAllowOnlyValidResolutionTypes(boolean allowOnlyValidResolutionTypes) {
      this.allowOnlyValidResolutionTypes = allowOnlyValidResolutionTypes;
   }

}
