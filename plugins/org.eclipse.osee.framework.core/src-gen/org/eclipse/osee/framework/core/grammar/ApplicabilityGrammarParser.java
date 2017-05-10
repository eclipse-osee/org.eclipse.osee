// $ANTLR 3.2 Sep 23, 2009 12:02:23 ApplicabilityGrammar.g 2017-04-26 12:09:41
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
      "WS",
      "'CONFIGURATION['",
      "']'",
      "'FEATURE['",
      "'='",
      "'('",
      "')'"};
   public static final int T__9 = 9;
   public static final int T__8 = 8;
   public static final int OR = 6;
   public static final int AND = 5;
   public static final int T__11 = 11;
   public static final int T__12 = 12;
   public static final int T__13 = 13;
   public static final int ID = 4;
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

   public String[] getTokenNames() {
      return ApplicabilityGrammarParser.tokenNames;
   }

   public String getGrammarFileName() {
      return "ApplicabilityGrammar.g";
   }

   private String applicabilityType = null;
   private String id = null;

   private HashMap<String, List<String>> id_values_map = new HashMap<>();
   private ArrayList<String> operators = new ArrayList<>();

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

      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "start"
   // ApplicabilityGrammar.g:38:1: start : applicability EOF ;
   public final ApplicabilityGrammarParser.start_return start() throws RecognitionException {
      ApplicabilityGrammarParser.start_return retval = new ApplicabilityGrammarParser.start_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token EOF2 = null;
      ApplicabilityGrammarParser.applicability_return applicability1 = null;

      CommonTree EOF2_tree = null;

      try {
         // ApplicabilityGrammar.g:38:24: ( applicability EOF )
         // ApplicabilityGrammar.g:38:27: applicability EOF
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

      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "applicability"
   // ApplicabilityGrammar.g:40:1: applicability : ( config_applicability | feature_applicability );
   public final ApplicabilityGrammarParser.applicability_return applicability() throws RecognitionException {
      ApplicabilityGrammarParser.applicability_return retval = new ApplicabilityGrammarParser.applicability_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      ApplicabilityGrammarParser.config_applicability_return config_applicability3 = null;

      ApplicabilityGrammarParser.feature_applicability_return feature_applicability4 = null;

      try {
         // ApplicabilityGrammar.g:40:25: ( config_applicability | feature_applicability )
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
            // ApplicabilityGrammar.g:40:27: config_applicability
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
            // ApplicabilityGrammar.g:41:11: feature_applicability
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

      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "config_applicability"
   // ApplicabilityGrammar.g:43:1: config_applicability : 'CONFIGURATION[' ( expressions )+ ']' ;
   public final ApplicabilityGrammarParser.config_applicability_return config_applicability() throws RecognitionException {
      ApplicabilityGrammarParser.config_applicability_return retval =
         new ApplicabilityGrammarParser.config_applicability_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token string_literal5 = null;
      Token char_literal7 = null;
      ApplicabilityGrammarParser.expressions_return expressions6 = null;

      CommonTree string_literal5_tree = null;
      CommonTree char_literal7_tree = null;

      try {
         // ApplicabilityGrammar.g:43:25: ( 'CONFIGURATION[' ( expressions )+ ']' )
         // ApplicabilityGrammar.g:43:27: 'CONFIGURATION[' ( expressions )+ ']'
         {
            root_0 = (CommonTree) adaptor.nil();

            string_literal5 = (Token) match(input, 8, FOLLOW_8_in_config_applicability118);
            string_literal5_tree = (CommonTree) adaptor.create(string_literal5);
            adaptor.addChild(root_0, string_literal5_tree);

            // ApplicabilityGrammar.g:43:44: ( expressions )+
            int cnt2 = 0;
            loop2: do {
               int alt2 = 2;
               int LA2_0 = input.LA(1);

               if (((LA2_0 >= ID && LA2_0 <= OR))) {
                  alt2 = 1;
               }

               switch (alt2) {
                  case 1:
                  // ApplicabilityGrammar.g:43:44: expressions
                  {
                     pushFollow(FOLLOW_expressions_in_config_applicability120);
                     expressions6 = expressions();

                     state._fsp--;

                     adaptor.addChild(root_0, expressions6.getTree());

                  }
                     break;

                  default:
                     if (cnt2 >= 1) break loop2;
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

   public static class feature_applicability_return extends ParserRuleReturnScope {
      CommonTree tree;

      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "feature_applicability"
   // ApplicabilityGrammar.g:45:1: feature_applicability : 'FEATURE[' ( expressions )+ ']' ;
   public final ApplicabilityGrammarParser.feature_applicability_return feature_applicability() throws RecognitionException {
      ApplicabilityGrammarParser.feature_applicability_return retval =
         new ApplicabilityGrammarParser.feature_applicability_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token string_literal8 = null;
      Token char_literal10 = null;
      ApplicabilityGrammarParser.expressions_return expressions9 = null;

      CommonTree string_literal8_tree = null;
      CommonTree char_literal10_tree = null;

      try {
         // ApplicabilityGrammar.g:45:25: ( 'FEATURE[' ( expressions )+ ']' )
         // ApplicabilityGrammar.g:45:27: 'FEATURE[' ( expressions )+ ']'
         {
            root_0 = (CommonTree) adaptor.nil();

            string_literal8 = (Token) match(input, 10, FOLLOW_10_in_feature_applicability133);
            string_literal8_tree = (CommonTree) adaptor.create(string_literal8);
            adaptor.addChild(root_0, string_literal8_tree);

            // ApplicabilityGrammar.g:45:38: ( expressions )+
            int cnt3 = 0;
            loop3: do {
               int alt3 = 2;
               int LA3_0 = input.LA(1);

               if (((LA3_0 >= ID && LA3_0 <= OR))) {
                  alt3 = 1;
               }

               switch (alt3) {
                  case 1:
                  // ApplicabilityGrammar.g:45:38: expressions
                  {
                     pushFollow(FOLLOW_expressions_in_feature_applicability135);
                     expressions9 = expressions();

                     state._fsp--;

                     adaptor.addChild(root_0, expressions9.getTree());

                  }
                     break;

                  default:
                     if (cnt3 >= 1) break loop3;
                     EarlyExitException eee = new EarlyExitException(3, input);
                     throw eee;
               }
               cnt3++;
            } while (true);

            char_literal10 = (Token) match(input, 9, FOLLOW_9_in_feature_applicability138);
            char_literal10_tree = (CommonTree) adaptor.create(char_literal10);
            adaptor.addChild(root_0, char_literal10_tree);

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

      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "expressions"
   // ApplicabilityGrammar.g:47:1: expressions : ( ( operator )? expression ) ;
   public final ApplicabilityGrammarParser.expressions_return expressions() throws RecognitionException {
      ApplicabilityGrammarParser.expressions_return retval = new ApplicabilityGrammarParser.expressions_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      ApplicabilityGrammarParser.operator_return operator11 = null;

      ApplicabilityGrammarParser.expression_return expression12 = null;

      try {
         // ApplicabilityGrammar.g:47:17: ( ( ( operator )? expression ) )
         // ApplicabilityGrammar.g:47:19: ( ( operator )? expression )
         {
            root_0 = (CommonTree) adaptor.nil();

            // ApplicabilityGrammar.g:47:19: ( ( operator )? expression )
            // ApplicabilityGrammar.g:47:20: ( operator )? expression
            {
               // ApplicabilityGrammar.g:47:20: ( operator )?
               int alt4 = 2;
               int LA4_0 = input.LA(1);

               if (((LA4_0 >= AND && LA4_0 <= OR))) {
                  alt4 = 1;
               }
               switch (alt4) {
                  case 1:
                  // ApplicabilityGrammar.g:47:20: operator
                  {
                     pushFollow(FOLLOW_operator_in_expressions152);
                     operator11 = operator();

                     state._fsp--;

                     adaptor.addChild(root_0, operator11.getTree());

                  }
                     break;

               }

               pushFollow(FOLLOW_expression_in_expressions155);
               expression12 = expression();

               state._fsp--;

               adaptor.addChild(root_0, expression12.getTree());

            }

            operators.add((operator11 != null ? input.toString(operator11.start, operator11.stop) : null));

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

      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "expression"
   // ApplicabilityGrammar.g:49:1: expression : ID ( '=' temp= val )? ;
   public final ApplicabilityGrammarParser.expression_return expression() throws RecognitionException {
      ApplicabilityGrammarParser.expression_return retval = new ApplicabilityGrammarParser.expression_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token ID13 = null;
      Token char_literal14 = null;
      ApplicabilityGrammarParser.val_return temp = null;

      CommonTree ID13_tree = null;
      CommonTree char_literal14_tree = null;

      try {
         // ApplicabilityGrammar.g:49:13: ( ID ( '=' temp= val )? )
         // ApplicabilityGrammar.g:49:15: ID ( '=' temp= val )?
         {
            root_0 = (CommonTree) adaptor.nil();

            ID13 = (Token) match(input, ID, FOLLOW_ID_in_expression167);
            ID13_tree = (CommonTree) adaptor.create(ID13);
            adaptor.addChild(root_0, ID13_tree);

            id = (ID13 != null ? ID13.getText() : null);
            if (id != null) {
               id = id.trim();
            }
            id_values_map.put(id, new ArrayList<String>());

            // ApplicabilityGrammar.g:52:9: ( '=' temp= val )?
            int alt5 = 2;
            int LA5_0 = input.LA(1);

            if ((LA5_0 == 11)) {
               alt5 = 1;
            }
            switch (alt5) {
               case 1:
               // ApplicabilityGrammar.g:52:10: '=' temp= val
               {
                  char_literal14 = (Token) match(input, 11, FOLLOW_11_in_expression181);
                  char_literal14_tree = (CommonTree) adaptor.create(char_literal14);
                  adaptor.addChild(root_0, char_literal14_tree);

                  pushFollow(FOLLOW_val_in_expression185);
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

      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "val"
   // ApplicabilityGrammar.g:57:1: val : ( value | start_compound );
   public final ApplicabilityGrammarParser.val_return val() throws RecognitionException {
      ApplicabilityGrammarParser.val_return retval = new ApplicabilityGrammarParser.val_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      ApplicabilityGrammarParser.value_return value15 = null;

      ApplicabilityGrammarParser.start_compound_return start_compound16 = null;

      try {
         // ApplicabilityGrammar.g:57:7: ( value | start_compound )
         int alt6 = 2;
         int LA6_0 = input.LA(1);

         if (((LA6_0 >= ID && LA6_0 <= OR))) {
            alt6 = 1;
         } else if ((LA6_0 == 12)) {
            alt6 = 2;
         } else {
            NoViableAltException nvae = new NoViableAltException("", 6, 0, input);

            throw nvae;
         }
         switch (alt6) {
            case 1:
            // ApplicabilityGrammar.g:57:10: value
            {
               root_0 = (CommonTree) adaptor.nil();

               pushFollow(FOLLOW_value_in_val217);
               value15 = value();

               state._fsp--;

               adaptor.addChild(root_0, value15.getTree());

            }
               break;
            case 2:
            // ApplicabilityGrammar.g:57:18: start_compound
            {
               root_0 = (CommonTree) adaptor.nil();

               pushFollow(FOLLOW_start_compound_in_val221);
               start_compound16 = start_compound();

               state._fsp--;

               adaptor.addChild(root_0, start_compound16.getTree());

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

      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "start_compound"
   // ApplicabilityGrammar.g:59:1: start_compound : '(' compound_value ')' ;
   public final ApplicabilityGrammarParser.start_compound_return start_compound() throws RecognitionException {
      ApplicabilityGrammarParser.start_compound_return retval = new ApplicabilityGrammarParser.start_compound_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token char_literal17 = null;
      Token char_literal19 = null;
      ApplicabilityGrammarParser.compound_value_return compound_value18 = null;

      CommonTree char_literal17_tree = null;
      CommonTree char_literal19_tree = null;

      try {
         // ApplicabilityGrammar.g:59:18: ( '(' compound_value ')' )
         // ApplicabilityGrammar.g:59:20: '(' compound_value ')'
         {
            root_0 = (CommonTree) adaptor.nil();

            char_literal17 = (Token) match(input, 12, FOLLOW_12_in_start_compound239);
            char_literal17_tree = (CommonTree) adaptor.create(char_literal17);
            adaptor.addChild(root_0, char_literal17_tree);

            id_values_map.get(id).add("(");
            pushFollow(FOLLOW_compound_value_in_start_compound254);
            compound_value18 = compound_value();

            state._fsp--;

            adaptor.addChild(root_0, compound_value18.getTree());
            char_literal19 = (Token) match(input, 13, FOLLOW_13_in_start_compound265);
            char_literal19_tree = (CommonTree) adaptor.create(char_literal19);
            adaptor.addChild(root_0, char_literal19_tree);

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

      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "compound_value"
   // ApplicabilityGrammar.g:63:1: compound_value : ( ( value )+ | multiple_compounds );
   public final ApplicabilityGrammarParser.compound_value_return compound_value() throws RecognitionException {
      ApplicabilityGrammarParser.compound_value_return retval = new ApplicabilityGrammarParser.compound_value_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      ApplicabilityGrammarParser.value_return value20 = null;

      ApplicabilityGrammarParser.multiple_compounds_return multiple_compounds21 = null;

      try {
         // ApplicabilityGrammar.g:63:18: ( ( value )+ | multiple_compounds )
         int alt8 = 2;
         int LA8_0 = input.LA(1);

         if (((LA8_0 >= ID && LA8_0 <= OR))) {
            alt8 = 1;
         } else if ((LA8_0 == 12)) {
            alt8 = 2;
         } else {
            NoViableAltException nvae = new NoViableAltException("", 8, 0, input);

            throw nvae;
         }
         switch (alt8) {
            case 1:
            // ApplicabilityGrammar.g:63:20: ( value )+
            {
               root_0 = (CommonTree) adaptor.nil();

               // ApplicabilityGrammar.g:63:20: ( value )+
               int cnt7 = 0;
               loop7: do {
                  int alt7 = 2;
                  int LA7_0 = input.LA(1);

                  if (((LA7_0 >= ID && LA7_0 <= OR))) {
                     alt7 = 1;
                  }

                  switch (alt7) {
                     case 1:
                     // ApplicabilityGrammar.g:63:20: value
                     {
                        pushFollow(FOLLOW_value_in_compound_value277);
                        value20 = value();

                        state._fsp--;

                        adaptor.addChild(root_0, value20.getTree());

                     }
                        break;

                     default:
                        if (cnt7 >= 1) break loop7;
                        EarlyExitException eee = new EarlyExitException(7, input);
                        throw eee;
                  }
                  cnt7++;
               } while (true);

            }
               break;
            case 2:
            // ApplicabilityGrammar.g:63:29: multiple_compounds
            {
               root_0 = (CommonTree) adaptor.nil();

               pushFollow(FOLLOW_multiple_compounds_in_compound_value282);
               multiple_compounds21 = multiple_compounds();

               state._fsp--;

               adaptor.addChild(root_0, multiple_compounds21.getTree());

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

      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "multiple_compounds"
   // ApplicabilityGrammar.g:65:1: multiple_compounds : start_compound operator compound_value ;
   public final ApplicabilityGrammarParser.multiple_compounds_return multiple_compounds() throws RecognitionException {
      ApplicabilityGrammarParser.multiple_compounds_return retval =
         new ApplicabilityGrammarParser.multiple_compounds_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      ApplicabilityGrammarParser.start_compound_return start_compound22 = null;

      ApplicabilityGrammarParser.operator_return operator23 = null;

      ApplicabilityGrammarParser.compound_value_return compound_value24 = null;

      try {
         // ApplicabilityGrammar.g:65:22: ( start_compound operator compound_value )
         // ApplicabilityGrammar.g:65:24: start_compound operator compound_value
         {
            root_0 = (CommonTree) adaptor.nil();

            pushFollow(FOLLOW_start_compound_in_multiple_compounds299);
            start_compound22 = start_compound();

            state._fsp--;

            adaptor.addChild(root_0, start_compound22.getTree());
            pushFollow(FOLLOW_operator_in_multiple_compounds311);
            operator23 = operator();

            state._fsp--;

            adaptor.addChild(root_0, operator23.getTree());
            id_values_map.get(id).add(input.toString(operator23.start, operator23.stop));
            pushFollow(FOLLOW_compound_value_in_multiple_compounds325);
            compound_value24 = compound_value();

            state._fsp--;

            adaptor.addChild(root_0, compound_value24.getTree());

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

      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "value"
   // ApplicabilityGrammar.g:69:1: value : (temp= operator )? ID ;
   public final ApplicabilityGrammarParser.value_return value() throws RecognitionException {
      ApplicabilityGrammarParser.value_return retval = new ApplicabilityGrammarParser.value_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token ID25 = null;
      ApplicabilityGrammarParser.operator_return temp = null;

      CommonTree ID25_tree = null;

      try {
         // ApplicabilityGrammar.g:69:16: ( (temp= operator )? ID )
         // ApplicabilityGrammar.g:69:18: (temp= operator )? ID
         {
            root_0 = (CommonTree) adaptor.nil();

            // ApplicabilityGrammar.g:69:22: (temp= operator )?
            int alt9 = 2;
            int LA9_0 = input.LA(1);

            if (((LA9_0 >= AND && LA9_0 <= OR))) {
               alt9 = 1;
            }
            switch (alt9) {
               case 1:
               // ApplicabilityGrammar.g:69:22: temp= operator
               {
                  pushFollow(FOLLOW_operator_in_value344);
                  temp = operator();

                  state._fsp--;

                  adaptor.addChild(root_0, temp.getTree());

               }
                  break;

            }

            ID25 = (Token) match(input, ID, FOLLOW_ID_in_value347);
            ID25_tree = (CommonTree) adaptor.create(ID25);
            adaptor.addChild(root_0, ID25_tree);

            if ((temp != null ? input.toString(temp.start, temp.stop) : null) != null) id_values_map.get(id).add(
               (temp != null ? input.toString(temp.start, temp.stop) : null));
            String id25 = (ID25 != null ? ID25.getText() : null);
            if (id25 != null) {
               id25 = id25.trim();
            }
            id_values_map.get(id).add(id25);

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

      public Object getTree() {
         return tree;
      }
   };

   // $ANTLR start "operator"
   // ApplicabilityGrammar.g:75:1: operator : ( AND | OR );
   public final ApplicabilityGrammarParser.operator_return operator() throws RecognitionException {
      ApplicabilityGrammarParser.operator_return retval = new ApplicabilityGrammarParser.operator_return();
      retval.start = input.LT(1);

      CommonTree root_0 = null;

      Token set26 = null;

      CommonTree set26_tree = null;

      try {
         // ApplicabilityGrammar.g:75:14: ( AND | OR )
         // ApplicabilityGrammar.g:
         {
            root_0 = (CommonTree) adaptor.nil();

            set26 = (Token) input.LT(1);
            if ((input.LA(1) >= AND && input.LA(1) <= OR)) {
               input.consume();
               adaptor.addChild(root_0, (CommonTree) adaptor.create(set26));
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
   public static final BitSet FOLLOW_8_in_config_applicability118 = new BitSet(new long[] {0x0000000000000070L});
   public static final BitSet FOLLOW_expressions_in_config_applicability120 =
      new BitSet(new long[] {0x0000000000000270L});
   public static final BitSet FOLLOW_9_in_config_applicability123 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_10_in_feature_applicability133 = new BitSet(new long[] {0x0000000000000070L});
   public static final BitSet FOLLOW_expressions_in_feature_applicability135 =
      new BitSet(new long[] {0x0000000000000270L});
   public static final BitSet FOLLOW_9_in_feature_applicability138 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_operator_in_expressions152 = new BitSet(new long[] {0x0000000000000070L});
   public static final BitSet FOLLOW_expression_in_expressions155 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ID_in_expression167 = new BitSet(new long[] {0x0000000000000802L});
   public static final BitSet FOLLOW_11_in_expression181 = new BitSet(new long[] {0x0000000000001070L});
   public static final BitSet FOLLOW_val_in_expression185 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_value_in_val217 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_start_compound_in_val221 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_12_in_start_compound239 = new BitSet(new long[] {0x0000000000001070L});
   public static final BitSet FOLLOW_compound_value_in_start_compound254 = new BitSet(new long[] {0x0000000000002000L});
   public static final BitSet FOLLOW_13_in_start_compound265 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_value_in_compound_value277 = new BitSet(new long[] {0x0000000000000072L});
   public static final BitSet FOLLOW_multiple_compounds_in_compound_value282 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_start_compound_in_multiple_compounds299 =
      new BitSet(new long[] {0x0000000000000060L});
   public static final BitSet FOLLOW_operator_in_multiple_compounds311 = new BitSet(new long[] {0x0000000000001070L});
   public static final BitSet FOLLOW_compound_value_in_multiple_compounds325 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_operator_in_value344 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ID_in_value347 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_set_in_operator0 = new BitSet(new long[] {0x0000000000000002L});

}