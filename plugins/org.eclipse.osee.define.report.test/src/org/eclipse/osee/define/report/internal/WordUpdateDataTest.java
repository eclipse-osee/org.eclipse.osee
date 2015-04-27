/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.report.internal;

import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.define.report.api.WordUpdateData;
import org.junit.Test;
import com.google.gson.Gson;

/**
 * Test unit for {@link WordUpdateData}
 *
 * @author David W. Miller
 */
public class WordUpdateDataTest {
   private final String wordData =
      "<w:p wsp:rsidR=\"007C56E2\" wsp:rsidRDefault=\"00C879B4\"><w:r><w:t>Test data</w:t></w:r></w:p><w:p wsp:rsidR=\"00C879B4\" wsp:rsidRDefault=\"00C879B4\"><w:hlink w:dest=\"http://localhost:8089/osee/client/loopback?branchUuid=2558725026483337297&amp;cmd=open.artifact&amp;context=osee%2Floopback&amp;guid=ABFW7XmPaldPHSHHAQAA&amp;isDeleted=false&amp;sessionId=AS%2Bfe6NXCRhgkB%2BXrAgA\"><w:proofErr w:type=\"gramStart\"></w:proofErr><w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"></w:rStyle></w:rPr><w:t>Software requirement for testing validation code.</w:t></w:r><w:proofErr w:type=\"gramEnd\"></w:proofErr></w:hlink></w:p>";

   @Test
   public void testCountClassMethods() {
      int numFields = WordUpdateData.class.getDeclaredFields().length;
      int numMethods = WordUpdateData.class.getDeclaredMethods().length;
      assertTrue(numFields == 7);
      // if you add a field, be sure to update the conversion test below
      assertTrue(numMethods == 2 * numFields);
   }

   @Test
   public void testJsonConversion() {
      List<Long> list = new ArrayList<Long>();
      list.add(1L);
      list.add(2L);
      list.add(3L);

      WordUpdateData data = new WordUpdateData();
      data.setArtifacts(list);
      data.setBranch(144L);
      data.setComment("This is a test data structure");
      data.setMultiEdit(true);
      data.setThreeWayMerge(true);
      data.setUserArtId(50L);
      data.setWordData(wordData.getBytes());

      Gson gson = new Gson();
      String s = gson.toJson(data);
      WordUpdateData roundTrip = gson.fromJson(s, WordUpdateData.class);
      compare(data, roundTrip);
   }

   private void compare(WordUpdateData source, WordUpdateData dest) {
      assertTrue(source.getBranch().equals(dest.getBranch()));
      assertTrue(source.isMultiEdit() == dest.isMultiEdit());
      assertTrue(source.isThreeWayMerge() == dest.isThreeWayMerge());
      assertTrue(source.getUserArtId().equals(dest.getUserArtId()));
      assertTrue(source.getComment().equals(dest.getComment()));
      assertTrue(source.getArtifacts().equals(dest.getArtifacts()));
      assertTrue(Arrays.equals(source.getWordData(), dest.getWordData()));
   }
}
