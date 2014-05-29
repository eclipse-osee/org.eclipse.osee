/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAttribute;
import org.eclipse.osee.ats.core.internal.AtsCoreService;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.AttributeId;

/**
 * @author Donald G. Dunne
 */
public class AtsUtilCore {

   public final static double DEFAULT_HOURS_PER_WORK_DAY = 8;
   private static IOseeBranch commonBranch = null;

   public synchronized static IOseeBranch getAtsBranch() {
      if (commonBranch == null) {
         commonBranch = CoreBranches.COMMON;
         String atsBranchUuid = System.getProperty("ats.branch");
         if (Strings.isValid(atsBranchUuid)) {
            try {
               Long branchUuid = Long.valueOf(atsBranchUuid);
               boolean hasPermission = AtsCoreService.getUserService().currentUserHasAccessToAtsBranch(branchUuid);
               if (!hasPermission) {
                  OseeLog.logf(AtsUtilCore.class, Level.SEVERE,
                     "User configured for ATS Branch %s, but has no read access; falling back to Common", atsBranchUuid);
               } else {
                  commonBranch = TokenFactory.createBranch(branchUuid, "ATS Branch");
               }
            } catch (Exception ex) {
               OseeLog.log(AtsUtilCore.class, Level.SEVERE, "Error processisng ATS Branch config permissions", ex);
            }
         }
      }
      return commonBranch;
   }

   public static boolean isInTest() {
      return Boolean.valueOf(System.getProperty("osee.isInTest"));
   }

   public static String doubleToI18nString(double d) {
      return doubleToI18nString(d, false);
   }

   public static String doubleToI18nString(double d, boolean blankIfZero) {
      if (blankIfZero && d == 0) {
         return "";
      }
      // This enables java to use same string for all 0 cases instead of creating new one
      else if (d == 0) {
         return "0.00";
      } else {
         return String.format("%4.2f", d);
      }
   }

   public static ArtifactId toArtifactId(IAtsWorkItem workItem) {
      return new ArtifactIdWrapper(workItem);
   }

   public static AttributeId toAttributeId(IAttribute<?> attr) {
      return new AttributeIdWrapper(attr);
   }
}
