lexer grammar InternalOseeTypes;
@header {
package org.eclipse.osee.framework.contentassist.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.ui.common.editor.contentassist.antlr.internal.Lexer;
}

T12 : 'DefaultAttributeDataProvider' ;
T13 : 'UriAttributeDataProvider' ;
T14 : 'MappedAttributeDataProvider' ;
T15 : 'unlimited' ;
T16 : 'DefaultAttributeTaggerProvider' ;
T17 : 'BooleanAttribute' ;
T18 : 'CompressedContentAttribute' ;
T19 : 'DateAttribute' ;
T20 : 'EnumeratedAttribute' ;
T21 : 'FloatingPointAttribute' ;
T22 : 'IntegerAttribute' ;
T23 : 'JavaObjectAttribute' ;
T24 : 'StringAttribute' ;
T25 : 'WordAttribute' ;
T26 : 'Lexicographical_Ascending' ;
T27 : 'Lexicographical_Descending' ;
T28 : 'Unordered' ;
T29 : 'ONE_TO_ONE' ;
T30 : 'ONE_TO_MANY' ;
T31 : 'MANY_TO_ONE' ;
T32 : 'MANY_TO_MANY' ;
T33 : 'import' ;
T34 : '.' ;
T35 : 'artifactType' ;
T36 : '{' ;
T37 : 'guid' ;
T38 : '}' ;
T39 : 'extends' ;
T40 : ',' ;
T41 : 'attribute' ;
T42 : 'branchGuid' ;
T43 : 'attributeType' ;
T44 : 'dataProvider' ;
T45 : 'min' ;
T46 : 'max' ;
T47 : 'overrides' ;
T48 : 'taggerId' ;
T49 : 'enumType' ;
T50 : 'description' ;
T51 : 'defaultValue' ;
T52 : 'fileExtension' ;
T53 : 'oseeEnumType' ;
T54 : 'entry' ;
T55 : 'overrides enum' ;
T56 : 'add' ;
T57 : 'remove' ;
T58 : 'relationType' ;
T59 : 'sideAName' ;
T60 : 'sideAArtifactType' ;
T61 : 'sideBName' ;
T62 : 'sideBArtifactType' ;
T63 : 'defaultOrderType' ;
T64 : 'multiplicity' ;
T65 : 'abstract' ;
T66 : 'inheritAll' ;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 3684
RULE_WHOLE_NUM_STR : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 3686
RULE_ID : '^'? ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 3688
RULE_INT : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 3690
RULE_STRING : ('"' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'"')))* '"'|'\'' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'\'')))* '\'');

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 3692
RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 3694
RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 3696
RULE_WS : (' '|'\t'|'\r'|'\n')+;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 3698
RULE_ANY_OTHER : .;


