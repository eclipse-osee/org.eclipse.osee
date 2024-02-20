/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.framework.core.data;

public class ArtifactTypeIcon {

   public static ArtifactTypeIcon SENTINEL =
      new ArtifactTypeIcon("padding", MaterialColors.NONE, MaterialShades.NONE, MaterialVariant.NONE);

   private final String icon;
   private final MaterialColors color;
   private final MaterialShades lightShade;
   private final MaterialShades darkShade;
   private final MaterialVariant variant;

   public ArtifactTypeIcon(String icon) {
      this.icon = icon;
      this.color = MaterialColors.NONE;
      this.lightShade = MaterialShades.NONE;
      this.darkShade = MaterialShades.NONE;
      this.variant = MaterialVariant.NONE;
   }

   public ArtifactTypeIcon(String icon, MaterialColors color, MaterialShades shade) {
      this(icon, color, shade, shade, MaterialVariant.NONE);
   }

   public ArtifactTypeIcon(String icon, MaterialColors color, MaterialShades shade, MaterialVariant variant) {
      this(icon, color, shade, shade, variant);
   }

   public ArtifactTypeIcon(String icon, MaterialColors color, MaterialShades lightShade, MaterialShades darkShade, MaterialVariant variant) {
      this.icon = icon;
      this.color = color;
      this.lightShade = lightShade;
      this.darkShade = darkShade;
      this.variant = variant;
   }

   public String getIcon() {
      return this.icon;
   }

   public String getColor() {
      return this.color.getValue();
   }

   public String getLightShade() {
      return this.lightShade.getValue();
   }

   public String getDarkShade() {
      return this.darkShade.getValue();
   }

   public String getVariant() {
      return this.variant.getValue();
   }
}
