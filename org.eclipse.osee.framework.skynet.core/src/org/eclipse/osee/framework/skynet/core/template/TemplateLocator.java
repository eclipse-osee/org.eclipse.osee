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
package org.eclipse.osee.framework.skynet.core.template;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class TemplateLocator {
   private String bundleName;
   private String templateName;
   private String templatePath;

   public TemplateLocator(String bundleName, String templateName, String templatePath) {
      this.bundleName = bundleName;
      this.templateName = templateName;
      this.templatePath = templatePath;
   }

   public String getBundleName() {
      return bundleName;
   }

   public String getTemplateName() {
      return templateName;
   }

   public String getTemplatePath() {
      return templatePath;
   }

   public URL getLocationURL() {
      Bundle bundle = Platform.getBundle(getBundleName());
      return bundle.getEntry(getTemplatePath());
   }

   public String getTemplate() throws IOException {
      InputStream inputStream = getLocationURL().openStream();
      return Lib.inputStreamToString(inputStream);
   }
}
