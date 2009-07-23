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
package org.eclipse.osee.ote.ui.markers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkspace;
import org.eclipse.osee.ote.core.framework.saxparse.elements.StacktraceData;

/**
 * @author Andrew M. Finkbeiner
 */
public class OteMarkerHelper {

   private List<MarkerInfo> markerInfo;
   private List<IMarker> markersToDelete;
   private Map<CheckPointHelper, CheckPointHelper> count = new HashMap<CheckPointHelper, CheckPointHelper>();
   private StringBuilder builder = new StringBuilder();
   private List<TestPointData> testPonitDatas;

   /**
    * @param testPointDatas
    */
   public OteMarkerHelper(List<TestPointData> testPointDatas) {
      this.testPonitDatas = testPointDatas;
      markerInfo = new ArrayList<MarkerInfo>();
      markersToDelete = new ArrayList<IMarker>();

      doWork();
   }

   private void doWork() {
      for (TestPointData data : testPonitDatas) {
         String description = getDescription(data.getCheckPointData());
         String number = data.getNumber();
         int num = Integer.parseInt(number);
         String details = String.format("#%03d %s", num, description);

         for (StacktraceData stackLocation : data.getStacktraceCollection().getStackTrace()) {
            String file = stackLocation.getSource();
            String line = stackLocation.getLine();
            //pull out the java file name
            file = file.substring(file.lastIndexOf(".") + 1);
            int innerMarker = file.indexOf("$");
            if (innerMarker > 0) {
               file = file.substring(0, file.indexOf("$"));
            }
            file += ".java";
            int linenumber = Integer.parseInt(line);

            markerInfo.add(new MarkerInfo(file, linenumber, details));
         }
      }
      finish();
   }

   private String getDescription(List<CheckPointData> datas) {
      count.clear();
      for (CheckPointData data : datas) {
         CheckPointHelper check = new CheckPointHelper(data);
         if (count.containsKey(check)) {
            count.get(check).increment();
         } else {
            count.put(check, check);
         }
      }

      CheckPointHelper[] helper = count.values().toArray(new CheckPointHelper[count.values().size()]);
      Arrays.sort(helper);
      int maxNumber = 2;
      builder.setLength(0);
      for (int i = 0; i < helper.length && i < maxNumber; i++) {
         builder.append(helper[i].toString());
         if ((i + 1) < Math.min(helper.length, maxNumber)) {
            builder.append(", ");
         }
         if ((i + 1) < helper.length && (i + 1) >= maxNumber) {
            builder.append("...");
         }
      }
      return builder.toString();
   }

   private void finish() {

      Set<String> fileAlreadyLookedFor = new HashSet<String>();
      Map<String, IResource> resources = new HashMap<String, IResource>();

      for (MarkerInfo marker : markerInfo) {
         try {
            if (!fileAlreadyLookedFor.contains(marker.getFile())) {
               fileAlreadyLookedFor.add(marker.getFile());
               IResource resource = AWorkspace.findWorkspaceFile(marker.getFile());
               if (resource != null) {
                  resources.put(marker.getFile(), resource);
               }
            }

            IResource resourceToMark = resources.get(marker.getFile());
            if (resourceToMark != null) {
               Map<String, Object> scriptMarkerMap = new HashMap<String, Object>();
               scriptMarkerMap.put(IMarker.MESSAGE, marker.getMessage());
               scriptMarkerMap.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_INFO));
               scriptMarkerMap.put(IMarker.LINE_NUMBER, marker.getLine());
               IMarker markerToDelete = resourceToMark.createMarker("org.eclipse.osee.ote.ui.output.errorMarker");
               markerToDelete.setAttributes(scriptMarkerMap);
               markersToDelete.add(markerToDelete);
            }
         } catch (Exception ex) {
            OseeLog.log(MarkerPlugin.class, Level.SEVERE, "Error adding markers from outfile", ex);
         }
      }
   }

   public List<IMarker> getMarkers() {
      return markersToDelete;
   }

}
