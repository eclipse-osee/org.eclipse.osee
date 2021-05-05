/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.framework.jdk.core.util;

/**
 * @author Audrey E Denk
 */
public class CellData {

   private String text = Strings.EMPTY_STRING;
   private String hyperlink = Strings.EMPTY_STRING;
   private String mergeAcross = Strings.EMPTY_STRING;
   private String mergeDown = Strings.EMPTY_STRING;
   private String style = Strings.EMPTY_STRING;

   public CellData() {
      //
   }

   public CellData(String text, String hyperlink, String mergeAcross, String mergeDown) {
      this.setText(text);
      this.setHyperlink(hyperlink);
      this.setMergeAcross(mergeAcross);
      this.setMergeDown(mergeDown);
   }

   public CellData(String text, String hyperlink, String mergeAcross, String mergeDown, String style) {
      this.setText(text);
      this.setHyperlink(hyperlink);
      this.setMergeAcross(mergeAcross);
      this.setMergeDown(mergeDown);
      this.setStyle(style);
   }

   public String getText() {
      return text;
   }

   public void setText(String text) {
      this.text = text;
   }

   public String getHyperlink() {
      return hyperlink;
   }

   public void setHyperlink(String hyperlink) {
      this.hyperlink = hyperlink;
   }

   public String getMergeAcross() {
      return mergeAcross;
   }

   public void setMergeAcross(String mergeAcross) {
      this.mergeAcross = mergeAcross;
   }

   public String getMergeDown() {
      return mergeDown;
   }

   public void setMergeDown(String mergeDown) {
      this.mergeDown = mergeDown;
   }

   public String getStyle() {
      return style;
   }

   public void setStyle(String style) {
      this.style = style;
   }

}
