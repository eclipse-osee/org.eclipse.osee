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
package org.eclipse.osee.define.report;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.Arrays;
import java.util.LinkedList;
import org.eclipse.osee.define.report.api.WordUpdateData;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Case for {@link WordUpdateEndpointImpl}
 *
 * @author Donald G. Dunne
 */
public class WordUpdateEndpointImplTest {

   private WordUpdateEndpointImpl update;
   private boolean caught;
   private WordUpdateData wud;

   @Before
   public void setup() {
      update = new WordUpdateEndpointImpl(null, null, null);
      caught = false;
      wud = getWud();
   }

   @Test
   public void testValidateNull() {
      try {
         update.validate(null);
      } catch (OseeArgumentException ex) {
         if (ex.getMessage().contains("WordUpdateData cannot be null")) {
            caught = true;
         }
      }
      if (!caught) {
         Assert.fail("Expected OseeArgumentException");
      }
   }

   @Test
   public void testValidateSuccess() {
      update.validate(wud);
   }

   @Test
   public void testValidateBranch() {
      // test branch
      wud.setBranch(BranchId.SENTINEL);
      try {
         update.validate(wud);
      } catch (OseeArgumentException ex) {
         if (ex.getMessage().contains("invalid branch")) {
            caught = true;
         }
      }
      if (!caught) {
         Assert.fail("Expected OseeArgumentException");
      }
   }

   @Test
   public void testValidateUser() {
      // test branch
      wud.setUserArtId(0L);
      try {
         update.validate(wud);
      } catch (OseeArgumentException ex) {
         if (ex.getMessage().contains("WordUpdateData invalid user id")) {
            caught = true;
         }
      }
      if (!caught) {
         Assert.fail("Expected OseeArgumentException");
      }
   }

   @Test
   public void testValidateComment() {
      wud = getWud();
      wud.setComment(null);
      getWud();
      try {
         update.validate(wud);
      } catch (OseeArgumentException ex) {
         if (ex.getMessage().contains("WordUpdateData comment cannot be null")) {
            caught = true;
         }
      }
      if (!caught) {
         Assert.fail("Expected OseeArgumentException");
      }
      wud.setComment("");
      getWud();
      try {
         update.validate(wud);
      } catch (OseeArgumentException ex) {
         if (ex.getMessage().contains("WordUpdateData comment cannot be empty")) {
            caught = true;
         }
      }
      if (!caught) {
         Assert.fail("Expected OseeArgumentException");
      }
   }

   @Test
   public void testValidateArtifacts() {
      wud = getWud();
      wud.setArtifacts(null);
      getWud();
      try {
         update.validate(wud);
      } catch (OseeArgumentException ex) {
         if (ex.getMessage().contains("WordUpdateData artifacts cannot be null")) {
            caught = true;
         }
      }
      if (!caught) {
         Assert.fail("Expected OseeArgumentException");
      }
      wud.setArtifacts(new LinkedList<>());
      getWud();
      try {
         update.validate(wud);
      } catch (OseeArgumentException ex) {
         if (ex.getMessage().contains("WordUpdateData artifacts cannot be empty")) {
            caught = true;
         }
      }
      if (!caught) {
         Assert.fail("Expected OseeArgumentException");
      }
   }

   public WordUpdateData getWud() {
      WordUpdateData wud = new WordUpdateData();
      wud.setBranch(COMMON);
      wud.setThreeWayMerge(false);
      wud.setComment("other data invalid");
      wud.setMultiEdit(false);
      wud.setUserArtId(234L);
      wud.setArtifacts(Arrays.asList(234L));
      wud.setWordData("asdf".getBytes());
      return wud;
   }

}
