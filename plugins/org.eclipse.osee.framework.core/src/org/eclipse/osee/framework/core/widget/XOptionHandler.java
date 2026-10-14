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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Donald G. Dunne
 */
public class XOptionHandler {

   private final Set<XOption> xOptions = new HashSet<>();

   public XOptionHandler(XOption... xOption) {
      XOption.ensureLoaded();
      set(xOption);
   }

   public void add(XOption xOption) {
      // Remove the opposing options
      xOptions.removeAll(xOption.getRemoveOpts());
      // Add this option
      xOptions.add(xOption);
   }

   public void add(XOption... xOption) {
      for (XOption xOpt : xOption) {
         add(xOpt);
      }
   }

   public void remove(XOption... xOption) {
      for (XOption xOpt : xOption) {
         remove(xOpt);
      }
   }

   public void add(Collection<XOption> xOptions) {
      // Use add so the remove / add options happen
      for (XOption opt : xOptions) {
         add(opt);
      }
   }

   public boolean contains(XOption xOption) {
      return xOptions.contains(xOption);
   }

   // Do not getXOptions and addAll, adds must go through add method
   public Set<XOption> getXOptions() {
      return xOptions;
   }

   /**
    * Must go through the add method to ensure values set properly
    */
   public void set(Set<XOption> options) {
      xOptions.clear();
      add(options);
   }

   /**
    * Must go through the add method to ensure values set properly
    */
   public void set(XOption[] options) {
      xOptions.clear();
      add(options);
   }

   @Override
   public String toString() {
      return String.valueOf(xOptions);
   }

   public void remove(XOption option) {
      xOptions.remove(option);
   }

   public boolean is(XOption option) {
      return contains(option);
   }

   /**
    * Run as a java application to see the XOption and their opposites
    */
   public static void main(String[] args) {
      new XOptionHandler(); // This ensures all is loaded

      for (XOption opt : XOption.values()) {
         System.out.println(String.format("XOption [%s] - Opposite(s) [%s]", opt, opt.getRemoveOpts()));
      }
   }

   public boolean hasValOption() {
      return contains(XOption.VAL_MULTI_SEL) || contains(XOption.VAL_SINGLE_SEL) //
         || contains(XOption.VAL_MULTI_TEXT) || contains(XOption.VAL_SINGLE_TEXT);
   }

   public XOption getValOption() {
      if (contains(XOption.VAL_MULTI_SEL)) {
         return XOption.VAL_MULTI_SEL;
      }
      if (contains(XOption.VAL_SINGLE_SEL)) {
         return XOption.VAL_SINGLE_SEL;
      }
      if (contains(XOption.VAL_MULTI_TEXT)) {
         return XOption.VAL_MULTI_TEXT;
      }
      if (contains(XOption.VAL_SINGLE_TEXT)) {
         return XOption.VAL_SINGLE_TEXT;
      }
      return null;
   }
}
