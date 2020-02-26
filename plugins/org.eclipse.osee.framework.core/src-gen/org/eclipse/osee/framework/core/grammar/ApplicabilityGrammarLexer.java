// $ANTLR 3.2 Sep 23, 2009 12:02:23 ApplicabilityGrammar.g 2020-03-03 12:17:20
package org.eclipse.osee.framework.core.grammar;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;

public class ApplicabilityGrammarLexer extends Lexer {
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

   public ApplicabilityGrammarLexer() {
      ;
   }

   public ApplicabilityGrammarLexer(CharStream input) {
      this(input, new RecognizerSharedState());
   }

   public ApplicabilityGrammarLexer(CharStream input, RecognizerSharedState state) {
      super(input, state);

   }

   @Override
   public String getGrammarFileName() {
      return "ApplicabilityGrammar.g";
   }

   // $ANTLR start "T__9"
   public final void mT__9() throws RecognitionException {
      try {
         int _type = T__9;
         int _channel = DEFAULT_TOKEN_CHANNEL;
         // ApplicabilityGrammar.g:3:6: ( 'CONFIGURATION' )
         // ApplicabilityGrammar.g:3:8: 'CONFIGURATION'
         {
            match("CONFIGURATION");

         }

         state.type = _type;
         state.channel = _channel;
      } finally {
      }
   }
   // $ANTLR end "T__9"

   // $ANTLR start "T__10"
   public final void mT__10() throws RecognitionException {
      try {
         int _type = T__10;
         int _channel = DEFAULT_TOKEN_CHANNEL;
         // ApplicabilityGrammar.g:4:7: ( 'CONFIGURATION NOT' )
         // ApplicabilityGrammar.g:4:9: 'CONFIGURATION NOT'
         {
            match("CONFIGURATION NOT");

         }

         state.type = _type;
         state.channel = _channel;
      } finally {
      }
   }
   // $ANTLR end "T__10"

   // $ANTLR start "T__11"
   public final void mT__11() throws RecognitionException {
      try {
         int _type = T__11;
         int _channel = DEFAULT_TOKEN_CHANNEL;
         // ApplicabilityGrammar.g:5:7: ( '[' )
         // ApplicabilityGrammar.g:5:9: '['
         {
            match('[');

         }

         state.type = _type;
         state.channel = _channel;
      } finally {
      }
   }
   // $ANTLR end "T__11"

   // $ANTLR start "T__12"
   public final void mT__12() throws RecognitionException {
      try {
         int _type = T__12;
         int _channel = DEFAULT_TOKEN_CHANNEL;
         // ApplicabilityGrammar.g:6:7: ( ']' )
         // ApplicabilityGrammar.g:6:9: ']'
         {
            match(']');

         }

         state.type = _type;
         state.channel = _channel;
      } finally {
      }
   }
   // $ANTLR end "T__12"

   // $ANTLR start "T__13"
   public final void mT__13() throws RecognitionException {
      try {
         int _type = T__13;
         int _channel = DEFAULT_TOKEN_CHANNEL;
         // ApplicabilityGrammar.g:7:7: ( 'FEATURE[' )
         // ApplicabilityGrammar.g:7:9: 'FEATURE['
         {
            match("FEATURE[");

         }

         state.type = _type;
         state.channel = _channel;
      } finally {
      }
   }
   // $ANTLR end "T__13"

   // $ANTLR start "T__14"
   public final void mT__14() throws RecognitionException {
      try {
         int _type = T__14;
         int _channel = DEFAULT_TOKEN_CHANNEL;
         // ApplicabilityGrammar.g:8:7: ( '=' )
         // ApplicabilityGrammar.g:8:9: '='
         {
            match('=');

         }

         state.type = _type;
         state.channel = _channel;
      } finally {
      }
   }
   // $ANTLR end "T__14"

   // $ANTLR start "T__15"
   public final void mT__15() throws RecognitionException {
      try {
         int _type = T__15;
         int _channel = DEFAULT_TOKEN_CHANNEL;
         // ApplicabilityGrammar.g:9:7: ( '(' )
         // ApplicabilityGrammar.g:9:9: '('
         {
            match('(');

         }

         state.type = _type;
         state.channel = _channel;
      } finally {
      }
   }
   // $ANTLR end "T__15"

   // $ANTLR start "T__16"
   public final void mT__16() throws RecognitionException {
      try {
         int _type = T__16;
         int _channel = DEFAULT_TOKEN_CHANNEL;
         // ApplicabilityGrammar.g:10:7: ( ')' )
         // ApplicabilityGrammar.g:10:9: ')'
         {
            match(')');

         }

         state.type = _type;
         state.channel = _channel;
      } finally {
      }
   }
   // $ANTLR end "T__16"

   // $ANTLR start "OR"
   public final void mOR() throws RecognitionException {
      try {
         int _type = OR;
         int _channel = DEFAULT_TOKEN_CHANNEL;
         // ApplicabilityGrammar.g:78:10: ( '|' )
         // ApplicabilityGrammar.g:78:12: '|'
         {
            match('|');

         }

         state.type = _type;
         state.channel = _channel;
      } finally {
      }
   }
   // $ANTLR end "OR"

   // $ANTLR start "AND"
   public final void mAND() throws RecognitionException {
      try {
         int _type = AND;
         int _channel = DEFAULT_TOKEN_CHANNEL;
         // ApplicabilityGrammar.g:79:10: ( '&' )
         // ApplicabilityGrammar.g:79:12: '&'
         {
            match('&');

         }

         state.type = _type;
         state.channel = _channel;
      } finally {
      }
   }
   // $ANTLR end "AND"

   // $ANTLR start "NOT"
   public final void mNOT() throws RecognitionException {
      try {
         int _type = NOT;
         int _channel = DEFAULT_TOKEN_CHANNEL;
         // ApplicabilityGrammar.g:80:10: ( 'NOT' )
         // ApplicabilityGrammar.g:80:12: 'NOT'
         {
            match("NOT");

         }

         state.type = _type;
         state.channel = _channel;
      } finally {
      }
   }
   // $ANTLR end "NOT"

   // $ANTLR start "ID"
   public final void mID() throws RecognitionException {
      try {
         int _type = ID;
         int _channel = DEFAULT_TOKEN_CHANNEL;
         // ApplicabilityGrammar.g:81:4: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' | ' ' | '(' | ')' )* )
         // ApplicabilityGrammar.g:81:6: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' | ' ' | '(' | ')' )*
         {
            if ((input.LA(1) >= 'A' && input.LA(1) <= 'Z') || (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
               input.consume();

            } else {
               MismatchedSetException mse = new MismatchedSetException(null, input);
               recover(mse);
               throw mse;
            }

            // ApplicabilityGrammar.g:81:25: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' | ' ' | '(' | ')' )*
            loop1: do {
               int alt1 = 2;
               int LA1_0 = input.LA(1);

               if ((LA1_0 == ' ' || (LA1_0 >= '(' && LA1_0 <= ')') || LA1_0 == '-' || (LA1_0 >= '0' && LA1_0 <= '9') || (LA1_0 >= 'A' && LA1_0 <= 'Z') || LA1_0 == '_' || (LA1_0 >= 'a' && LA1_0 <= 'z'))) {
                  alt1 = 1;
               }

               switch (alt1) {
                  case 1:
                  // ApplicabilityGrammar.g:
                  {
                     if (input.LA(1) == ' ' || (input.LA(1) >= '(' && input.LA(1) <= ')') || input.LA(
                        1) == '-' || (input.LA(1) >= '0' && input.LA(1) <= '9') || (input.LA(
                           1) >= 'A' && input.LA(1) <= 'Z') || input.LA(
                              1) == '_' || (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
                        input.consume();

                     } else {
                        MismatchedSetException mse = new MismatchedSetException(null, input);
                        recover(mse);
                        throw mse;
                     }

                  }
                     break;

                  default:
                     break loop1;
               }
            } while (true);

         }

         state.type = _type;
         state.channel = _channel;
      } finally {
      }
   }
   // $ANTLR end "ID"

   // $ANTLR start "WS"
   public final void mWS() throws RecognitionException {
      try {
         int _type = WS;
         int _channel = DEFAULT_TOKEN_CHANNEL;
         // ApplicabilityGrammar.g:83:4: ( ( ' ' | '\\r' | '\\t' | '\\n' )+ )
         // ApplicabilityGrammar.g:83:6: ( ' ' | '\\r' | '\\t' | '\\n' )+
         {
            // ApplicabilityGrammar.g:83:6: ( ' ' | '\\r' | '\\t' | '\\n' )+
            int cnt2 = 0;
            loop2: do {
               int alt2 = 2;
               int LA2_0 = input.LA(1);

               if (((LA2_0 >= '\t' && LA2_0 <= '\n') || LA2_0 == '\r' || LA2_0 == ' ')) {
                  alt2 = 1;
               }

               switch (alt2) {
                  case 1:
                  // ApplicabilityGrammar.g:
                  {
                     if ((input.LA(1) >= '\t' && input.LA(1) <= '\n') || input.LA(1) == '\r' || input.LA(1) == ' ') {
                        input.consume();

                     } else {
                        MismatchedSetException mse = new MismatchedSetException(null, input);
                        recover(mse);
                        throw mse;
                     }

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

            _channel = HIDDEN;

         }

         state.type = _type;
         state.channel = _channel;
      } finally {
      }
   }
   // $ANTLR end "WS"

   @Override
   public void mTokens() throws RecognitionException {
      // ApplicabilityGrammar.g:1:8: ( T__9 | T__10 | T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | OR | AND | NOT | ID | WS )
      int alt3 = 13;
      alt3 = dfa3.predict(input);
      switch (alt3) {
         case 1:
         // ApplicabilityGrammar.g:1:10: T__9
         {
            mT__9();

         }
            break;
         case 2:
         // ApplicabilityGrammar.g:1:15: T__10
         {
            mT__10();

         }
            break;
         case 3:
         // ApplicabilityGrammar.g:1:21: T__11
         {
            mT__11();

         }
            break;
         case 4:
         // ApplicabilityGrammar.g:1:27: T__12
         {
            mT__12();

         }
            break;
         case 5:
         // ApplicabilityGrammar.g:1:33: T__13
         {
            mT__13();

         }
            break;
         case 6:
         // ApplicabilityGrammar.g:1:39: T__14
         {
            mT__14();

         }
            break;
         case 7:
         // ApplicabilityGrammar.g:1:45: T__15
         {
            mT__15();

         }
            break;
         case 8:
         // ApplicabilityGrammar.g:1:51: T__16
         {
            mT__16();

         }
            break;
         case 9:
         // ApplicabilityGrammar.g:1:57: OR
         {
            mOR();

         }
            break;
         case 10:
         // ApplicabilityGrammar.g:1:60: AND
         {
            mAND();

         }
            break;
         case 11:
         // ApplicabilityGrammar.g:1:64: NOT
         {
            mNOT();

         }
            break;
         case 12:
         // ApplicabilityGrammar.g:1:68: ID
         {
            mID();

         }
            break;
         case 13:
         // ApplicabilityGrammar.g:1:71: WS
         {
            mWS();

         }
            break;

      }

   }

   protected DFA3 dfa3 = new DFA3(this);
   static final String DFA3_eotS =
      "\1\uffff\1\13\2\uffff\1\13\5\uffff\1\13\2\uffff\5\13\1\25\2\13" + "\1\uffff\7\13\1\uffff\4\13\1\44\1\13\1\uffff\2\13\1\50\1\uffff";
   static final String DFA3_eofS = "\51\uffff";
   static final String DFA3_minS =
      "\1\11\1\117\2\uffff\1\105\5\uffff\1\117\2\uffff\1\116\1\101\1\124" + "\1\106\1\124\1\40\1\111\1\125\1\uffff\1\107\1\122\1\125\1\105\1" + "\122\1\133\1\101\1\uffff\1\124\1\111\1\117\1\116\1\40\1\116\1\uffff" + "\1\117\1\124\1\40\1\uffff";
   static final String DFA3_maxS =
      "\1\174\1\117\2\uffff\1\105\5\uffff\1\117\2\uffff\1\116\1\101\1" + "\124\1\106\1\124\1\172\1\111\1\125\1\uffff\1\107\1\122\1\125\1\105" + "\1\122\1\133\1\101\1\uffff\1\124\1\111\1\117\1\116\1\172\1\116\1" + "\uffff\1\117\1\124\1\172\1\uffff";
   static final String DFA3_acceptS =
      "\2\uffff\1\3\1\4\1\uffff\1\6\1\7\1\10\1\11\1\12\1\uffff\1\14\1" + "\15\10\uffff\1\13\7\uffff\1\5\6\uffff\1\1\3\uffff\1\2";
   static final String DFA3_specialS = "\51\uffff}>";
   static final String[] DFA3_transitionS = {
      "\2\14\2\uffff\1\14\22\uffff\1\14\5\uffff\1\11\1\uffff\1\6\1" + "\7\23\uffff\1\5\3\uffff\2\13\1\1\2\13\1\4\7\13\1\12\14\13\1" + "\2\1\uffff\1\3\3\uffff\32\13\1\uffff\1\10",
      "\1\15",
      "",
      "",
      "\1\16",
      "",
      "",
      "",
      "",
      "",
      "\1\17",
      "",
      "",
      "\1\20",
      "\1\21",
      "\1\22",
      "\1\23",
      "\1\24",
      "\1\13\7\uffff\2\13\3\uffff\1\13\2\uffff\12\13\7\uffff\32\13" + "\4\uffff\1\13\1\uffff\32\13",
      "\1\26",
      "\1\27",
      "",
      "\1\30",
      "\1\31",
      "\1\32",
      "\1\33",
      "\1\34",
      "\1\35",
      "\1\36",
      "",
      "\1\37",
      "\1\40",
      "\1\41",
      "\1\42",
      "\1\43\7\uffff\2\13\3\uffff\1\13\2\uffff\12\13\7\uffff\32\13" + "\4\uffff\1\13\1\uffff\32\13",
      "\1\45",
      "",
      "\1\46",
      "\1\47",
      "\1\13\7\uffff\2\13\3\uffff\1\13\2\uffff\12\13\7\uffff\32\13" + "\4\uffff\1\13\1\uffff\32\13",
      ""};

   static final short[] DFA3_eot = DFA.unpackEncodedString(DFA3_eotS);
   static final short[] DFA3_eof = DFA.unpackEncodedString(DFA3_eofS);
   static final char[] DFA3_min = DFA.unpackEncodedStringToUnsignedChars(DFA3_minS);
   static final char[] DFA3_max = DFA.unpackEncodedStringToUnsignedChars(DFA3_maxS);
   static final short[] DFA3_accept = DFA.unpackEncodedString(DFA3_acceptS);
   static final short[] DFA3_special = DFA.unpackEncodedString(DFA3_specialS);
   static final short[][] DFA3_transition;

   static {
      int numStates = DFA3_transitionS.length;
      DFA3_transition = new short[numStates][];
      for (int i = 0; i < numStates; i++) {
         DFA3_transition[i] = DFA.unpackEncodedString(DFA3_transitionS[i]);
      }
   }

   class DFA3 extends DFA {

      public DFA3(BaseRecognizer recognizer) {
         this.recognizer = recognizer;
         this.decisionNumber = 3;
         this.eot = DFA3_eot;
         this.eof = DFA3_eof;
         this.min = DFA3_min;
         this.max = DFA3_max;
         this.accept = DFA3_accept;
         this.special = DFA3_special;
         this.transition = DFA3_transition;
      }

      @Override
      public String getDescription() {
         return "1:1: Tokens : ( T__9 | T__10 | T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | OR | AND | NOT | ID | WS );";
      }
   }

}