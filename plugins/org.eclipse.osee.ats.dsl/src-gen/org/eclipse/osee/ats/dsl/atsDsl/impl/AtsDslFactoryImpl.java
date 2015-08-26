/**
 */
package org.eclipse.osee.ats.dsl.atsDsl.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.osee.ats.dsl.atsDsl.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class AtsDslFactoryImpl extends EFactoryImpl implements AtsDslFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static AtsDslFactory init()
  {
    try
    {
      AtsDslFactory theAtsDslFactory = (AtsDslFactory)EPackage.Registry.INSTANCE.getEFactory(AtsDslPackage.eNS_URI);
      if (theAtsDslFactory != null)
      {
        return theAtsDslFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new AtsDslFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AtsDslFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
      case AtsDslPackage.ATS_DSL: return createAtsDsl();
      case AtsDslPackage.USER_DEF: return createUserDef();
      case AtsDslPackage.ATTR_DEF: return createAttrDef();
      case AtsDslPackage.ATTR_DEF_OPTIONS: return createAttrDefOptions();
      case AtsDslPackage.ATTR_VALUE_DEF: return createAttrValueDef();
      case AtsDslPackage.ATTR_FULL_DEF: return createAttrFullDef();
      case AtsDslPackage.PROGRAM_DEF: return createProgramDef();
      case AtsDslPackage.TEAM_DEF: return createTeamDef();
      case AtsDslPackage.ACTIONABLE_ITEM_DEF: return createActionableItemDef();
      case AtsDslPackage.VERSION_DEF: return createVersionDef();
      case AtsDslPackage.WORK_DEF: return createWorkDef();
      case AtsDslPackage.WIDGET_DEF: return createWidgetDef();
      case AtsDslPackage.WIDGET_REF: return createWidgetRef();
      case AtsDslPackage.ATTR_WIDGET: return createAttrWidget();
      case AtsDslPackage.STATE_DEF: return createStateDef();
      case AtsDslPackage.DECISION_REVIEW_REF: return createDecisionReviewRef();
      case AtsDslPackage.DECISION_REVIEW_DEF: return createDecisionReviewDef();
      case AtsDslPackage.DECISION_REVIEW_OPT: return createDecisionReviewOpt();
      case AtsDslPackage.PEER_REVIEW_REF: return createPeerReviewRef();
      case AtsDslPackage.PEER_REVIEW_DEF: return createPeerReviewDef();
      case AtsDslPackage.FOLLOWUP_REF: return createFollowupRef();
      case AtsDslPackage.USER_REF: return createUserRef();
      case AtsDslPackage.USER_BY_USER_ID: return createUserByUserId();
      case AtsDslPackage.USER_BY_NAME: return createUserByName();
      case AtsDslPackage.TO_STATE: return createToState();
      case AtsDslPackage.LAYOUT_TYPE: return createLayoutType();
      case AtsDslPackage.LAYOUT_DEF: return createLayoutDef();
      case AtsDslPackage.LAYOUT_COPY: return createLayoutCopy();
      case AtsDslPackage.LAYOUT_ITEM: return createLayoutItem();
      case AtsDslPackage.COMPOSITE: return createComposite();
      case AtsDslPackage.RULE_DEF: return createRuleDef();
      case AtsDslPackage.CREATE_TASK_RULE: return createCreateTaskRule();
      case AtsDslPackage.CREATE_DECISION_REVIEW_RULE: return createCreateDecisionReviewRule();
      case AtsDslPackage.CREATE_PEER_REVIEW_RULE: return createCreatePeerReviewRule();
      case AtsDslPackage.REVIEW_RULE: return createReviewRule();
      case AtsDslPackage.RULE: return createRule();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
      case AtsDslPackage.ON_EVENT_TYPE:
        return createOnEventTypeFromString(eDataType, initialValue);
      case AtsDslPackage.BOOLEAN_DEF:
        return createBooleanDefFromString(eDataType, initialValue);
      case AtsDslPackage.WORKFLOW_EVENT_TYPE:
        return createWorkflowEventTypeFromString(eDataType, initialValue);
      case AtsDslPackage.REVIEW_BLOCKING_TYPE:
        return createReviewBlockingTypeFromString(eDataType, initialValue);
      case AtsDslPackage.RULE_LOCATION:
        return createRuleLocationFromString(eDataType, initialValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
      case AtsDslPackage.ON_EVENT_TYPE:
        return convertOnEventTypeToString(eDataType, instanceValue);
      case AtsDslPackage.BOOLEAN_DEF:
        return convertBooleanDefToString(eDataType, instanceValue);
      case AtsDslPackage.WORKFLOW_EVENT_TYPE:
        return convertWorkflowEventTypeToString(eDataType, instanceValue);
      case AtsDslPackage.REVIEW_BLOCKING_TYPE:
        return convertReviewBlockingTypeToString(eDataType, instanceValue);
      case AtsDslPackage.RULE_LOCATION:
        return convertRuleLocationToString(eDataType, instanceValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AtsDsl createAtsDsl()
  {
    AtsDslImpl atsDsl = new AtsDslImpl();
    return atsDsl;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public UserDef createUserDef()
  {
    UserDefImpl userDef = new UserDefImpl();
    return userDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AttrDef createAttrDef()
  {
    AttrDefImpl attrDef = new AttrDefImpl();
    return attrDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AttrDefOptions createAttrDefOptions()
  {
    AttrDefOptionsImpl attrDefOptions = new AttrDefOptionsImpl();
    return attrDefOptions;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AttrValueDef createAttrValueDef()
  {
    AttrValueDefImpl attrValueDef = new AttrValueDefImpl();
    return attrValueDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AttrFullDef createAttrFullDef()
  {
    AttrFullDefImpl attrFullDef = new AttrFullDefImpl();
    return attrFullDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ProgramDef createProgramDef()
  {
    ProgramDefImpl programDef = new ProgramDefImpl();
    return programDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TeamDef createTeamDef()
  {
    TeamDefImpl teamDef = new TeamDefImpl();
    return teamDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ActionableItemDef createActionableItemDef()
  {
    ActionableItemDefImpl actionableItemDef = new ActionableItemDefImpl();
    return actionableItemDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public VersionDef createVersionDef()
  {
    VersionDefImpl versionDef = new VersionDefImpl();
    return versionDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public WorkDef createWorkDef()
  {
    WorkDefImpl workDef = new WorkDefImpl();
    return workDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public WidgetDef createWidgetDef()
  {
    WidgetDefImpl widgetDef = new WidgetDefImpl();
    return widgetDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public WidgetRef createWidgetRef()
  {
    WidgetRefImpl widgetRef = new WidgetRefImpl();
    return widgetRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AttrWidget createAttrWidget()
  {
    AttrWidgetImpl attrWidget = new AttrWidgetImpl();
    return attrWidget;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public StateDef createStateDef()
  {
    StateDefImpl stateDef = new StateDefImpl();
    return stateDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DecisionReviewRef createDecisionReviewRef()
  {
    DecisionReviewRefImpl decisionReviewRef = new DecisionReviewRefImpl();
    return decisionReviewRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DecisionReviewDef createDecisionReviewDef()
  {
    DecisionReviewDefImpl decisionReviewDef = new DecisionReviewDefImpl();
    return decisionReviewDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DecisionReviewOpt createDecisionReviewOpt()
  {
    DecisionReviewOptImpl decisionReviewOpt = new DecisionReviewOptImpl();
    return decisionReviewOpt;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PeerReviewRef createPeerReviewRef()
  {
    PeerReviewRefImpl peerReviewRef = new PeerReviewRefImpl();
    return peerReviewRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PeerReviewDef createPeerReviewDef()
  {
    PeerReviewDefImpl peerReviewDef = new PeerReviewDefImpl();
    return peerReviewDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public FollowupRef createFollowupRef()
  {
    FollowupRefImpl followupRef = new FollowupRefImpl();
    return followupRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public UserRef createUserRef()
  {
    UserRefImpl userRef = new UserRefImpl();
    return userRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public UserByUserId createUserByUserId()
  {
    UserByUserIdImpl userByUserId = new UserByUserIdImpl();
    return userByUserId;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public UserByName createUserByName()
  {
    UserByNameImpl userByName = new UserByNameImpl();
    return userByName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ToState createToState()
  {
    ToStateImpl toState = new ToStateImpl();
    return toState;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LayoutType createLayoutType()
  {
    LayoutTypeImpl layoutType = new LayoutTypeImpl();
    return layoutType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LayoutDef createLayoutDef()
  {
    LayoutDefImpl layoutDef = new LayoutDefImpl();
    return layoutDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LayoutCopy createLayoutCopy()
  {
    LayoutCopyImpl layoutCopy = new LayoutCopyImpl();
    return layoutCopy;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LayoutItem createLayoutItem()
  {
    LayoutItemImpl layoutItem = new LayoutItemImpl();
    return layoutItem;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Composite createComposite()
  {
    CompositeImpl composite = new CompositeImpl();
    return composite;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RuleDef createRuleDef()
  {
    RuleDefImpl ruleDef = new RuleDefImpl();
    return ruleDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public CreateTaskRule createCreateTaskRule()
  {
    CreateTaskRuleImpl createTaskRule = new CreateTaskRuleImpl();
    return createTaskRule;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public CreateDecisionReviewRule createCreateDecisionReviewRule()
  {
    CreateDecisionReviewRuleImpl createDecisionReviewRule = new CreateDecisionReviewRuleImpl();
    return createDecisionReviewRule;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public CreatePeerReviewRule createCreatePeerReviewRule()
  {
    CreatePeerReviewRuleImpl createPeerReviewRule = new CreatePeerReviewRuleImpl();
    return createPeerReviewRule;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ReviewRule createReviewRule()
  {
    ReviewRuleImpl reviewRule = new ReviewRuleImpl();
    return reviewRule;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Rule createRule()
  {
    RuleImpl rule = new RuleImpl();
    return rule;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OnEventType createOnEventTypeFromString(EDataType eDataType, String initialValue)
  {
    OnEventType result = OnEventType.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertOnEventTypeToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BooleanDef createBooleanDefFromString(EDataType eDataType, String initialValue)
  {
    BooleanDef result = BooleanDef.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertBooleanDefToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public WorkflowEventType createWorkflowEventTypeFromString(EDataType eDataType, String initialValue)
  {
    WorkflowEventType result = WorkflowEventType.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertWorkflowEventTypeToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ReviewBlockingType createReviewBlockingTypeFromString(EDataType eDataType, String initialValue)
  {
    ReviewBlockingType result = ReviewBlockingType.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertReviewBlockingTypeToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RuleLocation createRuleLocationFromString(EDataType eDataType, String initialValue)
  {
    RuleLocation result = RuleLocation.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertRuleLocationToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AtsDslPackage getAtsDslPackage()
  {
    return (AtsDslPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static AtsDslPackage getPackage()
  {
    return AtsDslPackage.eINSTANCE;
  }

} //AtsDslFactoryImpl
