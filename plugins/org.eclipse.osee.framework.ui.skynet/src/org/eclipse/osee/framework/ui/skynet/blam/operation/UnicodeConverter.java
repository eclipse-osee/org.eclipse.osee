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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fw314c
 */
public class UnicodeConverter {

   private static final byte[] tickyQuoteBack =
         new byte[] {(byte) -61, (byte) -94, (byte) -30, (byte) -126, (byte) -84, (byte) -59, (byte) -109};
   private static final String tickyQuoteBackString = "“";
   private static final byte[] tickyQuoteBackGood = new byte[] {(byte) -30, (byte) -128, (byte) -100};

   //   private static final byte[] degree =
   //         new byte[] {(byte) -61, (byte) -94, (byte) -30, (byte) -126, (byte) -84, (byte) -59, (byte) -109};
   private static final String degreeString = "°";
   private static final byte[] degreeGood = new byte[] {(byte) -62, (byte) -80};

   private static final byte[] tickyQuoteForward =
         new byte[] {(byte) -61, (byte) -94, (byte) -30, (byte) -126, (byte) -84, (byte) -17, (byte) -65, (byte) -67};
   private static final String tickyQuoteForwardString = "”";
   private static final byte[] tickyQuoteForwardGood = new byte[] {(byte) -30, (byte) -128, (byte) -99};

   private static final byte[] notEquals =
         new byte[] {(byte) -61, (byte) -81, (byte) -30, (byte) -128, (byte) -102, (byte) -62, (byte) -71};
   private static final byte[] notEqualsGood = new byte[] {(byte) -17, (byte) -126, (byte) -71};
   private static final String notEqualsString = " ";

   //Renders as a ? in Java however appears as  equal slash in Word
   private static final byte[] notEqualsForward =
         new byte[] {(byte) -61, (byte) -94, (byte) -30, (byte) -128, (byte) -80, (byte) -62, (byte) -96};
   private static final String notEqualsForwardString = "Not Equals Forward";
   private static final byte[] notEqualsForwardGood = new byte[] {(byte) -30, (byte) -119, (byte) -96};

   private static final byte[] greaterThanEquals =
         new byte[] {(byte) -61, (byte) -81, (byte) -30, (byte) -128, (byte) -102, (byte) -62, (byte) -77};
   private static final String greaterThanEqualsString = " ";
   private static final byte[] greaterThanEqualsGood = new byte[] {(byte) -17, (byte) -126, (byte) -77};

   private static final byte[] lessThanEquals =
         new byte[] {(byte) -61, (byte) -81, (byte) -30, (byte) -128, (byte) -102, (byte) -62, (byte) -93};
   private static final String lessThanEqualsString = " Less Than Equals";
   private static final byte[] lessThanEqualsGood = new byte[] {(byte) -17, (byte) -126, (byte) -93};

   //   private static final byte[] plusEquals =
   //         new byte[] {(byte) -61, (byte) -81, (byte) -30, (byte) -128, (byte) -102, (byte) -62, (byte) -93};
   private static final byte[] plusEqualsGood = new byte[] {(byte) -50, (byte) -79};

   private static final byte[] blankCharacter = new byte[] {(byte) -61, (byte) -126, (byte) -62, (byte) -96};
   private static final byte[] blankCharacterGood = new byte[] {(byte) -62, (byte) -96};

   //   private static final byte[] microCharacter =
   //      new byte[] {(byte) -61, (byte) -81, (byte) -30, (byte) -128, (byte) -102, (byte) -62, (byte) -93};
   private static final byte[] microCharacterGood = new byte[] {(byte) -62, (byte) -75};

   private static final byte[] forwardTick =
         new byte[] {(byte) -61, (byte) -94, (byte) -30, (byte) -126, (byte) -84, (byte) -30, (byte) -124, (byte) -94};
   private static final String forwardTickString = "’";
   private static final byte[] forwardTickGood = new byte[] {(byte) -30, (byte) -128, (byte) -103};

   private static final byte[] dash =
         new byte[] {(byte) -61, (byte) -94, (byte) -30, (byte) -126, (byte) -84, (byte) -30, (byte) -128, (byte) -100};
   private static final byte[] dashGood = new byte[] {(byte) -30, (byte) -128, (byte) -109};

   private static final byte[] dotGood = new byte[] {(byte) -62, (byte) -73};
   private static final byte[] plusMinusGood = new byte[] {(byte) -62, (byte) -79};
   private static final byte[] miniOneGood = new byte[] {(byte) -62, (byte) -71};

   private static final byte[] whatisit =
         new byte[] {(byte) -61, (byte) -81, (byte) -30, (byte) -126, (byte) -84, (byte) -62, (byte) -67};
   private static final byte[] whatisitGood = new byte[] {(byte) -17, (byte) -128, (byte) -67};

   private static final byte[] WHATISIT2 = new byte[] {(byte) -17, (byte) -126, (byte) -80};
   private static final byte[] WHATISIT3 = new byte[] {(byte) -30, (byte) -119, (byte) -91};
   private static final byte[] WHATISIT4 = new byte[] {(byte) -49, (byte) -128};
   private static final byte[] WHATISIT5 = new byte[] {(byte) -30, (byte) -119, (byte) -92};

   private static final byte[] upsideDownTick =
         new byte[] {(byte) -61, (byte) -94, (byte) -30, (byte) -126, (byte) -84, (byte) -53, (byte) -100};
   private static final byte[] upsideDownTickGood = new byte[] {(byte) -30, (byte) -128, (byte) -104};

   private static final byte[] dotdotdot =
         new byte[] {(byte) -61, (byte) -94, (byte) -30, (byte) -126, (byte) -84, (byte) -62, (byte) -90};
   private static final byte[] dotdotdotGood = new byte[] {(byte) -30, (byte) -128, (byte) -90};

   private static final byte[] lambdaGood = new byte[] {(byte) -62, (byte) -93};
   private static final byte[] oneHalfGood = new byte[] {(byte) -62, (byte) -67};
   private static final byte[] degree2Good = new byte[] {(byte) -62, (byte) -70};
   private static final byte[] whatisit2Good = new byte[] {(byte) -17, (byte) -126, (byte) -79};
   private static final byte[] whatisit3Good = new byte[] {(byte) -17, (byte) -127, (byte) -84};
   private static final String percent = "percent";
   private static final byte[] percentGood = new byte[] {(byte) -30, (byte) -128, (byte) -80};

   private static Map<byte[], byte[]> goodBytes = new HashMap<byte[], byte[]>();
   private static Map<String, byte[]> values = new HashMap<String, byte[]>();

   public static String getValue(List<Byte> bytes) throws UnsupportedEncodingException {
      if (values.isEmpty()) {
         values.put(tickyQuoteBackString, tickyQuoteBack);
         values.put(tickyQuoteForwardString, tickyQuoteForward);
         values.put(new String(notEqualsGood, "UTF-8"), notEquals);
         values.put(new String(greaterThanEqualsGood, "UTF-8"), greaterThanEquals);
         values.put(new String(forwardTickGood, "UTF-8"), forwardTick);
         values.put(new String(notEqualsForwardGood, "UTF-8"), notEqualsForward);
         values.put(new String(lessThanEqualsGood, "UTF-8"), lessThanEquals);
         values.put(new String(blankCharacterGood, "UTF-8"), blankCharacter);
         values.put(new String(dashGood, "UTF-8"), dash);
         values.put(new String(dotdotdotGood, "UTF-8"), dotdotdot);
         values.put(new String(upsideDownTickGood, "UTF-8"), upsideDownTick);
         values.put(new String(whatisitGood, "UTF-8"), whatisit);
         values.put(percent, percentGood);
         values.put("GOOD TICKY QUOTE FORWARD" + new String(tickyQuoteForwardGood, "UTF-8"), tickyQuoteForwardGood);
         values.put("GOOD TICKY QUOTE BACKWARD" + new String(tickyQuoteBackGood, "UTF-8"), tickyQuoteBackGood);
         values.put("GOOD NOT EQUALS" + new String(notEqualsGood, "UTF-8"), notEqualsGood);
         values.put("GOOD GREATER THAN EQUALS " + new String(greaterThanEqualsGood, "UTF-8"), greaterThanEqualsGood);
         values.put("GOOD FORWARD TICK " + new String(forwardTickGood, "UTF-8"), forwardTickGood);
         values.put("GOOD NOT EQUALS FORWARD " + new String(notEqualsForwardGood, "UTF-8"), notEqualsForwardGood);
         values.put("GOOD LESS THAN EQUALS " + new String(lessThanEqualsGood, "UTF-8"), lessThanEqualsGood);
         values.put("GOOD DEGREE MARK " + new String(degreeGood, "UTF-8"), degreeGood);
         values.put("GOOD BLANK CHARACTER " + new String(blankCharacterGood, "UTF-8"), blankCharacterGood);
         values.put("GOOD MICRO CHARACTER " + new String(microCharacterGood, "UTF-8"), microCharacterGood);
         values.put("GOOD PLUS EQUALS CHARACTER " + new String(plusEqualsGood, "UTF-8"), plusEqualsGood);
         values.put("GOOD DASH CHARACTER " + new String(dashGood, "UTF-8"), dashGood);
         values.put("GOOD DOT CHARACTER " + new String(dotGood, "UTF-8"), dotGood);
         values.put("GOOD DOT DOT DOT CHARACTER " + new String(dotdotdotGood, "UTF-8"), dotdotdotGood);
         values.put("        GOOD WHATISIT " + new String(whatisitGood, "UTF-8"), whatisitGood);
         values.put("        GOOD WHATISIT2 " + new String(WHATISIT2, "UTF-8"), WHATISIT2);
         values.put("        GOOD WHATISIT3 " + new String(WHATISIT3, "UTF-8"), WHATISIT3);
         values.put("        GOOD WHATISIT4 " + new String(WHATISIT4, "UTF-8"), WHATISIT4);
         values.put("        GOOD WHATISIT5 " + new String(WHATISIT5, "UTF-8"), WHATISIT5);
         values.put("GOOD UPSIDE DOWN TICK " + new String(upsideDownTickGood, "UTF-8"), upsideDownTickGood);
         values.put("GOOD PLUS MINUS " + new String(plusMinusGood, "UTF-8"), plusMinusGood);
         values.put("GOOD MINI ONE " + new String(miniOneGood, "UTF-8"), miniOneGood);

         values.put("GOOD LAMBDA " + new String(lambdaGood, "UTF-8"), lambdaGood);
         values.put("GOOD ONE HALF " + new String(oneHalfGood, "UTF-8"), oneHalfGood);
         values.put("GOOD DEGREE 2 " + new String(degree2Good, "UTF-8"), degree2Good);
         values.put("GOOD WHATISIT 2 " + new String(whatisit2Good, "UTF-8"), whatisit2Good);
         values.put("GOOD WHATISIT 3 " + new String(whatisit3Good, "UTF-8"), whatisit3Good);
         //values.put("GOOD PERCENT " + new String(percentGood, "UTF-8"), percentGood);

      }
      for (String string : values.keySet()) {
         if (bytes.size() == values.get(string).length) {
            boolean equals = true;
            for (int x = 0; x < bytes.size(); x++) {
               if (bytes.get(x).byteValue() != values.get(string)[x]) {
                  equals = false;
                  break;
               }
            }
            if (equals) {
               return string;
            }
         }
      }
      return null;
   }

   public static byte[] getGoodBytes(String string) throws UnsupportedEncodingException {
      if (goodBytes.isEmpty()) {
         goodBytes.put(tickyQuoteBack, tickyQuoteBackGood);
         goodBytes.put(tickyQuoteForward, tickyQuoteForwardGood);
         goodBytes.put(notEquals, notEqualsGood);
         goodBytes.put(greaterThanEquals, greaterThanEqualsGood);
         goodBytes.put(forwardTick, forwardTickGood);
         goodBytes.put(notEqualsForward, notEqualsForwardGood);
         goodBytes.put(lessThanEquals, lessThanEqualsGood);
         goodBytes.put(blankCharacter, blankCharacterGood);
         goodBytes.put(dash, dashGood);
         goodBytes.put(dotdotdot, dotdotdotGood);
         goodBytes.put(upsideDownTick, upsideDownTickGood);
         goodBytes.put(whatisit, whatisitGood);
         goodBytes.put(percentGood, new byte[] {});
      }
      return goodBytes.get(values.get(string));
   }
}
