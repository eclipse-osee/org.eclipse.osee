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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.util.HexUtil;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.OseeInfo;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author John Misinco
 * @author Donald G. Dunne
 */
public class ShowNextTypeRemoteIds extends AbstractBlam {

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      Long artOriginLong = HexUtil.toLong("0x0000" + OseeInfo.getCachedValue("osee.remote.origin") + "0000000000");
      Long attrOriginLong = HexUtil.toLong("0x1000" + OseeInfo.getCachedValue("osee.remote.origin") + "0000000000");
      Long relOriginLong = HexUtil.toLong("0x2000" + OseeInfo.getCachedValue("osee.remote.origin") + "0000000000");
      Long enumOriginLong = HexUtil.toLong("0x3000" + OseeInfo.getCachedValue("osee.remote.origin") + "0000000000");

      Long artBaseLong = 0L;
      Long attrBaseLong = 0x1000000000000000L;
      Long relBaseLong = 0x2000000000000000L;
      Long enumBaseLong = 0x3000000000000000L;

      String query = "select max(remote_id) from osee_type_id_map where remote_id > ? and remote_id < ?";
      String enumOriginquery = "select max(remote_id) from osee_type_id_map";

      log("\n");
      log("Artifact Type: Next Framework UUID: " + HexUtil.toString(new Long(
         ConnectionHandler.runPreparedQueryFetchString("", query, artBaseLong, artOriginLong)) + 1));
      log("Artifact Type: Next Origin UUID: " + HexUtil.toString(new Long(
         ConnectionHandler.runPreparedQueryFetchString("", query, artOriginLong, attrBaseLong)) + 1));
      log("\n");
      log("Attribute Type: Next Framework UUID: " + HexUtil.toString(new Long(
         ConnectionHandler.runPreparedQueryFetchString("", query, attrBaseLong, attrOriginLong)) + 1));
      log("Attribute Type: Next Origin UUID: " + HexUtil.toString(new Long(
         ConnectionHandler.runPreparedQueryFetchString("", query, attrOriginLong, relBaseLong)) + 1));
      log("\n");
      log("Relation Type: Next Framework UUID: " + HexUtil.toString(new Long(
         ConnectionHandler.runPreparedQueryFetchString("", query, relBaseLong, relOriginLong)) + 1));
      log("Relation Type: Next Origin UUID: " + HexUtil.toString(new Long(
         ConnectionHandler.runPreparedQueryFetchString("", query, relOriginLong, enumBaseLong)) + 1));
      log("\n");
      log("Enum Type: Next Framework UUID: " + HexUtil.toString(new Long(ConnectionHandler.runPreparedQueryFetchString(
         "", query, enumBaseLong, enumOriginLong)) + 1));
      log("Enum Type: Next Origin UUID: " + HexUtil.toString(new Long(ConnectionHandler.runPreparedQueryFetchString("",
         enumOriginquery)) + 1));
      log("\n");

   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets></xWidgets>";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define");
   }

   @Override
   public String getName() {
      return "Show Next OseeType Remote_Ids";
   }

   @Override
   public String getDescriptionUsage() {
      return "Return the next remote_ids for Artifact, Attribute, Relation and Enum types for both Framework (org.eclipse.osee) and Local (Origin) Types.";
   }
}