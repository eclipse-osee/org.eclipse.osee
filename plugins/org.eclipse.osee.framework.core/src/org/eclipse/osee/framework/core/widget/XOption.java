/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.widget;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import org.eclipse.osee.framework.core.data.Multiplicity;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 * @author Karol M. Wilk
 */
public enum XOption {

   //////////////////////
   // Singleton options don't require others to remove them
   //////////////////////

   NONE(),

   VALIDATE_DATE(),

   FORMAL_REV_REQ(), // required for formal review

   ENABLE_ALL(),

   WALKTHROUGH(),

   BUTTON_HYPER(),
   BUTTON_PUSH(),

   //////////////////////
   // Options with 2 are set to remove the opposite, this sets it up in the constructor of the 2nd
   //////////////////////

   SAVE(),
   NO_SAVE(SAVE),

   REQUIRED(),
   NO_REQUIRED(REQUIRED),

   RFC(), // Required for Completion
   NOT_RFC(RFC),

   ENABLED(),
   NOT_ENABLED(ENABLED),

   WRITE(),
   READ(WRITE),

   INLINE_EDIT(),
   NO_INLINE_EDIT(INLINE_EDIT),

   BORDER(),
   NO_BORDER(BORDER),

   FUTURE_DATE_REQ,
   NO_FUTURE_DATE_REQ(FUTURE_DATE_REQ),

   LRFT, // Lead required
   NO_LRFT(LRFT),

   HORZ_LABEL(),
   VERT_LABEL(HORZ_LABEL),

   CLEAR(),
   NO_CLEAR(CLEAR),

   COLON(), // Default
   NO_COLON(COLON),

   //////////////////////
   // Options with more than 2, have to set removals below
   //////////////////////

   VAL_MULTI_TEXT(), // Multi-line Text - eg: Description
   VAL_SINGLE_TEXT(), // Single-line Text - eg: Title
   VAL_MULTI_SEL(), // Multi-Selection from Item List - eg: Subsystem
   VAL_SINGLE_SEL(), // Single-Selection from Item List - eg: Targeted Version
   VAL_NOT_APPLICABLE(), // Not Applicable; Selection handled independently or Composite widget - eg: XXBranchAndViewSelWidget
   VAL_NO_SEL(), // No Selection Allowed - eg: Git Commit Ids or XXLabelWidget

   LABEL_AFTER(),
   LABEL_BEFORE(), // Default
   NO_LABEL(),
   NO_VALUE(), // Do not create a value control (label-only render); for widgets that are sometimes label, sometimes label+value

   SORT_ASC(), // Default
   SORT_DES(),
   NO_SORT(),

   COMP_4(),
   COMP_6(),
   COMP_8(),
   COMP_10(),
   GCOMP_4(),
   GCOMP_6(),
   GCOMP_8(),
   GCOMP_10(),
   COMP_END(),

   FILL_BOTH(),
   FILL_HORZ(),
   FILL_VERT(),
   NO_FILL(FILL_BOTH, FILL_HORZ, FILL_VERT),
   NO_FILL_VERT(FILL_HORZ, FILL_BOTH),
   NO_FILL_HORZ(FILL_VERT, FILL_BOTH),

   LABEL_RED(),
   LABEL_YELLOW(),
   LABEL_BLUE(),

   ALIGN_LEFT(),
   ALIGN_RIGHT(),
   ALIGN_CENTER(),

   ACTIVE(),
   NO_ACTIVE(),
   ACTIVE_BOTH(),

   MULTI_ANY(), // 0..n
   MULTI_ONE(), // 1
   MULTI_ZERO_ONE(), // 0,1
   MULTI_LEAST_ONE(), // 1..n

   LABEL_BOLD, // Default for labels
   NO_LABEL_BOLD(LABEL_BOLD),

   SPELL,
   NO_SPELL(SPELL), //

   VALUE_HYPER,

   ICON_WIDGET,
   VALUE_BORDER,
   VALUE_TOOLTIP,
   VALUE_WHITE,
   VALUE_WRAP,
   WRAP_VALUES;

   public static EnumSet<XOption> enumSet = EnumSet.allOf(XOption.class);
   private final List<XOption> oppositeOpts;
   public static boolean loaded = false;

   XOption(XOption... removeOptions) {

      // Options with 2, each have the other as their removal
      oppositeOpts = Collections.asList(removeOptions);
      for (XOption oppositeOpt : removeOptions) {
         oppositeOpt.addOppositeOpt(this);
      }

      // Options with > 2 have to be setup after enum instantiations, see ensureLoaded below

      // Run XOptionHandler which will print the opposites for review

   }

   public static List<XOption> getRequiredValOptions() {
      return Arrays.asList(VAL_NO_SEL, VAL_MULTI_SEL, VAL_SINGLE_SEL, VAL_MULTI_TEXT, VAL_SINGLE_TEXT,
         VAL_NOT_APPLICABLE);
   }

   public static void ensureLoaded() {
      if (!loaded) {
         setupSet(VAL_NO_SEL, VAL_MULTI_SEL, VAL_SINGLE_SEL, VAL_MULTI_TEXT, VAL_SINGLE_TEXT, VAL_NOT_APPLICABLE);
         setupSet(LABEL_AFTER, LABEL_BEFORE, NO_LABEL);
         setupSet(SORT_ASC, SORT_DES, NO_SORT);
         setupSet(COMP_4, COMP_6, COMP_8, COMP_10);
         setupSet(GCOMP_4, GCOMP_6, GCOMP_8, GCOMP_10);
         setupSet(ALIGN_CENTER, ALIGN_LEFT, ALIGN_RIGHT);
         setupSet(ACTIVE, NO_ACTIVE, ACTIVE_BOTH);
         setupSet(MULTI_ANY, MULTI_LEAST_ONE, MULTI_ONE, MULTI_ZERO_ONE);

         FILL_BOTH.addOppositeOpt(NO_FILL, NO_FILL_HORZ, NO_FILL_VERT);
         FILL_HORZ.addOppositeOpt(NO_FILL, NO_FILL_HORZ);
         FILL_VERT.addOppositeOpt(NO_FILL, NO_FILL_VERT);
         loaded = true;
      }
   }

   /**
    * For each item in set, add the others as opposites
    */
   private static void setupSet(XOption... opts) {
      List<XOption> set = Collections.asList(opts);
      for (XOption opt : set) {
         for (XOption opt2 : set) {
            if (opt != opt2) {
               opt.addOppositeOpt(opt2);
            }
         }
      }
   }

   void addOppositeOpt(XOption... oppositeOpt) {
      for (XOption opt : oppositeOpt) {
         oppositeOpts.add(opt);
      }
   }

   public static XOption multiplicityToXOption(Multiplicity multiplicity) {
      XOption option = XOption.NONE;
      if (multiplicity == Multiplicity.ANY) {
         option = XOption.MULTI_ANY;
      } else if (multiplicity == Multiplicity.ZERO_OR_ONE) {
         option = XOption.MULTI_ZERO_ONE;
      } else if (multiplicity == Multiplicity.EXACTLY_ONE) {
         option = XOption.MULTI_ONE;
      } else if (multiplicity == Multiplicity.AT_LEAST_ONE) {
         option = XOption.MULTI_LEAST_ONE;
      }
      return option;
   }

   public List<XOption> getRemoveOpts() {
      return oppositeOpts;
   }
}
