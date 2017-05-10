grammar ApplicabilityGrammar;

options {
ASTLabelType=CommonTree;
output=AST;
}


// START:members
@header {
	import java.util.HashMap;
	import java.util.Arrays;
	import java.util.Collections;
}

@members {

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
}

start                  :  applicability EOF! { operators.removeAll(Collections.singleton(null)); };

applicability           : config_applicability { applicabilityType="Config"; } 
								| feature_applicability { applicabilityType="Feature"; } ;
									
config_applicability    : 'CONFIGURATION[' expressions+ ']';

feature_applicability   : 'FEATURE[' expressions+ ']' ;

expressions     : (operator? expression) { operators.add($operator.text); };

expression		: ID { id = $ID.text.trim(); 
										 id_values_map.put(id, new ArrayList<String>());
										} 
								('=' temp=val)? { if($temp.text == null) {
																		id_values_map.put(id, Arrays.asList("Default"));
																	  }
																	};
																	
val			:  value	| start_compound ;
							
start_compound			: '(' { id_values_map.get(id).add("("); } 
							   compound_value 
								')' {	id_values_map.get(id).add(")"); };

compound_value			: value+ | multiple_compounds;
							
multiple_compounds   : start_compound 
							  operator { id_values_map.get(id).add($operator.text); } 
							  compound_value;

value				      : temp=operator? ID { 	
														if($temp.text != null)
															id_values_map.get(id).add($temp.text);
														id_values_map.get(id).add($ID.text.trim());
												 		};

operator					: AND | OR;
OR							: '|';
AND						: '&';
ID : ('a'..'z'|'A'..'Z')('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'-'|' ')* ;

WS : (' '|'\r'|'\t'|'\n')+ {$channel=HIDDEN;};



