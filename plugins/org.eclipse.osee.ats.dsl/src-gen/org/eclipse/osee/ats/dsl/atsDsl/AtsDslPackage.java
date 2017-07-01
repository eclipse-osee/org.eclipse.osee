/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;

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
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslFactory
 * @model kind="package"
 * @generated
 */
public interface AtsDslPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "atsDsl";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/osee/ats/dsl/AtsDsl";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "atsDsl";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  AtsDslPackage eINSTANCE = org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslImpl <em>Ats Dsl</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getAtsDsl()
   * @generated
   */
  int ATS_DSL = 0;

  /**
   * The feature id for the '<em><b>User Def</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATS_DSL__USER_DEF = 0;

  /**
   * The feature id for the '<em><b>Team Def</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATS_DSL__TEAM_DEF = 1;

  /**
   * The feature id for the '<em><b>Actionable Item Def</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATS_DSL__ACTIONABLE_ITEM_DEF = 2;

  /**
   * The feature id for the '<em><b>Work Def</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATS_DSL__WORK_DEF = 3;

  /**
   * The feature id for the '<em><b>Program</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATS_DSL__PROGRAM = 4;

  /**
   * The feature id for the '<em><b>Rule</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATS_DSL__RULE = 5;

  /**
   * The number of structural features of the '<em>Ats Dsl</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATS_DSL_FEATURE_COUNT = 6;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.UserDefImpl <em>User Def</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.UserDefImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getUserDef()
   * @generated
   */
  int USER_DEF = 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USER_DEF__NAME = 0;

  /**
   * The feature id for the '<em><b>User Def Option</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USER_DEF__USER_DEF_OPTION = 1;

  /**
   * The feature id for the '<em><b>Active</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USER_DEF__ACTIVE = 2;

  /**
   * The feature id for the '<em><b>User Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USER_DEF__USER_ID = 3;

  /**
   * The feature id for the '<em><b>Email</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USER_DEF__EMAIL = 4;

  /**
   * The feature id for the '<em><b>Admin</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USER_DEF__ADMIN = 5;

  /**
   * The number of structural features of the '<em>User Def</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USER_DEF_FEATURE_COUNT = 6;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.AttrDefImpl <em>Attr Def</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AttrDefImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getAttrDef()
   * @generated
   */
  int ATTR_DEF = 2;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTR_DEF__NAME = 0;

  /**
   * The feature id for the '<em><b>Option</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTR_DEF__OPTION = 1;

  /**
   * The number of structural features of the '<em>Attr Def</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTR_DEF_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.AttrDefOptionsImpl <em>Attr Def Options</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AttrDefOptionsImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getAttrDefOptions()
   * @generated
   */
  int ATTR_DEF_OPTIONS = 3;

  /**
   * The number of structural features of the '<em>Attr Def Options</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTR_DEF_OPTIONS_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.AttrValueDefImpl <em>Attr Value Def</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AttrValueDefImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getAttrValueDef()
   * @generated
   */
  int ATTR_VALUE_DEF = 4;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTR_VALUE_DEF__VALUE = ATTR_DEF_OPTIONS_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Attr Value Def</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTR_VALUE_DEF_FEATURE_COUNT = ATTR_DEF_OPTIONS_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.AttrFullDefImpl <em>Attr Full Def</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AttrFullDefImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getAttrFullDef()
   * @generated
   */
  int ATTR_FULL_DEF = 5;

  /**
   * The feature id for the '<em><b>Uuid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTR_FULL_DEF__UUID = ATTR_DEF_OPTIONS_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Values</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTR_FULL_DEF__VALUES = ATTR_DEF_OPTIONS_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Attr Full Def</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTR_FULL_DEF_FEATURE_COUNT = ATTR_DEF_OPTIONS_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ProgramDefImpl <em>Program Def</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.ProgramDefImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getProgramDef()
   * @generated
   */
  int PROGRAM_DEF = 6;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROGRAM_DEF__NAME = 0;

  /**
   * The feature id for the '<em><b>Program Def Option</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROGRAM_DEF__PROGRAM_DEF_OPTION = 1;

  /**
   * The feature id for the '<em><b>Uuid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROGRAM_DEF__UUID = 2;

  /**
   * The feature id for the '<em><b>Artifact Type Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROGRAM_DEF__ARTIFACT_TYPE_NAME = 3;

  /**
   * The feature id for the '<em><b>Active</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROGRAM_DEF__ACTIVE = 4;

  /**
   * The feature id for the '<em><b>Namespace</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROGRAM_DEF__NAMESPACE = 5;

  /**
   * The feature id for the '<em><b>Team Definition</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROGRAM_DEF__TEAM_DEFINITION = 6;

  /**
   * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROGRAM_DEF__ATTRIBUTES = 7;

  /**
   * The number of structural features of the '<em>Program Def</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROGRAM_DEF_FEATURE_COUNT = 8;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.TeamDefImpl <em>Team Def</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.TeamDefImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getTeamDef()
   * @generated
   */
  int TEAM_DEF = 7;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEAM_DEF__NAME = 0;

  /**
   * The feature id for the '<em><b>Team Def Option</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEAM_DEF__TEAM_DEF_OPTION = 1;

  /**
   * The feature id for the '<em><b>Uuid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEAM_DEF__UUID = 2;

  /**
   * The feature id for the '<em><b>Active</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEAM_DEF__ACTIVE = 3;

  /**
   * The feature id for the '<em><b>Static Id</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEAM_DEF__STATIC_ID = 4;

  /**
   * The feature id for the '<em><b>Lead</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEAM_DEF__LEAD = 5;

  /**
   * The feature id for the '<em><b>Member</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEAM_DEF__MEMBER = 6;

  /**
   * The feature id for the '<em><b>Privileged</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEAM_DEF__PRIVILEGED = 7;

  /**
   * The feature id for the '<em><b>Work Definition</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEAM_DEF__WORK_DEFINITION = 8;

  /**
   * The feature id for the '<em><b>Related Task Work Definition</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEAM_DEF__RELATED_TASK_WORK_DEFINITION = 9;

  /**
   * The feature id for the '<em><b>Team Workflow Artifact Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEAM_DEF__TEAM_WORKFLOW_ARTIFACT_TYPE = 10;

  /**
   * The feature id for the '<em><b>Access Context Id</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEAM_DEF__ACCESS_CONTEXT_ID = 11;

  /**
   * The feature id for the '<em><b>Version</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEAM_DEF__VERSION = 12;

  /**
   * The feature id for the '<em><b>Rules</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEAM_DEF__RULES = 13;

  /**
   * The feature id for the '<em><b>Children</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEAM_DEF__CHILDREN = 14;

  /**
   * The number of structural features of the '<em>Team Def</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEAM_DEF_FEATURE_COUNT = 15;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ActionableItemDefImpl <em>Actionable Item Def</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.ActionableItemDefImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getActionableItemDef()
   * @generated
   */
  int ACTIONABLE_ITEM_DEF = 8;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTIONABLE_ITEM_DEF__NAME = 0;

  /**
   * The feature id for the '<em><b>Ai Def Option</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTIONABLE_ITEM_DEF__AI_DEF_OPTION = 1;

  /**
   * The feature id for the '<em><b>Uuid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTIONABLE_ITEM_DEF__UUID = 2;

  /**
   * The feature id for the '<em><b>Active</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTIONABLE_ITEM_DEF__ACTIVE = 3;

  /**
   * The feature id for the '<em><b>Actionable</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTIONABLE_ITEM_DEF__ACTIONABLE = 4;

  /**
   * The feature id for the '<em><b>Lead</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTIONABLE_ITEM_DEF__LEAD = 5;

  /**
   * The feature id for the '<em><b>Owner</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTIONABLE_ITEM_DEF__OWNER = 6;

  /**
   * The feature id for the '<em><b>Static Id</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTIONABLE_ITEM_DEF__STATIC_ID = 7;

  /**
   * The feature id for the '<em><b>Team Def</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTIONABLE_ITEM_DEF__TEAM_DEF = 8;

  /**
   * The feature id for the '<em><b>Access Context Id</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTIONABLE_ITEM_DEF__ACCESS_CONTEXT_ID = 9;

  /**
   * The feature id for the '<em><b>Rules</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTIONABLE_ITEM_DEF__RULES = 10;

  /**
   * The feature id for the '<em><b>Children</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTIONABLE_ITEM_DEF__CHILDREN = 11;

  /**
   * The number of structural features of the '<em>Actionable Item Def</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTIONABLE_ITEM_DEF_FEATURE_COUNT = 12;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.VersionDefImpl <em>Version Def</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.VersionDefImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getVersionDef()
   * @generated
   */
  int VERSION_DEF = 9;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VERSION_DEF__NAME = 0;

  /**
   * The feature id for the '<em><b>Uuid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VERSION_DEF__UUID = 1;

  /**
   * The feature id for the '<em><b>Static Id</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VERSION_DEF__STATIC_ID = 2;

  /**
   * The feature id for the '<em><b>Next</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VERSION_DEF__NEXT = 3;

  /**
   * The feature id for the '<em><b>Released</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VERSION_DEF__RELEASED = 4;

  /**
   * The feature id for the '<em><b>Allow Create Branch</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VERSION_DEF__ALLOW_CREATE_BRANCH = 5;

  /**
   * The feature id for the '<em><b>Allow Commit Branch</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VERSION_DEF__ALLOW_COMMIT_BRANCH = 6;

  /**
   * The feature id for the '<em><b>Baseline Branch Uuid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VERSION_DEF__BASELINE_BRANCH_UUID = 7;

  /**
   * The feature id for the '<em><b>Parallel Version</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VERSION_DEF__PARALLEL_VERSION = 8;

  /**
   * The number of structural features of the '<em>Version Def</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VERSION_DEF_FEATURE_COUNT = 9;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.WorkDefImpl <em>Work Def</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.WorkDefImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getWorkDef()
   * @generated
   */
  int WORK_DEF = 10;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORK_DEF__NAME = 0;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORK_DEF__ID = 1;

  /**
   * The feature id for the '<em><b>Start State</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORK_DEF__START_STATE = 2;

  /**
   * The feature id for the '<em><b>Widget Defs</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORK_DEF__WIDGET_DEFS = 3;

  /**
   * The feature id for the '<em><b>Decision Review Defs</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORK_DEF__DECISION_REVIEW_DEFS = 4;

  /**
   * The feature id for the '<em><b>Peer Review Defs</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORK_DEF__PEER_REVIEW_DEFS = 5;

  /**
   * The feature id for the '<em><b>States</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORK_DEF__STATES = 6;

  /**
   * The number of structural features of the '<em>Work Def</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORK_DEF_FEATURE_COUNT = 7;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.WidgetDefImpl <em>Widget Def</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.WidgetDefImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getWidgetDef()
   * @generated
   */
  int WIDGET_DEF = 11;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WIDGET_DEF__NAME = 0;

  /**
   * The feature id for the '<em><b>Attribute Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WIDGET_DEF__ATTRIBUTE_NAME = 1;

  /**
   * The feature id for the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WIDGET_DEF__DESCRIPTION = 2;

  /**
   * The feature id for the '<em><b>XWidget Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WIDGET_DEF__XWIDGET_NAME = 3;

  /**
   * The feature id for the '<em><b>Default Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WIDGET_DEF__DEFAULT_VALUE = 4;

  /**
   * The feature id for the '<em><b>Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WIDGET_DEF__HEIGHT = 5;

  /**
   * The feature id for the '<em><b>Option</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WIDGET_DEF__OPTION = 6;

  /**
   * The feature id for the '<em><b>Min Constraint</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WIDGET_DEF__MIN_CONSTRAINT = 7;

  /**
   * The feature id for the '<em><b>Max Constraint</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WIDGET_DEF__MAX_CONSTRAINT = 8;

  /**
   * The number of structural features of the '<em>Widget Def</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WIDGET_DEF_FEATURE_COUNT = 9;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.LayoutItemImpl <em>Layout Item</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.LayoutItemImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getLayoutItem()
   * @generated
   */
  int LAYOUT_ITEM = 28;

  /**
   * The number of structural features of the '<em>Layout Item</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LAYOUT_ITEM_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.WidgetRefImpl <em>Widget Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.WidgetRefImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getWidgetRef()
   * @generated
   */
  int WIDGET_REF = 12;

  /**
   * The feature id for the '<em><b>Widget</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WIDGET_REF__WIDGET = LAYOUT_ITEM_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Widget Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WIDGET_REF_FEATURE_COUNT = LAYOUT_ITEM_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.AttrWidgetImpl <em>Attr Widget</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AttrWidgetImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getAttrWidget()
   * @generated
   */
  int ATTR_WIDGET = 13;

  /**
   * The feature id for the '<em><b>Attribute Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTR_WIDGET__ATTRIBUTE_NAME = LAYOUT_ITEM_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Option</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTR_WIDGET__OPTION = LAYOUT_ITEM_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Attr Widget</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTR_WIDGET_FEATURE_COUNT = LAYOUT_ITEM_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.StateDefImpl <em>State Def</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.StateDefImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getStateDef()
   * @generated
   */
  int STATE_DEF = 14;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATE_DEF__NAME = 0;

  /**
   * The feature id for the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATE_DEF__DESCRIPTION = 1;

  /**
   * The feature id for the '<em><b>Page Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATE_DEF__PAGE_TYPE = 2;

  /**
   * The feature id for the '<em><b>Ordinal</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATE_DEF__ORDINAL = 3;

  /**
   * The feature id for the '<em><b>Transition States</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATE_DEF__TRANSITION_STATES = 4;

  /**
   * The feature id for the '<em><b>Rules</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATE_DEF__RULES = 5;

  /**
   * The feature id for the '<em><b>Decision Reviews</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATE_DEF__DECISION_REVIEWS = 6;

  /**
   * The feature id for the '<em><b>Peer Reviews</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATE_DEF__PEER_REVIEWS = 7;

  /**
   * The feature id for the '<em><b>Percent Weight</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATE_DEF__PERCENT_WEIGHT = 8;

  /**
   * The feature id for the '<em><b>Recommended Percent Complete</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATE_DEF__RECOMMENDED_PERCENT_COMPLETE = 9;

  /**
   * The feature id for the '<em><b>Color</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATE_DEF__COLOR = 10;

  /**
   * The feature id for the '<em><b>Layout</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATE_DEF__LAYOUT = 11;

  /**
   * The number of structural features of the '<em>State Def</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATE_DEF_FEATURE_COUNT = 12;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.DecisionReviewRefImpl <em>Decision Review Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.DecisionReviewRefImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getDecisionReviewRef()
   * @generated
   */
  int DECISION_REVIEW_REF = 15;

  /**
   * The feature id for the '<em><b>Decision Review</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECISION_REVIEW_REF__DECISION_REVIEW = 0;

  /**
   * The number of structural features of the '<em>Decision Review Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECISION_REVIEW_REF_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.DecisionReviewDefImpl <em>Decision Review Def</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.DecisionReviewDefImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getDecisionReviewDef()
   * @generated
   */
  int DECISION_REVIEW_DEF = 16;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECISION_REVIEW_DEF__NAME = 0;

  /**
   * The feature id for the '<em><b>Title</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECISION_REVIEW_DEF__TITLE = 1;

  /**
   * The feature id for the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECISION_REVIEW_DEF__DESCRIPTION = 2;

  /**
   * The feature id for the '<em><b>Related To State</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECISION_REVIEW_DEF__RELATED_TO_STATE = 3;

  /**
   * The feature id for the '<em><b>Blocking Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECISION_REVIEW_DEF__BLOCKING_TYPE = 4;

  /**
   * The feature id for the '<em><b>State Event</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECISION_REVIEW_DEF__STATE_EVENT = 5;

  /**
   * The feature id for the '<em><b>Assignee Refs</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECISION_REVIEW_DEF__ASSIGNEE_REFS = 6;

  /**
   * The feature id for the '<em><b>Auto Transition To Decision</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECISION_REVIEW_DEF__AUTO_TRANSITION_TO_DECISION = 7;

  /**
   * The feature id for the '<em><b>Options</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECISION_REVIEW_DEF__OPTIONS = 8;

  /**
   * The number of structural features of the '<em>Decision Review Def</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECISION_REVIEW_DEF_FEATURE_COUNT = 9;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.DecisionReviewOptImpl <em>Decision Review Opt</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.DecisionReviewOptImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getDecisionReviewOpt()
   * @generated
   */
  int DECISION_REVIEW_OPT = 17;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECISION_REVIEW_OPT__NAME = 0;

  /**
   * The feature id for the '<em><b>Followup</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECISION_REVIEW_OPT__FOLLOWUP = 1;

  /**
   * The number of structural features of the '<em>Decision Review Opt</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECISION_REVIEW_OPT_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.PeerReviewRefImpl <em>Peer Review Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.PeerReviewRefImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getPeerReviewRef()
   * @generated
   */
  int PEER_REVIEW_REF = 18;

  /**
   * The feature id for the '<em><b>Peer Review</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PEER_REVIEW_REF__PEER_REVIEW = 0;

  /**
   * The number of structural features of the '<em>Peer Review Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PEER_REVIEW_REF_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.PeerReviewDefImpl <em>Peer Review Def</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.PeerReviewDefImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getPeerReviewDef()
   * @generated
   */
  int PEER_REVIEW_DEF = 19;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PEER_REVIEW_DEF__NAME = 0;

  /**
   * The feature id for the '<em><b>Title</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PEER_REVIEW_DEF__TITLE = 1;

  /**
   * The feature id for the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PEER_REVIEW_DEF__DESCRIPTION = 2;

  /**
   * The feature id for the '<em><b>Location</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PEER_REVIEW_DEF__LOCATION = 3;

  /**
   * The feature id for the '<em><b>Related To State</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PEER_REVIEW_DEF__RELATED_TO_STATE = 4;

  /**
   * The feature id for the '<em><b>Blocking Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PEER_REVIEW_DEF__BLOCKING_TYPE = 5;

  /**
   * The feature id for the '<em><b>State Event</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PEER_REVIEW_DEF__STATE_EVENT = 6;

  /**
   * The feature id for the '<em><b>Assignee Refs</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PEER_REVIEW_DEF__ASSIGNEE_REFS = 7;

  /**
   * The number of structural features of the '<em>Peer Review Def</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PEER_REVIEW_DEF_FEATURE_COUNT = 8;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.FollowupRefImpl <em>Followup Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.FollowupRefImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getFollowupRef()
   * @generated
   */
  int FOLLOWUP_REF = 20;

  /**
   * The feature id for the '<em><b>Assignee Refs</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FOLLOWUP_REF__ASSIGNEE_REFS = 0;

  /**
   * The number of structural features of the '<em>Followup Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FOLLOWUP_REF_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.UserRefImpl <em>User Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.UserRefImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getUserRef()
   * @generated
   */
  int USER_REF = 21;

  /**
   * The number of structural features of the '<em>User Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USER_REF_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.UserByUserIdImpl <em>User By User Id</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.UserByUserIdImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getUserByUserId()
   * @generated
   */
  int USER_BY_USER_ID = 22;

  /**
   * The feature id for the '<em><b>User Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USER_BY_USER_ID__USER_ID = USER_REF_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>User By User Id</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USER_BY_USER_ID_FEATURE_COUNT = USER_REF_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.UserByNameImpl <em>User By Name</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.UserByNameImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getUserByName()
   * @generated
   */
  int USER_BY_NAME = 23;

  /**
   * The feature id for the '<em><b>User Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USER_BY_NAME__USER_NAME = USER_REF_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>User By Name</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USER_BY_NAME_FEATURE_COUNT = USER_REF_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ToStateImpl <em>To State</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.ToStateImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getToState()
   * @generated
   */
  int TO_STATE = 24;

  /**
   * The feature id for the '<em><b>State</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TO_STATE__STATE = 0;

  /**
   * The feature id for the '<em><b>Options</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TO_STATE__OPTIONS = 1;

  /**
   * The number of structural features of the '<em>To State</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TO_STATE_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.LayoutTypeImpl <em>Layout Type</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.LayoutTypeImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getLayoutType()
   * @generated
   */
  int LAYOUT_TYPE = 25;

  /**
   * The number of structural features of the '<em>Layout Type</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LAYOUT_TYPE_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.LayoutDefImpl <em>Layout Def</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.LayoutDefImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getLayoutDef()
   * @generated
   */
  int LAYOUT_DEF = 26;

  /**
   * The feature id for the '<em><b>Layout Items</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LAYOUT_DEF__LAYOUT_ITEMS = LAYOUT_TYPE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Layout Def</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LAYOUT_DEF_FEATURE_COUNT = LAYOUT_TYPE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.LayoutCopyImpl <em>Layout Copy</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.LayoutCopyImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getLayoutCopy()
   * @generated
   */
  int LAYOUT_COPY = 27;

  /**
   * The feature id for the '<em><b>State</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LAYOUT_COPY__STATE = LAYOUT_TYPE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Layout Copy</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LAYOUT_COPY_FEATURE_COUNT = LAYOUT_TYPE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.CompositeImpl <em>Composite</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.CompositeImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getComposite()
   * @generated
   */
  int COMPOSITE = 29;

  /**
   * The feature id for the '<em><b>Num Columns</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSITE__NUM_COLUMNS = LAYOUT_ITEM_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Layout Items</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSITE__LAYOUT_ITEMS = LAYOUT_ITEM_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Options</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSITE__OPTIONS = LAYOUT_ITEM_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Composite</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSITE_FEATURE_COUNT = LAYOUT_ITEM_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.RuleImpl <em>Rule</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.RuleImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getRule()
   * @generated
   */
  int RULE = 35;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RULE__NAME = 0;

  /**
   * The feature id for the '<em><b>Title</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RULE__TITLE = 1;

  /**
   * The feature id for the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RULE__DESCRIPTION = 2;

  /**
   * The feature id for the '<em><b>Rule Location</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RULE__RULE_LOCATION = 3;

  /**
   * The number of structural features of the '<em>Rule</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RULE_FEATURE_COUNT = 4;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.RuleDefImpl <em>Rule Def</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.RuleDefImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getRuleDef()
   * @generated
   */
  int RULE_DEF = 30;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RULE_DEF__NAME = RULE__NAME;

  /**
   * The feature id for the '<em><b>Title</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RULE_DEF__TITLE = RULE__TITLE;

  /**
   * The feature id for the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RULE_DEF__DESCRIPTION = RULE__DESCRIPTION;

  /**
   * The feature id for the '<em><b>Rule Location</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RULE_DEF__RULE_LOCATION = RULE__RULE_LOCATION;

  /**
   * The number of structural features of the '<em>Rule Def</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RULE_DEF_FEATURE_COUNT = RULE_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.CreateTaskRuleImpl <em>Create Task Rule</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.CreateTaskRuleImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getCreateTaskRule()
   * @generated
   */
  int CREATE_TASK_RULE = 31;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_TASK_RULE__NAME = RULE__NAME;

  /**
   * The feature id for the '<em><b>Title</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_TASK_RULE__TITLE = RULE__TITLE;

  /**
   * The feature id for the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_TASK_RULE__DESCRIPTION = RULE__DESCRIPTION;

  /**
   * The feature id for the '<em><b>Rule Location</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_TASK_RULE__RULE_LOCATION = RULE__RULE_LOCATION;

  /**
   * The feature id for the '<em><b>Assignees</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_TASK_RULE__ASSIGNEES = RULE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Related State</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_TASK_RULE__RELATED_STATE = RULE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Task Work Def</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_TASK_RULE__TASK_WORK_DEF = RULE_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>On Event</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_TASK_RULE__ON_EVENT = RULE_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_TASK_RULE__ATTRIBUTES = RULE_FEATURE_COUNT + 4;

  /**
   * The number of structural features of the '<em>Create Task Rule</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_TASK_RULE_FEATURE_COUNT = RULE_FEATURE_COUNT + 5;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ReviewRuleImpl <em>Review Rule</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.ReviewRuleImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getReviewRule()
   * @generated
   */
  int REVIEW_RULE = 34;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_RULE__NAME = RULE__NAME;

  /**
   * The feature id for the '<em><b>Title</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_RULE__TITLE = RULE__TITLE;

  /**
   * The feature id for the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_RULE__DESCRIPTION = RULE__DESCRIPTION;

  /**
   * The feature id for the '<em><b>Rule Location</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_RULE__RULE_LOCATION = RULE__RULE_LOCATION;

  /**
   * The feature id for the '<em><b>Assignees</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_RULE__ASSIGNEES = RULE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Related To State</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_RULE__RELATED_TO_STATE = RULE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Blocking Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_RULE__BLOCKING_TYPE = RULE_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>State Event</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_RULE__STATE_EVENT = RULE_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_RULE__ATTRIBUTES = RULE_FEATURE_COUNT + 4;

  /**
   * The number of structural features of the '<em>Review Rule</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_RULE_FEATURE_COUNT = RULE_FEATURE_COUNT + 5;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.CreateDecisionReviewRuleImpl <em>Create Decision Review Rule</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.CreateDecisionReviewRuleImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getCreateDecisionReviewRule()
   * @generated
   */
  int CREATE_DECISION_REVIEW_RULE = 32;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_DECISION_REVIEW_RULE__NAME = REVIEW_RULE__NAME;

  /**
   * The feature id for the '<em><b>Title</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_DECISION_REVIEW_RULE__TITLE = REVIEW_RULE__TITLE;

  /**
   * The feature id for the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_DECISION_REVIEW_RULE__DESCRIPTION = REVIEW_RULE__DESCRIPTION;

  /**
   * The feature id for the '<em><b>Rule Location</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_DECISION_REVIEW_RULE__RULE_LOCATION = REVIEW_RULE__RULE_LOCATION;

  /**
   * The feature id for the '<em><b>Assignees</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_DECISION_REVIEW_RULE__ASSIGNEES = REVIEW_RULE__ASSIGNEES;

  /**
   * The feature id for the '<em><b>Related To State</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_DECISION_REVIEW_RULE__RELATED_TO_STATE = REVIEW_RULE__RELATED_TO_STATE;

  /**
   * The feature id for the '<em><b>Blocking Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_DECISION_REVIEW_RULE__BLOCKING_TYPE = REVIEW_RULE__BLOCKING_TYPE;

  /**
   * The feature id for the '<em><b>State Event</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_DECISION_REVIEW_RULE__STATE_EVENT = REVIEW_RULE__STATE_EVENT;

  /**
   * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_DECISION_REVIEW_RULE__ATTRIBUTES = REVIEW_RULE__ATTRIBUTES;

  /**
   * The feature id for the '<em><b>Auto Transition To Decision</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_DECISION_REVIEW_RULE__AUTO_TRANSITION_TO_DECISION = REVIEW_RULE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Options</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_DECISION_REVIEW_RULE__OPTIONS = REVIEW_RULE_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Create Decision Review Rule</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_DECISION_REVIEW_RULE_FEATURE_COUNT = REVIEW_RULE_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.CreatePeerReviewRuleImpl <em>Create Peer Review Rule</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.CreatePeerReviewRuleImpl
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getCreatePeerReviewRule()
   * @generated
   */
  int CREATE_PEER_REVIEW_RULE = 33;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_PEER_REVIEW_RULE__NAME = REVIEW_RULE__NAME;

  /**
   * The feature id for the '<em><b>Title</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_PEER_REVIEW_RULE__TITLE = REVIEW_RULE__TITLE;

  /**
   * The feature id for the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_PEER_REVIEW_RULE__DESCRIPTION = REVIEW_RULE__DESCRIPTION;

  /**
   * The feature id for the '<em><b>Rule Location</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_PEER_REVIEW_RULE__RULE_LOCATION = REVIEW_RULE__RULE_LOCATION;

  /**
   * The feature id for the '<em><b>Assignees</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_PEER_REVIEW_RULE__ASSIGNEES = REVIEW_RULE__ASSIGNEES;

  /**
   * The feature id for the '<em><b>Related To State</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_PEER_REVIEW_RULE__RELATED_TO_STATE = REVIEW_RULE__RELATED_TO_STATE;

  /**
   * The feature id for the '<em><b>Blocking Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_PEER_REVIEW_RULE__BLOCKING_TYPE = REVIEW_RULE__BLOCKING_TYPE;

  /**
   * The feature id for the '<em><b>State Event</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_PEER_REVIEW_RULE__STATE_EVENT = REVIEW_RULE__STATE_EVENT;

  /**
   * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_PEER_REVIEW_RULE__ATTRIBUTES = REVIEW_RULE__ATTRIBUTES;

  /**
   * The feature id for the '<em><b>Location</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_PEER_REVIEW_RULE__LOCATION = REVIEW_RULE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Create Peer Review Rule</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CREATE_PEER_REVIEW_RULE_FEATURE_COUNT = REVIEW_RULE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.OnEventType <em>On Event Type</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.OnEventType
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getOnEventType()
   * @generated
   */
  int ON_EVENT_TYPE = 36;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.BooleanDef <em>Boolean Def</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.BooleanDef
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getBooleanDef()
   * @generated
   */
  int BOOLEAN_DEF = 37;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.WorkflowEventType <em>Workflow Event Type</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.WorkflowEventType
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getWorkflowEventType()
   * @generated
   */
  int WORKFLOW_EVENT_TYPE = 38;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.ReviewBlockingType <em>Review Blocking Type</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.ReviewBlockingType
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getReviewBlockingType()
   * @generated
   */
  int REVIEW_BLOCKING_TYPE = 39;

  /**
   * The meta object id for the '{@link org.eclipse.osee.ats.dsl.atsDsl.RuleLocation <em>Rule Location</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.ats.dsl.atsDsl.RuleLocation
   * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getRuleLocation()
   * @generated
   */
  int RULE_LOCATION = 40;


  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.AtsDsl <em>Ats Dsl</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Ats Dsl</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDsl
   * @generated
   */
  EClass getAtsDsl();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.AtsDsl#getUserDef <em>User Def</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>User Def</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDsl#getUserDef()
   * @see #getAtsDsl()
   * @generated
   */
  EReference getAtsDsl_UserDef();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.AtsDsl#getTeamDef <em>Team Def</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Team Def</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDsl#getTeamDef()
   * @see #getAtsDsl()
   * @generated
   */
  EReference getAtsDsl_TeamDef();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.AtsDsl#getActionableItemDef <em>Actionable Item Def</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Actionable Item Def</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDsl#getActionableItemDef()
   * @see #getAtsDsl()
   * @generated
   */
  EReference getAtsDsl_ActionableItemDef();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.AtsDsl#getWorkDef <em>Work Def</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Work Def</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDsl#getWorkDef()
   * @see #getAtsDsl()
   * @generated
   */
  EReference getAtsDsl_WorkDef();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.AtsDsl#getProgram <em>Program</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Program</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDsl#getProgram()
   * @see #getAtsDsl()
   * @generated
   */
  EReference getAtsDsl_Program();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.AtsDsl#getRule <em>Rule</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Rule</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDsl#getRule()
   * @see #getAtsDsl()
   * @generated
   */
  EReference getAtsDsl_Rule();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.UserDef <em>User Def</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>User Def</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.UserDef
   * @generated
   */
  EClass getUserDef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.UserDef#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.UserDef#getName()
   * @see #getUserDef()
   * @generated
   */
  EAttribute getUserDef_Name();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.ats.dsl.atsDsl.UserDef#getUserDefOption <em>User Def Option</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>User Def Option</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.UserDef#getUserDefOption()
   * @see #getUserDef()
   * @generated
   */
  EAttribute getUserDef_UserDefOption();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.UserDef#getActive <em>Active</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Active</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.UserDef#getActive()
   * @see #getUserDef()
   * @generated
   */
  EAttribute getUserDef_Active();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.UserDef#getUserId <em>User Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>User Id</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.UserDef#getUserId()
   * @see #getUserDef()
   * @generated
   */
  EAttribute getUserDef_UserId();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.UserDef#getEmail <em>Email</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Email</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.UserDef#getEmail()
   * @see #getUserDef()
   * @generated
   */
  EAttribute getUserDef_Email();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.UserDef#getAdmin <em>Admin</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Admin</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.UserDef#getAdmin()
   * @see #getUserDef()
   * @generated
   */
  EAttribute getUserDef_Admin();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.AttrDef <em>Attr Def</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attr Def</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AttrDef
   * @generated
   */
  EClass getAttrDef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.AttrDef#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AttrDef#getName()
   * @see #getAttrDef()
   * @generated
   */
  EAttribute getAttrDef_Name();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.ats.dsl.atsDsl.AttrDef#getOption <em>Option</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Option</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AttrDef#getOption()
   * @see #getAttrDef()
   * @generated
   */
  EReference getAttrDef_Option();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.AttrDefOptions <em>Attr Def Options</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attr Def Options</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AttrDefOptions
   * @generated
   */
  EClass getAttrDefOptions();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.AttrValueDef <em>Attr Value Def</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attr Value Def</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AttrValueDef
   * @generated
   */
  EClass getAttrValueDef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.AttrValueDef#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AttrValueDef#getValue()
   * @see #getAttrValueDef()
   * @generated
   */
  EAttribute getAttrValueDef_Value();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.AttrFullDef <em>Attr Full Def</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attr Full Def</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AttrFullDef
   * @generated
   */
  EClass getAttrFullDef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.AttrFullDef#getUuid <em>Uuid</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Uuid</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AttrFullDef#getUuid()
   * @see #getAttrFullDef()
   * @generated
   */
  EAttribute getAttrFullDef_Uuid();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.ats.dsl.atsDsl.AttrFullDef#getValues <em>Values</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Values</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AttrFullDef#getValues()
   * @see #getAttrFullDef()
   * @generated
   */
  EAttribute getAttrFullDef_Values();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.ProgramDef <em>Program Def</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Program Def</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ProgramDef
   * @generated
   */
  EClass getProgramDef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getName()
   * @see #getProgramDef()
   * @generated
   */
  EAttribute getProgramDef_Name();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getProgramDefOption <em>Program Def Option</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Program Def Option</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getProgramDefOption()
   * @see #getProgramDef()
   * @generated
   */
  EAttribute getProgramDef_ProgramDefOption();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getUuid <em>Uuid</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Uuid</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getUuid()
   * @see #getProgramDef()
   * @generated
   */
  EAttribute getProgramDef_Uuid();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getArtifactTypeName <em>Artifact Type Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Artifact Type Name</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getArtifactTypeName()
   * @see #getProgramDef()
   * @generated
   */
  EAttribute getProgramDef_ArtifactTypeName();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getActive <em>Active</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Active</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getActive()
   * @see #getProgramDef()
   * @generated
   */
  EAttribute getProgramDef_Active();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getNamespace <em>Namespace</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Namespace</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getNamespace()
   * @see #getProgramDef()
   * @generated
   */
  EAttribute getProgramDef_Namespace();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getTeamDefinition <em>Team Definition</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Team Definition</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getTeamDefinition()
   * @see #getProgramDef()
   * @generated
   */
  EAttribute getProgramDef_TeamDefinition();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getAttributes <em>Attributes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Attributes</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getAttributes()
   * @see #getProgramDef()
   * @generated
   */
  EReference getProgramDef_Attributes();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.TeamDef <em>Team Def</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Team Def</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.TeamDef
   * @generated
   */
  EClass getTeamDef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getName()
   * @see #getTeamDef()
   * @generated
   */
  EAttribute getTeamDef_Name();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getTeamDefOption <em>Team Def Option</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Team Def Option</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getTeamDefOption()
   * @see #getTeamDef()
   * @generated
   */
  EAttribute getTeamDef_TeamDefOption();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getUuid <em>Uuid</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Uuid</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getUuid()
   * @see #getTeamDef()
   * @generated
   */
  EAttribute getTeamDef_Uuid();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getActive <em>Active</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Active</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getActive()
   * @see #getTeamDef()
   * @generated
   */
  EAttribute getTeamDef_Active();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getStaticId <em>Static Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Static Id</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getStaticId()
   * @see #getTeamDef()
   * @generated
   */
  EAttribute getTeamDef_StaticId();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getLead <em>Lead</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Lead</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getLead()
   * @see #getTeamDef()
   * @generated
   */
  EReference getTeamDef_Lead();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getMember <em>Member</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Member</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getMember()
   * @see #getTeamDef()
   * @generated
   */
  EReference getTeamDef_Member();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getPrivileged <em>Privileged</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Privileged</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getPrivileged()
   * @see #getTeamDef()
   * @generated
   */
  EReference getTeamDef_Privileged();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getWorkDefinition <em>Work Definition</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Work Definition</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getWorkDefinition()
   * @see #getTeamDef()
   * @generated
   */
  EAttribute getTeamDef_WorkDefinition();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getRelatedTaskWorkDefinition <em>Related Task Work Definition</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Related Task Work Definition</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getRelatedTaskWorkDefinition()
   * @see #getTeamDef()
   * @generated
   */
  EAttribute getTeamDef_RelatedTaskWorkDefinition();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getTeamWorkflowArtifactType <em>Team Workflow Artifact Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Team Workflow Artifact Type</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getTeamWorkflowArtifactType()
   * @see #getTeamDef()
   * @generated
   */
  EAttribute getTeamDef_TeamWorkflowArtifactType();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getAccessContextId <em>Access Context Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Access Context Id</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getAccessContextId()
   * @see #getTeamDef()
   * @generated
   */
  EAttribute getTeamDef_AccessContextId();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getVersion <em>Version</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Version</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getVersion()
   * @see #getTeamDef()
   * @generated
   */
  EReference getTeamDef_Version();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getRules <em>Rules</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Rules</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getRules()
   * @see #getTeamDef()
   * @generated
   */
  EAttribute getTeamDef_Rules();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getChildren <em>Children</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Children</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.TeamDef#getChildren()
   * @see #getTeamDef()
   * @generated
   */
  EReference getTeamDef_Children();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef <em>Actionable Item Def</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Actionable Item Def</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef
   * @generated
   */
  EClass getActionableItemDef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef#getName()
   * @see #getActionableItemDef()
   * @generated
   */
  EAttribute getActionableItemDef_Name();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef#getAiDefOption <em>Ai Def Option</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Ai Def Option</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef#getAiDefOption()
   * @see #getActionableItemDef()
   * @generated
   */
  EAttribute getActionableItemDef_AiDefOption();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef#getUuid <em>Uuid</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Uuid</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef#getUuid()
   * @see #getActionableItemDef()
   * @generated
   */
  EAttribute getActionableItemDef_Uuid();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef#getActive <em>Active</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Active</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef#getActive()
   * @see #getActionableItemDef()
   * @generated
   */
  EAttribute getActionableItemDef_Active();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef#getActionable <em>Actionable</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Actionable</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef#getActionable()
   * @see #getActionableItemDef()
   * @generated
   */
  EAttribute getActionableItemDef_Actionable();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef#getLead <em>Lead</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Lead</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef#getLead()
   * @see #getActionableItemDef()
   * @generated
   */
  EReference getActionableItemDef_Lead();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef#getOwner <em>Owner</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Owner</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef#getOwner()
   * @see #getActionableItemDef()
   * @generated
   */
  EReference getActionableItemDef_Owner();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef#getStaticId <em>Static Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Static Id</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef#getStaticId()
   * @see #getActionableItemDef()
   * @generated
   */
  EAttribute getActionableItemDef_StaticId();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef#getTeamDef <em>Team Def</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Team Def</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef#getTeamDef()
   * @see #getActionableItemDef()
   * @generated
   */
  EAttribute getActionableItemDef_TeamDef();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef#getAccessContextId <em>Access Context Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Access Context Id</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef#getAccessContextId()
   * @see #getActionableItemDef()
   * @generated
   */
  EAttribute getActionableItemDef_AccessContextId();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef#getRules <em>Rules</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Rules</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef#getRules()
   * @see #getActionableItemDef()
   * @generated
   */
  EAttribute getActionableItemDef_Rules();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef#getChildren <em>Children</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Children</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef#getChildren()
   * @see #getActionableItemDef()
   * @generated
   */
  EReference getActionableItemDef_Children();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef <em>Version Def</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Version Def</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.VersionDef
   * @generated
   */
  EClass getVersionDef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getName()
   * @see #getVersionDef()
   * @generated
   */
  EAttribute getVersionDef_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getUuid <em>Uuid</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Uuid</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getUuid()
   * @see #getVersionDef()
   * @generated
   */
  EAttribute getVersionDef_Uuid();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getStaticId <em>Static Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Static Id</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getStaticId()
   * @see #getVersionDef()
   * @generated
   */
  EAttribute getVersionDef_StaticId();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getNext <em>Next</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Next</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getNext()
   * @see #getVersionDef()
   * @generated
   */
  EAttribute getVersionDef_Next();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getReleased <em>Released</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Released</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getReleased()
   * @see #getVersionDef()
   * @generated
   */
  EAttribute getVersionDef_Released();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getAllowCreateBranch <em>Allow Create Branch</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Allow Create Branch</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getAllowCreateBranch()
   * @see #getVersionDef()
   * @generated
   */
  EAttribute getVersionDef_AllowCreateBranch();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getAllowCommitBranch <em>Allow Commit Branch</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Allow Commit Branch</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getAllowCommitBranch()
   * @see #getVersionDef()
   * @generated
   */
  EAttribute getVersionDef_AllowCommitBranch();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getBaselineBranchUuid <em>Baseline Branch Uuid</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Baseline Branch Uuid</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getBaselineBranchUuid()
   * @see #getVersionDef()
   * @generated
   */
  EAttribute getVersionDef_BaselineBranchUuid();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getParallelVersion <em>Parallel Version</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Parallel Version</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getParallelVersion()
   * @see #getVersionDef()
   * @generated
   */
  EAttribute getVersionDef_ParallelVersion();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.WorkDef <em>Work Def</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Work Def</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WorkDef
   * @generated
   */
  EClass getWorkDef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.WorkDef#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WorkDef#getName()
   * @see #getWorkDef()
   * @generated
   */
  EAttribute getWorkDef_Name();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.ats.dsl.atsDsl.WorkDef#getId <em>Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Id</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WorkDef#getId()
   * @see #getWorkDef()
   * @generated
   */
  EAttribute getWorkDef_Id();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.ats.dsl.atsDsl.WorkDef#getStartState <em>Start State</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Start State</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WorkDef#getStartState()
   * @see #getWorkDef()
   * @generated
   */
  EReference getWorkDef_StartState();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.WorkDef#getWidgetDefs <em>Widget Defs</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Widget Defs</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WorkDef#getWidgetDefs()
   * @see #getWorkDef()
   * @generated
   */
  EReference getWorkDef_WidgetDefs();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.WorkDef#getDecisionReviewDefs <em>Decision Review Defs</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Decision Review Defs</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WorkDef#getDecisionReviewDefs()
   * @see #getWorkDef()
   * @generated
   */
  EReference getWorkDef_DecisionReviewDefs();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.WorkDef#getPeerReviewDefs <em>Peer Review Defs</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Peer Review Defs</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WorkDef#getPeerReviewDefs()
   * @see #getWorkDef()
   * @generated
   */
  EReference getWorkDef_PeerReviewDefs();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.WorkDef#getStates <em>States</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>States</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WorkDef#getStates()
   * @see #getWorkDef()
   * @generated
   */
  EReference getWorkDef_States();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef <em>Widget Def</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Widget Def</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WidgetDef
   * @generated
   */
  EClass getWidgetDef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getName()
   * @see #getWidgetDef()
   * @generated
   */
  EAttribute getWidgetDef_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getAttributeName <em>Attribute Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attribute Name</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getAttributeName()
   * @see #getWidgetDef()
   * @generated
   */
  EAttribute getWidgetDef_AttributeName();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getDescription <em>Description</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Description</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getDescription()
   * @see #getWidgetDef()
   * @generated
   */
  EAttribute getWidgetDef_Description();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getXWidgetName <em>XWidget Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>XWidget Name</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getXWidgetName()
   * @see #getWidgetDef()
   * @generated
   */
  EAttribute getWidgetDef_XWidgetName();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getDefaultValue <em>Default Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Default Value</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getDefaultValue()
   * @see #getWidgetDef()
   * @generated
   */
  EAttribute getWidgetDef_DefaultValue();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getHeight <em>Height</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Height</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getHeight()
   * @see #getWidgetDef()
   * @generated
   */
  EAttribute getWidgetDef_Height();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getOption <em>Option</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Option</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getOption()
   * @see #getWidgetDef()
   * @generated
   */
  EAttribute getWidgetDef_Option();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getMinConstraint <em>Min Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Min Constraint</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getMinConstraint()
   * @see #getWidgetDef()
   * @generated
   */
  EAttribute getWidgetDef_MinConstraint();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getMaxConstraint <em>Max Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Max Constraint</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getMaxConstraint()
   * @see #getWidgetDef()
   * @generated
   */
  EAttribute getWidgetDef_MaxConstraint();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetRef <em>Widget Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Widget Ref</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WidgetRef
   * @generated
   */
  EClass getWidgetRef();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetRef#getWidget <em>Widget</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Widget</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WidgetRef#getWidget()
   * @see #getWidgetRef()
   * @generated
   */
  EReference getWidgetRef_Widget();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.AttrWidget <em>Attr Widget</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attr Widget</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AttrWidget
   * @generated
   */
  EClass getAttrWidget();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.AttrWidget#getAttributeName <em>Attribute Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attribute Name</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AttrWidget#getAttributeName()
   * @see #getAttrWidget()
   * @generated
   */
  EAttribute getAttrWidget_AttributeName();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.ats.dsl.atsDsl.AttrWidget#getOption <em>Option</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Option</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AttrWidget#getOption()
   * @see #getAttrWidget()
   * @generated
   */
  EAttribute getAttrWidget_Option();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef <em>State Def</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>State Def</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.StateDef
   * @generated
   */
  EClass getStateDef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.StateDef#getName()
   * @see #getStateDef()
   * @generated
   */
  EAttribute getStateDef_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getDescription <em>Description</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Description</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.StateDef#getDescription()
   * @see #getStateDef()
   * @generated
   */
  EAttribute getStateDef_Description();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getPageType <em>Page Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Page Type</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.StateDef#getPageType()
   * @see #getStateDef()
   * @generated
   */
  EAttribute getStateDef_PageType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getOrdinal <em>Ordinal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Ordinal</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.StateDef#getOrdinal()
   * @see #getStateDef()
   * @generated
   */
  EAttribute getStateDef_Ordinal();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getTransitionStates <em>Transition States</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Transition States</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.StateDef#getTransitionStates()
   * @see #getStateDef()
   * @generated
   */
  EReference getStateDef_TransitionStates();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getRules <em>Rules</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Rules</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.StateDef#getRules()
   * @see #getStateDef()
   * @generated
   */
  EAttribute getStateDef_Rules();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getDecisionReviews <em>Decision Reviews</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Decision Reviews</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.StateDef#getDecisionReviews()
   * @see #getStateDef()
   * @generated
   */
  EReference getStateDef_DecisionReviews();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getPeerReviews <em>Peer Reviews</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Peer Reviews</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.StateDef#getPeerReviews()
   * @see #getStateDef()
   * @generated
   */
  EReference getStateDef_PeerReviews();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getPercentWeight <em>Percent Weight</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Percent Weight</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.StateDef#getPercentWeight()
   * @see #getStateDef()
   * @generated
   */
  EAttribute getStateDef_PercentWeight();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getRecommendedPercentComplete <em>Recommended Percent Complete</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Recommended Percent Complete</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.StateDef#getRecommendedPercentComplete()
   * @see #getStateDef()
   * @generated
   */
  EAttribute getStateDef_RecommendedPercentComplete();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getColor <em>Color</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Color</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.StateDef#getColor()
   * @see #getStateDef()
   * @generated
   */
  EAttribute getStateDef_Color();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getLayout <em>Layout</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Layout</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.StateDef#getLayout()
   * @see #getStateDef()
   * @generated
   */
  EReference getStateDef_Layout();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewRef <em>Decision Review Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Decision Review Ref</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewRef
   * @generated
   */
  EClass getDecisionReviewRef();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewRef#getDecisionReview <em>Decision Review</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Decision Review</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewRef#getDecisionReview()
   * @see #getDecisionReviewRef()
   * @generated
   */
  EReference getDecisionReviewRef_DecisionReview();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef <em>Decision Review Def</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Decision Review Def</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef
   * @generated
   */
  EClass getDecisionReviewDef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef#getName()
   * @see #getDecisionReviewDef()
   * @generated
   */
  EAttribute getDecisionReviewDef_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef#getTitle <em>Title</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Title</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef#getTitle()
   * @see #getDecisionReviewDef()
   * @generated
   */
  EAttribute getDecisionReviewDef_Title();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef#getDescription <em>Description</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Description</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef#getDescription()
   * @see #getDecisionReviewDef()
   * @generated
   */
  EAttribute getDecisionReviewDef_Description();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef#getRelatedToState <em>Related To State</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Related To State</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef#getRelatedToState()
   * @see #getDecisionReviewDef()
   * @generated
   */
  EReference getDecisionReviewDef_RelatedToState();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef#getBlockingType <em>Blocking Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Blocking Type</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef#getBlockingType()
   * @see #getDecisionReviewDef()
   * @generated
   */
  EAttribute getDecisionReviewDef_BlockingType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef#getStateEvent <em>State Event</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>State Event</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef#getStateEvent()
   * @see #getDecisionReviewDef()
   * @generated
   */
  EAttribute getDecisionReviewDef_StateEvent();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef#getAssigneeRefs <em>Assignee Refs</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Assignee Refs</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef#getAssigneeRefs()
   * @see #getDecisionReviewDef()
   * @generated
   */
  EReference getDecisionReviewDef_AssigneeRefs();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef#getAutoTransitionToDecision <em>Auto Transition To Decision</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Auto Transition To Decision</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef#getAutoTransitionToDecision()
   * @see #getDecisionReviewDef()
   * @generated
   */
  EAttribute getDecisionReviewDef_AutoTransitionToDecision();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef#getOptions <em>Options</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Options</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef#getOptions()
   * @see #getDecisionReviewDef()
   * @generated
   */
  EReference getDecisionReviewDef_Options();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewOpt <em>Decision Review Opt</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Decision Review Opt</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewOpt
   * @generated
   */
  EClass getDecisionReviewOpt();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewOpt#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewOpt#getName()
   * @see #getDecisionReviewOpt()
   * @generated
   */
  EAttribute getDecisionReviewOpt_Name();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewOpt#getFollowup <em>Followup</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Followup</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewOpt#getFollowup()
   * @see #getDecisionReviewOpt()
   * @generated
   */
  EReference getDecisionReviewOpt_Followup();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewRef <em>Peer Review Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Peer Review Ref</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.PeerReviewRef
   * @generated
   */
  EClass getPeerReviewRef();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewRef#getPeerReview <em>Peer Review</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Peer Review</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.PeerReviewRef#getPeerReview()
   * @see #getPeerReviewRef()
   * @generated
   */
  EReference getPeerReviewRef_PeerReview();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef <em>Peer Review Def</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Peer Review Def</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef
   * @generated
   */
  EClass getPeerReviewDef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getName()
   * @see #getPeerReviewDef()
   * @generated
   */
  EAttribute getPeerReviewDef_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getTitle <em>Title</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Title</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getTitle()
   * @see #getPeerReviewDef()
   * @generated
   */
  EAttribute getPeerReviewDef_Title();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getDescription <em>Description</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Description</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getDescription()
   * @see #getPeerReviewDef()
   * @generated
   */
  EAttribute getPeerReviewDef_Description();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getLocation <em>Location</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Location</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getLocation()
   * @see #getPeerReviewDef()
   * @generated
   */
  EAttribute getPeerReviewDef_Location();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getRelatedToState <em>Related To State</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Related To State</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getRelatedToState()
   * @see #getPeerReviewDef()
   * @generated
   */
  EReference getPeerReviewDef_RelatedToState();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getBlockingType <em>Blocking Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Blocking Type</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getBlockingType()
   * @see #getPeerReviewDef()
   * @generated
   */
  EAttribute getPeerReviewDef_BlockingType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getStateEvent <em>State Event</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>State Event</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getStateEvent()
   * @see #getPeerReviewDef()
   * @generated
   */
  EAttribute getPeerReviewDef_StateEvent();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getAssigneeRefs <em>Assignee Refs</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Assignee Refs</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getAssigneeRefs()
   * @see #getPeerReviewDef()
   * @generated
   */
  EReference getPeerReviewDef_AssigneeRefs();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.FollowupRef <em>Followup Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Followup Ref</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.FollowupRef
   * @generated
   */
  EClass getFollowupRef();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.FollowupRef#getAssigneeRefs <em>Assignee Refs</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Assignee Refs</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.FollowupRef#getAssigneeRefs()
   * @see #getFollowupRef()
   * @generated
   */
  EReference getFollowupRef_AssigneeRefs();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.UserRef <em>User Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>User Ref</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.UserRef
   * @generated
   */
  EClass getUserRef();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.UserByUserId <em>User By User Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>User By User Id</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.UserByUserId
   * @generated
   */
  EClass getUserByUserId();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.UserByUserId#getUserId <em>User Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>User Id</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.UserByUserId#getUserId()
   * @see #getUserByUserId()
   * @generated
   */
  EAttribute getUserByUserId_UserId();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.UserByName <em>User By Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>User By Name</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.UserByName
   * @generated
   */
  EClass getUserByName();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.UserByName#getUserName <em>User Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>User Name</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.UserByName#getUserName()
   * @see #getUserByName()
   * @generated
   */
  EAttribute getUserByName_UserName();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.ToState <em>To State</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>To State</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ToState
   * @generated
   */
  EClass getToState();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.ats.dsl.atsDsl.ToState#getState <em>State</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>State</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ToState#getState()
   * @see #getToState()
   * @generated
   */
  EReference getToState_State();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.ats.dsl.atsDsl.ToState#getOptions <em>Options</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Options</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ToState#getOptions()
   * @see #getToState()
   * @generated
   */
  EAttribute getToState_Options();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.LayoutType <em>Layout Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Layout Type</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.LayoutType
   * @generated
   */
  EClass getLayoutType();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.LayoutDef <em>Layout Def</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Layout Def</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.LayoutDef
   * @generated
   */
  EClass getLayoutDef();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.LayoutDef#getLayoutItems <em>Layout Items</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Layout Items</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.LayoutDef#getLayoutItems()
   * @see #getLayoutDef()
   * @generated
   */
  EReference getLayoutDef_LayoutItems();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.LayoutCopy <em>Layout Copy</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Layout Copy</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.LayoutCopy
   * @generated
   */
  EClass getLayoutCopy();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.ats.dsl.atsDsl.LayoutCopy#getState <em>State</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>State</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.LayoutCopy#getState()
   * @see #getLayoutCopy()
   * @generated
   */
  EReference getLayoutCopy_State();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.LayoutItem <em>Layout Item</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Layout Item</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.LayoutItem
   * @generated
   */
  EClass getLayoutItem();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.Composite <em>Composite</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Composite</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.Composite
   * @generated
   */
  EClass getComposite();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.Composite#getNumColumns <em>Num Columns</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Num Columns</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.Composite#getNumColumns()
   * @see #getComposite()
   * @generated
   */
  EAttribute getComposite_NumColumns();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.Composite#getLayoutItems <em>Layout Items</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Layout Items</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.Composite#getLayoutItems()
   * @see #getComposite()
   * @generated
   */
  EReference getComposite_LayoutItems();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.ats.dsl.atsDsl.Composite#getOptions <em>Options</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Options</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.Composite#getOptions()
   * @see #getComposite()
   * @generated
   */
  EAttribute getComposite_Options();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.RuleDef <em>Rule Def</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Rule Def</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.RuleDef
   * @generated
   */
  EClass getRuleDef();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.CreateTaskRule <em>Create Task Rule</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Create Task Rule</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.CreateTaskRule
   * @generated
   */
  EClass getCreateTaskRule();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.CreateTaskRule#getAssignees <em>Assignees</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Assignees</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.CreateTaskRule#getAssignees()
   * @see #getCreateTaskRule()
   * @generated
   */
  EReference getCreateTaskRule_Assignees();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.CreateTaskRule#getRelatedState <em>Related State</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Related State</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.CreateTaskRule#getRelatedState()
   * @see #getCreateTaskRule()
   * @generated
   */
  EAttribute getCreateTaskRule_RelatedState();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.CreateTaskRule#getTaskWorkDef <em>Task Work Def</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Task Work Def</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.CreateTaskRule#getTaskWorkDef()
   * @see #getCreateTaskRule()
   * @generated
   */
  EAttribute getCreateTaskRule_TaskWorkDef();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.ats.dsl.atsDsl.CreateTaskRule#getOnEvent <em>On Event</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>On Event</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.CreateTaskRule#getOnEvent()
   * @see #getCreateTaskRule()
   * @generated
   */
  EAttribute getCreateTaskRule_OnEvent();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.CreateTaskRule#getAttributes <em>Attributes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Attributes</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.CreateTaskRule#getAttributes()
   * @see #getCreateTaskRule()
   * @generated
   */
  EReference getCreateTaskRule_Attributes();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.CreateDecisionReviewRule <em>Create Decision Review Rule</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Create Decision Review Rule</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.CreateDecisionReviewRule
   * @generated
   */
  EClass getCreateDecisionReviewRule();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.CreateDecisionReviewRule#getAutoTransitionToDecision <em>Auto Transition To Decision</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Auto Transition To Decision</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.CreateDecisionReviewRule#getAutoTransitionToDecision()
   * @see #getCreateDecisionReviewRule()
   * @generated
   */
  EAttribute getCreateDecisionReviewRule_AutoTransitionToDecision();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.CreateDecisionReviewRule#getOptions <em>Options</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Options</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.CreateDecisionReviewRule#getOptions()
   * @see #getCreateDecisionReviewRule()
   * @generated
   */
  EReference getCreateDecisionReviewRule_Options();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.CreatePeerReviewRule <em>Create Peer Review Rule</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Create Peer Review Rule</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.CreatePeerReviewRule
   * @generated
   */
  EClass getCreatePeerReviewRule();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.CreatePeerReviewRule#getLocation <em>Location</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Location</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.CreatePeerReviewRule#getLocation()
   * @see #getCreatePeerReviewRule()
   * @generated
   */
  EAttribute getCreatePeerReviewRule_Location();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.ReviewRule <em>Review Rule</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Review Rule</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ReviewRule
   * @generated
   */
  EClass getReviewRule();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.ReviewRule#getAssignees <em>Assignees</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Assignees</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ReviewRule#getAssignees()
   * @see #getReviewRule()
   * @generated
   */
  EReference getReviewRule_Assignees();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.ReviewRule#getRelatedToState <em>Related To State</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Related To State</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ReviewRule#getRelatedToState()
   * @see #getReviewRule()
   * @generated
   */
  EAttribute getReviewRule_RelatedToState();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.ReviewRule#getBlockingType <em>Blocking Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Blocking Type</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ReviewRule#getBlockingType()
   * @see #getReviewRule()
   * @generated
   */
  EAttribute getReviewRule_BlockingType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.ReviewRule#getStateEvent <em>State Event</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>State Event</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ReviewRule#getStateEvent()
   * @see #getReviewRule()
   * @generated
   */
  EAttribute getReviewRule_StateEvent();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.ats.dsl.atsDsl.ReviewRule#getAttributes <em>Attributes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Attributes</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ReviewRule#getAttributes()
   * @see #getReviewRule()
   * @generated
   */
  EReference getReviewRule_Attributes();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.ats.dsl.atsDsl.Rule <em>Rule</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Rule</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.Rule
   * @generated
   */
  EClass getRule();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.Rule#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.Rule#getName()
   * @see #getRule()
   * @generated
   */
  EAttribute getRule_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.Rule#getTitle <em>Title</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Title</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.Rule#getTitle()
   * @see #getRule()
   * @generated
   */
  EAttribute getRule_Title();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.ats.dsl.atsDsl.Rule#getDescription <em>Description</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Description</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.Rule#getDescription()
   * @see #getRule()
   * @generated
   */
  EAttribute getRule_Description();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.ats.dsl.atsDsl.Rule#getRuleLocation <em>Rule Location</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Rule Location</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.Rule#getRuleLocation()
   * @see #getRule()
   * @generated
   */
  EAttribute getRule_RuleLocation();

  /**
   * Returns the meta object for enum '{@link org.eclipse.osee.ats.dsl.atsDsl.OnEventType <em>On Event Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>On Event Type</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.OnEventType
   * @generated
   */
  EEnum getOnEventType();

  /**
   * Returns the meta object for enum '{@link org.eclipse.osee.ats.dsl.atsDsl.BooleanDef <em>Boolean Def</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Boolean Def</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.BooleanDef
   * @generated
   */
  EEnum getBooleanDef();

  /**
   * Returns the meta object for enum '{@link org.eclipse.osee.ats.dsl.atsDsl.WorkflowEventType <em>Workflow Event Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Workflow Event Type</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WorkflowEventType
   * @generated
   */
  EEnum getWorkflowEventType();

  /**
   * Returns the meta object for enum '{@link org.eclipse.osee.ats.dsl.atsDsl.ReviewBlockingType <em>Review Blocking Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Review Blocking Type</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ReviewBlockingType
   * @generated
   */
  EEnum getReviewBlockingType();

  /**
   * Returns the meta object for enum '{@link org.eclipse.osee.ats.dsl.atsDsl.RuleLocation <em>Rule Location</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Rule Location</em>'.
   * @see org.eclipse.osee.ats.dsl.atsDsl.RuleLocation
   * @generated
   */
  EEnum getRuleLocation();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  AtsDslFactory getAtsDslFactory();

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
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslImpl <em>Ats Dsl</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getAtsDsl()
     * @generated
     */
    EClass ATS_DSL = eINSTANCE.getAtsDsl();

    /**
     * The meta object literal for the '<em><b>User Def</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATS_DSL__USER_DEF = eINSTANCE.getAtsDsl_UserDef();

    /**
     * The meta object literal for the '<em><b>Team Def</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATS_DSL__TEAM_DEF = eINSTANCE.getAtsDsl_TeamDef();

    /**
     * The meta object literal for the '<em><b>Actionable Item Def</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATS_DSL__ACTIONABLE_ITEM_DEF = eINSTANCE.getAtsDsl_ActionableItemDef();

    /**
     * The meta object literal for the '<em><b>Work Def</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATS_DSL__WORK_DEF = eINSTANCE.getAtsDsl_WorkDef();

    /**
     * The meta object literal for the '<em><b>Program</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATS_DSL__PROGRAM = eINSTANCE.getAtsDsl_Program();

    /**
     * The meta object literal for the '<em><b>Rule</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATS_DSL__RULE = eINSTANCE.getAtsDsl_Rule();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.UserDefImpl <em>User Def</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.UserDefImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getUserDef()
     * @generated
     */
    EClass USER_DEF = eINSTANCE.getUserDef();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute USER_DEF__NAME = eINSTANCE.getUserDef_Name();

    /**
     * The meta object literal for the '<em><b>User Def Option</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute USER_DEF__USER_DEF_OPTION = eINSTANCE.getUserDef_UserDefOption();

    /**
     * The meta object literal for the '<em><b>Active</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute USER_DEF__ACTIVE = eINSTANCE.getUserDef_Active();

    /**
     * The meta object literal for the '<em><b>User Id</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute USER_DEF__USER_ID = eINSTANCE.getUserDef_UserId();

    /**
     * The meta object literal for the '<em><b>Email</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute USER_DEF__EMAIL = eINSTANCE.getUserDef_Email();

    /**
     * The meta object literal for the '<em><b>Admin</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute USER_DEF__ADMIN = eINSTANCE.getUserDef_Admin();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.AttrDefImpl <em>Attr Def</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AttrDefImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getAttrDef()
     * @generated
     */
    EClass ATTR_DEF = eINSTANCE.getAttrDef();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTR_DEF__NAME = eINSTANCE.getAttrDef_Name();

    /**
     * The meta object literal for the '<em><b>Option</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTR_DEF__OPTION = eINSTANCE.getAttrDef_Option();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.AttrDefOptionsImpl <em>Attr Def Options</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AttrDefOptionsImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getAttrDefOptions()
     * @generated
     */
    EClass ATTR_DEF_OPTIONS = eINSTANCE.getAttrDefOptions();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.AttrValueDefImpl <em>Attr Value Def</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AttrValueDefImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getAttrValueDef()
     * @generated
     */
    EClass ATTR_VALUE_DEF = eINSTANCE.getAttrValueDef();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTR_VALUE_DEF__VALUE = eINSTANCE.getAttrValueDef_Value();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.AttrFullDefImpl <em>Attr Full Def</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AttrFullDefImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getAttrFullDef()
     * @generated
     */
    EClass ATTR_FULL_DEF = eINSTANCE.getAttrFullDef();

    /**
     * The meta object literal for the '<em><b>Uuid</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTR_FULL_DEF__UUID = eINSTANCE.getAttrFullDef_Uuid();

    /**
     * The meta object literal for the '<em><b>Values</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTR_FULL_DEF__VALUES = eINSTANCE.getAttrFullDef_Values();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ProgramDefImpl <em>Program Def</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.ProgramDefImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getProgramDef()
     * @generated
     */
    EClass PROGRAM_DEF = eINSTANCE.getProgramDef();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PROGRAM_DEF__NAME = eINSTANCE.getProgramDef_Name();

    /**
     * The meta object literal for the '<em><b>Program Def Option</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PROGRAM_DEF__PROGRAM_DEF_OPTION = eINSTANCE.getProgramDef_ProgramDefOption();

    /**
     * The meta object literal for the '<em><b>Uuid</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PROGRAM_DEF__UUID = eINSTANCE.getProgramDef_Uuid();

    /**
     * The meta object literal for the '<em><b>Artifact Type Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PROGRAM_DEF__ARTIFACT_TYPE_NAME = eINSTANCE.getProgramDef_ArtifactTypeName();

    /**
     * The meta object literal for the '<em><b>Active</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PROGRAM_DEF__ACTIVE = eINSTANCE.getProgramDef_Active();

    /**
     * The meta object literal for the '<em><b>Namespace</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PROGRAM_DEF__NAMESPACE = eINSTANCE.getProgramDef_Namespace();

    /**
     * The meta object literal for the '<em><b>Team Definition</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PROGRAM_DEF__TEAM_DEFINITION = eINSTANCE.getProgramDef_TeamDefinition();

    /**
     * The meta object literal for the '<em><b>Attributes</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PROGRAM_DEF__ATTRIBUTES = eINSTANCE.getProgramDef_Attributes();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.TeamDefImpl <em>Team Def</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.TeamDefImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getTeamDef()
     * @generated
     */
    EClass TEAM_DEF = eINSTANCE.getTeamDef();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TEAM_DEF__NAME = eINSTANCE.getTeamDef_Name();

    /**
     * The meta object literal for the '<em><b>Team Def Option</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TEAM_DEF__TEAM_DEF_OPTION = eINSTANCE.getTeamDef_TeamDefOption();

    /**
     * The meta object literal for the '<em><b>Uuid</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TEAM_DEF__UUID = eINSTANCE.getTeamDef_Uuid();

    /**
     * The meta object literal for the '<em><b>Active</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TEAM_DEF__ACTIVE = eINSTANCE.getTeamDef_Active();

    /**
     * The meta object literal for the '<em><b>Static Id</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TEAM_DEF__STATIC_ID = eINSTANCE.getTeamDef_StaticId();

    /**
     * The meta object literal for the '<em><b>Lead</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TEAM_DEF__LEAD = eINSTANCE.getTeamDef_Lead();

    /**
     * The meta object literal for the '<em><b>Member</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TEAM_DEF__MEMBER = eINSTANCE.getTeamDef_Member();

    /**
     * The meta object literal for the '<em><b>Privileged</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TEAM_DEF__PRIVILEGED = eINSTANCE.getTeamDef_Privileged();

    /**
     * The meta object literal for the '<em><b>Work Definition</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TEAM_DEF__WORK_DEFINITION = eINSTANCE.getTeamDef_WorkDefinition();

    /**
     * The meta object literal for the '<em><b>Related Task Work Definition</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TEAM_DEF__RELATED_TASK_WORK_DEFINITION = eINSTANCE.getTeamDef_RelatedTaskWorkDefinition();

    /**
     * The meta object literal for the '<em><b>Team Workflow Artifact Type</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TEAM_DEF__TEAM_WORKFLOW_ARTIFACT_TYPE = eINSTANCE.getTeamDef_TeamWorkflowArtifactType();

    /**
     * The meta object literal for the '<em><b>Access Context Id</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TEAM_DEF__ACCESS_CONTEXT_ID = eINSTANCE.getTeamDef_AccessContextId();

    /**
     * The meta object literal for the '<em><b>Version</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TEAM_DEF__VERSION = eINSTANCE.getTeamDef_Version();

    /**
     * The meta object literal for the '<em><b>Rules</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TEAM_DEF__RULES = eINSTANCE.getTeamDef_Rules();

    /**
     * The meta object literal for the '<em><b>Children</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TEAM_DEF__CHILDREN = eINSTANCE.getTeamDef_Children();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ActionableItemDefImpl <em>Actionable Item Def</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.ActionableItemDefImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getActionableItemDef()
     * @generated
     */
    EClass ACTIONABLE_ITEM_DEF = eINSTANCE.getActionableItemDef();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ACTIONABLE_ITEM_DEF__NAME = eINSTANCE.getActionableItemDef_Name();

    /**
     * The meta object literal for the '<em><b>Ai Def Option</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ACTIONABLE_ITEM_DEF__AI_DEF_OPTION = eINSTANCE.getActionableItemDef_AiDefOption();

    /**
     * The meta object literal for the '<em><b>Uuid</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ACTIONABLE_ITEM_DEF__UUID = eINSTANCE.getActionableItemDef_Uuid();

    /**
     * The meta object literal for the '<em><b>Active</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ACTIONABLE_ITEM_DEF__ACTIVE = eINSTANCE.getActionableItemDef_Active();

    /**
     * The meta object literal for the '<em><b>Actionable</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ACTIONABLE_ITEM_DEF__ACTIONABLE = eINSTANCE.getActionableItemDef_Actionable();

    /**
     * The meta object literal for the '<em><b>Lead</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ACTIONABLE_ITEM_DEF__LEAD = eINSTANCE.getActionableItemDef_Lead();

    /**
     * The meta object literal for the '<em><b>Owner</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ACTIONABLE_ITEM_DEF__OWNER = eINSTANCE.getActionableItemDef_Owner();

    /**
     * The meta object literal for the '<em><b>Static Id</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ACTIONABLE_ITEM_DEF__STATIC_ID = eINSTANCE.getActionableItemDef_StaticId();

    /**
     * The meta object literal for the '<em><b>Team Def</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ACTIONABLE_ITEM_DEF__TEAM_DEF = eINSTANCE.getActionableItemDef_TeamDef();

    /**
     * The meta object literal for the '<em><b>Access Context Id</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ACTIONABLE_ITEM_DEF__ACCESS_CONTEXT_ID = eINSTANCE.getActionableItemDef_AccessContextId();

    /**
     * The meta object literal for the '<em><b>Rules</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ACTIONABLE_ITEM_DEF__RULES = eINSTANCE.getActionableItemDef_Rules();

    /**
     * The meta object literal for the '<em><b>Children</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ACTIONABLE_ITEM_DEF__CHILDREN = eINSTANCE.getActionableItemDef_Children();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.VersionDefImpl <em>Version Def</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.VersionDefImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getVersionDef()
     * @generated
     */
    EClass VERSION_DEF = eINSTANCE.getVersionDef();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute VERSION_DEF__NAME = eINSTANCE.getVersionDef_Name();

    /**
     * The meta object literal for the '<em><b>Uuid</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute VERSION_DEF__UUID = eINSTANCE.getVersionDef_Uuid();

    /**
     * The meta object literal for the '<em><b>Static Id</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute VERSION_DEF__STATIC_ID = eINSTANCE.getVersionDef_StaticId();

    /**
     * The meta object literal for the '<em><b>Next</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute VERSION_DEF__NEXT = eINSTANCE.getVersionDef_Next();

    /**
     * The meta object literal for the '<em><b>Released</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute VERSION_DEF__RELEASED = eINSTANCE.getVersionDef_Released();

    /**
     * The meta object literal for the '<em><b>Allow Create Branch</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute VERSION_DEF__ALLOW_CREATE_BRANCH = eINSTANCE.getVersionDef_AllowCreateBranch();

    /**
     * The meta object literal for the '<em><b>Allow Commit Branch</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute VERSION_DEF__ALLOW_COMMIT_BRANCH = eINSTANCE.getVersionDef_AllowCommitBranch();

    /**
     * The meta object literal for the '<em><b>Baseline Branch Uuid</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute VERSION_DEF__BASELINE_BRANCH_UUID = eINSTANCE.getVersionDef_BaselineBranchUuid();

    /**
     * The meta object literal for the '<em><b>Parallel Version</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute VERSION_DEF__PARALLEL_VERSION = eINSTANCE.getVersionDef_ParallelVersion();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.WorkDefImpl <em>Work Def</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.WorkDefImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getWorkDef()
     * @generated
     */
    EClass WORK_DEF = eINSTANCE.getWorkDef();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute WORK_DEF__NAME = eINSTANCE.getWorkDef_Name();

    /**
     * The meta object literal for the '<em><b>Id</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute WORK_DEF__ID = eINSTANCE.getWorkDef_Id();

    /**
     * The meta object literal for the '<em><b>Start State</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference WORK_DEF__START_STATE = eINSTANCE.getWorkDef_StartState();

    /**
     * The meta object literal for the '<em><b>Widget Defs</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference WORK_DEF__WIDGET_DEFS = eINSTANCE.getWorkDef_WidgetDefs();

    /**
     * The meta object literal for the '<em><b>Decision Review Defs</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference WORK_DEF__DECISION_REVIEW_DEFS = eINSTANCE.getWorkDef_DecisionReviewDefs();

    /**
     * The meta object literal for the '<em><b>Peer Review Defs</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference WORK_DEF__PEER_REVIEW_DEFS = eINSTANCE.getWorkDef_PeerReviewDefs();

    /**
     * The meta object literal for the '<em><b>States</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference WORK_DEF__STATES = eINSTANCE.getWorkDef_States();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.WidgetDefImpl <em>Widget Def</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.WidgetDefImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getWidgetDef()
     * @generated
     */
    EClass WIDGET_DEF = eINSTANCE.getWidgetDef();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute WIDGET_DEF__NAME = eINSTANCE.getWidgetDef_Name();

    /**
     * The meta object literal for the '<em><b>Attribute Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute WIDGET_DEF__ATTRIBUTE_NAME = eINSTANCE.getWidgetDef_AttributeName();

    /**
     * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute WIDGET_DEF__DESCRIPTION = eINSTANCE.getWidgetDef_Description();

    /**
     * The meta object literal for the '<em><b>XWidget Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute WIDGET_DEF__XWIDGET_NAME = eINSTANCE.getWidgetDef_XWidgetName();

    /**
     * The meta object literal for the '<em><b>Default Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute WIDGET_DEF__DEFAULT_VALUE = eINSTANCE.getWidgetDef_DefaultValue();

    /**
     * The meta object literal for the '<em><b>Height</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute WIDGET_DEF__HEIGHT = eINSTANCE.getWidgetDef_Height();

    /**
     * The meta object literal for the '<em><b>Option</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute WIDGET_DEF__OPTION = eINSTANCE.getWidgetDef_Option();

    /**
     * The meta object literal for the '<em><b>Min Constraint</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute WIDGET_DEF__MIN_CONSTRAINT = eINSTANCE.getWidgetDef_MinConstraint();

    /**
     * The meta object literal for the '<em><b>Max Constraint</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute WIDGET_DEF__MAX_CONSTRAINT = eINSTANCE.getWidgetDef_MaxConstraint();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.WidgetRefImpl <em>Widget Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.WidgetRefImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getWidgetRef()
     * @generated
     */
    EClass WIDGET_REF = eINSTANCE.getWidgetRef();

    /**
     * The meta object literal for the '<em><b>Widget</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference WIDGET_REF__WIDGET = eINSTANCE.getWidgetRef_Widget();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.AttrWidgetImpl <em>Attr Widget</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AttrWidgetImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getAttrWidget()
     * @generated
     */
    EClass ATTR_WIDGET = eINSTANCE.getAttrWidget();

    /**
     * The meta object literal for the '<em><b>Attribute Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTR_WIDGET__ATTRIBUTE_NAME = eINSTANCE.getAttrWidget_AttributeName();

    /**
     * The meta object literal for the '<em><b>Option</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTR_WIDGET__OPTION = eINSTANCE.getAttrWidget_Option();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.StateDefImpl <em>State Def</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.StateDefImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getStateDef()
     * @generated
     */
    EClass STATE_DEF = eINSTANCE.getStateDef();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute STATE_DEF__NAME = eINSTANCE.getStateDef_Name();

    /**
     * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute STATE_DEF__DESCRIPTION = eINSTANCE.getStateDef_Description();

    /**
     * The meta object literal for the '<em><b>Page Type</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute STATE_DEF__PAGE_TYPE = eINSTANCE.getStateDef_PageType();

    /**
     * The meta object literal for the '<em><b>Ordinal</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute STATE_DEF__ORDINAL = eINSTANCE.getStateDef_Ordinal();

    /**
     * The meta object literal for the '<em><b>Transition States</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference STATE_DEF__TRANSITION_STATES = eINSTANCE.getStateDef_TransitionStates();

    /**
     * The meta object literal for the '<em><b>Rules</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute STATE_DEF__RULES = eINSTANCE.getStateDef_Rules();

    /**
     * The meta object literal for the '<em><b>Decision Reviews</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference STATE_DEF__DECISION_REVIEWS = eINSTANCE.getStateDef_DecisionReviews();

    /**
     * The meta object literal for the '<em><b>Peer Reviews</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference STATE_DEF__PEER_REVIEWS = eINSTANCE.getStateDef_PeerReviews();

    /**
     * The meta object literal for the '<em><b>Percent Weight</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute STATE_DEF__PERCENT_WEIGHT = eINSTANCE.getStateDef_PercentWeight();

    /**
     * The meta object literal for the '<em><b>Recommended Percent Complete</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute STATE_DEF__RECOMMENDED_PERCENT_COMPLETE = eINSTANCE.getStateDef_RecommendedPercentComplete();

    /**
     * The meta object literal for the '<em><b>Color</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute STATE_DEF__COLOR = eINSTANCE.getStateDef_Color();

    /**
     * The meta object literal for the '<em><b>Layout</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference STATE_DEF__LAYOUT = eINSTANCE.getStateDef_Layout();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.DecisionReviewRefImpl <em>Decision Review Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.DecisionReviewRefImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getDecisionReviewRef()
     * @generated
     */
    EClass DECISION_REVIEW_REF = eINSTANCE.getDecisionReviewRef();

    /**
     * The meta object literal for the '<em><b>Decision Review</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DECISION_REVIEW_REF__DECISION_REVIEW = eINSTANCE.getDecisionReviewRef_DecisionReview();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.DecisionReviewDefImpl <em>Decision Review Def</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.DecisionReviewDefImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getDecisionReviewDef()
     * @generated
     */
    EClass DECISION_REVIEW_DEF = eINSTANCE.getDecisionReviewDef();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DECISION_REVIEW_DEF__NAME = eINSTANCE.getDecisionReviewDef_Name();

    /**
     * The meta object literal for the '<em><b>Title</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DECISION_REVIEW_DEF__TITLE = eINSTANCE.getDecisionReviewDef_Title();

    /**
     * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DECISION_REVIEW_DEF__DESCRIPTION = eINSTANCE.getDecisionReviewDef_Description();

    /**
     * The meta object literal for the '<em><b>Related To State</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DECISION_REVIEW_DEF__RELATED_TO_STATE = eINSTANCE.getDecisionReviewDef_RelatedToState();

    /**
     * The meta object literal for the '<em><b>Blocking Type</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DECISION_REVIEW_DEF__BLOCKING_TYPE = eINSTANCE.getDecisionReviewDef_BlockingType();

    /**
     * The meta object literal for the '<em><b>State Event</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DECISION_REVIEW_DEF__STATE_EVENT = eINSTANCE.getDecisionReviewDef_StateEvent();

    /**
     * The meta object literal for the '<em><b>Assignee Refs</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DECISION_REVIEW_DEF__ASSIGNEE_REFS = eINSTANCE.getDecisionReviewDef_AssigneeRefs();

    /**
     * The meta object literal for the '<em><b>Auto Transition To Decision</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DECISION_REVIEW_DEF__AUTO_TRANSITION_TO_DECISION = eINSTANCE.getDecisionReviewDef_AutoTransitionToDecision();

    /**
     * The meta object literal for the '<em><b>Options</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DECISION_REVIEW_DEF__OPTIONS = eINSTANCE.getDecisionReviewDef_Options();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.DecisionReviewOptImpl <em>Decision Review Opt</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.DecisionReviewOptImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getDecisionReviewOpt()
     * @generated
     */
    EClass DECISION_REVIEW_OPT = eINSTANCE.getDecisionReviewOpt();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DECISION_REVIEW_OPT__NAME = eINSTANCE.getDecisionReviewOpt_Name();

    /**
     * The meta object literal for the '<em><b>Followup</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DECISION_REVIEW_OPT__FOLLOWUP = eINSTANCE.getDecisionReviewOpt_Followup();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.PeerReviewRefImpl <em>Peer Review Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.PeerReviewRefImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getPeerReviewRef()
     * @generated
     */
    EClass PEER_REVIEW_REF = eINSTANCE.getPeerReviewRef();

    /**
     * The meta object literal for the '<em><b>Peer Review</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PEER_REVIEW_REF__PEER_REVIEW = eINSTANCE.getPeerReviewRef_PeerReview();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.PeerReviewDefImpl <em>Peer Review Def</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.PeerReviewDefImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getPeerReviewDef()
     * @generated
     */
    EClass PEER_REVIEW_DEF = eINSTANCE.getPeerReviewDef();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PEER_REVIEW_DEF__NAME = eINSTANCE.getPeerReviewDef_Name();

    /**
     * The meta object literal for the '<em><b>Title</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PEER_REVIEW_DEF__TITLE = eINSTANCE.getPeerReviewDef_Title();

    /**
     * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PEER_REVIEW_DEF__DESCRIPTION = eINSTANCE.getPeerReviewDef_Description();

    /**
     * The meta object literal for the '<em><b>Location</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PEER_REVIEW_DEF__LOCATION = eINSTANCE.getPeerReviewDef_Location();

    /**
     * The meta object literal for the '<em><b>Related To State</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PEER_REVIEW_DEF__RELATED_TO_STATE = eINSTANCE.getPeerReviewDef_RelatedToState();

    /**
     * The meta object literal for the '<em><b>Blocking Type</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PEER_REVIEW_DEF__BLOCKING_TYPE = eINSTANCE.getPeerReviewDef_BlockingType();

    /**
     * The meta object literal for the '<em><b>State Event</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PEER_REVIEW_DEF__STATE_EVENT = eINSTANCE.getPeerReviewDef_StateEvent();

    /**
     * The meta object literal for the '<em><b>Assignee Refs</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PEER_REVIEW_DEF__ASSIGNEE_REFS = eINSTANCE.getPeerReviewDef_AssigneeRefs();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.FollowupRefImpl <em>Followup Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.FollowupRefImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getFollowupRef()
     * @generated
     */
    EClass FOLLOWUP_REF = eINSTANCE.getFollowupRef();

    /**
     * The meta object literal for the '<em><b>Assignee Refs</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference FOLLOWUP_REF__ASSIGNEE_REFS = eINSTANCE.getFollowupRef_AssigneeRefs();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.UserRefImpl <em>User Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.UserRefImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getUserRef()
     * @generated
     */
    EClass USER_REF = eINSTANCE.getUserRef();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.UserByUserIdImpl <em>User By User Id</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.UserByUserIdImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getUserByUserId()
     * @generated
     */
    EClass USER_BY_USER_ID = eINSTANCE.getUserByUserId();

    /**
     * The meta object literal for the '<em><b>User Id</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute USER_BY_USER_ID__USER_ID = eINSTANCE.getUserByUserId_UserId();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.UserByNameImpl <em>User By Name</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.UserByNameImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getUserByName()
     * @generated
     */
    EClass USER_BY_NAME = eINSTANCE.getUserByName();

    /**
     * The meta object literal for the '<em><b>User Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute USER_BY_NAME__USER_NAME = eINSTANCE.getUserByName_UserName();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ToStateImpl <em>To State</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.ToStateImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getToState()
     * @generated
     */
    EClass TO_STATE = eINSTANCE.getToState();

    /**
     * The meta object literal for the '<em><b>State</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TO_STATE__STATE = eINSTANCE.getToState_State();

    /**
     * The meta object literal for the '<em><b>Options</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TO_STATE__OPTIONS = eINSTANCE.getToState_Options();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.LayoutTypeImpl <em>Layout Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.LayoutTypeImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getLayoutType()
     * @generated
     */
    EClass LAYOUT_TYPE = eINSTANCE.getLayoutType();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.LayoutDefImpl <em>Layout Def</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.LayoutDefImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getLayoutDef()
     * @generated
     */
    EClass LAYOUT_DEF = eINSTANCE.getLayoutDef();

    /**
     * The meta object literal for the '<em><b>Layout Items</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference LAYOUT_DEF__LAYOUT_ITEMS = eINSTANCE.getLayoutDef_LayoutItems();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.LayoutCopyImpl <em>Layout Copy</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.LayoutCopyImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getLayoutCopy()
     * @generated
     */
    EClass LAYOUT_COPY = eINSTANCE.getLayoutCopy();

    /**
     * The meta object literal for the '<em><b>State</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference LAYOUT_COPY__STATE = eINSTANCE.getLayoutCopy_State();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.LayoutItemImpl <em>Layout Item</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.LayoutItemImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getLayoutItem()
     * @generated
     */
    EClass LAYOUT_ITEM = eINSTANCE.getLayoutItem();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.CompositeImpl <em>Composite</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.CompositeImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getComposite()
     * @generated
     */
    EClass COMPOSITE = eINSTANCE.getComposite();

    /**
     * The meta object literal for the '<em><b>Num Columns</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute COMPOSITE__NUM_COLUMNS = eINSTANCE.getComposite_NumColumns();

    /**
     * The meta object literal for the '<em><b>Layout Items</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPOSITE__LAYOUT_ITEMS = eINSTANCE.getComposite_LayoutItems();

    /**
     * The meta object literal for the '<em><b>Options</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute COMPOSITE__OPTIONS = eINSTANCE.getComposite_Options();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.RuleDefImpl <em>Rule Def</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.RuleDefImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getRuleDef()
     * @generated
     */
    EClass RULE_DEF = eINSTANCE.getRuleDef();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.CreateTaskRuleImpl <em>Create Task Rule</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.CreateTaskRuleImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getCreateTaskRule()
     * @generated
     */
    EClass CREATE_TASK_RULE = eINSTANCE.getCreateTaskRule();

    /**
     * The meta object literal for the '<em><b>Assignees</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CREATE_TASK_RULE__ASSIGNEES = eINSTANCE.getCreateTaskRule_Assignees();

    /**
     * The meta object literal for the '<em><b>Related State</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CREATE_TASK_RULE__RELATED_STATE = eINSTANCE.getCreateTaskRule_RelatedState();

    /**
     * The meta object literal for the '<em><b>Task Work Def</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CREATE_TASK_RULE__TASK_WORK_DEF = eINSTANCE.getCreateTaskRule_TaskWorkDef();

    /**
     * The meta object literal for the '<em><b>On Event</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CREATE_TASK_RULE__ON_EVENT = eINSTANCE.getCreateTaskRule_OnEvent();

    /**
     * The meta object literal for the '<em><b>Attributes</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CREATE_TASK_RULE__ATTRIBUTES = eINSTANCE.getCreateTaskRule_Attributes();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.CreateDecisionReviewRuleImpl <em>Create Decision Review Rule</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.CreateDecisionReviewRuleImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getCreateDecisionReviewRule()
     * @generated
     */
    EClass CREATE_DECISION_REVIEW_RULE = eINSTANCE.getCreateDecisionReviewRule();

    /**
     * The meta object literal for the '<em><b>Auto Transition To Decision</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CREATE_DECISION_REVIEW_RULE__AUTO_TRANSITION_TO_DECISION = eINSTANCE.getCreateDecisionReviewRule_AutoTransitionToDecision();

    /**
     * The meta object literal for the '<em><b>Options</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CREATE_DECISION_REVIEW_RULE__OPTIONS = eINSTANCE.getCreateDecisionReviewRule_Options();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.CreatePeerReviewRuleImpl <em>Create Peer Review Rule</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.CreatePeerReviewRuleImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getCreatePeerReviewRule()
     * @generated
     */
    EClass CREATE_PEER_REVIEW_RULE = eINSTANCE.getCreatePeerReviewRule();

    /**
     * The meta object literal for the '<em><b>Location</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CREATE_PEER_REVIEW_RULE__LOCATION = eINSTANCE.getCreatePeerReviewRule_Location();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ReviewRuleImpl <em>Review Rule</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.ReviewRuleImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getReviewRule()
     * @generated
     */
    EClass REVIEW_RULE = eINSTANCE.getReviewRule();

    /**
     * The meta object literal for the '<em><b>Assignees</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REVIEW_RULE__ASSIGNEES = eINSTANCE.getReviewRule_Assignees();

    /**
     * The meta object literal for the '<em><b>Related To State</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute REVIEW_RULE__RELATED_TO_STATE = eINSTANCE.getReviewRule_RelatedToState();

    /**
     * The meta object literal for the '<em><b>Blocking Type</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute REVIEW_RULE__BLOCKING_TYPE = eINSTANCE.getReviewRule_BlockingType();

    /**
     * The meta object literal for the '<em><b>State Event</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute REVIEW_RULE__STATE_EVENT = eINSTANCE.getReviewRule_StateEvent();

    /**
     * The meta object literal for the '<em><b>Attributes</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REVIEW_RULE__ATTRIBUTES = eINSTANCE.getReviewRule_Attributes();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.impl.RuleImpl <em>Rule</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.RuleImpl
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getRule()
     * @generated
     */
    EClass RULE = eINSTANCE.getRule();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute RULE__NAME = eINSTANCE.getRule_Name();

    /**
     * The meta object literal for the '<em><b>Title</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute RULE__TITLE = eINSTANCE.getRule_Title();

    /**
     * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute RULE__DESCRIPTION = eINSTANCE.getRule_Description();

    /**
     * The meta object literal for the '<em><b>Rule Location</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute RULE__RULE_LOCATION = eINSTANCE.getRule_RuleLocation();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.OnEventType <em>On Event Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.OnEventType
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getOnEventType()
     * @generated
     */
    EEnum ON_EVENT_TYPE = eINSTANCE.getOnEventType();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.BooleanDef <em>Boolean Def</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.BooleanDef
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getBooleanDef()
     * @generated
     */
    EEnum BOOLEAN_DEF = eINSTANCE.getBooleanDef();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.WorkflowEventType <em>Workflow Event Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.WorkflowEventType
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getWorkflowEventType()
     * @generated
     */
    EEnum WORKFLOW_EVENT_TYPE = eINSTANCE.getWorkflowEventType();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.ReviewBlockingType <em>Review Blocking Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.ReviewBlockingType
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getReviewBlockingType()
     * @generated
     */
    EEnum REVIEW_BLOCKING_TYPE = eINSTANCE.getReviewBlockingType();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.ats.dsl.atsDsl.RuleLocation <em>Rule Location</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.ats.dsl.atsDsl.RuleLocation
     * @see org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslPackageImpl#getRuleLocation()
     * @generated
     */
    EEnum RULE_LOCATION = eINSTANCE.getRuleLocation();

  }

} //AtsDslPackage
