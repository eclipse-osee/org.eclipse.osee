/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.plugin.util;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.result.XConsoleLogger;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class ViewPartUtil {

   /**
    * Attempts to start up the ViewPart using an integer as the unique identifier for the secondary ID. This secondary
    * ID will be the smallest integer not currently being used as a secondary ID for this view type, beginning with 1.
    *
    * @param viewID The Canonical name to the ViewPart class to be started.
    * @return the integer used as the secondary ID
    */
   public static int startMultiView(String viewID) {
      /*
       * ViewParts have a primary ID (which identifies the type of the View) and a secondary ID (which allows us to have
       * multiple Views of the same type running concurrently. Since the secondary ID must be unique, we need to find an
       * ID that isn't being used currently. Also, its nice to display this to the user so they can keep the views
       * straight. So, we've opted to simply use an integer number for this purpose. Therefore, to startup another
       * instnace of this view, we want to find a number which isn't currently being used, and use that. The lower
       * numbers are preferable from a useability standpoint, and we'll start counting at 1. If a view is closed, we
       * should first re-use its number before going on to higher numbers. For example, if views with secondary IDs 1,
       * and 3 are currently running, the next instance of the view should be 2.
       */

      SortedSet<Integer> set = new TreeSet<>();
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      IViewReference[] viewRefs = page.getViewReferences();
      for (IViewReference viewRef : viewRefs) {
         if (viewRef.getId().equals(viewID)) {
            try {
               set.add(new Integer(viewRef.getSecondaryId()));
            } catch (NumberFormatException ex) {
               ex.printStackTrace();
            }
         }
      }

      // Find the next available integer number
      Iterator<Integer> iter = set.iterator();
      boolean found = false;
      int secondaryId = 1;
      while (iter.hasNext() && !found) {
         if (iter.next().intValue() != secondaryId) {
            found = true;
         } else {
            secondaryId++;
         }
      }
      try {
         page.showView(viewID, Integer.toString(secondaryId), IWorkbenchPage.VIEW_ACTIVATE);
      } catch (PartInitException ex) {
         XConsoleLogger.err("COULD NOT FIND " + viewID + ", with ID # = " + secondaryId);
      }

      return secondaryId;
   }

   public static IViewPart openOrShowView(String viewId) {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      try {
         return page.showView(viewId);
      } catch (PartInitException e1) {
         MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Launch Error",
            "Couldn't Launch " + viewId + "\n\n" + e1.getMessage());
      }
      return null;
   }

}
