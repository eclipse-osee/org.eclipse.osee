// $ANTLR 3.2 Sep 23, 2009 12:02:23 ApplicabilityGrammar.g 2017-08-14 12:13:17

package org.eclipse.osee.framework.core.grammar;

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
      "ID",
      "AND",
      "OR",
      "NOT",
      "WS",
      "'CONFIGURATION'",
      "'CONFIGURATION NOT'",
      "'['",
      "']'",
      "'FEATURE['",
      "'='",
      "'('",
      "')'"};
   public static final int T__9 = 9;
   public static final int OR = 6;
   public static final int NOT = 7;
   public static final int T__15 = 15;
   public static final int T__16 = 16;
   public static final int AND = 5;
   public static final int T__11 = 11;
   public static final int T__12 = 12;
   public static final int T__13 = 13;
   public static final int T__14 = 14;
   public static final int ID = 4;
   public static final int WS = 8;
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
   private String id = null;

   private final HashMap<String, List<String>> id_values_map = new HashMap<>();
   private final ArrayList<String> operators = new ArrayList<>();

   public ArrayList<String> getOperators() {
      return operators;
   }

   public HashMap<String, List<String>> getIdValuesMap() {
      return id_values_map;
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
   // ApplicabilityGrammar.g:40:1: start : applicability EOF ;
   public final ApplicabilityGrammarParser.start_return start() throws RecognitionException {
      ApplicabilityGrammarParser.start_return retval = new ApplicabilityGrammarParser.start_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token EOF2 = null;
      ApplicabilityGrammarParser.applicability_return applicability1 = null;

      CommonTree EOF2_tree = null;

      try {
         // ApplicabilityGrammar.g:40:24: ( applicability EOF )
         // ApplicabilityGrammar.g:40:27: applicability EOF
         {
            root_0 = (CommonTree) adaptor.nil();

            pushFollow(FOLLOW_applicability_in_start57);
            applicability1 = applicability();

            state._fsp--;

            adaptor.addChild(root_0, applicability1.getTree());
            EOF2 = (Token) match(input, EOF, FOLLOW_EOF_in_start59);
            operators.removeAll(Collections.singleton(null));

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
   // ApplicabilityGrammar.g:42:1: applicability : ( config_applicability | feature_applicability );
   public final ApplicabilityGrammarParser.applicability_return applicability() throws RecognitionException {
      ApplicabilityGrammarParser.applicability_return retval = new ApplicabilityGrammarParser.applicability_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      ApplicabilityGrammarParser.config_applicability_return config_applicability3 = null;

      ApplicabilityGrammarParser.feature_applicability_return feature_applicability4 = null;

      try {
         // ApplicabilityGrammar.g:42:25: ( config_applicability | feature_applicability )
         int alt1 = 2;
         int LA1_0 = input.LA(1);

         if (((LA1_0 >= 9 && LA1_0 <= 10))) {
            alt1 = 1;
         } else if ((LA1_0 == 13)) {
            alt1 = 2;
         } else {
            NoViableAltException nvae = new NoViableAltException("", 1, 0, input);

            throw nvae;
         }
         switch (alt1) {
            case 1:
            // ApplicabilityGrammar.g:42:27: config_applicability
            {
               root_0 = (CommonTree) adaptor.nil();

               pushFollow(FOLLOW_config_applicability_in_applicability80);
               config_applicability3 = config_applicability();

               state._fsp--;

               adaptor.addChild(root_0, config_applicability3.getTree());
               applicabilityType = "Config";

            }
               break;
            case 2:
            // ApplicabilityGrammar.g:43:11: feature_applicability
            {
               root_0 = (CommonTree) adaptor.nil();

               pushFollow(FOLLOW_feature_applicability_in_applicability95);
               feature_applicability4 = feature_applicability();

               state._fsp--;

               adaptor.addChild(root_0, feature_applicability4.getTree());
               applicabilityType = "Feature";

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
   // ApplicabilityGrammar.g:45:1: config_applicability : ( 'CONFIGURATION' | 'CONFIGURATION NOT' ) '[' ( expressions )+ ']' ;
   public final ApplicabilityGrammarParser.config_applicability_return config_applicability() throws RecognitionException {
      ApplicabilityGrammarParser.config_applicability_return retval =
         new ApplicabilityGrammarParser.config_applicability_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token set5 = null;
      Token char_literal6 = null;
      Token char_literal8 = null;
      ApplicabilityGrammarParser.expressions_return expressions7 = null;

      CommonTree set5_tree = null;
      CommonTree char_literal6_tree = null;
      CommonTree char_literal8_tree = null;

      try {
         // ApplicabilityGrammar.g:45:25: ( ( 'CONFIGURATION' | 'CONFIGURATION NOT' ) '[' ( expressions )+ ']' )
         // ApplicabilityGrammar.g:45:27: ( 'CONFIGURATION' | 'CONFIGURATION NOT' ) '[' ( expressions )+ ']'
         {
            root_0 = (CommonTree) adaptor.nil();

            set5 = input.LT(1);
            if ((input.LA(1) >= 9 && input.LA(1) <= 10)) {
               input.consume();
               adaptor.addChild(root_0, adaptor.create(set5));
               state.errorRecovery = false;
            } else {
               MismatchedSetException mse = new MismatchedSetException(null, input);
               throw mse;
            }

            char_literal6 = (Token) match(input, 11, FOLLOW_11_in_config_applicability123);
            char_literal6_tree = (CommonTree) adaptor.create(char_literal6);
            adaptor.addChild(root_0, char_literal6_tree);

            // ApplicabilityGrammar.g:45:68: ( expressions )+
            int cnt2 = 0;
            loop2: do {
               int alt2 = 2;
               int LA2_0 = input.LA(1);

               if (((LA2_0 >= ID && LA2_0 <= OR))) {
                  alt2 = 1;
               }

               switch (alt2) {
                  case 1:
                  // ApplicabilityGrammar.g:45:68: expressions
                  {
                     pushFollow(FOLLOW_expressions_in_config_applicability125);
                     expressions7 = expressions();

                     state._fsp--;

                     adaptor.addChild(root_0, expressions7.getTree());

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

            char_literal8 = (Token) match(input, 12, FOLLOW_12_in_config_applicability128);
            char_literal8_tree = (CommonTree) adaptor.create(char_literal8);
            adaptor.addChild(root_0, char_literal8_tree);

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

   public static class feature_applicability_return extends ParserRuleReturnScope {
      CommonTree tree;

      @Override
      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "feature_applicability"
   // ApplicabilityGrammar.g:47:1: feature_applicability : 'FEATURE[' ( expressions )+ ']' ;
   public final ApplicabilityGrammarParser.feature_applicability_return feature_applicability() throws RecognitionException {
      ApplicabilityGrammarParser.feature_applicability_return retval =
         new ApplicabilityGrammarParser.feature_applicability_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token string_literal9 = null;
      Token char_literal11 = null;
      ApplicabilityGrammarParser.expressions_return expressions10 = null;

      CommonTree string_literal9_tree = null;
      CommonTree char_literal11_tree = null;

      try {
         // ApplicabilityGrammar.g:47:25: ( 'FEATURE[' ( expressions )+ ']' )
         // ApplicabilityGrammar.g:47:27: 'FEATURE[' ( expressions )+ ']'
         {
            root_0 = (CommonTree) adaptor.nil();

            string_literal9 = (Token) match(input, 13, FOLLOW_13_in_feature_applicability138);
            string_literal9_tree = (CommonTree) adaptor.create(string_literal9);
            adaptor.addChild(root_0, string_literal9_tree);

            // ApplicabilityGrammar.g:47:38: ( expressions )+
            int cnt3 = 0;
            loop3: do {
               int alt3 = 2;
               int LA3_0 = input.LA(1);

               if (((LA3_0 >= ID && LA3_0 <= OR))) {
                  alt3 = 1;
               }

               switch (alt3) {
                  case 1:
                  // ApplicabilityGrammar.g:47:38: expressions
                  {
                     pushFollow(FOLLOW_expressions_in_feature_applicability140);
                     expressions10 = expressions();

                     state._fsp--;

                     adaptor.addChild(root_0, expressions10.getTree());

                  }
                     break;

                  default:
                     if (cnt3 >= 1) {
                        break loop3;
                     }
                     EarlyExitException eee = new EarlyExitException(3, input);
                     throw eee;
               }
               cnt3++;
            } while (true);

            char_literal11 = (Token) match(input, 12, FOLLOW_12_in_feature_applicability143);
            char_literal11_tree = (CommonTree) adaptor.create(char_literal11);
            adaptor.addChild(root_0, char_literal11_tree);

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

   public static class expressions_return extends ParserRuleReturnScope {
      CommonTree tree;

      @Override
      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "expressions"
   // ApplicabilityGrammar.g:49:1: expressions : ( ( operator )? expression ) ;
   public final ApplicabilityGrammarParser.expressions_return expressions() throws RecognitionException {
      ApplicabilityGrammarParser.expressions_return retval = new ApplicabilityGrammarParser.expressions_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      ApplicabilityGrammarParser.operator_return operator12 = null;

      ApplicabilityGrammarParser.expression_return expression13 = null;

      try {
         // ApplicabilityGrammar.g:49:17: ( ( ( operator )? expression ) )
         // ApplicabilityGrammar.g:49:19: ( ( operator )? expression )
         {
            root_0 = (CommonTree) adaptor.nil();

            // ApplicabilityGrammar.g:49:19: ( ( operator )? expression )
            // ApplicabilityGrammar.g:49:20: ( operator )? expression
            {
               // ApplicabilityGrammar.g:49:20: ( operator )?
               int alt4 = 2;
               int LA4_0 = input.LA(1);

               if (((LA4_0 >= AND && LA4_0 <= OR))) {
                  alt4 = 1;
               }
               switch (alt4) {
                  case 1:
                  // ApplicabilityGrammar.g:49:20: operator
                  {
                     pushFollow(FOLLOW_operator_in_expressions157);
                     operator12 = operator();

                     state._fsp--;

                     adaptor.addChild(root_0, operator12.getTree());

                  }
                     break;

               }

               pushFollow(FOLLOW_expression_in_expressions160);
               expression13 = expression();

               state._fsp--;

               adaptor.addChild(root_0, expression13.getTree());

            }

            operators.add((operator12 != null ? input.toString(operator12.start, operator12.stop) : null));

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
   // $ANTLR end "expressions"

   public static class expression_return extends ParserRuleReturnScope {
      CommonTree tree;

      @Override
      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "expression"
   // ApplicabilityGrammar.g:51:1: expression : ID ( '=' temp= val )? ;
   public final ApplicabilityGrammarParser.expression_return expression() throws RecognitionException {
      ApplicabilityGrammarParser.expression_return retval = new ApplicabilityGrammarParser.expression_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token ID14 = null;
      Token char_literal15 = null;
      ApplicabilityGrammarParser.val_return temp = null;

      CommonTree ID14_tree = null;
      CommonTree char_literal15_tree = null;

      try {
         // ApplicabilityGrammar.g:51:13: ( ID ( '=' temp= val )? )
         // ApplicabilityGrammar.g:51:15: ID ( '=' temp= val )?
         {
            root_0 = (CommonTree) adaptor.nil();

            ID14 = (Token) match(input, ID, FOLLOW_ID_in_expression172);
            ID14_tree = (CommonTree) adaptor.create(ID14);
            adaptor.addChild(root_0, ID14_tree);

            if (ID14 != null) {
               id = ID14.getText().trim();
            } else {
               id = null;
            }
            id_values_map.put(id, new ArrayList<String>());

            // ApplicabilityGrammar.g:54:9: ( '=' temp= val )?
            int alt5 = 2;
            int LA5_0 = input.LA(1);

            if ((LA5_0 == 14)) {
               alt5 = 1;
            }
            switch (alt5) {
               case 1:
               // ApplicabilityGrammar.g:54:10: '=' temp= val
               {
                  char_literal15 = (Token) match(input, 14, FOLLOW_14_in_expression186);
                  char_literal15_tree = (CommonTree) adaptor.create(char_literal15);
                  adaptor.addChild(root_0, char_literal15_tree);

                  pushFollow(FOLLOW_val_in_expression190);
                  temp = val();

                  state._fsp--;

                  adaptor.addChild(root_0, temp.getTree());

               }
                  break;

            }

            if ((temp != null ? input.toString(temp.start, temp.stop) : null) == null) {
               id_values_map.put(id, Arrays.asList("Default"));
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
   // $ANTLR end "expression"

   public static class val_return extends ParserRuleReturnScope {
      CommonTree tree;

      @Override
      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "val"
   // ApplicabilityGrammar.g:59:1: val : ( value | start_compound );
   public final ApplicabilityGrammarParser.val_return val() throws RecognitionException {
      ApplicabilityGrammarParser.val_return retval = new ApplicabilityGrammarParser.val_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      ApplicabilityGrammarParser.value_return value16 = null;

      ApplicabilityGrammarParser.start_compound_return start_compound17 = null;

      try {
         // ApplicabilityGrammar.g:59:7: ( value | start_compound )
         int alt6 = 2;
         int LA6_0 = input.LA(1);

         if (((LA6_0 >= ID && LA6_0 <= OR))) {
            alt6 = 1;
         } else if ((LA6_0 == 15)) {
            alt6 = 2;
         } else {
            NoViableAltException nvae = new NoViableAltException("", 6, 0, input);

            throw nvae;
         }
         switch (alt6) {
            case 1:
            // ApplicabilityGrammar.g:59:10: value
            {
               root_0 = (CommonTree) adaptor.nil();

               pushFollow(FOLLOW_value_in_val222);
               value16 = value();

               state._fsp--;

               adaptor.addChild(root_0, value16.getTree());

            }
               break;
            case 2:
            // ApplicabilityGrammar.g:59:18: start_compound
            {
               root_0 = (CommonTree) adaptor.nil();

               pushFollow(FOLLOW_start_compound_in_val226);
               start_compound17 = start_compound();

               state._fsp--;

               adaptor.addChild(root_0, start_compound17.getTree());

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
   // $ANTLR end "val"

   public static class start_compound_return extends ParserRuleReturnScope {
      CommonTree tree;

      @Override
      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "start_compound"
   // ApplicabilityGrammar.g:61:1: start_compound : '(' compound_value ')' ;
   public final ApplicabilityGrammarParser.start_compound_return start_compound() throws RecognitionException {
      ApplicabilityGrammarParser.start_compound_return retval = new ApplicabilityGrammarParser.start_compound_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token char_literal18 = null;
      Token char_literal20 = null;
      ApplicabilityGrammarParser.compound_value_return compound_value19 = null;

      CommonTree char_literal18_tree = null;
      CommonTree char_literal20_tree = null;

      try {
         // ApplicabilityGrammar.g:61:18: ( '(' compound_value ')' )
         // ApplicabilityGrammar.g:61:20: '(' compound_value ')'
         {
            root_0 = (CommonTree) adaptor.nil();

            char_literal18 = (Token) match(input, 15, FOLLOW_15_in_start_compound244);
            char_literal18_tree = (CommonTree) adaptor.create(char_literal18);
            adaptor.addChild(root_0, char_literal18_tree);

            id_values_map.get(id).add("(");
            pushFollow(FOLLOW_compound_value_in_start_compound259);
            compound_value19 = compound_value();

            state._fsp--;

            adaptor.addChild(root_0, compound_value19.getTree());
            char_literal20 = (Token) match(input, 16, FOLLOW_16_in_start_compound270);
            char_literal20_tree = (CommonTree) adaptor.create(char_literal20);
            adaptor.addChild(root_0, char_literal20_tree);

            id_values_map.get(id).add(")");

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
   // ApplicabilityGrammar.g:65:1: compound_value : ( ( value )+ | multiple_compounds );
   public final ApplicabilityGrammarParser.compound_value_return compound_value() throws RecognitionException {
      ApplicabilityGrammarParser.compound_value_return retval = new ApplicabilityGrammarParser.compound_value_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      ApplicabilityGrammarParser.value_return value21 = null;

      ApplicabilityGrammarParser.multiple_compounds_return multiple_compounds22 = null;

      try {
         // ApplicabilityGrammar.g:65:18: ( ( value )+ | multiple_compounds )
         int alt8 = 2;
         int LA8_0 = input.LA(1);

         if (((LA8_0 >= ID && LA8_0 <= OR))) {
            alt8 = 1;
         } else if ((LA8_0 == 15)) {
            alt8 = 2;
         } else {
            NoViableAltException nvae = new NoViableAltException("", 8, 0, input);

            throw nvae;
         }
         switch (alt8) {
            case 1:
            // ApplicabilityGrammar.g:65:20: ( value )+
            {
               root_0 = (CommonTree) adaptor.nil();

               // ApplicabilityGrammar.g:65:20: ( value )+
               int cnt7 = 0;
               loop7: do {
                  int alt7 = 2;
                  int LA7_0 = input.LA(1);

                  if (((LA7_0 >= ID && LA7_0 <= OR))) {
                     alt7 = 1;
                  }

                  switch (alt7) {
                     case 1:
                     // ApplicabilityGrammar.g:65:20: value
                     {
                        pushFollow(FOLLOW_value_in_compound_value282);
                        value21 = value();

                        state._fsp--;

                        adaptor.addChild(root_0, value21.getTree());

                     }
                        break;

                     default:
                        if (cnt7 >= 1) {
                           break loop7;
                        }
                        EarlyExitException eee = new EarlyExitException(7, input);
                        throw eee;
                  }
                  cnt7++;
               } while (true);

            }
               break;
            case 2:
            // ApplicabilityGrammar.g:65:29: multiple_compounds
            {
               root_0 = (CommonTree) adaptor.nil();

               pushFollow(FOLLOW_multiple_compounds_in_compound_value287);
               multiple_compounds22 = multiple_compounds();

               state._fsp--;

               adaptor.addChild(root_0, multiple_compounds22.getTree());

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
   // ApplicabilityGrammar.g:67:1: multiple_compounds : start_compound operator compound_value ;
   public final ApplicabilityGrammarParser.multiple_compounds_return multiple_compounds() throws RecognitionException {
      ApplicabilityGrammarParser.multiple_compounds_return retval =
         new ApplicabilityGrammarParser.multiple_compounds_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      ApplicabilityGrammarParser.start_compound_return start_compound23 = null;

      ApplicabilityGrammarParser.operator_return operator24 = null;

      ApplicabilityGrammarParser.compound_value_return compound_value25 = null;

      try {
         // ApplicabilityGrammar.g:67:22: ( start_compound operator compound_value )
         // ApplicabilityGrammar.g:67:24: start_compound operator compound_value
         {
            root_0 = (CommonTree) adaptor.nil();

            pushFollow(FOLLOW_start_compound_in_multiple_compounds304);
            start_compound23 = start_compound();

            state._fsp--;

            adaptor.addChild(root_0, start_compound23.getTree());
            pushFollow(FOLLOW_operator_in_multiple_compounds316);
            operator24 = operator();

            state._fsp--;

            adaptor.addChild(root_0, operator24.getTree());
            id_values_map.get(id).add((operator24 != null ? input.toString(operator24.start, operator24.stop) : null));
            pushFollow(FOLLOW_compound_value_in_multiple_compounds330);
            compound_value25 = compound_value();

            state._fsp--;

            adaptor.addChild(root_0, compound_value25.getTree());

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
   // ApplicabilityGrammar.g:71:1: value : (temp= operator )? ID ;
   public final ApplicabilityGrammarParser.value_return value() throws RecognitionException {
      ApplicabilityGrammarParser.value_return retval = new ApplicabilityGrammarParser.value_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token ID26 = null;
      ApplicabilityGrammarParser.operator_return temp = null;

      CommonTree ID26_tree = null;

      try {
         // ApplicabilityGrammar.g:71:16: ( (temp= operator )? ID )
         // ApplicabilityGrammar.g:71:18: (temp= operator )? ID
         {
            root_0 = (CommonTree) adaptor.nil();

            // ApplicabilityGrammar.g:71:22: (temp= operator )?
            int alt9 = 2;
            int LA9_0 = input.LA(1);

            if (((LA9_0 >= AND && LA9_0 <= OR))) {
               alt9 = 1;
            }
            switch (alt9) {
               case 1:
               // ApplicabilityGrammar.g:71:22: temp= operator
               {
                  pushFollow(FOLLOW_operator_in_value349);
                  temp = operator();

                  state._fsp--;

                  adaptor.addChild(root_0, temp.getTree());

               }
                  break;

            }

            ID26 = (Token) match(input, ID, FOLLOW_ID_in_value352);
            ID26_tree = (CommonTree) adaptor.create(ID26);
            adaptor.addChild(root_0, ID26_tree);

            if ((temp != null ? input.toString(temp.start, temp.stop) : null) != null) {
               id_values_map.get(id).add((temp != null ? input.toString(temp.start, temp.stop) : null));
            }

            if (ID26 != null) {
               id_values_map.get(id).add(ID26.getText().trim());
            } else {
               id_values_map.get(id).add(null);
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
   // $ANTLR end "value"

   public static class operator_return extends ParserRuleReturnScope {
      CommonTree tree;

      @Override
      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "operator"
   // ApplicabilityGrammar.g:77:1: operator : ( AND | OR );
   public final ApplicabilityGrammarParser.operator_return operator() throws RecognitionException {
      ApplicabilityGrammarParser.operator_return retval = new ApplicabilityGrammarParser.operator_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token set27 = null;

      CommonTree set27_tree = null;

      try {
         // ApplicabilityGrammar.g:77:14: ( AND | OR )
         // ApplicabilityGrammar.g:
         {
            root_0 = (CommonTree) adaptor.nil();

            set27 = input.LT(1);
            if ((input.LA(1) >= AND && input.LA(1) <= OR)) {
               input.consume();
               adaptor.addChild(root_0, adaptor.create(set27));
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
   public static final BitSet FOLLOW_set_in_config_applicability118 = new BitSet(new long[] {0x0000000000000800L});
   public static final BitSet FOLLOW_11_in_config_applicability123 = new BitSet(new long[] {0x0000000000000070L});
   public static final BitSet FOLLOW_expressions_in_config_applicability125 =
      new BitSet(new long[] {0x0000000000001070L});
   public static final BitSet FOLLOW_12_in_config_applicability128 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_13_in_feature_applicability138 = new BitSet(new long[] {0x0000000000000070L});
   public static final BitSet FOLLOW_expressions_in_feature_applicability140 =
      new BitSet(new long[] {0x0000000000001070L});
   public static final BitSet FOLLOW_12_in_feature_applicability143 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_operator_in_expressions157 = new BitSet(new long[] {0x0000000000000070L});
   public static final BitSet FOLLOW_expression_in_expressions160 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ID_in_expression172 = new BitSet(new long[] {0x0000000000004002L});
   public static final BitSet FOLLOW_14_in_expression186 = new BitSet(new long[] {0x0000000000008070L});
   public static final BitSet FOLLOW_val_in_expression190 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_value_in_val222 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_start_compound_in_val226 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_15_in_start_compound244 = new BitSet(new long[] {0x0000000000008070L});
   public static final BitSet FOLLOW_compound_value_in_start_compound259 = new BitSet(new long[] {0x0000000000010000L});
   public static final BitSet FOLLOW_16_in_start_compound270 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_value_in_compound_value282 = new BitSet(new long[] {0x0000000000000072L});
   public static final BitSet FOLLOW_multiple_compounds_in_compound_value287 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_start_compound_in_multiple_compounds304 =
      new BitSet(new long[] {0x0000000000000060L});
   public static final BitSet FOLLOW_operator_in_multiple_compounds316 = new BitSet(new long[] {0x0000000000008070L});
   public static final BitSet FOLLOW_compound_value_in_multiple_compounds330 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_operator_in_value349 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ID_in_value352 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_set_in_operator0 = new BitSet(new long[] {0x0000000000000002L});

}