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
package org.eclipse.osee.define.artifact;

import java.io.CharArrayReader;
import java.nio.CharBuffer;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;

public class Resolver implements URIResolver {
   private static final OseeUiActivator plugin = DefinePlugin.getInstance();
   private static final CharBuffer xslAuxiliary = plugin.getCharBuffer("support/xslt/auxiliary.xsl");
   private static final CharBuffer xslProperties = plugin.getCharBuffer("support/xslt/elementProperties.xsl");
   private static final CharBuffer xslStructure = plugin.getCharBuffer("support/xslt/elementStructure.xsl");
   private static final CharBuffer xslPageLayout = plugin.getCharBuffer("support/xslt/pageLayout.xsl");
   private static final CharBuffer xslProfile = plugin.getCharBuffer("support/xslt/profile.xsl");

   public Source resolve(String href, String base) throws TransformerException {
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
      return new StreamSource(new CharArrayReader(rightOne.array()));
   }
}