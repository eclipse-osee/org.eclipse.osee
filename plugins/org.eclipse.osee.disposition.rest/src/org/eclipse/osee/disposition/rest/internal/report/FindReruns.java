/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal.report;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.disposition.model.DispoAnnotationData;

/**
 * @author Megumi Telles
 */
public class FindReruns {
   Pattern removeLastDot = Pattern.compile("[^\\.]([^.]*)$", Pattern.CASE_INSENSITIVE);

   public HashMap<String, String> createList(List<DispoAnnotationData> annotations) {
      HashMap<String, String> reruns = new HashMap<>();
      for (DispoAnnotationData data : annotations) {
         String name = "", path = "", comment = "";
         String resolution = data.getResolution();
         if (!resolution.isEmpty()) {
            String[] split = resolution.split("___");
            if (split.length > 1) {
               path = split[0];
               comment = split[1];
            } else {
               path = split.toString();
            }
            path = path.replaceFirst("results", "");
            Matcher matcher = removeLastDot.matcher(path);
            while (matcher.find()) {
               name = matcher.group() + ".java";
            }
            path = path.replaceAll("\\.", "/");
         }
         reruns.put(name, path);
      }
      return reruns;
   }
}
