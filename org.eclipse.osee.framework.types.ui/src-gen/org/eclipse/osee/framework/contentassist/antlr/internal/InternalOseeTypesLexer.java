package org.eclipse.osee.framework.contentassist.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.ui.common.editor.contentassist.antlr.internal.Lexer;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class InternalOseeTypesLexer extends Lexer {
    public static final int RULE_ID=6;
    public static final int RULE_ANY_OTHER=11;
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
    public static final int RULE_INT=7;
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
    public static final int RULE_WHOLE_NUM_STR=5;
    public static final int T49=49;
    public static final int T48=48;
    public static final int T43=43;
    public static final int Tokens=67;
    public static final int RULE_SL_COMMENT=9;
    public static final int T42=42;
    public static final int T41=41;
    public static final int T40=40;
    public static final int T47=47;
    public static final int T46=46;
    public static final int T45=45;
    public static final int RULE_ML_COMMENT=8;
    public static final int T44=44;
    public static final int RULE_STRING=4;
    public static final int T50=50;
    public static final int T59=59;
    public static final int T12=12;
    public static final int T13=13;
    public static final int T14=14;
    public static final int T52=52;
    public static final int T15=15;
    public static final int RULE_WS=10;
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

    // $ANTLR start T12
    public final void mT12() throws RecognitionException {
        try {
            int _type = T12;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:10:5: ( 'DefaultAttributeDataProvider' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:10:7: 'DefaultAttributeDataProvider'
            {
            match("DefaultAttributeDataProvider"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:11:5: ( 'UriAttributeDataProvider' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:11:7: 'UriAttributeDataProvider'
            {
            match("UriAttributeDataProvider"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:12:5: ( 'unlimited' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:12:7: 'unlimited'
            {
            match("unlimited"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:13:5: ( 'DefaultAttributeTaggerProvider' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:13:7: 'DefaultAttributeTaggerProvider'
            {
            match("DefaultAttributeTaggerProvider"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:14:5: ( 'BooleanAttribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:14:7: 'BooleanAttribute'
            {
            match("BooleanAttribute"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:15:5: ( 'CompressedContentAttribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:15:7: 'CompressedContentAttribute'
            {
            match("CompressedContentAttribute"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:16:5: ( 'DateAttribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:16:7: 'DateAttribute'
            {
            match("DateAttribute"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:17:5: ( 'EnumeratedAttribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:17:7: 'EnumeratedAttribute'
            {
            match("EnumeratedAttribute"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:18:5: ( 'FloatingPointAttribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:18:7: 'FloatingPointAttribute'
            {
            match("FloatingPointAttribute"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:19:5: ( 'IntegerAttribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:19:7: 'IntegerAttribute'
            {
            match("IntegerAttribute"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:20:5: ( 'JavaObjectAttribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:20:7: 'JavaObjectAttribute'
            {
            match("JavaObjectAttribute"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:21:5: ( 'StringAttribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:21:7: 'StringAttribute'
            {
            match("StringAttribute"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:22:5: ( 'WordAttribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:22:7: 'WordAttribute'
            {
            match("WordAttribute"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:23:5: ( 'Lexicographical_Ascending' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:23:7: 'Lexicographical_Ascending'
            {
            match("Lexicographical_Ascending"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:24:5: ( 'Lexicographical_Descending' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:24:7: 'Lexicographical_Descending'
            {
            match("Lexicographical_Descending"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:25:5: ( 'Unordered' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:25:7: 'Unordered'
            {
            match("Unordered"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:26:5: ( 'ONE_TO_ONE' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:26:7: 'ONE_TO_ONE'
            {
            match("ONE_TO_ONE"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:27:5: ( 'ONE_TO_MANY' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:27:7: 'ONE_TO_MANY'
            {
            match("ONE_TO_MANY"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:28:5: ( 'MANY_TO_ONE' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:28:7: 'MANY_TO_ONE'
            {
            match("MANY_TO_ONE"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:29:5: ( 'MANY_TO_MANY' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:29:7: 'MANY_TO_MANY'
            {
            match("MANY_TO_MANY"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:30:5: ( 'import' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:30:7: 'import'
            {
            match("import"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:31:5: ( '.' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:31:7: '.'
            {
            match('.'); 

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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:32:5: ( 'artifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:32:7: 'artifactType'
            {
            match("artifactType"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:33:5: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:33:7: '{'
            {
            match('{'); 

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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:34:5: ( 'guid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:34:7: 'guid'
            {
            match("guid"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:35:5: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:35:7: '}'
            {
            match('}'); 

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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:36:5: ( 'extends' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:36:7: 'extends'
            {
            match("extends"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:37:5: ( ',' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:37:7: ','
            {
            match(','); 

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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:38:5: ( 'attribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:38:7: 'attribute'
            {
            match("attribute"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:39:5: ( 'branchGuid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:39:7: 'branchGuid'
            {
            match("branchGuid"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:40:5: ( 'attributeType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:40:7: 'attributeType'
            {
            match("attributeType"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:41:5: ( 'dataProvider' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:41:7: 'dataProvider'
            {
            match("dataProvider"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:42:5: ( 'min' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:42:7: 'min'
            {
            match("min"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:43:5: ( 'max' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:43:7: 'max'
            {
            match("max"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:44:5: ( 'overrides' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:44:7: 'overrides'
            {
            match("overrides"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:45:5: ( 'taggerId' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:45:7: 'taggerId'
            {
            match("taggerId"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:46:5: ( 'enumType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:46:7: 'enumType'
            {
            match("enumType"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:47:5: ( 'description' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:47:7: 'description'
            {
            match("description"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:48:5: ( 'defaultValue' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:48:7: 'defaultValue'
            {
            match("defaultValue"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:49:5: ( 'fileExtension' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:49:7: 'fileExtension'
            {
            match("fileExtension"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:50:5: ( 'oseeEnumType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:50:7: 'oseeEnumType'
            {
            match("oseeEnumType"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:51:5: ( 'entry' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:51:7: 'entry'
            {
            match("entry"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:52:5: ( 'entryGuid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:52:7: 'entryGuid'
            {
            match("entryGuid"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:53:5: ( 'overrides enum' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:53:7: 'overrides enum'
            {
            match("overrides enum"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:54:5: ( 'add' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:54:7: 'add'
            {
            match("add"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:55:5: ( 'remove' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:55:7: 'remove'
            {
            match("remove"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:56:5: ( 'relationType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:56:7: 'relationType'
            {
            match("relationType"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:57:5: ( 'sideAName' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:57:7: 'sideAName'
            {
            match("sideAName"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:58:5: ( 'sideAArtifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:58:7: 'sideAArtifactType'
            {
            match("sideAArtifactType"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:59:5: ( 'sideBName' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:59:7: 'sideBName'
            {
            match("sideBName"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:60:5: ( 'sideBArtifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:60:7: 'sideBArtifactType'
            {
            match("sideBArtifactType"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:61:5: ( 'defaultOrderType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:61:7: 'defaultOrderType'
            {
            match("defaultOrderType"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:62:5: ( 'multiplicity' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:62:7: 'multiplicity'
            {
            match("multiplicity"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:63:5: ( 'abstract' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:63:7: 'abstract'
            {
            match("abstract"); 


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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:64:5: ( 'inheritAll' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:64:7: 'inheritAll'
            {
            match("inheritAll"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T66

    // $ANTLR start RULE_WHOLE_NUM_STR
    public final void mRULE_WHOLE_NUM_STR() throws RecognitionException {
        try {
            int _type = RULE_WHOLE_NUM_STR;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3828:20: ( ( '0' .. '9' )+ )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3828:22: ( '0' .. '9' )+
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3828:22: ( '0' .. '9' )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='0' && LA1_0<='9')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3828:23: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RULE_WHOLE_NUM_STR

    // $ANTLR start RULE_ID
    public final void mRULE_ID() throws RecognitionException {
        try {
            int _type = RULE_ID;
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3830:9: ( ( '^' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3830:11: ( '^' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3830:11: ( '^' )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='^') ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3830:11: '^'
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

            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3830:40: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0>='0' && LA3_0<='9')||(LA3_0>='A' && LA3_0<='Z')||LA3_0=='_'||(LA3_0>='a' && LA3_0<='z')) ) {
                    alt3=1;
                }


                switch (alt3) {
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
            	    break loop3;
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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3832:10: ( ( '0' .. '9' )+ )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3832:12: ( '0' .. '9' )+
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3832:12: ( '0' .. '9' )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>='0' && LA4_0<='9')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3832:13: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3834:13: ( ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3834:15: ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3834:15: ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0=='\"') ) {
                alt7=1;
            }
            else if ( (LA7_0=='\'') ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("3834:15: ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3834:16: '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )* '\"'
                    {
                    match('\"'); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3834:20: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )*
                    loop5:
                    do {
                        int alt5=3;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0=='\\') ) {
                            alt5=1;
                        }
                        else if ( ((LA5_0>='\u0000' && LA5_0<='!')||(LA5_0>='#' && LA5_0<='[')||(LA5_0>=']' && LA5_0<='\uFFFE')) ) {
                            alt5=2;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3834:21: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' )
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
                    	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3834:62: ~ ( ( '\\\\' | '\"' ) )
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
                    	    break loop5;
                        }
                    } while (true);

                    match('\"'); 

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3834:82: '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\''
                    {
                    match('\''); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3834:87: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )*
                    loop6:
                    do {
                        int alt6=3;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0=='\\') ) {
                            alt6=1;
                        }
                        else if ( ((LA6_0>='\u0000' && LA6_0<='&')||(LA6_0>='(' && LA6_0<='[')||(LA6_0>=']' && LA6_0<='\uFFFE')) ) {
                            alt6=2;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3834:88: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' )
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
                    	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3834:129: ~ ( ( '\\\\' | '\\'' ) )
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
                    	    break loop6;
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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3836:17: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3836:19: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3836:24: ( options {greedy=false; } : . )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0=='*') ) {
                    int LA8_1 = input.LA(2);

                    if ( (LA8_1=='/') ) {
                        alt8=2;
                    }
                    else if ( ((LA8_1>='\u0000' && LA8_1<='.')||(LA8_1>='0' && LA8_1<='\uFFFE')) ) {
                        alt8=1;
                    }


                }
                else if ( ((LA8_0>='\u0000' && LA8_0<=')')||(LA8_0>='+' && LA8_0<='\uFFFE')) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3836:52: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop8;
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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3838:17: ( '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3838:19: '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )?
            {
            match("//"); 

            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3838:24: (~ ( ( '\\n' | '\\r' ) ) )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( ((LA9_0>='\u0000' && LA9_0<='\t')||(LA9_0>='\u000B' && LA9_0<='\f')||(LA9_0>='\u000E' && LA9_0<='\uFFFE')) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3838:24: ~ ( ( '\\n' | '\\r' ) )
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
            	    break loop9;
                }
            } while (true);

            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3838:40: ( ( '\\r' )? '\\n' )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0=='\n'||LA11_0=='\r') ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3838:41: ( '\\r' )? '\\n'
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3838:41: ( '\\r' )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0=='\r') ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3838:41: '\\r'
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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3840:9: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3840:11: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3840:11: ( ' ' | '\\t' | '\\r' | '\\n' )+
            int cnt12=0;
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( ((LA12_0>='\t' && LA12_0<='\n')||LA12_0=='\r'||LA12_0==' ') ) {
                    alt12=1;
                }


                switch (alt12) {
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
            	    if ( cnt12 >= 1 ) break loop12;
                        EarlyExitException eee =
                            new EarlyExitException(12, input);
                        throw eee;
                }
                cnt12++;
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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3842:16: ( . )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3842:18: .
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
        // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:8: ( T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | T33 | T34 | T35 | T36 | T37 | T38 | T39 | T40 | T41 | T42 | T43 | T44 | T45 | T46 | T47 | T48 | T49 | T50 | T51 | T52 | T53 | T54 | T55 | T56 | T57 | T58 | T59 | T60 | T61 | T62 | T63 | T64 | T65 | T66 | RULE_WHOLE_NUM_STR | RULE_ID | RULE_INT | RULE_STRING | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_WS | RULE_ANY_OTHER )
        int alt13=63;
        alt13 = dfa13.predict(input);
        switch (alt13) {
            case 1 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:10: T12
                {
                mT12(); 

                }
                break;
            case 2 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:14: T13
                {
                mT13(); 

                }
                break;
            case 3 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:18: T14
                {
                mT14(); 

                }
                break;
            case 4 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:22: T15
                {
                mT15(); 

                }
                break;
            case 5 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:26: T16
                {
                mT16(); 

                }
                break;
            case 6 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:30: T17
                {
                mT17(); 

                }
                break;
            case 7 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:34: T18
                {
                mT18(); 

                }
                break;
            case 8 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:38: T19
                {
                mT19(); 

                }
                break;
            case 9 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:42: T20
                {
                mT20(); 

                }
                break;
            case 10 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:46: T21
                {
                mT21(); 

                }
                break;
            case 11 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:50: T22
                {
                mT22(); 

                }
                break;
            case 12 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:54: T23
                {
                mT23(); 

                }
                break;
            case 13 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:58: T24
                {
                mT24(); 

                }
                break;
            case 14 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:62: T25
                {
                mT25(); 

                }
                break;
            case 15 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:66: T26
                {
                mT26(); 

                }
                break;
            case 16 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:70: T27
                {
                mT27(); 

                }
                break;
            case 17 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:74: T28
                {
                mT28(); 

                }
                break;
            case 18 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:78: T29
                {
                mT29(); 

                }
                break;
            case 19 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:82: T30
                {
                mT30(); 

                }
                break;
            case 20 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:86: T31
                {
                mT31(); 

                }
                break;
            case 21 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:90: T32
                {
                mT32(); 

                }
                break;
            case 22 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:94: T33
                {
                mT33(); 

                }
                break;
            case 23 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:98: T34
                {
                mT34(); 

                }
                break;
            case 24 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:102: T35
                {
                mT35(); 

                }
                break;
            case 25 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:106: T36
                {
                mT36(); 

                }
                break;
            case 26 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:110: T37
                {
                mT37(); 

                }
                break;
            case 27 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:114: T38
                {
                mT38(); 

                }
                break;
            case 28 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:118: T39
                {
                mT39(); 

                }
                break;
            case 29 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:122: T40
                {
                mT40(); 

                }
                break;
            case 30 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:126: T41
                {
                mT41(); 

                }
                break;
            case 31 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:130: T42
                {
                mT42(); 

                }
                break;
            case 32 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:134: T43
                {
                mT43(); 

                }
                break;
            case 33 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:138: T44
                {
                mT44(); 

                }
                break;
            case 34 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:142: T45
                {
                mT45(); 

                }
                break;
            case 35 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:146: T46
                {
                mT46(); 

                }
                break;
            case 36 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:150: T47
                {
                mT47(); 

                }
                break;
            case 37 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:154: T48
                {
                mT48(); 

                }
                break;
            case 38 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:158: T49
                {
                mT49(); 

                }
                break;
            case 39 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:162: T50
                {
                mT50(); 

                }
                break;
            case 40 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:166: T51
                {
                mT51(); 

                }
                break;
            case 41 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:170: T52
                {
                mT52(); 

                }
                break;
            case 42 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:174: T53
                {
                mT53(); 

                }
                break;
            case 43 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:178: T54
                {
                mT54(); 

                }
                break;
            case 44 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:182: T55
                {
                mT55(); 

                }
                break;
            case 45 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:186: T56
                {
                mT56(); 

                }
                break;
            case 46 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:190: T57
                {
                mT57(); 

                }
                break;
            case 47 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:194: T58
                {
                mT58(); 

                }
                break;
            case 48 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:198: T59
                {
                mT59(); 

                }
                break;
            case 49 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:202: T60
                {
                mT60(); 

                }
                break;
            case 50 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:206: T61
                {
                mT61(); 

                }
                break;
            case 51 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:210: T62
                {
                mT62(); 

                }
                break;
            case 52 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:214: T63
                {
                mT63(); 

                }
                break;
            case 53 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:218: T64
                {
                mT64(); 

                }
                break;
            case 54 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:222: T65
                {
                mT65(); 

                }
                break;
            case 55 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:226: T66
                {
                mT66(); 

                }
                break;
            case 56 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:230: RULE_WHOLE_NUM_STR
                {
                mRULE_WHOLE_NUM_STR(); 

                }
                break;
            case 57 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:249: RULE_ID
                {
                mRULE_ID(); 

                }
                break;
            case 58 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:257: RULE_INT
                {
                mRULE_INT(); 

                }
                break;
            case 59 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:266: RULE_STRING
                {
                mRULE_STRING(); 

                }
                break;
            case 60 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:278: RULE_ML_COMMENT
                {
                mRULE_ML_COMMENT(); 

                }
                break;
            case 61 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:294: RULE_SL_COMMENT
                {
                mRULE_SL_COMMENT(); 

                }
                break;
            case 62 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:310: RULE_WS
                {
                mRULE_WS(); 

                }
                break;
            case 63 :
                // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1:318: RULE_ANY_OTHER
                {
                mRULE_ANY_OTHER(); 

                }
                break;

        }

    }


    protected DFA13 dfa13 = new DFA13(this);
    static final String DFA13_eotS =
        "\1\uffff\17\51\1\uffff\1\51\1\uffff\1\51\1\uffff\1\51\1\uffff\10"+
        "\51\1\121\1\46\1\uffff\3\46\2\uffff\2\51\1\uffff\20\51\1\uffff\4"+
        "\51\1\uffff\1\51\1\uffff\2\51\1\uffff\14\51\1\uffff\1\121\4\uffff"+
        "\24\51\1\u0093\11\51\1\u009d\1\u009e\34\51\1\uffff\1\51\1\u00bc"+
        "\7\51\2\uffff\35\51\1\uffff\2\51\1\u00e5\35\51\1\u0105\7\51\1\uffff"+
        "\12\51\1\u0117\24\51\1\uffff\4\51\1\u0131\14\51\1\uffff\30\51\1"+
        "\u0158\1\uffff\1\u0159\11\51\1\u0163\11\51\1\u016d\1\u016e\17\51"+
        "\1\u017f\2\uffff\1\u0180\6\51\1\u0188\1\51\1\uffff\2\51\1\u018c"+
        "\1\51\1\u018e\4\51\2\uffff\12\51\1\u019d\2\51\1\u01a0\2\51\2\uffff"+
        "\1\u01a3\5\51\2\uffff\3\51\1\uffff\1\51\1\uffff\15\51\1\u01ba\1"+
        "\uffff\1\u01bb\1\51\1\uffff\2\51\1\uffff\2\51\1\u01c1\23\51\2\uffff"+
        "\1\u01d5\1\u01d6\1\51\1\u01d8\1\51\1\uffff\1\u01da\1\u01db\1\u01dc"+
        "\1\51\1\u01de\3\51\1\u01e2\10\51\1\u01eb\1\51\2\uffff\1\u01ed\1"+
        "\uffff\1\51\3\uffff\1\u01ef\1\uffff\3\51\1\uffff\10\51\1\uffff\1"+
        "\51\1\uffff\1\51\1\uffff\12\51\1\u0207\6\51\1\u020f\3\51\1\u0213"+
        "\1\51\1\uffff\1\51\1\u0217\5\51\1\uffff\3\51\1\uffff\3\51\1\uffff"+
        "\1\u0223\1\u0224\11\51\2\uffff\4\51\1\u0232\1\51\1\u0234\6\51\1"+
        "\uffff\1\51\1\uffff\15\51\1\u0249\6\51\1\uffff\4\51\1\u0254\5\51"+
        "\1\uffff\2\51\1\u025c\2\51\1\u025f\1\u0260\1\uffff\2\51\2\uffff"+
        "\1\u0263\1\51\1\uffff\1\51\1\u0266\1\uffff";
    static final String DFA13_eofS =
        "\u0267\uffff";
    static final String DFA13_minS =
        "\1\0\1\141\2\156\2\157\1\156\1\154\1\156\1\141\1\164\1\157\1\145"+
        "\1\116\1\101\1\155\1\uffff\1\142\1\uffff\1\165\1\uffff\1\156\1\uffff"+
        "\1\162\2\141\1\163\1\141\1\151\1\145\1\151\1\60\1\101\1\uffff\2"+
        "\0\1\52\2\uffff\1\146\1\164\1\uffff\1\151\1\157\1\154\1\157\1\155"+
        "\1\165\1\157\1\164\1\166\2\162\1\170\1\105\1\116\1\160\1\150\1\uffff"+
        "\2\164\1\144\1\163\1\uffff\1\151\1\uffff\2\164\1\uffff\1\141\1\146"+
        "\1\164\1\156\1\170\1\154\2\145\1\147\2\154\1\144\1\uffff\1\60\4"+
        "\uffff\1\141\1\145\1\101\1\162\1\151\1\154\1\160\1\155\1\141\1\145"+
        "\1\141\1\151\1\144\1\151\1\137\1\131\1\157\1\145\1\151\1\162\1\60"+
        "\1\164\1\144\1\145\1\155\1\162\1\156\1\141\1\143\1\141\2\60\1\164"+
        "\1\162\1\145\1\147\1\145\1\141\1\157\1\145\1\165\1\101\1\164\1\144"+
        "\1\155\1\145\1\162\1\145\1\164\1\147\1\117\1\156\1\101\1\143\1\124"+
        "\1\137\2\162\1\146\1\151\1\uffff\1\162\1\60\1\156\1\124\1\171\1"+
        "\143\1\165\1\162\1\120\2\uffff\1\151\1\162\1\105\1\145\1\105\1\164"+
        "\1\166\1\101\1\154\2\164\1\145\1\151\1\141\1\145\1\162\1\151\1\145"+
        "\1\142\1\147\1\164\1\157\1\117\1\124\1\164\1\151\1\141\1\142\1\141"+
        "\1\uffff\1\144\1\171\1\60\1\150\1\154\1\151\1\162\1\160\1\151\1"+
        "\156\1\162\1\170\1\151\1\145\2\101\2\164\2\162\1\164\1\156\1\163"+
        "\1\141\1\156\1\162\1\152\1\101\1\164\1\147\1\137\1\117\1\60\1\164"+
        "\1\143\1\165\1\143\1\163\1\160\1\165\1\uffff\1\107\1\164\1\160\1"+
        "\157\1\154\1\144\1\165\1\111\1\164\1\157\1\60\1\141\1\162\1\141"+
        "\1\162\1\101\1\162\1\151\2\145\1\101\1\163\1\164\1\147\1\101\1\145"+
        "\1\164\2\162\1\115\1\137\1\uffff\1\101\3\164\1\60\1\145\1\151\1"+
        "\165\1\117\1\164\1\166\1\151\1\145\1\155\1\144\1\145\1\156\1\uffff"+
        "\1\155\1\164\1\155\2\164\1\151\1\142\2\144\1\164\2\145\1\120\1\164"+
        "\1\143\1\164\1\151\1\141\1\101\1\116\1\115\1\154\1\124\1\145\1\60"+
        "\1\uffff\1\60\1\144\1\151\1\141\1\162\2\151\1\143\1\163\1\124\1"+
        "\60\1\156\1\124\1\145\1\151\1\145\1\151\1\164\1\142\1\165\2\60\1"+
        "\164\2\144\1\157\2\164\1\162\1\142\1\160\1\116\1\105\1\116\1\101"+
        "\1\154\1\171\1\60\2\uffff\1\60\1\144\1\154\1\144\1\157\1\144\1\151"+
        "\1\40\1\171\1\uffff\1\163\1\171\1\60\1\146\1\60\1\146\1\162\1\165"+
        "\1\164\2\uffff\1\162\1\103\1\101\1\151\1\162\1\101\1\151\1\165\1"+
        "\150\1\131\1\60\1\105\1\116\1\60\1\160\1\171\2\uffff\1\60\1\165"+
        "\1\145\1\156\1\145\1\164\2\uffff\1\160\1\151\1\160\1\uffff\1\141"+
        "\1\uffff\1\141\1\151\1\164\1\145\1\151\1\157\1\164\1\156\1\151\1"+
        "\164\1\142\1\164\1\151\1\60\1\uffff\1\60\1\131\1\uffff\1\145\1\160"+
        "\1\uffff\1\145\1\162\1\60\1\162\1\171\1\145\1\157\1\145\2\143\1"+
        "\142\1\145\1\104\1\142\1\156\2\164\1\142\1\164\1\165\1\145\1\143"+
        "\2\uffff\2\60\1\145\1\60\1\124\1\uffff\3\60\1\156\1\60\2\164\1\165"+
        "\1\60\1\141\1\165\1\164\1\162\1\101\1\165\1\162\1\164\1\60\1\141"+
        "\2\uffff\1\60\1\uffff\1\171\3\uffff\1\60\1\uffff\2\124\1\164\1\uffff"+
        "\2\164\1\145\1\151\2\164\1\151\1\145\1\uffff\1\154\1\uffff\1\160"+
        "\1\uffff\2\171\1\145\1\141\1\145\1\156\1\142\1\164\1\145\1\142\1"+
        "\60\1\137\1\145\2\160\1\104\1\120\1\60\1\164\1\165\1\162\1\60\1"+
        "\165\1\uffff\1\101\1\60\2\145\2\141\1\162\1\uffff\1\101\1\164\1"+
        "\151\1\uffff\1\164\1\145\1\163\1\uffff\2\60\1\164\1\147\1\157\1"+
        "\164\1\145\1\142\1\145\1\163\1\143\2\uffff\1\141\1\147\1\166\1\164"+
        "\1\60\1\165\1\60\1\143\1\145\1\120\1\145\1\151\1\162\1\uffff\1\164"+
        "\1\uffff\1\145\1\156\2\162\1\144\1\151\1\145\1\156\1\144\1\157\1"+
        "\120\1\145\1\142\1\60\1\144\1\151\1\166\2\162\1\165\1\uffff\1\151"+
        "\1\156\1\151\1\157\1\60\1\164\1\156\1\147\1\144\1\166\1\uffff\1"+
        "\145\1\147\1\60\1\145\1\151\2\60\1\uffff\1\162\1\144\2\uffff\1\60"+
        "\1\145\1\uffff\1\162\1\60\1\uffff";
    static final String DFA13_maxS =
        "\1\ufffe\1\145\1\162\1\156\2\157\1\156\1\154\1\156\1\141\1\164\1"+
        "\157\1\145\1\116\1\101\1\156\1\uffff\1\164\1\uffff\1\165\1\uffff"+
        "\1\170\1\uffff\1\162\1\145\1\165\1\166\1\141\1\151\1\145\1\151\1"+
        "\71\1\172\1\uffff\2\ufffe\1\57\2\uffff\1\146\1\164\1\uffff\1\151"+
        "\1\157\1\154\1\157\1\155\1\165\1\157\1\164\1\166\2\162\1\170\1\105"+
        "\1\116\1\160\1\150\1\uffff\2\164\1\144\1\163\1\uffff\1\151\1\uffff"+
        "\1\164\1\165\1\uffff\1\141\1\163\1\164\1\156\1\170\1\154\2\145\1"+
        "\147\1\154\1\155\1\144\1\uffff\1\71\4\uffff\1\141\1\145\1\101\1"+
        "\162\1\151\1\154\1\160\1\155\1\141\1\145\1\141\1\151\1\144\1\151"+
        "\1\137\1\131\1\157\1\145\1\151\1\162\1\172\1\164\1\144\1\145\1\155"+
        "\1\162\1\156\1\141\1\143\1\141\2\172\1\164\1\162\1\145\1\147\1\145"+
        "\1\141\1\157\1\145\1\165\1\101\1\164\1\144\1\155\1\145\1\162\1\145"+
        "\1\164\1\147\1\117\1\156\1\101\1\143\1\124\1\137\2\162\1\146\1\151"+
        "\1\uffff\1\162\1\172\1\156\1\124\1\171\1\143\1\165\1\162\1\120\2"+
        "\uffff\1\151\1\162\1\105\1\145\1\105\1\164\1\166\1\102\1\154\2\164"+
        "\1\145\1\151\1\141\1\145\1\162\1\151\1\145\1\142\1\147\1\164\1\157"+
        "\1\117\1\124\1\164\1\151\1\141\1\142\1\141\1\uffff\1\144\1\171\1"+
        "\172\1\150\1\154\1\151\1\162\1\160\1\151\1\156\1\162\1\170\1\151"+
        "\1\145\2\116\2\164\2\162\1\164\1\156\1\163\1\141\1\156\1\162\1\152"+
        "\1\101\1\164\1\147\1\137\1\117\1\172\1\164\1\143\1\165\1\143\1\163"+
        "\1\160\1\165\1\uffff\1\107\1\164\1\160\1\157\1\154\1\144\1\165\1"+
        "\111\1\164\1\157\1\172\1\141\1\162\1\141\1\162\1\101\1\162\1\151"+
        "\2\145\1\101\1\163\1\164\1\147\1\101\1\145\1\164\2\162\1\117\1\137"+
        "\1\uffff\1\101\3\164\1\172\1\145\1\151\1\165\1\126\1\164\1\166\1"+
        "\151\1\145\1\155\1\144\1\145\1\156\1\uffff\1\155\1\164\1\155\2\164"+
        "\1\151\1\142\2\144\1\164\2\145\1\120\1\164\1\143\1\164\1\151\1\141"+
        "\1\101\1\116\1\117\1\154\1\124\1\145\1\172\1\uffff\1\172\1\144\1"+
        "\151\1\141\1\162\2\151\1\143\1\163\1\124\1\172\1\156\1\124\1\145"+
        "\1\151\1\145\1\151\1\164\1\142\1\165\2\172\1\164\2\144\1\157\2\164"+
        "\1\162\1\142\1\160\1\116\1\105\1\116\1\101\1\154\1\171\1\172\2\uffff"+
        "\1\172\1\144\1\154\1\144\1\157\1\144\1\151\1\172\1\171\1\uffff\1"+
        "\163\1\171\1\172\1\146\1\172\1\146\1\162\1\165\1\164\2\uffff\1\162"+
        "\1\103\1\101\1\151\1\162\1\101\1\151\1\165\1\150\1\131\1\172\1\105"+
        "\1\116\1\172\1\160\1\171\2\uffff\1\172\1\165\1\145\1\156\1\145\1"+
        "\164\2\uffff\1\160\1\151\1\160\1\uffff\1\141\1\uffff\1\141\1\151"+
        "\1\164\1\145\1\151\1\157\1\164\1\156\1\151\1\164\1\142\1\164\1\151"+
        "\1\172\1\uffff\1\172\1\131\1\uffff\1\145\1\160\1\uffff\1\145\1\162"+
        "\1\172\1\162\1\171\1\145\1\157\1\145\2\143\1\142\1\145\1\104\1\142"+
        "\1\156\2\164\1\142\1\164\1\165\1\145\1\143\2\uffff\2\172\1\145\1"+
        "\172\1\124\1\uffff\3\172\1\156\1\172\2\164\1\165\1\172\1\141\1\165"+
        "\1\164\1\162\1\101\1\165\1\162\1\164\1\172\1\141\2\uffff\1\172\1"+
        "\uffff\1\171\3\uffff\1\172\1\uffff\2\124\1\164\1\uffff\2\164\1\145"+
        "\1\151\2\164\1\151\1\145\1\uffff\1\154\1\uffff\1\160\1\uffff\2\171"+
        "\1\145\1\141\1\145\1\156\1\142\1\164\1\145\1\142\1\172\1\137\1\145"+
        "\2\160\1\124\1\120\1\172\1\164\1\165\1\162\1\172\1\165\1\uffff\1"+
        "\104\1\172\2\145\2\141\1\162\1\uffff\1\101\1\164\1\151\1\uffff\1"+
        "\164\1\145\1\163\1\uffff\2\172\1\164\1\147\1\157\1\164\1\145\1\142"+
        "\1\145\1\163\1\143\2\uffff\1\141\1\147\1\166\1\164\1\172\1\165\1"+
        "\172\1\143\1\145\1\120\1\145\1\151\1\162\1\uffff\1\164\1\uffff\1"+
        "\145\1\156\2\162\1\144\1\151\1\145\1\156\1\144\1\157\1\120\1\145"+
        "\1\142\1\172\1\144\1\151\1\166\2\162\1\165\1\uffff\1\151\1\156\1"+
        "\151\1\157\1\172\1\164\1\156\1\147\1\144\1\166\1\uffff\1\145\1\147"+
        "\1\172\1\145\1\151\2\172\1\uffff\1\162\1\144\2\uffff\1\172\1\145"+
        "\1\uffff\1\162\1\172\1\uffff";
    static final String DFA13_acceptS =
        "\20\uffff\1\26\1\uffff\1\30\1\uffff\1\32\1\uffff\1\34\12\uffff\1"+
        "\71\3\uffff\1\76\1\77\2\uffff\1\71\20\uffff\1\26\4\uffff\1\30\1"+
        "\uffff\1\32\2\uffff\1\34\14\uffff\1\70\1\uffff\1\73\1\75\1\74\1"+
        "\76\74\uffff\1\55\11\uffff\1\41\1\42\35\uffff\1\31\50\uffff\1\52"+
        "\37\uffff\1\25\21\uffff\1\56\31\uffff\1\33\46\uffff\1\66\1\45\11"+
        "\uffff\1\44\11\uffff\1\20\1\3\20\uffff\1\35\1\53\6\uffff\1\54\1"+
        "\43\3\uffff\1\62\1\uffff\1\60\16\uffff\1\21\2\uffff\1\67\2\uffff"+
        "\1\36\26\uffff\1\22\1\23\5\uffff\1\46\23\uffff\1\24\1\27\1\uffff"+
        "\1\47\1\uffff\1\40\1\65\1\51\1\uffff\1\57\3\uffff\1\7\10\uffff\1"+
        "\15\1\uffff\1\37\1\uffff\1\50\27\uffff\1\14\7\uffff\1\5\3\uffff"+
        "\1\12\3\uffff\1\64\13\uffff\1\63\1\61\15\uffff\1\10\1\uffff\1\13"+
        "\24\uffff\1\11\12\uffff\1\2\7\uffff\1\16\2\uffff\1\6\1\17\2\uffff"+
        "\1\1\2\uffff\1\4";
    static final String DFA13_specialS =
        "\u0267\uffff}>";
    static final String[] DFA13_transitionS = {
            "\11\46\2\45\2\46\1\45\22\46\1\45\1\46\1\42\4\46\1\43\4\46\1"+
            "\26\1\46\1\20\1\44\12\37\7\46\1\41\1\4\1\5\1\1\1\6\1\7\2\41"+
            "\1\10\1\11\1\41\1\14\1\16\1\41\1\15\3\41\1\12\1\41\1\2\1\41"+
            "\1\13\3\41\3\46\1\40\1\41\1\46\1\21\1\27\1\41\1\30\1\25\1\34"+
            "\1\23\1\41\1\17\3\41\1\31\1\41\1\32\2\41\1\35\1\36\1\33\1\3"+
            "\5\41\1\22\1\46\1\24\uff81\46",
            "\1\50\3\uffff\1\47",
            "\1\53\3\uffff\1\52",
            "\1\54",
            "\1\55",
            "\1\56",
            "\1\57",
            "\1\60",
            "\1\61",
            "\1\62",
            "\1\63",
            "\1\64",
            "\1\65",
            "\1\66",
            "\1\67",
            "\1\70\1\71",
            "",
            "\1\76\1\uffff\1\75\15\uffff\1\73\1\uffff\1\74",
            "",
            "\1\100",
            "",
            "\1\103\11\uffff\1\102",
            "",
            "\1\105",
            "\1\107\3\uffff\1\106",
            "\1\111\7\uffff\1\110\13\uffff\1\112",
            "\1\114\2\uffff\1\113",
            "\1\115",
            "\1\116",
            "\1\117",
            "\1\120",
            "\12\122",
            "\32\51\4\uffff\1\51\1\uffff\32\51",
            "",
            "\uffff\123",
            "\uffff\123",
            "\1\125\4\uffff\1\124",
            "",
            "",
            "\1\127",
            "\1\130",
            "",
            "\1\131",
            "\1\132",
            "\1\133",
            "\1\134",
            "\1\135",
            "\1\136",
            "\1\137",
            "\1\140",
            "\1\141",
            "\1\142",
            "\1\143",
            "\1\144",
            "\1\145",
            "\1\146",
            "\1\147",
            "\1\150",
            "",
            "\1\151",
            "\1\152",
            "\1\153",
            "\1\154",
            "",
            "\1\155",
            "",
            "\1\156",
            "\1\160\1\157",
            "",
            "\1\161",
            "\1\162\14\uffff\1\163",
            "\1\164",
            "\1\165",
            "\1\166",
            "\1\167",
            "\1\170",
            "\1\171",
            "\1\172",
            "\1\173",
            "\1\174\1\175",
            "\1\176",
            "",
            "\12\122",
            "",
            "",
            "",
            "",
            "\1\177",
            "\1\u0080",
            "\1\u0081",
            "\1\u0082",
            "\1\u0083",
            "\1\u0084",
            "\1\u0085",
            "\1\u0086",
            "\1\u0087",
            "\1\u0088",
            "\1\u0089",
            "\1\u008a",
            "\1\u008b",
            "\1\u008c",
            "\1\u008d",
            "\1\u008e",
            "\1\u008f",
            "\1\u0090",
            "\1\u0091",
            "\1\u0092",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u0094",
            "\1\u0095",
            "\1\u0096",
            "\1\u0097",
            "\1\u0098",
            "\1\u0099",
            "\1\u009a",
            "\1\u009b",
            "\1\u009c",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u009f",
            "\1\u00a0",
            "\1\u00a1",
            "\1\u00a2",
            "\1\u00a3",
            "\1\u00a4",
            "\1\u00a5",
            "\1\u00a6",
            "\1\u00a7",
            "\1\u00a8",
            "\1\u00a9",
            "\1\u00aa",
            "\1\u00ab",
            "\1\u00ac",
            "\1\u00ad",
            "\1\u00ae",
            "\1\u00af",
            "\1\u00b0",
            "\1\u00b1",
            "\1\u00b2",
            "\1\u00b3",
            "\1\u00b4",
            "\1\u00b5",
            "\1\u00b6",
            "\1\u00b7",
            "\1\u00b8",
            "\1\u00b9",
            "\1\u00ba",
            "",
            "\1\u00bb",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u00bd",
            "\1\u00be",
            "\1\u00bf",
            "\1\u00c0",
            "\1\u00c1",
            "\1\u00c2",
            "\1\u00c3",
            "",
            "",
            "\1\u00c4",
            "\1\u00c5",
            "\1\u00c6",
            "\1\u00c7",
            "\1\u00c8",
            "\1\u00c9",
            "\1\u00ca",
            "\1\u00cc\1\u00cb",
            "\1\u00cd",
            "\1\u00ce",
            "\1\u00cf",
            "\1\u00d0",
            "\1\u00d1",
            "\1\u00d2",
            "\1\u00d3",
            "\1\u00d4",
            "\1\u00d5",
            "\1\u00d6",
            "\1\u00d7",
            "\1\u00d8",
            "\1\u00d9",
            "\1\u00da",
            "\1\u00db",
            "\1\u00dc",
            "\1\u00dd",
            "\1\u00de",
            "\1\u00df",
            "\1\u00e0",
            "\1\u00e1",
            "",
            "\1\u00e2",
            "\1\u00e3",
            "\12\51\7\uffff\6\51\1\u00e4\23\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u00e6",
            "\1\u00e7",
            "\1\u00e8",
            "\1\u00e9",
            "\1\u00ea",
            "\1\u00eb",
            "\1\u00ec",
            "\1\u00ed",
            "\1\u00ee",
            "\1\u00ef",
            "\1\u00f0",
            "\1\u00f2\14\uffff\1\u00f1",
            "\1\u00f4\14\uffff\1\u00f3",
            "\1\u00f5",
            "\1\u00f6",
            "\1\u00f7",
            "\1\u00f8",
            "\1\u00f9",
            "\1\u00fa",
            "\1\u00fb",
            "\1\u00fc",
            "\1\u00fd",
            "\1\u00fe",
            "\1\u00ff",
            "\1\u0100",
            "\1\u0101",
            "\1\u0102",
            "\1\u0103",
            "\1\u0104",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u0106",
            "\1\u0107",
            "\1\u0108",
            "\1\u0109",
            "\1\u010a",
            "\1\u010b",
            "\1\u010c",
            "",
            "\1\u010d",
            "\1\u010e",
            "\1\u010f",
            "\1\u0110",
            "\1\u0111",
            "\1\u0112",
            "\1\u0113",
            "\1\u0114",
            "\1\u0115",
            "\1\u0116",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u0118",
            "\1\u0119",
            "\1\u011a",
            "\1\u011b",
            "\1\u011c",
            "\1\u011d",
            "\1\u011e",
            "\1\u011f",
            "\1\u0120",
            "\1\u0121",
            "\1\u0122",
            "\1\u0123",
            "\1\u0124",
            "\1\u0125",
            "\1\u0126",
            "\1\u0127",
            "\1\u0128",
            "\1\u0129",
            "\1\u012a\1\uffff\1\u012b",
            "\1\u012c",
            "",
            "\1\u012d",
            "\1\u012e",
            "\1\u012f",
            "\1\u0130",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u0132",
            "\1\u0133",
            "\1\u0134",
            "\1\u0136\6\uffff\1\u0135",
            "\1\u0137",
            "\1\u0138",
            "\1\u0139",
            "\1\u013a",
            "\1\u013b",
            "\1\u013c",
            "\1\u013d",
            "\1\u013e",
            "",
            "\1\u013f",
            "\1\u0140",
            "\1\u0141",
            "\1\u0142",
            "\1\u0143",
            "\1\u0144",
            "\1\u0145",
            "\1\u0146",
            "\1\u0147",
            "\1\u0148",
            "\1\u0149",
            "\1\u014a",
            "\1\u014b",
            "\1\u014c",
            "\1\u014d",
            "\1\u014e",
            "\1\u014f",
            "\1\u0150",
            "\1\u0151",
            "\1\u0152",
            "\1\u0154\1\uffff\1\u0153",
            "\1\u0155",
            "\1\u0156",
            "\1\u0157",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u015a",
            "\1\u015b",
            "\1\u015c",
            "\1\u015d",
            "\1\u015e",
            "\1\u015f",
            "\1\u0160",
            "\1\u0161",
            "\1\u0162",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u0164",
            "\1\u0165",
            "\1\u0166",
            "\1\u0167",
            "\1\u0168",
            "\1\u0169",
            "\1\u016a",
            "\1\u016b",
            "\1\u016c",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u016f",
            "\1\u0170",
            "\1\u0171",
            "\1\u0172",
            "\1\u0173",
            "\1\u0174",
            "\1\u0175",
            "\1\u0176",
            "\1\u0177",
            "\1\u0178",
            "\1\u0179",
            "\1\u017a",
            "\1\u017b",
            "\1\u017c",
            "\1\u017d",
            "\12\51\7\uffff\23\51\1\u017e\6\51\4\uffff\1\51\1\uffff\32\51",
            "",
            "",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u0181",
            "\1\u0182",
            "\1\u0183",
            "\1\u0184",
            "\1\u0185",
            "\1\u0186",
            "\1\u0187\17\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32"+
            "\51",
            "\1\u0189",
            "",
            "\1\u018a",
            "\1\u018b",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u018d",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u018f",
            "\1\u0190",
            "\1\u0191",
            "\1\u0192",
            "",
            "",
            "\1\u0193",
            "\1\u0194",
            "\1\u0195",
            "\1\u0196",
            "\1\u0197",
            "\1\u0198",
            "\1\u0199",
            "\1\u019a",
            "\1\u019b",
            "\1\u019c",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u019e",
            "\1\u019f",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u01a1",
            "\1\u01a2",
            "",
            "",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u01a4",
            "\1\u01a5",
            "\1\u01a6",
            "\1\u01a7",
            "\1\u01a8",
            "",
            "",
            "\1\u01a9",
            "\1\u01aa",
            "\1\u01ab",
            "",
            "\1\u01ac",
            "",
            "\1\u01ad",
            "\1\u01ae",
            "\1\u01af",
            "\1\u01b0",
            "\1\u01b1",
            "\1\u01b2",
            "\1\u01b3",
            "\1\u01b4",
            "\1\u01b5",
            "\1\u01b6",
            "\1\u01b7",
            "\1\u01b8",
            "\1\u01b9",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u01bc",
            "",
            "\1\u01bd",
            "\1\u01be",
            "",
            "\1\u01bf",
            "\1\u01c0",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u01c2",
            "\1\u01c3",
            "\1\u01c4",
            "\1\u01c5",
            "\1\u01c6",
            "\1\u01c7",
            "\1\u01c8",
            "\1\u01c9",
            "\1\u01ca",
            "\1\u01cb",
            "\1\u01cc",
            "\1\u01cd",
            "\1\u01ce",
            "\1\u01cf",
            "\1\u01d0",
            "\1\u01d1",
            "\1\u01d2",
            "\1\u01d3",
            "\1\u01d4",
            "",
            "",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u01d7",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u01d9",
            "",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u01dd",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u01df",
            "\1\u01e0",
            "\1\u01e1",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u01e3",
            "\1\u01e4",
            "\1\u01e5",
            "\1\u01e6",
            "\1\u01e7",
            "\1\u01e8",
            "\1\u01e9",
            "\1\u01ea",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u01ec",
            "",
            "",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "",
            "\1\u01ee",
            "",
            "",
            "",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "",
            "\1\u01f0",
            "\1\u01f1",
            "\1\u01f2",
            "",
            "\1\u01f3",
            "\1\u01f4",
            "\1\u01f5",
            "\1\u01f6",
            "\1\u01f7",
            "\1\u01f8",
            "\1\u01f9",
            "\1\u01fa",
            "",
            "\1\u01fb",
            "",
            "\1\u01fc",
            "",
            "\1\u01fd",
            "\1\u01fe",
            "\1\u01ff",
            "\1\u0200",
            "\1\u0201",
            "\1\u0202",
            "\1\u0203",
            "\1\u0204",
            "\1\u0205",
            "\1\u0206",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u0208",
            "\1\u0209",
            "\1\u020a",
            "\1\u020b",
            "\1\u020c\17\uffff\1\u020d",
            "\1\u020e",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u0210",
            "\1\u0211",
            "\1\u0212",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u0214",
            "",
            "\1\u0216\2\uffff\1\u0215",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u0218",
            "\1\u0219",
            "\1\u021a",
            "\1\u021b",
            "\1\u021c",
            "",
            "\1\u021d",
            "\1\u021e",
            "\1\u021f",
            "",
            "\1\u0220",
            "\1\u0221",
            "\1\u0222",
            "",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u0225",
            "\1\u0226",
            "\1\u0227",
            "\1\u0228",
            "\1\u0229",
            "\1\u022a",
            "\1\u022b",
            "\1\u022c",
            "\1\u022d",
            "",
            "",
            "\1\u022e",
            "\1\u022f",
            "\1\u0230",
            "\1\u0231",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u0233",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u0235",
            "\1\u0236",
            "\1\u0237",
            "\1\u0238",
            "\1\u0239",
            "\1\u023a",
            "",
            "\1\u023b",
            "",
            "\1\u023c",
            "\1\u023d",
            "\1\u023e",
            "\1\u023f",
            "\1\u0240",
            "\1\u0241",
            "\1\u0242",
            "\1\u0243",
            "\1\u0244",
            "\1\u0245",
            "\1\u0246",
            "\1\u0247",
            "\1\u0248",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u024a",
            "\1\u024b",
            "\1\u024c",
            "\1\u024d",
            "\1\u024e",
            "\1\u024f",
            "",
            "\1\u0250",
            "\1\u0251",
            "\1\u0252",
            "\1\u0253",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u0255",
            "\1\u0256",
            "\1\u0257",
            "\1\u0258",
            "\1\u0259",
            "",
            "\1\u025a",
            "\1\u025b",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u025d",
            "\1\u025e",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "",
            "\1\u0261",
            "\1\u0262",
            "",
            "",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u0264",
            "",
            "\1\u0265",
            "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            ""
    };

    static final short[] DFA13_eot = DFA.unpackEncodedString(DFA13_eotS);
    static final short[] DFA13_eof = DFA.unpackEncodedString(DFA13_eofS);
    static final char[] DFA13_min = DFA.unpackEncodedStringToUnsignedChars(DFA13_minS);
    static final char[] DFA13_max = DFA.unpackEncodedStringToUnsignedChars(DFA13_maxS);
    static final short[] DFA13_accept = DFA.unpackEncodedString(DFA13_acceptS);
    static final short[] DFA13_special = DFA.unpackEncodedString(DFA13_specialS);
    static final short[][] DFA13_transition;

    static {
        int numStates = DFA13_transitionS.length;
        DFA13_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA13_transition[i] = DFA.unpackEncodedString(DFA13_transitionS[i]);
        }
    }

    class DFA13 extends DFA {

        public DFA13(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 13;
            this.eot = DFA13_eot;
            this.eof = DFA13_eof;
            this.min = DFA13_min;
            this.max = DFA13_max;
            this.accept = DFA13_accept;
            this.special = DFA13_special;
            this.transition = DFA13_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | T33 | T34 | T35 | T36 | T37 | T38 | T39 | T40 | T41 | T42 | T43 | T44 | T45 | T46 | T47 | T48 | T49 | T50 | T51 | T52 | T53 | T54 | T55 | T56 | T57 | T58 | T59 | T60 | T61 | T62 | T63 | T64 | T65 | T66 | RULE_WHOLE_NUM_STR | RULE_ID | RULE_INT | RULE_STRING | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_WS | RULE_ANY_OTHER );";
        }
    }
 

}