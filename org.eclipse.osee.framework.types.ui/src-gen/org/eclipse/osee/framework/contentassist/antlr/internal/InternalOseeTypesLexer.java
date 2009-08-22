package org.eclipse.osee.framework.contentassist.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.ui.common.editor.contentassist.antlr.internal.Lexer;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class InternalOseeTypesLexer extends Lexer {
    public static final int RULE_ID=5;
    public static final int RULE_ANY_OTHER=10;
    public static final int T29=29;
    public static final int T28=28;
    public static final int T27=27;
    public static final int T26=26;
    public static final int T25=25;
    public static final int Tokens=32;
    public static final int T24=24;
    public static final int EOF=-1;
    public static final int RULE_SL_COMMENT=8;
    public static final int T23=23;
    public static final int T22=22;
    public static final int T21=21;
    public static final int T20=20;
    public static final int RULE_ML_COMMENT=7;
    public static final int RULE_STRING=4;
    public static final int RULE_INT=6;
    public static final int T11=11;
    public static final int T12=12;
    public static final int T13=13;
    public static final int T14=14;
    public static final int RULE_WS=9;
    public static final int T15=15;
    public static final int T16=16;
    public static final int T17=17;
    public static final int T18=18;
    public static final int T30=30;
    public static final int T19=19;
    public static final int T31=31;
    public InternalOseeTypesLexer() {;} 
    public InternalOseeTypesLexer(CharStream input) {
        super(input);
    }
    public String getGrammarFileName() { return "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g"; }

    // $ANTLR start T11
    public final void mT11() throws RecognitionException {
        try {
            int _type = T11;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:10:5: ( 'one-to-many' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:10:7: 'one-to-many'
            {
            match("one-to-many"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T11

    // $ANTLR start T12
    public final void mT12() throws RecognitionException {
        try {
            int _type = T12;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:11:5: ( 'many-to-many' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:11:7: 'many-to-many'
            {
            match("many-to-many"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T12

    // $ANTLR start T13
    public final void mT13() throws RecognitionException {
        try {
            int _type = T13;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:12:5: ( 'many-to-one' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:12:7: 'many-to-one'
            {
            match("many-to-one"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T13

    // $ANTLR start T14
    public final void mT14() throws RecognitionException {
        try {
            int _type = T14;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:13:5: ( 'import' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:13:7: 'import'
            {
            match("import"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T14

    // $ANTLR start T15
    public final void mT15() throws RecognitionException {
        try {
            int _type = T15;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:14:5: ( 'artifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:14:7: 'artifactType'
            {
            match("artifactType"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T15

    // $ANTLR start T16
    public final void mT16() throws RecognitionException {
        try {
            int _type = T16;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:15:5: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:15:7: '{'
            {
            match('{'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T16

    // $ANTLR start T17
    public final void mT17() throws RecognitionException {
        try {
            int _type = T17;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:16:5: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:16:7: '}'
            {
            match('}'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T17

    // $ANTLR start T18
    public final void mT18() throws RecognitionException {
        try {
            int _type = T18;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:17:5: ( 'extends' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:17:7: 'extends'
            {
            match("extends"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T18

    // $ANTLR start T19
    public final void mT19() throws RecognitionException {
        try {
            int _type = T19;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:18:5: ( 'relation' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:18:7: 'relation'
            {
            match("relation"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T19

    // $ANTLR start T20
    public final void mT20() throws RecognitionException {
        try {
            int _type = T20;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:19:5: ( 'attribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:19:7: 'attribute'
            {
            match("attribute"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T20

    // $ANTLR start T21
    public final void mT21() throws RecognitionException {
        try {
            int _type = T21;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:20:5: ( 'attributeType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:20:7: 'attributeType'
            {
            match("attributeType"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T21

    // $ANTLR start T22
    public final void mT22() throws RecognitionException {
        try {
            int _type = T22;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:21:5: ( 'dataProvider' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:21:7: 'dataProvider'
            {
            match("dataProvider"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T22

    // $ANTLR start T23
    public final void mT23() throws RecognitionException {
        try {
            int _type = T23;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:22:5: ( 'taggerId' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:22:7: 'taggerId'
            {
            match("taggerId"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T23

    // $ANTLR start T24
    public final void mT24() throws RecognitionException {
        try {
            int _type = T24;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:23:5: ( 'defaultValue' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:23:7: 'defaultValue'
            {
            match("defaultValue"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T24

    // $ANTLR start T25
    public final void mT25() throws RecognitionException {
        try {
            int _type = T25;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:24:5: ( 'relationType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:24:7: 'relationType'
            {
            match("relationType"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T25

    // $ANTLR start T26
    public final void mT26() throws RecognitionException {
        try {
            int _type = T26;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:25:5: ( 'sideAName' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:25:7: 'sideAName'
            {
            match("sideAName"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T26

    // $ANTLR start T27
    public final void mT27() throws RecognitionException {
        try {
            int _type = T27;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:26:5: ( 'sideAArtifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:26:7: 'sideAArtifactType'
            {
            match("sideAArtifactType"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T27

    // $ANTLR start T28
    public final void mT28() throws RecognitionException {
        try {
            int _type = T28;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:27:5: ( 'sideBName' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:27:7: 'sideBName'
            {
            match("sideBName"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T28

    // $ANTLR start T29
    public final void mT29() throws RecognitionException {
        try {
            int _type = T29;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:28:5: ( 'sideBArtifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:28:7: 'sideBArtifactType'
            {
            match("sideBArtifactType"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T29

    // $ANTLR start T30
    public final void mT30() throws RecognitionException {
        try {
            int _type = T30;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:29:5: ( 'defaultOrderType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:29:7: 'defaultOrderType'
            {
            match("defaultOrderType"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T30

    // $ANTLR start T31
    public final void mT31() throws RecognitionException {
        try {
            int _type = T31;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:30:5: ( 'multiplicity' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:30:7: 'multiplicity'
            {
            match("multiplicity"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T31

    // $ANTLR start RULE_ID
    public final void mRULE_ID() throws RecognitionException {
        try {
            int _type = RULE_ID;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1793:9: ( ( '^' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1793:11: ( '^' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1793:11: ( '^' )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0=='^') ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1793:11: '^'
                    {
                    match('^'); 

                    }
                    break;

            }

            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1793:40: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='0' && LA2_0<='9')||(LA2_0>='A' && LA2_0<='Z')||LA2_0=='_'||(LA2_0>='a' && LA2_0<='z')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RULE_ID

    // $ANTLR start RULE_INT
    public final void mRULE_INT() throws RecognitionException {
        try {
            int _type = RULE_INT;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1795:10: ( ( '0' .. '9' )+ )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1795:12: ( '0' .. '9' )+
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1795:12: ( '0' .. '9' )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0>='0' && LA3_0<='9')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1795:13: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RULE_INT

    // $ANTLR start RULE_STRING
    public final void mRULE_STRING() throws RecognitionException {
        try {
            int _type = RULE_STRING;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1797:13: ( ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1797:15: ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1797:15: ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0=='\"') ) {
                alt6=1;
            }
            else if ( (LA6_0=='\'') ) {
                alt6=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1797:15: ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1797:16: '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )* '\"'
                    {
                    match('\"'); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1797:20: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )*
                    loop4:
                    do {
                        int alt4=3;
                        int LA4_0 = input.LA(1);

                        if ( (LA4_0=='\\') ) {
                            alt4=1;
                        }
                        else if ( ((LA4_0>='\u0000' && LA4_0<='!')||(LA4_0>='#' && LA4_0<='[')||(LA4_0>=']' && LA4_0<='\uFFFE')) ) {
                            alt4=2;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1797:21: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' )
                    	    {
                    	    match('\\'); 
                    	    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recover(mse);    throw mse;
                    	    }


                    	    }
                    	    break;
                    	case 2 :
                    	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1797:62: ~ ( ( '\\\\' | '\"' ) )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFE') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recover(mse);    throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop4;
                        }
                    } while (true);

                    match('\"'); 

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1797:82: '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\''
                    {
                    match('\''); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1797:87: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )*
                    loop5:
                    do {
                        int alt5=3;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0=='\\') ) {
                            alt5=1;
                        }
                        else if ( ((LA5_0>='\u0000' && LA5_0<='&')||(LA5_0>='(' && LA5_0<='[')||(LA5_0>=']' && LA5_0<='\uFFFE')) ) {
                            alt5=2;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1797:88: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' )
                    	    {
                    	    match('\\'); 
                    	    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recover(mse);    throw mse;
                    	    }


                    	    }
                    	    break;
                    	case 2 :
                    	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1797:129: ~ ( ( '\\\\' | '\\'' ) )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFE') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recover(mse);    throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop5;
                        }
                    } while (true);

                    match('\''); 

                    }
                    break;

            }


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RULE_STRING

    // $ANTLR start RULE_ML_COMMENT
    public final void mRULE_ML_COMMENT() throws RecognitionException {
        try {
            int _type = RULE_ML_COMMENT;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1799:17: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1799:19: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1799:24: ( options {greedy=false; } : . )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0=='*') ) {
                    int LA7_1 = input.LA(2);

                    if ( (LA7_1=='/') ) {
                        alt7=2;
                    }
                    else if ( ((LA7_1>='\u0000' && LA7_1<='.')||(LA7_1>='0' && LA7_1<='\uFFFE')) ) {
                        alt7=1;
                    }


                }
                else if ( ((LA7_0>='\u0000' && LA7_0<=')')||(LA7_0>='+' && LA7_0<='\uFFFE')) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1799:52: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);

            match("*/"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RULE_ML_COMMENT

    // $ANTLR start RULE_SL_COMMENT
    public final void mRULE_SL_COMMENT() throws RecognitionException {
        try {
            int _type = RULE_SL_COMMENT;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1801:17: ( '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1801:19: '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )?
            {
            match("//"); 

            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1801:24: (~ ( ( '\\n' | '\\r' ) ) )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( ((LA8_0>='\u0000' && LA8_0<='\t')||(LA8_0>='\u000B' && LA8_0<='\f')||(LA8_0>='\u000E' && LA8_0<='\uFFFE')) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1801:24: ~ ( ( '\\n' | '\\r' ) )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1801:40: ( ( '\\r' )? '\\n' )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0=='\n'||LA10_0=='\r') ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1801:41: ( '\\r' )? '\\n'
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1801:41: ( '\\r' )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0=='\r') ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1801:41: '\\r'
                            {
                            match('\r'); 

                            }
                            break;

                    }

                    match('\n'); 

                    }
                    break;

            }


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RULE_SL_COMMENT

    // $ANTLR start RULE_WS
    public final void mRULE_WS() throws RecognitionException {
        try {
            int _type = RULE_WS;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1803:9: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1803:11: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1803:11: ( ' ' | '\\t' | '\\r' | '\\n' )+
            int cnt11=0;
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( ((LA11_0>='\t' && LA11_0<='\n')||LA11_0=='\r'||LA11_0==' ') ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt11 >= 1 ) break loop11;
                        EarlyExitException eee =
                            new EarlyExitException(11, input);
                        throw eee;
                }
                cnt11++;
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RULE_WS

    // $ANTLR start RULE_ANY_OTHER
    public final void mRULE_ANY_OTHER() throws RecognitionException {
        try {
            int _type = RULE_ANY_OTHER;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1805:16: ( . )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1805:18: .
            {
            matchAny(); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RULE_ANY_OTHER

    public void mTokens() throws RecognitionException {
        // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:8: ( T11 | T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | RULE_ID | RULE_INT | RULE_STRING | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_WS | RULE_ANY_OTHER )
        int alt12=28;
        int LA12_0 = input.LA(1);

        if ( (LA12_0=='o') ) {
            int LA12_1 = input.LA(2);

            if ( (LA12_1=='n') ) {
                int LA12_20 = input.LA(3);

                if ( (LA12_20=='e') ) {
                    int LA12_40 = input.LA(4);

                    if ( (LA12_40=='-') ) {
                        alt12=1;
                    }
                    else {
                        alt12=22;}
                }
                else {
                    alt12=22;}
            }
            else {
                alt12=22;}
        }
        else if ( (LA12_0=='m') ) {
            switch ( input.LA(2) ) {
            case 'a':
                {
                int LA12_22 = input.LA(3);

                if ( (LA12_22=='n') ) {
                    int LA12_41 = input.LA(4);

                    if ( (LA12_41=='y') ) {
                        int LA12_53 = input.LA(5);

                        if ( (LA12_53=='-') ) {
                            int LA12_64 = input.LA(6);

                            if ( (LA12_64=='t') ) {
                                int LA12_76 = input.LA(7);

                                if ( (LA12_76=='o') ) {
                                    int LA12_90 = input.LA(8);

                                    if ( (LA12_90=='-') ) {
                                        int LA12_104 = input.LA(9);

                                        if ( (LA12_104=='m') ) {
                                            alt12=2;
                                        }
                                        else if ( (LA12_104=='o') ) {
                                            alt12=3;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("1:1: Tokens : ( T11 | T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | RULE_ID | RULE_INT | RULE_STRING | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_WS | RULE_ANY_OTHER );", 12, 104, input);

                                            throw nvae;
                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("1:1: Tokens : ( T11 | T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | RULE_ID | RULE_INT | RULE_STRING | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_WS | RULE_ANY_OTHER );", 12, 90, input);

                                        throw nvae;
                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("1:1: Tokens : ( T11 | T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | RULE_ID | RULE_INT | RULE_STRING | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_WS | RULE_ANY_OTHER );", 12, 76, input);

                                    throw nvae;
                                }
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("1:1: Tokens : ( T11 | T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | RULE_ID | RULE_INT | RULE_STRING | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_WS | RULE_ANY_OTHER );", 12, 64, input);

                                throw nvae;
                            }
                        }
                        else {
                            alt12=22;}
                    }
                    else {
                        alt12=22;}
                }
                else {
                    alt12=22;}
                }
                break;
            case 'u':
                {
                int LA12_23 = input.LA(3);

                if ( (LA12_23=='l') ) {
                    int LA12_42 = input.LA(4);

                    if ( (LA12_42=='t') ) {
                        int LA12_54 = input.LA(5);

                        if ( (LA12_54=='i') ) {
                            int LA12_65 = input.LA(6);

                            if ( (LA12_65=='p') ) {
                                int LA12_77 = input.LA(7);

                                if ( (LA12_77=='l') ) {
                                    int LA12_91 = input.LA(8);

                                    if ( (LA12_91=='i') ) {
                                        int LA12_105 = input.LA(9);

                                        if ( (LA12_105=='c') ) {
                                            int LA12_120 = input.LA(10);

                                            if ( (LA12_120=='i') ) {
                                                int LA12_133 = input.LA(11);

                                                if ( (LA12_133=='t') ) {
                                                    int LA12_145 = input.LA(12);

                                                    if ( (LA12_145=='y') ) {
                                                        int LA12_154 = input.LA(13);

                                                        if ( ((LA12_154>='0' && LA12_154<='9')||(LA12_154>='A' && LA12_154<='Z')||LA12_154=='_'||(LA12_154>='a' && LA12_154<='z')) ) {
                                                            alt12=22;
                                                        }
                                                        else {
                                                            alt12=21;}
                                                    }
                                                    else {
                                                        alt12=22;}
                                                }
                                                else {
                                                    alt12=22;}
                                            }
                                            else {
                                                alt12=22;}
                                        }
                                        else {
                                            alt12=22;}
                                    }
                                    else {
                                        alt12=22;}
                                }
                                else {
                                    alt12=22;}
                            }
                            else {
                                alt12=22;}
                        }
                        else {
                            alt12=22;}
                    }
                    else {
                        alt12=22;}
                }
                else {
                    alt12=22;}
                }
                break;
            default:
                alt12=22;}

        }
        else if ( (LA12_0=='i') ) {
            int LA12_3 = input.LA(2);

            if ( (LA12_3=='m') ) {
                int LA12_24 = input.LA(3);

                if ( (LA12_24=='p') ) {
                    int LA12_43 = input.LA(4);

                    if ( (LA12_43=='o') ) {
                        int LA12_55 = input.LA(5);

                        if ( (LA12_55=='r') ) {
                            int LA12_66 = input.LA(6);

                            if ( (LA12_66=='t') ) {
                                int LA12_78 = input.LA(7);

                                if ( ((LA12_78>='0' && LA12_78<='9')||(LA12_78>='A' && LA12_78<='Z')||LA12_78=='_'||(LA12_78>='a' && LA12_78<='z')) ) {
                                    alt12=22;
                                }
                                else {
                                    alt12=4;}
                            }
                            else {
                                alt12=22;}
                        }
                        else {
                            alt12=22;}
                    }
                    else {
                        alt12=22;}
                }
                else {
                    alt12=22;}
            }
            else {
                alt12=22;}
        }
        else if ( (LA12_0=='a') ) {
            switch ( input.LA(2) ) {
            case 'r':
                {
                int LA12_25 = input.LA(3);

                if ( (LA12_25=='t') ) {
                    int LA12_44 = input.LA(4);

                    if ( (LA12_44=='i') ) {
                        int LA12_56 = input.LA(5);

                        if ( (LA12_56=='f') ) {
                            int LA12_67 = input.LA(6);

                            if ( (LA12_67=='a') ) {
                                int LA12_79 = input.LA(7);

                                if ( (LA12_79=='c') ) {
                                    int LA12_93 = input.LA(8);

                                    if ( (LA12_93=='t') ) {
                                        int LA12_106 = input.LA(9);

                                        if ( (LA12_106=='T') ) {
                                            int LA12_121 = input.LA(10);

                                            if ( (LA12_121=='y') ) {
                                                int LA12_134 = input.LA(11);

                                                if ( (LA12_134=='p') ) {
                                                    int LA12_146 = input.LA(12);

                                                    if ( (LA12_146=='e') ) {
                                                        int LA12_155 = input.LA(13);

                                                        if ( ((LA12_155>='0' && LA12_155<='9')||(LA12_155>='A' && LA12_155<='Z')||LA12_155=='_'||(LA12_155>='a' && LA12_155<='z')) ) {
                                                            alt12=22;
                                                        }
                                                        else {
                                                            alt12=5;}
                                                    }
                                                    else {
                                                        alt12=22;}
                                                }
                                                else {
                                                    alt12=22;}
                                            }
                                            else {
                                                alt12=22;}
                                        }
                                        else {
                                            alt12=22;}
                                    }
                                    else {
                                        alt12=22;}
                                }
                                else {
                                    alt12=22;}
                            }
                            else {
                                alt12=22;}
                        }
                        else {
                            alt12=22;}
                    }
                    else {
                        alt12=22;}
                }
                else {
                    alt12=22;}
                }
                break;
            case 't':
                {
                int LA12_26 = input.LA(3);

                if ( (LA12_26=='t') ) {
                    int LA12_45 = input.LA(4);

                    if ( (LA12_45=='r') ) {
                        int LA12_57 = input.LA(5);

                        if ( (LA12_57=='i') ) {
                            int LA12_68 = input.LA(6);

                            if ( (LA12_68=='b') ) {
                                int LA12_80 = input.LA(7);

                                if ( (LA12_80=='u') ) {
                                    int LA12_94 = input.LA(8);

                                    if ( (LA12_94=='t') ) {
                                        int LA12_107 = input.LA(9);

                                        if ( (LA12_107=='e') ) {
                                            switch ( input.LA(10) ) {
                                            case 'T':
                                                {
                                                int LA12_135 = input.LA(11);

                                                if ( (LA12_135=='y') ) {
                                                    int LA12_147 = input.LA(12);

                                                    if ( (LA12_147=='p') ) {
                                                        int LA12_156 = input.LA(13);

                                                        if ( (LA12_156=='e') ) {
                                                            int LA12_165 = input.LA(14);

                                                            if ( ((LA12_165>='0' && LA12_165<='9')||(LA12_165>='A' && LA12_165<='Z')||LA12_165=='_'||(LA12_165>='a' && LA12_165<='z')) ) {
                                                                alt12=22;
                                                            }
                                                            else {
                                                                alt12=11;}
                                                        }
                                                        else {
                                                            alt12=22;}
                                                    }
                                                    else {
                                                        alt12=22;}
                                                }
                                                else {
                                                    alt12=22;}
                                                }
                                                break;
                                            case '0':
                                            case '1':
                                            case '2':
                                            case '3':
                                            case '4':
                                            case '5':
                                            case '6':
                                            case '7':
                                            case '8':
                                            case '9':
                                            case 'A':
                                            case 'B':
                                            case 'C':
                                            case 'D':
                                            case 'E':
                                            case 'F':
                                            case 'G':
                                            case 'H':
                                            case 'I':
                                            case 'J':
                                            case 'K':
                                            case 'L':
                                            case 'M':
                                            case 'N':
                                            case 'O':
                                            case 'P':
                                            case 'Q':
                                            case 'R':
                                            case 'S':
                                            case 'U':
                                            case 'V':
                                            case 'W':
                                            case 'X':
                                            case 'Y':
                                            case 'Z':
                                            case '_':
                                            case 'a':
                                            case 'b':
                                            case 'c':
                                            case 'd':
                                            case 'e':
                                            case 'f':
                                            case 'g':
                                            case 'h':
                                            case 'i':
                                            case 'j':
                                            case 'k':
                                            case 'l':
                                            case 'm':
                                            case 'n':
                                            case 'o':
                                            case 'p':
                                            case 'q':
                                            case 'r':
                                            case 's':
                                            case 't':
                                            case 'u':
                                            case 'v':
                                            case 'w':
                                            case 'x':
                                            case 'y':
                                            case 'z':
                                                {
                                                alt12=22;
                                                }
                                                break;
                                            default:
                                                alt12=10;}

                                        }
                                        else {
                                            alt12=22;}
                                    }
                                    else {
                                        alt12=22;}
                                }
                                else {
                                    alt12=22;}
                            }
                            else {
                                alt12=22;}
                        }
                        else {
                            alt12=22;}
                    }
                    else {
                        alt12=22;}
                }
                else {
                    alt12=22;}
                }
                break;
            default:
                alt12=22;}

        }
        else if ( (LA12_0=='{') ) {
            alt12=6;
        }
        else if ( (LA12_0=='}') ) {
            alt12=7;
        }
        else if ( (LA12_0=='e') ) {
            int LA12_7 = input.LA(2);

            if ( (LA12_7=='x') ) {
                int LA12_29 = input.LA(3);

                if ( (LA12_29=='t') ) {
                    int LA12_46 = input.LA(4);

                    if ( (LA12_46=='e') ) {
                        int LA12_58 = input.LA(5);

                        if ( (LA12_58=='n') ) {
                            int LA12_69 = input.LA(6);

                            if ( (LA12_69=='d') ) {
                                int LA12_81 = input.LA(7);

                                if ( (LA12_81=='s') ) {
                                    int LA12_95 = input.LA(8);

                                    if ( ((LA12_95>='0' && LA12_95<='9')||(LA12_95>='A' && LA12_95<='Z')||LA12_95=='_'||(LA12_95>='a' && LA12_95<='z')) ) {
                                        alt12=22;
                                    }
                                    else {
                                        alt12=8;}
                                }
                                else {
                                    alt12=22;}
                            }
                            else {
                                alt12=22;}
                        }
                        else {
                            alt12=22;}
                    }
                    else {
                        alt12=22;}
                }
                else {
                    alt12=22;}
            }
            else {
                alt12=22;}
        }
        else if ( (LA12_0=='r') ) {
            int LA12_8 = input.LA(2);

            if ( (LA12_8=='e') ) {
                int LA12_30 = input.LA(3);

                if ( (LA12_30=='l') ) {
                    int LA12_47 = input.LA(4);

                    if ( (LA12_47=='a') ) {
                        int LA12_59 = input.LA(5);

                        if ( (LA12_59=='t') ) {
                            int LA12_70 = input.LA(6);

                            if ( (LA12_70=='i') ) {
                                int LA12_82 = input.LA(7);

                                if ( (LA12_82=='o') ) {
                                    int LA12_96 = input.LA(8);

                                    if ( (LA12_96=='n') ) {
                                        switch ( input.LA(9) ) {
                                        case 'T':
                                            {
                                            int LA12_123 = input.LA(10);

                                            if ( (LA12_123=='y') ) {
                                                int LA12_137 = input.LA(11);

                                                if ( (LA12_137=='p') ) {
                                                    int LA12_148 = input.LA(12);

                                                    if ( (LA12_148=='e') ) {
                                                        int LA12_157 = input.LA(13);

                                                        if ( ((LA12_157>='0' && LA12_157<='9')||(LA12_157>='A' && LA12_157<='Z')||LA12_157=='_'||(LA12_157>='a' && LA12_157<='z')) ) {
                                                            alt12=22;
                                                        }
                                                        else {
                                                            alt12=15;}
                                                    }
                                                    else {
                                                        alt12=22;}
                                                }
                                                else {
                                                    alt12=22;}
                                            }
                                            else {
                                                alt12=22;}
                                            }
                                            break;
                                        case '0':
                                        case '1':
                                        case '2':
                                        case '3':
                                        case '4':
                                        case '5':
                                        case '6':
                                        case '7':
                                        case '8':
                                        case '9':
                                        case 'A':
                                        case 'B':
                                        case 'C':
                                        case 'D':
                                        case 'E':
                                        case 'F':
                                        case 'G':
                                        case 'H':
                                        case 'I':
                                        case 'J':
                                        case 'K':
                                        case 'L':
                                        case 'M':
                                        case 'N':
                                        case 'O':
                                        case 'P':
                                        case 'Q':
                                        case 'R':
                                        case 'S':
                                        case 'U':
                                        case 'V':
                                        case 'W':
                                        case 'X':
                                        case 'Y':
                                        case 'Z':
                                        case '_':
                                        case 'a':
                                        case 'b':
                                        case 'c':
                                        case 'd':
                                        case 'e':
                                        case 'f':
                                        case 'g':
                                        case 'h':
                                        case 'i':
                                        case 'j':
                                        case 'k':
                                        case 'l':
                                        case 'm':
                                        case 'n':
                                        case 'o':
                                        case 'p':
                                        case 'q':
                                        case 'r':
                                        case 's':
                                        case 't':
                                        case 'u':
                                        case 'v':
                                        case 'w':
                                        case 'x':
                                        case 'y':
                                        case 'z':
                                            {
                                            alt12=22;
                                            }
                                            break;
                                        default:
                                            alt12=9;}

                                    }
                                    else {
                                        alt12=22;}
                                }
                                else {
                                    alt12=22;}
                            }
                            else {
                                alt12=22;}
                        }
                        else {
                            alt12=22;}
                    }
                    else {
                        alt12=22;}
                }
                else {
                    alt12=22;}
            }
            else {
                alt12=22;}
        }
        else if ( (LA12_0=='d') ) {
            switch ( input.LA(2) ) {
            case 'a':
                {
                int LA12_31 = input.LA(3);

                if ( (LA12_31=='t') ) {
                    int LA12_48 = input.LA(4);

                    if ( (LA12_48=='a') ) {
                        int LA12_60 = input.LA(5);

                        if ( (LA12_60=='P') ) {
                            int LA12_71 = input.LA(6);

                            if ( (LA12_71=='r') ) {
                                int LA12_83 = input.LA(7);

                                if ( (LA12_83=='o') ) {
                                    int LA12_97 = input.LA(8);

                                    if ( (LA12_97=='v') ) {
                                        int LA12_110 = input.LA(9);

                                        if ( (LA12_110=='i') ) {
                                            int LA12_125 = input.LA(10);

                                            if ( (LA12_125=='d') ) {
                                                int LA12_138 = input.LA(11);

                                                if ( (LA12_138=='e') ) {
                                                    int LA12_149 = input.LA(12);

                                                    if ( (LA12_149=='r') ) {
                                                        int LA12_158 = input.LA(13);

                                                        if ( ((LA12_158>='0' && LA12_158<='9')||(LA12_158>='A' && LA12_158<='Z')||LA12_158=='_'||(LA12_158>='a' && LA12_158<='z')) ) {
                                                            alt12=22;
                                                        }
                                                        else {
                                                            alt12=12;}
                                                    }
                                                    else {
                                                        alt12=22;}
                                                }
                                                else {
                                                    alt12=22;}
                                            }
                                            else {
                                                alt12=22;}
                                        }
                                        else {
                                            alt12=22;}
                                    }
                                    else {
                                        alt12=22;}
                                }
                                else {
                                    alt12=22;}
                            }
                            else {
                                alt12=22;}
                        }
                        else {
                            alt12=22;}
                    }
                    else {
                        alt12=22;}
                }
                else {
                    alt12=22;}
                }
                break;
            case 'e':
                {
                int LA12_32 = input.LA(3);

                if ( (LA12_32=='f') ) {
                    int LA12_49 = input.LA(4);

                    if ( (LA12_49=='a') ) {
                        int LA12_61 = input.LA(5);

                        if ( (LA12_61=='u') ) {
                            int LA12_72 = input.LA(6);

                            if ( (LA12_72=='l') ) {
                                int LA12_84 = input.LA(7);

                                if ( (LA12_84=='t') ) {
                                    switch ( input.LA(8) ) {
                                    case 'O':
                                        {
                                        int LA12_111 = input.LA(9);

                                        if ( (LA12_111=='r') ) {
                                            int LA12_126 = input.LA(10);

                                            if ( (LA12_126=='d') ) {
                                                int LA12_139 = input.LA(11);

                                                if ( (LA12_139=='e') ) {
                                                    int LA12_150 = input.LA(12);

                                                    if ( (LA12_150=='r') ) {
                                                        int LA12_159 = input.LA(13);

                                                        if ( (LA12_159=='T') ) {
                                                            int LA12_168 = input.LA(14);

                                                            if ( (LA12_168=='y') ) {
                                                                int LA12_173 = input.LA(15);

                                                                if ( (LA12_173=='p') ) {
                                                                    int LA12_176 = input.LA(16);

                                                                    if ( (LA12_176=='e') ) {
                                                                        int LA12_179 = input.LA(17);

                                                                        if ( ((LA12_179>='0' && LA12_179<='9')||(LA12_179>='A' && LA12_179<='Z')||LA12_179=='_'||(LA12_179>='a' && LA12_179<='z')) ) {
                                                                            alt12=22;
                                                                        }
                                                                        else {
                                                                            alt12=20;}
                                                                    }
                                                                    else {
                                                                        alt12=22;}
                                                                }
                                                                else {
                                                                    alt12=22;}
                                                            }
                                                            else {
                                                                alt12=22;}
                                                        }
                                                        else {
                                                            alt12=22;}
                                                    }
                                                    else {
                                                        alt12=22;}
                                                }
                                                else {
                                                    alt12=22;}
                                            }
                                            else {
                                                alt12=22;}
                                        }
                                        else {
                                            alt12=22;}
                                        }
                                        break;
                                    case 'V':
                                        {
                                        int LA12_112 = input.LA(9);

                                        if ( (LA12_112=='a') ) {
                                            int LA12_127 = input.LA(10);

                                            if ( (LA12_127=='l') ) {
                                                int LA12_140 = input.LA(11);

                                                if ( (LA12_140=='u') ) {
                                                    int LA12_151 = input.LA(12);

                                                    if ( (LA12_151=='e') ) {
                                                        int LA12_160 = input.LA(13);

                                                        if ( ((LA12_160>='0' && LA12_160<='9')||(LA12_160>='A' && LA12_160<='Z')||LA12_160=='_'||(LA12_160>='a' && LA12_160<='z')) ) {
                                                            alt12=22;
                                                        }
                                                        else {
                                                            alt12=14;}
                                                    }
                                                    else {
                                                        alt12=22;}
                                                }
                                                else {
                                                    alt12=22;}
                                            }
                                            else {
                                                alt12=22;}
                                        }
                                        else {
                                            alt12=22;}
                                        }
                                        break;
                                    default:
                                        alt12=22;}

                                }
                                else {
                                    alt12=22;}
                            }
                            else {
                                alt12=22;}
                        }
                        else {
                            alt12=22;}
                    }
                    else {
                        alt12=22;}
                }
                else {
                    alt12=22;}
                }
                break;
            default:
                alt12=22;}

        }
        else if ( (LA12_0=='t') ) {
            int LA12_10 = input.LA(2);

            if ( (LA12_10=='a') ) {
                int LA12_33 = input.LA(3);

                if ( (LA12_33=='g') ) {
                    int LA12_50 = input.LA(4);

                    if ( (LA12_50=='g') ) {
                        int LA12_62 = input.LA(5);

                        if ( (LA12_62=='e') ) {
                            int LA12_73 = input.LA(6);

                            if ( (LA12_73=='r') ) {
                                int LA12_85 = input.LA(7);

                                if ( (LA12_85=='I') ) {
                                    int LA12_99 = input.LA(8);

                                    if ( (LA12_99=='d') ) {
                                        int LA12_113 = input.LA(9);

                                        if ( ((LA12_113>='0' && LA12_113<='9')||(LA12_113>='A' && LA12_113<='Z')||LA12_113=='_'||(LA12_113>='a' && LA12_113<='z')) ) {
                                            alt12=22;
                                        }
                                        else {
                                            alt12=13;}
                                    }
                                    else {
                                        alt12=22;}
                                }
                                else {
                                    alt12=22;}
                            }
                            else {
                                alt12=22;}
                        }
                        else {
                            alt12=22;}
                    }
                    else {
                        alt12=22;}
                }
                else {
                    alt12=22;}
            }
            else {
                alt12=22;}
        }
        else if ( (LA12_0=='s') ) {
            int LA12_11 = input.LA(2);

            if ( (LA12_11=='i') ) {
                int LA12_34 = input.LA(3);

                if ( (LA12_34=='d') ) {
                    int LA12_51 = input.LA(4);

                    if ( (LA12_51=='e') ) {
                        switch ( input.LA(5) ) {
                        case 'B':
                            {
                            switch ( input.LA(6) ) {
                            case 'N':
                                {
                                int LA12_86 = input.LA(7);

                                if ( (LA12_86=='a') ) {
                                    int LA12_100 = input.LA(8);

                                    if ( (LA12_100=='m') ) {
                                        int LA12_114 = input.LA(9);

                                        if ( (LA12_114=='e') ) {
                                            int LA12_129 = input.LA(10);

                                            if ( ((LA12_129>='0' && LA12_129<='9')||(LA12_129>='A' && LA12_129<='Z')||LA12_129=='_'||(LA12_129>='a' && LA12_129<='z')) ) {
                                                alt12=22;
                                            }
                                            else {
                                                alt12=18;}
                                        }
                                        else {
                                            alt12=22;}
                                    }
                                    else {
                                        alt12=22;}
                                }
                                else {
                                    alt12=22;}
                                }
                                break;
                            case 'A':
                                {
                                int LA12_87 = input.LA(7);

                                if ( (LA12_87=='r') ) {
                                    int LA12_101 = input.LA(8);

                                    if ( (LA12_101=='t') ) {
                                        int LA12_115 = input.LA(9);

                                        if ( (LA12_115=='i') ) {
                                            int LA12_130 = input.LA(10);

                                            if ( (LA12_130=='f') ) {
                                                int LA12_142 = input.LA(11);

                                                if ( (LA12_142=='a') ) {
                                                    int LA12_152 = input.LA(12);

                                                    if ( (LA12_152=='c') ) {
                                                        int LA12_161 = input.LA(13);

                                                        if ( (LA12_161=='t') ) {
                                                            int LA12_170 = input.LA(14);

                                                            if ( (LA12_170=='T') ) {
                                                                int LA12_174 = input.LA(15);

                                                                if ( (LA12_174=='y') ) {
                                                                    int LA12_177 = input.LA(16);

                                                                    if ( (LA12_177=='p') ) {
                                                                        int LA12_180 = input.LA(17);

                                                                        if ( (LA12_180=='e') ) {
                                                                            int LA12_183 = input.LA(18);

                                                                            if ( ((LA12_183>='0' && LA12_183<='9')||(LA12_183>='A' && LA12_183<='Z')||LA12_183=='_'||(LA12_183>='a' && LA12_183<='z')) ) {
                                                                                alt12=22;
                                                                            }
                                                                            else {
                                                                                alt12=19;}
                                                                        }
                                                                        else {
                                                                            alt12=22;}
                                                                    }
                                                                    else {
                                                                        alt12=22;}
                                                                }
                                                                else {
                                                                    alt12=22;}
                                                            }
                                                            else {
                                                                alt12=22;}
                                                        }
                                                        else {
                                                            alt12=22;}
                                                    }
                                                    else {
                                                        alt12=22;}
                                                }
                                                else {
                                                    alt12=22;}
                                            }
                                            else {
                                                alt12=22;}
                                        }
                                        else {
                                            alt12=22;}
                                    }
                                    else {
                                        alt12=22;}
                                }
                                else {
                                    alt12=22;}
                                }
                                break;
                            default:
                                alt12=22;}

                            }
                            break;
                        case 'A':
                            {
                            switch ( input.LA(6) ) {
                            case 'A':
                                {
                                int LA12_88 = input.LA(7);

                                if ( (LA12_88=='r') ) {
                                    int LA12_102 = input.LA(8);

                                    if ( (LA12_102=='t') ) {
                                        int LA12_116 = input.LA(9);

                                        if ( (LA12_116=='i') ) {
                                            int LA12_131 = input.LA(10);

                                            if ( (LA12_131=='f') ) {
                                                int LA12_143 = input.LA(11);

                                                if ( (LA12_143=='a') ) {
                                                    int LA12_153 = input.LA(12);

                                                    if ( (LA12_153=='c') ) {
                                                        int LA12_162 = input.LA(13);

                                                        if ( (LA12_162=='t') ) {
                                                            int LA12_171 = input.LA(14);

                                                            if ( (LA12_171=='T') ) {
                                                                int LA12_175 = input.LA(15);

                                                                if ( (LA12_175=='y') ) {
                                                                    int LA12_178 = input.LA(16);

                                                                    if ( (LA12_178=='p') ) {
                                                                        int LA12_181 = input.LA(17);

                                                                        if ( (LA12_181=='e') ) {
                                                                            int LA12_184 = input.LA(18);

                                                                            if ( ((LA12_184>='0' && LA12_184<='9')||(LA12_184>='A' && LA12_184<='Z')||LA12_184=='_'||(LA12_184>='a' && LA12_184<='z')) ) {
                                                                                alt12=22;
                                                                            }
                                                                            else {
                                                                                alt12=17;}
                                                                        }
                                                                        else {
                                                                            alt12=22;}
                                                                    }
                                                                    else {
                                                                        alt12=22;}
                                                                }
                                                                else {
                                                                    alt12=22;}
                                                            }
                                                            else {
                                                                alt12=22;}
                                                        }
                                                        else {
                                                            alt12=22;}
                                                    }
                                                    else {
                                                        alt12=22;}
                                                }
                                                else {
                                                    alt12=22;}
                                            }
                                            else {
                                                alt12=22;}
                                        }
                                        else {
                                            alt12=22;}
                                    }
                                    else {
                                        alt12=22;}
                                }
                                else {
                                    alt12=22;}
                                }
                                break;
                            case 'N':
                                {
                                int LA12_89 = input.LA(7);

                                if ( (LA12_89=='a') ) {
                                    int LA12_103 = input.LA(8);

                                    if ( (LA12_103=='m') ) {
                                        int LA12_117 = input.LA(9);

                                        if ( (LA12_117=='e') ) {
                                            int LA12_132 = input.LA(10);

                                            if ( ((LA12_132>='0' && LA12_132<='9')||(LA12_132>='A' && LA12_132<='Z')||LA12_132=='_'||(LA12_132>='a' && LA12_132<='z')) ) {
                                                alt12=22;
                                            }
                                            else {
                                                alt12=16;}
                                        }
                                        else {
                                            alt12=22;}
                                    }
                                    else {
                                        alt12=22;}
                                }
                                else {
                                    alt12=22;}
                                }
                                break;
                            default:
                                alt12=22;}

                            }
                            break;
                        default:
                            alt12=22;}

                    }
                    else {
                        alt12=22;}
                }
                else {
                    alt12=22;}
            }
            else {
                alt12=22;}
        }
        else if ( (LA12_0=='^') ) {
            int LA12_12 = input.LA(2);

            if ( ((LA12_12>='A' && LA12_12<='Z')||LA12_12=='_'||(LA12_12>='a' && LA12_12<='z')) ) {
                alt12=22;
            }
            else {
                alt12=28;}
        }
        else if ( ((LA12_0>='A' && LA12_0<='Z')||LA12_0=='_'||(LA12_0>='b' && LA12_0<='c')||(LA12_0>='f' && LA12_0<='h')||(LA12_0>='j' && LA12_0<='l')||LA12_0=='n'||(LA12_0>='p' && LA12_0<='q')||(LA12_0>='u' && LA12_0<='z')) ) {
            alt12=22;
        }
        else if ( ((LA12_0>='0' && LA12_0<='9')) ) {
            alt12=23;
        }
        else if ( (LA12_0=='\"') ) {
            int LA12_15 = input.LA(2);

            if ( ((LA12_15>='\u0000' && LA12_15<='\uFFFE')) ) {
                alt12=24;
            }
            else {
                alt12=28;}
        }
        else if ( (LA12_0=='\'') ) {
            int LA12_16 = input.LA(2);

            if ( ((LA12_16>='\u0000' && LA12_16<='\uFFFE')) ) {
                alt12=24;
            }
            else {
                alt12=28;}
        }
        else if ( (LA12_0=='/') ) {
            switch ( input.LA(2) ) {
            case '/':
                {
                alt12=26;
                }
                break;
            case '*':
                {
                alt12=25;
                }
                break;
            default:
                alt12=28;}

        }
        else if ( ((LA12_0>='\t' && LA12_0<='\n')||LA12_0=='\r'||LA12_0==' ') ) {
            alt12=27;
        }
        else if ( ((LA12_0>='\u0000' && LA12_0<='\b')||(LA12_0>='\u000B' && LA12_0<='\f')||(LA12_0>='\u000E' && LA12_0<='\u001F')||LA12_0=='!'||(LA12_0>='#' && LA12_0<='&')||(LA12_0>='(' && LA12_0<='.')||(LA12_0>=':' && LA12_0<='@')||(LA12_0>='[' && LA12_0<=']')||LA12_0=='`'||LA12_0=='|'||(LA12_0>='~' && LA12_0<='\uFFFE')) ) {
            alt12=28;
        }
        else {
            NoViableAltException nvae =
                new NoViableAltException("1:1: Tokens : ( T11 | T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | RULE_ID | RULE_INT | RULE_STRING | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_WS | RULE_ANY_OTHER );", 12, 0, input);

            throw nvae;
        }
        switch (alt12) {
            case 1 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:10: T11
                {
                mT11(); 

                }
                break;
            case 2 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:14: T12
                {
                mT12(); 

                }
                break;
            case 3 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:18: T13
                {
                mT13(); 

                }
                break;
            case 4 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:22: T14
                {
                mT14(); 

                }
                break;
            case 5 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:26: T15
                {
                mT15(); 

                }
                break;
            case 6 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:30: T16
                {
                mT16(); 

                }
                break;
            case 7 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:34: T17
                {
                mT17(); 

                }
                break;
            case 8 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:38: T18
                {
                mT18(); 

                }
                break;
            case 9 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:42: T19
                {
                mT19(); 

                }
                break;
            case 10 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:46: T20
                {
                mT20(); 

                }
                break;
            case 11 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:50: T21
                {
                mT21(); 

                }
                break;
            case 12 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:54: T22
                {
                mT22(); 

                }
                break;
            case 13 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:58: T23
                {
                mT23(); 

                }
                break;
            case 14 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:62: T24
                {
                mT24(); 

                }
                break;
            case 15 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:66: T25
                {
                mT25(); 

                }
                break;
            case 16 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:70: T26
                {
                mT26(); 

                }
                break;
            case 17 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:74: T27
                {
                mT27(); 

                }
                break;
            case 18 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:78: T28
                {
                mT28(); 

                }
                break;
            case 19 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:82: T29
                {
                mT29(); 

                }
                break;
            case 20 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:86: T30
                {
                mT30(); 

                }
                break;
            case 21 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:90: T31
                {
                mT31(); 

                }
                break;
            case 22 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:94: RULE_ID
                {
                mRULE_ID(); 

                }
                break;
            case 23 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:102: RULE_INT
                {
                mRULE_INT(); 

                }
                break;
            case 24 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:111: RULE_STRING
                {
                mRULE_STRING(); 

                }
                break;
            case 25 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:123: RULE_ML_COMMENT
                {
                mRULE_ML_COMMENT(); 

                }
                break;
            case 26 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:139: RULE_SL_COMMENT
                {
                mRULE_SL_COMMENT(); 

                }
                break;
            case 27 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:155: RULE_WS
                {
                mRULE_WS(); 

                }
                break;
            case 28 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:163: RULE_ANY_OTHER
                {
                mRULE_ANY_OTHER(); 

                }
                break;

        }

    }


 

}