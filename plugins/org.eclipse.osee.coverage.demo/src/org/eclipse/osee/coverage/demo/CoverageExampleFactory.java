/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.demo;

import java.net.URL;
import org.eclipse.osee.coverage.ICoverageImporter;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Roberto E. Escobar
 */
public final class CoverageExampleFactory {

   private static final String OPERATION_ELEMENT = "Operation";
   private static final String CLASSNAME_ATTRIBUTE = "className";
   private static final String EXTENSION_POINT_ID = "org.eclipse.osee.framework.ui.skynet.BlamOperation";

   private static final ExtensionDefinedObjects<ICoverageImporter> edo =
      new ExtensionDefinedObjects<ICoverageImporter>(EXTENSION_POINT_ID, OPERATION_ELEMENT, CLASSNAME_ATTRIBUTE);

   private CoverageExampleFactory() {
      // Static Factory
   }

   public static ICoverageImporter createExample(CoverageExamples exampleType) throws OseeCoreException {
      ICoverageImporter coverageBlam = edo.getObjectById(exampleType.getExtensionId());
      Conditions.checkNotNull(coverageBlam, "coverageBlam", "Unable to create [%s]", exampleType);
      return coverageBlam;
   }

   public static URL getCoverageSource(Class<?> clazz, String filename) {
      String packageName = clazz.getPackage().getName();
      StringBuilder builder = new StringBuilder("src/");
      for (String token : packageName.split("\\.")) {
         builder.append(token);
         builder.append("/");
      }
      builder.append(filename);
      String resourceName = builder.toString();

      Bundle bundle = FrameworkUtil.getBundle(clazz);
      URL url = bundle.getEntry(resourceName);
      return url;
   }
}
