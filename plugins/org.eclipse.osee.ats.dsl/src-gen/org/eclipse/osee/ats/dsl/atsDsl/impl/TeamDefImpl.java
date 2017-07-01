/**
 */
package org.eclipse.osee.ats.dsl.atsDsl.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage;
import org.eclipse.osee.ats.dsl.atsDsl.BooleanDef;
import org.eclipse.osee.ats.dsl.atsDsl.TeamDef;
import org.eclipse.osee.ats.dsl.atsDsl.UserRef;
import org.eclipse.osee.ats.dsl.atsDsl.VersionDef;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Team Def</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.TeamDefImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.TeamDefImpl#getTeamDefOption <em>Team Def Option</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.TeamDefImpl#getUuid <em>Uuid</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.TeamDefImpl#getActive <em>Active</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.TeamDefImpl#getStaticId <em>Static Id</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.TeamDefImpl#getLead <em>Lead</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.TeamDefImpl#getMember <em>Member</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.TeamDefImpl#getPrivileged <em>Privileged</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.TeamDefImpl#getWorkDefinition <em>Work Definition</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.TeamDefImpl#getRelatedTaskWorkDefinition <em>Related Task Work Definition</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.TeamDefImpl#getTeamWorkflowArtifactType <em>Team Workflow Artifact Type</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.TeamDefImpl#getAccessContextId <em>Access Context Id</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.TeamDefImpl#getVersion <em>Version</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.TeamDefImpl#getRules <em>Rules</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.TeamDefImpl#getChildren <em>Children</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TeamDefImpl extends MinimalEObjectImpl.Container implements TeamDef
{
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getTeamDefOption() <em>Team Def Option</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTeamDefOption()
   * @generated
   * @ordered
   */
  protected EList<String> teamDefOption;

  /**
   * The default value of the '{@link #getUuid() <em>Uuid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUuid()
   * @generated
   * @ordered
   */
  protected static final int UUID_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getUuid() <em>Uuid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUuid()
   * @generated
   * @ordered
   */
  protected int uuid = UUID_EDEFAULT;

  /**
   * The default value of the '{@link #getActive() <em>Active</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getActive()
   * @generated
   * @ordered
   */
  protected static final BooleanDef ACTIVE_EDEFAULT = BooleanDef.NONE;

  /**
   * The cached value of the '{@link #getActive() <em>Active</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getActive()
   * @generated
   * @ordered
   */
  protected BooleanDef active = ACTIVE_EDEFAULT;

  /**
   * The cached value of the '{@link #getStaticId() <em>Static Id</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getStaticId()
   * @generated
   * @ordered
   */
  protected EList<String> staticId;

  /**
   * The cached value of the '{@link #getLead() <em>Lead</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLead()
   * @generated
   * @ordered
   */
  protected EList<UserRef> lead;

  /**
   * The cached value of the '{@link #getMember() <em>Member</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMember()
   * @generated
   * @ordered
   */
  protected EList<UserRef> member;

  /**
   * The cached value of the '{@link #getPrivileged() <em>Privileged</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPrivileged()
   * @generated
   * @ordered
   */
  protected EList<UserRef> privileged;

  /**
   * The default value of the '{@link #getWorkDefinition() <em>Work Definition</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getWorkDefinition()
   * @generated
   * @ordered
   */
  protected static final String WORK_DEFINITION_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getWorkDefinition() <em>Work Definition</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getWorkDefinition()
   * @generated
   * @ordered
   */
  protected String workDefinition = WORK_DEFINITION_EDEFAULT;

  /**
   * The default value of the '{@link #getRelatedTaskWorkDefinition() <em>Related Task Work Definition</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRelatedTaskWorkDefinition()
   * @generated
   * @ordered
   */
  protected static final String RELATED_TASK_WORK_DEFINITION_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getRelatedTaskWorkDefinition() <em>Related Task Work Definition</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRelatedTaskWorkDefinition()
   * @generated
   * @ordered
   */
  protected String relatedTaskWorkDefinition = RELATED_TASK_WORK_DEFINITION_EDEFAULT;

  /**
   * The default value of the '{@link #getTeamWorkflowArtifactType() <em>Team Workflow Artifact Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTeamWorkflowArtifactType()
   * @generated
   * @ordered
   */
  protected static final String TEAM_WORKFLOW_ARTIFACT_TYPE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getTeamWorkflowArtifactType() <em>Team Workflow Artifact Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTeamWorkflowArtifactType()
   * @generated
   * @ordered
   */
  protected String teamWorkflowArtifactType = TEAM_WORKFLOW_ARTIFACT_TYPE_EDEFAULT;

  /**
   * The cached value of the '{@link #getAccessContextId() <em>Access Context Id</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAccessContextId()
   * @generated
   * @ordered
   */
  protected EList<String> accessContextId;

  /**
   * The cached value of the '{@link #getVersion() <em>Version</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getVersion()
   * @generated
   * @ordered
   */
  protected EList<VersionDef> version;

  /**
   * The cached value of the '{@link #getRules() <em>Rules</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRules()
   * @generated
   * @ordered
   */
  protected EList<String> rules;

  /**
   * The cached value of the '{@link #getChildren() <em>Children</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getChildren()
   * @generated
   * @ordered
   */
  protected EList<TeamDef> children;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected TeamDefImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return AtsDslPackage.Literals.TEAM_DEF;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.TEAM_DEF__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<String> getTeamDefOption()
  {
    if (teamDefOption == null)
    {
      teamDefOption = new EDataTypeEList<String>(String.class, this, AtsDslPackage.TEAM_DEF__TEAM_DEF_OPTION);
    }
    return teamDefOption;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public int getUuid()
  {
    return uuid;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setUuid(int newUuid)
  {
    int oldUuid = uuid;
    uuid = newUuid;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.TEAM_DEF__UUID, oldUuid, uuid));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BooleanDef getActive()
  {
    return active;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setActive(BooleanDef newActive)
  {
    BooleanDef oldActive = active;
    active = newActive == null ? ACTIVE_EDEFAULT : newActive;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.TEAM_DEF__ACTIVE, oldActive, active));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<String> getStaticId()
  {
    if (staticId == null)
    {
      staticId = new EDataTypeEList<String>(String.class, this, AtsDslPackage.TEAM_DEF__STATIC_ID);
    }
    return staticId;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<UserRef> getLead()
  {
    if (lead == null)
    {
      lead = new EObjectContainmentEList<UserRef>(UserRef.class, this, AtsDslPackage.TEAM_DEF__LEAD);
    }
    return lead;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<UserRef> getMember()
  {
    if (member == null)
    {
      member = new EObjectContainmentEList<UserRef>(UserRef.class, this, AtsDslPackage.TEAM_DEF__MEMBER);
    }
    return member;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<UserRef> getPrivileged()
  {
    if (privileged == null)
    {
      privileged = new EObjectContainmentEList<UserRef>(UserRef.class, this, AtsDslPackage.TEAM_DEF__PRIVILEGED);
    }
    return privileged;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getWorkDefinition()
  {
    return workDefinition;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setWorkDefinition(String newWorkDefinition)
  {
    String oldWorkDefinition = workDefinition;
    workDefinition = newWorkDefinition;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.TEAM_DEF__WORK_DEFINITION, oldWorkDefinition, workDefinition));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getRelatedTaskWorkDefinition()
  {
    return relatedTaskWorkDefinition;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRelatedTaskWorkDefinition(String newRelatedTaskWorkDefinition)
  {
    String oldRelatedTaskWorkDefinition = relatedTaskWorkDefinition;
    relatedTaskWorkDefinition = newRelatedTaskWorkDefinition;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.TEAM_DEF__RELATED_TASK_WORK_DEFINITION, oldRelatedTaskWorkDefinition, relatedTaskWorkDefinition));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getTeamWorkflowArtifactType()
  {
    return teamWorkflowArtifactType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTeamWorkflowArtifactType(String newTeamWorkflowArtifactType)
  {
    String oldTeamWorkflowArtifactType = teamWorkflowArtifactType;
    teamWorkflowArtifactType = newTeamWorkflowArtifactType;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.TEAM_DEF__TEAM_WORKFLOW_ARTIFACT_TYPE, oldTeamWorkflowArtifactType, teamWorkflowArtifactType));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<String> getAccessContextId()
  {
    if (accessContextId == null)
    {
      accessContextId = new EDataTypeEList<String>(String.class, this, AtsDslPackage.TEAM_DEF__ACCESS_CONTEXT_ID);
    }
    return accessContextId;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<VersionDef> getVersion()
  {
    if (version == null)
    {
      version = new EObjectContainmentEList<VersionDef>(VersionDef.class, this, AtsDslPackage.TEAM_DEF__VERSION);
    }
    return version;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<String> getRules()
  {
    if (rules == null)
    {
      rules = new EDataTypeEList<String>(String.class, this, AtsDslPackage.TEAM_DEF__RULES);
    }
    return rules;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<TeamDef> getChildren()
  {
    if (children == null)
    {
      children = new EObjectContainmentEList<TeamDef>(TeamDef.class, this, AtsDslPackage.TEAM_DEF__CHILDREN);
    }
    return children;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case AtsDslPackage.TEAM_DEF__LEAD:
        return ((InternalEList<?>)getLead()).basicRemove(otherEnd, msgs);
      case AtsDslPackage.TEAM_DEF__MEMBER:
        return ((InternalEList<?>)getMember()).basicRemove(otherEnd, msgs);
      case AtsDslPackage.TEAM_DEF__PRIVILEGED:
        return ((InternalEList<?>)getPrivileged()).basicRemove(otherEnd, msgs);
      case AtsDslPackage.TEAM_DEF__VERSION:
        return ((InternalEList<?>)getVersion()).basicRemove(otherEnd, msgs);
      case AtsDslPackage.TEAM_DEF__CHILDREN:
        return ((InternalEList<?>)getChildren()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case AtsDslPackage.TEAM_DEF__NAME:
        return getName();
      case AtsDslPackage.TEAM_DEF__TEAM_DEF_OPTION:
        return getTeamDefOption();
      case AtsDslPackage.TEAM_DEF__UUID:
        return getUuid();
      case AtsDslPackage.TEAM_DEF__ACTIVE:
        return getActive();
      case AtsDslPackage.TEAM_DEF__STATIC_ID:
        return getStaticId();
      case AtsDslPackage.TEAM_DEF__LEAD:
        return getLead();
      case AtsDslPackage.TEAM_DEF__MEMBER:
        return getMember();
      case AtsDslPackage.TEAM_DEF__PRIVILEGED:
        return getPrivileged();
      case AtsDslPackage.TEAM_DEF__WORK_DEFINITION:
        return getWorkDefinition();
      case AtsDslPackage.TEAM_DEF__RELATED_TASK_WORK_DEFINITION:
        return getRelatedTaskWorkDefinition();
      case AtsDslPackage.TEAM_DEF__TEAM_WORKFLOW_ARTIFACT_TYPE:
        return getTeamWorkflowArtifactType();
      case AtsDslPackage.TEAM_DEF__ACCESS_CONTEXT_ID:
        return getAccessContextId();
      case AtsDslPackage.TEAM_DEF__VERSION:
        return getVersion();
      case AtsDslPackage.TEAM_DEF__RULES:
        return getRules();
      case AtsDslPackage.TEAM_DEF__CHILDREN:
        return getChildren();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case AtsDslPackage.TEAM_DEF__NAME:
        setName((String)newValue);
        return;
      case AtsDslPackage.TEAM_DEF__TEAM_DEF_OPTION:
        getTeamDefOption().clear();
        getTeamDefOption().addAll((Collection<? extends String>)newValue);
        return;
      case AtsDslPackage.TEAM_DEF__UUID:
        setUuid((Integer)newValue);
        return;
      case AtsDslPackage.TEAM_DEF__ACTIVE:
        setActive((BooleanDef)newValue);
        return;
      case AtsDslPackage.TEAM_DEF__STATIC_ID:
        getStaticId().clear();
        getStaticId().addAll((Collection<? extends String>)newValue);
        return;
      case AtsDslPackage.TEAM_DEF__LEAD:
        getLead().clear();
        getLead().addAll((Collection<? extends UserRef>)newValue);
        return;
      case AtsDslPackage.TEAM_DEF__MEMBER:
        getMember().clear();
        getMember().addAll((Collection<? extends UserRef>)newValue);
        return;
      case AtsDslPackage.TEAM_DEF__PRIVILEGED:
        getPrivileged().clear();
        getPrivileged().addAll((Collection<? extends UserRef>)newValue);
        return;
      case AtsDslPackage.TEAM_DEF__WORK_DEFINITION:
        setWorkDefinition((String)newValue);
        return;
      case AtsDslPackage.TEAM_DEF__RELATED_TASK_WORK_DEFINITION:
        setRelatedTaskWorkDefinition((String)newValue);
        return;
      case AtsDslPackage.TEAM_DEF__TEAM_WORKFLOW_ARTIFACT_TYPE:
        setTeamWorkflowArtifactType((String)newValue);
        return;
      case AtsDslPackage.TEAM_DEF__ACCESS_CONTEXT_ID:
        getAccessContextId().clear();
        getAccessContextId().addAll((Collection<? extends String>)newValue);
        return;
      case AtsDslPackage.TEAM_DEF__VERSION:
        getVersion().clear();
        getVersion().addAll((Collection<? extends VersionDef>)newValue);
        return;
      case AtsDslPackage.TEAM_DEF__RULES:
        getRules().clear();
        getRules().addAll((Collection<? extends String>)newValue);
        return;
      case AtsDslPackage.TEAM_DEF__CHILDREN:
        getChildren().clear();
        getChildren().addAll((Collection<? extends TeamDef>)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case AtsDslPackage.TEAM_DEF__NAME:
        setName(NAME_EDEFAULT);
        return;
      case AtsDslPackage.TEAM_DEF__TEAM_DEF_OPTION:
        getTeamDefOption().clear();
        return;
      case AtsDslPackage.TEAM_DEF__UUID:
        setUuid(UUID_EDEFAULT);
        return;
      case AtsDslPackage.TEAM_DEF__ACTIVE:
        setActive(ACTIVE_EDEFAULT);
        return;
      case AtsDslPackage.TEAM_DEF__STATIC_ID:
        getStaticId().clear();
        return;
      case AtsDslPackage.TEAM_DEF__LEAD:
        getLead().clear();
        return;
      case AtsDslPackage.TEAM_DEF__MEMBER:
        getMember().clear();
        return;
      case AtsDslPackage.TEAM_DEF__PRIVILEGED:
        getPrivileged().clear();
        return;
      case AtsDslPackage.TEAM_DEF__WORK_DEFINITION:
        setWorkDefinition(WORK_DEFINITION_EDEFAULT);
        return;
      case AtsDslPackage.TEAM_DEF__RELATED_TASK_WORK_DEFINITION:
        setRelatedTaskWorkDefinition(RELATED_TASK_WORK_DEFINITION_EDEFAULT);
        return;
      case AtsDslPackage.TEAM_DEF__TEAM_WORKFLOW_ARTIFACT_TYPE:
        setTeamWorkflowArtifactType(TEAM_WORKFLOW_ARTIFACT_TYPE_EDEFAULT);
        return;
      case AtsDslPackage.TEAM_DEF__ACCESS_CONTEXT_ID:
        getAccessContextId().clear();
        return;
      case AtsDslPackage.TEAM_DEF__VERSION:
        getVersion().clear();
        return;
      case AtsDslPackage.TEAM_DEF__RULES:
        getRules().clear();
        return;
      case AtsDslPackage.TEAM_DEF__CHILDREN:
        getChildren().clear();
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case AtsDslPackage.TEAM_DEF__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case AtsDslPackage.TEAM_DEF__TEAM_DEF_OPTION:
        return teamDefOption != null && !teamDefOption.isEmpty();
      case AtsDslPackage.TEAM_DEF__UUID:
        return uuid != UUID_EDEFAULT;
      case AtsDslPackage.TEAM_DEF__ACTIVE:
        return active != ACTIVE_EDEFAULT;
      case AtsDslPackage.TEAM_DEF__STATIC_ID:
        return staticId != null && !staticId.isEmpty();
      case AtsDslPackage.TEAM_DEF__LEAD:
        return lead != null && !lead.isEmpty();
      case AtsDslPackage.TEAM_DEF__MEMBER:
        return member != null && !member.isEmpty();
      case AtsDslPackage.TEAM_DEF__PRIVILEGED:
        return privileged != null && !privileged.isEmpty();
      case AtsDslPackage.TEAM_DEF__WORK_DEFINITION:
        return WORK_DEFINITION_EDEFAULT == null ? workDefinition != null : !WORK_DEFINITION_EDEFAULT.equals(workDefinition);
      case AtsDslPackage.TEAM_DEF__RELATED_TASK_WORK_DEFINITION:
        return RELATED_TASK_WORK_DEFINITION_EDEFAULT == null ? relatedTaskWorkDefinition != null : !RELATED_TASK_WORK_DEFINITION_EDEFAULT.equals(relatedTaskWorkDefinition);
      case AtsDslPackage.TEAM_DEF__TEAM_WORKFLOW_ARTIFACT_TYPE:
        return TEAM_WORKFLOW_ARTIFACT_TYPE_EDEFAULT == null ? teamWorkflowArtifactType != null : !TEAM_WORKFLOW_ARTIFACT_TYPE_EDEFAULT.equals(teamWorkflowArtifactType);
      case AtsDslPackage.TEAM_DEF__ACCESS_CONTEXT_ID:
        return accessContextId != null && !accessContextId.isEmpty();
      case AtsDslPackage.TEAM_DEF__VERSION:
        return version != null && !version.isEmpty();
      case AtsDslPackage.TEAM_DEF__RULES:
        return rules != null && !rules.isEmpty();
      case AtsDslPackage.TEAM_DEF__CHILDREN:
        return children != null && !children.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(", teamDefOption: ");
    result.append(teamDefOption);
    result.append(", uuid: ");
    result.append(uuid);
    result.append(", active: ");
    result.append(active);
    result.append(", staticId: ");
    result.append(staticId);
    result.append(", workDefinition: ");
    result.append(workDefinition);
    result.append(", relatedTaskWorkDefinition: ");
    result.append(relatedTaskWorkDefinition);
    result.append(", teamWorkflowArtifactType: ");
    result.append(teamWorkflowArtifactType);
    result.append(", accessContextId: ");
    result.append(accessContextId);
    result.append(", rules: ");
    result.append(rules);
    result.append(')');
    return result.toString();
  }

} //TeamDefImpl
