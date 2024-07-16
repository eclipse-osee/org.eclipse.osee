/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.jaxrs.mvc;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractViewResolver<T> implements ViewResolver<T> {

   protected Charset getDefaultEncoding() {
      return Strings.UTF_8;
   }

   protected Charset computeEncoding(MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
      Charset defaultEncoding) {
      return JaxRsMvcUtils.computeEncoding(mediaType, httpHeaders, defaultEncoding);
   }

   @Override
   public final void write(ViewModel model, T view, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
      OutputStream output) throws IOException {
      Charset defaultEncoding = getDefaultEncoding();
      Charset encoding = computeEncoding(mediaType, httpHeaders, defaultEncoding);
      write(model, view, mediaType, httpHeaders, output, encoding);
   }

   public abstract void write(ViewModel model, T view, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
      OutputStream output, Charset encoding) throws IOException;

}