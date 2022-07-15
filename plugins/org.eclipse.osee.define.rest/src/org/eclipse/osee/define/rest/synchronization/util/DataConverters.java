/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.define.rest.synchronization.util;

import java.math.BigInteger;
import java.time.ZoneId;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.TimeZone;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.rmf.reqif10.ReqIF10Factory;
import org.eclipse.rmf.reqif10.XhtmlContent;
import org.eclipse.rmf.reqif10.xhtml.XhtmlDivType;
import org.eclipse.rmf.reqif10.xhtml.XhtmlFactory;

/**
 * Collection of converters to RMF ReqIF data types.
 *
 * @author Loren K. Ashley
 */

public class DataConverters {

   /**
    * Time {@link ZoneId} constant for "Zulu".
    */

   private static final ZoneId zoneIdZ = ZoneId.of("Z");

   /**
    * {@link TimeZone} constant for "Zulu".
    */

   private static final TimeZone timeZoneZ = TimeZone.getTimeZone(DataConverters.zoneIdZ);

   /**
    * Private do nothing constructor to prevent instantiation of the class.
    */

   private DataConverters() {
   }

   /**
    * Converts a {@link Date} value to a {@link GregorianCalendar} for the Zulu (UTC) time zone.
    *
    * @param value the {@link Date} value to convert.
    * @return the {@link GregorianCalendar} date.
    */

   public static GregorianCalendar dateToGregorianCalendar(Date value) {
      assert Objects.nonNull(value);

      var gregorianCalendar = new GregorianCalendar();
      gregorianCalendar.setTime(value);
      gregorianCalendar.setTimeZone(DataConverters.timeZoneZ);
      return gregorianCalendar;
   }

   /**
    * Converts an OSEE {@link Id} to a {@link BigInteger}.
    *
    * @param value the {@link Id} to convert.
    * @return the {@link BigInteger}.
    */

   public static BigInteger idToBigInteger(Id value) {
      assert Objects.nonNull(value);

      return BigInteger.valueOf(value.getId());
   }

   /**
    * Converts an {@link Integer} to a {@link BigInteger}.
    *
    * @param value the {@link Integer} to convert.
    * @return the {@link BigInteger}.
    */

   public static BigInteger integerToBigInteger(Integer value) {
      assert Objects.nonNull(value);

      return BigInteger.valueOf(value);
   }

   /**
    * Converts a {@link Long} to a {@link BigInteger}.
    *
    * @param value the {@link Long} to convert.
    * @return the {@link BigInteger}.
    */

   public static BigInteger longToBigInteger(Long value) {
      assert Objects.nonNull(value);

      return BigInteger.valueOf(value);
   }

   /**
    * Converts a {@link String} containing Word ML content to {@link XhtmlContent}. This converter creates a single
    * XHTML div with only the words from the Word ML. This converter does not process any of the Word ML text
    * formatting.
    *
    * @param value the {@link String} containing Word ML content.
    * @return the {@link XhtmlContent}.
    */

   public static XhtmlContent wordMlStringToXhtmlContent(String value) {
      assert Objects.nonNull(value);

      value = value.replaceAll("<[^>]*>", " ").replaceAll("[ \t\n]+", " ");

      XhtmlContent xhtmlContent = ReqIF10Factory.eINSTANCE.createXhtmlContent();
      XhtmlDivType div = XhtmlFactory.eINSTANCE.createXhtmlDivType();
      FeatureMap featureMap = div.getMixed();
      FeatureMapUtil.addText(featureMap, value);
      xhtmlContent.setXhtml(div);

      return xhtmlContent;
   }
}

/* EOF */
