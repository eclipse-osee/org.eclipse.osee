package org.eclipse.osee.framework.core.grammar;
// $ANTLR 3.2 Sep 23, 2009 12:02:23 ApplicabilityGrammar.g 2016-12-19 11:00:12

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.TreeAdaptor;

public class ApplicabilityGrammarParser extends Parser {
   public static final String[] tokenNames = new String[] {
      "<invalid>",
      "<EOR>",
      "<DOWN>",
      "<UP>",
      "OR",
      "ID",
      "AND",
      "WS",
      "'Configuration['",
      "']'",
      "'Feature['",
      "'='",
      "'('",
      "')'"};
   public static final int T__9 = 9;
   public static final int T__8 = 8;
   public static final int OR = 4;
   public static final int AND = 6;
   public static final int T__11 = 11;
   public static final int T__12 = 12;
   public static final int T__13 = 13;
   public static final int ID = 5;
   public static final int WS = 7;
   public static final int EOF = -1;
   public static final int T__10 = 10;

   // delegates
   // delegators

   public ApplicabilityGrammarParser(TokenStream input) {
      this(input, new RecognizerSharedState());
   }

   public ApplicabilityGrammarParser(TokenStream input, RecognizerSharedState state) {
      super(input, state);

   }

   protected TreeAdaptor adaptor = new CommonTreeAdaptor();

   public void setTreeAdaptor(TreeAdaptor adaptor) {
      this.adaptor = adaptor;
   }

   public TreeAdaptor getTreeAdaptor() {
      return adaptor;
   }

   @Override
   public String[] getTokenNames() {
      return ApplicabilityGrammarParser.tokenNames;
   }

   @Override
   public String getGrammarFileName() {
      return "ApplicabilityGrammar.g";
   }

   private String applicabilityType = null;
   private String featureId = null;

   private final ArrayList<String> configIds = new ArrayList<>();
   private final HashMap<String, List<String>> featureId_values_map = new HashMap<>();
   private final ArrayList<String> featureOperators = new ArrayList<>();

   public ArrayList<String> getFeatureOperators() {
      return featureOperators;
   }

   public ArrayList<String> getConfigIds() {
      return configIds;
   }

   public HashMap<String, List<String>> getFeatureIdValuesMap() {
      return featureId_values_map;
   }

   public String getApplicabilityType() {
      return applicabilityType;
   }

   public static class start_return extends ParserRuleReturnScope {
      CommonTree tree;

      @Override
      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "start"
   // ApplicabilityGrammar.g:43:1: start : applicability EOF ;
   public final ApplicabilityGrammarParser.start_return start() throws RecognitionException {
      ApplicabilityGrammarParser.start_return retval = new ApplicabilityGrammarParser.start_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token EOF2 = null;
      ApplicabilityGrammarParser.applicability_return applicability1 = null;

      CommonTree EOF2_tree = null;

      try {
         // ApplicabilityGrammar.g:43:24: ( applicability EOF )
         // ApplicabilityGrammar.g:43:27: applicability EOF
         {
            root_0 = (CommonTree) adaptor.nil();

            pushFollow(FOLLOW_applicability_in_start57);
            applicability1 = applicability();

            state._fsp--;

            adaptor.addChild(root_0, applicability1.getTree());
            EOF2 = (Token) match(input, EOF, FOLLOW_EOF_in_start59);
            featureOperators.removeAll(Collections.singleton(null));

         }

         retval.stop = input.LT(-1);

         retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
         adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      } catch (RecognitionException re) {
         reportError(re);
         recover(input, re);
         retval.tree = (CommonTree) adaptor.errorNode(input, retval.start, input.LT(-1), re);

      } finally {
      }
      return retval;
   }
   // $ANTLR end "start"

   public static class applicability_return extends ParserRuleReturnScope {
      CommonTree tree;

      @Override
      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "applicability"
   // ApplicabilityGrammar.g:45:1: applicability : ( config_applicability | feature_applicability );
   public final ApplicabilityGrammarParser.applicability_return applicability() throws RecognitionException {
      ApplicabilityGrammarParser.applicability_return retval = new ApplicabilityGrammarParser.applicability_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      ApplicabilityGrammarParser.config_applicability_return config_applicability3 = null;

      ApplicabilityGrammarParser.feature_applicability_return feature_applicability4 = null;

      try {
         // ApplicabilityGrammar.g:45:25: ( config_applicability | feature_applicability )
         int alt1 = 2;
         int LA1_0 = input.LA(1);

         if ((LA1_0 == 8)) {
            alt1 = 1;
         } else if ((LA1_0 == 10)) {
            alt1 = 2;
         } else {
            NoViableAltException nvae = new NoViableAltException("", 1, 0, input);

            throw nvae;
         }
         switch (alt1) {
            case 1:
            // ApplicabilityGrammar.g:45:27: config_applicability
            {
               root_0 = (CommonTree) adaptor.nil();

               pushFollow(FOLLOW_config_applicability_in_applicability80);
               config_applicability3 = config_applicability();

               state._fsp--;

               adaptor.addChild(root_0, config_applicability3.getTree());
               applicabilityType = "config";

            }
               break;
            case 2:
            // ApplicabilityGrammar.g:46:11: feature_applicability
            {
               root_0 = (CommonTree) adaptor.nil();

               pushFollow(FOLLOW_feature_applicability_in_applicability95);
               feature_applicability4 = feature_applicability();

               state._fsp--;

               adaptor.addChild(root_0, feature_applicability4.getTree());
               applicabilityType = "feature";

            }
               break;

         }
         retval.stop = input.LT(-1);

         retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
         adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      } catch (RecognitionException re) {
         reportError(re);
         recover(input, re);
         retval.tree = (CommonTree) adaptor.errorNode(input, retval.start, input.LT(-1), re);

      } finally {
      }
      return retval;
   }
   // $ANTLR end "applicability"

   public static class config_applicability_return extends ParserRuleReturnScope {
      CommonTree tree;

      @Override
      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "config_applicability"
   // ApplicabilityGrammar.g:48:1: config_applicability : 'Configuration[' ( config_expressions )+ ']' ;
   public final ApplicabilityGrammarParser.config_applicability_return config_applicability() throws RecognitionException {
      ApplicabilityGrammarParser.config_applicability_return retval =
         new ApplicabilityGrammarParser.config_applicability_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token string_literal5 = null;
      Token char_literal7 = null;
      ApplicabilityGrammarParser.config_expressions_return config_expressions6 = null;

      CommonTree string_literal5_tree = null;
      CommonTree char_literal7_tree = null;

      try {
         // ApplicabilityGrammar.g:48:25: ( 'Configuration[' ( config_expressions )+ ']' )
         // ApplicabilityGrammar.g:48:27: 'Configuration[' ( config_expressions )+ ']'
         {
            root_0 = (CommonTree) adaptor.nil();

            string_literal5 = (Token) match(input, 8, FOLLOW_8_in_config_applicability118);
            string_literal5_tree = (CommonTree) adaptor.create(string_literal5);
            adaptor.addChild(root_0, string_literal5_tree);

            // ApplicabilityGrammar.g:48:44: ( config_expressions )+
            int cnt2 = 0;
            loop2: do {
               int alt2 = 2;
               int LA2_0 = input.LA(1);

               if (((LA2_0 >= OR && LA2_0 <= ID))) {
                  alt2 = 1;
               }

               switch (alt2) {
                  case 1:
                  // ApplicabilityGrammar.g:48:44: config_expressions
                  {
                     pushFollow(FOLLOW_config_expressions_in_config_applicability120);
                     config_expressions6 = config_expressions();

                     state._fsp--;

                     adaptor.addChild(root_0, config_expressions6.getTree());

                  }
                     break;

                  default:
                     if (cnt2 >= 1) {
                        break loop2;
                     }
                     EarlyExitException eee = new EarlyExitException(2, input);
                     throw eee;
               }
               cnt2++;
            } while (true);

            char_literal7 = (Token) match(input, 9, FOLLOW_9_in_config_applicability123);
            char_literal7_tree = (CommonTree) adaptor.create(char_literal7);
            adaptor.addChild(root_0, char_literal7_tree);

         }

         retval.stop = input.LT(-1);

         retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
         adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      } catch (RecognitionException re) {
         reportError(re);
         recover(input, re);
         retval.tree = (CommonTree) adaptor.errorNode(input, retval.start, input.LT(-1), re);

      } finally {
      }
      return retval;
   }
   // $ANTLR end "config_applicability"

   public static class config_expressions_return extends ParserRuleReturnScope {
      CommonTree tree;

      @Override
      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "config_expressions"
   // ApplicabilityGrammar.g:50:1: config_expressions : ( OR )? ID ;
   public final ApplicabilityGrammarParser.config_expressions_return config_expressions() throws RecognitionException {
      ApplicabilityGrammarParser.config_expressions_return retval =
         new ApplicabilityGrammarParser.config_expressions_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token OR8 = null;
      Token ID9 = null;

      CommonTree OR8_tree = null;
      CommonTree ID9_tree = null;

      try {
         // ApplicabilityGrammar.g:50:21: ( ( OR )? ID )
         // ApplicabilityGrammar.g:50:24: ( OR )? ID
         {
            root_0 = (CommonTree) adaptor.nil();

            // ApplicabilityGrammar.g:50:24: ( OR )?
            int alt3 = 2;
            int LA3_0 = input.LA(1);

            if ((LA3_0 == OR)) {
               alt3 = 1;
            }
            switch (alt3) {
               case 1:
               // ApplicabilityGrammar.g:50:24: OR
               {
                  OR8 = (Token) match(input, OR, FOLLOW_OR_in_config_expressions133);
                  OR8_tree = (CommonTree) adaptor.create(OR8);
                  adaptor.addChild(root_0, OR8_tree);

               }
                  break;

            }

            ID9 = (Token) match(input, ID, FOLLOW_ID_in_config_expressions136);
            ID9_tree = (CommonTree) adaptor.create(ID9);
            adaptor.addChild(root_0, ID9_tree);

            configIds.add((ID9 != null ? ID9.getText() : null));

         }

         retval.stop = input.LT(-1);

         retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
         adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      } catch (RecognitionException re) {
         reportError(re);
         recover(input, re);
         retval.tree = (CommonTree) adaptor.errorNode(input, retval.start, input.LT(-1), re);

      } finally {
      }
      return retval;
   }
   // $ANTLR end "config_expressions"

   public static class feature_applicability_return extends ParserRuleReturnScope {
      CommonTree tree;

      @Override
      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "feature_applicability"
   // ApplicabilityGrammar.g:52:1: feature_applicability : 'Feature[' ( feature_expressions )+ ']' ;
   public final ApplicabilityGrammarParser.feature_applicability_return feature_applicability() throws RecognitionException {
      ApplicabilityGrammarParser.feature_applicability_return retval =
         new ApplicabilityGrammarParser.feature_applicability_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token string_literal10 = null;
      Token char_literal12 = null;
      ApplicabilityGrammarParser.feature_expressions_return feature_expressions11 = null;

      CommonTree string_literal10_tree = null;
      CommonTree char_literal12_tree = null;

      try {
         // ApplicabilityGrammar.g:52:25: ( 'Feature[' ( feature_expressions )+ ']' )
         // ApplicabilityGrammar.g:52:27: 'Feature[' ( feature_expressions )+ ']'
         {
            root_0 = (CommonTree) adaptor.nil();

            string_literal10 = (Token) match(input, 10, FOLLOW_10_in_feature_applicability148);
            string_literal10_tree = (CommonTree) adaptor.create(string_literal10);
            adaptor.addChild(root_0, string_literal10_tree);

            // ApplicabilityGrammar.g:52:38: ( feature_expressions )+
            int cnt4 = 0;
            loop4: do {
               int alt4 = 2;
               int LA4_0 = input.LA(1);

               if (((LA4_0 >= OR && LA4_0 <= AND))) {
                  alt4 = 1;
               }

               switch (alt4) {
                  case 1:
                  // ApplicabilityGrammar.g:52:38: feature_expressions
                  {
                     pushFollow(FOLLOW_feature_expressions_in_feature_applicability150);
                     feature_expressions11 = feature_expressions();

                     state._fsp--;

                     adaptor.addChild(root_0, feature_expressions11.getTree());

                  }
                     break;

                  default:
                     if (cnt4 >= 1) {
                        break loop4;
                     }
                     EarlyExitException eee = new EarlyExitException(4, input);
                     throw eee;
               }
               cnt4++;
            } while (true);

            char_literal12 = (Token) match(input, 9, FOLLOW_9_in_feature_applicability153);
            char_literal12_tree = (CommonTree) adaptor.create(char_literal12);
            adaptor.addChild(root_0, char_literal12_tree);

         }

         retval.stop = input.LT(-1);

         retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
         adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      } catch (RecognitionException re) {
         reportError(re);
         recover(input, re);
         retval.tree = (CommonTree) adaptor.errorNode(input, retval.start, input.LT(-1), re);

      } finally {
      }
      return retval;
   }
   // $ANTLR end "feature_applicability"

   public static class feature_expressions_return extends ParserRuleReturnScope {
      CommonTree tree;

      @Override
      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "feature_expressions"
   // ApplicabilityGrammar.g:54:1: feature_expressions : ( ( operator )? feature_expression ) ;
   public final ApplicabilityGrammarParser.feature_expressions_return feature_expressions() throws RecognitionException {
      ApplicabilityGrammarParser.feature_expressions_return retval =
         new ApplicabilityGrammarParser.feature_expressions_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      ApplicabilityGrammarParser.operator_return operator13 = null;

      ApplicabilityGrammarParser.feature_expression_return feature_expression14 = null;

      try {
         // ApplicabilityGrammar.g:54:25: ( ( ( operator )? feature_expression ) )
         // ApplicabilityGrammar.g:54:27: ( ( operator )? feature_expression )
         {
            root_0 = (CommonTree) adaptor.nil();

            // ApplicabilityGrammar.g:54:27: ( ( operator )? feature_expression )
            // ApplicabilityGrammar.g:54:28: ( operator )? feature_expression
            {
               // ApplicabilityGrammar.g:54:28: ( operator )?
               int alt5 = 2;
               int LA5_0 = input.LA(1);

               if ((LA5_0 == OR || LA5_0 == AND)) {
                  alt5 = 1;
               }
               switch (alt5) {
                  case 1:
                  // ApplicabilityGrammar.g:54:28: operator
                  {
                     pushFollow(FOLLOW_operator_in_feature_expressions167);
                     operator13 = operator();

                     state._fsp--;

                     adaptor.addChild(root_0, operator13.getTree());

                  }
                     break;

               }

               pushFollow(FOLLOW_feature_expression_in_feature_expressions170);
               feature_expression14 = feature_expression();

               state._fsp--;

               adaptor.addChild(root_0, feature_expression14.getTree());

            }

            featureOperators.add((operator13 != null ? input.toString(operator13.start, operator13.stop) : null));

         }

         retval.stop = input.LT(-1);

         retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
         adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      } catch (RecognitionException re) {
         reportError(re);
         recover(input, re);
         retval.tree = (CommonTree) adaptor.errorNode(input, retval.start, input.LT(-1), re);

      } finally {
      }
      return retval;
   }
   // $ANTLR end "feature_expressions"

   public static class feature_expression_return extends ParserRuleReturnScope {
      CommonTree tree;

      @Override
      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "feature_expression"
   // ApplicabilityGrammar.g:56:1: feature_expression : ID ( '=' temp= feature_value )? ;
   public final ApplicabilityGrammarParser.feature_expression_return feature_expression() throws RecognitionException {
      ApplicabilityGrammarParser.feature_expression_return retval =
         new ApplicabilityGrammarParser.feature_expression_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token ID15 = null;
      Token char_literal16 = null;
      ApplicabilityGrammarParser.feature_value_return temp = null;

      CommonTree ID15_tree = null;
      CommonTree char_literal16_tree = null;

      try {
         // ApplicabilityGrammar.g:56:21: ( ID ( '=' temp= feature_value )? )
         // ApplicabilityGrammar.g:56:23: ID ( '=' temp= feature_value )?
         {
            root_0 = (CommonTree) adaptor.nil();

            ID15 = (Token) match(input, ID, FOLLOW_ID_in_feature_expression182);
            ID15_tree = (CommonTree) adaptor.create(ID15);
            adaptor.addChild(root_0, ID15_tree);

            featureId = (ID15 != null ? ID15.getText() : null);
            featureId_values_map.put(featureId, new ArrayList<String>());

            // ApplicabilityGrammar.g:59:9: ( '=' temp= feature_value )?
            int alt6 = 2;
            int LA6_0 = input.LA(1);

            if ((LA6_0 == 11)) {
               alt6 = 1;
            }
            switch (alt6) {
               case 1:
               // ApplicabilityGrammar.g:59:10: '=' temp= feature_value
               {
                  char_literal16 = (Token) match(input, 11, FOLLOW_11_in_feature_expression196);
                  char_literal16_tree = (CommonTree) adaptor.create(char_literal16);
                  adaptor.addChild(root_0, char_literal16_tree);

                  pushFollow(FOLLOW_feature_value_in_feature_expression200);
                  temp = feature_value();

                  state._fsp--;

                  adaptor.addChild(root_0, temp.getTree());

               }
                  break;

            }

            if ((temp != null ? input.toString(temp.start, temp.stop) : null) == null) {
               featureId_values_map.put(featureId, Arrays.asList("Default"));
            }

         }

         retval.stop = input.LT(-1);

         retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
         adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      } catch (RecognitionException re) {
         reportError(re);
         recover(input, re);
         retval.tree = (CommonTree) adaptor.errorNode(input, retval.start, input.LT(-1), re);

      } finally {
      }
      return retval;
   }
   // $ANTLR end "feature_expression"

   public static class feature_value_return extends ParserRuleReturnScope {
      CommonTree tree;

      @Override
      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "feature_value"
   // ApplicabilityGrammar.g:64:1: feature_value : ( value | start_compound );
   public final ApplicabilityGrammarParser.feature_value_return feature_value() throws RecognitionException {
      ApplicabilityGrammarParser.feature_value_return retval = new ApplicabilityGrammarParser.feature_value_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      ApplicabilityGrammarParser.value_return value17 = null;

      ApplicabilityGrammarParser.start_compound_return start_compound18 = null;

      try {
         // ApplicabilityGrammar.g:64:17: ( value | start_compound )
         int alt7 = 2;
         int LA7_0 = input.LA(1);

         if (((LA7_0 >= OR && LA7_0 <= AND))) {
            alt7 = 1;
         } else if ((LA7_0 == 12)) {
            alt7 = 2;
         } else {
            NoViableAltException nvae = new NoViableAltException("", 7, 0, input);

            throw nvae;
         }
         switch (alt7) {
            case 1:
            // ApplicabilityGrammar.g:64:20: value
            {
               root_0 = (CommonTree) adaptor.nil();

               pushFollow(FOLLOW_value_in_feature_value232);
               value17 = value();

               state._fsp--;

               adaptor.addChild(root_0, value17.getTree());

            }
               break;
            case 2:
            // ApplicabilityGrammar.g:64:28: start_compound
            {
               root_0 = (CommonTree) adaptor.nil();

               pushFollow(FOLLOW_start_compound_in_feature_value236);
               start_compound18 = start_compound();

               state._fsp--;

               adaptor.addChild(root_0, start_compound18.getTree());

            }
               break;

         }
         retval.stop = input.LT(-1);

         retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
         adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      } catch (RecognitionException re) {
         reportError(re);
         recover(input, re);
         retval.tree = (CommonTree) adaptor.errorNode(input, retval.start, input.LT(-1), re);

      } finally {
      }
      return retval;
   }
   // $ANTLR end "feature_value"

   public static class start_compound_return extends ParserRuleReturnScope {
      CommonTree tree;

      @Override
      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "start_compound"
   // ApplicabilityGrammar.g:66:1: start_compound : '(' compound_value ')' ;
   public final ApplicabilityGrammarParser.start_compound_return start_compound() throws RecognitionException {
      ApplicabilityGrammarParser.start_compound_return retval = new ApplicabilityGrammarParser.start_compound_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token char_literal19 = null;
      Token char_literal21 = null;
      ApplicabilityGrammarParser.compound_value_return compound_value20 = null;

      CommonTree char_literal19_tree = null;
      CommonTree char_literal21_tree = null;

      try {
         // ApplicabilityGrammar.g:66:18: ( '(' compound_value ')' )
         // ApplicabilityGrammar.g:66:20: '(' compound_value ')'
         {
            root_0 = (CommonTree) adaptor.nil();

            char_literal19 = (Token) match(input, 12, FOLLOW_12_in_start_compound254);
            char_literal19_tree = (CommonTree) adaptor.create(char_literal19);
            adaptor.addChild(root_0, char_literal19_tree);

            featureId_values_map.get(featureId).add("(");
            pushFollow(FOLLOW_compound_value_in_start_compound269);
            compound_value20 = compound_value();

            state._fsp--;

            adaptor.addChild(root_0, compound_value20.getTree());
            char_literal21 = (Token) match(input, 13, FOLLOW_13_in_start_compound280);
            char_literal21_tree = (CommonTree) adaptor.create(char_literal21);
            adaptor.addChild(root_0, char_literal21_tree);

            featureId_values_map.get(featureId).add(")");

         }

         retval.stop = input.LT(-1);

         retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
         adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      } catch (RecognitionException re) {
         reportError(re);
         recover(input, re);
         retval.tree = (CommonTree) adaptor.errorNode(input, retval.start, input.LT(-1), re);

      } finally {
      }
      return retval;
   }
   // $ANTLR end "start_compound"

   public static class compound_value_return extends ParserRuleReturnScope {
      CommonTree tree;

      @Override
      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "compound_value"
   // ApplicabilityGrammar.g:70:1: compound_value : ( ( value )+ | multiple_compounds );
   public final ApplicabilityGrammarParser.compound_value_return compound_value() throws RecognitionException {
      ApplicabilityGrammarParser.compound_value_return retval = new ApplicabilityGrammarParser.compound_value_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      ApplicabilityGrammarParser.value_return value22 = null;

      ApplicabilityGrammarParser.multiple_compounds_return multiple_compounds23 = null;

      try {
         // ApplicabilityGrammar.g:70:18: ( ( value )+ | multiple_compounds )
         int alt9 = 2;
         int LA9_0 = input.LA(1);

         if (((LA9_0 >= OR && LA9_0 <= AND))) {
            alt9 = 1;
         } else if ((LA9_0 == 12)) {
            alt9 = 2;
         } else {
            NoViableAltException nvae = new NoViableAltException("", 9, 0, input);

            throw nvae;
         }
         switch (alt9) {
            case 1:
            // ApplicabilityGrammar.g:70:20: ( value )+
            {
               root_0 = (CommonTree) adaptor.nil();

               // ApplicabilityGrammar.g:70:20: ( value )+
               int cnt8 = 0;
               loop8: do {
                  int alt8 = 2;
                  int LA8_0 = input.LA(1);

                  if (((LA8_0 >= OR && LA8_0 <= AND))) {
                     alt8 = 1;
                  }

                  switch (alt8) {
                     case 1:
                     // ApplicabilityGrammar.g:70:20: value
                     {
                        pushFollow(FOLLOW_value_in_compound_value292);
                        value22 = value();

                        state._fsp--;

                        adaptor.addChild(root_0, value22.getTree());

                     }
                        break;

                     default:
                        if (cnt8 >= 1) {
                           break loop8;
                        }
                        EarlyExitException eee = new EarlyExitException(8, input);
                        throw eee;
                  }
                  cnt8++;
               } while (true);

            }
               break;
            case 2:
            // ApplicabilityGrammar.g:70:29: multiple_compounds
            {
               root_0 = (CommonTree) adaptor.nil();

               pushFollow(FOLLOW_multiple_compounds_in_compound_value297);
               multiple_compounds23 = multiple_compounds();

               state._fsp--;

               adaptor.addChild(root_0, multiple_compounds23.getTree());

            }
               break;

         }
         retval.stop = input.LT(-1);

         retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
         adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      } catch (RecognitionException re) {
         reportError(re);
         recover(input, re);
         retval.tree = (CommonTree) adaptor.errorNode(input, retval.start, input.LT(-1), re);

      } finally {
      }
      return retval;
   }
   // $ANTLR end "compound_value"

   public static class multiple_compounds_return extends ParserRuleReturnScope {
      CommonTree tree;

      @Override
      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "multiple_compounds"
   // ApplicabilityGrammar.g:72:1: multiple_compounds : start_compound operator compound_value ;
   public final ApplicabilityGrammarParser.multiple_compounds_return multiple_compounds() throws RecognitionException {
      ApplicabilityGrammarParser.multiple_compounds_return retval =
         new ApplicabilityGrammarParser.multiple_compounds_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      ApplicabilityGrammarParser.start_compound_return start_compound24 = null;

      ApplicabilityGrammarParser.operator_return operator25 = null;

      ApplicabilityGrammarParser.compound_value_return compound_value26 = null;

      try {
         // ApplicabilityGrammar.g:72:22: ( start_compound operator compound_value )
         // ApplicabilityGrammar.g:72:24: start_compound operator compound_value
         {
            root_0 = (CommonTree) adaptor.nil();

            pushFollow(FOLLOW_start_compound_in_multiple_compounds314);
            start_compound24 = start_compound();

            state._fsp--;

            adaptor.addChild(root_0, start_compound24.getTree());
            pushFollow(FOLLOW_operator_in_multiple_compounds326);
            operator25 = operator();

            state._fsp--;

            adaptor.addChild(root_0, operator25.getTree());
            featureId_values_map.get(featureId).add((input.toString(operator25.start, operator25.stop)));
            pushFollow(FOLLOW_compound_value_in_multiple_compounds340);
            compound_value26 = compound_value();

            state._fsp--;

            adaptor.addChild(root_0, compound_value26.getTree());

         }

         retval.stop = input.LT(-1);

         retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
         adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      } catch (RecognitionException re) {
         reportError(re);
         recover(input, re);
         retval.tree = (CommonTree) adaptor.errorNode(input, retval.start, input.LT(-1), re);

      } finally {
      }
      return retval;
   }
   // $ANTLR end "multiple_compounds"

   public static class value_return extends ParserRuleReturnScope {
      CommonTree tree;

      @Override
      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "value"
   // ApplicabilityGrammar.g:76:1: value : (temp= operator )? ID ;
   public final ApplicabilityGrammarParser.value_return value() throws RecognitionException {
      ApplicabilityGrammarParser.value_return retval = new ApplicabilityGrammarParser.value_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token ID27 = null;
      ApplicabilityGrammarParser.operator_return temp = null;

      CommonTree ID27_tree = null;

      try {
         // ApplicabilityGrammar.g:76:16: ( (temp= operator )? ID )
         // ApplicabilityGrammar.g:76:18: (temp= operator )? ID
         {
            root_0 = (CommonTree) adaptor.nil();

            // ApplicabilityGrammar.g:76:22: (temp= operator )?
            int alt10 = 2;
            int LA10_0 = input.LA(1);

            if ((LA10_0 == OR || LA10_0 == AND)) {
               alt10 = 1;
            }
            switch (alt10) {
               case 1:
               // ApplicabilityGrammar.g:76:22: temp= operator
               {
                  pushFollow(FOLLOW_operator_in_value359);
                  temp = operator();

                  state._fsp--;

                  adaptor.addChild(root_0, temp.getTree());

               }
                  break;

            }

            ID27 = (Token) match(input, ID, FOLLOW_ID_in_value362);
            ID27_tree = (CommonTree) adaptor.create(ID27);
            adaptor.addChild(root_0, ID27_tree);

            if ((temp != null ? input.toString(temp.start, temp.stop) : null) != null) {
               featureId_values_map.get(featureId).add((temp != null ? input.toString(temp.start, temp.stop) : null));
            }

            featureId_values_map.get(featureId).add((ID27 != null ? ID27.getText() : null));

         }

         retval.stop = input.LT(-1);

         retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
         adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      } catch (RecognitionException re) {
         reportError(re);
         recover(input, re);
         retval.tree = (CommonTree) adaptor.errorNode(input, retval.start, input.LT(-1), re);

      } finally {
      }
      return retval;
   }
   // $ANTLR end "value"

   public static class operator_return extends ParserRuleReturnScope {
      CommonTree tree;

      @Override
      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "operator"
   // ApplicabilityGrammar.g:84:1: operator : ( AND | OR );
   public final ApplicabilityGrammarParser.operator_return operator() throws RecognitionException {
      ApplicabilityGrammarParser.operator_return retval = new ApplicabilityGrammarParser.operator_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token set28 = null;

      CommonTree set28_tree = null;

      try {
         // ApplicabilityGrammar.g:84:14: ( AND | OR )
         // ApplicabilityGrammar.g:
         {
            root_0 = (CommonTree) adaptor.nil();

            set28 = input.LT(1);
            if (input.LA(1) == OR || input.LA(1) == AND) {
               input.consume();
               adaptor.addChild(root_0, adaptor.create(set28));
               state.errorRecovery = false;
            } else {
               MismatchedSetException mse = new MismatchedSetException(null, input);
               throw mse;
            }

         }

         retval.stop = input.LT(-1);

         retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
         adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      } catch (RecognitionException re) {
         reportError(re);
         recover(input, re);
         retval.tree = (CommonTree) adaptor.errorNode(input, retval.start, input.LT(-1), re);

      } finally {
      }
      return retval;
   }
   // $ANTLR end "operator"

   // Delegated rules

   public static final BitSet FOLLOW_applicability_in_start57 = new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_start59 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_config_applicability_in_applicability80 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_feature_applicability_in_applicability95 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_8_in_config_applicability118 = new BitSet(new long[] {0x0000000000000030L});
   public static final BitSet FOLLOW_config_expressions_in_config_applicability120 =
      new BitSet(new long[] {0x0000000000000230L});
   public static final BitSet FOLLOW_9_in_config_applicability123 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_OR_in_config_expressions133 = new BitSet(new long[] {0x0000000000000020L});
   public static final BitSet FOLLOW_ID_in_config_expressions136 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_10_in_feature_applicability148 = new BitSet(new long[] {0x0000000000000070L});
   public static final BitSet FOLLOW_feature_expressions_in_feature_applicability150 =
      new BitSet(new long[] {0x0000000000000270L});
   public static final BitSet FOLLOW_9_in_feature_applicability153 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_operator_in_feature_expressions167 = new BitSet(new long[] {0x0000000000000070L});
   public static final BitSet FOLLOW_feature_expression_in_feature_expressions170 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ID_in_feature_expression182 = new BitSet(new long[] {0x0000000000000802L});
   public static final BitSet FOLLOW_11_in_feature_expression196 = new BitSet(new long[] {0x0000000000001070L});
   public static final BitSet FOLLOW_feature_value_in_feature_expression200 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_value_in_feature_value232 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_start_compound_in_feature_value236 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_12_in_start_compound254 = new BitSet(new long[] {0x0000000000001070L});
   public static final BitSet FOLLOW_compound_value_in_start_compound269 = new BitSet(new long[] {0x0000000000002000L});
   public static final BitSet FOLLOW_13_in_start_compound280 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_value_in_compound_value292 = new BitSet(new long[] {0x0000000000000072L});
   public static final BitSet FOLLOW_multiple_compounds_in_compound_value297 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_start_compound_in_multiple_compounds314 =
      new BitSet(new long[] {0x0000000000000050L});
   public static final BitSet FOLLOW_operator_in_multiple_compounds326 = new BitSet(new long[] {0x0000000000001070L});
   public static final BitSet FOLLOW_compound_value_in_multiple_compounds340 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_operator_in_value359 = new BitSet(new long[] {0x0000000000000020L});
   public static final BitSet FOLLOW_ID_in_value362 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_set_in_operator0 = new BitSet(new long[] {0x0000000000000002L});

}