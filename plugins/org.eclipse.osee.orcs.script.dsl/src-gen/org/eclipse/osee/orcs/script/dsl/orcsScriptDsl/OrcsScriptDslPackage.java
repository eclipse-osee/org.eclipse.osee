/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslFactory
 * @model kind="package"
 * @generated
 */
public interface OrcsScriptDslPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "orcsScriptDsl";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/osee/orcs/script/dsl/OrcsScriptDsl";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "orcsScriptDsl";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  OrcsScriptDslPackage eINSTANCE = org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptImpl <em>Orcs Script</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOrcsScript()
   * @generated
   */
  int ORCS_SCRIPT = 0;

  /**
   * The feature id for the '<em><b>Version</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORCS_SCRIPT__VERSION = 0;

  /**
   * The feature id for the '<em><b>Statements</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORCS_SCRIPT__STATEMENTS = 1;

  /**
   * The number of structural features of the '<em>Orcs Script</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORCS_SCRIPT_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.ScriptStatementImpl <em>Script Statement</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.ScriptStatementImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getScriptStatement()
   * @generated
   */
  int SCRIPT_STATEMENT = 1;

  /**
   * The number of structural features of the '<em>Script Statement</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SCRIPT_STATEMENT_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.ScriptVersionImpl <em>Script Version</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.ScriptVersionImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getScriptVersion()
   * @generated
   */
  int SCRIPT_VERSION = 2;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SCRIPT_VERSION__NAME = 0;

  /**
   * The feature id for the '<em><b>Version</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SCRIPT_VERSION__VERSION = 1;

  /**
   * The number of structural features of the '<em>Script Version</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SCRIPT_VERSION_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsExpressionImpl <em>Os Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsExpressionImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsExpression()
   * @generated
   */
  int OS_EXPRESSION = 3;

  /**
   * The number of structural features of the '<em>Os Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_EXPRESSION_FEATURE_COUNT = SCRIPT_STATEMENT_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectionLiteralImpl <em>Os Collection Literal</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectionLiteralImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsCollectionLiteral()
   * @generated
   */
  int OS_COLLECTION_LITERAL = 4;

  /**
   * The number of structural features of the '<em>Os Collection Literal</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_COLLECTION_LITERAL_FEATURE_COUNT = OS_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsListLiteralImpl <em>Os List Literal</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsListLiteralImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsListLiteral()
   * @generated
   */
  int OS_LIST_LITERAL = 5;

  /**
   * The feature id for the '<em><b>Elements</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_LIST_LITERAL__ELEMENTS = OS_COLLECTION_LITERAL_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os List Literal</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_LIST_LITERAL_FEATURE_COUNT = OS_COLLECTION_LITERAL_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsQueryStatementImpl <em>Os Query Statement</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsQueryStatementImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsQueryStatement()
   * @generated
   */
  int OS_QUERY_STATEMENT = 6;

  /**
   * The feature id for the '<em><b>Stmt</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_QUERY_STATEMENT__STMT = SCRIPT_STATEMENT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Query Statement</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_QUERY_STATEMENT_FEATURE_COUNT = SCRIPT_STATEMENT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsQueryImpl <em>Os Query</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsQueryImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsQuery()
   * @generated
   */
  int OS_QUERY = 7;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_QUERY__NAME = 0;

  /**
   * The feature id for the '<em><b>Collect</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_QUERY__COLLECT = 1;

  /**
   * The number of structural features of the '<em>Os Query</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_QUERY_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCritieriaImpl <em>Os Critieria</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCritieriaImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsCritieria()
   * @generated
   */
  int OS_CRITIERIA = 8;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_CRITIERIA__NAME = 0;

  /**
   * The number of structural features of the '<em>Os Critieria</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_CRITIERIA_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsClauseImpl <em>Os Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsClause()
   * @generated
   */
  int OS_CLAUSE = 9;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_CLAUSE__NAME = 0;

  /**
   * The number of structural features of the '<em>Os Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_CLAUSE_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectClauseImpl <em>Os Collect Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsCollectClause()
   * @generated
   */
  int OS_COLLECT_CLAUSE = 10;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_COLLECT_CLAUSE__NAME = 0;

  /**
   * The feature id for the '<em><b>Expression</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_COLLECT_CLAUSE__EXPRESSION = 1;

  /**
   * The feature id for the '<em><b>Limit</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_COLLECT_CLAUSE__LIMIT = 2;

  /**
   * The number of structural features of the '<em>Os Collect Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_COLLECT_CLAUSE_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsLimitClauseImpl <em>Os Limit Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsLimitClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsLimitClause()
   * @generated
   */
  int OS_LIMIT_CLAUSE = 11;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_LIMIT_CLAUSE__NAME = 0;

  /**
   * The feature id for the '<em><b>Limit</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_LIMIT_CLAUSE__LIMIT = 1;

  /**
   * The number of structural features of the '<em>Os Limit Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_LIMIT_CLAUSE_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectExpressionImpl <em>Os Collect Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectExpressionImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsCollectExpression()
   * @generated
   */
  int OS_COLLECT_EXPRESSION = 12;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_COLLECT_EXPRESSION__NAME = 0;

  /**
   * The number of structural features of the '<em>Os Collect Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_COLLECT_EXPRESSION_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsFindClauseImpl <em>Os Find Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsFindClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsFindClause()
   * @generated
   */
  int OS_FIND_CLAUSE = 13;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_FIND_CLAUSE__NAME = OS_CLAUSE__NAME;

  /**
   * The feature id for the '<em><b>Query</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_FIND_CLAUSE__QUERY = OS_CLAUSE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Find Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_FIND_CLAUSE_FEATURE_COUNT = OS_CLAUSE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsObjectQueryImpl <em>Os Object Query</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsObjectQueryImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsObjectQuery()
   * @generated
   */
  int OS_OBJECT_QUERY = 14;

  /**
   * The number of structural features of the '<em>Os Object Query</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_OBJECT_QUERY_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchQueryStatementImpl <em>Os Branch Query Statement</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchQueryStatementImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchQueryStatement()
   * @generated
   */
  int OS_BRANCH_QUERY_STATEMENT = 15;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_QUERY_STATEMENT__NAME = OS_QUERY__NAME;

  /**
   * The feature id for the '<em><b>Collect</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_QUERY_STATEMENT__COLLECT = OS_QUERY__COLLECT;

  /**
   * The feature id for the '<em><b>Data</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_QUERY_STATEMENT__DATA = OS_QUERY_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Branch Query Statement</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_QUERY_STATEMENT_FEATURE_COUNT = OS_QUERY_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchQueryImpl <em>Os Branch Query</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchQueryImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchQuery()
   * @generated
   */
  int OS_BRANCH_QUERY = 16;

  /**
   * The number of structural features of the '<em>Os Branch Query</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_QUERY_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchQueryByIdImpl <em>Os Branch Query By Id</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchQueryByIdImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchQueryById()
   * @generated
   */
  int OS_BRANCH_QUERY_BY_ID = 17;

  /**
   * The feature id for the '<em><b>Name</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_QUERY_BY_ID__NAME = OS_BRANCH_QUERY_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Branch Query By Id</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_QUERY_BY_ID_FEATURE_COUNT = OS_BRANCH_QUERY_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchQueryAllImpl <em>Os Branch Query All</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchQueryAllImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchQueryAll()
   * @generated
   */
  int OS_BRANCH_QUERY_ALL = 18;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_QUERY_ALL__NAME = OS_BRANCH_QUERY_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Branch Query All</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_QUERY_ALL_FEATURE_COUNT = OS_BRANCH_QUERY_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchQueryByPredicateImpl <em>Os Branch Query By Predicate</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchQueryByPredicateImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchQueryByPredicate()
   * @generated
   */
  int OS_BRANCH_QUERY_BY_PREDICATE = 19;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_QUERY_BY_PREDICATE__NAME = OS_BRANCH_QUERY_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Criteria</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_QUERY_BY_PREDICATE__CRITERIA = OS_BRANCH_QUERY_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Os Branch Query By Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_QUERY_BY_PREDICATE_FEATURE_COUNT = OS_BRANCH_QUERY_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchCriteriaImpl <em>Os Branch Criteria</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchCriteriaImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchCriteria()
   * @generated
   */
  int OS_BRANCH_CRITERIA = 20;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_CRITERIA__NAME = OS_CRITIERIA__NAME;

  /**
   * The number of structural features of the '<em>Os Branch Criteria</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_CRITERIA_FEATURE_COUNT = OS_CRITIERIA_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchNameCriteriaImpl <em>Os Branch Name Criteria</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchNameCriteriaImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchNameCriteria()
   * @generated
   */
  int OS_BRANCH_NAME_CRITERIA = 21;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_NAME_CRITERIA__NAME = OS_BRANCH_CRITERIA__NAME;

  /**
   * The feature id for the '<em><b>Clause</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_NAME_CRITERIA__CLAUSE = OS_BRANCH_CRITERIA_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Branch Name Criteria</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_NAME_CRITERIA_FEATURE_COUNT = OS_BRANCH_CRITERIA_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchNameClauseImpl <em>Os Branch Name Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchNameClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchNameClause()
   * @generated
   */
  int OS_BRANCH_NAME_CLAUSE = 22;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_NAME_CLAUSE__NAME = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_NAME_CLAUSE__VALUE = 1;

  /**
   * The number of structural features of the '<em>Os Branch Name Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_NAME_CLAUSE_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchNameEqualsClauseImpl <em>Os Branch Name Equals Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchNameEqualsClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchNameEqualsClause()
   * @generated
   */
  int OS_BRANCH_NAME_EQUALS_CLAUSE = 23;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_NAME_EQUALS_CLAUSE__NAME = OS_BRANCH_NAME_CLAUSE__NAME;

  /**
   * The feature id for the '<em><b>Value</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_NAME_EQUALS_CLAUSE__VALUE = OS_BRANCH_NAME_CLAUSE__VALUE;

  /**
   * The number of structural features of the '<em>Os Branch Name Equals Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_NAME_EQUALS_CLAUSE_FEATURE_COUNT = OS_BRANCH_NAME_CLAUSE_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchNamePatternClauseImpl <em>Os Branch Name Pattern Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchNamePatternClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchNamePatternClause()
   * @generated
   */
  int OS_BRANCH_NAME_PATTERN_CLAUSE = 24;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_NAME_PATTERN_CLAUSE__NAME = OS_BRANCH_NAME_CLAUSE__NAME;

  /**
   * The feature id for the '<em><b>Value</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_NAME_PATTERN_CLAUSE__VALUE = OS_BRANCH_NAME_CLAUSE__VALUE;

  /**
   * The number of structural features of the '<em>Os Branch Name Pattern Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_NAME_PATTERN_CLAUSE_FEATURE_COUNT = OS_BRANCH_NAME_CLAUSE_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchTypeCriteriaImpl <em>Os Branch Type Criteria</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchTypeCriteriaImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchTypeCriteria()
   * @generated
   */
  int OS_BRANCH_TYPE_CRITERIA = 25;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_TYPE_CRITERIA__NAME = OS_BRANCH_CRITERIA__NAME;

  /**
   * The feature id for the '<em><b>Types</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_TYPE_CRITERIA__TYPES = OS_BRANCH_CRITERIA_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Branch Type Criteria</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_TYPE_CRITERIA_FEATURE_COUNT = OS_BRANCH_CRITERIA_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchStateCriteriaImpl <em>Os Branch State Criteria</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchStateCriteriaImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchStateCriteria()
   * @generated
   */
  int OS_BRANCH_STATE_CRITERIA = 26;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_STATE_CRITERIA__NAME = OS_BRANCH_CRITERIA__NAME;

  /**
   * The feature id for the '<em><b>States</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_STATE_CRITERIA__STATES = OS_BRANCH_CRITERIA_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Branch State Criteria</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_STATE_CRITERIA_FEATURE_COUNT = OS_BRANCH_CRITERIA_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchArchivedCriteriaImpl <em>Os Branch Archived Criteria</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchArchivedCriteriaImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchArchivedCriteria()
   * @generated
   */
  int OS_BRANCH_ARCHIVED_CRITERIA = 27;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_ARCHIVED_CRITERIA__NAME = OS_BRANCH_CRITERIA__NAME;

  /**
   * The feature id for the '<em><b>Filter</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_ARCHIVED_CRITERIA__FILTER = OS_BRANCH_CRITERIA_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Branch Archived Criteria</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_ARCHIVED_CRITERIA_FEATURE_COUNT = OS_BRANCH_CRITERIA_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchIdCriteriaImpl <em>Os Branch Id Criteria</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchIdCriteriaImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchIdCriteria()
   * @generated
   */
  int OS_BRANCH_ID_CRITERIA = 28;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_ID_CRITERIA__NAME = OS_BRANCH_CRITERIA__NAME;

  /**
   * The feature id for the '<em><b>Ids</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_ID_CRITERIA__IDS = OS_BRANCH_CRITERIA_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Branch Id Criteria</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_ID_CRITERIA_FEATURE_COUNT = OS_BRANCH_CRITERIA_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchOfCriteriaImpl <em>Os Branch Of Criteria</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchOfCriteriaImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchOfCriteria()
   * @generated
   */
  int OS_BRANCH_OF_CRITERIA = 29;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_OF_CRITERIA__NAME = OS_BRANCH_CRITERIA__NAME;

  /**
   * The feature id for the '<em><b>Clause</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_OF_CRITERIA__CLAUSE = OS_BRANCH_CRITERIA_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Branch Of Criteria</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_OF_CRITERIA_FEATURE_COUNT = OS_BRANCH_CRITERIA_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchClauseImpl <em>Os Branch Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchClause()
   * @generated
   */
  int OS_BRANCH_CLAUSE = 30;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_CLAUSE__NAME = 0;

  /**
   * The feature id for the '<em><b>Id</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_CLAUSE__ID = 1;

  /**
   * The number of structural features of the '<em>Os Branch Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_CLAUSE_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchChildOfClauseImpl <em>Os Branch Child Of Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchChildOfClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchChildOfClause()
   * @generated
   */
  int OS_BRANCH_CHILD_OF_CLAUSE = 31;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_CHILD_OF_CLAUSE__NAME = OS_BRANCH_CLAUSE__NAME;

  /**
   * The feature id for the '<em><b>Id</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_CHILD_OF_CLAUSE__ID = OS_BRANCH_CLAUSE__ID;

  /**
   * The number of structural features of the '<em>Os Branch Child Of Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_CHILD_OF_CLAUSE_FEATURE_COUNT = OS_BRANCH_CLAUSE_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchParentOfClauseImpl <em>Os Branch Parent Of Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchParentOfClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchParentOfClause()
   * @generated
   */
  int OS_BRANCH_PARENT_OF_CLAUSE = 32;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_PARENT_OF_CLAUSE__NAME = OS_BRANCH_CLAUSE__NAME;

  /**
   * The feature id for the '<em><b>Id</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_PARENT_OF_CLAUSE__ID = OS_BRANCH_CLAUSE__ID;

  /**
   * The number of structural features of the '<em>Os Branch Parent Of Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BRANCH_PARENT_OF_CLAUSE_FEATURE_COUNT = OS_BRANCH_CLAUSE_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxQueryStatementImpl <em>Os Tx Query Statement</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxQueryStatementImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxQueryStatement()
   * @generated
   */
  int OS_TX_QUERY_STATEMENT = 33;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_QUERY_STATEMENT__NAME = OS_QUERY__NAME;

  /**
   * The feature id for the '<em><b>Collect</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_QUERY_STATEMENT__COLLECT = OS_QUERY__COLLECT;

  /**
   * The feature id for the '<em><b>Data</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_QUERY_STATEMENT__DATA = OS_QUERY_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Tx Query Statement</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_QUERY_STATEMENT_FEATURE_COUNT = OS_QUERY_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxQueryImpl <em>Os Tx Query</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxQueryImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxQuery()
   * @generated
   */
  int OS_TX_QUERY = 34;

  /**
   * The number of structural features of the '<em>Os Tx Query</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_QUERY_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxQueryByIdImpl <em>Os Tx Query By Id</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxQueryByIdImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxQueryById()
   * @generated
   */
  int OS_TX_QUERY_BY_ID = 35;

  /**
   * The feature id for the '<em><b>Name</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_QUERY_BY_ID__NAME = OS_TX_QUERY_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Tx Query By Id</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_QUERY_BY_ID_FEATURE_COUNT = OS_TX_QUERY_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxQueryAllImpl <em>Os Tx Query All</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxQueryAllImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxQueryAll()
   * @generated
   */
  int OS_TX_QUERY_ALL = 36;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_QUERY_ALL__NAME = OS_TX_QUERY_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Tx Query All</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_QUERY_ALL_FEATURE_COUNT = OS_TX_QUERY_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxQueryByPredicateImpl <em>Os Tx Query By Predicate</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxQueryByPredicateImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxQueryByPredicate()
   * @generated
   */
  int OS_TX_QUERY_BY_PREDICATE = 37;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_QUERY_BY_PREDICATE__NAME = OS_TX_QUERY_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Criteria</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_QUERY_BY_PREDICATE__CRITERIA = OS_TX_QUERY_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Os Tx Query By Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_QUERY_BY_PREDICATE_FEATURE_COUNT = OS_TX_QUERY_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCriteriaImpl <em>Os Tx Criteria</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCriteriaImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxCriteria()
   * @generated
   */
  int OS_TX_CRITERIA = 38;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_CRITERIA__NAME = OS_CRITIERIA__NAME;

  /**
   * The number of structural features of the '<em>Os Tx Criteria</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_CRITERIA_FEATURE_COUNT = OS_CRITIERIA_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxTypeCriteriaImpl <em>Os Tx Type Criteria</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxTypeCriteriaImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxTypeCriteria()
   * @generated
   */
  int OS_TX_TYPE_CRITERIA = 39;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_TYPE_CRITERIA__NAME = OS_TX_CRITERIA__NAME;

  /**
   * The feature id for the '<em><b>Types</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_TYPE_CRITERIA__TYPES = OS_TX_CRITERIA_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Tx Type Criteria</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_TYPE_CRITERIA_FEATURE_COUNT = OS_TX_CRITERIA_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommentCriteriaImpl <em>Os Tx Comment Criteria</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommentCriteriaImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxCommentCriteria()
   * @generated
   */
  int OS_TX_COMMENT_CRITERIA = 40;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_COMMENT_CRITERIA__NAME = OS_TX_CRITERIA__NAME;

  /**
   * The feature id for the '<em><b>Clause</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_COMMENT_CRITERIA__CLAUSE = OS_TX_CRITERIA_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Tx Comment Criteria</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_COMMENT_CRITERIA_FEATURE_COUNT = OS_TX_CRITERIA_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommentClauseImpl <em>Os Tx Comment Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommentClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxCommentClause()
   * @generated
   */
  int OS_TX_COMMENT_CLAUSE = 41;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_COMMENT_CLAUSE__NAME = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_COMMENT_CLAUSE__VALUE = 1;

  /**
   * The number of structural features of the '<em>Os Tx Comment Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_COMMENT_CLAUSE_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommentEqualsClauseImpl <em>Os Tx Comment Equals Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommentEqualsClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxCommentEqualsClause()
   * @generated
   */
  int OS_TX_COMMENT_EQUALS_CLAUSE = 42;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_COMMENT_EQUALS_CLAUSE__NAME = OS_TX_COMMENT_CLAUSE__NAME;

  /**
   * The feature id for the '<em><b>Value</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_COMMENT_EQUALS_CLAUSE__VALUE = OS_TX_COMMENT_CLAUSE__VALUE;

  /**
   * The number of structural features of the '<em>Os Tx Comment Equals Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_COMMENT_EQUALS_CLAUSE_FEATURE_COUNT = OS_TX_COMMENT_CLAUSE_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommentPatternClauseImpl <em>Os Tx Comment Pattern Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommentPatternClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxCommentPatternClause()
   * @generated
   */
  int OS_TX_COMMENT_PATTERN_CLAUSE = 43;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_COMMENT_PATTERN_CLAUSE__NAME = OS_TX_COMMENT_CLAUSE__NAME;

  /**
   * The feature id for the '<em><b>Value</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_COMMENT_PATTERN_CLAUSE__VALUE = OS_TX_COMMENT_CLAUSE__VALUE;

  /**
   * The number of structural features of the '<em>Os Tx Comment Pattern Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_COMMENT_PATTERN_CLAUSE_FEATURE_COUNT = OS_TX_COMMENT_CLAUSE_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxBranchIdCriteriaImpl <em>Os Tx Branch Id Criteria</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxBranchIdCriteriaImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxBranchIdCriteria()
   * @generated
   */
  int OS_TX_BRANCH_ID_CRITERIA = 44;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_BRANCH_ID_CRITERIA__NAME = OS_TX_CRITERIA__NAME;

  /**
   * The feature id for the '<em><b>Ids</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_BRANCH_ID_CRITERIA__IDS = OS_TX_CRITERIA_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Tx Branch Id Criteria</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_BRANCH_ID_CRITERIA_FEATURE_COUNT = OS_TX_CRITERIA_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxHeadOfBranchIdCriteriaImpl <em>Os Tx Head Of Branch Id Criteria</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxHeadOfBranchIdCriteriaImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxHeadOfBranchIdCriteria()
   * @generated
   */
  int OS_TX_HEAD_OF_BRANCH_ID_CRITERIA = 45;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_HEAD_OF_BRANCH_ID_CRITERIA__NAME = OS_TX_CRITERIA__NAME;

  /**
   * The feature id for the '<em><b>Id</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_HEAD_OF_BRANCH_ID_CRITERIA__ID = OS_TX_CRITERIA_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Tx Head Of Branch Id Criteria</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_HEAD_OF_BRANCH_ID_CRITERIA_FEATURE_COUNT = OS_TX_CRITERIA_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxAuthorIdCriteriaImpl <em>Os Tx Author Id Criteria</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxAuthorIdCriteriaImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxAuthorIdCriteria()
   * @generated
   */
  int OS_TX_AUTHOR_ID_CRITERIA = 46;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_AUTHOR_ID_CRITERIA__NAME = OS_TX_CRITERIA__NAME;

  /**
   * The feature id for the '<em><b>Ids</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_AUTHOR_ID_CRITERIA__IDS = OS_TX_CRITERIA_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Tx Author Id Criteria</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_AUTHOR_ID_CRITERIA_FEATURE_COUNT = OS_TX_CRITERIA_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommitIdCriteriaImpl <em>Os Tx Commit Id Criteria</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommitIdCriteriaImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxCommitIdCriteria()
   * @generated
   */
  int OS_TX_COMMIT_ID_CRITERIA = 47;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_COMMIT_ID_CRITERIA__NAME = OS_TX_CRITERIA__NAME;

  /**
   * The feature id for the '<em><b>Clause</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_COMMIT_ID_CRITERIA__CLAUSE = OS_TX_CRITERIA_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Tx Commit Id Criteria</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_COMMIT_ID_CRITERIA_FEATURE_COUNT = OS_TX_CRITERIA_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommitIdClauseImpl <em>Os Tx Commit Id Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommitIdClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxCommitIdClause()
   * @generated
   */
  int OS_TX_COMMIT_ID_CLAUSE = 48;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_COMMIT_ID_CLAUSE__NAME = 0;

  /**
   * The number of structural features of the '<em>Os Tx Commit Id Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_COMMIT_ID_CLAUSE_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommitIdEqualsClauseImpl <em>Os Tx Commit Id Equals Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommitIdEqualsClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxCommitIdEqualsClause()
   * @generated
   */
  int OS_TX_COMMIT_ID_EQUALS_CLAUSE = 49;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_COMMIT_ID_EQUALS_CLAUSE__NAME = OS_TX_COMMIT_ID_CLAUSE__NAME;

  /**
   * The feature id for the '<em><b>Ids</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_COMMIT_ID_EQUALS_CLAUSE__IDS = OS_TX_COMMIT_ID_CLAUSE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Tx Commit Id Equals Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_COMMIT_ID_EQUALS_CLAUSE_FEATURE_COUNT = OS_TX_COMMIT_ID_CLAUSE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommitIdIsNullClauseImpl <em>Os Tx Commit Id Is Null Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommitIdIsNullClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxCommitIdIsNullClause()
   * @generated
   */
  int OS_TX_COMMIT_ID_IS_NULL_CLAUSE = 50;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_COMMIT_ID_IS_NULL_CLAUSE__NAME = OS_TX_COMMIT_ID_CLAUSE__NAME;

  /**
   * The number of structural features of the '<em>Os Tx Commit Id Is Null Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_COMMIT_ID_IS_NULL_CLAUSE_FEATURE_COUNT = OS_TX_COMMIT_ID_CLAUSE_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdCriteriaImpl <em>Os Tx Id Criteria</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdCriteriaImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxIdCriteria()
   * @generated
   */
  int OS_TX_ID_CRITERIA = 51;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_ID_CRITERIA__NAME = OS_TX_CRITERIA__NAME;

  /**
   * The feature id for the '<em><b>Clause</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_ID_CRITERIA__CLAUSE = OS_TX_CRITERIA_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Tx Id Criteria</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_ID_CRITERIA_FEATURE_COUNT = OS_TX_CRITERIA_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdClauseImpl <em>Os Tx Id Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxIdClause()
   * @generated
   */
  int OS_TX_ID_CLAUSE = 52;

  /**
   * The number of structural features of the '<em>Os Tx Id Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_ID_CLAUSE_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdEqualsClauseImpl <em>Os Tx Id Equals Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdEqualsClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxIdEqualsClause()
   * @generated
   */
  int OS_TX_ID_EQUALS_CLAUSE = 53;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_ID_EQUALS_CLAUSE__NAME = OS_TX_ID_CLAUSE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Ids</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_ID_EQUALS_CLAUSE__IDS = OS_TX_ID_CLAUSE_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Os Tx Id Equals Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_ID_EQUALS_CLAUSE_FEATURE_COUNT = OS_TX_ID_CLAUSE_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdOpClauseImpl <em>Os Tx Id Op Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdOpClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxIdOpClause()
   * @generated
   */
  int OS_TX_ID_OP_CLAUSE = 54;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_ID_OP_CLAUSE__OP = OS_TX_ID_CLAUSE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Id</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_ID_OP_CLAUSE__ID = OS_TX_ID_CLAUSE_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Os Tx Id Op Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_ID_OP_CLAUSE_FEATURE_COUNT = OS_TX_ID_CLAUSE_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdRangeClauseImpl <em>Os Tx Id Range Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdRangeClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxIdRangeClause()
   * @generated
   */
  int OS_TX_ID_RANGE_CLAUSE = 55;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_ID_RANGE_CLAUSE__NAME = OS_TX_ID_CLAUSE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>From Id</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_ID_RANGE_CLAUSE__FROM_ID = OS_TX_ID_CLAUSE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>To Id</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_ID_RANGE_CLAUSE__TO_ID = OS_TX_ID_CLAUSE_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Os Tx Id Range Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_ID_RANGE_CLAUSE_FEATURE_COUNT = OS_TX_ID_CLAUSE_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxTimestampCriteriaImpl <em>Os Tx Timestamp Criteria</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxTimestampCriteriaImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxTimestampCriteria()
   * @generated
   */
  int OS_TX_TIMESTAMP_CRITERIA = 56;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_TIMESTAMP_CRITERIA__NAME = OS_TX_CRITERIA__NAME;

  /**
   * The feature id for the '<em><b>Clause</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_TIMESTAMP_CRITERIA__CLAUSE = OS_TX_CRITERIA_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Tx Timestamp Criteria</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_TIMESTAMP_CRITERIA_FEATURE_COUNT = OS_TX_CRITERIA_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxTimestampClauseImpl <em>Os Tx Timestamp Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxTimestampClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxTimestampClause()
   * @generated
   */
  int OS_TX_TIMESTAMP_CLAUSE = 57;

  /**
   * The number of structural features of the '<em>Os Tx Timestamp Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_TIMESTAMP_CLAUSE_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxTimestampOpClauseImpl <em>Os Tx Timestamp Op Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxTimestampOpClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxTimestampOpClause()
   * @generated
   */
  int OS_TX_TIMESTAMP_OP_CLAUSE = 58;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_TIMESTAMP_OP_CLAUSE__OP = OS_TX_TIMESTAMP_CLAUSE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Timestamp</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_TIMESTAMP_OP_CLAUSE__TIMESTAMP = OS_TX_TIMESTAMP_CLAUSE_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Os Tx Timestamp Op Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_TIMESTAMP_OP_CLAUSE_FEATURE_COUNT = OS_TX_TIMESTAMP_CLAUSE_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxTimestampRangeClauseImpl <em>Os Tx Timestamp Range Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxTimestampRangeClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxTimestampRangeClause()
   * @generated
   */
  int OS_TX_TIMESTAMP_RANGE_CLAUSE = 59;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_TIMESTAMP_RANGE_CLAUSE__NAME = OS_TX_TIMESTAMP_CLAUSE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>From</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_TIMESTAMP_RANGE_CLAUSE__FROM = OS_TX_TIMESTAMP_CLAUSE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>To</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_TIMESTAMP_RANGE_CLAUSE__TO = OS_TX_TIMESTAMP_CLAUSE_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Os Tx Timestamp Range Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TX_TIMESTAMP_RANGE_CLAUSE_FEATURE_COUNT = OS_TX_TIMESTAMP_CLAUSE_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactQueryStatementImpl <em>Os Artifact Query Statement</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactQueryStatementImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsArtifactQueryStatement()
   * @generated
   */
  int OS_ARTIFACT_QUERY_STATEMENT = 60;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_QUERY_STATEMENT__NAME = OS_OBJECT_QUERY_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Data</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_QUERY_STATEMENT__DATA = OS_OBJECT_QUERY_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Collect</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_QUERY_STATEMENT__COLLECT = OS_OBJECT_QUERY_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Os Artifact Query Statement</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_QUERY_STATEMENT_FEATURE_COUNT = OS_OBJECT_QUERY_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactQueryImpl <em>Os Artifact Query</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactQueryImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsArtifactQuery()
   * @generated
   */
  int OS_ARTIFACT_QUERY = 61;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_QUERY__NAME = 0;

  /**
   * The number of structural features of the '<em>Os Artifact Query</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_QUERY_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactQueryAllImpl <em>Os Artifact Query All</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactQueryAllImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsArtifactQueryAll()
   * @generated
   */
  int OS_ARTIFACT_QUERY_ALL = 62;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_QUERY_ALL__NAME = OS_ARTIFACT_QUERY__NAME;

  /**
   * The number of structural features of the '<em>Os Artifact Query All</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_QUERY_ALL_FEATURE_COUNT = OS_ARTIFACT_QUERY_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactQueryByPredicateImpl <em>Os Artifact Query By Predicate</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactQueryByPredicateImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsArtifactQueryByPredicate()
   * @generated
   */
  int OS_ARTIFACT_QUERY_BY_PREDICATE = 63;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_QUERY_BY_PREDICATE__NAME = OS_ARTIFACT_QUERY__NAME;

  /**
   * The feature id for the '<em><b>Criteria</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_QUERY_BY_PREDICATE__CRITERIA = OS_ARTIFACT_QUERY_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Artifact Query By Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_QUERY_BY_PREDICATE_FEATURE_COUNT = OS_ARTIFACT_QUERY_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsItemCriteriaImpl <em>Os Item Criteria</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsItemCriteriaImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsItemCriteria()
   * @generated
   */
  int OS_ITEM_CRITERIA = 64;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ITEM_CRITERIA__NAME = OS_CRITIERIA__NAME;

  /**
   * The number of structural features of the '<em>Os Item Criteria</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ITEM_CRITERIA_FEATURE_COUNT = OS_CRITIERIA_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactCriteriaImpl <em>Os Artifact Criteria</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactCriteriaImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsArtifactCriteria()
   * @generated
   */
  int OS_ARTIFACT_CRITERIA = 65;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_CRITERIA__NAME = OS_ITEM_CRITERIA__NAME;

  /**
   * The number of structural features of the '<em>Os Artifact Criteria</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_CRITERIA_FEATURE_COUNT = OS_ITEM_CRITERIA_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactIdCriteriaImpl <em>Os Artifact Id Criteria</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactIdCriteriaImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsArtifactIdCriteria()
   * @generated
   */
  int OS_ARTIFACT_ID_CRITERIA = 66;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_ID_CRITERIA__NAME = OS_ARTIFACT_CRITERIA__NAME;

  /**
   * The feature id for the '<em><b>Ids</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_ID_CRITERIA__IDS = OS_ARTIFACT_CRITERIA_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Artifact Id Criteria</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_ID_CRITERIA_FEATURE_COUNT = OS_ARTIFACT_CRITERIA_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactTypeCriteriaImpl <em>Os Artifact Type Criteria</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactTypeCriteriaImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsArtifactTypeCriteria()
   * @generated
   */
  int OS_ARTIFACT_TYPE_CRITERIA = 67;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_TYPE_CRITERIA__NAME = OS_ARTIFACT_CRITERIA__NAME;

  /**
   * The feature id for the '<em><b>Clause</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_TYPE_CRITERIA__CLAUSE = OS_ARTIFACT_CRITERIA_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Artifact Type Criteria</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_TYPE_CRITERIA_FEATURE_COUNT = OS_ARTIFACT_CRITERIA_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactTypeClauseImpl <em>Os Artifact Type Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactTypeClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsArtifactTypeClause()
   * @generated
   */
  int OS_ARTIFACT_TYPE_CLAUSE = 68;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_TYPE_CLAUSE__NAME = 0;

  /**
   * The feature id for the '<em><b>Types</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_TYPE_CLAUSE__TYPES = 1;

  /**
   * The number of structural features of the '<em>Os Artifact Type Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_TYPE_CLAUSE_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactTypeEqualsClauseImpl <em>Os Artifact Type Equals Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactTypeEqualsClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsArtifactTypeEqualsClause()
   * @generated
   */
  int OS_ARTIFACT_TYPE_EQUALS_CLAUSE = 69;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_TYPE_EQUALS_CLAUSE__NAME = OS_ARTIFACT_TYPE_CLAUSE__NAME;

  /**
   * The feature id for the '<em><b>Types</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_TYPE_EQUALS_CLAUSE__TYPES = OS_ARTIFACT_TYPE_CLAUSE__TYPES;

  /**
   * The number of structural features of the '<em>Os Artifact Type Equals Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_TYPE_EQUALS_CLAUSE_FEATURE_COUNT = OS_ARTIFACT_TYPE_CLAUSE_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactTypeInstanceOfClauseImpl <em>Os Artifact Type Instance Of Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactTypeInstanceOfClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsArtifactTypeInstanceOfClause()
   * @generated
   */
  int OS_ARTIFACT_TYPE_INSTANCE_OF_CLAUSE = 70;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_TYPE_INSTANCE_OF_CLAUSE__NAME = OS_ARTIFACT_TYPE_CLAUSE__NAME;

  /**
   * The feature id for the '<em><b>Types</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_TYPE_INSTANCE_OF_CLAUSE__TYPES = OS_ARTIFACT_TYPE_CLAUSE__TYPES;

  /**
   * The number of structural features of the '<em>Os Artifact Type Instance Of Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ARTIFACT_TYPE_INSTANCE_OF_CLAUSE_FEATURE_COUNT = OS_ARTIFACT_TYPE_CLAUSE_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsAttributeCriteriaImpl <em>Os Attribute Criteria</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsAttributeCriteriaImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsAttributeCriteria()
   * @generated
   */
  int OS_ATTRIBUTE_CRITERIA = 71;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ATTRIBUTE_CRITERIA__NAME = OS_ITEM_CRITERIA__NAME;

  /**
   * The feature id for the '<em><b>Clause</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ATTRIBUTE_CRITERIA__CLAUSE = OS_ITEM_CRITERIA_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Attribute Criteria</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ATTRIBUTE_CRITERIA_FEATURE_COUNT = OS_ITEM_CRITERIA_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsAttributeClauseImpl <em>Os Attribute Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsAttributeClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsAttributeClause()
   * @generated
   */
  int OS_ATTRIBUTE_CLAUSE = 72;

  /**
   * The feature id for the '<em><b>Types</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ATTRIBUTE_CLAUSE__TYPES = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ATTRIBUTE_CLAUSE__NAME = 1;

  /**
   * The number of structural features of the '<em>Os Attribute Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ATTRIBUTE_CLAUSE_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsAttributeExistClauseImpl <em>Os Attribute Exist Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsAttributeExistClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsAttributeExistClause()
   * @generated
   */
  int OS_ATTRIBUTE_EXIST_CLAUSE = 73;

  /**
   * The feature id for the '<em><b>Types</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ATTRIBUTE_EXIST_CLAUSE__TYPES = OS_ATTRIBUTE_CLAUSE__TYPES;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ATTRIBUTE_EXIST_CLAUSE__NAME = OS_ATTRIBUTE_CLAUSE__NAME;

  /**
   * The number of structural features of the '<em>Os Attribute Exist Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ATTRIBUTE_EXIST_CLAUSE_FEATURE_COUNT = OS_ATTRIBUTE_CLAUSE_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OseAttributeOpClauseImpl <em>Ose Attribute Op Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OseAttributeOpClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOseAttributeOpClause()
   * @generated
   */
  int OSE_ATTRIBUTE_OP_CLAUSE = 74;

  /**
   * The feature id for the '<em><b>Types</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSE_ATTRIBUTE_OP_CLAUSE__TYPES = OS_ATTRIBUTE_CLAUSE__TYPES;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSE_ATTRIBUTE_OP_CLAUSE__NAME = OS_ATTRIBUTE_CLAUSE__NAME;

  /**
   * The feature id for the '<em><b>Options</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSE_ATTRIBUTE_OP_CLAUSE__OPTIONS = OS_ATTRIBUTE_CLAUSE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Values</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSE_ATTRIBUTE_OP_CLAUSE__VALUES = OS_ATTRIBUTE_CLAUSE_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Ose Attribute Op Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSE_ATTRIBUTE_OP_CLAUSE_FEATURE_COUNT = OS_ATTRIBUTE_CLAUSE_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsRelationCriteriaImpl <em>Os Relation Criteria</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsRelationCriteriaImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsRelationCriteria()
   * @generated
   */
  int OS_RELATION_CRITERIA = 75;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_RELATION_CRITERIA__NAME = OS_ITEM_CRITERIA__NAME;

  /**
   * The feature id for the '<em><b>Clause</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_RELATION_CRITERIA__CLAUSE = OS_ITEM_CRITERIA_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Relation Criteria</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_RELATION_CRITERIA_FEATURE_COUNT = OS_ITEM_CRITERIA_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsRelationClauseImpl <em>Os Relation Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsRelationClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsRelationClause()
   * @generated
   */
  int OS_RELATION_CLAUSE = 76;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_RELATION_CLAUSE__NAME = 0;

  /**
   * The feature id for the '<em><b>Type</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_RELATION_CLAUSE__TYPE = 1;

  /**
   * The feature id for the '<em><b>Side</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_RELATION_CLAUSE__SIDE = 2;

  /**
   * The number of structural features of the '<em>Os Relation Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_RELATION_CLAUSE_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsRelationExistClauseImpl <em>Os Relation Exist Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsRelationExistClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsRelationExistClause()
   * @generated
   */
  int OS_RELATION_EXIST_CLAUSE = 77;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_RELATION_EXIST_CLAUSE__NAME = OS_RELATION_CLAUSE__NAME;

  /**
   * The feature id for the '<em><b>Type</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_RELATION_EXIST_CLAUSE__TYPE = OS_RELATION_CLAUSE__TYPE;

  /**
   * The feature id for the '<em><b>Side</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_RELATION_EXIST_CLAUSE__SIDE = OS_RELATION_CLAUSE__SIDE;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_RELATION_EXIST_CLAUSE__OP = OS_RELATION_CLAUSE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Relation Exist Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_RELATION_EXIST_CLAUSE_FEATURE_COUNT = OS_RELATION_CLAUSE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsRelatedToClauseImpl <em>Os Related To Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsRelatedToClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsRelatedToClause()
   * @generated
   */
  int OS_RELATED_TO_CLAUSE = 78;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_RELATED_TO_CLAUSE__NAME = OS_RELATION_CLAUSE__NAME;

  /**
   * The feature id for the '<em><b>Type</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_RELATED_TO_CLAUSE__TYPE = OS_RELATION_CLAUSE__TYPE;

  /**
   * The feature id for the '<em><b>Side</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_RELATED_TO_CLAUSE__SIDE = OS_RELATION_CLAUSE__SIDE;

  /**
   * The feature id for the '<em><b>Ids</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_RELATED_TO_CLAUSE__IDS = OS_RELATION_CLAUSE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Related To Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_RELATED_TO_CLAUSE_FEATURE_COUNT = OS_RELATION_CLAUSE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsFollowClauseImpl <em>Os Follow Clause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsFollowClauseImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsFollowClause()
   * @generated
   */
  int OS_FOLLOW_CLAUSE = 79;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_FOLLOW_CLAUSE__NAME = OS_CLAUSE__NAME;

  /**
   * The feature id for the '<em><b>Stmt</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_FOLLOW_CLAUSE__STMT = OS_CLAUSE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Follow Clause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_FOLLOW_CLAUSE_FEATURE_COUNT = OS_CLAUSE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsFollowStatementImpl <em>Os Follow Statement</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsFollowStatementImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsFollowStatement()
   * @generated
   */
  int OS_FOLLOW_STATEMENT = 80;

  /**
   * The number of structural features of the '<em>Os Follow Statement</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_FOLLOW_STATEMENT_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsFollowRelationTypeImpl <em>Os Follow Relation Type</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsFollowRelationTypeImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsFollowRelationType()
   * @generated
   */
  int OS_FOLLOW_RELATION_TYPE = 81;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_FOLLOW_RELATION_TYPE__NAME = OS_FOLLOW_STATEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Type</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_FOLLOW_RELATION_TYPE__TYPE = OS_FOLLOW_STATEMENT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Side</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_FOLLOW_RELATION_TYPE__SIDE = OS_FOLLOW_STATEMENT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Criteria</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_FOLLOW_RELATION_TYPE__CRITERIA = OS_FOLLOW_STATEMENT_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Collect</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_FOLLOW_RELATION_TYPE__COLLECT = OS_FOLLOW_STATEMENT_FEATURE_COUNT + 4;

  /**
   * The number of structural features of the '<em>Os Follow Relation Type</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_FOLLOW_RELATION_TYPE_FEATURE_COUNT = OS_FOLLOW_STATEMENT_FEATURE_COUNT + 5;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsVariableDeclarationImpl <em>Os Variable Declaration</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsVariableDeclarationImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsVariableDeclaration()
   * @generated
   */
  int OS_VARIABLE_DECLARATION = 82;

  /**
   * The feature id for the '<em><b>Elements</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_VARIABLE_DECLARATION__ELEMENTS = OS_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Variable Declaration</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_VARIABLE_DECLARATION_FEATURE_COUNT = OS_EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsVariableImpl <em>Os Variable</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsVariableImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsVariable()
   * @generated
   */
  int OS_VARIABLE = 83;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_VARIABLE__NAME = OS_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_VARIABLE__RIGHT = OS_EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Os Variable</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_VARIABLE_FEATURE_COUNT = OS_EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsAssignmentImpl <em>Os Assignment</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsAssignmentImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsAssignment()
   * @generated
   */
  int OS_ASSIGNMENT = 84;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ASSIGNMENT__RIGHT = OS_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Assignment</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_ASSIGNMENT_FEATURE_COUNT = OS_EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBooleanLiteralImpl <em>Os Boolean Literal</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBooleanLiteralImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBooleanLiteral()
   * @generated
   */
  int OS_BOOLEAN_LITERAL = 85;

  /**
   * The feature id for the '<em><b>Is True</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BOOLEAN_LITERAL__IS_TRUE = OS_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Boolean Literal</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_BOOLEAN_LITERAL_FEATURE_COUNT = OS_EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsNullLiteralImpl <em>Os Null Literal</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsNullLiteralImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsNullLiteral()
   * @generated
   */
  int OS_NULL_LITERAL = 86;

  /**
   * The number of structural features of the '<em>Os Null Literal</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_NULL_LITERAL_FEATURE_COUNT = OS_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsNumberLiteralImpl <em>Os Number Literal</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsNumberLiteralImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsNumberLiteral()
   * @generated
   */
  int OS_NUMBER_LITERAL = 87;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_NUMBER_LITERAL__VALUE = OS_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Number Literal</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_NUMBER_LITERAL_FEATURE_COUNT = OS_EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsStringLiteralImpl <em>Os String Literal</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsStringLiteralImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsStringLiteral()
   * @generated
   */
  int OS_STRING_LITERAL = 88;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_STRING_LITERAL__VALUE = OS_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os String Literal</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_STRING_LITERAL_FEATURE_COUNT = OS_EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTemplateLiteralImpl <em>Os Template Literal</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTemplateLiteralImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTemplateLiteral()
   * @generated
   */
  int OS_TEMPLATE_LITERAL = 89;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TEMPLATE_LITERAL__VALUE = OS_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Template Literal</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_TEMPLATE_LITERAL_FEATURE_COUNT = OS_EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsVariableReferenceImpl <em>Os Variable Reference</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsVariableReferenceImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsVariableReference()
   * @generated
   */
  int OS_VARIABLE_REFERENCE = 90;

  /**
   * The feature id for the '<em><b>Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_VARIABLE_REFERENCE__REF = OS_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Variable Reference</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_VARIABLE_REFERENCE_FEATURE_COUNT = OS_EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsDotExpressionImpl <em>Os Dot Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsDotExpressionImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsDotExpression()
   * @generated
   */
  int OS_DOT_EXPRESSION = 91;

  /**
   * The feature id for the '<em><b>Ref</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_DOT_EXPRESSION__REF = OS_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Tail</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_DOT_EXPRESSION__TAIL = OS_EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Os Dot Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_DOT_EXPRESSION_FEATURE_COUNT = OS_EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsQueryExpressionImpl <em>Os Query Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsQueryExpressionImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsQueryExpression()
   * @generated
   */
  int OS_QUERY_EXPRESSION = 92;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_QUERY_EXPRESSION__NAME = OS_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Query</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_QUERY_EXPRESSION__QUERY = OS_EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Clause</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_QUERY_EXPRESSION__CLAUSE = OS_EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Os Query Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_QUERY_EXPRESSION_FEATURE_COUNT = OS_EXPRESSION_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectObjectExpressionImpl <em>Os Collect Object Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectObjectExpressionImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsCollectObjectExpression()
   * @generated
   */
  int OS_COLLECT_OBJECT_EXPRESSION = 93;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_COLLECT_OBJECT_EXPRESSION__NAME = OS_COLLECT_EXPRESSION__NAME;

  /**
   * The feature id for the '<em><b>Alias</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_COLLECT_OBJECT_EXPRESSION__ALIAS = OS_COLLECT_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Expressions</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_COLLECT_OBJECT_EXPRESSION__EXPRESSIONS = OS_COLLECT_EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Os Collect Object Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_COLLECT_OBJECT_EXPRESSION_FEATURE_COUNT = OS_COLLECT_EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectAllFieldsExpressionImpl <em>Os Collect All Fields Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectAllFieldsExpressionImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsCollectAllFieldsExpression()
   * @generated
   */
  int OS_COLLECT_ALL_FIELDS_EXPRESSION = 94;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_COLLECT_ALL_FIELDS_EXPRESSION__NAME = OS_COLLECT_EXPRESSION__NAME;

  /**
   * The number of structural features of the '<em>Os Collect All Fields Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_COLLECT_ALL_FIELDS_EXPRESSION_FEATURE_COUNT = OS_COLLECT_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectFieldExpressionImpl <em>Os Collect Field Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectFieldExpressionImpl
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsCollectFieldExpression()
   * @generated
   */
  int OS_COLLECT_FIELD_EXPRESSION = 95;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_COLLECT_FIELD_EXPRESSION__NAME = OS_COLLECT_EXPRESSION__NAME;

  /**
   * The feature id for the '<em><b>Alias</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_COLLECT_FIELD_EXPRESSION__ALIAS = OS_COLLECT_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Os Collect Field Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OS_COLLECT_FIELD_EXPRESSION_FEATURE_COUNT = OS_COLLECT_EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchState <em>Os Branch State</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchState
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchState()
   * @generated
   */
  int OS_BRANCH_STATE = 96;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchType <em>Os Branch Type</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchType
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchType()
   * @generated
   */
  int OS_BRANCH_TYPE = 97;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchArchiveFilter <em>Os Branch Archive Filter</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchArchiveFilter
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchArchiveFilter()
   * @generated
   */
  int OS_BRANCH_ARCHIVE_FILTER = 98;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxType <em>Os Tx Type</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxType
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxType()
   * @generated
   */
  int OS_TX_TYPE = 99;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationSide <em>Os Relation Side</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationSide
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsRelationSide()
   * @generated
   */
  int OS_RELATION_SIDE = 100;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsOperator <em>Os Operator</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsOperator
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsOperator()
   * @generated
   */
  int OS_OPERATOR = 101;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNonEqualOperator <em>Os Non Equal Operator</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNonEqualOperator
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsNonEqualOperator()
   * @generated
   */
  int OS_NON_EQUAL_OPERATOR = 102;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExistenceOperator <em>Os Existence Operator</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExistenceOperator
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsExistenceOperator()
   * @generated
   */
  int OS_EXISTENCE_OPERATOR = 103;

  /**
   * The meta object id for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryOption <em>Os Query Option</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryOption
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsQueryOption()
   * @generated
   */
  int OS_QUERY_OPTION = 104;


  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScript <em>Orcs Script</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Orcs Script</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScript
   * @generated
   */
  EClass getOrcsScript();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScript#getVersion <em>Version</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Version</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScript#getVersion()
   * @see #getOrcsScript()
   * @generated
   */
  EReference getOrcsScript_Version();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScript#getStatements <em>Statements</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Statements</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScript#getStatements()
   * @see #getOrcsScript()
   * @generated
   */
  EReference getOrcsScript_Statements();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.ScriptStatement <em>Script Statement</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Script Statement</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.ScriptStatement
   * @generated
   */
  EClass getScriptStatement();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.ScriptVersion <em>Script Version</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Script Version</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.ScriptVersion
   * @generated
   */
  EClass getScriptVersion();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.ScriptVersion#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.ScriptVersion#getName()
   * @see #getScriptVersion()
   * @generated
   */
  EAttribute getScriptVersion_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.ScriptVersion#getVersion <em>Version</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Version</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.ScriptVersion#getVersion()
   * @see #getScriptVersion()
   * @generated
   */
  EAttribute getScriptVersion_Version();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExpression <em>Os Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Expression</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExpression
   * @generated
   */
  EClass getOsExpression();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectionLiteral <em>Os Collection Literal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Collection Literal</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectionLiteral
   * @generated
   */
  EClass getOsCollectionLiteral();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsListLiteral <em>Os List Literal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os List Literal</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsListLiteral
   * @generated
   */
  EClass getOsListLiteral();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsListLiteral#getElements <em>Elements</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Elements</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsListLiteral#getElements()
   * @see #getOsListLiteral()
   * @generated
   */
  EReference getOsListLiteral_Elements();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryStatement <em>Os Query Statement</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Query Statement</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryStatement
   * @generated
   */
  EClass getOsQueryStatement();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryStatement#getStmt <em>Stmt</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Stmt</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryStatement#getStmt()
   * @see #getOsQueryStatement()
   * @generated
   */
  EReference getOsQueryStatement_Stmt();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQuery <em>Os Query</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Query</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQuery
   * @generated
   */
  EClass getOsQuery();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQuery#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQuery#getName()
   * @see #getOsQuery()
   * @generated
   */
  EAttribute getOsQuery_Name();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQuery#getCollect <em>Collect</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Collect</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQuery#getCollect()
   * @see #getOsQuery()
   * @generated
   */
  EReference getOsQuery_Collect();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCritieria <em>Os Critieria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Critieria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCritieria
   * @generated
   */
  EClass getOsCritieria();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCritieria#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCritieria#getName()
   * @see #getOsCritieria()
   * @generated
   */
  EAttribute getOsCritieria_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsClause <em>Os Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsClause
   * @generated
   */
  EClass getOsClause();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsClause#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsClause#getName()
   * @see #getOsClause()
   * @generated
   */
  EAttribute getOsClause_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause <em>Os Collect Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Collect Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause
   * @generated
   */
  EClass getOsCollectClause();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause#getName()
   * @see #getOsCollectClause()
   * @generated
   */
  EAttribute getOsCollectClause_Name();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause#getExpression <em>Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Expression</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause#getExpression()
   * @see #getOsCollectClause()
   * @generated
   */
  EReference getOsCollectClause_Expression();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause#getLimit <em>Limit</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Limit</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause#getLimit()
   * @see #getOsCollectClause()
   * @generated
   */
  EReference getOsCollectClause_Limit();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsLimitClause <em>Os Limit Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Limit Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsLimitClause
   * @generated
   */
  EClass getOsLimitClause();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsLimitClause#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsLimitClause#getName()
   * @see #getOsLimitClause()
   * @generated
   */
  EAttribute getOsLimitClause_Name();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsLimitClause#getLimit <em>Limit</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Limit</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsLimitClause#getLimit()
   * @see #getOsLimitClause()
   * @generated
   */
  EReference getOsLimitClause_Limit();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectExpression <em>Os Collect Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Collect Expression</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectExpression
   * @generated
   */
  EClass getOsCollectExpression();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectExpression#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectExpression#getName()
   * @see #getOsCollectExpression()
   * @generated
   */
  EAttribute getOsCollectExpression_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFindClause <em>Os Find Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Find Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFindClause
   * @generated
   */
  EClass getOsFindClause();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFindClause#getQuery <em>Query</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Query</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFindClause#getQuery()
   * @see #getOsFindClause()
   * @generated
   */
  EReference getOsFindClause_Query();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsObjectQuery <em>Os Object Query</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Object Query</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsObjectQuery
   * @generated
   */
  EClass getOsObjectQuery();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryStatement <em>Os Branch Query Statement</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Branch Query Statement</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryStatement
   * @generated
   */
  EClass getOsBranchQueryStatement();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryStatement#getData <em>Data</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Data</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryStatement#getData()
   * @see #getOsBranchQueryStatement()
   * @generated
   */
  EReference getOsBranchQueryStatement_Data();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQuery <em>Os Branch Query</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Branch Query</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQuery
   * @generated
   */
  EClass getOsBranchQuery();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryById <em>Os Branch Query By Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Branch Query By Id</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryById
   * @generated
   */
  EClass getOsBranchQueryById();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryById#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryById#getName()
   * @see #getOsBranchQueryById()
   * @generated
   */
  EReference getOsBranchQueryById_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryAll <em>Os Branch Query All</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Branch Query All</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryAll
   * @generated
   */
  EClass getOsBranchQueryAll();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryAll#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryAll#getName()
   * @see #getOsBranchQueryAll()
   * @generated
   */
  EAttribute getOsBranchQueryAll_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryByPredicate <em>Os Branch Query By Predicate</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Branch Query By Predicate</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryByPredicate
   * @generated
   */
  EClass getOsBranchQueryByPredicate();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryByPredicate#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryByPredicate#getName()
   * @see #getOsBranchQueryByPredicate()
   * @generated
   */
  EAttribute getOsBranchQueryByPredicate_Name();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryByPredicate#getCriteria <em>Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryByPredicate#getCriteria()
   * @see #getOsBranchQueryByPredicate()
   * @generated
   */
  EReference getOsBranchQueryByPredicate_Criteria();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchCriteria <em>Os Branch Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Branch Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchCriteria
   * @generated
   */
  EClass getOsBranchCriteria();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNameCriteria <em>Os Branch Name Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Branch Name Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNameCriteria
   * @generated
   */
  EClass getOsBranchNameCriteria();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNameCriteria#getClause <em>Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNameCriteria#getClause()
   * @see #getOsBranchNameCriteria()
   * @generated
   */
  EReference getOsBranchNameCriteria_Clause();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNameClause <em>Os Branch Name Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Branch Name Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNameClause
   * @generated
   */
  EClass getOsBranchNameClause();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNameClause#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNameClause#getName()
   * @see #getOsBranchNameClause()
   * @generated
   */
  EAttribute getOsBranchNameClause_Name();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNameClause#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Value</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNameClause#getValue()
   * @see #getOsBranchNameClause()
   * @generated
   */
  EReference getOsBranchNameClause_Value();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNameEqualsClause <em>Os Branch Name Equals Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Branch Name Equals Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNameEqualsClause
   * @generated
   */
  EClass getOsBranchNameEqualsClause();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNamePatternClause <em>Os Branch Name Pattern Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Branch Name Pattern Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNamePatternClause
   * @generated
   */
  EClass getOsBranchNamePatternClause();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchTypeCriteria <em>Os Branch Type Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Branch Type Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchTypeCriteria
   * @generated
   */
  EClass getOsBranchTypeCriteria();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchTypeCriteria#getTypes <em>Types</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Types</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchTypeCriteria#getTypes()
   * @see #getOsBranchTypeCriteria()
   * @generated
   */
  EAttribute getOsBranchTypeCriteria_Types();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchStateCriteria <em>Os Branch State Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Branch State Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchStateCriteria
   * @generated
   */
  EClass getOsBranchStateCriteria();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchStateCriteria#getStates <em>States</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>States</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchStateCriteria#getStates()
   * @see #getOsBranchStateCriteria()
   * @generated
   */
  EAttribute getOsBranchStateCriteria_States();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchArchivedCriteria <em>Os Branch Archived Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Branch Archived Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchArchivedCriteria
   * @generated
   */
  EClass getOsBranchArchivedCriteria();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchArchivedCriteria#getFilter <em>Filter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Filter</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchArchivedCriteria#getFilter()
   * @see #getOsBranchArchivedCriteria()
   * @generated
   */
  EAttribute getOsBranchArchivedCriteria_Filter();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchIdCriteria <em>Os Branch Id Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Branch Id Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchIdCriteria
   * @generated
   */
  EClass getOsBranchIdCriteria();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchIdCriteria#getIds <em>Ids</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Ids</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchIdCriteria#getIds()
   * @see #getOsBranchIdCriteria()
   * @generated
   */
  EReference getOsBranchIdCriteria_Ids();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchOfCriteria <em>Os Branch Of Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Branch Of Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchOfCriteria
   * @generated
   */
  EClass getOsBranchOfCriteria();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchOfCriteria#getClause <em>Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchOfCriteria#getClause()
   * @see #getOsBranchOfCriteria()
   * @generated
   */
  EReference getOsBranchOfCriteria_Clause();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchClause <em>Os Branch Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Branch Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchClause
   * @generated
   */
  EClass getOsBranchClause();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchClause#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchClause#getName()
   * @see #getOsBranchClause()
   * @generated
   */
  EAttribute getOsBranchClause_Name();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchClause#getId <em>Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Id</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchClause#getId()
   * @see #getOsBranchClause()
   * @generated
   */
  EReference getOsBranchClause_Id();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchChildOfClause <em>Os Branch Child Of Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Branch Child Of Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchChildOfClause
   * @generated
   */
  EClass getOsBranchChildOfClause();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchParentOfClause <em>Os Branch Parent Of Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Branch Parent Of Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchParentOfClause
   * @generated
   */
  EClass getOsBranchParentOfClause();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryStatement <em>Os Tx Query Statement</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Query Statement</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryStatement
   * @generated
   */
  EClass getOsTxQueryStatement();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryStatement#getData <em>Data</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Data</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryStatement#getData()
   * @see #getOsTxQueryStatement()
   * @generated
   */
  EReference getOsTxQueryStatement_Data();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQuery <em>Os Tx Query</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Query</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQuery
   * @generated
   */
  EClass getOsTxQuery();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryById <em>Os Tx Query By Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Query By Id</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryById
   * @generated
   */
  EClass getOsTxQueryById();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryById#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryById#getName()
   * @see #getOsTxQueryById()
   * @generated
   */
  EReference getOsTxQueryById_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryAll <em>Os Tx Query All</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Query All</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryAll
   * @generated
   */
  EClass getOsTxQueryAll();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryAll#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryAll#getName()
   * @see #getOsTxQueryAll()
   * @generated
   */
  EAttribute getOsTxQueryAll_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryByPredicate <em>Os Tx Query By Predicate</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Query By Predicate</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryByPredicate
   * @generated
   */
  EClass getOsTxQueryByPredicate();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryByPredicate#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryByPredicate#getName()
   * @see #getOsTxQueryByPredicate()
   * @generated
   */
  EAttribute getOsTxQueryByPredicate_Name();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryByPredicate#getCriteria <em>Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryByPredicate#getCriteria()
   * @see #getOsTxQueryByPredicate()
   * @generated
   */
  EReference getOsTxQueryByPredicate_Criteria();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCriteria <em>Os Tx Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCriteria
   * @generated
   */
  EClass getOsTxCriteria();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTypeCriteria <em>Os Tx Type Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Type Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTypeCriteria
   * @generated
   */
  EClass getOsTxTypeCriteria();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTypeCriteria#getTypes <em>Types</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Types</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTypeCriteria#getTypes()
   * @see #getOsTxTypeCriteria()
   * @generated
   */
  EAttribute getOsTxTypeCriteria_Types();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentCriteria <em>Os Tx Comment Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Comment Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentCriteria
   * @generated
   */
  EClass getOsTxCommentCriteria();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentCriteria#getClause <em>Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentCriteria#getClause()
   * @see #getOsTxCommentCriteria()
   * @generated
   */
  EReference getOsTxCommentCriteria_Clause();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentClause <em>Os Tx Comment Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Comment Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentClause
   * @generated
   */
  EClass getOsTxCommentClause();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentClause#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentClause#getName()
   * @see #getOsTxCommentClause()
   * @generated
   */
  EAttribute getOsTxCommentClause_Name();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentClause#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Value</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentClause#getValue()
   * @see #getOsTxCommentClause()
   * @generated
   */
  EReference getOsTxCommentClause_Value();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentEqualsClause <em>Os Tx Comment Equals Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Comment Equals Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentEqualsClause
   * @generated
   */
  EClass getOsTxCommentEqualsClause();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentPatternClause <em>Os Tx Comment Pattern Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Comment Pattern Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentPatternClause
   * @generated
   */
  EClass getOsTxCommentPatternClause();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxBranchIdCriteria <em>Os Tx Branch Id Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Branch Id Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxBranchIdCriteria
   * @generated
   */
  EClass getOsTxBranchIdCriteria();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxBranchIdCriteria#getIds <em>Ids</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Ids</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxBranchIdCriteria#getIds()
   * @see #getOsTxBranchIdCriteria()
   * @generated
   */
  EReference getOsTxBranchIdCriteria_Ids();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxHeadOfBranchIdCriteria <em>Os Tx Head Of Branch Id Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Head Of Branch Id Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxHeadOfBranchIdCriteria
   * @generated
   */
  EClass getOsTxHeadOfBranchIdCriteria();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxHeadOfBranchIdCriteria#getId <em>Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Id</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxHeadOfBranchIdCriteria#getId()
   * @see #getOsTxHeadOfBranchIdCriteria()
   * @generated
   */
  EReference getOsTxHeadOfBranchIdCriteria_Id();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxAuthorIdCriteria <em>Os Tx Author Id Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Author Id Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxAuthorIdCriteria
   * @generated
   */
  EClass getOsTxAuthorIdCriteria();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxAuthorIdCriteria#getIds <em>Ids</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Ids</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxAuthorIdCriteria#getIds()
   * @see #getOsTxAuthorIdCriteria()
   * @generated
   */
  EReference getOsTxAuthorIdCriteria_Ids();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdCriteria <em>Os Tx Commit Id Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Commit Id Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdCriteria
   * @generated
   */
  EClass getOsTxCommitIdCriteria();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdCriteria#getClause <em>Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdCriteria#getClause()
   * @see #getOsTxCommitIdCriteria()
   * @generated
   */
  EReference getOsTxCommitIdCriteria_Clause();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdClause <em>Os Tx Commit Id Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Commit Id Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdClause
   * @generated
   */
  EClass getOsTxCommitIdClause();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdClause#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdClause#getName()
   * @see #getOsTxCommitIdClause()
   * @generated
   */
  EAttribute getOsTxCommitIdClause_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdEqualsClause <em>Os Tx Commit Id Equals Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Commit Id Equals Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdEqualsClause
   * @generated
   */
  EClass getOsTxCommitIdEqualsClause();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdEqualsClause#getIds <em>Ids</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Ids</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdEqualsClause#getIds()
   * @see #getOsTxCommitIdEqualsClause()
   * @generated
   */
  EReference getOsTxCommitIdEqualsClause_Ids();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdIsNullClause <em>Os Tx Commit Id Is Null Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Commit Id Is Null Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdIsNullClause
   * @generated
   */
  EClass getOsTxCommitIdIsNullClause();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdCriteria <em>Os Tx Id Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Id Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdCriteria
   * @generated
   */
  EClass getOsTxIdCriteria();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdCriteria#getClause <em>Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdCriteria#getClause()
   * @see #getOsTxIdCriteria()
   * @generated
   */
  EReference getOsTxIdCriteria_Clause();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdClause <em>Os Tx Id Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Id Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdClause
   * @generated
   */
  EClass getOsTxIdClause();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdEqualsClause <em>Os Tx Id Equals Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Id Equals Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdEqualsClause
   * @generated
   */
  EClass getOsTxIdEqualsClause();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdEqualsClause#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdEqualsClause#getName()
   * @see #getOsTxIdEqualsClause()
   * @generated
   */
  EAttribute getOsTxIdEqualsClause_Name();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdEqualsClause#getIds <em>Ids</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Ids</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdEqualsClause#getIds()
   * @see #getOsTxIdEqualsClause()
   * @generated
   */
  EReference getOsTxIdEqualsClause_Ids();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdOpClause <em>Os Tx Id Op Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Id Op Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdOpClause
   * @generated
   */
  EClass getOsTxIdOpClause();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdOpClause#getOp <em>Op</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Op</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdOpClause#getOp()
   * @see #getOsTxIdOpClause()
   * @generated
   */
  EAttribute getOsTxIdOpClause_Op();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdOpClause#getId <em>Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Id</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdOpClause#getId()
   * @see #getOsTxIdOpClause()
   * @generated
   */
  EReference getOsTxIdOpClause_Id();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdRangeClause <em>Os Tx Id Range Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Id Range Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdRangeClause
   * @generated
   */
  EClass getOsTxIdRangeClause();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdRangeClause#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdRangeClause#getName()
   * @see #getOsTxIdRangeClause()
   * @generated
   */
  EAttribute getOsTxIdRangeClause_Name();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdRangeClause#getFromId <em>From Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>From Id</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdRangeClause#getFromId()
   * @see #getOsTxIdRangeClause()
   * @generated
   */
  EReference getOsTxIdRangeClause_FromId();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdRangeClause#getToId <em>To Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>To Id</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdRangeClause#getToId()
   * @see #getOsTxIdRangeClause()
   * @generated
   */
  EReference getOsTxIdRangeClause_ToId();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampCriteria <em>Os Tx Timestamp Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Timestamp Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampCriteria
   * @generated
   */
  EClass getOsTxTimestampCriteria();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampCriteria#getClause <em>Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampCriteria#getClause()
   * @see #getOsTxTimestampCriteria()
   * @generated
   */
  EReference getOsTxTimestampCriteria_Clause();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampClause <em>Os Tx Timestamp Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Timestamp Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampClause
   * @generated
   */
  EClass getOsTxTimestampClause();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampOpClause <em>Os Tx Timestamp Op Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Timestamp Op Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampOpClause
   * @generated
   */
  EClass getOsTxTimestampOpClause();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampOpClause#getOp <em>Op</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Op</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampOpClause#getOp()
   * @see #getOsTxTimestampOpClause()
   * @generated
   */
  EAttribute getOsTxTimestampOpClause_Op();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampOpClause#getTimestamp <em>Timestamp</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Timestamp</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampOpClause#getTimestamp()
   * @see #getOsTxTimestampOpClause()
   * @generated
   */
  EReference getOsTxTimestampOpClause_Timestamp();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampRangeClause <em>Os Tx Timestamp Range Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Tx Timestamp Range Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampRangeClause
   * @generated
   */
  EClass getOsTxTimestampRangeClause();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampRangeClause#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampRangeClause#getName()
   * @see #getOsTxTimestampRangeClause()
   * @generated
   */
  EAttribute getOsTxTimestampRangeClause_Name();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampRangeClause#getFrom <em>From</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>From</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampRangeClause#getFrom()
   * @see #getOsTxTimestampRangeClause()
   * @generated
   */
  EReference getOsTxTimestampRangeClause_From();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampRangeClause#getTo <em>To</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>To</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampRangeClause#getTo()
   * @see #getOsTxTimestampRangeClause()
   * @generated
   */
  EReference getOsTxTimestampRangeClause_To();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryStatement <em>Os Artifact Query Statement</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Artifact Query Statement</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryStatement
   * @generated
   */
  EClass getOsArtifactQueryStatement();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryStatement#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryStatement#getName()
   * @see #getOsArtifactQueryStatement()
   * @generated
   */
  EAttribute getOsArtifactQueryStatement_Name();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryStatement#getData <em>Data</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Data</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryStatement#getData()
   * @see #getOsArtifactQueryStatement()
   * @generated
   */
  EReference getOsArtifactQueryStatement_Data();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryStatement#getCollect <em>Collect</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Collect</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryStatement#getCollect()
   * @see #getOsArtifactQueryStatement()
   * @generated
   */
  EReference getOsArtifactQueryStatement_Collect();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQuery <em>Os Artifact Query</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Artifact Query</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQuery
   * @generated
   */
  EClass getOsArtifactQuery();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQuery#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQuery#getName()
   * @see #getOsArtifactQuery()
   * @generated
   */
  EAttribute getOsArtifactQuery_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryAll <em>Os Artifact Query All</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Artifact Query All</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryAll
   * @generated
   */
  EClass getOsArtifactQueryAll();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryByPredicate <em>Os Artifact Query By Predicate</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Artifact Query By Predicate</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryByPredicate
   * @generated
   */
  EClass getOsArtifactQueryByPredicate();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryByPredicate#getCriteria <em>Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryByPredicate#getCriteria()
   * @see #getOsArtifactQueryByPredicate()
   * @generated
   */
  EReference getOsArtifactQueryByPredicate_Criteria();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsItemCriteria <em>Os Item Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Item Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsItemCriteria
   * @generated
   */
  EClass getOsItemCriteria();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactCriteria <em>Os Artifact Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Artifact Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactCriteria
   * @generated
   */
  EClass getOsArtifactCriteria();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactIdCriteria <em>Os Artifact Id Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Artifact Id Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactIdCriteria
   * @generated
   */
  EClass getOsArtifactIdCriteria();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactIdCriteria#getIds <em>Ids</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Ids</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactIdCriteria#getIds()
   * @see #getOsArtifactIdCriteria()
   * @generated
   */
  EReference getOsArtifactIdCriteria_Ids();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeCriteria <em>Os Artifact Type Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Artifact Type Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeCriteria
   * @generated
   */
  EClass getOsArtifactTypeCriteria();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeCriteria#getClause <em>Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeCriteria#getClause()
   * @see #getOsArtifactTypeCriteria()
   * @generated
   */
  EReference getOsArtifactTypeCriteria_Clause();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeClause <em>Os Artifact Type Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Artifact Type Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeClause
   * @generated
   */
  EClass getOsArtifactTypeClause();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeClause#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeClause#getName()
   * @see #getOsArtifactTypeClause()
   * @generated
   */
  EAttribute getOsArtifactTypeClause_Name();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeClause#getTypes <em>Types</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Types</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeClause#getTypes()
   * @see #getOsArtifactTypeClause()
   * @generated
   */
  EReference getOsArtifactTypeClause_Types();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeEqualsClause <em>Os Artifact Type Equals Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Artifact Type Equals Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeEqualsClause
   * @generated
   */
  EClass getOsArtifactTypeEqualsClause();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeInstanceOfClause <em>Os Artifact Type Instance Of Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Artifact Type Instance Of Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeInstanceOfClause
   * @generated
   */
  EClass getOsArtifactTypeInstanceOfClause();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAttributeCriteria <em>Os Attribute Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Attribute Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAttributeCriteria
   * @generated
   */
  EClass getOsAttributeCriteria();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAttributeCriteria#getClause <em>Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAttributeCriteria#getClause()
   * @see #getOsAttributeCriteria()
   * @generated
   */
  EReference getOsAttributeCriteria_Clause();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAttributeClause <em>Os Attribute Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Attribute Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAttributeClause
   * @generated
   */
  EClass getOsAttributeClause();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAttributeClause#getTypes <em>Types</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Types</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAttributeClause#getTypes()
   * @see #getOsAttributeClause()
   * @generated
   */
  EReference getOsAttributeClause_Types();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAttributeClause#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAttributeClause#getName()
   * @see #getOsAttributeClause()
   * @generated
   */
  EAttribute getOsAttributeClause_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAttributeExistClause <em>Os Attribute Exist Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Attribute Exist Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAttributeExistClause
   * @generated
   */
  EClass getOsAttributeExistClause();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OseAttributeOpClause <em>Ose Attribute Op Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Ose Attribute Op Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OseAttributeOpClause
   * @generated
   */
  EClass getOseAttributeOpClause();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OseAttributeOpClause#getOptions <em>Options</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Options</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OseAttributeOpClause#getOptions()
   * @see #getOseAttributeOpClause()
   * @generated
   */
  EAttribute getOseAttributeOpClause_Options();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OseAttributeOpClause#getValues <em>Values</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Values</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OseAttributeOpClause#getValues()
   * @see #getOseAttributeOpClause()
   * @generated
   */
  EReference getOseAttributeOpClause_Values();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationCriteria <em>Os Relation Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Relation Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationCriteria
   * @generated
   */
  EClass getOsRelationCriteria();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationCriteria#getClause <em>Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationCriteria#getClause()
   * @see #getOsRelationCriteria()
   * @generated
   */
  EReference getOsRelationCriteria_Clause();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationClause <em>Os Relation Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Relation Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationClause
   * @generated
   */
  EClass getOsRelationClause();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationClause#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationClause#getName()
   * @see #getOsRelationClause()
   * @generated
   */
  EAttribute getOsRelationClause_Name();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationClause#getType <em>Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Type</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationClause#getType()
   * @see #getOsRelationClause()
   * @generated
   */
  EReference getOsRelationClause_Type();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationClause#getSide <em>Side</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Side</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationClause#getSide()
   * @see #getOsRelationClause()
   * @generated
   */
  EAttribute getOsRelationClause_Side();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationExistClause <em>Os Relation Exist Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Relation Exist Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationExistClause
   * @generated
   */
  EClass getOsRelationExistClause();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationExistClause#getOp <em>Op</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Op</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationExistClause#getOp()
   * @see #getOsRelationExistClause()
   * @generated
   */
  EAttribute getOsRelationExistClause_Op();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelatedToClause <em>Os Related To Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Related To Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelatedToClause
   * @generated
   */
  EClass getOsRelatedToClause();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelatedToClause#getIds <em>Ids</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Ids</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelatedToClause#getIds()
   * @see #getOsRelatedToClause()
   * @generated
   */
  EReference getOsRelatedToClause_Ids();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowClause <em>Os Follow Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Follow Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowClause
   * @generated
   */
  EClass getOsFollowClause();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowClause#getStmt <em>Stmt</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Stmt</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowClause#getStmt()
   * @see #getOsFollowClause()
   * @generated
   */
  EReference getOsFollowClause_Stmt();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowStatement <em>Os Follow Statement</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Follow Statement</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowStatement
   * @generated
   */
  EClass getOsFollowStatement();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType <em>Os Follow Relation Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Follow Relation Type</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType
   * @generated
   */
  EClass getOsFollowRelationType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType#getName()
   * @see #getOsFollowRelationType()
   * @generated
   */
  EAttribute getOsFollowRelationType_Name();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType#getType <em>Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Type</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType#getType()
   * @see #getOsFollowRelationType()
   * @generated
   */
  EReference getOsFollowRelationType_Type();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType#getSide <em>Side</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Side</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType#getSide()
   * @see #getOsFollowRelationType()
   * @generated
   */
  EAttribute getOsFollowRelationType_Side();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType#getCriteria <em>Criteria</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Criteria</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType#getCriteria()
   * @see #getOsFollowRelationType()
   * @generated
   */
  EReference getOsFollowRelationType_Criteria();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType#getCollect <em>Collect</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Collect</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType#getCollect()
   * @see #getOsFollowRelationType()
   * @generated
   */
  EReference getOsFollowRelationType_Collect();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariableDeclaration <em>Os Variable Declaration</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Variable Declaration</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariableDeclaration
   * @generated
   */
  EClass getOsVariableDeclaration();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariableDeclaration#getElements <em>Elements</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Elements</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariableDeclaration#getElements()
   * @see #getOsVariableDeclaration()
   * @generated
   */
  EReference getOsVariableDeclaration_Elements();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariable <em>Os Variable</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Variable</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariable
   * @generated
   */
  EClass getOsVariable();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariable#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariable#getName()
   * @see #getOsVariable()
   * @generated
   */
  EAttribute getOsVariable_Name();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariable#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariable#getRight()
   * @see #getOsVariable()
   * @generated
   */
  EReference getOsVariable_Right();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAssignment <em>Os Assignment</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Assignment</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAssignment
   * @generated
   */
  EClass getOsAssignment();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAssignment#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAssignment#getRight()
   * @see #getOsAssignment()
   * @generated
   */
  EReference getOsAssignment_Right();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBooleanLiteral <em>Os Boolean Literal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Boolean Literal</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBooleanLiteral
   * @generated
   */
  EClass getOsBooleanLiteral();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBooleanLiteral#isIsTrue <em>Is True</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Is True</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBooleanLiteral#isIsTrue()
   * @see #getOsBooleanLiteral()
   * @generated
   */
  EAttribute getOsBooleanLiteral_IsTrue();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNullLiteral <em>Os Null Literal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Null Literal</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNullLiteral
   * @generated
   */
  EClass getOsNullLiteral();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNumberLiteral <em>Os Number Literal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Number Literal</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNumberLiteral
   * @generated
   */
  EClass getOsNumberLiteral();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNumberLiteral#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNumberLiteral#getValue()
   * @see #getOsNumberLiteral()
   * @generated
   */
  EAttribute getOsNumberLiteral_Value();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsStringLiteral <em>Os String Literal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os String Literal</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsStringLiteral
   * @generated
   */
  EClass getOsStringLiteral();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsStringLiteral#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsStringLiteral#getValue()
   * @see #getOsStringLiteral()
   * @generated
   */
  EAttribute getOsStringLiteral_Value();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTemplateLiteral <em>Os Template Literal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Template Literal</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTemplateLiteral
   * @generated
   */
  EClass getOsTemplateLiteral();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTemplateLiteral#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTemplateLiteral#getValue()
   * @see #getOsTemplateLiteral()
   * @generated
   */
  EAttribute getOsTemplateLiteral_Value();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariableReference <em>Os Variable Reference</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Variable Reference</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariableReference
   * @generated
   */
  EClass getOsVariableReference();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariableReference#getRef <em>Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Ref</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariableReference#getRef()
   * @see #getOsVariableReference()
   * @generated
   */
  EReference getOsVariableReference_Ref();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsDotExpression <em>Os Dot Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Dot Expression</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsDotExpression
   * @generated
   */
  EClass getOsDotExpression();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsDotExpression#getRef <em>Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Ref</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsDotExpression#getRef()
   * @see #getOsDotExpression()
   * @generated
   */
  EReference getOsDotExpression_Ref();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsDotExpression#getTail <em>Tail</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Tail</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsDotExpression#getTail()
   * @see #getOsDotExpression()
   * @generated
   */
  EReference getOsDotExpression_Tail();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryExpression <em>Os Query Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Query Expression</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryExpression
   * @generated
   */
  EClass getOsQueryExpression();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryExpression#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryExpression#getName()
   * @see #getOsQueryExpression()
   * @generated
   */
  EAttribute getOsQueryExpression_Name();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryExpression#getQuery <em>Query</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Query</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryExpression#getQuery()
   * @see #getOsQueryExpression()
   * @generated
   */
  EReference getOsQueryExpression_Query();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryExpression#getClause <em>Clause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Clause</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryExpression#getClause()
   * @see #getOsQueryExpression()
   * @generated
   */
  EReference getOsQueryExpression_Clause();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectObjectExpression <em>Os Collect Object Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Collect Object Expression</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectObjectExpression
   * @generated
   */
  EClass getOsCollectObjectExpression();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectObjectExpression#getAlias <em>Alias</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Alias</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectObjectExpression#getAlias()
   * @see #getOsCollectObjectExpression()
   * @generated
   */
  EReference getOsCollectObjectExpression_Alias();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectObjectExpression#getExpressions <em>Expressions</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Expressions</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectObjectExpression#getExpressions()
   * @see #getOsCollectObjectExpression()
   * @generated
   */
  EReference getOsCollectObjectExpression_Expressions();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectAllFieldsExpression <em>Os Collect All Fields Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Collect All Fields Expression</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectAllFieldsExpression
   * @generated
   */
  EClass getOsCollectAllFieldsExpression();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectFieldExpression <em>Os Collect Field Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Os Collect Field Expression</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectFieldExpression
   * @generated
   */
  EClass getOsCollectFieldExpression();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectFieldExpression#getAlias <em>Alias</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Alias</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectFieldExpression#getAlias()
   * @see #getOsCollectFieldExpression()
   * @generated
   */
  EReference getOsCollectFieldExpression_Alias();

  /**
   * Returns the meta object for enum '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchState <em>Os Branch State</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Os Branch State</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchState
   * @generated
   */
  EEnum getOsBranchState();

  /**
   * Returns the meta object for enum '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchType <em>Os Branch Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Os Branch Type</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchType
   * @generated
   */
  EEnum getOsBranchType();

  /**
   * Returns the meta object for enum '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchArchiveFilter <em>Os Branch Archive Filter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Os Branch Archive Filter</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchArchiveFilter
   * @generated
   */
  EEnum getOsBranchArchiveFilter();

  /**
   * Returns the meta object for enum '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxType <em>Os Tx Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Os Tx Type</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxType
   * @generated
   */
  EEnum getOsTxType();

  /**
   * Returns the meta object for enum '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationSide <em>Os Relation Side</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Os Relation Side</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationSide
   * @generated
   */
  EEnum getOsRelationSide();

  /**
   * Returns the meta object for enum '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsOperator <em>Os Operator</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Os Operator</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsOperator
   * @generated
   */
  EEnum getOsOperator();

  /**
   * Returns the meta object for enum '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNonEqualOperator <em>Os Non Equal Operator</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Os Non Equal Operator</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNonEqualOperator
   * @generated
   */
  EEnum getOsNonEqualOperator();

  /**
   * Returns the meta object for enum '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExistenceOperator <em>Os Existence Operator</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Os Existence Operator</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExistenceOperator
   * @generated
   */
  EEnum getOsExistenceOperator();

  /**
   * Returns the meta object for enum '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryOption <em>Os Query Option</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Os Query Option</em>'.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryOption
   * @generated
   */
  EEnum getOsQueryOption();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  OrcsScriptDslFactory getOrcsScriptDslFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptImpl <em>Orcs Script</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOrcsScript()
     * @generated
     */
    EClass ORCS_SCRIPT = eINSTANCE.getOrcsScript();

    /**
     * The meta object literal for the '<em><b>Version</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ORCS_SCRIPT__VERSION = eINSTANCE.getOrcsScript_Version();

    /**
     * The meta object literal for the '<em><b>Statements</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ORCS_SCRIPT__STATEMENTS = eINSTANCE.getOrcsScript_Statements();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.ScriptStatementImpl <em>Script Statement</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.ScriptStatementImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getScriptStatement()
     * @generated
     */
    EClass SCRIPT_STATEMENT = eINSTANCE.getScriptStatement();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.ScriptVersionImpl <em>Script Version</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.ScriptVersionImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getScriptVersion()
     * @generated
     */
    EClass SCRIPT_VERSION = eINSTANCE.getScriptVersion();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SCRIPT_VERSION__NAME = eINSTANCE.getScriptVersion_Name();

    /**
     * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SCRIPT_VERSION__VERSION = eINSTANCE.getScriptVersion_Version();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsExpressionImpl <em>Os Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsExpressionImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsExpression()
     * @generated
     */
    EClass OS_EXPRESSION = eINSTANCE.getOsExpression();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectionLiteralImpl <em>Os Collection Literal</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectionLiteralImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsCollectionLiteral()
     * @generated
     */
    EClass OS_COLLECTION_LITERAL = eINSTANCE.getOsCollectionLiteral();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsListLiteralImpl <em>Os List Literal</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsListLiteralImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsListLiteral()
     * @generated
     */
    EClass OS_LIST_LITERAL = eINSTANCE.getOsListLiteral();

    /**
     * The meta object literal for the '<em><b>Elements</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_LIST_LITERAL__ELEMENTS = eINSTANCE.getOsListLiteral_Elements();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsQueryStatementImpl <em>Os Query Statement</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsQueryStatementImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsQueryStatement()
     * @generated
     */
    EClass OS_QUERY_STATEMENT = eINSTANCE.getOsQueryStatement();

    /**
     * The meta object literal for the '<em><b>Stmt</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_QUERY_STATEMENT__STMT = eINSTANCE.getOsQueryStatement_Stmt();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsQueryImpl <em>Os Query</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsQueryImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsQuery()
     * @generated
     */
    EClass OS_QUERY = eINSTANCE.getOsQuery();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_QUERY__NAME = eINSTANCE.getOsQuery_Name();

    /**
     * The meta object literal for the '<em><b>Collect</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_QUERY__COLLECT = eINSTANCE.getOsQuery_Collect();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCritieriaImpl <em>Os Critieria</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCritieriaImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsCritieria()
     * @generated
     */
    EClass OS_CRITIERIA = eINSTANCE.getOsCritieria();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_CRITIERIA__NAME = eINSTANCE.getOsCritieria_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsClauseImpl <em>Os Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsClause()
     * @generated
     */
    EClass OS_CLAUSE = eINSTANCE.getOsClause();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_CLAUSE__NAME = eINSTANCE.getOsClause_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectClauseImpl <em>Os Collect Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsCollectClause()
     * @generated
     */
    EClass OS_COLLECT_CLAUSE = eINSTANCE.getOsCollectClause();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_COLLECT_CLAUSE__NAME = eINSTANCE.getOsCollectClause_Name();

    /**
     * The meta object literal for the '<em><b>Expression</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_COLLECT_CLAUSE__EXPRESSION = eINSTANCE.getOsCollectClause_Expression();

    /**
     * The meta object literal for the '<em><b>Limit</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_COLLECT_CLAUSE__LIMIT = eINSTANCE.getOsCollectClause_Limit();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsLimitClauseImpl <em>Os Limit Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsLimitClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsLimitClause()
     * @generated
     */
    EClass OS_LIMIT_CLAUSE = eINSTANCE.getOsLimitClause();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_LIMIT_CLAUSE__NAME = eINSTANCE.getOsLimitClause_Name();

    /**
     * The meta object literal for the '<em><b>Limit</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_LIMIT_CLAUSE__LIMIT = eINSTANCE.getOsLimitClause_Limit();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectExpressionImpl <em>Os Collect Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectExpressionImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsCollectExpression()
     * @generated
     */
    EClass OS_COLLECT_EXPRESSION = eINSTANCE.getOsCollectExpression();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_COLLECT_EXPRESSION__NAME = eINSTANCE.getOsCollectExpression_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsFindClauseImpl <em>Os Find Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsFindClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsFindClause()
     * @generated
     */
    EClass OS_FIND_CLAUSE = eINSTANCE.getOsFindClause();

    /**
     * The meta object literal for the '<em><b>Query</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_FIND_CLAUSE__QUERY = eINSTANCE.getOsFindClause_Query();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsObjectQueryImpl <em>Os Object Query</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsObjectQueryImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsObjectQuery()
     * @generated
     */
    EClass OS_OBJECT_QUERY = eINSTANCE.getOsObjectQuery();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchQueryStatementImpl <em>Os Branch Query Statement</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchQueryStatementImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchQueryStatement()
     * @generated
     */
    EClass OS_BRANCH_QUERY_STATEMENT = eINSTANCE.getOsBranchQueryStatement();

    /**
     * The meta object literal for the '<em><b>Data</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_BRANCH_QUERY_STATEMENT__DATA = eINSTANCE.getOsBranchQueryStatement_Data();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchQueryImpl <em>Os Branch Query</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchQueryImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchQuery()
     * @generated
     */
    EClass OS_BRANCH_QUERY = eINSTANCE.getOsBranchQuery();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchQueryByIdImpl <em>Os Branch Query By Id</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchQueryByIdImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchQueryById()
     * @generated
     */
    EClass OS_BRANCH_QUERY_BY_ID = eINSTANCE.getOsBranchQueryById();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_BRANCH_QUERY_BY_ID__NAME = eINSTANCE.getOsBranchQueryById_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchQueryAllImpl <em>Os Branch Query All</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchQueryAllImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchQueryAll()
     * @generated
     */
    EClass OS_BRANCH_QUERY_ALL = eINSTANCE.getOsBranchQueryAll();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_BRANCH_QUERY_ALL__NAME = eINSTANCE.getOsBranchQueryAll_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchQueryByPredicateImpl <em>Os Branch Query By Predicate</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchQueryByPredicateImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchQueryByPredicate()
     * @generated
     */
    EClass OS_BRANCH_QUERY_BY_PREDICATE = eINSTANCE.getOsBranchQueryByPredicate();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_BRANCH_QUERY_BY_PREDICATE__NAME = eINSTANCE.getOsBranchQueryByPredicate_Name();

    /**
     * The meta object literal for the '<em><b>Criteria</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_BRANCH_QUERY_BY_PREDICATE__CRITERIA = eINSTANCE.getOsBranchQueryByPredicate_Criteria();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchCriteriaImpl <em>Os Branch Criteria</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchCriteriaImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchCriteria()
     * @generated
     */
    EClass OS_BRANCH_CRITERIA = eINSTANCE.getOsBranchCriteria();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchNameCriteriaImpl <em>Os Branch Name Criteria</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchNameCriteriaImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchNameCriteria()
     * @generated
     */
    EClass OS_BRANCH_NAME_CRITERIA = eINSTANCE.getOsBranchNameCriteria();

    /**
     * The meta object literal for the '<em><b>Clause</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_BRANCH_NAME_CRITERIA__CLAUSE = eINSTANCE.getOsBranchNameCriteria_Clause();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchNameClauseImpl <em>Os Branch Name Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchNameClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchNameClause()
     * @generated
     */
    EClass OS_BRANCH_NAME_CLAUSE = eINSTANCE.getOsBranchNameClause();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_BRANCH_NAME_CLAUSE__NAME = eINSTANCE.getOsBranchNameClause_Name();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_BRANCH_NAME_CLAUSE__VALUE = eINSTANCE.getOsBranchNameClause_Value();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchNameEqualsClauseImpl <em>Os Branch Name Equals Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchNameEqualsClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchNameEqualsClause()
     * @generated
     */
    EClass OS_BRANCH_NAME_EQUALS_CLAUSE = eINSTANCE.getOsBranchNameEqualsClause();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchNamePatternClauseImpl <em>Os Branch Name Pattern Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchNamePatternClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchNamePatternClause()
     * @generated
     */
    EClass OS_BRANCH_NAME_PATTERN_CLAUSE = eINSTANCE.getOsBranchNamePatternClause();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchTypeCriteriaImpl <em>Os Branch Type Criteria</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchTypeCriteriaImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchTypeCriteria()
     * @generated
     */
    EClass OS_BRANCH_TYPE_CRITERIA = eINSTANCE.getOsBranchTypeCriteria();

    /**
     * The meta object literal for the '<em><b>Types</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_BRANCH_TYPE_CRITERIA__TYPES = eINSTANCE.getOsBranchTypeCriteria_Types();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchStateCriteriaImpl <em>Os Branch State Criteria</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchStateCriteriaImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchStateCriteria()
     * @generated
     */
    EClass OS_BRANCH_STATE_CRITERIA = eINSTANCE.getOsBranchStateCriteria();

    /**
     * The meta object literal for the '<em><b>States</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_BRANCH_STATE_CRITERIA__STATES = eINSTANCE.getOsBranchStateCriteria_States();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchArchivedCriteriaImpl <em>Os Branch Archived Criteria</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchArchivedCriteriaImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchArchivedCriteria()
     * @generated
     */
    EClass OS_BRANCH_ARCHIVED_CRITERIA = eINSTANCE.getOsBranchArchivedCriteria();

    /**
     * The meta object literal for the '<em><b>Filter</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_BRANCH_ARCHIVED_CRITERIA__FILTER = eINSTANCE.getOsBranchArchivedCriteria_Filter();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchIdCriteriaImpl <em>Os Branch Id Criteria</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchIdCriteriaImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchIdCriteria()
     * @generated
     */
    EClass OS_BRANCH_ID_CRITERIA = eINSTANCE.getOsBranchIdCriteria();

    /**
     * The meta object literal for the '<em><b>Ids</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_BRANCH_ID_CRITERIA__IDS = eINSTANCE.getOsBranchIdCriteria_Ids();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchOfCriteriaImpl <em>Os Branch Of Criteria</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchOfCriteriaImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchOfCriteria()
     * @generated
     */
    EClass OS_BRANCH_OF_CRITERIA = eINSTANCE.getOsBranchOfCriteria();

    /**
     * The meta object literal for the '<em><b>Clause</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_BRANCH_OF_CRITERIA__CLAUSE = eINSTANCE.getOsBranchOfCriteria_Clause();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchClauseImpl <em>Os Branch Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchClause()
     * @generated
     */
    EClass OS_BRANCH_CLAUSE = eINSTANCE.getOsBranchClause();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_BRANCH_CLAUSE__NAME = eINSTANCE.getOsBranchClause_Name();

    /**
     * The meta object literal for the '<em><b>Id</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_BRANCH_CLAUSE__ID = eINSTANCE.getOsBranchClause_Id();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchChildOfClauseImpl <em>Os Branch Child Of Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchChildOfClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchChildOfClause()
     * @generated
     */
    EClass OS_BRANCH_CHILD_OF_CLAUSE = eINSTANCE.getOsBranchChildOfClause();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchParentOfClauseImpl <em>Os Branch Parent Of Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBranchParentOfClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchParentOfClause()
     * @generated
     */
    EClass OS_BRANCH_PARENT_OF_CLAUSE = eINSTANCE.getOsBranchParentOfClause();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxQueryStatementImpl <em>Os Tx Query Statement</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxQueryStatementImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxQueryStatement()
     * @generated
     */
    EClass OS_TX_QUERY_STATEMENT = eINSTANCE.getOsTxQueryStatement();

    /**
     * The meta object literal for the '<em><b>Data</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_TX_QUERY_STATEMENT__DATA = eINSTANCE.getOsTxQueryStatement_Data();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxQueryImpl <em>Os Tx Query</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxQueryImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxQuery()
     * @generated
     */
    EClass OS_TX_QUERY = eINSTANCE.getOsTxQuery();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxQueryByIdImpl <em>Os Tx Query By Id</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxQueryByIdImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxQueryById()
     * @generated
     */
    EClass OS_TX_QUERY_BY_ID = eINSTANCE.getOsTxQueryById();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_TX_QUERY_BY_ID__NAME = eINSTANCE.getOsTxQueryById_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxQueryAllImpl <em>Os Tx Query All</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxQueryAllImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxQueryAll()
     * @generated
     */
    EClass OS_TX_QUERY_ALL = eINSTANCE.getOsTxQueryAll();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_TX_QUERY_ALL__NAME = eINSTANCE.getOsTxQueryAll_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxQueryByPredicateImpl <em>Os Tx Query By Predicate</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxQueryByPredicateImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxQueryByPredicate()
     * @generated
     */
    EClass OS_TX_QUERY_BY_PREDICATE = eINSTANCE.getOsTxQueryByPredicate();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_TX_QUERY_BY_PREDICATE__NAME = eINSTANCE.getOsTxQueryByPredicate_Name();

    /**
     * The meta object literal for the '<em><b>Criteria</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_TX_QUERY_BY_PREDICATE__CRITERIA = eINSTANCE.getOsTxQueryByPredicate_Criteria();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCriteriaImpl <em>Os Tx Criteria</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCriteriaImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxCriteria()
     * @generated
     */
    EClass OS_TX_CRITERIA = eINSTANCE.getOsTxCriteria();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxTypeCriteriaImpl <em>Os Tx Type Criteria</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxTypeCriteriaImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxTypeCriteria()
     * @generated
     */
    EClass OS_TX_TYPE_CRITERIA = eINSTANCE.getOsTxTypeCriteria();

    /**
     * The meta object literal for the '<em><b>Types</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_TX_TYPE_CRITERIA__TYPES = eINSTANCE.getOsTxTypeCriteria_Types();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommentCriteriaImpl <em>Os Tx Comment Criteria</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommentCriteriaImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxCommentCriteria()
     * @generated
     */
    EClass OS_TX_COMMENT_CRITERIA = eINSTANCE.getOsTxCommentCriteria();

    /**
     * The meta object literal for the '<em><b>Clause</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_TX_COMMENT_CRITERIA__CLAUSE = eINSTANCE.getOsTxCommentCriteria_Clause();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommentClauseImpl <em>Os Tx Comment Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommentClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxCommentClause()
     * @generated
     */
    EClass OS_TX_COMMENT_CLAUSE = eINSTANCE.getOsTxCommentClause();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_TX_COMMENT_CLAUSE__NAME = eINSTANCE.getOsTxCommentClause_Name();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_TX_COMMENT_CLAUSE__VALUE = eINSTANCE.getOsTxCommentClause_Value();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommentEqualsClauseImpl <em>Os Tx Comment Equals Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommentEqualsClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxCommentEqualsClause()
     * @generated
     */
    EClass OS_TX_COMMENT_EQUALS_CLAUSE = eINSTANCE.getOsTxCommentEqualsClause();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommentPatternClauseImpl <em>Os Tx Comment Pattern Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommentPatternClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxCommentPatternClause()
     * @generated
     */
    EClass OS_TX_COMMENT_PATTERN_CLAUSE = eINSTANCE.getOsTxCommentPatternClause();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxBranchIdCriteriaImpl <em>Os Tx Branch Id Criteria</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxBranchIdCriteriaImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxBranchIdCriteria()
     * @generated
     */
    EClass OS_TX_BRANCH_ID_CRITERIA = eINSTANCE.getOsTxBranchIdCriteria();

    /**
     * The meta object literal for the '<em><b>Ids</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_TX_BRANCH_ID_CRITERIA__IDS = eINSTANCE.getOsTxBranchIdCriteria_Ids();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxHeadOfBranchIdCriteriaImpl <em>Os Tx Head Of Branch Id Criteria</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxHeadOfBranchIdCriteriaImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxHeadOfBranchIdCriteria()
     * @generated
     */
    EClass OS_TX_HEAD_OF_BRANCH_ID_CRITERIA = eINSTANCE.getOsTxHeadOfBranchIdCriteria();

    /**
     * The meta object literal for the '<em><b>Id</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_TX_HEAD_OF_BRANCH_ID_CRITERIA__ID = eINSTANCE.getOsTxHeadOfBranchIdCriteria_Id();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxAuthorIdCriteriaImpl <em>Os Tx Author Id Criteria</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxAuthorIdCriteriaImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxAuthorIdCriteria()
     * @generated
     */
    EClass OS_TX_AUTHOR_ID_CRITERIA = eINSTANCE.getOsTxAuthorIdCriteria();

    /**
     * The meta object literal for the '<em><b>Ids</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_TX_AUTHOR_ID_CRITERIA__IDS = eINSTANCE.getOsTxAuthorIdCriteria_Ids();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommitIdCriteriaImpl <em>Os Tx Commit Id Criteria</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommitIdCriteriaImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxCommitIdCriteria()
     * @generated
     */
    EClass OS_TX_COMMIT_ID_CRITERIA = eINSTANCE.getOsTxCommitIdCriteria();

    /**
     * The meta object literal for the '<em><b>Clause</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_TX_COMMIT_ID_CRITERIA__CLAUSE = eINSTANCE.getOsTxCommitIdCriteria_Clause();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommitIdClauseImpl <em>Os Tx Commit Id Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommitIdClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxCommitIdClause()
     * @generated
     */
    EClass OS_TX_COMMIT_ID_CLAUSE = eINSTANCE.getOsTxCommitIdClause();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_TX_COMMIT_ID_CLAUSE__NAME = eINSTANCE.getOsTxCommitIdClause_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommitIdEqualsClauseImpl <em>Os Tx Commit Id Equals Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommitIdEqualsClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxCommitIdEqualsClause()
     * @generated
     */
    EClass OS_TX_COMMIT_ID_EQUALS_CLAUSE = eINSTANCE.getOsTxCommitIdEqualsClause();

    /**
     * The meta object literal for the '<em><b>Ids</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_TX_COMMIT_ID_EQUALS_CLAUSE__IDS = eINSTANCE.getOsTxCommitIdEqualsClause_Ids();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommitIdIsNullClauseImpl <em>Os Tx Commit Id Is Null Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommitIdIsNullClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxCommitIdIsNullClause()
     * @generated
     */
    EClass OS_TX_COMMIT_ID_IS_NULL_CLAUSE = eINSTANCE.getOsTxCommitIdIsNullClause();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdCriteriaImpl <em>Os Tx Id Criteria</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdCriteriaImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxIdCriteria()
     * @generated
     */
    EClass OS_TX_ID_CRITERIA = eINSTANCE.getOsTxIdCriteria();

    /**
     * The meta object literal for the '<em><b>Clause</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_TX_ID_CRITERIA__CLAUSE = eINSTANCE.getOsTxIdCriteria_Clause();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdClauseImpl <em>Os Tx Id Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxIdClause()
     * @generated
     */
    EClass OS_TX_ID_CLAUSE = eINSTANCE.getOsTxIdClause();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdEqualsClauseImpl <em>Os Tx Id Equals Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdEqualsClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxIdEqualsClause()
     * @generated
     */
    EClass OS_TX_ID_EQUALS_CLAUSE = eINSTANCE.getOsTxIdEqualsClause();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_TX_ID_EQUALS_CLAUSE__NAME = eINSTANCE.getOsTxIdEqualsClause_Name();

    /**
     * The meta object literal for the '<em><b>Ids</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_TX_ID_EQUALS_CLAUSE__IDS = eINSTANCE.getOsTxIdEqualsClause_Ids();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdOpClauseImpl <em>Os Tx Id Op Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdOpClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxIdOpClause()
     * @generated
     */
    EClass OS_TX_ID_OP_CLAUSE = eINSTANCE.getOsTxIdOpClause();

    /**
     * The meta object literal for the '<em><b>Op</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_TX_ID_OP_CLAUSE__OP = eINSTANCE.getOsTxIdOpClause_Op();

    /**
     * The meta object literal for the '<em><b>Id</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_TX_ID_OP_CLAUSE__ID = eINSTANCE.getOsTxIdOpClause_Id();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdRangeClauseImpl <em>Os Tx Id Range Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdRangeClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxIdRangeClause()
     * @generated
     */
    EClass OS_TX_ID_RANGE_CLAUSE = eINSTANCE.getOsTxIdRangeClause();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_TX_ID_RANGE_CLAUSE__NAME = eINSTANCE.getOsTxIdRangeClause_Name();

    /**
     * The meta object literal for the '<em><b>From Id</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_TX_ID_RANGE_CLAUSE__FROM_ID = eINSTANCE.getOsTxIdRangeClause_FromId();

    /**
     * The meta object literal for the '<em><b>To Id</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_TX_ID_RANGE_CLAUSE__TO_ID = eINSTANCE.getOsTxIdRangeClause_ToId();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxTimestampCriteriaImpl <em>Os Tx Timestamp Criteria</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxTimestampCriteriaImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxTimestampCriteria()
     * @generated
     */
    EClass OS_TX_TIMESTAMP_CRITERIA = eINSTANCE.getOsTxTimestampCriteria();

    /**
     * The meta object literal for the '<em><b>Clause</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_TX_TIMESTAMP_CRITERIA__CLAUSE = eINSTANCE.getOsTxTimestampCriteria_Clause();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxTimestampClauseImpl <em>Os Tx Timestamp Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxTimestampClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxTimestampClause()
     * @generated
     */
    EClass OS_TX_TIMESTAMP_CLAUSE = eINSTANCE.getOsTxTimestampClause();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxTimestampOpClauseImpl <em>Os Tx Timestamp Op Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxTimestampOpClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxTimestampOpClause()
     * @generated
     */
    EClass OS_TX_TIMESTAMP_OP_CLAUSE = eINSTANCE.getOsTxTimestampOpClause();

    /**
     * The meta object literal for the '<em><b>Op</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_TX_TIMESTAMP_OP_CLAUSE__OP = eINSTANCE.getOsTxTimestampOpClause_Op();

    /**
     * The meta object literal for the '<em><b>Timestamp</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_TX_TIMESTAMP_OP_CLAUSE__TIMESTAMP = eINSTANCE.getOsTxTimestampOpClause_Timestamp();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxTimestampRangeClauseImpl <em>Os Tx Timestamp Range Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxTimestampRangeClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxTimestampRangeClause()
     * @generated
     */
    EClass OS_TX_TIMESTAMP_RANGE_CLAUSE = eINSTANCE.getOsTxTimestampRangeClause();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_TX_TIMESTAMP_RANGE_CLAUSE__NAME = eINSTANCE.getOsTxTimestampRangeClause_Name();

    /**
     * The meta object literal for the '<em><b>From</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_TX_TIMESTAMP_RANGE_CLAUSE__FROM = eINSTANCE.getOsTxTimestampRangeClause_From();

    /**
     * The meta object literal for the '<em><b>To</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_TX_TIMESTAMP_RANGE_CLAUSE__TO = eINSTANCE.getOsTxTimestampRangeClause_To();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactQueryStatementImpl <em>Os Artifact Query Statement</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactQueryStatementImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsArtifactQueryStatement()
     * @generated
     */
    EClass OS_ARTIFACT_QUERY_STATEMENT = eINSTANCE.getOsArtifactQueryStatement();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_ARTIFACT_QUERY_STATEMENT__NAME = eINSTANCE.getOsArtifactQueryStatement_Name();

    /**
     * The meta object literal for the '<em><b>Data</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_ARTIFACT_QUERY_STATEMENT__DATA = eINSTANCE.getOsArtifactQueryStatement_Data();

    /**
     * The meta object literal for the '<em><b>Collect</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_ARTIFACT_QUERY_STATEMENT__COLLECT = eINSTANCE.getOsArtifactQueryStatement_Collect();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactQueryImpl <em>Os Artifact Query</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactQueryImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsArtifactQuery()
     * @generated
     */
    EClass OS_ARTIFACT_QUERY = eINSTANCE.getOsArtifactQuery();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_ARTIFACT_QUERY__NAME = eINSTANCE.getOsArtifactQuery_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactQueryAllImpl <em>Os Artifact Query All</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactQueryAllImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsArtifactQueryAll()
     * @generated
     */
    EClass OS_ARTIFACT_QUERY_ALL = eINSTANCE.getOsArtifactQueryAll();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactQueryByPredicateImpl <em>Os Artifact Query By Predicate</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactQueryByPredicateImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsArtifactQueryByPredicate()
     * @generated
     */
    EClass OS_ARTIFACT_QUERY_BY_PREDICATE = eINSTANCE.getOsArtifactQueryByPredicate();

    /**
     * The meta object literal for the '<em><b>Criteria</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_ARTIFACT_QUERY_BY_PREDICATE__CRITERIA = eINSTANCE.getOsArtifactQueryByPredicate_Criteria();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsItemCriteriaImpl <em>Os Item Criteria</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsItemCriteriaImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsItemCriteria()
     * @generated
     */
    EClass OS_ITEM_CRITERIA = eINSTANCE.getOsItemCriteria();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactCriteriaImpl <em>Os Artifact Criteria</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactCriteriaImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsArtifactCriteria()
     * @generated
     */
    EClass OS_ARTIFACT_CRITERIA = eINSTANCE.getOsArtifactCriteria();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactIdCriteriaImpl <em>Os Artifact Id Criteria</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactIdCriteriaImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsArtifactIdCriteria()
     * @generated
     */
    EClass OS_ARTIFACT_ID_CRITERIA = eINSTANCE.getOsArtifactIdCriteria();

    /**
     * The meta object literal for the '<em><b>Ids</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_ARTIFACT_ID_CRITERIA__IDS = eINSTANCE.getOsArtifactIdCriteria_Ids();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactTypeCriteriaImpl <em>Os Artifact Type Criteria</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactTypeCriteriaImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsArtifactTypeCriteria()
     * @generated
     */
    EClass OS_ARTIFACT_TYPE_CRITERIA = eINSTANCE.getOsArtifactTypeCriteria();

    /**
     * The meta object literal for the '<em><b>Clause</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_ARTIFACT_TYPE_CRITERIA__CLAUSE = eINSTANCE.getOsArtifactTypeCriteria_Clause();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactTypeClauseImpl <em>Os Artifact Type Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactTypeClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsArtifactTypeClause()
     * @generated
     */
    EClass OS_ARTIFACT_TYPE_CLAUSE = eINSTANCE.getOsArtifactTypeClause();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_ARTIFACT_TYPE_CLAUSE__NAME = eINSTANCE.getOsArtifactTypeClause_Name();

    /**
     * The meta object literal for the '<em><b>Types</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_ARTIFACT_TYPE_CLAUSE__TYPES = eINSTANCE.getOsArtifactTypeClause_Types();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactTypeEqualsClauseImpl <em>Os Artifact Type Equals Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactTypeEqualsClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsArtifactTypeEqualsClause()
     * @generated
     */
    EClass OS_ARTIFACT_TYPE_EQUALS_CLAUSE = eINSTANCE.getOsArtifactTypeEqualsClause();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactTypeInstanceOfClauseImpl <em>Os Artifact Type Instance Of Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactTypeInstanceOfClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsArtifactTypeInstanceOfClause()
     * @generated
     */
    EClass OS_ARTIFACT_TYPE_INSTANCE_OF_CLAUSE = eINSTANCE.getOsArtifactTypeInstanceOfClause();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsAttributeCriteriaImpl <em>Os Attribute Criteria</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsAttributeCriteriaImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsAttributeCriteria()
     * @generated
     */
    EClass OS_ATTRIBUTE_CRITERIA = eINSTANCE.getOsAttributeCriteria();

    /**
     * The meta object literal for the '<em><b>Clause</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_ATTRIBUTE_CRITERIA__CLAUSE = eINSTANCE.getOsAttributeCriteria_Clause();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsAttributeClauseImpl <em>Os Attribute Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsAttributeClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsAttributeClause()
     * @generated
     */
    EClass OS_ATTRIBUTE_CLAUSE = eINSTANCE.getOsAttributeClause();

    /**
     * The meta object literal for the '<em><b>Types</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_ATTRIBUTE_CLAUSE__TYPES = eINSTANCE.getOsAttributeClause_Types();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_ATTRIBUTE_CLAUSE__NAME = eINSTANCE.getOsAttributeClause_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsAttributeExistClauseImpl <em>Os Attribute Exist Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsAttributeExistClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsAttributeExistClause()
     * @generated
     */
    EClass OS_ATTRIBUTE_EXIST_CLAUSE = eINSTANCE.getOsAttributeExistClause();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OseAttributeOpClauseImpl <em>Ose Attribute Op Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OseAttributeOpClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOseAttributeOpClause()
     * @generated
     */
    EClass OSE_ATTRIBUTE_OP_CLAUSE = eINSTANCE.getOseAttributeOpClause();

    /**
     * The meta object literal for the '<em><b>Options</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OSE_ATTRIBUTE_OP_CLAUSE__OPTIONS = eINSTANCE.getOseAttributeOpClause_Options();

    /**
     * The meta object literal for the '<em><b>Values</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OSE_ATTRIBUTE_OP_CLAUSE__VALUES = eINSTANCE.getOseAttributeOpClause_Values();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsRelationCriteriaImpl <em>Os Relation Criteria</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsRelationCriteriaImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsRelationCriteria()
     * @generated
     */
    EClass OS_RELATION_CRITERIA = eINSTANCE.getOsRelationCriteria();

    /**
     * The meta object literal for the '<em><b>Clause</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_RELATION_CRITERIA__CLAUSE = eINSTANCE.getOsRelationCriteria_Clause();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsRelationClauseImpl <em>Os Relation Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsRelationClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsRelationClause()
     * @generated
     */
    EClass OS_RELATION_CLAUSE = eINSTANCE.getOsRelationClause();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_RELATION_CLAUSE__NAME = eINSTANCE.getOsRelationClause_Name();

    /**
     * The meta object literal for the '<em><b>Type</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_RELATION_CLAUSE__TYPE = eINSTANCE.getOsRelationClause_Type();

    /**
     * The meta object literal for the '<em><b>Side</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_RELATION_CLAUSE__SIDE = eINSTANCE.getOsRelationClause_Side();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsRelationExistClauseImpl <em>Os Relation Exist Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsRelationExistClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsRelationExistClause()
     * @generated
     */
    EClass OS_RELATION_EXIST_CLAUSE = eINSTANCE.getOsRelationExistClause();

    /**
     * The meta object literal for the '<em><b>Op</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_RELATION_EXIST_CLAUSE__OP = eINSTANCE.getOsRelationExistClause_Op();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsRelatedToClauseImpl <em>Os Related To Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsRelatedToClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsRelatedToClause()
     * @generated
     */
    EClass OS_RELATED_TO_CLAUSE = eINSTANCE.getOsRelatedToClause();

    /**
     * The meta object literal for the '<em><b>Ids</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_RELATED_TO_CLAUSE__IDS = eINSTANCE.getOsRelatedToClause_Ids();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsFollowClauseImpl <em>Os Follow Clause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsFollowClauseImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsFollowClause()
     * @generated
     */
    EClass OS_FOLLOW_CLAUSE = eINSTANCE.getOsFollowClause();

    /**
     * The meta object literal for the '<em><b>Stmt</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_FOLLOW_CLAUSE__STMT = eINSTANCE.getOsFollowClause_Stmt();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsFollowStatementImpl <em>Os Follow Statement</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsFollowStatementImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsFollowStatement()
     * @generated
     */
    EClass OS_FOLLOW_STATEMENT = eINSTANCE.getOsFollowStatement();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsFollowRelationTypeImpl <em>Os Follow Relation Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsFollowRelationTypeImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsFollowRelationType()
     * @generated
     */
    EClass OS_FOLLOW_RELATION_TYPE = eINSTANCE.getOsFollowRelationType();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_FOLLOW_RELATION_TYPE__NAME = eINSTANCE.getOsFollowRelationType_Name();

    /**
     * The meta object literal for the '<em><b>Type</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_FOLLOW_RELATION_TYPE__TYPE = eINSTANCE.getOsFollowRelationType_Type();

    /**
     * The meta object literal for the '<em><b>Side</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_FOLLOW_RELATION_TYPE__SIDE = eINSTANCE.getOsFollowRelationType_Side();

    /**
     * The meta object literal for the '<em><b>Criteria</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_FOLLOW_RELATION_TYPE__CRITERIA = eINSTANCE.getOsFollowRelationType_Criteria();

    /**
     * The meta object literal for the '<em><b>Collect</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_FOLLOW_RELATION_TYPE__COLLECT = eINSTANCE.getOsFollowRelationType_Collect();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsVariableDeclarationImpl <em>Os Variable Declaration</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsVariableDeclarationImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsVariableDeclaration()
     * @generated
     */
    EClass OS_VARIABLE_DECLARATION = eINSTANCE.getOsVariableDeclaration();

    /**
     * The meta object literal for the '<em><b>Elements</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_VARIABLE_DECLARATION__ELEMENTS = eINSTANCE.getOsVariableDeclaration_Elements();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsVariableImpl <em>Os Variable</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsVariableImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsVariable()
     * @generated
     */
    EClass OS_VARIABLE = eINSTANCE.getOsVariable();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_VARIABLE__NAME = eINSTANCE.getOsVariable_Name();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_VARIABLE__RIGHT = eINSTANCE.getOsVariable_Right();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsAssignmentImpl <em>Os Assignment</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsAssignmentImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsAssignment()
     * @generated
     */
    EClass OS_ASSIGNMENT = eINSTANCE.getOsAssignment();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_ASSIGNMENT__RIGHT = eINSTANCE.getOsAssignment_Right();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBooleanLiteralImpl <em>Os Boolean Literal</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsBooleanLiteralImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBooleanLiteral()
     * @generated
     */
    EClass OS_BOOLEAN_LITERAL = eINSTANCE.getOsBooleanLiteral();

    /**
     * The meta object literal for the '<em><b>Is True</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_BOOLEAN_LITERAL__IS_TRUE = eINSTANCE.getOsBooleanLiteral_IsTrue();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsNullLiteralImpl <em>Os Null Literal</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsNullLiteralImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsNullLiteral()
     * @generated
     */
    EClass OS_NULL_LITERAL = eINSTANCE.getOsNullLiteral();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsNumberLiteralImpl <em>Os Number Literal</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsNumberLiteralImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsNumberLiteral()
     * @generated
     */
    EClass OS_NUMBER_LITERAL = eINSTANCE.getOsNumberLiteral();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_NUMBER_LITERAL__VALUE = eINSTANCE.getOsNumberLiteral_Value();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsStringLiteralImpl <em>Os String Literal</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsStringLiteralImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsStringLiteral()
     * @generated
     */
    EClass OS_STRING_LITERAL = eINSTANCE.getOsStringLiteral();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_STRING_LITERAL__VALUE = eINSTANCE.getOsStringLiteral_Value();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTemplateLiteralImpl <em>Os Template Literal</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTemplateLiteralImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTemplateLiteral()
     * @generated
     */
    EClass OS_TEMPLATE_LITERAL = eINSTANCE.getOsTemplateLiteral();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_TEMPLATE_LITERAL__VALUE = eINSTANCE.getOsTemplateLiteral_Value();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsVariableReferenceImpl <em>Os Variable Reference</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsVariableReferenceImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsVariableReference()
     * @generated
     */
    EClass OS_VARIABLE_REFERENCE = eINSTANCE.getOsVariableReference();

    /**
     * The meta object literal for the '<em><b>Ref</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_VARIABLE_REFERENCE__REF = eINSTANCE.getOsVariableReference_Ref();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsDotExpressionImpl <em>Os Dot Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsDotExpressionImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsDotExpression()
     * @generated
     */
    EClass OS_DOT_EXPRESSION = eINSTANCE.getOsDotExpression();

    /**
     * The meta object literal for the '<em><b>Ref</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_DOT_EXPRESSION__REF = eINSTANCE.getOsDotExpression_Ref();

    /**
     * The meta object literal for the '<em><b>Tail</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_DOT_EXPRESSION__TAIL = eINSTANCE.getOsDotExpression_Tail();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsQueryExpressionImpl <em>Os Query Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsQueryExpressionImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsQueryExpression()
     * @generated
     */
    EClass OS_QUERY_EXPRESSION = eINSTANCE.getOsQueryExpression();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OS_QUERY_EXPRESSION__NAME = eINSTANCE.getOsQueryExpression_Name();

    /**
     * The meta object literal for the '<em><b>Query</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_QUERY_EXPRESSION__QUERY = eINSTANCE.getOsQueryExpression_Query();

    /**
     * The meta object literal for the '<em><b>Clause</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_QUERY_EXPRESSION__CLAUSE = eINSTANCE.getOsQueryExpression_Clause();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectObjectExpressionImpl <em>Os Collect Object Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectObjectExpressionImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsCollectObjectExpression()
     * @generated
     */
    EClass OS_COLLECT_OBJECT_EXPRESSION = eINSTANCE.getOsCollectObjectExpression();

    /**
     * The meta object literal for the '<em><b>Alias</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_COLLECT_OBJECT_EXPRESSION__ALIAS = eINSTANCE.getOsCollectObjectExpression_Alias();

    /**
     * The meta object literal for the '<em><b>Expressions</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_COLLECT_OBJECT_EXPRESSION__EXPRESSIONS = eINSTANCE.getOsCollectObjectExpression_Expressions();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectAllFieldsExpressionImpl <em>Os Collect All Fields Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectAllFieldsExpressionImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsCollectAllFieldsExpression()
     * @generated
     */
    EClass OS_COLLECT_ALL_FIELDS_EXPRESSION = eINSTANCE.getOsCollectAllFieldsExpression();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectFieldExpressionImpl <em>Os Collect Field Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectFieldExpressionImpl
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsCollectFieldExpression()
     * @generated
     */
    EClass OS_COLLECT_FIELD_EXPRESSION = eINSTANCE.getOsCollectFieldExpression();

    /**
     * The meta object literal for the '<em><b>Alias</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OS_COLLECT_FIELD_EXPRESSION__ALIAS = eINSTANCE.getOsCollectFieldExpression_Alias();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchState <em>Os Branch State</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchState
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchState()
     * @generated
     */
    EEnum OS_BRANCH_STATE = eINSTANCE.getOsBranchState();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchType <em>Os Branch Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchType
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchType()
     * @generated
     */
    EEnum OS_BRANCH_TYPE = eINSTANCE.getOsBranchType();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchArchiveFilter <em>Os Branch Archive Filter</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchArchiveFilter
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsBranchArchiveFilter()
     * @generated
     */
    EEnum OS_BRANCH_ARCHIVE_FILTER = eINSTANCE.getOsBranchArchiveFilter();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxType <em>Os Tx Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxType
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsTxType()
     * @generated
     */
    EEnum OS_TX_TYPE = eINSTANCE.getOsTxType();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationSide <em>Os Relation Side</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationSide
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsRelationSide()
     * @generated
     */
    EEnum OS_RELATION_SIDE = eINSTANCE.getOsRelationSide();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsOperator <em>Os Operator</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsOperator
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsOperator()
     * @generated
     */
    EEnum OS_OPERATOR = eINSTANCE.getOsOperator();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNonEqualOperator <em>Os Non Equal Operator</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNonEqualOperator
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsNonEqualOperator()
     * @generated
     */
    EEnum OS_NON_EQUAL_OPERATOR = eINSTANCE.getOsNonEqualOperator();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExistenceOperator <em>Os Existence Operator</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExistenceOperator
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsExistenceOperator()
     * @generated
     */
    EEnum OS_EXISTENCE_OPERATOR = eINSTANCE.getOsExistenceOperator();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryOption <em>Os Query Option</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryOption
     * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OrcsScriptDslPackageImpl#getOsQueryOption()
     * @generated
     */
    EEnum OS_QUERY_OPTION = eINSTANCE.getOsQueryOption();

  }

} //OrcsScriptDslPackage
