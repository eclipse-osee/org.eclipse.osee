/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public enum FrameworkImage implements KeyedImage {

   ADD_GREEN(CoreImage.ADD_GREEN),
   ATTRIBUTE_MOLECULE(CoreImage.ATTRIBUTE_MOLECULE),
   BRANCH_CHANGE(CoreImage.BRANCH_CHANGE),
   BRANCH_VIEW(CoreImage.BRANCH_VIEW),
   COMPARE_HEAD_TX(CoreImage.COMPARE_HEAD_TX),
   COMPARE_PARENT_BRANCH(CoreImage.COMPARE_PARENT_BRANCH),
   DB_ICON_BLUE(CoreImage.DB_ICON_BLUE),
   DELETE(CoreImage.DELETE),
   DELTAS(CoreImage.DELTAS),
   DELTAS_BASE_TO_HEAD_TXS(CoreImage.DELTAS_BASE_TO_HEAD_TXS),
   DELTAS_DIFFERENT_BRANCHES(CoreImage.DELTAS_DIFFERENT_BRANCHES),
   DELTAS_DIFFERENT_BRANCHES_WITH_MERGE(CoreImage.DELTAS_DIFFERENT_BRANCHES_WITH_MERGE),
   DELTAS_TXS_SAME_BRANCH(CoreImage.DELTAS_TXS_SAME_BRANCH),
   DOCUMENT(CoreImage.DOCUMENT),
   EMAIL(CoreImage.EMAIL),
   FEATURE(CoreImage.FEATURE),
   GEAR(CoreImage.GEAR),
   GROUP(CoreImage.GROUP),
   HEADING(CoreImage.HEADING),
   IMPLEMENTATION_DETAILS(CoreImage.IMPLEMENTATION_DETAILS),
   IMPLEMENTATION_DETAILS_DATA_DEFINITION(CoreImage.IMPLEMENTATION_DETAILS_DATA_DEFINITION),
   IMPLEMENTATION_DETAILS_DRAWING(CoreImage.IMPLEMENTATION_DETAILS_DRAWING),
   IMPLEMENTATION_DETAILS_FUNCTION(CoreImage.IMPLEMENTATION_DETAILS_FUNCTION),
   IMPLEMENTATION_DETAILS_PROCEDURE(CoreImage.IMPLEMENTATION_DETAILS_PROCEDURE),
   LASER(CoreImage.LASER),
   LOCKED_NO_ACCESS(CoreImage.LOCKED_NO_ACCESS),
   LOCKED_WITH_ACCESS(CoreImage.LOCKED_WITH_ACCESS),
   LOCK_OVERLAY(CoreImage.LOCK_OVERLAY),
   OPEN(CoreImage.OPEN),
   PURGE(CoreImage.PURGE),
   PURPLE(CoreImage.PURPLE),
   QUESTION(CoreImage.QUESTION),
   RELATION(CoreImage.RELATION),
   ROOT_HIERARCHY(CoreImage.ROOT_HIERARCHY),
   SOFTWARE_REQUIERMENT_DRAWING(CoreImage.SOFTWARE_REQUIERMENT_DRAWING),
   SOFTWARE_REQUIERMENT_FUNCTION(CoreImage.SOFTWARE_REQUIERMENT_FUNCTION),
   SOFTWARE_REQUIERMENT_PROCEDURE(CoreImage.SOFTWARE_REQUIERMENT_PROCEDURE),
   SOFTWARE_REQUIREMENT_DATA_DEFINITION(CoreImage.SOFTWARE_REQUIREMENT_DATA_DEFINITION),
   TUPLE(CoreImage.TUPLE),
   USER(CoreImage.USER),
   USERS(CoreImage.USERS),
   USER_GREY(CoreImage.USER_GREY),
   USER_RED(CoreImage.USER_RED),
   USER_YELLOW(CoreImage.USER_YELLOW),
   hardware_requirement(CoreImage.hardware_requirement),
   software_design(CoreImage.software_design),
   software_requirement(CoreImage.software_requirement),
   subsystem_design(CoreImage.subsystem_design),
   subsystem_requirement(CoreImage.subsystem_requirement),
   system_requirement(CoreImage.system_requirement);

   private final String fileName;
   private final KeyedImage alias;

   private FrameworkImage(CoreImage image) {
      this.fileName = image.getName();
      this.alias = null;
   }

   private FrameworkImage(String fileName) {
      this.fileName = fileName;
      this.alias = null;
   }

   private FrameworkImage(KeyedImage alias) {
      this.fileName = alias.getImageKey();
      this.alias = alias;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      if (alias == null) {
         return ImageManager.createImageDescriptor(Activator.PLUGIN_ID, fileName);
      }
      return alias.createImageDescriptor();
   }

   @Override
   public String getImageKey() {
      if (alias == null) {
         return Activator.PLUGIN_ID + "." + fileName;
      }
      return alias.getImageKey();
   }
}