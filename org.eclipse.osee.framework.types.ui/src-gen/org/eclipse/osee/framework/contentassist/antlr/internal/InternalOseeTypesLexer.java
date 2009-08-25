package org.eclipse.osee.framework.contentassist.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.ui.common.editor.contentassist.antlr.internal.Lexer;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class InternalOseeTypesLexer extends Lexer {
    public static final int RULE_ID=4;
    public static final int RULE_ANY_OTHER=10;
    public static final int T29=29;
    public static final int T28=28;
    public static final int T27=27;
    public static final int T26=26;
    public static final int T25=25;
    public static final int EOF=-1;
    public static final int T24=24;
    public static final int T23=23;
    public static final int T22=22;
    public static final int T21=21;
    public static final int T20=20;
    public static final int T62=62;
    public static final int T63=63;
    public static final int T64=64;
    public static final int T65=65;
    public static final int T66=66;
    public static final int RULE_INT=6;
    public static final int T38=38;
    public static final int T37=37;
    public static final int T39=39;
    public static final int T34=34;
    public static final int T33=33;
    public static final int T36=36;
    public static final int T35=35;
    public static final int T30=30;
    public static final int T61=61;
    public static final int T32=32;
    public static final int T60=60;
    public static final int T31=31;
    public static final int T49=49;
    public static final int T48=48;
    public static final int T43=43;
    public static final int Tokens=67;
    public static final int RULE_SL_COMMENT=8;
    public static final int T42=42;
    public static final int T41=41;
    public static final int T40=40;
    public static final int T47=47;
    public static final int T46=46;
    public static final int T45=45;
    public static final int RULE_ML_COMMENT=7;
    public static final int T44=44;
    public static final int RULE_STRING=5;
    public static final int T50=50;
    public static final int T59=59;
    public static final int T11=11;
    public static final int T12=12;
    public static final int T13=13;
    public static final int T14=14;
    public static final int T52=52;
    public static final int T15=15;
    public static final int RULE_WS=9;
    public static final int T51=51;
    public static final int T16=16;
    public static final int T54=54;
    public static final int T17=17;
    public static final int T53=53;
    public static final int T18=18;
    public static final int T56=56;
    public static final int T19=19;
    public static final int T55=55;
    public static final int T58=58;
    public static final int T57=57;
    public InternalOseeTypesLexer() {;} 
    public InternalOseeTypesLexer(CharStream input) {
        super(input);
    }
    public String getGrammarFileName() { return "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g"; }

    // $ANTLR start T11
    public final void mT11() throws RecognitionException {
        try {
            int _type = T11;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:10:5: ( '0' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:10:7: '0'
            {
            match('0'); 

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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:11:5: ( '1' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:11:7: '1'
            {
            match('1'); 

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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:12:5: ( '2' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:12:7: '2'
            {
            match('2'); 

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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:13:5: ( '3' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:13:7: '3'
            {
            match('3'); 

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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:14:5: ( '4' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:14:7: '4'
            {
            match('4'); 

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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:15:5: ( '5' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:15:7: '5'
            {
            match('5'); 

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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:16:5: ( '6' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:16:7: '6'
            {
            match('6'); 

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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:17:5: ( '7' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:17:7: '7'
            {
            match('7'); 

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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:18:5: ( '8' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:18:7: '8'
            {
            match('8'); 

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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:19:5: ( '9' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:19:7: '9'
            {
            match('9'); 

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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:20:5: ( 'DefaultAttributeDataProvider' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:20:7: 'DefaultAttributeDataProvider'
            {
            match("DefaultAttributeDataProvider"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:21:5: ( 'UriAttributeDataProvider' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:21:7: 'UriAttributeDataProvider'
            {
            match("UriAttributeDataProvider"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:22:5: ( 'MappedAttributeDataProvider' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:22:7: 'MappedAttributeDataProvider'
            {
            match("MappedAttributeDataProvider"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:23:5: ( 'unlimited' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:23:7: 'unlimited'
            {
            match("unlimited"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:24:5: ( 'DefaultAttributeTaggerProvider' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:24:7: 'DefaultAttributeTaggerProvider'
            {
            match("DefaultAttributeTaggerProvider"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:25:5: ( 'BooleanAttribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:25:7: 'BooleanAttribute'
            {
            match("BooleanAttribute"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:26:5: ( 'CompressedContentAttribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:26:7: 'CompressedContentAttribute'
            {
            match("CompressedContentAttribute"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:27:5: ( 'DateAttribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:27:7: 'DateAttribute'
            {
            match("DateAttribute"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:28:5: ( 'EnumeratedAttribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:28:7: 'EnumeratedAttribute'
            {
            match("EnumeratedAttribute"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:29:5: ( 'FloatingPointAttribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:29:7: 'FloatingPointAttribute'
            {
            match("FloatingPointAttribute"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:30:5: ( 'IntegerAttribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:30:7: 'IntegerAttribute'
            {
            match("IntegerAttribute"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T31

    // $ANTLR start T32
    public final void mT32() throws RecognitionException {
        try {
            int _type = T32;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:31:5: ( 'JavaObjectAttribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:31:7: 'JavaObjectAttribute'
            {
            match("JavaObjectAttribute"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T32

    // $ANTLR start T33
    public final void mT33() throws RecognitionException {
        try {
            int _type = T33;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:32:5: ( 'StringAttribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:32:7: 'StringAttribute'
            {
            match("StringAttribute"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T33

    // $ANTLR start T34
    public final void mT34() throws RecognitionException {
        try {
            int _type = T34;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:33:5: ( 'WordAttribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:33:7: 'WordAttribute'
            {
            match("WordAttribute"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T34

    // $ANTLR start T35
    public final void mT35() throws RecognitionException {
        try {
            int _type = T35;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:34:5: ( 'Lexicographical_Ascending' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:34:7: 'Lexicographical_Ascending'
            {
            match("Lexicographical_Ascending"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T35

    // $ANTLR start T36
    public final void mT36() throws RecognitionException {
        try {
            int _type = T36;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:35:5: ( 'Lexicographical_Descending' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:35:7: 'Lexicographical_Descending'
            {
            match("Lexicographical_Descending"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T36

    // $ANTLR start T37
    public final void mT37() throws RecognitionException {
        try {
            int _type = T37;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:36:5: ( 'Unordered' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:36:7: 'Unordered'
            {
            match("Unordered"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T37

    // $ANTLR start T38
    public final void mT38() throws RecognitionException {
        try {
            int _type = T38;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:37:5: ( 'ONE_TO_MANY' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:37:7: 'ONE_TO_MANY'
            {
            match("ONE_TO_MANY"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T38

    // $ANTLR start T39
    public final void mT39() throws RecognitionException {
        try {
            int _type = T39;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:38:5: ( 'MANY_TO_MANY' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:38:7: 'MANY_TO_MANY'
            {
            match("MANY_TO_MANY"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T39

    // $ANTLR start T40
    public final void mT40() throws RecognitionException {
        try {
            int _type = T40;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:39:5: ( 'MANY_TO_ONE' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:39:7: 'MANY_TO_ONE'
            {
            match("MANY_TO_ONE"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T40

    // $ANTLR start T41
    public final void mT41() throws RecognitionException {
        try {
            int _type = T41;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:40:5: ( 'import' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:40:7: 'import'
            {
            match("import"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T41

    // $ANTLR start T42
    public final void mT42() throws RecognitionException {
        try {
            int _type = T42;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:41:5: ( '.' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:41:7: '.'
            {
            match('.'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T42

    // $ANTLR start T43
    public final void mT43() throws RecognitionException {
        try {
            int _type = T43;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:42:5: ( 'abstract' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:42:7: 'abstract'
            {
            match("abstract"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T43

    // $ANTLR start T44
    public final void mT44() throws RecognitionException {
        try {
            int _type = T44;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:43:5: ( 'artifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:43:7: 'artifactType'
            {
            match("artifactType"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T44

    // $ANTLR start T45
    public final void mT45() throws RecognitionException {
        try {
            int _type = T45;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:44:5: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:44:7: '{'
            {
            match('{'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T45

    // $ANTLR start T46
    public final void mT46() throws RecognitionException {
        try {
            int _type = T46;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:45:5: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:45:7: '}'
            {
            match('}'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T46

    // $ANTLR start T47
    public final void mT47() throws RecognitionException {
        try {
            int _type = T47;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:46:5: ( 'extends' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:46:7: 'extends'
            {
            match("extends"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T47

    // $ANTLR start T48
    public final void mT48() throws RecognitionException {
        try {
            int _type = T48;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:47:5: ( 'attribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:47:7: 'attribute'
            {
            match("attribute"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T48

    // $ANTLR start T49
    public final void mT49() throws RecognitionException {
        try {
            int _type = T49;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:48:5: ( 'attributeType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:48:7: 'attributeType'
            {
            match("attributeType"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T49

    // $ANTLR start T50
    public final void mT50() throws RecognitionException {
        try {
            int _type = T50;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:49:5: ( 'dataProvider' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:49:7: 'dataProvider'
            {
            match("dataProvider"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T50

    // $ANTLR start T51
    public final void mT51() throws RecognitionException {
        try {
            int _type = T51;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:50:5: ( 'min' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:50:7: 'min'
            {
            match("min"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T51

    // $ANTLR start T52
    public final void mT52() throws RecognitionException {
        try {
            int _type = T52;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:51:5: ( 'max' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:51:7: 'max'
            {
            match("max"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T52

    // $ANTLR start T53
    public final void mT53() throws RecognitionException {
        try {
            int _type = T53;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:52:5: ( 'taggerId' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:52:7: 'taggerId'
            {
            match("taggerId"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T53

    // $ANTLR start T54
    public final void mT54() throws RecognitionException {
        try {
            int _type = T54;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:53:5: ( 'enumType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:53:7: 'enumType'
            {
            match("enumType"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T54

    // $ANTLR start T55
    public final void mT55() throws RecognitionException {
        try {
            int _type = T55;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:54:5: ( 'description' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:54:7: 'description'
            {
            match("description"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T55

    // $ANTLR start T56
    public final void mT56() throws RecognitionException {
        try {
            int _type = T56;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:55:5: ( 'defaultValue' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:55:7: 'defaultValue'
            {
            match("defaultValue"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T56

    // $ANTLR start T57
    public final void mT57() throws RecognitionException {
        try {
            int _type = T57;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:56:5: ( 'fileExtension' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:56:7: 'fileExtension'
            {
            match("fileExtension"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T57

    // $ANTLR start T58
    public final void mT58() throws RecognitionException {
        try {
            int _type = T58;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:57:5: ( 'oseeEnumType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:57:7: 'oseeEnumType'
            {
            match("oseeEnumType"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T58

    // $ANTLR start T59
    public final void mT59() throws RecognitionException {
        try {
            int _type = T59;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:58:5: ( 'entry' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:58:7: 'entry'
            {
            match("entry"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T59

    // $ANTLR start T60
    public final void mT60() throws RecognitionException {
        try {
            int _type = T60;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:59:5: ( 'relationType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:59:7: 'relationType'
            {
            match("relationType"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T60

    // $ANTLR start T61
    public final void mT61() throws RecognitionException {
        try {
            int _type = T61;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:60:5: ( 'sideAName' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:60:7: 'sideAName'
            {
            match("sideAName"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T61

    // $ANTLR start T62
    public final void mT62() throws RecognitionException {
        try {
            int _type = T62;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:61:5: ( 'sideAArtifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:61:7: 'sideAArtifactType'
            {
            match("sideAArtifactType"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T62

    // $ANTLR start T63
    public final void mT63() throws RecognitionException {
        try {
            int _type = T63;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:62:5: ( 'sideBName' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:62:7: 'sideBName'
            {
            match("sideBName"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T63

    // $ANTLR start T64
    public final void mT64() throws RecognitionException {
        try {
            int _type = T64;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:63:5: ( 'sideBArtifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:63:7: 'sideBArtifactType'
            {
            match("sideBArtifactType"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T64

    // $ANTLR start T65
    public final void mT65() throws RecognitionException {
        try {
            int _type = T65;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:64:5: ( 'defaultOrderType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:64:7: 'defaultOrderType'
            {
            match("defaultOrderType"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T65

    // $ANTLR start T66
    public final void mT66() throws RecognitionException {
        try {
            int _type = T66;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:65:5: ( 'multiplicity' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:65:7: 'multiplicity'
            {
            match("multiplicity"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T66

    // $ANTLR start RULE_ID
    public final void mRULE_ID() throws RecognitionException {
        try {
            int _type = RULE_ID;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2697:9: ( ( '^' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2697:11: ( '^' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2697:11: ( '^' )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0=='^') ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2697:11: '^'
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

            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2697:40: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2699:10: ( ( '0' .. '9' )+ )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2699:12: ( '0' .. '9' )+
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2699:12: ( '0' .. '9' )+
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
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2699:13: '0' .. '9'
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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2701:13: ( ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2701:15: ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2701:15: ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
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
                    new NoViableAltException("2701:15: ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2701:16: '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )* '\"'
                    {
                    match('\"'); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2701:20: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )*
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
                    	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2701:21: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' )
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
                    	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2701:62: ~ ( ( '\\\\' | '\"' ) )
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2701:82: '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\''
                    {
                    match('\''); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2701:87: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )*
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
                    	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2701:88: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' )
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
                    	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2701:129: ~ ( ( '\\\\' | '\\'' ) )
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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2703:17: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2703:19: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2703:24: ( options {greedy=false; } : . )*
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
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2703:52: .
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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2705:17: ( '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2705:19: '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )?
            {
            match("//"); 

            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2705:24: (~ ( ( '\\n' | '\\r' ) ) )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( ((LA8_0>='\u0000' && LA8_0<='\t')||(LA8_0>='\u000B' && LA8_0<='\f')||(LA8_0>='\u000E' && LA8_0<='\uFFFE')) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2705:24: ~ ( ( '\\n' | '\\r' ) )
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

            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2705:40: ( ( '\\r' )? '\\n' )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0=='\n'||LA10_0=='\r') ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2705:41: ( '\\r' )? '\\n'
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2705:41: ( '\\r' )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0=='\r') ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2705:41: '\\r'
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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2707:9: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2707:11: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2707:11: ( ' ' | '\\t' | '\\r' | '\\n' )+
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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2709:16: ( . )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2709:18: .
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
        // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:8: ( T11 | T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | T33 | T34 | T35 | T36 | T37 | T38 | T39 | T40 | T41 | T42 | T43 | T44 | T45 | T46 | T47 | T48 | T49 | T50 | T51 | T52 | T53 | T54 | T55 | T56 | T57 | T58 | T59 | T60 | T61 | T62 | T63 | T64 | T65 | T66 | RULE_ID | RULE_INT | RULE_STRING | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_WS | RULE_ANY_OTHER )
        int alt12=63;
        int LA12_0 = input.LA(1);

        if ( (LA12_0=='0') ) {
            int LA12_1 = input.LA(2);

            if ( ((LA12_1>='0' && LA12_1<='9')) ) {
                alt12=58;
            }
            else {
                alt12=1;}
        }
        else if ( (LA12_0=='1') ) {
            int LA12_2 = input.LA(2);

            if ( ((LA12_2>='0' && LA12_2<='9')) ) {
                alt12=58;
            }
            else {
                alt12=2;}
        }
        else if ( (LA12_0=='2') ) {
            int LA12_3 = input.LA(2);

            if ( ((LA12_3>='0' && LA12_3<='9')) ) {
                alt12=58;
            }
            else {
                alt12=3;}
        }
        else if ( (LA12_0=='3') ) {
            int LA12_4 = input.LA(2);

            if ( ((LA12_4>='0' && LA12_4<='9')) ) {
                alt12=58;
            }
            else {
                alt12=4;}
        }
        else if ( (LA12_0=='4') ) {
            int LA12_5 = input.LA(2);

            if ( ((LA12_5>='0' && LA12_5<='9')) ) {
                alt12=58;
            }
            else {
                alt12=5;}
        }
        else if ( (LA12_0=='5') ) {
            int LA12_6 = input.LA(2);

            if ( ((LA12_6>='0' && LA12_6<='9')) ) {
                alt12=58;
            }
            else {
                alt12=6;}
        }
        else if ( (LA12_0=='6') ) {
            int LA12_7 = input.LA(2);

            if ( ((LA12_7>='0' && LA12_7<='9')) ) {
                alt12=58;
            }
            else {
                alt12=7;}
        }
        else if ( (LA12_0=='7') ) {
            int LA12_8 = input.LA(2);

            if ( ((LA12_8>='0' && LA12_8<='9')) ) {
                alt12=58;
            }
            else {
                alt12=8;}
        }
        else if ( (LA12_0=='8') ) {
            int LA12_9 = input.LA(2);

            if ( ((LA12_9>='0' && LA12_9<='9')) ) {
                alt12=58;
            }
            else {
                alt12=9;}
        }
        else if ( (LA12_0=='9') ) {
            int LA12_10 = input.LA(2);

            if ( ((LA12_10>='0' && LA12_10<='9')) ) {
                alt12=58;
            }
            else {
                alt12=10;}
        }
        else if ( (LA12_0=='D') ) {
            switch ( input.LA(2) ) {
            case 'e':
                {
                int LA12_56 = input.LA(3);

                if ( (LA12_56=='f') ) {
                    int LA12_97 = input.LA(4);

                    if ( (LA12_97=='a') ) {
                        int LA12_132 = input.LA(5);

                        if ( (LA12_132=='u') ) {
                            int LA12_167 = input.LA(6);

                            if ( (LA12_167=='l') ) {
                                int LA12_201 = input.LA(7);

                                if ( (LA12_201=='t') ) {
                                    int LA12_237 = input.LA(8);

                                    if ( (LA12_237=='A') ) {
                                        int LA12_272 = input.LA(9);

                                        if ( (LA12_272=='t') ) {
                                            int LA12_307 = input.LA(10);

                                            if ( (LA12_307=='t') ) {
                                                int LA12_342 = input.LA(11);

                                                if ( (LA12_342=='r') ) {
                                                    int LA12_375 = input.LA(12);

                                                    if ( (LA12_375=='i') ) {
                                                        int LA12_403 = input.LA(13);

                                                        if ( (LA12_403=='b') ) {
                                                            int LA12_431 = input.LA(14);

                                                            if ( (LA12_431=='u') ) {
                                                                int LA12_456 = input.LA(15);

                                                                if ( (LA12_456=='t') ) {
                                                                    int LA12_474 = input.LA(16);

                                                                    if ( (LA12_474=='e') ) {
                                                                        switch ( input.LA(17) ) {
                                                                        case 'D':
                                                                            {
                                                                            int LA12_502 = input.LA(18);

                                                                            if ( (LA12_502=='a') ) {
                                                                                int LA12_517 = input.LA(19);

                                                                                if ( (LA12_517=='t') ) {
                                                                                    int LA12_529 = input.LA(20);

                                                                                    if ( (LA12_529=='a') ) {
                                                                                        int LA12_539 = input.LA(21);

                                                                                        if ( (LA12_539=='P') ) {
                                                                                            int LA12_549 = input.LA(22);

                                                                                            if ( (LA12_549=='r') ) {
                                                                                                int LA12_557 = input.LA(23);

                                                                                                if ( (LA12_557=='o') ) {
                                                                                                    int LA12_565 = input.LA(24);

                                                                                                    if ( (LA12_565=='v') ) {
                                                                                                        int LA12_573 = input.LA(25);

                                                                                                        if ( (LA12_573=='i') ) {
                                                                                                            int LA12_580 = input.LA(26);

                                                                                                            if ( (LA12_580=='d') ) {
                                                                                                                int LA12_587 = input.LA(27);

                                                                                                                if ( (LA12_587=='e') ) {
                                                                                                                    int LA12_593 = input.LA(28);

                                                                                                                    if ( (LA12_593=='r') ) {
                                                                                                                        int LA12_598 = input.LA(29);

                                                                                                                        if ( ((LA12_598>='0' && LA12_598<='9')||(LA12_598>='A' && LA12_598<='Z')||LA12_598=='_'||(LA12_598>='a' && LA12_598<='z')) ) {
                                                                                                                            alt12=57;
                                                                                                                        }
                                                                                                                        else {
                                                                                                                            alt12=11;}
                                                                                                                    }
                                                                                                                    else {
                                                                                                                        alt12=57;}
                                                                                                                }
                                                                                                                else {
                                                                                                                    alt12=57;}
                                                                                                            }
                                                                                                            else {
                                                                                                                alt12=57;}
                                                                                                        }
                                                                                                        else {
                                                                                                            alt12=57;}
                                                                                                    }
                                                                                                    else {
                                                                                                        alt12=57;}
                                                                                                }
                                                                                                else {
                                                                                                    alt12=57;}
                                                                                            }
                                                                                            else {
                                                                                                alt12=57;}
                                                                                        }
                                                                                        else {
                                                                                            alt12=57;}
                                                                                    }
                                                                                    else {
                                                                                        alt12=57;}
                                                                                }
                                                                                else {
                                                                                    alt12=57;}
                                                                            }
                                                                            else {
                                                                                alt12=57;}
                                                                            }
                                                                            break;
                                                                        case 'T':
                                                                            {
                                                                            int LA12_503 = input.LA(18);

                                                                            if ( (LA12_503=='a') ) {
                                                                                int LA12_518 = input.LA(19);

                                                                                if ( (LA12_518=='g') ) {
                                                                                    int LA12_530 = input.LA(20);

                                                                                    if ( (LA12_530=='g') ) {
                                                                                        int LA12_540 = input.LA(21);

                                                                                        if ( (LA12_540=='e') ) {
                                                                                            int LA12_550 = input.LA(22);

                                                                                            if ( (LA12_550=='r') ) {
                                                                                                int LA12_558 = input.LA(23);

                                                                                                if ( (LA12_558=='P') ) {
                                                                                                    int LA12_566 = input.LA(24);

                                                                                                    if ( (LA12_566=='r') ) {
                                                                                                        int LA12_574 = input.LA(25);

                                                                                                        if ( (LA12_574=='o') ) {
                                                                                                            int LA12_581 = input.LA(26);

                                                                                                            if ( (LA12_581=='v') ) {
                                                                                                                int LA12_588 = input.LA(27);

                                                                                                                if ( (LA12_588=='i') ) {
                                                                                                                    int LA12_594 = input.LA(28);

                                                                                                                    if ( (LA12_594=='d') ) {
                                                                                                                        int LA12_599 = input.LA(29);

                                                                                                                        if ( (LA12_599=='e') ) {
                                                                                                                            int LA12_602 = input.LA(30);

                                                                                                                            if ( (LA12_602=='r') ) {
                                                                                                                                int LA12_603 = input.LA(31);

                                                                                                                                if ( ((LA12_603>='0' && LA12_603<='9')||(LA12_603>='A' && LA12_603<='Z')||LA12_603=='_'||(LA12_603>='a' && LA12_603<='z')) ) {
                                                                                                                                    alt12=57;
                                                                                                                                }
                                                                                                                                else {
                                                                                                                                    alt12=15;}
                                                                                                                            }
                                                                                                                            else {
                                                                                                                                alt12=57;}
                                                                                                                        }
                                                                                                                        else {
                                                                                                                            alt12=57;}
                                                                                                                    }
                                                                                                                    else {
                                                                                                                        alt12=57;}
                                                                                                                }
                                                                                                                else {
                                                                                                                    alt12=57;}
                                                                                                            }
                                                                                                            else {
                                                                                                                alt12=57;}
                                                                                                        }
                                                                                                        else {
                                                                                                            alt12=57;}
                                                                                                    }
                                                                                                    else {
                                                                                                        alt12=57;}
                                                                                                }
                                                                                                else {
                                                                                                    alt12=57;}
                                                                                            }
                                                                                            else {
                                                                                                alt12=57;}
                                                                                        }
                                                                                        else {
                                                                                            alt12=57;}
                                                                                    }
                                                                                    else {
                                                                                        alt12=57;}
                                                                                }
                                                                                else {
                                                                                    alt12=57;}
                                                                            }
                                                                            else {
                                                                                alt12=57;}
                                                                            }
                                                                            break;
                                                                        default:
                                                                            alt12=57;}

                                                                    }
                                                                    else {
                                                                        alt12=57;}
                                                                }
                                                                else {
                                                                    alt12=57;}
                                                            }
                                                            else {
                                                                alt12=57;}
                                                        }
                                                        else {
                                                            alt12=57;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
                }
                break;
            case 'a':
                {
                int LA12_57 = input.LA(3);

                if ( (LA12_57=='t') ) {
                    int LA12_98 = input.LA(4);

                    if ( (LA12_98=='e') ) {
                        int LA12_133 = input.LA(5);

                        if ( (LA12_133=='A') ) {
                            int LA12_168 = input.LA(6);

                            if ( (LA12_168=='t') ) {
                                int LA12_202 = input.LA(7);

                                if ( (LA12_202=='t') ) {
                                    int LA12_238 = input.LA(8);

                                    if ( (LA12_238=='r') ) {
                                        int LA12_273 = input.LA(9);

                                        if ( (LA12_273=='i') ) {
                                            int LA12_308 = input.LA(10);

                                            if ( (LA12_308=='b') ) {
                                                int LA12_343 = input.LA(11);

                                                if ( (LA12_343=='u') ) {
                                                    int LA12_376 = input.LA(12);

                                                    if ( (LA12_376=='t') ) {
                                                        int LA12_404 = input.LA(13);

                                                        if ( (LA12_404=='e') ) {
                                                            int LA12_432 = input.LA(14);

                                                            if ( ((LA12_432>='0' && LA12_432<='9')||(LA12_432>='A' && LA12_432<='Z')||LA12_432=='_'||(LA12_432>='a' && LA12_432<='z')) ) {
                                                                alt12=57;
                                                            }
                                                            else {
                                                                alt12=18;}
                                                        }
                                                        else {
                                                            alt12=57;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
                }
                break;
            default:
                alt12=57;}

        }
        else if ( (LA12_0=='U') ) {
            switch ( input.LA(2) ) {
            case 'n':
                {
                int LA12_59 = input.LA(3);

                if ( (LA12_59=='o') ) {
                    int LA12_99 = input.LA(4);

                    if ( (LA12_99=='r') ) {
                        int LA12_134 = input.LA(5);

                        if ( (LA12_134=='d') ) {
                            int LA12_169 = input.LA(6);

                            if ( (LA12_169=='e') ) {
                                int LA12_203 = input.LA(7);

                                if ( (LA12_203=='r') ) {
                                    int LA12_239 = input.LA(8);

                                    if ( (LA12_239=='e') ) {
                                        int LA12_274 = input.LA(9);

                                        if ( (LA12_274=='d') ) {
                                            int LA12_309 = input.LA(10);

                                            if ( ((LA12_309>='0' && LA12_309<='9')||(LA12_309>='A' && LA12_309<='Z')||LA12_309=='_'||(LA12_309>='a' && LA12_309<='z')) ) {
                                                alt12=57;
                                            }
                                            else {
                                                alt12=27;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
                }
                break;
            case 'r':
                {
                int LA12_60 = input.LA(3);

                if ( (LA12_60=='i') ) {
                    int LA12_100 = input.LA(4);

                    if ( (LA12_100=='A') ) {
                        int LA12_135 = input.LA(5);

                        if ( (LA12_135=='t') ) {
                            int LA12_170 = input.LA(6);

                            if ( (LA12_170=='t') ) {
                                int LA12_204 = input.LA(7);

                                if ( (LA12_204=='r') ) {
                                    int LA12_240 = input.LA(8);

                                    if ( (LA12_240=='i') ) {
                                        int LA12_275 = input.LA(9);

                                        if ( (LA12_275=='b') ) {
                                            int LA12_310 = input.LA(10);

                                            if ( (LA12_310=='u') ) {
                                                int LA12_345 = input.LA(11);

                                                if ( (LA12_345=='t') ) {
                                                    int LA12_377 = input.LA(12);

                                                    if ( (LA12_377=='e') ) {
                                                        int LA12_405 = input.LA(13);

                                                        if ( (LA12_405=='D') ) {
                                                            int LA12_433 = input.LA(14);

                                                            if ( (LA12_433=='a') ) {
                                                                int LA12_458 = input.LA(15);

                                                                if ( (LA12_458=='t') ) {
                                                                    int LA12_475 = input.LA(16);

                                                                    if ( (LA12_475=='a') ) {
                                                                        int LA12_489 = input.LA(17);

                                                                        if ( (LA12_489=='P') ) {
                                                                            int LA12_504 = input.LA(18);

                                                                            if ( (LA12_504=='r') ) {
                                                                                int LA12_519 = input.LA(19);

                                                                                if ( (LA12_519=='o') ) {
                                                                                    int LA12_531 = input.LA(20);

                                                                                    if ( (LA12_531=='v') ) {
                                                                                        int LA12_541 = input.LA(21);

                                                                                        if ( (LA12_541=='i') ) {
                                                                                            int LA12_551 = input.LA(22);

                                                                                            if ( (LA12_551=='d') ) {
                                                                                                int LA12_559 = input.LA(23);

                                                                                                if ( (LA12_559=='e') ) {
                                                                                                    int LA12_567 = input.LA(24);

                                                                                                    if ( (LA12_567=='r') ) {
                                                                                                        int LA12_575 = input.LA(25);

                                                                                                        if ( ((LA12_575>='0' && LA12_575<='9')||(LA12_575>='A' && LA12_575<='Z')||LA12_575=='_'||(LA12_575>='a' && LA12_575<='z')) ) {
                                                                                                            alt12=57;
                                                                                                        }
                                                                                                        else {
                                                                                                            alt12=12;}
                                                                                                    }
                                                                                                    else {
                                                                                                        alt12=57;}
                                                                                                }
                                                                                                else {
                                                                                                    alt12=57;}
                                                                                            }
                                                                                            else {
                                                                                                alt12=57;}
                                                                                        }
                                                                                        else {
                                                                                            alt12=57;}
                                                                                    }
                                                                                    else {
                                                                                        alt12=57;}
                                                                                }
                                                                                else {
                                                                                    alt12=57;}
                                                                            }
                                                                            else {
                                                                                alt12=57;}
                                                                        }
                                                                        else {
                                                                            alt12=57;}
                                                                    }
                                                                    else {
                                                                        alt12=57;}
                                                                }
                                                                else {
                                                                    alt12=57;}
                                                            }
                                                            else {
                                                                alt12=57;}
                                                        }
                                                        else {
                                                            alt12=57;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
                }
                break;
            default:
                alt12=57;}

        }
        else if ( (LA12_0=='M') ) {
            switch ( input.LA(2) ) {
            case 'A':
                {
                int LA12_61 = input.LA(3);

                if ( (LA12_61=='N') ) {
                    int LA12_101 = input.LA(4);

                    if ( (LA12_101=='Y') ) {
                        int LA12_136 = input.LA(5);

                        if ( (LA12_136=='_') ) {
                            int LA12_171 = input.LA(6);

                            if ( (LA12_171=='T') ) {
                                int LA12_205 = input.LA(7);

                                if ( (LA12_205=='O') ) {
                                    int LA12_241 = input.LA(8);

                                    if ( (LA12_241=='_') ) {
                                        switch ( input.LA(9) ) {
                                        case 'O':
                                            {
                                            int LA12_311 = input.LA(10);

                                            if ( (LA12_311=='N') ) {
                                                int LA12_346 = input.LA(11);

                                                if ( (LA12_346=='E') ) {
                                                    int LA12_378 = input.LA(12);

                                                    if ( ((LA12_378>='0' && LA12_378<='9')||(LA12_378>='A' && LA12_378<='Z')||LA12_378=='_'||(LA12_378>='a' && LA12_378<='z')) ) {
                                                        alt12=57;
                                                    }
                                                    else {
                                                        alt12=30;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                            }
                                            break;
                                        case 'M':
                                            {
                                            int LA12_312 = input.LA(10);

                                            if ( (LA12_312=='A') ) {
                                                int LA12_347 = input.LA(11);

                                                if ( (LA12_347=='N') ) {
                                                    int LA12_379 = input.LA(12);

                                                    if ( (LA12_379=='Y') ) {
                                                        int LA12_407 = input.LA(13);

                                                        if ( ((LA12_407>='0' && LA12_407<='9')||(LA12_407>='A' && LA12_407<='Z')||LA12_407=='_'||(LA12_407>='a' && LA12_407<='z')) ) {
                                                            alt12=57;
                                                        }
                                                        else {
                                                            alt12=29;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                            }
                                            break;
                                        default:
                                            alt12=57;}

                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
                }
                break;
            case 'a':
                {
                int LA12_62 = input.LA(3);

                if ( (LA12_62=='p') ) {
                    int LA12_102 = input.LA(4);

                    if ( (LA12_102=='p') ) {
                        int LA12_137 = input.LA(5);

                        if ( (LA12_137=='e') ) {
                            int LA12_172 = input.LA(6);

                            if ( (LA12_172=='d') ) {
                                int LA12_206 = input.LA(7);

                                if ( (LA12_206=='A') ) {
                                    int LA12_242 = input.LA(8);

                                    if ( (LA12_242=='t') ) {
                                        int LA12_277 = input.LA(9);

                                        if ( (LA12_277=='t') ) {
                                            int LA12_313 = input.LA(10);

                                            if ( (LA12_313=='r') ) {
                                                int LA12_348 = input.LA(11);

                                                if ( (LA12_348=='i') ) {
                                                    int LA12_380 = input.LA(12);

                                                    if ( (LA12_380=='b') ) {
                                                        int LA12_408 = input.LA(13);

                                                        if ( (LA12_408=='u') ) {
                                                            int LA12_435 = input.LA(14);

                                                            if ( (LA12_435=='t') ) {
                                                                int LA12_459 = input.LA(15);

                                                                if ( (LA12_459=='e') ) {
                                                                    int LA12_476 = input.LA(16);

                                                                    if ( (LA12_476=='D') ) {
                                                                        int LA12_490 = input.LA(17);

                                                                        if ( (LA12_490=='a') ) {
                                                                            int LA12_505 = input.LA(18);

                                                                            if ( (LA12_505=='t') ) {
                                                                                int LA12_520 = input.LA(19);

                                                                                if ( (LA12_520=='a') ) {
                                                                                    int LA12_532 = input.LA(20);

                                                                                    if ( (LA12_532=='P') ) {
                                                                                        int LA12_542 = input.LA(21);

                                                                                        if ( (LA12_542=='r') ) {
                                                                                            int LA12_552 = input.LA(22);

                                                                                            if ( (LA12_552=='o') ) {
                                                                                                int LA12_560 = input.LA(23);

                                                                                                if ( (LA12_560=='v') ) {
                                                                                                    int LA12_568 = input.LA(24);

                                                                                                    if ( (LA12_568=='i') ) {
                                                                                                        int LA12_576 = input.LA(25);

                                                                                                        if ( (LA12_576=='d') ) {
                                                                                                            int LA12_583 = input.LA(26);

                                                                                                            if ( (LA12_583=='e') ) {
                                                                                                                int LA12_589 = input.LA(27);

                                                                                                                if ( (LA12_589=='r') ) {
                                                                                                                    int LA12_595 = input.LA(28);

                                                                                                                    if ( ((LA12_595>='0' && LA12_595<='9')||(LA12_595>='A' && LA12_595<='Z')||LA12_595=='_'||(LA12_595>='a' && LA12_595<='z')) ) {
                                                                                                                        alt12=57;
                                                                                                                    }
                                                                                                                    else {
                                                                                                                        alt12=13;}
                                                                                                                }
                                                                                                                else {
                                                                                                                    alt12=57;}
                                                                                                            }
                                                                                                            else {
                                                                                                                alt12=57;}
                                                                                                        }
                                                                                                        else {
                                                                                                            alt12=57;}
                                                                                                    }
                                                                                                    else {
                                                                                                        alt12=57;}
                                                                                                }
                                                                                                else {
                                                                                                    alt12=57;}
                                                                                            }
                                                                                            else {
                                                                                                alt12=57;}
                                                                                        }
                                                                                        else {
                                                                                            alt12=57;}
                                                                                    }
                                                                                    else {
                                                                                        alt12=57;}
                                                                                }
                                                                                else {
                                                                                    alt12=57;}
                                                                            }
                                                                            else {
                                                                                alt12=57;}
                                                                        }
                                                                        else {
                                                                            alt12=57;}
                                                                    }
                                                                    else {
                                                                        alt12=57;}
                                                                }
                                                                else {
                                                                    alt12=57;}
                                                            }
                                                            else {
                                                                alt12=57;}
                                                        }
                                                        else {
                                                            alt12=57;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
                }
                break;
            default:
                alt12=57;}

        }
        else if ( (LA12_0=='u') ) {
            int LA12_14 = input.LA(2);

            if ( (LA12_14=='n') ) {
                int LA12_63 = input.LA(3);

                if ( (LA12_63=='l') ) {
                    int LA12_103 = input.LA(4);

                    if ( (LA12_103=='i') ) {
                        int LA12_138 = input.LA(5);

                        if ( (LA12_138=='m') ) {
                            int LA12_173 = input.LA(6);

                            if ( (LA12_173=='i') ) {
                                int LA12_207 = input.LA(7);

                                if ( (LA12_207=='t') ) {
                                    int LA12_243 = input.LA(8);

                                    if ( (LA12_243=='e') ) {
                                        int LA12_278 = input.LA(9);

                                        if ( (LA12_278=='d') ) {
                                            int LA12_314 = input.LA(10);

                                            if ( ((LA12_314>='0' && LA12_314<='9')||(LA12_314>='A' && LA12_314<='Z')||LA12_314=='_'||(LA12_314>='a' && LA12_314<='z')) ) {
                                                alt12=57;
                                            }
                                            else {
                                                alt12=14;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
            }
            else {
                alt12=57;}
        }
        else if ( (LA12_0=='B') ) {
            int LA12_15 = input.LA(2);

            if ( (LA12_15=='o') ) {
                int LA12_64 = input.LA(3);

                if ( (LA12_64=='o') ) {
                    int LA12_104 = input.LA(4);

                    if ( (LA12_104=='l') ) {
                        int LA12_139 = input.LA(5);

                        if ( (LA12_139=='e') ) {
                            int LA12_174 = input.LA(6);

                            if ( (LA12_174=='a') ) {
                                int LA12_208 = input.LA(7);

                                if ( (LA12_208=='n') ) {
                                    int LA12_244 = input.LA(8);

                                    if ( (LA12_244=='A') ) {
                                        int LA12_279 = input.LA(9);

                                        if ( (LA12_279=='t') ) {
                                            int LA12_315 = input.LA(10);

                                            if ( (LA12_315=='t') ) {
                                                int LA12_350 = input.LA(11);

                                                if ( (LA12_350=='r') ) {
                                                    int LA12_381 = input.LA(12);

                                                    if ( (LA12_381=='i') ) {
                                                        int LA12_409 = input.LA(13);

                                                        if ( (LA12_409=='b') ) {
                                                            int LA12_436 = input.LA(14);

                                                            if ( (LA12_436=='u') ) {
                                                                int LA12_460 = input.LA(15);

                                                                if ( (LA12_460=='t') ) {
                                                                    int LA12_477 = input.LA(16);

                                                                    if ( (LA12_477=='e') ) {
                                                                        int LA12_491 = input.LA(17);

                                                                        if ( ((LA12_491>='0' && LA12_491<='9')||(LA12_491>='A' && LA12_491<='Z')||LA12_491=='_'||(LA12_491>='a' && LA12_491<='z')) ) {
                                                                            alt12=57;
                                                                        }
                                                                        else {
                                                                            alt12=16;}
                                                                    }
                                                                    else {
                                                                        alt12=57;}
                                                                }
                                                                else {
                                                                    alt12=57;}
                                                            }
                                                            else {
                                                                alt12=57;}
                                                        }
                                                        else {
                                                            alt12=57;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
            }
            else {
                alt12=57;}
        }
        else if ( (LA12_0=='C') ) {
            int LA12_16 = input.LA(2);

            if ( (LA12_16=='o') ) {
                int LA12_65 = input.LA(3);

                if ( (LA12_65=='m') ) {
                    int LA12_105 = input.LA(4);

                    if ( (LA12_105=='p') ) {
                        int LA12_140 = input.LA(5);

                        if ( (LA12_140=='r') ) {
                            int LA12_175 = input.LA(6);

                            if ( (LA12_175=='e') ) {
                                int LA12_209 = input.LA(7);

                                if ( (LA12_209=='s') ) {
                                    int LA12_245 = input.LA(8);

                                    if ( (LA12_245=='s') ) {
                                        int LA12_280 = input.LA(9);

                                        if ( (LA12_280=='e') ) {
                                            int LA12_316 = input.LA(10);

                                            if ( (LA12_316=='d') ) {
                                                int LA12_351 = input.LA(11);

                                                if ( (LA12_351=='C') ) {
                                                    int LA12_382 = input.LA(12);

                                                    if ( (LA12_382=='o') ) {
                                                        int LA12_410 = input.LA(13);

                                                        if ( (LA12_410=='n') ) {
                                                            int LA12_437 = input.LA(14);

                                                            if ( (LA12_437=='t') ) {
                                                                int LA12_461 = input.LA(15);

                                                                if ( (LA12_461=='e') ) {
                                                                    int LA12_478 = input.LA(16);

                                                                    if ( (LA12_478=='n') ) {
                                                                        int LA12_492 = input.LA(17);

                                                                        if ( (LA12_492=='t') ) {
                                                                            int LA12_507 = input.LA(18);

                                                                            if ( (LA12_507=='A') ) {
                                                                                int LA12_521 = input.LA(19);

                                                                                if ( (LA12_521=='t') ) {
                                                                                    int LA12_533 = input.LA(20);

                                                                                    if ( (LA12_533=='t') ) {
                                                                                        int LA12_543 = input.LA(21);

                                                                                        if ( (LA12_543=='r') ) {
                                                                                            int LA12_553 = input.LA(22);

                                                                                            if ( (LA12_553=='i') ) {
                                                                                                int LA12_561 = input.LA(23);

                                                                                                if ( (LA12_561=='b') ) {
                                                                                                    int LA12_569 = input.LA(24);

                                                                                                    if ( (LA12_569=='u') ) {
                                                                                                        int LA12_577 = input.LA(25);

                                                                                                        if ( (LA12_577=='t') ) {
                                                                                                            int LA12_584 = input.LA(26);

                                                                                                            if ( (LA12_584=='e') ) {
                                                                                                                int LA12_590 = input.LA(27);

                                                                                                                if ( ((LA12_590>='0' && LA12_590<='9')||(LA12_590>='A' && LA12_590<='Z')||LA12_590=='_'||(LA12_590>='a' && LA12_590<='z')) ) {
                                                                                                                    alt12=57;
                                                                                                                }
                                                                                                                else {
                                                                                                                    alt12=17;}
                                                                                                            }
                                                                                                            else {
                                                                                                                alt12=57;}
                                                                                                        }
                                                                                                        else {
                                                                                                            alt12=57;}
                                                                                                    }
                                                                                                    else {
                                                                                                        alt12=57;}
                                                                                                }
                                                                                                else {
                                                                                                    alt12=57;}
                                                                                            }
                                                                                            else {
                                                                                                alt12=57;}
                                                                                        }
                                                                                        else {
                                                                                            alt12=57;}
                                                                                    }
                                                                                    else {
                                                                                        alt12=57;}
                                                                                }
                                                                                else {
                                                                                    alt12=57;}
                                                                            }
                                                                            else {
                                                                                alt12=57;}
                                                                        }
                                                                        else {
                                                                            alt12=57;}
                                                                    }
                                                                    else {
                                                                        alt12=57;}
                                                                }
                                                                else {
                                                                    alt12=57;}
                                                            }
                                                            else {
                                                                alt12=57;}
                                                        }
                                                        else {
                                                            alt12=57;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
            }
            else {
                alt12=57;}
        }
        else if ( (LA12_0=='E') ) {
            int LA12_17 = input.LA(2);

            if ( (LA12_17=='n') ) {
                int LA12_66 = input.LA(3);

                if ( (LA12_66=='u') ) {
                    int LA12_106 = input.LA(4);

                    if ( (LA12_106=='m') ) {
                        int LA12_141 = input.LA(5);

                        if ( (LA12_141=='e') ) {
                            int LA12_176 = input.LA(6);

                            if ( (LA12_176=='r') ) {
                                int LA12_210 = input.LA(7);

                                if ( (LA12_210=='a') ) {
                                    int LA12_246 = input.LA(8);

                                    if ( (LA12_246=='t') ) {
                                        int LA12_281 = input.LA(9);

                                        if ( (LA12_281=='e') ) {
                                            int LA12_317 = input.LA(10);

                                            if ( (LA12_317=='d') ) {
                                                int LA12_352 = input.LA(11);

                                                if ( (LA12_352=='A') ) {
                                                    int LA12_383 = input.LA(12);

                                                    if ( (LA12_383=='t') ) {
                                                        int LA12_411 = input.LA(13);

                                                        if ( (LA12_411=='t') ) {
                                                            int LA12_438 = input.LA(14);

                                                            if ( (LA12_438=='r') ) {
                                                                int LA12_462 = input.LA(15);

                                                                if ( (LA12_462=='i') ) {
                                                                    int LA12_479 = input.LA(16);

                                                                    if ( (LA12_479=='b') ) {
                                                                        int LA12_493 = input.LA(17);

                                                                        if ( (LA12_493=='u') ) {
                                                                            int LA12_508 = input.LA(18);

                                                                            if ( (LA12_508=='t') ) {
                                                                                int LA12_522 = input.LA(19);

                                                                                if ( (LA12_522=='e') ) {
                                                                                    int LA12_534 = input.LA(20);

                                                                                    if ( ((LA12_534>='0' && LA12_534<='9')||(LA12_534>='A' && LA12_534<='Z')||LA12_534=='_'||(LA12_534>='a' && LA12_534<='z')) ) {
                                                                                        alt12=57;
                                                                                    }
                                                                                    else {
                                                                                        alt12=19;}
                                                                                }
                                                                                else {
                                                                                    alt12=57;}
                                                                            }
                                                                            else {
                                                                                alt12=57;}
                                                                        }
                                                                        else {
                                                                            alt12=57;}
                                                                    }
                                                                    else {
                                                                        alt12=57;}
                                                                }
                                                                else {
                                                                    alt12=57;}
                                                            }
                                                            else {
                                                                alt12=57;}
                                                        }
                                                        else {
                                                            alt12=57;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
            }
            else {
                alt12=57;}
        }
        else if ( (LA12_0=='F') ) {
            int LA12_18 = input.LA(2);

            if ( (LA12_18=='l') ) {
                int LA12_67 = input.LA(3);

                if ( (LA12_67=='o') ) {
                    int LA12_107 = input.LA(4);

                    if ( (LA12_107=='a') ) {
                        int LA12_142 = input.LA(5);

                        if ( (LA12_142=='t') ) {
                            int LA12_177 = input.LA(6);

                            if ( (LA12_177=='i') ) {
                                int LA12_211 = input.LA(7);

                                if ( (LA12_211=='n') ) {
                                    int LA12_247 = input.LA(8);

                                    if ( (LA12_247=='g') ) {
                                        int LA12_282 = input.LA(9);

                                        if ( (LA12_282=='P') ) {
                                            int LA12_318 = input.LA(10);

                                            if ( (LA12_318=='o') ) {
                                                int LA12_353 = input.LA(11);

                                                if ( (LA12_353=='i') ) {
                                                    int LA12_384 = input.LA(12);

                                                    if ( (LA12_384=='n') ) {
                                                        int LA12_412 = input.LA(13);

                                                        if ( (LA12_412=='t') ) {
                                                            int LA12_439 = input.LA(14);

                                                            if ( (LA12_439=='A') ) {
                                                                int LA12_463 = input.LA(15);

                                                                if ( (LA12_463=='t') ) {
                                                                    int LA12_480 = input.LA(16);

                                                                    if ( (LA12_480=='t') ) {
                                                                        int LA12_494 = input.LA(17);

                                                                        if ( (LA12_494=='r') ) {
                                                                            int LA12_509 = input.LA(18);

                                                                            if ( (LA12_509=='i') ) {
                                                                                int LA12_523 = input.LA(19);

                                                                                if ( (LA12_523=='b') ) {
                                                                                    int LA12_535 = input.LA(20);

                                                                                    if ( (LA12_535=='u') ) {
                                                                                        int LA12_545 = input.LA(21);

                                                                                        if ( (LA12_545=='t') ) {
                                                                                            int LA12_554 = input.LA(22);

                                                                                            if ( (LA12_554=='e') ) {
                                                                                                int LA12_562 = input.LA(23);

                                                                                                if ( ((LA12_562>='0' && LA12_562<='9')||(LA12_562>='A' && LA12_562<='Z')||LA12_562=='_'||(LA12_562>='a' && LA12_562<='z')) ) {
                                                                                                    alt12=57;
                                                                                                }
                                                                                                else {
                                                                                                    alt12=20;}
                                                                                            }
                                                                                            else {
                                                                                                alt12=57;}
                                                                                        }
                                                                                        else {
                                                                                            alt12=57;}
                                                                                    }
                                                                                    else {
                                                                                        alt12=57;}
                                                                                }
                                                                                else {
                                                                                    alt12=57;}
                                                                            }
                                                                            else {
                                                                                alt12=57;}
                                                                        }
                                                                        else {
                                                                            alt12=57;}
                                                                    }
                                                                    else {
                                                                        alt12=57;}
                                                                }
                                                                else {
                                                                    alt12=57;}
                                                            }
                                                            else {
                                                                alt12=57;}
                                                        }
                                                        else {
                                                            alt12=57;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
            }
            else {
                alt12=57;}
        }
        else if ( (LA12_0=='I') ) {
            int LA12_19 = input.LA(2);

            if ( (LA12_19=='n') ) {
                int LA12_68 = input.LA(3);

                if ( (LA12_68=='t') ) {
                    int LA12_108 = input.LA(4);

                    if ( (LA12_108=='e') ) {
                        int LA12_143 = input.LA(5);

                        if ( (LA12_143=='g') ) {
                            int LA12_178 = input.LA(6);

                            if ( (LA12_178=='e') ) {
                                int LA12_212 = input.LA(7);

                                if ( (LA12_212=='r') ) {
                                    int LA12_248 = input.LA(8);

                                    if ( (LA12_248=='A') ) {
                                        int LA12_283 = input.LA(9);

                                        if ( (LA12_283=='t') ) {
                                            int LA12_319 = input.LA(10);

                                            if ( (LA12_319=='t') ) {
                                                int LA12_354 = input.LA(11);

                                                if ( (LA12_354=='r') ) {
                                                    int LA12_385 = input.LA(12);

                                                    if ( (LA12_385=='i') ) {
                                                        int LA12_413 = input.LA(13);

                                                        if ( (LA12_413=='b') ) {
                                                            int LA12_440 = input.LA(14);

                                                            if ( (LA12_440=='u') ) {
                                                                int LA12_464 = input.LA(15);

                                                                if ( (LA12_464=='t') ) {
                                                                    int LA12_481 = input.LA(16);

                                                                    if ( (LA12_481=='e') ) {
                                                                        int LA12_495 = input.LA(17);

                                                                        if ( ((LA12_495>='0' && LA12_495<='9')||(LA12_495>='A' && LA12_495<='Z')||LA12_495=='_'||(LA12_495>='a' && LA12_495<='z')) ) {
                                                                            alt12=57;
                                                                        }
                                                                        else {
                                                                            alt12=21;}
                                                                    }
                                                                    else {
                                                                        alt12=57;}
                                                                }
                                                                else {
                                                                    alt12=57;}
                                                            }
                                                            else {
                                                                alt12=57;}
                                                        }
                                                        else {
                                                            alt12=57;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
            }
            else {
                alt12=57;}
        }
        else if ( (LA12_0=='J') ) {
            int LA12_20 = input.LA(2);

            if ( (LA12_20=='a') ) {
                int LA12_69 = input.LA(3);

                if ( (LA12_69=='v') ) {
                    int LA12_109 = input.LA(4);

                    if ( (LA12_109=='a') ) {
                        int LA12_144 = input.LA(5);

                        if ( (LA12_144=='O') ) {
                            int LA12_179 = input.LA(6);

                            if ( (LA12_179=='b') ) {
                                int LA12_213 = input.LA(7);

                                if ( (LA12_213=='j') ) {
                                    int LA12_249 = input.LA(8);

                                    if ( (LA12_249=='e') ) {
                                        int LA12_284 = input.LA(9);

                                        if ( (LA12_284=='c') ) {
                                            int LA12_320 = input.LA(10);

                                            if ( (LA12_320=='t') ) {
                                                int LA12_355 = input.LA(11);

                                                if ( (LA12_355=='A') ) {
                                                    int LA12_386 = input.LA(12);

                                                    if ( (LA12_386=='t') ) {
                                                        int LA12_414 = input.LA(13);

                                                        if ( (LA12_414=='t') ) {
                                                            int LA12_441 = input.LA(14);

                                                            if ( (LA12_441=='r') ) {
                                                                int LA12_465 = input.LA(15);

                                                                if ( (LA12_465=='i') ) {
                                                                    int LA12_482 = input.LA(16);

                                                                    if ( (LA12_482=='b') ) {
                                                                        int LA12_496 = input.LA(17);

                                                                        if ( (LA12_496=='u') ) {
                                                                            int LA12_511 = input.LA(18);

                                                                            if ( (LA12_511=='t') ) {
                                                                                int LA12_524 = input.LA(19);

                                                                                if ( (LA12_524=='e') ) {
                                                                                    int LA12_536 = input.LA(20);

                                                                                    if ( ((LA12_536>='0' && LA12_536<='9')||(LA12_536>='A' && LA12_536<='Z')||LA12_536=='_'||(LA12_536>='a' && LA12_536<='z')) ) {
                                                                                        alt12=57;
                                                                                    }
                                                                                    else {
                                                                                        alt12=22;}
                                                                                }
                                                                                else {
                                                                                    alt12=57;}
                                                                            }
                                                                            else {
                                                                                alt12=57;}
                                                                        }
                                                                        else {
                                                                            alt12=57;}
                                                                    }
                                                                    else {
                                                                        alt12=57;}
                                                                }
                                                                else {
                                                                    alt12=57;}
                                                            }
                                                            else {
                                                                alt12=57;}
                                                        }
                                                        else {
                                                            alt12=57;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
            }
            else {
                alt12=57;}
        }
        else if ( (LA12_0=='S') ) {
            int LA12_21 = input.LA(2);

            if ( (LA12_21=='t') ) {
                int LA12_70 = input.LA(3);

                if ( (LA12_70=='r') ) {
                    int LA12_110 = input.LA(4);

                    if ( (LA12_110=='i') ) {
                        int LA12_145 = input.LA(5);

                        if ( (LA12_145=='n') ) {
                            int LA12_180 = input.LA(6);

                            if ( (LA12_180=='g') ) {
                                int LA12_214 = input.LA(7);

                                if ( (LA12_214=='A') ) {
                                    int LA12_250 = input.LA(8);

                                    if ( (LA12_250=='t') ) {
                                        int LA12_285 = input.LA(9);

                                        if ( (LA12_285=='t') ) {
                                            int LA12_321 = input.LA(10);

                                            if ( (LA12_321=='r') ) {
                                                int LA12_356 = input.LA(11);

                                                if ( (LA12_356=='i') ) {
                                                    int LA12_387 = input.LA(12);

                                                    if ( (LA12_387=='b') ) {
                                                        int LA12_415 = input.LA(13);

                                                        if ( (LA12_415=='u') ) {
                                                            int LA12_442 = input.LA(14);

                                                            if ( (LA12_442=='t') ) {
                                                                int LA12_466 = input.LA(15);

                                                                if ( (LA12_466=='e') ) {
                                                                    int LA12_483 = input.LA(16);

                                                                    if ( ((LA12_483>='0' && LA12_483<='9')||(LA12_483>='A' && LA12_483<='Z')||LA12_483=='_'||(LA12_483>='a' && LA12_483<='z')) ) {
                                                                        alt12=57;
                                                                    }
                                                                    else {
                                                                        alt12=23;}
                                                                }
                                                                else {
                                                                    alt12=57;}
                                                            }
                                                            else {
                                                                alt12=57;}
                                                        }
                                                        else {
                                                            alt12=57;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
            }
            else {
                alt12=57;}
        }
        else if ( (LA12_0=='W') ) {
            int LA12_22 = input.LA(2);

            if ( (LA12_22=='o') ) {
                int LA12_71 = input.LA(3);

                if ( (LA12_71=='r') ) {
                    int LA12_111 = input.LA(4);

                    if ( (LA12_111=='d') ) {
                        int LA12_146 = input.LA(5);

                        if ( (LA12_146=='A') ) {
                            int LA12_181 = input.LA(6);

                            if ( (LA12_181=='t') ) {
                                int LA12_215 = input.LA(7);

                                if ( (LA12_215=='t') ) {
                                    int LA12_251 = input.LA(8);

                                    if ( (LA12_251=='r') ) {
                                        int LA12_286 = input.LA(9);

                                        if ( (LA12_286=='i') ) {
                                            int LA12_322 = input.LA(10);

                                            if ( (LA12_322=='b') ) {
                                                int LA12_357 = input.LA(11);

                                                if ( (LA12_357=='u') ) {
                                                    int LA12_388 = input.LA(12);

                                                    if ( (LA12_388=='t') ) {
                                                        int LA12_416 = input.LA(13);

                                                        if ( (LA12_416=='e') ) {
                                                            int LA12_443 = input.LA(14);

                                                            if ( ((LA12_443>='0' && LA12_443<='9')||(LA12_443>='A' && LA12_443<='Z')||LA12_443=='_'||(LA12_443>='a' && LA12_443<='z')) ) {
                                                                alt12=57;
                                                            }
                                                            else {
                                                                alt12=24;}
                                                        }
                                                        else {
                                                            alt12=57;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
            }
            else {
                alt12=57;}
        }
        else if ( (LA12_0=='L') ) {
            int LA12_23 = input.LA(2);

            if ( (LA12_23=='e') ) {
                int LA12_72 = input.LA(3);

                if ( (LA12_72=='x') ) {
                    int LA12_112 = input.LA(4);

                    if ( (LA12_112=='i') ) {
                        int LA12_147 = input.LA(5);

                        if ( (LA12_147=='c') ) {
                            int LA12_182 = input.LA(6);

                            if ( (LA12_182=='o') ) {
                                int LA12_216 = input.LA(7);

                                if ( (LA12_216=='g') ) {
                                    int LA12_252 = input.LA(8);

                                    if ( (LA12_252=='r') ) {
                                        int LA12_287 = input.LA(9);

                                        if ( (LA12_287=='a') ) {
                                            int LA12_323 = input.LA(10);

                                            if ( (LA12_323=='p') ) {
                                                int LA12_358 = input.LA(11);

                                                if ( (LA12_358=='h') ) {
                                                    int LA12_389 = input.LA(12);

                                                    if ( (LA12_389=='i') ) {
                                                        int LA12_417 = input.LA(13);

                                                        if ( (LA12_417=='c') ) {
                                                            int LA12_444 = input.LA(14);

                                                            if ( (LA12_444=='a') ) {
                                                                int LA12_468 = input.LA(15);

                                                                if ( (LA12_468=='l') ) {
                                                                    int LA12_484 = input.LA(16);

                                                                    if ( (LA12_484=='_') ) {
                                                                        switch ( input.LA(17) ) {
                                                                        case 'D':
                                                                            {
                                                                            int LA12_512 = input.LA(18);

                                                                            if ( (LA12_512=='e') ) {
                                                                                int LA12_525 = input.LA(19);

                                                                                if ( (LA12_525=='s') ) {
                                                                                    int LA12_537 = input.LA(20);

                                                                                    if ( (LA12_537=='c') ) {
                                                                                        int LA12_547 = input.LA(21);

                                                                                        if ( (LA12_547=='e') ) {
                                                                                            int LA12_555 = input.LA(22);

                                                                                            if ( (LA12_555=='n') ) {
                                                                                                int LA12_563 = input.LA(23);

                                                                                                if ( (LA12_563=='d') ) {
                                                                                                    int LA12_571 = input.LA(24);

                                                                                                    if ( (LA12_571=='i') ) {
                                                                                                        int LA12_578 = input.LA(25);

                                                                                                        if ( (LA12_578=='n') ) {
                                                                                                            int LA12_585 = input.LA(26);

                                                                                                            if ( (LA12_585=='g') ) {
                                                                                                                int LA12_591 = input.LA(27);

                                                                                                                if ( ((LA12_591>='0' && LA12_591<='9')||(LA12_591>='A' && LA12_591<='Z')||LA12_591=='_'||(LA12_591>='a' && LA12_591<='z')) ) {
                                                                                                                    alt12=57;
                                                                                                                }
                                                                                                                else {
                                                                                                                    alt12=26;}
                                                                                                            }
                                                                                                            else {
                                                                                                                alt12=57;}
                                                                                                        }
                                                                                                        else {
                                                                                                            alt12=57;}
                                                                                                    }
                                                                                                    else {
                                                                                                        alt12=57;}
                                                                                                }
                                                                                                else {
                                                                                                    alt12=57;}
                                                                                            }
                                                                                            else {
                                                                                                alt12=57;}
                                                                                        }
                                                                                        else {
                                                                                            alt12=57;}
                                                                                    }
                                                                                    else {
                                                                                        alt12=57;}
                                                                                }
                                                                                else {
                                                                                    alt12=57;}
                                                                            }
                                                                            else {
                                                                                alt12=57;}
                                                                            }
                                                                            break;
                                                                        case 'A':
                                                                            {
                                                                            int LA12_513 = input.LA(18);

                                                                            if ( (LA12_513=='s') ) {
                                                                                int LA12_526 = input.LA(19);

                                                                                if ( (LA12_526=='c') ) {
                                                                                    int LA12_538 = input.LA(20);

                                                                                    if ( (LA12_538=='e') ) {
                                                                                        int LA12_548 = input.LA(21);

                                                                                        if ( (LA12_548=='n') ) {
                                                                                            int LA12_556 = input.LA(22);

                                                                                            if ( (LA12_556=='d') ) {
                                                                                                int LA12_564 = input.LA(23);

                                                                                                if ( (LA12_564=='i') ) {
                                                                                                    int LA12_572 = input.LA(24);

                                                                                                    if ( (LA12_572=='n') ) {
                                                                                                        int LA12_579 = input.LA(25);

                                                                                                        if ( (LA12_579=='g') ) {
                                                                                                            int LA12_586 = input.LA(26);

                                                                                                            if ( ((LA12_586>='0' && LA12_586<='9')||(LA12_586>='A' && LA12_586<='Z')||LA12_586=='_'||(LA12_586>='a' && LA12_586<='z')) ) {
                                                                                                                alt12=57;
                                                                                                            }
                                                                                                            else {
                                                                                                                alt12=25;}
                                                                                                        }
                                                                                                        else {
                                                                                                            alt12=57;}
                                                                                                    }
                                                                                                    else {
                                                                                                        alt12=57;}
                                                                                                }
                                                                                                else {
                                                                                                    alt12=57;}
                                                                                            }
                                                                                            else {
                                                                                                alt12=57;}
                                                                                        }
                                                                                        else {
                                                                                            alt12=57;}
                                                                                    }
                                                                                    else {
                                                                                        alt12=57;}
                                                                                }
                                                                                else {
                                                                                    alt12=57;}
                                                                            }
                                                                            else {
                                                                                alt12=57;}
                                                                            }
                                                                            break;
                                                                        default:
                                                                            alt12=57;}

                                                                    }
                                                                    else {
                                                                        alt12=57;}
                                                                }
                                                                else {
                                                                    alt12=57;}
                                                            }
                                                            else {
                                                                alt12=57;}
                                                        }
                                                        else {
                                                            alt12=57;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
            }
            else {
                alt12=57;}
        }
        else if ( (LA12_0=='O') ) {
            int LA12_24 = input.LA(2);

            if ( (LA12_24=='N') ) {
                int LA12_73 = input.LA(3);

                if ( (LA12_73=='E') ) {
                    int LA12_113 = input.LA(4);

                    if ( (LA12_113=='_') ) {
                        int LA12_148 = input.LA(5);

                        if ( (LA12_148=='T') ) {
                            int LA12_183 = input.LA(6);

                            if ( (LA12_183=='O') ) {
                                int LA12_217 = input.LA(7);

                                if ( (LA12_217=='_') ) {
                                    int LA12_253 = input.LA(8);

                                    if ( (LA12_253=='M') ) {
                                        int LA12_288 = input.LA(9);

                                        if ( (LA12_288=='A') ) {
                                            int LA12_324 = input.LA(10);

                                            if ( (LA12_324=='N') ) {
                                                int LA12_359 = input.LA(11);

                                                if ( (LA12_359=='Y') ) {
                                                    int LA12_390 = input.LA(12);

                                                    if ( ((LA12_390>='0' && LA12_390<='9')||(LA12_390>='A' && LA12_390<='Z')||LA12_390=='_'||(LA12_390>='a' && LA12_390<='z')) ) {
                                                        alt12=57;
                                                    }
                                                    else {
                                                        alt12=28;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
            }
            else {
                alt12=57;}
        }
        else if ( (LA12_0=='i') ) {
            int LA12_25 = input.LA(2);

            if ( (LA12_25=='m') ) {
                int LA12_74 = input.LA(3);

                if ( (LA12_74=='p') ) {
                    int LA12_114 = input.LA(4);

                    if ( (LA12_114=='o') ) {
                        int LA12_149 = input.LA(5);

                        if ( (LA12_149=='r') ) {
                            int LA12_184 = input.LA(6);

                            if ( (LA12_184=='t') ) {
                                int LA12_218 = input.LA(7);

                                if ( ((LA12_218>='0' && LA12_218<='9')||(LA12_218>='A' && LA12_218<='Z')||LA12_218=='_'||(LA12_218>='a' && LA12_218<='z')) ) {
                                    alt12=57;
                                }
                                else {
                                    alt12=31;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
            }
            else {
                alt12=57;}
        }
        else if ( (LA12_0=='.') ) {
            alt12=32;
        }
        else if ( (LA12_0=='a') ) {
            switch ( input.LA(2) ) {
            case 'b':
                {
                int LA12_76 = input.LA(3);

                if ( (LA12_76=='s') ) {
                    int LA12_115 = input.LA(4);

                    if ( (LA12_115=='t') ) {
                        int LA12_150 = input.LA(5);

                        if ( (LA12_150=='r') ) {
                            int LA12_185 = input.LA(6);

                            if ( (LA12_185=='a') ) {
                                int LA12_219 = input.LA(7);

                                if ( (LA12_219=='c') ) {
                                    int LA12_255 = input.LA(8);

                                    if ( (LA12_255=='t') ) {
                                        int LA12_289 = input.LA(9);

                                        if ( ((LA12_289>='0' && LA12_289<='9')||(LA12_289>='A' && LA12_289<='Z')||LA12_289=='_'||(LA12_289>='a' && LA12_289<='z')) ) {
                                            alt12=57;
                                        }
                                        else {
                                            alt12=33;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
                }
                break;
            case 'r':
                {
                int LA12_77 = input.LA(3);

                if ( (LA12_77=='t') ) {
                    int LA12_116 = input.LA(4);

                    if ( (LA12_116=='i') ) {
                        int LA12_151 = input.LA(5);

                        if ( (LA12_151=='f') ) {
                            int LA12_186 = input.LA(6);

                            if ( (LA12_186=='a') ) {
                                int LA12_220 = input.LA(7);

                                if ( (LA12_220=='c') ) {
                                    int LA12_256 = input.LA(8);

                                    if ( (LA12_256=='t') ) {
                                        int LA12_290 = input.LA(9);

                                        if ( (LA12_290=='T') ) {
                                            int LA12_326 = input.LA(10);

                                            if ( (LA12_326=='y') ) {
                                                int LA12_360 = input.LA(11);

                                                if ( (LA12_360=='p') ) {
                                                    int LA12_391 = input.LA(12);

                                                    if ( (LA12_391=='e') ) {
                                                        int LA12_419 = input.LA(13);

                                                        if ( ((LA12_419>='0' && LA12_419<='9')||(LA12_419>='A' && LA12_419<='Z')||LA12_419=='_'||(LA12_419>='a' && LA12_419<='z')) ) {
                                                            alt12=57;
                                                        }
                                                        else {
                                                            alt12=34;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
                }
                break;
            case 't':
                {
                int LA12_78 = input.LA(3);

                if ( (LA12_78=='t') ) {
                    int LA12_117 = input.LA(4);

                    if ( (LA12_117=='r') ) {
                        int LA12_152 = input.LA(5);

                        if ( (LA12_152=='i') ) {
                            int LA12_187 = input.LA(6);

                            if ( (LA12_187=='b') ) {
                                int LA12_221 = input.LA(7);

                                if ( (LA12_221=='u') ) {
                                    int LA12_257 = input.LA(8);

                                    if ( (LA12_257=='t') ) {
                                        int LA12_291 = input.LA(9);

                                        if ( (LA12_291=='e') ) {
                                            switch ( input.LA(10) ) {
                                            case 'T':
                                                {
                                                int LA12_361 = input.LA(11);

                                                if ( (LA12_361=='y') ) {
                                                    int LA12_392 = input.LA(12);

                                                    if ( (LA12_392=='p') ) {
                                                        int LA12_420 = input.LA(13);

                                                        if ( (LA12_420=='e') ) {
                                                            int LA12_446 = input.LA(14);

                                                            if ( ((LA12_446>='0' && LA12_446<='9')||(LA12_446>='A' && LA12_446<='Z')||LA12_446=='_'||(LA12_446>='a' && LA12_446<='z')) ) {
                                                                alt12=57;
                                                            }
                                                            else {
                                                                alt12=39;}
                                                        }
                                                        else {
                                                            alt12=57;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
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
                                                alt12=57;
                                                }
                                                break;
                                            default:
                                                alt12=38;}

                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
                }
                break;
            default:
                alt12=57;}

        }
        else if ( (LA12_0=='{') ) {
            alt12=35;
        }
        else if ( (LA12_0=='}') ) {
            alt12=36;
        }
        else if ( (LA12_0=='e') ) {
            switch ( input.LA(2) ) {
            case 'n':
                {
                switch ( input.LA(3) ) {
                case 'u':
                    {
                    int LA12_118 = input.LA(4);

                    if ( (LA12_118=='m') ) {
                        int LA12_153 = input.LA(5);

                        if ( (LA12_153=='T') ) {
                            int LA12_188 = input.LA(6);

                            if ( (LA12_188=='y') ) {
                                int LA12_222 = input.LA(7);

                                if ( (LA12_222=='p') ) {
                                    int LA12_258 = input.LA(8);

                                    if ( (LA12_258=='e') ) {
                                        int LA12_292 = input.LA(9);

                                        if ( ((LA12_292>='0' && LA12_292<='9')||(LA12_292>='A' && LA12_292<='Z')||LA12_292=='_'||(LA12_292>='a' && LA12_292<='z')) ) {
                                            alt12=57;
                                        }
                                        else {
                                            alt12=44;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                    }
                    break;
                case 't':
                    {
                    int LA12_119 = input.LA(4);

                    if ( (LA12_119=='r') ) {
                        int LA12_154 = input.LA(5);

                        if ( (LA12_154=='y') ) {
                            int LA12_189 = input.LA(6);

                            if ( ((LA12_189>='0' && LA12_189<='9')||(LA12_189>='A' && LA12_189<='Z')||LA12_189=='_'||(LA12_189>='a' && LA12_189<='z')) ) {
                                alt12=57;
                            }
                            else {
                                alt12=49;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                    }
                    break;
                default:
                    alt12=57;}

                }
                break;
            case 'x':
                {
                int LA12_82 = input.LA(3);

                if ( (LA12_82=='t') ) {
                    int LA12_120 = input.LA(4);

                    if ( (LA12_120=='e') ) {
                        int LA12_155 = input.LA(5);

                        if ( (LA12_155=='n') ) {
                            int LA12_190 = input.LA(6);

                            if ( (LA12_190=='d') ) {
                                int LA12_224 = input.LA(7);

                                if ( (LA12_224=='s') ) {
                                    int LA12_259 = input.LA(8);

                                    if ( ((LA12_259>='0' && LA12_259<='9')||(LA12_259>='A' && LA12_259<='Z')||LA12_259=='_'||(LA12_259>='a' && LA12_259<='z')) ) {
                                        alt12=57;
                                    }
                                    else {
                                        alt12=37;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
                }
                break;
            default:
                alt12=57;}

        }
        else if ( (LA12_0=='d') ) {
            switch ( input.LA(2) ) {
            case 'e':
                {
                switch ( input.LA(3) ) {
                case 'f':
                    {
                    int LA12_121 = input.LA(4);

                    if ( (LA12_121=='a') ) {
                        int LA12_156 = input.LA(5);

                        if ( (LA12_156=='u') ) {
                            int LA12_191 = input.LA(6);

                            if ( (LA12_191=='l') ) {
                                int LA12_225 = input.LA(7);

                                if ( (LA12_225=='t') ) {
                                    switch ( input.LA(8) ) {
                                    case 'V':
                                        {
                                        int LA12_294 = input.LA(9);

                                        if ( (LA12_294=='a') ) {
                                            int LA12_329 = input.LA(10);

                                            if ( (LA12_329=='l') ) {
                                                int LA12_363 = input.LA(11);

                                                if ( (LA12_363=='u') ) {
                                                    int LA12_393 = input.LA(12);

                                                    if ( (LA12_393=='e') ) {
                                                        int LA12_421 = input.LA(13);

                                                        if ( ((LA12_421>='0' && LA12_421<='9')||(LA12_421>='A' && LA12_421<='Z')||LA12_421=='_'||(LA12_421>='a' && LA12_421<='z')) ) {
                                                            alt12=57;
                                                        }
                                                        else {
                                                            alt12=46;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                        }
                                        break;
                                    case 'O':
                                        {
                                        int LA12_295 = input.LA(9);

                                        if ( (LA12_295=='r') ) {
                                            int LA12_330 = input.LA(10);

                                            if ( (LA12_330=='d') ) {
                                                int LA12_364 = input.LA(11);

                                                if ( (LA12_364=='e') ) {
                                                    int LA12_394 = input.LA(12);

                                                    if ( (LA12_394=='r') ) {
                                                        int LA12_422 = input.LA(13);

                                                        if ( (LA12_422=='T') ) {
                                                            int LA12_448 = input.LA(14);

                                                            if ( (LA12_448=='y') ) {
                                                                int LA12_470 = input.LA(15);

                                                                if ( (LA12_470=='p') ) {
                                                                    int LA12_485 = input.LA(16);

                                                                    if ( (LA12_485=='e') ) {
                                                                        int LA12_499 = input.LA(17);

                                                                        if ( ((LA12_499>='0' && LA12_499<='9')||(LA12_499>='A' && LA12_499<='Z')||LA12_499=='_'||(LA12_499>='a' && LA12_499<='z')) ) {
                                                                            alt12=57;
                                                                        }
                                                                        else {
                                                                            alt12=55;}
                                                                    }
                                                                    else {
                                                                        alt12=57;}
                                                                }
                                                                else {
                                                                    alt12=57;}
                                                            }
                                                            else {
                                                                alt12=57;}
                                                        }
                                                        else {
                                                            alt12=57;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                        }
                                        break;
                                    default:
                                        alt12=57;}

                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                    }
                    break;
                case 's':
                    {
                    int LA12_122 = input.LA(4);

                    if ( (LA12_122=='c') ) {
                        int LA12_157 = input.LA(5);

                        if ( (LA12_157=='r') ) {
                            int LA12_192 = input.LA(6);

                            if ( (LA12_192=='i') ) {
                                int LA12_226 = input.LA(7);

                                if ( (LA12_226=='p') ) {
                                    int LA12_261 = input.LA(8);

                                    if ( (LA12_261=='t') ) {
                                        int LA12_296 = input.LA(9);

                                        if ( (LA12_296=='i') ) {
                                            int LA12_331 = input.LA(10);

                                            if ( (LA12_331=='o') ) {
                                                int LA12_365 = input.LA(11);

                                                if ( (LA12_365=='n') ) {
                                                    int LA12_395 = input.LA(12);

                                                    if ( ((LA12_395>='0' && LA12_395<='9')||(LA12_395>='A' && LA12_395<='Z')||LA12_395=='_'||(LA12_395>='a' && LA12_395<='z')) ) {
                                                        alt12=57;
                                                    }
                                                    else {
                                                        alt12=45;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                    }
                    break;
                default:
                    alt12=57;}

                }
                break;
            case 'a':
                {
                int LA12_84 = input.LA(3);

                if ( (LA12_84=='t') ) {
                    int LA12_123 = input.LA(4);

                    if ( (LA12_123=='a') ) {
                        int LA12_158 = input.LA(5);

                        if ( (LA12_158=='P') ) {
                            int LA12_193 = input.LA(6);

                            if ( (LA12_193=='r') ) {
                                int LA12_227 = input.LA(7);

                                if ( (LA12_227=='o') ) {
                                    int LA12_262 = input.LA(8);

                                    if ( (LA12_262=='v') ) {
                                        int LA12_297 = input.LA(9);

                                        if ( (LA12_297=='i') ) {
                                            int LA12_332 = input.LA(10);

                                            if ( (LA12_332=='d') ) {
                                                int LA12_366 = input.LA(11);

                                                if ( (LA12_366=='e') ) {
                                                    int LA12_396 = input.LA(12);

                                                    if ( (LA12_396=='r') ) {
                                                        int LA12_424 = input.LA(13);

                                                        if ( ((LA12_424>='0' && LA12_424<='9')||(LA12_424>='A' && LA12_424<='Z')||LA12_424=='_'||(LA12_424>='a' && LA12_424<='z')) ) {
                                                            alt12=57;
                                                        }
                                                        else {
                                                            alt12=40;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
                }
                break;
            default:
                alt12=57;}

        }
        else if ( (LA12_0=='m') ) {
            switch ( input.LA(2) ) {
            case 'i':
                {
                int LA12_85 = input.LA(3);

                if ( (LA12_85=='n') ) {
                    int LA12_124 = input.LA(4);

                    if ( ((LA12_124>='0' && LA12_124<='9')||(LA12_124>='A' && LA12_124<='Z')||LA12_124=='_'||(LA12_124>='a' && LA12_124<='z')) ) {
                        alt12=57;
                    }
                    else {
                        alt12=41;}
                }
                else {
                    alt12=57;}
                }
                break;
            case 'a':
                {
                int LA12_86 = input.LA(3);

                if ( (LA12_86=='x') ) {
                    int LA12_125 = input.LA(4);

                    if ( ((LA12_125>='0' && LA12_125<='9')||(LA12_125>='A' && LA12_125<='Z')||LA12_125=='_'||(LA12_125>='a' && LA12_125<='z')) ) {
                        alt12=57;
                    }
                    else {
                        alt12=42;}
                }
                else {
                    alt12=57;}
                }
                break;
            case 'u':
                {
                int LA12_87 = input.LA(3);

                if ( (LA12_87=='l') ) {
                    int LA12_126 = input.LA(4);

                    if ( (LA12_126=='t') ) {
                        int LA12_161 = input.LA(5);

                        if ( (LA12_161=='i') ) {
                            int LA12_194 = input.LA(6);

                            if ( (LA12_194=='p') ) {
                                int LA12_228 = input.LA(7);

                                if ( (LA12_228=='l') ) {
                                    int LA12_263 = input.LA(8);

                                    if ( (LA12_263=='i') ) {
                                        int LA12_298 = input.LA(9);

                                        if ( (LA12_298=='c') ) {
                                            int LA12_333 = input.LA(10);

                                            if ( (LA12_333=='i') ) {
                                                int LA12_367 = input.LA(11);

                                                if ( (LA12_367=='t') ) {
                                                    int LA12_397 = input.LA(12);

                                                    if ( (LA12_397=='y') ) {
                                                        int LA12_425 = input.LA(13);

                                                        if ( ((LA12_425>='0' && LA12_425<='9')||(LA12_425>='A' && LA12_425<='Z')||LA12_425=='_'||(LA12_425>='a' && LA12_425<='z')) ) {
                                                            alt12=57;
                                                        }
                                                        else {
                                                            alt12=56;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
                }
                break;
            default:
                alt12=57;}

        }
        else if ( (LA12_0=='t') ) {
            int LA12_33 = input.LA(2);

            if ( (LA12_33=='a') ) {
                int LA12_88 = input.LA(3);

                if ( (LA12_88=='g') ) {
                    int LA12_127 = input.LA(4);

                    if ( (LA12_127=='g') ) {
                        int LA12_162 = input.LA(5);

                        if ( (LA12_162=='e') ) {
                            int LA12_195 = input.LA(6);

                            if ( (LA12_195=='r') ) {
                                int LA12_229 = input.LA(7);

                                if ( (LA12_229=='I') ) {
                                    int LA12_264 = input.LA(8);

                                    if ( (LA12_264=='d') ) {
                                        int LA12_299 = input.LA(9);

                                        if ( ((LA12_299>='0' && LA12_299<='9')||(LA12_299>='A' && LA12_299<='Z')||LA12_299=='_'||(LA12_299>='a' && LA12_299<='z')) ) {
                                            alt12=57;
                                        }
                                        else {
                                            alt12=43;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
            }
            else {
                alt12=57;}
        }
        else if ( (LA12_0=='f') ) {
            int LA12_34 = input.LA(2);

            if ( (LA12_34=='i') ) {
                int LA12_89 = input.LA(3);

                if ( (LA12_89=='l') ) {
                    int LA12_128 = input.LA(4);

                    if ( (LA12_128=='e') ) {
                        int LA12_163 = input.LA(5);

                        if ( (LA12_163=='E') ) {
                            int LA12_196 = input.LA(6);

                            if ( (LA12_196=='x') ) {
                                int LA12_230 = input.LA(7);

                                if ( (LA12_230=='t') ) {
                                    int LA12_265 = input.LA(8);

                                    if ( (LA12_265=='e') ) {
                                        int LA12_300 = input.LA(9);

                                        if ( (LA12_300=='n') ) {
                                            int LA12_335 = input.LA(10);

                                            if ( (LA12_335=='s') ) {
                                                int LA12_368 = input.LA(11);

                                                if ( (LA12_368=='i') ) {
                                                    int LA12_398 = input.LA(12);

                                                    if ( (LA12_398=='o') ) {
                                                        int LA12_426 = input.LA(13);

                                                        if ( (LA12_426=='n') ) {
                                                            int LA12_451 = input.LA(14);

                                                            if ( ((LA12_451>='0' && LA12_451<='9')||(LA12_451>='A' && LA12_451<='Z')||LA12_451=='_'||(LA12_451>='a' && LA12_451<='z')) ) {
                                                                alt12=57;
                                                            }
                                                            else {
                                                                alt12=47;}
                                                        }
                                                        else {
                                                            alt12=57;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
            }
            else {
                alt12=57;}
        }
        else if ( (LA12_0=='o') ) {
            int LA12_35 = input.LA(2);

            if ( (LA12_35=='s') ) {
                int LA12_90 = input.LA(3);

                if ( (LA12_90=='e') ) {
                    int LA12_129 = input.LA(4);

                    if ( (LA12_129=='e') ) {
                        int LA12_164 = input.LA(5);

                        if ( (LA12_164=='E') ) {
                            int LA12_197 = input.LA(6);

                            if ( (LA12_197=='n') ) {
                                int LA12_231 = input.LA(7);

                                if ( (LA12_231=='u') ) {
                                    int LA12_266 = input.LA(8);

                                    if ( (LA12_266=='m') ) {
                                        int LA12_301 = input.LA(9);

                                        if ( (LA12_301=='T') ) {
                                            int LA12_336 = input.LA(10);

                                            if ( (LA12_336=='y') ) {
                                                int LA12_369 = input.LA(11);

                                                if ( (LA12_369=='p') ) {
                                                    int LA12_399 = input.LA(12);

                                                    if ( (LA12_399=='e') ) {
                                                        int LA12_427 = input.LA(13);

                                                        if ( ((LA12_427>='0' && LA12_427<='9')||(LA12_427>='A' && LA12_427<='Z')||LA12_427=='_'||(LA12_427>='a' && LA12_427<='z')) ) {
                                                            alt12=57;
                                                        }
                                                        else {
                                                            alt12=48;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
            }
            else {
                alt12=57;}
        }
        else if ( (LA12_0=='r') ) {
            int LA12_36 = input.LA(2);

            if ( (LA12_36=='e') ) {
                int LA12_91 = input.LA(3);

                if ( (LA12_91=='l') ) {
                    int LA12_130 = input.LA(4);

                    if ( (LA12_130=='a') ) {
                        int LA12_165 = input.LA(5);

                        if ( (LA12_165=='t') ) {
                            int LA12_198 = input.LA(6);

                            if ( (LA12_198=='i') ) {
                                int LA12_232 = input.LA(7);

                                if ( (LA12_232=='o') ) {
                                    int LA12_267 = input.LA(8);

                                    if ( (LA12_267=='n') ) {
                                        int LA12_302 = input.LA(9);

                                        if ( (LA12_302=='T') ) {
                                            int LA12_337 = input.LA(10);

                                            if ( (LA12_337=='y') ) {
                                                int LA12_370 = input.LA(11);

                                                if ( (LA12_370=='p') ) {
                                                    int LA12_400 = input.LA(12);

                                                    if ( (LA12_400=='e') ) {
                                                        int LA12_428 = input.LA(13);

                                                        if ( ((LA12_428>='0' && LA12_428<='9')||(LA12_428>='A' && LA12_428<='Z')||LA12_428=='_'||(LA12_428>='a' && LA12_428<='z')) ) {
                                                            alt12=57;
                                                        }
                                                        else {
                                                            alt12=50;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                            }
                            else {
                                alt12=57;}
                        }
                        else {
                            alt12=57;}
                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
            }
            else {
                alt12=57;}
        }
        else if ( (LA12_0=='s') ) {
            int LA12_37 = input.LA(2);

            if ( (LA12_37=='i') ) {
                int LA12_92 = input.LA(3);

                if ( (LA12_92=='d') ) {
                    int LA12_131 = input.LA(4);

                    if ( (LA12_131=='e') ) {
                        switch ( input.LA(5) ) {
                        case 'B':
                            {
                            switch ( input.LA(6) ) {
                            case 'N':
                                {
                                int LA12_233 = input.LA(7);

                                if ( (LA12_233=='a') ) {
                                    int LA12_268 = input.LA(8);

                                    if ( (LA12_268=='m') ) {
                                        int LA12_303 = input.LA(9);

                                        if ( (LA12_303=='e') ) {
                                            int LA12_338 = input.LA(10);

                                            if ( ((LA12_338>='0' && LA12_338<='9')||(LA12_338>='A' && LA12_338<='Z')||LA12_338=='_'||(LA12_338>='a' && LA12_338<='z')) ) {
                                                alt12=57;
                                            }
                                            else {
                                                alt12=53;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                                }
                                break;
                            case 'A':
                                {
                                int LA12_234 = input.LA(7);

                                if ( (LA12_234=='r') ) {
                                    int LA12_269 = input.LA(8);

                                    if ( (LA12_269=='t') ) {
                                        int LA12_304 = input.LA(9);

                                        if ( (LA12_304=='i') ) {
                                            int LA12_339 = input.LA(10);

                                            if ( (LA12_339=='f') ) {
                                                int LA12_372 = input.LA(11);

                                                if ( (LA12_372=='a') ) {
                                                    int LA12_401 = input.LA(12);

                                                    if ( (LA12_401=='c') ) {
                                                        int LA12_429 = input.LA(13);

                                                        if ( (LA12_429=='t') ) {
                                                            int LA12_454 = input.LA(14);

                                                            if ( (LA12_454=='T') ) {
                                                                int LA12_472 = input.LA(15);

                                                                if ( (LA12_472=='y') ) {
                                                                    int LA12_486 = input.LA(16);

                                                                    if ( (LA12_486=='p') ) {
                                                                        int LA12_500 = input.LA(17);

                                                                        if ( (LA12_500=='e') ) {
                                                                            int LA12_515 = input.LA(18);

                                                                            if ( ((LA12_515>='0' && LA12_515<='9')||(LA12_515>='A' && LA12_515<='Z')||LA12_515=='_'||(LA12_515>='a' && LA12_515<='z')) ) {
                                                                                alt12=57;
                                                                            }
                                                                            else {
                                                                                alt12=54;}
                                                                        }
                                                                        else {
                                                                            alt12=57;}
                                                                    }
                                                                    else {
                                                                        alt12=57;}
                                                                }
                                                                else {
                                                                    alt12=57;}
                                                            }
                                                            else {
                                                                alt12=57;}
                                                        }
                                                        else {
                                                            alt12=57;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                                }
                                break;
                            default:
                                alt12=57;}

                            }
                            break;
                        case 'A':
                            {
                            switch ( input.LA(6) ) {
                            case 'A':
                                {
                                int LA12_235 = input.LA(7);

                                if ( (LA12_235=='r') ) {
                                    int LA12_270 = input.LA(8);

                                    if ( (LA12_270=='t') ) {
                                        int LA12_305 = input.LA(9);

                                        if ( (LA12_305=='i') ) {
                                            int LA12_340 = input.LA(10);

                                            if ( (LA12_340=='f') ) {
                                                int LA12_373 = input.LA(11);

                                                if ( (LA12_373=='a') ) {
                                                    int LA12_402 = input.LA(12);

                                                    if ( (LA12_402=='c') ) {
                                                        int LA12_430 = input.LA(13);

                                                        if ( (LA12_430=='t') ) {
                                                            int LA12_455 = input.LA(14);

                                                            if ( (LA12_455=='T') ) {
                                                                int LA12_473 = input.LA(15);

                                                                if ( (LA12_473=='y') ) {
                                                                    int LA12_487 = input.LA(16);

                                                                    if ( (LA12_487=='p') ) {
                                                                        int LA12_501 = input.LA(17);

                                                                        if ( (LA12_501=='e') ) {
                                                                            int LA12_516 = input.LA(18);

                                                                            if ( ((LA12_516>='0' && LA12_516<='9')||(LA12_516>='A' && LA12_516<='Z')||LA12_516=='_'||(LA12_516>='a' && LA12_516<='z')) ) {
                                                                                alt12=57;
                                                                            }
                                                                            else {
                                                                                alt12=52;}
                                                                        }
                                                                        else {
                                                                            alt12=57;}
                                                                    }
                                                                    else {
                                                                        alt12=57;}
                                                                }
                                                                else {
                                                                    alt12=57;}
                                                            }
                                                            else {
                                                                alt12=57;}
                                                        }
                                                        else {
                                                            alt12=57;}
                                                    }
                                                    else {
                                                        alt12=57;}
                                                }
                                                else {
                                                    alt12=57;}
                                            }
                                            else {
                                                alt12=57;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                                }
                                break;
                            case 'N':
                                {
                                int LA12_236 = input.LA(7);

                                if ( (LA12_236=='a') ) {
                                    int LA12_271 = input.LA(8);

                                    if ( (LA12_271=='m') ) {
                                        int LA12_306 = input.LA(9);

                                        if ( (LA12_306=='e') ) {
                                            int LA12_341 = input.LA(10);

                                            if ( ((LA12_341>='0' && LA12_341<='9')||(LA12_341>='A' && LA12_341<='Z')||LA12_341=='_'||(LA12_341>='a' && LA12_341<='z')) ) {
                                                alt12=57;
                                            }
                                            else {
                                                alt12=51;}
                                        }
                                        else {
                                            alt12=57;}
                                    }
                                    else {
                                        alt12=57;}
                                }
                                else {
                                    alt12=57;}
                                }
                                break;
                            default:
                                alt12=57;}

                            }
                            break;
                        default:
                            alt12=57;}

                    }
                    else {
                        alt12=57;}
                }
                else {
                    alt12=57;}
            }
            else {
                alt12=57;}
        }
        else if ( (LA12_0=='^') ) {
            int LA12_38 = input.LA(2);

            if ( ((LA12_38>='A' && LA12_38<='Z')||LA12_38=='_'||(LA12_38>='a' && LA12_38<='z')) ) {
                alt12=57;
            }
            else {
                alt12=63;}
        }
        else if ( (LA12_0=='A'||(LA12_0>='G' && LA12_0<='H')||LA12_0=='K'||LA12_0=='N'||(LA12_0>='P' && LA12_0<='R')||LA12_0=='T'||LA12_0=='V'||(LA12_0>='X' && LA12_0<='Z')||LA12_0=='_'||(LA12_0>='b' && LA12_0<='c')||(LA12_0>='g' && LA12_0<='h')||(LA12_0>='j' && LA12_0<='l')||LA12_0=='n'||(LA12_0>='p' && LA12_0<='q')||(LA12_0>='v' && LA12_0<='z')) ) {
            alt12=57;
        }
        else if ( (LA12_0=='\"') ) {
            int LA12_40 = input.LA(2);

            if ( ((LA12_40>='\u0000' && LA12_40<='\uFFFE')) ) {
                alt12=59;
            }
            else {
                alt12=63;}
        }
        else if ( (LA12_0=='\'') ) {
            int LA12_41 = input.LA(2);

            if ( ((LA12_41>='\u0000' && LA12_41<='\uFFFE')) ) {
                alt12=59;
            }
            else {
                alt12=63;}
        }
        else if ( (LA12_0=='/') ) {
            switch ( input.LA(2) ) {
            case '*':
                {
                alt12=60;
                }
                break;
            case '/':
                {
                alt12=61;
                }
                break;
            default:
                alt12=63;}

        }
        else if ( ((LA12_0>='\t' && LA12_0<='\n')||LA12_0=='\r'||LA12_0==' ') ) {
            alt12=62;
        }
        else if ( ((LA12_0>='\u0000' && LA12_0<='\b')||(LA12_0>='\u000B' && LA12_0<='\f')||(LA12_0>='\u000E' && LA12_0<='\u001F')||LA12_0=='!'||(LA12_0>='#' && LA12_0<='&')||(LA12_0>='(' && LA12_0<='-')||(LA12_0>=':' && LA12_0<='@')||(LA12_0>='[' && LA12_0<=']')||LA12_0=='`'||LA12_0=='|'||(LA12_0>='~' && LA12_0<='\uFFFE')) ) {
            alt12=63;
        }
        else {
            NoViableAltException nvae =
                new NoViableAltException("1:1: Tokens : ( T11 | T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | T33 | T34 | T35 | T36 | T37 | T38 | T39 | T40 | T41 | T42 | T43 | T44 | T45 | T46 | T47 | T48 | T49 | T50 | T51 | T52 | T53 | T54 | T55 | T56 | T57 | T58 | T59 | T60 | T61 | T62 | T63 | T64 | T65 | T66 | RULE_ID | RULE_INT | RULE_STRING | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_WS | RULE_ANY_OTHER );", 12, 0, input);

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
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:94: T32
                {
                mT32(); 

                }
                break;
            case 23 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:98: T33
                {
                mT33(); 

                }
                break;
            case 24 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:102: T34
                {
                mT34(); 

                }
                break;
            case 25 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:106: T35
                {
                mT35(); 

                }
                break;
            case 26 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:110: T36
                {
                mT36(); 

                }
                break;
            case 27 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:114: T37
                {
                mT37(); 

                }
                break;
            case 28 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:118: T38
                {
                mT38(); 

                }
                break;
            case 29 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:122: T39
                {
                mT39(); 

                }
                break;
            case 30 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:126: T40
                {
                mT40(); 

                }
                break;
            case 31 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:130: T41
                {
                mT41(); 

                }
                break;
            case 32 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:134: T42
                {
                mT42(); 

                }
                break;
            case 33 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:138: T43
                {
                mT43(); 

                }
                break;
            case 34 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:142: T44
                {
                mT44(); 

                }
                break;
            case 35 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:146: T45
                {
                mT45(); 

                }
                break;
            case 36 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:150: T46
                {
                mT46(); 

                }
                break;
            case 37 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:154: T47
                {
                mT47(); 

                }
                break;
            case 38 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:158: T48
                {
                mT48(); 

                }
                break;
            case 39 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:162: T49
                {
                mT49(); 

                }
                break;
            case 40 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:166: T50
                {
                mT50(); 

                }
                break;
            case 41 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:170: T51
                {
                mT51(); 

                }
                break;
            case 42 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:174: T52
                {
                mT52(); 

                }
                break;
            case 43 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:178: T53
                {
                mT53(); 

                }
                break;
            case 44 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:182: T54
                {
                mT54(); 

                }
                break;
            case 45 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:186: T55
                {
                mT55(); 

                }
                break;
            case 46 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:190: T56
                {
                mT56(); 

                }
                break;
            case 47 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:194: T57
                {
                mT57(); 

                }
                break;
            case 48 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:198: T58
                {
                mT58(); 

                }
                break;
            case 49 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:202: T59
                {
                mT59(); 

                }
                break;
            case 50 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:206: T60
                {
                mT60(); 

                }
                break;
            case 51 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:210: T61
                {
                mT61(); 

                }
                break;
            case 52 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:214: T62
                {
                mT62(); 

                }
                break;
            case 53 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:218: T63
                {
                mT63(); 

                }
                break;
            case 54 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:222: T64
                {
                mT64(); 

                }
                break;
            case 55 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:226: T65
                {
                mT65(); 

                }
                break;
            case 56 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:230: T66
                {
                mT66(); 

                }
                break;
            case 57 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:234: RULE_ID
                {
                mRULE_ID(); 

                }
                break;
            case 58 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:242: RULE_INT
                {
                mRULE_INT(); 

                }
                break;
            case 59 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:251: RULE_STRING
                {
                mRULE_STRING(); 

                }
                break;
            case 60 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:263: RULE_ML_COMMENT
                {
                mRULE_ML_COMMENT(); 

                }
                break;
            case 61 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:279: RULE_SL_COMMENT
                {
                mRULE_SL_COMMENT(); 

                }
                break;
            case 62 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:295: RULE_WS
                {
                mRULE_WS(); 

                }
                break;
            case 63 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:303: RULE_ANY_OTHER
                {
                mRULE_ANY_OTHER(); 

                }
                break;

        }

    }


 

}