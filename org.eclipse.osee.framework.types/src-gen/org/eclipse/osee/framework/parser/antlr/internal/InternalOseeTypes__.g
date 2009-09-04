lexer grammar InternalOseeTypes;
@header {
package org.eclipse.osee.framework.parser.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.parser.antlr.Lexer;
}

T12 : 'import' ;
T13 : '.' ;
T14 : 'abstract' ;
T15 : 'artifactType' ;
T16 : 'extends' ;
T17 : ',' ;
T18 : '{' ;
T19 : '}' ;
T20 : 'attribute' ;
T21 : 'branchGuid' ;
T22 : 'attributeType' ;
T23 : 'overrides' ;
T24 : 'dataProvider' ;
T25 : 'DefaultAttributeDataProvider' ;
T26 : 'UriAttributeDataProvider' ;
T27 : 'MappedAttributeDataProvider' ;
T28 : 'min' ;
T29 : 'max' ;
T30 : 'unlimited' ;
T31 : 'taggerId' ;
T32 : 'DefaultAttributeTaggerProvider' ;
T33 : 'enumType' ;
T34 : 'description' ;
T35 : 'defaultValue' ;
T36 : 'fileExtension' ;
T37 : 'BooleanAttribute' ;
T38 : 'CompressedContentAttribute' ;
T39 : 'DateAttribute' ;
T40 : 'EnumeratedAttribute' ;
T41 : 'FloatingPointAttribute' ;
T42 : 'IntegerAttribute' ;
T43 : 'JavaObjectAttribute' ;
T44 : 'StringAttribute' ;
T45 : 'WordAttribute' ;
T46 : 'oseeEnumType' ;
T47 : 'entry' ;
T48 : 'overrides enum' ;
T49 : 'inheritAll' ;
T50 : 'add' ;
T51 : 'remove' ;
T52 : 'relationType' ;
T53 : 'sideAName' ;
T54 : 'sideAArtifactType' ;
T55 : 'sideBName' ;
T56 : 'sideBArtifactType' ;
T57 : 'defaultOrderType' ;
T58 : 'multiplicity' ;
T59 : 'Lexicographical_Ascending' ;
T60 : 'Lexicographical_Descending' ;
T61 : 'Unordered' ;
T62 : 'ONE_TO_ONE' ;
T63 : 'ONE_TO_MANY' ;
T64 : 'MANY_TO_ONE' ;
T65 : 'MANY_TO_MANY' ;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1812
RULE_WHOLE_NUM_STR : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1814
RULE_ID : '^'? ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1816
RULE_INT : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1818
RULE_STRING : ('"' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'"')))* '"'|'\'' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'\'')))* '\'');

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1820
RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1822
RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1824
RULE_WS : (' '|'\t'|'\r'|'\n')+;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1826
RULE_ANY_OTHER : .;


