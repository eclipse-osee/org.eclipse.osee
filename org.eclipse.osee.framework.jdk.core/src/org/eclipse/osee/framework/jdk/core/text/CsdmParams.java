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
package org.eclipse.osee.framework.jdk.core.text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Ryan D. Brooks
 */
public class CsdmParams {
   protected BufferedReader in;
   protected BufferedWriter out;

   public CsdmParams() {
      try {
         in = new BufferedReader(new InputStreamReader(System.in));
         out = new BufferedWriter(new FileWriter("csdm_params.csv"));
      } catch (IOException ex) {
         System.out.println(ex);
      }
   }

   protected void finalize() throws IOException {
      out.close();
      in.close();
   }

   public static void main(String args[]) throws IOException {
      CsdmParams gen = new CsdmParams();
      gen.genThreats();
      gen.finalize();
   }

   public void genThreats() throws IOException {
      for (int i = 0; i < 32; i++) {
         String numStr = Lib.padLeading(String.valueOf(i), '0', 2);
         out.write("THRT_LAT_" + numStr + ",IEEE64,19346,\n");
         out.write("THRT_LAT_Z_" + numStr + ",IEEE64,19347,\n");
         out.write("THRT_LONG_" + numStr + ",IEEE64,19346,\n");
         out.write("THRT_LONG_Z_" + numStr + ",IEEE64,19347,\n");
         out.write("THRT_DETECT_" + numStr + ",IEEE64,19346,\n");
         out.write("THRT_DETECT_Z_" + numStr + ",IEEE64,19347,\n");
         out.write("THRT_LETHAL_" + numStr + ",IEEE64,19346,\n");
         out.write("THRT_LETHAL_Z_" + numStr + ",IEEE64,19347,\n");
         out.write("THRT_HEIGHT_ABOVE_" + numStr + ",ANALOG,19154,-2_147_483_648 to 2_147_483_647\n");
         out.write("THRT_COUNT_" + numStr + ",ANALOG,5461,0 to 255\n");
         out.write("THRT_STAT_OK_" + numStr + ",ENUM,162,\"0=FALSE, 1=TRUE\"\n");
         out.write("SPARE,SPARE,0,\n");
      }
   }

   public void parseAda() throws IOException {
      String line = null;
      while ((line = in.readLine()) != null) {
         StringTokenizer strTok = new StringTokenizer(line, " .");
         if (strTok.countTokens() == 4) {
            String adaName = strTok.nextToken();
            out.write(adaName + ", ");
            out.write(strTok.nextToken() + ", ");
            out.write(strTok.nextToken() + ", ");
            out.write(strTok.nextToken() + "\n");
         }
      }
   }
}
