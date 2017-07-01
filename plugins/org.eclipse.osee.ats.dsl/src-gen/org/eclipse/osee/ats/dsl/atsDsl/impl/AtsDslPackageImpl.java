/**
 */
package org.eclipse.osee.ats.dsl.atsDsl.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDslFactory;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage;
import org.eclipse.osee.ats.dsl.atsDsl.AttrDef;
import org.eclipse.osee.ats.dsl.atsDsl.AttrDefOptions;
import org.eclipse.osee.ats.dsl.atsDsl.AttrFullDef;
import org.eclipse.osee.ats.dsl.atsDsl.AttrValueDef;
import org.eclipse.osee.ats.dsl.atsDsl.AttrWidget;
import org.eclipse.osee.ats.dsl.atsDsl.BooleanDef;
import org.eclipse.osee.ats.dsl.atsDsl.Composite;
import org.eclipse.osee.ats.dsl.atsDsl.CreateDecisionReviewRule;
import org.eclipse.osee.ats.dsl.atsDsl.CreatePeerReviewRule;
import org.eclipse.osee.ats.dsl.atsDsl.CreateTaskRule;
import org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef;
import org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewOpt;
import org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewRef;
import org.eclipse.osee.ats.dsl.atsDsl.FollowupRef;
import org.eclipse.osee.ats.dsl.atsDsl.LayoutCopy;
import org.eclipse.osee.ats.dsl.atsDsl.LayoutDef;
import org.eclipse.osee.ats.dsl.atsDsl.LayoutItem;
import org.eclipse.osee.ats.dsl.atsDsl.LayoutType;
import org.eclipse.osee.ats.dsl.atsDsl.OnEventType;
import org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef;
import org.eclipse.osee.ats.dsl.atsDsl.PeerReviewRef;
import org.eclipse.osee.ats.dsl.atsDsl.ProgramDef;
import org.eclipse.osee.ats.dsl.atsDsl.ReviewBlockingType;
import org.eclipse.osee.ats.dsl.atsDsl.ReviewRule;
import org.eclipse.osee.ats.dsl.atsDsl.Rule;
import org.eclipse.osee.ats.dsl.atsDsl.RuleDef;
import org.eclipse.osee.ats.dsl.atsDsl.RuleLocation;
import org.eclipse.osee.ats.dsl.atsDsl.StateDef;
import org.eclipse.osee.ats.dsl.atsDsl.TeamDef;
import org.eclipse.osee.ats.dsl.atsDsl.ToState;
import org.eclipse.osee.ats.dsl.atsDsl.UserByName;
import org.eclipse.osee.ats.dsl.atsDsl.UserByUserId;
import org.eclipse.osee.ats.dsl.atsDsl.UserDef;
import org.eclipse.osee.ats.dsl.atsDsl.UserRef;
import org.eclipse.osee.ats.dsl.atsDsl.VersionDef;
import org.eclipse.osee.ats.dsl.atsDsl.WidgetDef;
import org.eclipse.osee.ats.dsl.atsDsl.WidgetRef;
import org.eclipse.osee.ats.dsl.atsDsl.WorkDef;
import org.eclipse.osee.ats.dsl.atsDsl.WorkflowEventType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class AtsDslPackageImpl extends EPackageImpl implements AtsDslPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass atsDslEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass userDefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass attrDefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass attrDefOptionsEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass attrValueDefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass attrFullDefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass programDefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass teamDefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass actionableItemDefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass versionDefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass workDefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass widgetDefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass widgetRefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass attrWidgetEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass stateDefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass decisionReviewRefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass decisionReviewDefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass decisionReviewOptEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass peerReviewRefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass peerReviewDefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass followupRefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass userRefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass userByUserIdEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass userByNameEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass toStateEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass layoutTypeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass layoutDefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass layoutCopyEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass layoutItemEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass compositeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass ruleDefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass createTaskRuleEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass createDecisionReviewRuleEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass createPeerReviewRuleEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass reviewRuleEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass ruleEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum onEventTypeEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum booleanDefEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum workflowEventTypeEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum reviewBlockingTypeEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum ruleLocationEEnum = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private AtsDslPackageImpl()
  {
    super(eNS_URI, AtsDslFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * 
   * <p>This method is used to initialize {@link AtsDslPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static AtsDslPackage init()
  {
    if (isInited) return (AtsDslPackage)EPackage.Registry.INSTANCE.getEPackage(AtsDslPackage.eNS_URI);

    // Obtain or create and register package
    AtsDslPackageImpl theAtsDslPackage = (AtsDslPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof AtsDslPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new AtsDslPackageImpl());

    isInited = true;

    // Create package meta-data objects
    theAtsDslPackage.createPackageContents();

    // Initialize created meta-data
    theAtsDslPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theAtsDslPackage.freeze();

  
    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(AtsDslPackage.eNS_URI, theAtsDslPackage);
    return theAtsDslPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAtsDsl()
  {
    return atsDslEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAtsDsl_UserDef()
  {
    return (EReference)atsDslEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAtsDsl_TeamDef()
  {
    return (EReference)atsDslEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAtsDsl_ActionableItemDef()
  {
    return (EReference)atsDslEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAtsDsl_WorkDef()
  {
    return (EReference)atsDslEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAtsDsl_Program()
  {
    return (EReference)atsDslEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAtsDsl_Rule()
  {
    return (EReference)atsDslEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getUserDef()
  {
    return userDefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getUserDef_Name()
  {
    return (EAttribute)userDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getUserDef_UserDefOption()
  {
    return (EAttribute)userDefEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getUserDef_Active()
  {
    return (EAttribute)userDefEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getUserDef_UserId()
  {
    return (EAttribute)userDefEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getUserDef_Email()
  {
    return (EAttribute)userDefEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getUserDef_Admin()
  {
    return (EAttribute)userDefEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAttrDef()
  {
    return attrDefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAttrDef_Name()
  {
    return (EAttribute)attrDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAttrDef_Option()
  {
    return (EReference)attrDefEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAttrDefOptions()
  {
    return attrDefOptionsEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAttrValueDef()
  {
    return attrValueDefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAttrValueDef_Value()
  {
    return (EAttribute)attrValueDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAttrFullDef()
  {
    return attrFullDefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAttrFullDef_Uuid()
  {
    return (EAttribute)attrFullDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAttrFullDef_Values()
  {
    return (EAttribute)attrFullDefEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getProgramDef()
  {
    return programDefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getProgramDef_Name()
  {
    return (EAttribute)programDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getProgramDef_ProgramDefOption()
  {
    return (EAttribute)programDefEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getProgramDef_Uuid()
  {
    return (EAttribute)programDefEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getProgramDef_ArtifactTypeName()
  {
    return (EAttribute)programDefEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getProgramDef_Active()
  {
    return (EAttribute)programDefEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getProgramDef_Namespace()
  {
    return (EAttribute)programDefEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getProgramDef_TeamDefinition()
  {
    return (EAttribute)programDefEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getProgramDef_Attributes()
  {
    return (EReference)programDefEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTeamDef()
  {
    return teamDefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTeamDef_Name()
  {
    return (EAttribute)teamDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTeamDef_TeamDefOption()
  {
    return (EAttribute)teamDefEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTeamDef_Uuid()
  {
    return (EAttribute)teamDefEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTeamDef_Active()
  {
    return (EAttribute)teamDefEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTeamDef_StaticId()
  {
    return (EAttribute)teamDefEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTeamDef_Lead()
  {
    return (EReference)teamDefEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTeamDef_Member()
  {
    return (EReference)teamDefEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTeamDef_Privileged()
  {
    return (EReference)teamDefEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTeamDef_WorkDefinition()
  {
    return (EAttribute)teamDefEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTeamDef_RelatedTaskWorkDefinition()
  {
    return (EAttribute)teamDefEClass.getEStructuralFeatures().get(9);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTeamDef_TeamWorkflowArtifactType()
  {
    return (EAttribute)teamDefEClass.getEStructuralFeatures().get(10);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTeamDef_AccessContextId()
  {
    return (EAttribute)teamDefEClass.getEStructuralFeatures().get(11);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTeamDef_Version()
  {
    return (EReference)teamDefEClass.getEStructuralFeatures().get(12);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTeamDef_Rules()
  {
    return (EAttribute)teamDefEClass.getEStructuralFeatures().get(13);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTeamDef_Children()
  {
    return (EReference)teamDefEClass.getEStructuralFeatures().get(14);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getActionableItemDef()
  {
    return actionableItemDefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getActionableItemDef_Name()
  {
    return (EAttribute)actionableItemDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getActionableItemDef_AiDefOption()
  {
    return (EAttribute)actionableItemDefEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getActionableItemDef_Uuid()
  {
    return (EAttribute)actionableItemDefEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getActionableItemDef_Active()
  {
    return (EAttribute)actionableItemDefEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getActionableItemDef_Actionable()
  {
    return (EAttribute)actionableItemDefEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getActionableItemDef_Lead()
  {
    return (EReference)actionableItemDefEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getActionableItemDef_Owner()
  {
    return (EReference)actionableItemDefEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getActionableItemDef_StaticId()
  {
    return (EAttribute)actionableItemDefEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getActionableItemDef_TeamDef()
  {
    return (EAttribute)actionableItemDefEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getActionableItemDef_AccessContextId()
  {
    return (EAttribute)actionableItemDefEClass.getEStructuralFeatures().get(9);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getActionableItemDef_Rules()
  {
    return (EAttribute)actionableItemDefEClass.getEStructuralFeatures().get(10);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getActionableItemDef_Children()
  {
    return (EReference)actionableItemDefEClass.getEStructuralFeatures().get(11);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getVersionDef()
  {
    return versionDefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getVersionDef_Name()
  {
    return (EAttribute)versionDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getVersionDef_Uuid()
  {
    return (EAttribute)versionDefEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getVersionDef_StaticId()
  {
    return (EAttribute)versionDefEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getVersionDef_Next()
  {
    return (EAttribute)versionDefEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getVersionDef_Released()
  {
    return (EAttribute)versionDefEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getVersionDef_AllowCreateBranch()
  {
    return (EAttribute)versionDefEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getVersionDef_AllowCommitBranch()
  {
    return (EAttribute)versionDefEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getVersionDef_BaselineBranchUuid()
  {
    return (EAttribute)versionDefEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getVersionDef_ParallelVersion()
  {
    return (EAttribute)versionDefEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getWorkDef()
  {
    return workDefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getWorkDef_Name()
  {
    return (EAttribute)workDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getWorkDef_Id()
  {
    return (EAttribute)workDefEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getWorkDef_StartState()
  {
    return (EReference)workDefEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getWorkDef_WidgetDefs()
  {
    return (EReference)workDefEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getWorkDef_DecisionReviewDefs()
  {
    return (EReference)workDefEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getWorkDef_PeerReviewDefs()
  {
    return (EReference)workDefEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getWorkDef_States()
  {
    return (EReference)workDefEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getWidgetDef()
  {
    return widgetDefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getWidgetDef_Name()
  {
    return (EAttribute)widgetDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getWidgetDef_AttributeName()
  {
    return (EAttribute)widgetDefEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getWidgetDef_Description()
  {
    return (EAttribute)widgetDefEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getWidgetDef_XWidgetName()
  {
    return (EAttribute)widgetDefEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getWidgetDef_DefaultValue()
  {
    return (EAttribute)widgetDefEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getWidgetDef_Height()
  {
    return (EAttribute)widgetDefEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getWidgetDef_Option()
  {
    return (EAttribute)widgetDefEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getWidgetDef_MinConstraint()
  {
    return (EAttribute)widgetDefEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getWidgetDef_MaxConstraint()
  {
    return (EAttribute)widgetDefEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getWidgetRef()
  {
    return widgetRefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getWidgetRef_Widget()
  {
    return (EReference)widgetRefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAttrWidget()
  {
    return attrWidgetEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAttrWidget_AttributeName()
  {
    return (EAttribute)attrWidgetEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAttrWidget_Option()
  {
    return (EAttribute)attrWidgetEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getStateDef()
  {
    return stateDefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getStateDef_Name()
  {
    return (EAttribute)stateDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getStateDef_Description()
  {
    return (EAttribute)stateDefEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getStateDef_PageType()
  {
    return (EAttribute)stateDefEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getStateDef_Ordinal()
  {
    return (EAttribute)stateDefEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getStateDef_TransitionStates()
  {
    return (EReference)stateDefEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getStateDef_Rules()
  {
    return (EAttribute)stateDefEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getStateDef_DecisionReviews()
  {
    return (EReference)stateDefEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getStateDef_PeerReviews()
  {
    return (EReference)stateDefEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getStateDef_PercentWeight()
  {
    return (EAttribute)stateDefEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getStateDef_RecommendedPercentComplete()
  {
    return (EAttribute)stateDefEClass.getEStructuralFeatures().get(9);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getStateDef_Color()
  {
    return (EAttribute)stateDefEClass.getEStructuralFeatures().get(10);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getStateDef_Layout()
  {
    return (EReference)stateDefEClass.getEStructuralFeatures().get(11);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDecisionReviewRef()
  {
    return decisionReviewRefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDecisionReviewRef_DecisionReview()
  {
    return (EReference)decisionReviewRefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDecisionReviewDef()
  {
    return decisionReviewDefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDecisionReviewDef_Name()
  {
    return (EAttribute)decisionReviewDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDecisionReviewDef_Title()
  {
    return (EAttribute)decisionReviewDefEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDecisionReviewDef_Description()
  {
    return (EAttribute)decisionReviewDefEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDecisionReviewDef_RelatedToState()
  {
    return (EReference)decisionReviewDefEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDecisionReviewDef_BlockingType()
  {
    return (EAttribute)decisionReviewDefEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDecisionReviewDef_StateEvent()
  {
    return (EAttribute)decisionReviewDefEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDecisionReviewDef_AssigneeRefs()
  {
    return (EReference)decisionReviewDefEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDecisionReviewDef_AutoTransitionToDecision()
  {
    return (EAttribute)decisionReviewDefEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDecisionReviewDef_Options()
  {
    return (EReference)decisionReviewDefEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDecisionReviewOpt()
  {
    return decisionReviewOptEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDecisionReviewOpt_Name()
  {
    return (EAttribute)decisionReviewOptEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDecisionReviewOpt_Followup()
  {
    return (EReference)decisionReviewOptEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getPeerReviewRef()
  {
    return peerReviewRefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getPeerReviewRef_PeerReview()
  {
    return (EReference)peerReviewRefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getPeerReviewDef()
  {
    return peerReviewDefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPeerReviewDef_Name()
  {
    return (EAttribute)peerReviewDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPeerReviewDef_Title()
  {
    return (EAttribute)peerReviewDefEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPeerReviewDef_Description()
  {
    return (EAttribute)peerReviewDefEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPeerReviewDef_Location()
  {
    return (EAttribute)peerReviewDefEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getPeerReviewDef_RelatedToState()
  {
    return (EReference)peerReviewDefEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPeerReviewDef_BlockingType()
  {
    return (EAttribute)peerReviewDefEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPeerReviewDef_StateEvent()
  {
    return (EAttribute)peerReviewDefEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getPeerReviewDef_AssigneeRefs()
  {
    return (EReference)peerReviewDefEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getFollowupRef()
  {
    return followupRefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getFollowupRef_AssigneeRefs()
  {
    return (EReference)followupRefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getUserRef()
  {
    return userRefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getUserByUserId()
  {
    return userByUserIdEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getUserByUserId_UserId()
  {
    return (EAttribute)userByUserIdEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getUserByName()
  {
    return userByNameEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getUserByName_UserName()
  {
    return (EAttribute)userByNameEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getToState()
  {
    return toStateEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getToState_State()
  {
    return (EReference)toStateEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getToState_Options()
  {
    return (EAttribute)toStateEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getLayoutType()
  {
    return layoutTypeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getLayoutDef()
  {
    return layoutDefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getLayoutDef_LayoutItems()
  {
    return (EReference)layoutDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getLayoutCopy()
  {
    return layoutCopyEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getLayoutCopy_State()
  {
    return (EReference)layoutCopyEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getLayoutItem()
  {
    return layoutItemEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getComposite()
  {
    return compositeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getComposite_NumColumns()
  {
    return (EAttribute)compositeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getComposite_LayoutItems()
  {
    return (EReference)compositeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getComposite_Options()
  {
    return (EAttribute)compositeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRuleDef()
  {
    return ruleDefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getCreateTaskRule()
  {
    return createTaskRuleEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getCreateTaskRule_Assignees()
  {
    return (EReference)createTaskRuleEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getCreateTaskRule_RelatedState()
  {
    return (EAttribute)createTaskRuleEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getCreateTaskRule_TaskWorkDef()
  {
    return (EAttribute)createTaskRuleEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getCreateTaskRule_OnEvent()
  {
    return (EAttribute)createTaskRuleEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getCreateTaskRule_Attributes()
  {
    return (EReference)createTaskRuleEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getCreateDecisionReviewRule()
  {
    return createDecisionReviewRuleEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getCreateDecisionReviewRule_AutoTransitionToDecision()
  {
    return (EAttribute)createDecisionReviewRuleEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getCreateDecisionReviewRule_Options()
  {
    return (EReference)createDecisionReviewRuleEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getCreatePeerReviewRule()
  {
    return createPeerReviewRuleEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getCreatePeerReviewRule_Location()
  {
    return (EAttribute)createPeerReviewRuleEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getReviewRule()
  {
    return reviewRuleEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getReviewRule_Assignees()
  {
    return (EReference)reviewRuleEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getReviewRule_RelatedToState()
  {
    return (EAttribute)reviewRuleEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getReviewRule_BlockingType()
  {
    return (EAttribute)reviewRuleEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getReviewRule_StateEvent()
  {
    return (EAttribute)reviewRuleEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getReviewRule_Attributes()
  {
    return (EReference)reviewRuleEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRule()
  {
    return ruleEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getRule_Name()
  {
    return (EAttribute)ruleEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getRule_Title()
  {
    return (EAttribute)ruleEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getRule_Description()
  {
    return (EAttribute)ruleEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getRule_RuleLocation()
  {
    return (EAttribute)ruleEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getOnEventType()
  {
    return onEventTypeEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getBooleanDef()
  {
    return booleanDefEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getWorkflowEventType()
  {
    return workflowEventTypeEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getReviewBlockingType()
  {
    return reviewBlockingTypeEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getRuleLocation()
  {
    return ruleLocationEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AtsDslFactory getAtsDslFactory()
  {
    return (AtsDslFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated) return;
    isCreated = true;

    // Create classes and their features
    atsDslEClass = createEClass(ATS_DSL);
    createEReference(atsDslEClass, ATS_DSL__USER_DEF);
    createEReference(atsDslEClass, ATS_DSL__TEAM_DEF);
    createEReference(atsDslEClass, ATS_DSL__ACTIONABLE_ITEM_DEF);
    createEReference(atsDslEClass, ATS_DSL__WORK_DEF);
    createEReference(atsDslEClass, ATS_DSL__PROGRAM);
    createEReference(atsDslEClass, ATS_DSL__RULE);

    userDefEClass = createEClass(USER_DEF);
    createEAttribute(userDefEClass, USER_DEF__NAME);
    createEAttribute(userDefEClass, USER_DEF__USER_DEF_OPTION);
    createEAttribute(userDefEClass, USER_DEF__ACTIVE);
    createEAttribute(userDefEClass, USER_DEF__USER_ID);
    createEAttribute(userDefEClass, USER_DEF__EMAIL);
    createEAttribute(userDefEClass, USER_DEF__ADMIN);

    attrDefEClass = createEClass(ATTR_DEF);
    createEAttribute(attrDefEClass, ATTR_DEF__NAME);
    createEReference(attrDefEClass, ATTR_DEF__OPTION);

    attrDefOptionsEClass = createEClass(ATTR_DEF_OPTIONS);

    attrValueDefEClass = createEClass(ATTR_VALUE_DEF);
    createEAttribute(attrValueDefEClass, ATTR_VALUE_DEF__VALUE);

    attrFullDefEClass = createEClass(ATTR_FULL_DEF);
    createEAttribute(attrFullDefEClass, ATTR_FULL_DEF__UUID);
    createEAttribute(attrFullDefEClass, ATTR_FULL_DEF__VALUES);

    programDefEClass = createEClass(PROGRAM_DEF);
    createEAttribute(programDefEClass, PROGRAM_DEF__NAME);
    createEAttribute(programDefEClass, PROGRAM_DEF__PROGRAM_DEF_OPTION);
    createEAttribute(programDefEClass, PROGRAM_DEF__UUID);
    createEAttribute(programDefEClass, PROGRAM_DEF__ARTIFACT_TYPE_NAME);
    createEAttribute(programDefEClass, PROGRAM_DEF__ACTIVE);
    createEAttribute(programDefEClass, PROGRAM_DEF__NAMESPACE);
    createEAttribute(programDefEClass, PROGRAM_DEF__TEAM_DEFINITION);
    createEReference(programDefEClass, PROGRAM_DEF__ATTRIBUTES);

    teamDefEClass = createEClass(TEAM_DEF);
    createEAttribute(teamDefEClass, TEAM_DEF__NAME);
    createEAttribute(teamDefEClass, TEAM_DEF__TEAM_DEF_OPTION);
    createEAttribute(teamDefEClass, TEAM_DEF__UUID);
    createEAttribute(teamDefEClass, TEAM_DEF__ACTIVE);
    createEAttribute(teamDefEClass, TEAM_DEF__STATIC_ID);
    createEReference(teamDefEClass, TEAM_DEF__LEAD);
    createEReference(teamDefEClass, TEAM_DEF__MEMBER);
    createEReference(teamDefEClass, TEAM_DEF__PRIVILEGED);
    createEAttribute(teamDefEClass, TEAM_DEF__WORK_DEFINITION);
    createEAttribute(teamDefEClass, TEAM_DEF__RELATED_TASK_WORK_DEFINITION);
    createEAttribute(teamDefEClass, TEAM_DEF__TEAM_WORKFLOW_ARTIFACT_TYPE);
    createEAttribute(teamDefEClass, TEAM_DEF__ACCESS_CONTEXT_ID);
    createEReference(teamDefEClass, TEAM_DEF__VERSION);
    createEAttribute(teamDefEClass, TEAM_DEF__RULES);
    createEReference(teamDefEClass, TEAM_DEF__CHILDREN);

    actionableItemDefEClass = createEClass(ACTIONABLE_ITEM_DEF);
    createEAttribute(actionableItemDefEClass, ACTIONABLE_ITEM_DEF__NAME);
    createEAttribute(actionableItemDefEClass, ACTIONABLE_ITEM_DEF__AI_DEF_OPTION);
    createEAttribute(actionableItemDefEClass, ACTIONABLE_ITEM_DEF__UUID);
    createEAttribute(actionableItemDefEClass, ACTIONABLE_ITEM_DEF__ACTIVE);
    createEAttribute(actionableItemDefEClass, ACTIONABLE_ITEM_DEF__ACTIONABLE);
    createEReference(actionableItemDefEClass, ACTIONABLE_ITEM_DEF__LEAD);
    createEReference(actionableItemDefEClass, ACTIONABLE_ITEM_DEF__OWNER);
    createEAttribute(actionableItemDefEClass, ACTIONABLE_ITEM_DEF__STATIC_ID);
    createEAttribute(actionableItemDefEClass, ACTIONABLE_ITEM_DEF__TEAM_DEF);
    createEAttribute(actionableItemDefEClass, ACTIONABLE_ITEM_DEF__ACCESS_CONTEXT_ID);
    createEAttribute(actionableItemDefEClass, ACTIONABLE_ITEM_DEF__RULES);
    createEReference(actionableItemDefEClass, ACTIONABLE_ITEM_DEF__CHILDREN);

    versionDefEClass = createEClass(VERSION_DEF);
    createEAttribute(versionDefEClass, VERSION_DEF__NAME);
    createEAttribute(versionDefEClass, VERSION_DEF__UUID);
    createEAttribute(versionDefEClass, VERSION_DEF__STATIC_ID);
    createEAttribute(versionDefEClass, VERSION_DEF__NEXT);
    createEAttribute(versionDefEClass, VERSION_DEF__RELEASED);
    createEAttribute(versionDefEClass, VERSION_DEF__ALLOW_CREATE_BRANCH);
    createEAttribute(versionDefEClass, VERSION_DEF__ALLOW_COMMIT_BRANCH);
    createEAttribute(versionDefEClass, VERSION_DEF__BASELINE_BRANCH_UUID);
    createEAttribute(versionDefEClass, VERSION_DEF__PARALLEL_VERSION);

    workDefEClass = createEClass(WORK_DEF);
    createEAttribute(workDefEClass, WORK_DEF__NAME);
    createEAttribute(workDefEClass, WORK_DEF__ID);
    createEReference(workDefEClass, WORK_DEF__START_STATE);
    createEReference(workDefEClass, WORK_DEF__WIDGET_DEFS);
    createEReference(workDefEClass, WORK_DEF__DECISION_REVIEW_DEFS);
    createEReference(workDefEClass, WORK_DEF__PEER_REVIEW_DEFS);
    createEReference(workDefEClass, WORK_DEF__STATES);

    widgetDefEClass = createEClass(WIDGET_DEF);
    createEAttribute(widgetDefEClass, WIDGET_DEF__NAME);
    createEAttribute(widgetDefEClass, WIDGET_DEF__ATTRIBUTE_NAME);
    createEAttribute(widgetDefEClass, WIDGET_DEF__DESCRIPTION);
    createEAttribute(widgetDefEClass, WIDGET_DEF__XWIDGET_NAME);
    createEAttribute(widgetDefEClass, WIDGET_DEF__DEFAULT_VALUE);
    createEAttribute(widgetDefEClass, WIDGET_DEF__HEIGHT);
    createEAttribute(widgetDefEClass, WIDGET_DEF__OPTION);
    createEAttribute(widgetDefEClass, WIDGET_DEF__MIN_CONSTRAINT);
    createEAttribute(widgetDefEClass, WIDGET_DEF__MAX_CONSTRAINT);

    widgetRefEClass = createEClass(WIDGET_REF);
    createEReference(widgetRefEClass, WIDGET_REF__WIDGET);

    attrWidgetEClass = createEClass(ATTR_WIDGET);
    createEAttribute(attrWidgetEClass, ATTR_WIDGET__ATTRIBUTE_NAME);
    createEAttribute(attrWidgetEClass, ATTR_WIDGET__OPTION);

    stateDefEClass = createEClass(STATE_DEF);
    createEAttribute(stateDefEClass, STATE_DEF__NAME);
    createEAttribute(stateDefEClass, STATE_DEF__DESCRIPTION);
    createEAttribute(stateDefEClass, STATE_DEF__PAGE_TYPE);
    createEAttribute(stateDefEClass, STATE_DEF__ORDINAL);
    createEReference(stateDefEClass, STATE_DEF__TRANSITION_STATES);
    createEAttribute(stateDefEClass, STATE_DEF__RULES);
    createEReference(stateDefEClass, STATE_DEF__DECISION_REVIEWS);
    createEReference(stateDefEClass, STATE_DEF__PEER_REVIEWS);
    createEAttribute(stateDefEClass, STATE_DEF__PERCENT_WEIGHT);
    createEAttribute(stateDefEClass, STATE_DEF__RECOMMENDED_PERCENT_COMPLETE);
    createEAttribute(stateDefEClass, STATE_DEF__COLOR);
    createEReference(stateDefEClass, STATE_DEF__LAYOUT);

    decisionReviewRefEClass = createEClass(DECISION_REVIEW_REF);
    createEReference(decisionReviewRefEClass, DECISION_REVIEW_REF__DECISION_REVIEW);

    decisionReviewDefEClass = createEClass(DECISION_REVIEW_DEF);
    createEAttribute(decisionReviewDefEClass, DECISION_REVIEW_DEF__NAME);
    createEAttribute(decisionReviewDefEClass, DECISION_REVIEW_DEF__TITLE);
    createEAttribute(decisionReviewDefEClass, DECISION_REVIEW_DEF__DESCRIPTION);
    createEReference(decisionReviewDefEClass, DECISION_REVIEW_DEF__RELATED_TO_STATE);
    createEAttribute(decisionReviewDefEClass, DECISION_REVIEW_DEF__BLOCKING_TYPE);
    createEAttribute(decisionReviewDefEClass, DECISION_REVIEW_DEF__STATE_EVENT);
    createEReference(decisionReviewDefEClass, DECISION_REVIEW_DEF__ASSIGNEE_REFS);
    createEAttribute(decisionReviewDefEClass, DECISION_REVIEW_DEF__AUTO_TRANSITION_TO_DECISION);
    createEReference(decisionReviewDefEClass, DECISION_REVIEW_DEF__OPTIONS);

    decisionReviewOptEClass = createEClass(DECISION_REVIEW_OPT);
    createEAttribute(decisionReviewOptEClass, DECISION_REVIEW_OPT__NAME);
    createEReference(decisionReviewOptEClass, DECISION_REVIEW_OPT__FOLLOWUP);

    peerReviewRefEClass = createEClass(PEER_REVIEW_REF);
    createEReference(peerReviewRefEClass, PEER_REVIEW_REF__PEER_REVIEW);

    peerReviewDefEClass = createEClass(PEER_REVIEW_DEF);
    createEAttribute(peerReviewDefEClass, PEER_REVIEW_DEF__NAME);
    createEAttribute(peerReviewDefEClass, PEER_REVIEW_DEF__TITLE);
    createEAttribute(peerReviewDefEClass, PEER_REVIEW_DEF__DESCRIPTION);
    createEAttribute(peerReviewDefEClass, PEER_REVIEW_DEF__LOCATION);
    createEReference(peerReviewDefEClass, PEER_REVIEW_DEF__RELATED_TO_STATE);
    createEAttribute(peerReviewDefEClass, PEER_REVIEW_DEF__BLOCKING_TYPE);
    createEAttribute(peerReviewDefEClass, PEER_REVIEW_DEF__STATE_EVENT);
    createEReference(peerReviewDefEClass, PEER_REVIEW_DEF__ASSIGNEE_REFS);

    followupRefEClass = createEClass(FOLLOWUP_REF);
    createEReference(followupRefEClass, FOLLOWUP_REF__ASSIGNEE_REFS);

    userRefEClass = createEClass(USER_REF);

    userByUserIdEClass = createEClass(USER_BY_USER_ID);
    createEAttribute(userByUserIdEClass, USER_BY_USER_ID__USER_ID);

    userByNameEClass = createEClass(USER_BY_NAME);
    createEAttribute(userByNameEClass, USER_BY_NAME__USER_NAME);

    toStateEClass = createEClass(TO_STATE);
    createEReference(toStateEClass, TO_STATE__STATE);
    createEAttribute(toStateEClass, TO_STATE__OPTIONS);

    layoutTypeEClass = createEClass(LAYOUT_TYPE);

    layoutDefEClass = createEClass(LAYOUT_DEF);
    createEReference(layoutDefEClass, LAYOUT_DEF__LAYOUT_ITEMS);

    layoutCopyEClass = createEClass(LAYOUT_COPY);
    createEReference(layoutCopyEClass, LAYOUT_COPY__STATE);

    layoutItemEClass = createEClass(LAYOUT_ITEM);

    compositeEClass = createEClass(COMPOSITE);
    createEAttribute(compositeEClass, COMPOSITE__NUM_COLUMNS);
    createEReference(compositeEClass, COMPOSITE__LAYOUT_ITEMS);
    createEAttribute(compositeEClass, COMPOSITE__OPTIONS);

    ruleDefEClass = createEClass(RULE_DEF);

    createTaskRuleEClass = createEClass(CREATE_TASK_RULE);
    createEReference(createTaskRuleEClass, CREATE_TASK_RULE__ASSIGNEES);
    createEAttribute(createTaskRuleEClass, CREATE_TASK_RULE__RELATED_STATE);
    createEAttribute(createTaskRuleEClass, CREATE_TASK_RULE__TASK_WORK_DEF);
    createEAttribute(createTaskRuleEClass, CREATE_TASK_RULE__ON_EVENT);
    createEReference(createTaskRuleEClass, CREATE_TASK_RULE__ATTRIBUTES);

    createDecisionReviewRuleEClass = createEClass(CREATE_DECISION_REVIEW_RULE);
    createEAttribute(createDecisionReviewRuleEClass, CREATE_DECISION_REVIEW_RULE__AUTO_TRANSITION_TO_DECISION);
    createEReference(createDecisionReviewRuleEClass, CREATE_DECISION_REVIEW_RULE__OPTIONS);

    createPeerReviewRuleEClass = createEClass(CREATE_PEER_REVIEW_RULE);
    createEAttribute(createPeerReviewRuleEClass, CREATE_PEER_REVIEW_RULE__LOCATION);

    reviewRuleEClass = createEClass(REVIEW_RULE);
    createEReference(reviewRuleEClass, REVIEW_RULE__ASSIGNEES);
    createEAttribute(reviewRuleEClass, REVIEW_RULE__RELATED_TO_STATE);
    createEAttribute(reviewRuleEClass, REVIEW_RULE__BLOCKING_TYPE);
    createEAttribute(reviewRuleEClass, REVIEW_RULE__STATE_EVENT);
    createEReference(reviewRuleEClass, REVIEW_RULE__ATTRIBUTES);

    ruleEClass = createEClass(RULE);
    createEAttribute(ruleEClass, RULE__NAME);
    createEAttribute(ruleEClass, RULE__TITLE);
    createEAttribute(ruleEClass, RULE__DESCRIPTION);
    createEAttribute(ruleEClass, RULE__RULE_LOCATION);

    // Create enums
    onEventTypeEEnum = createEEnum(ON_EVENT_TYPE);
    booleanDefEEnum = createEEnum(BOOLEAN_DEF);
    workflowEventTypeEEnum = createEEnum(WORKFLOW_EVENT_TYPE);
    reviewBlockingTypeEEnum = createEEnum(REVIEW_BLOCKING_TYPE);
    ruleLocationEEnum = createEEnum(RULE_LOCATION);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized) return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    attrValueDefEClass.getESuperTypes().add(this.getAttrDefOptions());
    attrFullDefEClass.getESuperTypes().add(this.getAttrDefOptions());
    widgetRefEClass.getESuperTypes().add(this.getLayoutItem());
    attrWidgetEClass.getESuperTypes().add(this.getLayoutItem());
    userByUserIdEClass.getESuperTypes().add(this.getUserRef());
    userByNameEClass.getESuperTypes().add(this.getUserRef());
    layoutDefEClass.getESuperTypes().add(this.getLayoutType());
    layoutCopyEClass.getESuperTypes().add(this.getLayoutType());
    compositeEClass.getESuperTypes().add(this.getLayoutItem());
    ruleDefEClass.getESuperTypes().add(this.getRule());
    createTaskRuleEClass.getESuperTypes().add(this.getRule());
    createDecisionReviewRuleEClass.getESuperTypes().add(this.getReviewRule());
    createPeerReviewRuleEClass.getESuperTypes().add(this.getReviewRule());
    reviewRuleEClass.getESuperTypes().add(this.getRule());

    // Initialize classes and features; add operations and parameters
    initEClass(atsDslEClass, AtsDsl.class, "AtsDsl", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAtsDsl_UserDef(), this.getUserDef(), null, "userDef", null, 0, -1, AtsDsl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAtsDsl_TeamDef(), this.getTeamDef(), null, "teamDef", null, 0, -1, AtsDsl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAtsDsl_ActionableItemDef(), this.getActionableItemDef(), null, "actionableItemDef", null, 0, -1, AtsDsl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAtsDsl_WorkDef(), this.getWorkDef(), null, "workDef", null, 0, -1, AtsDsl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAtsDsl_Program(), this.getProgramDef(), null, "program", null, 0, -1, AtsDsl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAtsDsl_Rule(), this.getRule(), null, "rule", null, 0, -1, AtsDsl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(userDefEClass, UserDef.class, "UserDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getUserDef_Name(), ecorePackage.getEString(), "name", null, 0, 1, UserDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUserDef_UserDefOption(), ecorePackage.getEString(), "userDefOption", null, 0, -1, UserDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUserDef_Active(), this.getBooleanDef(), "active", null, 0, 1, UserDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUserDef_UserId(), ecorePackage.getEString(), "userId", null, 0, 1, UserDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUserDef_Email(), ecorePackage.getEString(), "email", null, 0, 1, UserDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUserDef_Admin(), this.getBooleanDef(), "admin", null, 0, 1, UserDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(attrDefEClass, AttrDef.class, "AttrDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAttrDef_Name(), ecorePackage.getEString(), "name", null, 0, 1, AttrDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAttrDef_Option(), this.getAttrDefOptions(), null, "option", null, 0, 1, AttrDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(attrDefOptionsEClass, AttrDefOptions.class, "AttrDefOptions", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(attrValueDefEClass, AttrValueDef.class, "AttrValueDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAttrValueDef_Value(), ecorePackage.getEString(), "value", null, 0, 1, AttrValueDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(attrFullDefEClass, AttrFullDef.class, "AttrFullDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAttrFullDef_Uuid(), ecorePackage.getEString(), "uuid", null, 0, 1, AttrFullDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAttrFullDef_Values(), ecorePackage.getEString(), "values", null, 0, -1, AttrFullDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(programDefEClass, ProgramDef.class, "ProgramDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getProgramDef_Name(), ecorePackage.getEString(), "name", null, 0, 1, ProgramDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getProgramDef_ProgramDefOption(), ecorePackage.getEString(), "programDefOption", null, 0, -1, ProgramDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getProgramDef_Uuid(), ecorePackage.getEInt(), "uuid", null, 0, 1, ProgramDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getProgramDef_ArtifactTypeName(), ecorePackage.getEString(), "artifactTypeName", null, 0, 1, ProgramDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getProgramDef_Active(), this.getBooleanDef(), "active", null, 0, 1, ProgramDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getProgramDef_Namespace(), ecorePackage.getEString(), "namespace", null, 0, 1, ProgramDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getProgramDef_TeamDefinition(), ecorePackage.getEString(), "teamDefinition", null, 0, 1, ProgramDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getProgramDef_Attributes(), this.getAttrDef(), null, "attributes", null, 0, -1, ProgramDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(teamDefEClass, TeamDef.class, "TeamDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getTeamDef_Name(), ecorePackage.getEString(), "name", null, 0, 1, TeamDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTeamDef_TeamDefOption(), ecorePackage.getEString(), "teamDefOption", null, 0, -1, TeamDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTeamDef_Uuid(), ecorePackage.getEInt(), "uuid", null, 0, 1, TeamDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTeamDef_Active(), this.getBooleanDef(), "active", null, 0, 1, TeamDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTeamDef_StaticId(), ecorePackage.getEString(), "staticId", null, 0, -1, TeamDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTeamDef_Lead(), this.getUserRef(), null, "lead", null, 0, -1, TeamDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTeamDef_Member(), this.getUserRef(), null, "member", null, 0, -1, TeamDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTeamDef_Privileged(), this.getUserRef(), null, "privileged", null, 0, -1, TeamDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTeamDef_WorkDefinition(), ecorePackage.getEString(), "workDefinition", null, 0, 1, TeamDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTeamDef_RelatedTaskWorkDefinition(), ecorePackage.getEString(), "relatedTaskWorkDefinition", null, 0, 1, TeamDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTeamDef_TeamWorkflowArtifactType(), ecorePackage.getEString(), "teamWorkflowArtifactType", null, 0, 1, TeamDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTeamDef_AccessContextId(), ecorePackage.getEString(), "accessContextId", null, 0, -1, TeamDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTeamDef_Version(), this.getVersionDef(), null, "version", null, 0, -1, TeamDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTeamDef_Rules(), ecorePackage.getEString(), "rules", null, 0, -1, TeamDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTeamDef_Children(), this.getTeamDef(), null, "children", null, 0, -1, TeamDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(actionableItemDefEClass, ActionableItemDef.class, "ActionableItemDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getActionableItemDef_Name(), ecorePackage.getEString(), "name", null, 0, 1, ActionableItemDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getActionableItemDef_AiDefOption(), ecorePackage.getEString(), "aiDefOption", null, 0, -1, ActionableItemDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getActionableItemDef_Uuid(), ecorePackage.getEInt(), "uuid", null, 0, 1, ActionableItemDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getActionableItemDef_Active(), this.getBooleanDef(), "active", null, 0, 1, ActionableItemDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getActionableItemDef_Actionable(), this.getBooleanDef(), "actionable", null, 0, 1, ActionableItemDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getActionableItemDef_Lead(), this.getUserRef(), null, "lead", null, 0, -1, ActionableItemDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getActionableItemDef_Owner(), this.getUserRef(), null, "owner", null, 0, -1, ActionableItemDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getActionableItemDef_StaticId(), ecorePackage.getEString(), "staticId", null, 0, -1, ActionableItemDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getActionableItemDef_TeamDef(), ecorePackage.getEString(), "teamDef", null, 0, 1, ActionableItemDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getActionableItemDef_AccessContextId(), ecorePackage.getEString(), "accessContextId", null, 0, -1, ActionableItemDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getActionableItemDef_Rules(), ecorePackage.getEString(), "rules", null, 0, -1, ActionableItemDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getActionableItemDef_Children(), this.getActionableItemDef(), null, "children", null, 0, -1, ActionableItemDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(versionDefEClass, VersionDef.class, "VersionDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getVersionDef_Name(), ecorePackage.getEString(), "name", null, 0, 1, VersionDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getVersionDef_Uuid(), ecorePackage.getEInt(), "uuid", null, 0, 1, VersionDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getVersionDef_StaticId(), ecorePackage.getEString(), "staticId", null, 0, -1, VersionDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getVersionDef_Next(), this.getBooleanDef(), "next", null, 0, 1, VersionDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getVersionDef_Released(), this.getBooleanDef(), "released", null, 0, 1, VersionDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getVersionDef_AllowCreateBranch(), this.getBooleanDef(), "allowCreateBranch", null, 0, 1, VersionDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getVersionDef_AllowCommitBranch(), this.getBooleanDef(), "allowCommitBranch", null, 0, 1, VersionDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getVersionDef_BaselineBranchUuid(), ecorePackage.getEString(), "baselineBranchUuid", null, 0, 1, VersionDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getVersionDef_ParallelVersion(), ecorePackage.getEString(), "parallelVersion", null, 0, -1, VersionDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(workDefEClass, WorkDef.class, "WorkDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getWorkDef_Name(), ecorePackage.getEString(), "name", null, 0, 1, WorkDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getWorkDef_Id(), ecorePackage.getEString(), "id", null, 0, -1, WorkDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getWorkDef_StartState(), this.getStateDef(), null, "startState", null, 0, 1, WorkDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getWorkDef_WidgetDefs(), this.getWidgetDef(), null, "widgetDefs", null, 0, -1, WorkDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getWorkDef_DecisionReviewDefs(), this.getDecisionReviewDef(), null, "decisionReviewDefs", null, 0, -1, WorkDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getWorkDef_PeerReviewDefs(), this.getPeerReviewDef(), null, "peerReviewDefs", null, 0, -1, WorkDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getWorkDef_States(), this.getStateDef(), null, "states", null, 0, -1, WorkDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(widgetDefEClass, WidgetDef.class, "WidgetDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getWidgetDef_Name(), ecorePackage.getEString(), "name", null, 0, 1, WidgetDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getWidgetDef_AttributeName(), ecorePackage.getEString(), "attributeName", null, 0, 1, WidgetDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getWidgetDef_Description(), ecorePackage.getEString(), "description", null, 0, 1, WidgetDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getWidgetDef_XWidgetName(), ecorePackage.getEString(), "xWidgetName", null, 0, 1, WidgetDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getWidgetDef_DefaultValue(), ecorePackage.getEString(), "defaultValue", null, 0, 1, WidgetDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getWidgetDef_Height(), ecorePackage.getEInt(), "height", null, 0, 1, WidgetDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getWidgetDef_Option(), ecorePackage.getEString(), "option", null, 0, -1, WidgetDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getWidgetDef_MinConstraint(), ecorePackage.getEString(), "minConstraint", null, 0, 1, WidgetDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getWidgetDef_MaxConstraint(), ecorePackage.getEString(), "maxConstraint", null, 0, 1, WidgetDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(widgetRefEClass, WidgetRef.class, "WidgetRef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getWidgetRef_Widget(), this.getWidgetDef(), null, "widget", null, 0, 1, WidgetRef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(attrWidgetEClass, AttrWidget.class, "AttrWidget", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAttrWidget_AttributeName(), ecorePackage.getEString(), "attributeName", null, 0, 1, AttrWidget.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAttrWidget_Option(), ecorePackage.getEString(), "option", null, 0, -1, AttrWidget.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(stateDefEClass, StateDef.class, "StateDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getStateDef_Name(), ecorePackage.getEString(), "name", null, 0, 1, StateDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getStateDef_Description(), ecorePackage.getEString(), "description", null, 0, 1, StateDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getStateDef_PageType(), ecorePackage.getEString(), "pageType", null, 0, 1, StateDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getStateDef_Ordinal(), ecorePackage.getEInt(), "ordinal", null, 0, 1, StateDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getStateDef_TransitionStates(), this.getToState(), null, "transitionStates", null, 0, -1, StateDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getStateDef_Rules(), ecorePackage.getEString(), "rules", null, 0, -1, StateDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getStateDef_DecisionReviews(), this.getDecisionReviewRef(), null, "decisionReviews", null, 0, -1, StateDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getStateDef_PeerReviews(), this.getPeerReviewRef(), null, "peerReviews", null, 0, -1, StateDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getStateDef_PercentWeight(), ecorePackage.getEInt(), "percentWeight", null, 0, 1, StateDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getStateDef_RecommendedPercentComplete(), ecorePackage.getEInt(), "recommendedPercentComplete", null, 0, 1, StateDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getStateDef_Color(), ecorePackage.getEString(), "color", null, 0, 1, StateDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getStateDef_Layout(), this.getLayoutType(), null, "layout", null, 0, 1, StateDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(decisionReviewRefEClass, DecisionReviewRef.class, "DecisionReviewRef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDecisionReviewRef_DecisionReview(), this.getDecisionReviewDef(), null, "decisionReview", null, 0, 1, DecisionReviewRef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(decisionReviewDefEClass, DecisionReviewDef.class, "DecisionReviewDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDecisionReviewDef_Name(), ecorePackage.getEString(), "name", null, 0, 1, DecisionReviewDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDecisionReviewDef_Title(), ecorePackage.getEString(), "title", null, 0, 1, DecisionReviewDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDecisionReviewDef_Description(), ecorePackage.getEString(), "description", null, 0, 1, DecisionReviewDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDecisionReviewDef_RelatedToState(), this.getStateDef(), null, "relatedToState", null, 0, 1, DecisionReviewDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDecisionReviewDef_BlockingType(), this.getReviewBlockingType(), "blockingType", null, 0, 1, DecisionReviewDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDecisionReviewDef_StateEvent(), this.getWorkflowEventType(), "stateEvent", null, 0, 1, DecisionReviewDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDecisionReviewDef_AssigneeRefs(), this.getUserRef(), null, "assigneeRefs", null, 0, -1, DecisionReviewDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDecisionReviewDef_AutoTransitionToDecision(), this.getBooleanDef(), "autoTransitionToDecision", null, 0, 1, DecisionReviewDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDecisionReviewDef_Options(), this.getDecisionReviewOpt(), null, "options", null, 0, -1, DecisionReviewDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(decisionReviewOptEClass, DecisionReviewOpt.class, "DecisionReviewOpt", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDecisionReviewOpt_Name(), ecorePackage.getEString(), "name", null, 0, 1, DecisionReviewOpt.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDecisionReviewOpt_Followup(), this.getFollowupRef(), null, "followup", null, 0, 1, DecisionReviewOpt.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(peerReviewRefEClass, PeerReviewRef.class, "PeerReviewRef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getPeerReviewRef_PeerReview(), this.getPeerReviewDef(), null, "peerReview", null, 0, 1, PeerReviewRef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(peerReviewDefEClass, PeerReviewDef.class, "PeerReviewDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getPeerReviewDef_Name(), ecorePackage.getEString(), "name", null, 0, 1, PeerReviewDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getPeerReviewDef_Title(), ecorePackage.getEString(), "title", null, 0, 1, PeerReviewDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getPeerReviewDef_Description(), ecorePackage.getEString(), "description", null, 0, 1, PeerReviewDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getPeerReviewDef_Location(), ecorePackage.getEString(), "location", null, 0, 1, PeerReviewDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getPeerReviewDef_RelatedToState(), this.getStateDef(), null, "relatedToState", null, 0, 1, PeerReviewDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getPeerReviewDef_BlockingType(), this.getReviewBlockingType(), "blockingType", null, 0, 1, PeerReviewDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getPeerReviewDef_StateEvent(), this.getWorkflowEventType(), "stateEvent", null, 0, 1, PeerReviewDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getPeerReviewDef_AssigneeRefs(), this.getUserRef(), null, "assigneeRefs", null, 0, -1, PeerReviewDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(followupRefEClass, FollowupRef.class, "FollowupRef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getFollowupRef_AssigneeRefs(), this.getUserRef(), null, "assigneeRefs", null, 0, -1, FollowupRef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(userRefEClass, UserRef.class, "UserRef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(userByUserIdEClass, UserByUserId.class, "UserByUserId", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getUserByUserId_UserId(), ecorePackage.getEString(), "userId", null, 0, 1, UserByUserId.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(userByNameEClass, UserByName.class, "UserByName", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getUserByName_UserName(), ecorePackage.getEString(), "userName", null, 0, 1, UserByName.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(toStateEClass, ToState.class, "ToState", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getToState_State(), this.getStateDef(), null, "state", null, 0, 1, ToState.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getToState_Options(), ecorePackage.getEString(), "options", null, 0, -1, ToState.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(layoutTypeEClass, LayoutType.class, "LayoutType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(layoutDefEClass, LayoutDef.class, "LayoutDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getLayoutDef_LayoutItems(), this.getLayoutItem(), null, "layoutItems", null, 0, -1, LayoutDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(layoutCopyEClass, LayoutCopy.class, "LayoutCopy", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getLayoutCopy_State(), this.getStateDef(), null, "state", null, 0, 1, LayoutCopy.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(layoutItemEClass, LayoutItem.class, "LayoutItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(compositeEClass, Composite.class, "Composite", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getComposite_NumColumns(), ecorePackage.getEInt(), "numColumns", null, 0, 1, Composite.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getComposite_LayoutItems(), this.getLayoutItem(), null, "layoutItems", null, 0, -1, Composite.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getComposite_Options(), ecorePackage.getEString(), "options", null, 0, -1, Composite.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(ruleDefEClass, RuleDef.class, "RuleDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(createTaskRuleEClass, CreateTaskRule.class, "CreateTaskRule", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCreateTaskRule_Assignees(), this.getUserDef(), null, "assignees", null, 0, -1, CreateTaskRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getCreateTaskRule_RelatedState(), ecorePackage.getEString(), "relatedState", null, 0, 1, CreateTaskRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getCreateTaskRule_TaskWorkDef(), ecorePackage.getEString(), "taskWorkDef", null, 0, 1, CreateTaskRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getCreateTaskRule_OnEvent(), this.getOnEventType(), "onEvent", null, 0, -1, CreateTaskRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getCreateTaskRule_Attributes(), this.getAttrDef(), null, "attributes", null, 0, -1, CreateTaskRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(createDecisionReviewRuleEClass, CreateDecisionReviewRule.class, "CreateDecisionReviewRule", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getCreateDecisionReviewRule_AutoTransitionToDecision(), this.getBooleanDef(), "autoTransitionToDecision", null, 0, 1, CreateDecisionReviewRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getCreateDecisionReviewRule_Options(), this.getDecisionReviewOpt(), null, "options", null, 0, -1, CreateDecisionReviewRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(createPeerReviewRuleEClass, CreatePeerReviewRule.class, "CreatePeerReviewRule", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getCreatePeerReviewRule_Location(), ecorePackage.getEString(), "location", null, 0, 1, CreatePeerReviewRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(reviewRuleEClass, ReviewRule.class, "ReviewRule", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getReviewRule_Assignees(), this.getUserDef(), null, "assignees", null, 0, -1, ReviewRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getReviewRule_RelatedToState(), ecorePackage.getEString(), "relatedToState", null, 0, 1, ReviewRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getReviewRule_BlockingType(), this.getReviewBlockingType(), "blockingType", null, 0, 1, ReviewRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getReviewRule_StateEvent(), this.getWorkflowEventType(), "stateEvent", null, 0, 1, ReviewRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getReviewRule_Attributes(), this.getAttrDef(), null, "attributes", null, 0, -1, ReviewRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(ruleEClass, Rule.class, "Rule", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getRule_Name(), ecorePackage.getEString(), "name", null, 0, 1, Rule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getRule_Title(), ecorePackage.getEString(), "title", null, 0, 1, Rule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getRule_Description(), ecorePackage.getEString(), "description", null, 0, 1, Rule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getRule_RuleLocation(), this.getRuleLocation(), "ruleLocation", null, 0, -1, Rule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(onEventTypeEEnum, OnEventType.class, "OnEventType");
    addEEnumLiteral(onEventTypeEEnum, OnEventType.CREATE_BRANCH);
    addEEnumLiteral(onEventTypeEEnum, OnEventType.COMMIT_BRANCH);
    addEEnumLiteral(onEventTypeEEnum, OnEventType.CREATE_WORKFLOW);
    addEEnumLiteral(onEventTypeEEnum, OnEventType.TRANSITION_TO);
    addEEnumLiteral(onEventTypeEEnum, OnEventType.MANUAL);

    initEEnum(booleanDefEEnum, BooleanDef.class, "BooleanDef");
    addEEnumLiteral(booleanDefEEnum, BooleanDef.NONE);
    addEEnumLiteral(booleanDefEEnum, BooleanDef.TRUE);
    addEEnumLiteral(booleanDefEEnum, BooleanDef.FALSE);

    initEEnum(workflowEventTypeEEnum, WorkflowEventType.class, "WorkflowEventType");
    addEEnumLiteral(workflowEventTypeEEnum, WorkflowEventType.TRANSITION_TO);
    addEEnumLiteral(workflowEventTypeEEnum, WorkflowEventType.CREATE_BRANCH);
    addEEnumLiteral(workflowEventTypeEEnum, WorkflowEventType.COMMIT_BRANCH);

    initEEnum(reviewBlockingTypeEEnum, ReviewBlockingType.class, "ReviewBlockingType");
    addEEnumLiteral(reviewBlockingTypeEEnum, ReviewBlockingType.TRANSITION);
    addEEnumLiteral(reviewBlockingTypeEEnum, ReviewBlockingType.COMMIT);

    initEEnum(ruleLocationEEnum, RuleLocation.class, "RuleLocation");
    addEEnumLiteral(ruleLocationEEnum, RuleLocation.STATE_DEFINITION);
    addEEnumLiteral(ruleLocationEEnum, RuleLocation.TEAM_DEFINITION);
    addEEnumLiteral(ruleLocationEEnum, RuleLocation.ACTIONABLE_ITEM);

    // Create resource
    createResource(eNS_URI);
  }

} //AtsDslPackageImpl
