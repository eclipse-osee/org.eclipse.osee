// $ANTLR 3.2 Sep 23, 2009 12:02:23 ApplicabilityGrammar.g 2020-08-11 07:54:21

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
      "'CONFIGURATIONGROUP'",
      "'CONFIGURATIONGROUP NOT'",
      "'FEATURE['",
      "'='",
      "'('",
      "')'"};
   public static final int T__9 = 9;
   public static final int OR = 6;
   public static final int NOT = 7;
   public static final int T__15 = 15;
   public static final int T__16 = 16;
   public static final int T__17 = 17;
   public static final int T__18 = 18;
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
   // ApplicabilityGrammar.g:42:1: applicability : ( config_applicability | feature_applicability | configurationgroup_applicability );
   public final ApplicabilityGrammarParser.applicability_return applicability() throws RecognitionException {
      ApplicabilityGrammarParser.applicability_return retval = new ApplicabilityGrammarParser.applicability_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      ApplicabilityGrammarParser.config_applicability_return config_applicability3 = null;

      ApplicabilityGrammarParser.feature_applicability_return feature_applicability4 = null;

      ApplicabilityGrammarParser.configurationgroup_applicability_return configurationgroup_applicability5 = null;

      try {
         // ApplicabilityGrammar.g:42:25: ( config_applicability | feature_applicability | configurationgroup_applicability )
         int alt1 = 3;
         switch (input.LA(1)) {
            case 9:
            case 10: {
               alt1 = 1;
            }
               break;
            case 15: {
               alt1 = 2;
            }
               break;
            case 13:
            case 14: {
               alt1 = 3;
            }
               break;
            default:
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
            case 3:
            // ApplicabilityGrammar.g:44:11: configurationgroup_applicability
            {
               root_0 = (CommonTree) adaptor.nil();

               pushFollow(FOLLOW_configurationgroup_applicability_in_applicability110);
               configurationgroup_applicability5 = configurationgroup_applicability();

               state._fsp--;

               adaptor.addChild(root_0, configurationgroup_applicability5.getTree());
               applicabilityType = "ConfigurationGroup";

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
   // ApplicabilityGrammar.g:46:1: config_applicability : ( 'CONFIGURATION' | 'CONFIGURATION NOT' ) '[' ( expressions )+ ']' ;
   public final ApplicabilityGrammarParser.config_applicability_return config_applicability() throws RecognitionException {
      ApplicabilityGrammarParser.config_applicability_return retval =
         new ApplicabilityGrammarParser.config_applicability_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token set6 = null;
      Token char_literal7 = null;
      Token char_literal9 = null;
      ApplicabilityGrammarParser.expressions_return expressions8 = null;

      CommonTree char_literal7_tree = null;
      CommonTree char_literal9_tree = null;

      try {
         // ApplicabilityGrammar.g:46:25: ( ( 'CONFIGURATION' | 'CONFIGURATION NOT' ) '[' ( expressions )+ ']' )
         // ApplicabilityGrammar.g:46:27: ( 'CONFIGURATION' | 'CONFIGURATION NOT' ) '[' ( expressions )+ ']'
         {
            root_0 = (CommonTree) adaptor.nil();

            set6 = input.LT(1);
            if ((input.LA(1) >= 9 && input.LA(1) <= 10)) {
               input.consume();
               adaptor.addChild(root_0, adaptor.create(set6));
               state.errorRecovery = false;
            } else {
               MismatchedSetException mse = new MismatchedSetException(null, input);
               throw mse;
            }

            char_literal7 = (Token) match(input, 11, FOLLOW_11_in_config_applicability137);
            char_literal7_tree = (CommonTree) adaptor.create(char_literal7);
            adaptor.addChild(root_0, char_literal7_tree);

            // ApplicabilityGrammar.g:46:68: ( expressions )+
            int cnt2 = 0;
            loop2: do {
               int alt2 = 2;
               int LA2_0 = input.LA(1);

               if (((LA2_0 >= ID && LA2_0 <= OR))) {
                  alt2 = 1;
               }

               switch (alt2) {
                  case 1:
                  // ApplicabilityGrammar.g:46:68: expressions
                  {
                     pushFollow(FOLLOW_expressions_in_config_applicability139);
                     expressions8 = expressions();

                     state._fsp--;

                     adaptor.addChild(root_0, expressions8.getTree());

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

            char_literal9 = (Token) match(input, 12, FOLLOW_12_in_config_applicability142);
            char_literal9_tree = (CommonTree) adaptor.create(char_literal9);
            adaptor.addChild(root_0, char_literal9_tree);

         }

         retval.stop = input.LT(-1);

         retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
         adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      } catch (RecognitionException re) {
         reportError(re);
         recover(input, re);
         retval.tree = (CommonTree) adaptor.errorNode(input, retval.start, input.LT(-1), re);

      }

      return retval;
   }
   // $ANTLR end "config_applicability"

   public static class configurationgroup_applicability_return extends ParserRuleReturnScope {
      CommonTree tree;
      @Override
      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "configurationgroup_applicability"
   // ApplicabilityGrammar.g:48:1: configurationgroup_applicability : ( 'CONFIGURATIONGROUP' | 'CONFIGURATIONGROUP NOT' ) '[' ( expressions )+ ']' ;
   public final ApplicabilityGrammarParser.configurationgroup_applicability_return configurationgroup_applicability() throws RecognitionException {
      ApplicabilityGrammarParser.configurationgroup_applicability_return retval =
         new ApplicabilityGrammarParser.configurationgroup_applicability_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token set10 = null;
      Token char_literal11 = null;
      Token char_literal13 = null;
      ApplicabilityGrammarParser.expressions_return expressions12 = null;

      CommonTree char_literal11_tree = null;
      CommonTree char_literal13_tree = null;

      try {
         // ApplicabilityGrammar.g:48:34: ( ( 'CONFIGURATIONGROUP' | 'CONFIGURATIONGROUP NOT' ) '[' ( expressions )+ ']' )
         // ApplicabilityGrammar.g:48:36: ( 'CONFIGURATIONGROUP' | 'CONFIGURATIONGROUP NOT' ) '[' ( expressions )+ ']'
         {
            root_0 = (CommonTree) adaptor.nil();

            set10 = input.LT(1);
            if ((input.LA(1) >= 13 && input.LA(1) <= 14)) {
               input.consume();
               adaptor.addChild(root_0, adaptor.create(set10));
               state.errorRecovery = false;
            } else {
               MismatchedSetException mse = new MismatchedSetException(null, input);
               throw mse;
            }

            char_literal11 = (Token) match(input, 11, FOLLOW_11_in_configurationgroup_applicability155);
            char_literal11_tree = (CommonTree) adaptor.create(char_literal11);
            adaptor.addChild(root_0, char_literal11_tree);

            // ApplicabilityGrammar.g:48:87: ( expressions )+
            int cnt3 = 0;
            loop3: do {
               int alt3 = 2;
               int LA3_0 = input.LA(1);

               if (((LA3_0 >= ID && LA3_0 <= OR))) {
                  alt3 = 1;
               }

               switch (alt3) {
                  case 1:
                  // ApplicabilityGrammar.g:48:87: expressions
                  {
                     pushFollow(FOLLOW_expressions_in_configurationgroup_applicability157);
                     expressions12 = expressions();

                     state._fsp--;

                     adaptor.addChild(root_0, expressions12.getTree());

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

            char_literal13 = (Token) match(input, 12, FOLLOW_12_in_configurationgroup_applicability160);
            char_literal13_tree = (CommonTree) adaptor.create(char_literal13);
            adaptor.addChild(root_0, char_literal13_tree);

         }

         retval.stop = input.LT(-1);

         retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
         adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      } catch (RecognitionException re) {
         reportError(re);
         recover(input, re);
         retval.tree = (CommonTree) adaptor.errorNode(input, retval.start, input.LT(-1), re);

      }

      return retval;
   }
   // $ANTLR end "configurationgroup_applicability"

   public static class feature_applicability_return extends ParserRuleReturnScope {
      CommonTree tree;
      @Override
      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "feature_applicability"
   // ApplicabilityGrammar.g:50:1: feature_applicability : 'FEATURE[' ( expressions )+ ']' ;
   public final ApplicabilityGrammarParser.feature_applicability_return feature_applicability() throws RecognitionException {
      ApplicabilityGrammarParser.feature_applicability_return retval =
         new ApplicabilityGrammarParser.feature_applicability_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token string_literal14 = null;
      Token char_literal16 = null;
      ApplicabilityGrammarParser.expressions_return expressions15 = null;

      CommonTree string_literal14_tree = null;
      CommonTree char_literal16_tree = null;

      try {
         // ApplicabilityGrammar.g:50:25: ( 'FEATURE[' ( expressions )+ ']' )
         // ApplicabilityGrammar.g:50:27: 'FEATURE[' ( expressions )+ ']'
         {
            root_0 = (CommonTree) adaptor.nil();

            string_literal14 = (Token) match(input, 15, FOLLOW_15_in_feature_applicability170);
            string_literal14_tree = (CommonTree) adaptor.create(string_literal14);
            adaptor.addChild(root_0, string_literal14_tree);

            // ApplicabilityGrammar.g:50:38: ( expressions )+
            int cnt4 = 0;
            loop4: do {
               int alt4 = 2;
               int LA4_0 = input.LA(1);

               if (((LA4_0 >= ID && LA4_0 <= OR))) {
                  alt4 = 1;
               }

               switch (alt4) {
                  case 1:
                  // ApplicabilityGrammar.g:50:38: expressions
                  {
                     pushFollow(FOLLOW_expressions_in_feature_applicability172);
                     expressions15 = expressions();

                     state._fsp--;

                     adaptor.addChild(root_0, expressions15.getTree());

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

            char_literal16 = (Token) match(input, 12, FOLLOW_12_in_feature_applicability175);
            char_literal16_tree = (CommonTree) adaptor.create(char_literal16);
            adaptor.addChild(root_0, char_literal16_tree);

         }

         retval.stop = input.LT(-1);

         retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
         adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      } catch (RecognitionException re) {
         reportError(re);
         recover(input, re);
         retval.tree = (CommonTree) adaptor.errorNode(input, retval.start, input.LT(-1), re);

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
   // ApplicabilityGrammar.g:52:1: expressions : ( ( operator )? expression ) ;
   public final ApplicabilityGrammarParser.expressions_return expressions() throws RecognitionException {
      ApplicabilityGrammarParser.expressions_return retval = new ApplicabilityGrammarParser.expressions_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      ApplicabilityGrammarParser.operator_return operator17 = null;

      ApplicabilityGrammarParser.expression_return expression18 = null;

      try {
         // ApplicabilityGrammar.g:52:17: ( ( ( operator )? expression ) )
         // ApplicabilityGrammar.g:52:19: ( ( operator )? expression )
         {
            root_0 = (CommonTree) adaptor.nil();

            // ApplicabilityGrammar.g:52:19: ( ( operator )? expression )
            // ApplicabilityGrammar.g:52:20: ( operator )? expression
            {
               // ApplicabilityGrammar.g:52:20: ( operator )?
               int alt5 = 2;
               int LA5_0 = input.LA(1);

               if (((LA5_0 >= AND && LA5_0 <= OR))) {
                  alt5 = 1;
               }
               switch (alt5) {
                  case 1:
                  // ApplicabilityGrammar.g:52:20: operator
                  {
                     pushFollow(FOLLOW_operator_in_expressions189);
                     operator17 = operator();

                     state._fsp--;

                     adaptor.addChild(root_0, operator17.getTree());

                  }
                     break;

               }

               pushFollow(FOLLOW_expression_in_expressions192);
               expression18 = expression();

               state._fsp--;

               adaptor.addChild(root_0, expression18.getTree());

            }

            operators.add((operator17 != null ? input.toString(operator17.start, operator17.stop) : null));

         }

         retval.stop = input.LT(-1);

         retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
         adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      } catch (RecognitionException re) {
         reportError(re);
         recover(input, re);
         retval.tree = (CommonTree) adaptor.errorNode(input, retval.start, input.LT(-1), re);

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
   // ApplicabilityGrammar.g:54:1: expression : ID ( '=' temp= val )? ;
   public final ApplicabilityGrammarParser.expression_return expression() throws RecognitionException {
      ApplicabilityGrammarParser.expression_return retval = new ApplicabilityGrammarParser.expression_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token ID19 = null;
      Token char_literal20 = null;
      ApplicabilityGrammarParser.val_return temp = null;

      CommonTree ID19_tree = null;
      CommonTree char_literal20_tree = null;

      try {
         // ApplicabilityGrammar.g:54:13: ( ID ( '=' temp= val )? )
         // ApplicabilityGrammar.g:54:15: ID ( '=' temp= val )?
         {
            root_0 = (CommonTree) adaptor.nil();

            ID19 = (Token) match(input, ID, FOLLOW_ID_in_expression204);
            ID19_tree = (CommonTree) adaptor.create(ID19);
            adaptor.addChild(root_0, ID19_tree);

            id = (ID19 != null ? ID19.getText() : null).trim();
            id_values_map.put(id, new ArrayList<String>());

            // ApplicabilityGrammar.g:57:9: ( '=' temp= val )?
            int alt6 = 2;
            int LA6_0 = input.LA(1);

            if ((LA6_0 == 16)) {
               alt6 = 1;
            }
            switch (alt6) {
               case 1:
               // ApplicabilityGrammar.g:57:10: '=' temp= val
               {
                  char_literal20 = (Token) match(input, 16, FOLLOW_16_in_expression218);
                  char_literal20_tree = (CommonTree) adaptor.create(char_literal20);
                  adaptor.addChild(root_0, char_literal20_tree);

                  pushFollow(FOLLOW_val_in_expression222);
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
   // ApplicabilityGrammar.g:62:1: val : ( value | start_compound );
   public final ApplicabilityGrammarParser.val_return val() throws RecognitionException {
      ApplicabilityGrammarParser.val_return retval = new ApplicabilityGrammarParser.val_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      ApplicabilityGrammarParser.value_return value21 = null;

      ApplicabilityGrammarParser.start_compound_return start_compound22 = null;

      try {
         // ApplicabilityGrammar.g:62:7: ( value | start_compound )
         int alt7 = 2;
         int LA7_0 = input.LA(1);

         if (((LA7_0 >= ID && LA7_0 <= OR))) {
            alt7 = 1;
         } else if ((LA7_0 == 17)) {
            alt7 = 2;
         } else {
            NoViableAltException nvae = new NoViableAltException("", 7, 0, input);

            throw nvae;
         }
         switch (alt7) {
            case 1:
            // ApplicabilityGrammar.g:62:10: value
            {
               root_0 = (CommonTree) adaptor.nil();

               pushFollow(FOLLOW_value_in_val254);
               value21 = value();

               state._fsp--;

               adaptor.addChild(root_0, value21.getTree());

            }
               break;
            case 2:
            // ApplicabilityGrammar.g:62:18: start_compound
            {
               root_0 = (CommonTree) adaptor.nil();

               pushFollow(FOLLOW_start_compound_in_val258);
               start_compound22 = start_compound();

               state._fsp--;

               adaptor.addChild(root_0, start_compound22.getTree());

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
   // ApplicabilityGrammar.g:64:1: start_compound : '(' compound_value ')' ;
   public final ApplicabilityGrammarParser.start_compound_return start_compound() throws RecognitionException {
      ApplicabilityGrammarParser.start_compound_return retval = new ApplicabilityGrammarParser.start_compound_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token char_literal23 = null;
      Token char_literal25 = null;
      ApplicabilityGrammarParser.compound_value_return compound_value24 = null;

      CommonTree char_literal23_tree = null;
      CommonTree char_literal25_tree = null;

      try {
         // ApplicabilityGrammar.g:64:18: ( '(' compound_value ')' )
         // ApplicabilityGrammar.g:64:20: '(' compound_value ')'
         {
            root_0 = (CommonTree) adaptor.nil();

            char_literal23 = (Token) match(input, 17, FOLLOW_17_in_start_compound276);
            char_literal23_tree = (CommonTree) adaptor.create(char_literal23);
            adaptor.addChild(root_0, char_literal23_tree);

            id_values_map.get(id).add("(");
            pushFollow(FOLLOW_compound_value_in_start_compound291);
            compound_value24 = compound_value();

            state._fsp--;

            adaptor.addChild(root_0, compound_value24.getTree());
            char_literal25 = (Token) match(input, 18, FOLLOW_18_in_start_compound302);
            char_literal25_tree = (CommonTree) adaptor.create(char_literal25);
            adaptor.addChild(root_0, char_literal25_tree);

            id_values_map.get(id).add(")");

         }

         retval.stop = input.LT(-1);

         retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
         adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      } catch (RecognitionException re) {
         reportError(re);
         recover(input, re);
         retval.tree = (CommonTree) adaptor.errorNode(input, retval.start, input.LT(-1), re);

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
   // ApplicabilityGrammar.g:68:1: compound_value : ( ( value )+ | multiple_compounds );
   public final ApplicabilityGrammarParser.compound_value_return compound_value() throws RecognitionException {
      ApplicabilityGrammarParser.compound_value_return retval = new ApplicabilityGrammarParser.compound_value_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      ApplicabilityGrammarParser.value_return value26 = null;

      ApplicabilityGrammarParser.multiple_compounds_return multiple_compounds27 = null;

      try {
         // ApplicabilityGrammar.g:68:18: ( ( value )+ | multiple_compounds )
         int alt9 = 2;
         int LA9_0 = input.LA(1);

         if (((LA9_0 >= ID && LA9_0 <= OR))) {
            alt9 = 1;
         } else if ((LA9_0 == 17)) {
            alt9 = 2;
         } else {
            NoViableAltException nvae = new NoViableAltException("", 9, 0, input);

            throw nvae;
         }
         switch (alt9) {
            case 1:
            // ApplicabilityGrammar.g:68:20: ( value )+
            {
               root_0 = (CommonTree) adaptor.nil();

               // ApplicabilityGrammar.g:68:20: ( value )+
               int cnt8 = 0;
               loop8: do {
                  int alt8 = 2;
                  int LA8_0 = input.LA(1);

                  if (((LA8_0 >= ID && LA8_0 <= OR))) {
                     alt8 = 1;
                  }

                  switch (alt8) {
                     case 1:
                     // ApplicabilityGrammar.g:68:20: value
                     {
                        pushFollow(FOLLOW_value_in_compound_value314);
                        value26 = value();

                        state._fsp--;

                        adaptor.addChild(root_0, value26.getTree());

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
            // ApplicabilityGrammar.g:68:29: multiple_compounds
            {
               root_0 = (CommonTree) adaptor.nil();

               pushFollow(FOLLOW_multiple_compounds_in_compound_value319);
               multiple_compounds27 = multiple_compounds();

               state._fsp--;

               adaptor.addChild(root_0, multiple_compounds27.getTree());

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
   // ApplicabilityGrammar.g:70:1: multiple_compounds : start_compound operator compound_value ;
   public final ApplicabilityGrammarParser.multiple_compounds_return multiple_compounds() throws RecognitionException {
      ApplicabilityGrammarParser.multiple_compounds_return retval =
         new ApplicabilityGrammarParser.multiple_compounds_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      ApplicabilityGrammarParser.start_compound_return start_compound28 = null;

      ApplicabilityGrammarParser.operator_return operator29 = null;

      ApplicabilityGrammarParser.compound_value_return compound_value30 = null;

      try {
         // ApplicabilityGrammar.g:70:22: ( start_compound operator compound_value )
         // ApplicabilityGrammar.g:70:24: start_compound operator compound_value
         {
            root_0 = (CommonTree) adaptor.nil();

            pushFollow(FOLLOW_start_compound_in_multiple_compounds336);
            start_compound28 = start_compound();

            state._fsp--;

            adaptor.addChild(root_0, start_compound28.getTree());
            pushFollow(FOLLOW_operator_in_multiple_compounds348);
            operator29 = operator();

            state._fsp--;

            adaptor.addChild(root_0, operator29.getTree());
            id_values_map.get(id).add((operator29 != null ? input.toString(operator29.start, operator29.stop) : null));
            pushFollow(FOLLOW_compound_value_in_multiple_compounds362);
            compound_value30 = compound_value();

            state._fsp--;

            adaptor.addChild(root_0, compound_value30.getTree());

         }

         retval.stop = input.LT(-1);

         retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
         adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      } catch (RecognitionException re) {
         reportError(re);
         recover(input, re);
         retval.tree = (CommonTree) adaptor.errorNode(input, retval.start, input.LT(-1), re);

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
   // ApplicabilityGrammar.g:74:1: value : (temp= operator )? ID ;
   public final ApplicabilityGrammarParser.value_return value() throws RecognitionException {
      ApplicabilityGrammarParser.value_return retval = new ApplicabilityGrammarParser.value_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token ID31 = null;
      ApplicabilityGrammarParser.operator_return temp = null;

      CommonTree ID31_tree = null;

      try {
         // ApplicabilityGrammar.g:74:16: ( (temp= operator )? ID )
         // ApplicabilityGrammar.g:74:18: (temp= operator )? ID
         {
            root_0 = (CommonTree) adaptor.nil();

            // ApplicabilityGrammar.g:74:22: (temp= operator )?
            int alt10 = 2;
            int LA10_0 = input.LA(1);

            if (((LA10_0 >= AND && LA10_0 <= OR))) {
               alt10 = 1;
            }
            switch (alt10) {
               case 1:
               // ApplicabilityGrammar.g:74:22: temp= operator
               {
                  pushFollow(FOLLOW_operator_in_value381);
                  temp = operator();

                  state._fsp--;

                  adaptor.addChild(root_0, temp.getTree());

               }
                  break;

            }

            ID31 = (Token) match(input, ID, FOLLOW_ID_in_value384);
            ID31_tree = (CommonTree) adaptor.create(ID31);
            adaptor.addChild(root_0, ID31_tree);

            if ((temp != null ? input.toString(temp.start, temp.stop) : null) != null) {
               id_values_map.get(id).add((temp != null ? input.toString(temp.start, temp.stop) : null));
            }
            id_values_map.get(id).add((ID31 != null ? ID31.getText() : null).trim());

         }

         retval.stop = input.LT(-1);

         retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
         adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      } catch (RecognitionException re) {
         reportError(re);
         recover(input, re);
         retval.tree = (CommonTree) adaptor.errorNode(input, retval.start, input.LT(-1), re);

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
   // ApplicabilityGrammar.g:80:1: operator : ( AND | OR );
   public final ApplicabilityGrammarParser.operator_return operator() throws RecognitionException {
      ApplicabilityGrammarParser.operator_return retval = new ApplicabilityGrammarParser.operator_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token set32 = null;

      try {
         // ApplicabilityGrammar.g:80:14: ( AND | OR )
         // ApplicabilityGrammar.g:
         {
            root_0 = (CommonTree) adaptor.nil();

            set32 = input.LT(1);
            if ((input.LA(1) >= AND && input.LA(1) <= OR)) {
               input.consume();
               adaptor.addChild(root_0, adaptor.create(set32));
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
   public static final BitSet FOLLOW_configurationgroup_applicability_in_applicability110 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_set_in_config_applicability132 = new BitSet(new long[] {0x0000000000000800L});
   public static final BitSet FOLLOW_11_in_config_applicability137 = new BitSet(new long[] {0x0000000000000070L});
   public static final BitSet FOLLOW_expressions_in_config_applicability139 =
      new BitSet(new long[] {0x0000000000001070L});
   public static final BitSet FOLLOW_12_in_config_applicability142 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_set_in_configurationgroup_applicability150 =
      new BitSet(new long[] {0x0000000000000800L});
   public static final BitSet FOLLOW_11_in_configurationgroup_applicability155 =
      new BitSet(new long[] {0x0000000000000070L});
   public static final BitSet FOLLOW_expressions_in_configurationgroup_applicability157 =
      new BitSet(new long[] {0x0000000000001070L});
   public static final BitSet FOLLOW_12_in_configurationgroup_applicability160 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_15_in_feature_applicability170 = new BitSet(new long[] {0x0000000000000070L});
   public static final BitSet FOLLOW_expressions_in_feature_applicability172 =
      new BitSet(new long[] {0x0000000000001070L});
   public static final BitSet FOLLOW_12_in_feature_applicability175 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_operator_in_expressions189 = new BitSet(new long[] {0x0000000000000070L});
   public static final BitSet FOLLOW_expression_in_expressions192 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ID_in_expression204 = new BitSet(new long[] {0x0000000000010002L});
   public static final BitSet FOLLOW_16_in_expression218 = new BitSet(new long[] {0x0000000000020070L});
   public static final BitSet FOLLOW_val_in_expression222 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_value_in_val254 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_start_compound_in_val258 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_17_in_start_compound276 = new BitSet(new long[] {0x0000000000020070L});
   public static final BitSet FOLLOW_compound_value_in_start_compound291 = new BitSet(new long[] {0x0000000000040000L});
   public static final BitSet FOLLOW_18_in_start_compound302 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_value_in_compound_value314 = new BitSet(new long[] {0x0000000000000072L});
   public static final BitSet FOLLOW_multiple_compounds_in_compound_value319 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_start_compound_in_multiple_compounds336 =
      new BitSet(new long[] {0x0000000000000060L});
   public static final BitSet FOLLOW_operator_in_multiple_compounds348 = new BitSet(new long[] {0x0000000000020070L});
   public static final BitSet FOLLOW_compound_value_in_multiple_compounds362 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_operator_in_value381 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ID_in_value384 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_set_in_operator0 = new BitSet(new long[] {0x0000000000000002L});

}