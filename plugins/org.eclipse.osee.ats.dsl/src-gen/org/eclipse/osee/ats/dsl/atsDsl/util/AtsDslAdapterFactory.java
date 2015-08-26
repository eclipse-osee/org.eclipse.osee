/**
 */
package org.eclipse.osee.ats.dsl.atsDsl.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.osee.ats.dsl.atsDsl.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage
 * @generated
 */
public class AtsDslAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static AtsDslPackage modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AtsDslAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = AtsDslPackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object.
   * <!-- begin-user-doc -->
   * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
   * <!-- end-user-doc -->
   * @return whether this factory is applicable for the type of the object.
   * @generated
   */
  @Override
  public boolean isFactoryForType(Object object)
  {
    if (object == modelPackage)
    {
      return true;
    }
    if (object instanceof EObject)
    {
      return ((EObject)object).eClass().getEPackage() == modelPackage;
    }
    return false;
  }

  /**
   * The switch that delegates to the <code>createXXX</code> methods.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected AtsDslSwitch<Adapter> modelSwitch =
    new AtsDslSwitch<Adapter>()
    {
      @Override
      public Adapter caseAtsDsl(AtsDsl object)
      {
        return createAtsDslAdapter();
      }
      @Override
      public Adapter caseUserDef(UserDef object)
      {
        return createUserDefAdapter();
      }
      @Override
      public Adapter caseAttrDef(AttrDef object)
      {
        return createAttrDefAdapter();
      }
      @Override
      public Adapter caseAttrDefOptions(AttrDefOptions object)
      {
        return createAttrDefOptionsAdapter();
      }
      @Override
      public Adapter caseAttrValueDef(AttrValueDef object)
      {
        return createAttrValueDefAdapter();
      }
      @Override
      public Adapter caseAttrFullDef(AttrFullDef object)
      {
        return createAttrFullDefAdapter();
      }
      @Override
      public Adapter caseProgramDef(ProgramDef object)
      {
        return createProgramDefAdapter();
      }
      @Override
      public Adapter caseTeamDef(TeamDef object)
      {
        return createTeamDefAdapter();
      }
      @Override
      public Adapter caseActionableItemDef(ActionableItemDef object)
      {
        return createActionableItemDefAdapter();
      }
      @Override
      public Adapter caseVersionDef(VersionDef object)
      {
        return createVersionDefAdapter();
      }
      @Override
      public Adapter caseWorkDef(WorkDef object)
      {
        return createWorkDefAdapter();
      }
      @Override
      public Adapter caseWidgetDef(WidgetDef object)
      {
        return createWidgetDefAdapter();
      }
      @Override
      public Adapter caseWidgetRef(WidgetRef object)
      {
        return createWidgetRefAdapter();
      }
      @Override
      public Adapter caseAttrWidget(AttrWidget object)
      {
        return createAttrWidgetAdapter();
      }
      @Override
      public Adapter caseStateDef(StateDef object)
      {
        return createStateDefAdapter();
      }
      @Override
      public Adapter caseDecisionReviewRef(DecisionReviewRef object)
      {
        return createDecisionReviewRefAdapter();
      }
      @Override
      public Adapter caseDecisionReviewDef(DecisionReviewDef object)
      {
        return createDecisionReviewDefAdapter();
      }
      @Override
      public Adapter caseDecisionReviewOpt(DecisionReviewOpt object)
      {
        return createDecisionReviewOptAdapter();
      }
      @Override
      public Adapter casePeerReviewRef(PeerReviewRef object)
      {
        return createPeerReviewRefAdapter();
      }
      @Override
      public Adapter casePeerReviewDef(PeerReviewDef object)
      {
        return createPeerReviewDefAdapter();
      }
      @Override
      public Adapter caseFollowupRef(FollowupRef object)
      {
        return createFollowupRefAdapter();
      }
      @Override
      public Adapter caseUserRef(UserRef object)
      {
        return createUserRefAdapter();
      }
      @Override
      public Adapter caseUserByUserId(UserByUserId object)
      {
        return createUserByUserIdAdapter();
      }
      @Override
      public Adapter caseUserByName(UserByName object)
      {
        return createUserByNameAdapter();
      }
      @Override
      public Adapter caseToState(ToState object)
      {
        return createToStateAdapter();
      }
      @Override
      public Adapter caseLayoutType(LayoutType object)
      {
        return createLayoutTypeAdapter();
      }
      @Override
      public Adapter caseLayoutDef(LayoutDef object)
      {
        return createLayoutDefAdapter();
      }
      @Override
      public Adapter caseLayoutCopy(LayoutCopy object)
      {
        return createLayoutCopyAdapter();
      }
      @Override
      public Adapter caseLayoutItem(LayoutItem object)
      {
        return createLayoutItemAdapter();
      }
      @Override
      public Adapter caseComposite(Composite object)
      {
        return createCompositeAdapter();
      }
      @Override
      public Adapter caseRuleDef(RuleDef object)
      {
        return createRuleDefAdapter();
      }
      @Override
      public Adapter caseCreateTaskRule(CreateTaskRule object)
      {
        return createCreateTaskRuleAdapter();
      }
      @Override
      public Adapter caseCreateDecisionReviewRule(CreateDecisionReviewRule object)
      {
        return createCreateDecisionReviewRuleAdapter();
      }
      @Override
      public Adapter caseCreatePeerReviewRule(CreatePeerReviewRule object)
      {
        return createCreatePeerReviewRuleAdapter();
      }
      @Override
      public Adapter caseReviewRule(ReviewRule object)
      {
        return createReviewRuleAdapter();
      }
      @Override
      public Adapter caseRule(Rule object)
      {
        return createRuleAdapter();
      }
      @Override
      public Adapter defaultCase(EObject object)
      {
        return createEObjectAdapter();
      }
    };

  /**
   * Creates an adapter for the <code>target</code>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param target the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  @Override
  public Adapter createAdapter(Notifier target)
  {
    return modelSwitch.doSwitch((EObject)target);
  }


  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.AtsDsl <em>Ats Dsl</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDsl
   * @generated
   */
  public Adapter createAtsDslAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.UserDef <em>User Def</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.UserDef
   * @generated
   */
  public Adapter createUserDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.AttrDef <em>Attr Def</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AttrDef
   * @generated
   */
  public Adapter createAttrDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.AttrDefOptions <em>Attr Def Options</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AttrDefOptions
   * @generated
   */
  public Adapter createAttrDefOptionsAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.AttrValueDef <em>Attr Value Def</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AttrValueDef
   * @generated
   */
  public Adapter createAttrValueDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.AttrFullDef <em>Attr Full Def</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AttrFullDef
   * @generated
   */
  public Adapter createAttrFullDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.ProgramDef <em>Program Def</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ProgramDef
   * @generated
   */
  public Adapter createProgramDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.TeamDef <em>Team Def</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.TeamDef
   * @generated
   */
  public Adapter createTeamDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef <em>Actionable Item Def</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef
   * @generated
   */
  public Adapter createActionableItemDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef <em>Version Def</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.VersionDef
   * @generated
   */
  public Adapter createVersionDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.WorkDef <em>Work Def</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WorkDef
   * @generated
   */
  public Adapter createWorkDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef <em>Widget Def</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WidgetDef
   * @generated
   */
  public Adapter createWidgetDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetRef <em>Widget Ref</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WidgetRef
   * @generated
   */
  public Adapter createWidgetRefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.AttrWidget <em>Attr Widget</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AttrWidget
   * @generated
   */
  public Adapter createAttrWidgetAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef <em>State Def</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.StateDef
   * @generated
   */
  public Adapter createStateDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewRef <em>Decision Review Ref</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewRef
   * @generated
   */
  public Adapter createDecisionReviewRefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef <em>Decision Review Def</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef
   * @generated
   */
  public Adapter createDecisionReviewDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewOpt <em>Decision Review Opt</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewOpt
   * @generated
   */
  public Adapter createDecisionReviewOptAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewRef <em>Peer Review Ref</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.PeerReviewRef
   * @generated
   */
  public Adapter createPeerReviewRefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef <em>Peer Review Def</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef
   * @generated
   */
  public Adapter createPeerReviewDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.FollowupRef <em>Followup Ref</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.FollowupRef
   * @generated
   */
  public Adapter createFollowupRefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.UserRef <em>User Ref</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.UserRef
   * @generated
   */
  public Adapter createUserRefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.UserByUserId <em>User By User Id</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.UserByUserId
   * @generated
   */
  public Adapter createUserByUserIdAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.UserByName <em>User By Name</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.UserByName
   * @generated
   */
  public Adapter createUserByNameAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.ToState <em>To State</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ToState
   * @generated
   */
  public Adapter createToStateAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.LayoutType <em>Layout Type</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.LayoutType
   * @generated
   */
  public Adapter createLayoutTypeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.LayoutDef <em>Layout Def</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.LayoutDef
   * @generated
   */
  public Adapter createLayoutDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.LayoutCopy <em>Layout Copy</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.LayoutCopy
   * @generated
   */
  public Adapter createLayoutCopyAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.LayoutItem <em>Layout Item</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.LayoutItem
   * @generated
   */
  public Adapter createLayoutItemAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.Composite <em>Composite</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.Composite
   * @generated
   */
  public Adapter createCompositeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.RuleDef <em>Rule Def</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.RuleDef
   * @generated
   */
  public Adapter createRuleDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.CreateTaskRule <em>Create Task Rule</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.CreateTaskRule
   * @generated
   */
  public Adapter createCreateTaskRuleAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.CreateDecisionReviewRule <em>Create Decision Review Rule</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.CreateDecisionReviewRule
   * @generated
   */
  public Adapter createCreateDecisionReviewRuleAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.CreatePeerReviewRule <em>Create Peer Review Rule</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.CreatePeerReviewRule
   * @generated
   */
  public Adapter createCreatePeerReviewRuleAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.ReviewRule <em>Review Rule</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ReviewRule
   * @generated
   */
  public Adapter createReviewRuleAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.ats.dsl.atsDsl.Rule <em>Rule</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.ats.dsl.atsDsl.Rule
   * @generated
   */
  public Adapter createRuleAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case.
   * <!-- begin-user-doc -->
   * This default implementation returns null.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} //AtsDslAdapterFactory
