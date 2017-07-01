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
import org.eclipse.osee.ats.dsl.atsDsl.AttrDef;
import org.eclipse.osee.ats.dsl.atsDsl.BooleanDef;
import org.eclipse.osee.ats.dsl.atsDsl.ProgramDef;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Program Def</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ProgramDefImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ProgramDefImpl#getProgramDefOption <em>Program Def Option</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ProgramDefImpl#getUuid <em>Uuid</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ProgramDefImpl#getArtifactTypeName <em>Artifact Type Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ProgramDefImpl#getActive <em>Active</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ProgramDefImpl#getNamespace <em>Namespace</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ProgramDefImpl#getTeamDefinition <em>Team Definition</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ProgramDefImpl#getAttributes <em>Attributes</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ProgramDefImpl extends MinimalEObjectImpl.Container implements ProgramDef
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
   * The cached value of the '{@link #getProgramDefOption() <em>Program Def Option</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getProgramDefOption()
   * @generated
   * @ordered
   */
  protected EList<String> programDefOption;

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
   * The default value of the '{@link #getArtifactTypeName() <em>Artifact Type Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getArtifactTypeName()
   * @generated
   * @ordered
   */
  protected static final String ARTIFACT_TYPE_NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getArtifactTypeName() <em>Artifact Type Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getArtifactTypeName()
   * @generated
   * @ordered
   */
  protected String artifactTypeName = ARTIFACT_TYPE_NAME_EDEFAULT;

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
   * The default value of the '{@link #getNamespace() <em>Namespace</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getNamespace()
   * @generated
   * @ordered
   */
  protected static final String NAMESPACE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getNamespace() <em>Namespace</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getNamespace()
   * @generated
   * @ordered
   */
  protected String namespace = NAMESPACE_EDEFAULT;

  /**
   * The default value of the '{@link #getTeamDefinition() <em>Team Definition</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTeamDefinition()
   * @generated
   * @ordered
   */
  protected static final String TEAM_DEFINITION_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getTeamDefinition() <em>Team Definition</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTeamDefinition()
   * @generated
   * @ordered
   */
  protected String teamDefinition = TEAM_DEFINITION_EDEFAULT;

  /**
   * The cached value of the '{@link #getAttributes() <em>Attributes</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttributes()
   * @generated
   * @ordered
   */
  protected EList<AttrDef> attributes;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ProgramDefImpl()
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
    return AtsDslPackage.Literals.PROGRAM_DEF;
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
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.PROGRAM_DEF__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<String> getProgramDefOption()
  {
    if (programDefOption == null)
    {
      programDefOption = new EDataTypeEList<String>(String.class, this, AtsDslPackage.PROGRAM_DEF__PROGRAM_DEF_OPTION);
    }
    return programDefOption;
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
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.PROGRAM_DEF__UUID, oldUuid, uuid));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getArtifactTypeName()
  {
    return artifactTypeName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setArtifactTypeName(String newArtifactTypeName)
  {
    String oldArtifactTypeName = artifactTypeName;
    artifactTypeName = newArtifactTypeName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.PROGRAM_DEF__ARTIFACT_TYPE_NAME, oldArtifactTypeName, artifactTypeName));
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
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.PROGRAM_DEF__ACTIVE, oldActive, active));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getNamespace()
  {
    return namespace;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setNamespace(String newNamespace)
  {
    String oldNamespace = namespace;
    namespace = newNamespace;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.PROGRAM_DEF__NAMESPACE, oldNamespace, namespace));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getTeamDefinition()
  {
    return teamDefinition;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTeamDefinition(String newTeamDefinition)
  {
    String oldTeamDefinition = teamDefinition;
    teamDefinition = newTeamDefinition;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.PROGRAM_DEF__TEAM_DEFINITION, oldTeamDefinition, teamDefinition));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<AttrDef> getAttributes()
  {
    if (attributes == null)
    {
      attributes = new EObjectContainmentEList<AttrDef>(AttrDef.class, this, AtsDslPackage.PROGRAM_DEF__ATTRIBUTES);
    }
    return attributes;
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
      case AtsDslPackage.PROGRAM_DEF__ATTRIBUTES:
        return ((InternalEList<?>)getAttributes()).basicRemove(otherEnd, msgs);
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
      case AtsDslPackage.PROGRAM_DEF__NAME:
        return getName();
      case AtsDslPackage.PROGRAM_DEF__PROGRAM_DEF_OPTION:
        return getProgramDefOption();
      case AtsDslPackage.PROGRAM_DEF__UUID:
        return getUuid();
      case AtsDslPackage.PROGRAM_DEF__ARTIFACT_TYPE_NAME:
        return getArtifactTypeName();
      case AtsDslPackage.PROGRAM_DEF__ACTIVE:
        return getActive();
      case AtsDslPackage.PROGRAM_DEF__NAMESPACE:
        return getNamespace();
      case AtsDslPackage.PROGRAM_DEF__TEAM_DEFINITION:
        return getTeamDefinition();
      case AtsDslPackage.PROGRAM_DEF__ATTRIBUTES:
        return getAttributes();
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
      case AtsDslPackage.PROGRAM_DEF__NAME:
        setName((String)newValue);
        return;
      case AtsDslPackage.PROGRAM_DEF__PROGRAM_DEF_OPTION:
        getProgramDefOption().clear();
        getProgramDefOption().addAll((Collection<? extends String>)newValue);
        return;
      case AtsDslPackage.PROGRAM_DEF__UUID:
        setUuid((Integer)newValue);
        return;
      case AtsDslPackage.PROGRAM_DEF__ARTIFACT_TYPE_NAME:
        setArtifactTypeName((String)newValue);
        return;
      case AtsDslPackage.PROGRAM_DEF__ACTIVE:
        setActive((BooleanDef)newValue);
        return;
      case AtsDslPackage.PROGRAM_DEF__NAMESPACE:
        setNamespace((String)newValue);
        return;
      case AtsDslPackage.PROGRAM_DEF__TEAM_DEFINITION:
        setTeamDefinition((String)newValue);
        return;
      case AtsDslPackage.PROGRAM_DEF__ATTRIBUTES:
        getAttributes().clear();
        getAttributes().addAll((Collection<? extends AttrDef>)newValue);
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
      case AtsDslPackage.PROGRAM_DEF__NAME:
        setName(NAME_EDEFAULT);
        return;
      case AtsDslPackage.PROGRAM_DEF__PROGRAM_DEF_OPTION:
        getProgramDefOption().clear();
        return;
      case AtsDslPackage.PROGRAM_DEF__UUID:
        setUuid(UUID_EDEFAULT);
        return;
      case AtsDslPackage.PROGRAM_DEF__ARTIFACT_TYPE_NAME:
        setArtifactTypeName(ARTIFACT_TYPE_NAME_EDEFAULT);
        return;
      case AtsDslPackage.PROGRAM_DEF__ACTIVE:
        setActive(ACTIVE_EDEFAULT);
        return;
      case AtsDslPackage.PROGRAM_DEF__NAMESPACE:
        setNamespace(NAMESPACE_EDEFAULT);
        return;
      case AtsDslPackage.PROGRAM_DEF__TEAM_DEFINITION:
        setTeamDefinition(TEAM_DEFINITION_EDEFAULT);
        return;
      case AtsDslPackage.PROGRAM_DEF__ATTRIBUTES:
        getAttributes().clear();
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
      case AtsDslPackage.PROGRAM_DEF__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case AtsDslPackage.PROGRAM_DEF__PROGRAM_DEF_OPTION:
        return programDefOption != null && !programDefOption.isEmpty();
      case AtsDslPackage.PROGRAM_DEF__UUID:
        return uuid != UUID_EDEFAULT;
      case AtsDslPackage.PROGRAM_DEF__ARTIFACT_TYPE_NAME:
        return ARTIFACT_TYPE_NAME_EDEFAULT == null ? artifactTypeName != null : !ARTIFACT_TYPE_NAME_EDEFAULT.equals(artifactTypeName);
      case AtsDslPackage.PROGRAM_DEF__ACTIVE:
        return active != ACTIVE_EDEFAULT;
      case AtsDslPackage.PROGRAM_DEF__NAMESPACE:
        return NAMESPACE_EDEFAULT == null ? namespace != null : !NAMESPACE_EDEFAULT.equals(namespace);
      case AtsDslPackage.PROGRAM_DEF__TEAM_DEFINITION:
        return TEAM_DEFINITION_EDEFAULT == null ? teamDefinition != null : !TEAM_DEFINITION_EDEFAULT.equals(teamDefinition);
      case AtsDslPackage.PROGRAM_DEF__ATTRIBUTES:
        return attributes != null && !attributes.isEmpty();
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
    result.append(", programDefOption: ");
    result.append(programDefOption);
    result.append(", uuid: ");
    result.append(uuid);
    result.append(", artifactTypeName: ");
    result.append(artifactTypeName);
    result.append(", active: ");
    result.append(active);
    result.append(", namespace: ");
    result.append(namespace);
    result.append(", teamDefinition: ");
    result.append(teamDefinition);
    result.append(')');
    return result.toString();
  }

} //ProgramDefImpl
