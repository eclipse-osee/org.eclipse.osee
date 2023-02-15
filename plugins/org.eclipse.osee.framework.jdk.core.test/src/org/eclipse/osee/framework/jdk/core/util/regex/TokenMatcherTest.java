/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.framework.jdk.core.util.regex;

import org.junit.Assert;
import org.junit.Test;

public class TokenMatcherTest {

   //@formatter:off
   private static final String testSequence1 =
     //+-0       +-10      +-20      +-30      +-40      +-50      +-60      +-70      +-80      +-90      +-100
     //|         |         |         |         |         |         |         |         |         |         |
     //v         v         v         v         v         v         v         v         v         v         v
      "abcdefghijklmnopqrstuvwxyz<w:p><w:t><w:r>INSERT_ARTIFACT_HERE</w:r></w:t></w:p>abcdefghijklmnopqrstuvwxyz";
   //@formatter:on

   @Test
   public void testSequence1() {

      //@formatter:off
      var tokenPattern =
         TokenPattern.compile
            (
               true,                            /* Prefix Suffix Required    */
               14,                              /* Prefix Backup Safety      */
               '<',                             /* Prefix Start Char         */
               "<w:p>",                         /* Prefix Start Token Regexp */
               "<w:p>\\s*<w:t>\\s*<w:r>",       /* Prefix Regexp             */
               "INSERT_(ARTIFACT|LINK)_HERE",   /* Core Token Regexp         */
               "</w:r>\\s*(</w:t>)\\s*(</w:p>)" /* Suffix Regexp             */
            );

      var tokenMatcher = tokenPattern.tokenMatcher( TokenMatcherTest.testSequence1 );

      Assert.assertTrue  ( "tokenMatcher.find()",                        tokenMatcher.find()                                                           );
      Assert.assertEquals( "tokenMatcher.start()",                       26,                     tokenMatcher.start()                                  );
      Assert.assertEquals( "tokenMatcher.end()",                         79,                     tokenMatcher.end()                                    );
      Assert.assertEquals( "tokenMatcher.coreTokenStart(0)",             41,                     tokenMatcher.coreTokenStart(0)                        );
      Assert.assertEquals( "tokenMatcher.coreTokenEnd(0)",               61,                     tokenMatcher.coreTokenEnd(0)                          );
      Assert.assertEquals( "tokenMatcher.coreTokenGroupString(0)",       "INSERT_ARTIFACT_HERE", tokenMatcher.coreTokenGroupString(0)                  );
      Assert.assertEquals( "tokenMatcher.coreTokenGroupCharSequence(0)", "INSERT_ARTIFACT_HERE", tokenMatcher.coreTokenGroupCharSequence(0).toString() );
      Assert.assertEquals( "tokenMatcher.coreTokenStart(1)",             48,                     tokenMatcher.coreTokenStart(1)                        );
      Assert.assertEquals( "tokenMatcher.coreTokenEnd(1)",               56,                     tokenMatcher.coreTokenEnd(1)                          );
      Assert.assertEquals( "tokenMatcher.coreTokenGroupString(1)",       "ARTIFACT",             tokenMatcher.coreTokenGroupString(1)                  );
      Assert.assertEquals( "tokenMatcher.coreTokenGroupCharSequence(1)", "ARTIFACT",             tokenMatcher.coreTokenGroupCharSequence(1).toString() );
      Assert.assertEquals( "tokenMatcher.suffixStart(0)",                61,                     tokenMatcher.suffixStart(0)                           );
      Assert.assertEquals( "tokenMatcher.suffixEnd(0)",                  79,                     tokenMatcher.suffixEnd(0)                             );
      Assert.assertEquals( "tokenMatcher.suffixGroupString(0)",          "</w:r></w:t></w:p>",   tokenMatcher.suffixGroupString(0)                     );
      Assert.assertEquals( "tokenMatcher.suffixGroupCharSequence(0)",    "</w:r></w:t></w:p>",   tokenMatcher.suffixGroupCharSequence(0).toString()    );
      Assert.assertEquals( "tokenMatcher.suffixStart(1)",                67,                     tokenMatcher.suffixStart(1)                           );
      Assert.assertEquals( "tokenMatcher.suffixEnd(1)",                  73,                     tokenMatcher.suffixEnd(1)                             );
      Assert.assertEquals( "tokenMatcher.suffixGroupString(1)",          "</w:t>",               tokenMatcher.suffixGroupString(1)                     );
      Assert.assertEquals( "tokenMatcher.suffixGroupCharSequence(1)",    "</w:t>",               tokenMatcher.suffixGroupCharSequence(1).toString()    );
      Assert.assertEquals( "tokenMatcher.suffixStart(2)",                73,                     tokenMatcher.suffixStart(2)                           );
      Assert.assertEquals( "tokenMatcher.suffixEnd(2)",                  79,                     tokenMatcher.suffixEnd(2)                             );
      Assert.assertEquals( "tokenMatcher.suffixGroupString(2)",          "</w:p>",               tokenMatcher.suffixGroupString(2)                     );
      Assert.assertEquals( "tokenMatcher.suffixGroupCharSequence(2)",    "</w:p>",               tokenMatcher.suffixGroupCharSequence(2).toString()    );

      //@formatter:on
   }

   @Test
   public void testSequence1SafetyExpired() {

      //@formatter:off
      var tokenPattern =
         TokenPattern.compile
            (
               true,                            /* Prefix Suffix Required    */
               13,                              /* Prefix Backup Safety      */
               '<',                             /* Prefix Start Char         */
               "<w:p>",                         /* Prefix Start Token Regexp */
               "<w:p>\\s*<w:t>\\s*<w:r>",       /* Prefix Regexp             */
               "INSERT_(ARTIFACT|LINK)_HERE",   /* Core Token Regexp         */
               "</w:r>\\s*(</w:t>)\\s*(</w:p>)" /* Suffix Regexp             */
            );

      var tokenMatcher = tokenPattern.tokenMatcher( TokenMatcherTest.testSequence1 );

      Assert.assertFalse( "tokenMatcher.find()", tokenMatcher.find() );

      //@formatter:on
   }

   @Test
   public void testSequence1PrefixMatch1Short() {

      //@formatter:off
      var tokenPattern =
         TokenPattern.compile
            (
               true,                            /* Prefix Suffix Required    */
               13,                              /* Prefix Backup Safety      */
               '<',                             /* Prefix Start Char         */
               "<w:p>",                         /* Prefix Start Token Regexp */
               "<w:p>\\s*<w:t>\\s*<w:r",        /* Prefix Regexp             */
               "INSERT_(ARTIFACT|LINK)_HERE",   /* Core Token Regexp         */
               "</w:r>\\s*(</w:t>)\\s*(</w:p>)" /* Suffix Regexp             */
            );

      var tokenMatcher = tokenPattern.tokenMatcher( TokenMatcherTest.testSequence1 );

      Assert.assertFalse( "tokenMatcher.find()", tokenMatcher.find() );

      //@formatter:on
   }

   @Test
   public void testSequence1PrefixSuffixNotRequired() {

      //@formatter:off
      var tokenPattern =
         TokenPattern.compile
            (
               false,                           /* Prefix Suffix Required    */
               14,                              /* Prefix Backup Safety      */
               '<',                             /* Prefix Start Char         */
               "<w:p>",                         /* Prefix Start Token Regexp */
               "NO-PREFIX-MATCH",               /* Prefix Regexp             */
               "INSERT_(ARTIFACT|LINK)_HERE",   /* Core Token Regexp         */
               "</w:r>\\s*(</w:t>)\\s*(</w:p>)" /* Suffix Regexp             */
            );

      var tokenMatcher = tokenPattern.tokenMatcher( TokenMatcherTest.testSequence1 );

      Assert.assertTrue  ( "tokenMatcher.find()",                                                tokenMatcher.find()                                   );
      Assert.assertEquals( "tokenMatcher.start()",                       41,                     tokenMatcher.start()                                  );
      Assert.assertEquals( "tokenMatcher.end()",                         61,                     tokenMatcher.end()                                    );
      Assert.assertEquals( "tokenMatcher.coreTokenStart(0)",             41,                     tokenMatcher.coreTokenStart(0)                        );
      Assert.assertEquals( "tokenMatcher.coreTokenEnd(0)",               61,                     tokenMatcher.coreTokenEnd(0)                          );
      Assert.assertEquals( "tokenMatcher.coreTokenGroupString(0)",       "INSERT_ARTIFACT_HERE", tokenMatcher.coreTokenGroupString(0)                  );
      Assert.assertEquals( "tokenMatcher.coreTokenGroupCharSequence(0)", "INSERT_ARTIFACT_HERE", tokenMatcher.coreTokenGroupCharSequence(0).toString() );
      Assert.assertEquals( "tokenMatcher.coreTokenStart(1)",             48,                     tokenMatcher.coreTokenStart(1)                        );
      Assert.assertEquals( "tokenMatcher.coreTokenEnd(1)",               56,                     tokenMatcher.coreTokenEnd(1)                          );
      Assert.assertEquals( "tokenMatcher.coreTokenGroupString(1)",       "ARTIFACT",             tokenMatcher.coreTokenGroupString(1)                  );
      Assert.assertEquals( "tokenMatcher.coreTokenGroupCharSequence(1)", "ARTIFACT",             tokenMatcher.coreTokenGroupCharSequence(1).toString() );
      Assert.assertEquals( "tokenMatcher.suffixStart(0)",                -1,                     tokenMatcher.suffixStart(0)                           );
      Assert.assertEquals( "tokenMatcher.suffixEnd(0)",                  -1,                     tokenMatcher.suffixEnd(0)                             );
      Assert.assertNull  ( "tokenMatcher.suffixGroupString(0)",                                  tokenMatcher.suffixGroupString(0)                     );
      Assert.assertNull  ( "tokenMatcher.suffixGroupCharSequence(0)",                            tokenMatcher.suffixGroupCharSequence(0)               );
      Assert.assertEquals( "tokenMatcher.suffixStart(1)",                -1,                     tokenMatcher.suffixStart(1)                           );
      Assert.assertEquals( "tokenMatcher.suffixEnd(1)",                  -1,                     tokenMatcher.suffixEnd(1)                             );
      Assert.assertNull  ( "tokenMatcher.suffixGroupString(1)",                                  tokenMatcher.suffixGroupString(1)                     );
      Assert.assertNull  ( "tokenMatcher.suffixGroupCharSequence(1)",                            tokenMatcher.suffixGroupCharSequence(1)               );
      Assert.assertEquals( "tokenMatcher.suffixStart(2)",                -1,                     tokenMatcher.suffixStart(2)                           );
      Assert.assertEquals( "tokenMatcher.suffixEnd(2)",                  -1,                     tokenMatcher.suffixEnd(2)                             );
      Assert.assertNull  ( "tokenMatcher.suffixGroupString(2)",                                  tokenMatcher.suffixGroupString(2)                     );
      Assert.assertNull  ( "tokenMatcher.suffixGroupCharSequence(2)",                            tokenMatcher.suffixGroupCharSequence(2)               );

      //@formatter:on
   }

}

/* EOF */
