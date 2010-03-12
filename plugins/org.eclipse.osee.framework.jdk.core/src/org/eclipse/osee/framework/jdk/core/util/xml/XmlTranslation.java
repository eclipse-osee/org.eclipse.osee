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
package org.eclipse.osee.framework.jdk.core.util.xml;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.nio.CharBuffer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @author Ryan D. Brooks
 */
public class XmlTranslation {
   /**
    * applies the xslFilename to inFilename and writes the output to outFilename.
    */
   public static CharBuffer applyXsl(CharBuffer xmlBuffer, CharBuffer xslBuffer, URIResolver uriResolver) {
      return applyXsl(new StreamSource(new CharArrayReader(xmlBuffer.array())), xslBuffer, uriResolver);
   }

   /**
    * applies the xslFilename to inFilename and writes the output to outFilename.
    */
   public static CharBuffer applyXsl(Source source, CharBuffer xslBuffer, URIResolver uriResolver) {
      try {
         TransformerFactory factory = TransformerFactory.newInstance();
         factory.setURIResolver(uriResolver);

         Templates template = factory.newTemplates(new StreamSource(new CharArrayReader(xslBuffer.array())));
         Transformer xformer = template.newTransformer();

         CharArrayWriter resultWriter = new CharArrayWriter(10000);
         Result result = new StreamResult(resultWriter);

         xformer.transform(source, result);
         return CharBuffer.wrap(resultWriter.toCharArray());
      } catch (TransformerConfigurationException ex) {
         ex.printStackTrace();
      } catch (TransformerException ex) {
         ex.printStackTrace();
      }
      return null;
   }
}
