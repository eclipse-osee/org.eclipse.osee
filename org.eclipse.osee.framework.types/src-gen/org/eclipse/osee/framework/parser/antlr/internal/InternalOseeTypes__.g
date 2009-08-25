lexer grammar InternalOseeTypes;
@header {
package org.eclipse.osee.framework.parser.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.parser.antlr.Lexer;
}

T11 : 'import' ;
T12 : '.' ;
T13 : '0' ;
T14 : '1' ;
T15 : '2' ;
T16 : '3' ;
T17 : '4' ;
T18 : '5' ;
T19 : '6' ;
T20 : '7' ;
T21 : '8' ;
T22 : '9' ;
T23 : 'abstract' ;
T24 : 'artifactType' ;
T25 : 'extends' ;
T26 : '{' ;
T27 : '}' ;
T28 : 'attribute' ;
T29 : 'attributeType' ;
T30 : 'dataProvider' ;
T31 : 'DefaultAttributeDataProvider' ;
T32 : 'UriAttributeDataProvider' ;
T33 : 'MappedAttributeDataProvider' ;
T34 : 'min' ;
T35 : 'max' ;
T36 : 'unlimited' ;
T37 : 'taggerId' ;
T38 : 'DefaultAttributeTaggerProvider' ;
T39 : 'enumType' ;
T40 : 'description' ;
T41 : 'defaultValue' ;
T42 : 'fileExtension' ;
T43 : 'BooleanAttribute' ;
T44 : 'CompressedContentAttribute' ;
T45 : 'DateAttribute' ;
T46 : 'EnumeratedAttribute' ;
T47 : 'FloatingPointAttribute' ;
T48 : 'IntegerAttribute' ;
T49 : 'JavaObjectAttribute' ;
T50 : 'StringAttribute' ;
T51 : 'WordAttribute' ;
T52 : 'oseeEnumType' ;
T53 : 'entry' ;
T54 : 'relationType' ;
T55 : 'sideAName' ;
T56 : 'sideAArtifactType' ;
T57 : 'sideBName' ;
T58 : 'sideBArtifactType' ;
T59 : 'defaultOrderType' ;
T60 : 'Lexicographical_Ascending' ;
T61 : 'Lexicographical_Descending' ;
T62 : 'Unordered' ;
T63 : 'multiplicity' ;
T64 : 'ONE_TO_MANY' ;
T65 : 'MANY_TO_MANY' ;
T66 : 'MANY_TO_ONE' ;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1297
RULE_ID : '^'? ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1299
RULE_INT : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1301
RULE_STRING : ('"' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'"')))* '"'|'\'' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'\'')))* '\'');

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1303
RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1305
RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1307
RULE_WS : (' '|'\t'|'\r'|'\n')+;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1309
RULE_ANY_OTHER : .;


