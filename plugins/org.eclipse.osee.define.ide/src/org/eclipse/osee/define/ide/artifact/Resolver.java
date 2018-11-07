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
package org.eclipse.osee.define.ide.artifact;

import java.io.CharArrayReader;
import java.io.IOException;
import java.nio.CharBuffer;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.osee.define.ide.internal.Activator;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.PluginUtil;

/**
 * @author Ryan D. Brooks
 */
public class Resolver implements URIResolver {
   private static final PluginUtil plugin = new PluginUtil(Activator.PLUGIN_ID);
   private static final CharBuffer xslAuxiliary = getResource("support/xslt/auxiliary.xsl");
   private static final CharBuffer xslProperties = getResource("support/xslt/elementProperties.xsl");
   private static final CharBuffer xslStructure = getResource("support/xslt/elementStructure.xsl");
   private static final CharBuffer xslPageLayout = getResource("support/xslt/pageLayout.xsl");
   private static final CharBuffer xslProfile = getResource("support/xslt/profile.xsl");

   private static CharBuffer getResource(String resource) {
      try {
         return Lib.inputStreamToCharBuffer(plugin.getInputStream(resource));
      } catch (IOException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return null;
      }
   }

   @Override
   public Source resolve(String href, String base) {
      CharBuffer rightOne = null;
      if (href.equals("auxiliary.xsl")) {
         rightOne = xslAuxiliary;
      }
      if (href.equals("elementProperties.xsl")) {
         rightOne = xslProperties;
      }
      if (href.equals("elementStructure.xsl")) {
         rightOne = xslStructure;
      }
      if (href.equals("pageLayout.xsl")) {
         rightOne = xslPageLayout;
      }
      if (href.equals("profile.xsl")) {
         rightOne = xslProfile;
      }
      if (rightOne != null) {
         return new StreamSource(new CharArrayReader(rightOne.array()));
      }
      return null;
   }
}
