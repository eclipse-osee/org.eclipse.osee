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
package org.eclipse.osee.framework.manager.servlet.data;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.management.StandardOptions;

/**
 * This class is responsible for parsing servlet resquests into object[]
 * http://localhost:8089/resource?param1=blah&param2=blah
 * 
 * @author Roberto E. Escobar
 */
public class HttpRequestDecoder {

   private static final String CHECK_AVAILABLE = "check.available";
   private static final String URI = "uri";
   private static final String PROTOCOL = "protocol";
   private static final String SEED = "seed";
   private static final String NAME = "name";
   private static final String EXTENSION = "extension";

   // Whether data should be compressed
   private static final String COMPRESS_ON_ACQUIRE = "compress.before.sending";
   private static final String COMPRESS_ON_SAVE = "compress.before.saving";

   // Whether data should be decompressed
   private static final String DECOMPRESS_ON_ACQUIRE = "decompress.before.sending";

   // Whether data has already been compressed
   private static final String IS_COMPRESSED = "is.compressed";

   private static final String IS_OVERWRITE_ALLOWED = "is.overwrite.allowed";

   private HttpRequestDecoder() {
   }

   public static String[] fromPutRequest(HttpServletRequest request) {
      List<String> toReturn = new ArrayList<String>();
      toReturn.add(request.getParameter(PROTOCOL));
      toReturn.add(request.getParameter(SEED));
      StringBuilder builder = new StringBuilder();
      builder.append(request.getParameter(NAME));
      String extension = request.getParameter(EXTENSION);
      if (extension != null && extension.length() > 0) {
         builder.append(".");
         builder.append(extension);
      }
      toReturn.add(builder.toString());
      return toReturn.toArray(new String[toReturn.size()]);
   }

   public static Pair<String, Boolean> fromGetRequest(HttpServletRequest request) {
      return new Pair<String, Boolean>(request.getParameter(URI),
            Boolean.valueOf(request.getParameter(CHECK_AVAILABLE)));
   }

   public static Options getOptions(HttpServletRequest request) {
      Options options = new Options();
      options.put(StandardOptions.CompressOnSave.name(), request.getParameter(COMPRESS_ON_SAVE));
      options.put(StandardOptions.CompressOnAcquire.name(), request.getParameter(COMPRESS_ON_ACQUIRE));
      options.put(StandardOptions.DecompressOnAquire.name(), request.getParameter(DECOMPRESS_ON_ACQUIRE));
      options.put(StandardOptions.Overwrite.name(), request.getParameter(IS_OVERWRITE_ALLOWED));
      return options;
   }

   protected static boolean isDataCompressed(HttpServletRequest request) {
      return new Boolean(request.getParameter(IS_COMPRESSED));
   }

   public static String fromDeleteRequest(HttpServletRequest request) {
      return request.getParameter(URI);
   }

}
