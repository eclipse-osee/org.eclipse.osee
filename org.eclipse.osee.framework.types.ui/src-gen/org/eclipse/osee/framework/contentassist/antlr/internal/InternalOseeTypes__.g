lexer grammar InternalOseeTypes;
@header {
package org.eclipse.osee.framework.contentassist.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.ui.common.editor.contentassist.antlr.internal.Lexer;
}

T11 : '0' ;
T12 : '1' ;
T13 : '2' ;
T14 : '3' ;
T15 : '4' ;
T16 : '5' ;
T17 : '6' ;
T18 : '7' ;
T19 : '8' ;
T20 : '9' ;
T21 : 'DefaultAttributeDataProvider' ;
T22 : 'UriAttributeDataProvider' ;
T23 : 'MappedAttributeDataProvider' ;
T24 : 'unlimited' ;
T25 : 'DefaultAttributeTaggerProvider' ;
T26 : 'BooleanAttribute' ;
T27 : 'CompressedContentAttribute' ;
T28 : 'DateAttribute' ;
T29 : 'EnumeratedAttribute' ;
T30 : 'FloatingPointAttribute' ;
T31 : 'IntegerAttribute' ;
T32 : 'JavaObjectAttribute' ;
T33 : 'StringAttribute' ;
T34 : 'WordAttribute' ;
T35 : 'Lexicographical_Ascending' ;
T36 : 'Lexicographical_Descending' ;
T37 : 'Unordered' ;
T38 : 'ONE_TO_MANY' ;
T39 : 'MANY_TO_MANY' ;
T40 : 'MANY_TO_ONE' ;
T41 : 'import' ;
T42 : '.' ;
T43 : 'abstract' ;
T44 : 'artifactType' ;
T45 : '{' ;
T46 : '}' ;
T47 : 'extends' ;
T48 : 'attribute' ;
T49 : 'attributeType' ;
T50 : 'dataProvider' ;
T51 : 'min' ;
T52 : 'max' ;
T53 : 'taggerId' ;
T54 : 'enumType' ;
T55 : 'description' ;
T56 : 'defaultValue' ;
T57 : 'fileExtension' ;
T58 : 'oseeEnumType' ;
T59 : 'entry' ;
T60 : 'relationType' ;
T61 : 'sideAName' ;
T62 : 'sideAArtifactType' ;
T63 : 'sideBName' ;
T64 : 'sideBArtifactType' ;
T65 : 'defaultOrderType' ;
T66 : 'multiplicity' ;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 2697
RULE_ID : '^'? ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 2699
RULE_INT : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 2701
RULE_STRING : ('"' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'"')))* '"'|'\'' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'\'')))* '\'');

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 2703
RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 2705
RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 2707
RULE_WS : (' '|'\t'|'\r'|'\n')+;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 2709
RULE_ANY_OTHER : .;


