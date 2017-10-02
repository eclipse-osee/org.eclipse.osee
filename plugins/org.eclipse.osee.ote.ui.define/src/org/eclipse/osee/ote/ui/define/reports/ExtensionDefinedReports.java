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
package org.eclipse.osee.ote.ui.define.reports;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.ote.ui.define.OteUiDefinePlugin;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class ExtensionDefinedReports {
   private static final String EXTENSION_ID = "ITestRunReport";
   private final Map<String, ReportData> reportMap;
   private Map<String, Pair<String, String>> idsAndNameMap;

   private static ExtensionDefinedReports instance = null;

   private ExtensionDefinedReports() {
      this.reportMap = new HashMap<>();
      loadReports();
   }

   public static ExtensionDefinedReports getInstance() {
      if (instance == null) {
         instance = new ExtensionDefinedReports();
      }
      return instance;
   }

   @SuppressWarnings("unchecked")
   public Pair<String, String>[] getIdsAndNames() {
      if (idsAndNameMap == null) {
         this.idsAndNameMap = new HashMap<>();
         Set<String> ids = reportMap.keySet();
         for (String id : ids) {
            ReportData data = reportMap.get(id);
            this.idsAndNameMap.put(id, new Pair<String, String>(id, data.getName()));
         }
      }
      return idsAndNameMap.values().toArray(new Pair[idsAndNameMap.size()]);
   }

   public Image getImage(String key) {
      ReportData data = reportMap.get(key);
      Image toReturn = data.getIcon();
      return toReturn != null ? toReturn : ImageManager.getImage(ImageManager.MISSING);
   }

   public ITestRunReport getReportGenerator(String key) {
      ReportData data = reportMap.get(key);
      return data != null ? data.getTestRunReport() : null;
   }

   public Pair<String, String> getIdAndName(String id) {
      return idsAndNameMap.get(id);
   }

   private void loadReports() {
      List<IConfigurationElement> elements =
         ExtensionPoints.getExtensionElements(OteUiDefinePlugin.getInstance(), EXTENSION_ID, EXTENSION_ID);
      for (IConfigurationElement element : elements) {
         IExtension extension = (IExtension) element.getParent();
         String identifier = extension.getUniqueIdentifier();
         String name = extension.getLabel();
         String className = element.getAttribute("classname");
         String iconName = element.getAttribute("icon");
         String bundleName = element.getContributor().getName();

         if (Strings.isValid(bundleName) && Strings.isValid(className)) {
            try {
               Bundle bundle = Platform.getBundle(bundleName);
               Class<?> taskClass = bundle.loadClass(className);
               ITestRunReport object = (ITestRunReport) taskClass.newInstance();
               if (object != null) {
                  URL url = bundle.getEntry(iconName);
                  ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);
                  reportMap.put(identifier, new ReportData(identifier, name, object, imageDescriptor.createImage()));
               }
            } catch (Exception ex) {
               OseeLog.logf(OteUiDefinePlugin.class, Level.SEVERE, ex, "Error loading report [%s]", className);
            }
         }
      }
   }

   private final class ReportData {
      private final String name;
      private final Image icon;
      private final ITestRunReport testRunReport;

      public ReportData(String id, String name, ITestRunReport testRunReport, Image icon) {
         super();
         this.name = name;
         this.icon = icon;
         this.testRunReport = testRunReport;
      }

      /**
       * @return the name
       */
      public String getName() {
         return name;
      }

      /**
       * @return the icon
       */
      public Image getIcon() {
         return icon;
      }

      /**
       * @return the testRunReport
       */
      public ITestRunReport getTestRunReport() {
         return testRunReport;
      }

   }
}
