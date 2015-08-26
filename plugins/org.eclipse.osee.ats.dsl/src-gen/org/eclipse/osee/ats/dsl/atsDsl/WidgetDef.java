/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Widget Def</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getAttributeName <em>Attribute Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getXWidgetName <em>XWidget Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getDefaultValue <em>Default Value</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getHeight <em>Height</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getOption <em>Option</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getMinConstraint <em>Min Constraint</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getMaxConstraint <em>Max Constraint</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getWidgetDef()
 * @model
 * @generated
 */
public interface WidgetDef extends EObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getWidgetDef_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Attribute Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Attribute Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Attribute Name</em>' attribute.
   * @see #setAttributeName(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getWidgetDef_AttributeName()
   * @model
   * @generated
   */
  String getAttributeName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getAttributeName <em>Attribute Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Attribute Name</em>' attribute.
   * @see #getAttributeName()
   * @generated
   */
  void setAttributeName(String value);

  /**
   * Returns the value of the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Description</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Description</em>' attribute.
   * @see #setDescription(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getWidgetDef_Description()
   * @model
   * @generated
   */
  String getDescription();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getDescription <em>Description</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Description</em>' attribute.
   * @see #getDescription()
   * @generated
   */
  void setDescription(String value);

  /**
   * Returns the value of the '<em><b>XWidget Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>XWidget Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>XWidget Name</em>' attribute.
   * @see #setXWidgetName(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getWidgetDef_XWidgetName()
   * @model
   * @generated
   */
  String getXWidgetName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getXWidgetName <em>XWidget Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>XWidget Name</em>' attribute.
   * @see #getXWidgetName()
   * @generated
   */
  void setXWidgetName(String value);

  /**
   * Returns the value of the '<em><b>Default Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Default Value</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Default Value</em>' attribute.
   * @see #setDefaultValue(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getWidgetDef_DefaultValue()
   * @model
   * @generated
   */
  String getDefaultValue();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getDefaultValue <em>Default Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Default Value</em>' attribute.
   * @see #getDefaultValue()
   * @generated
   */
  void setDefaultValue(String value);

  /**
   * Returns the value of the '<em><b>Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Height</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Height</em>' attribute.
   * @see #setHeight(int)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getWidgetDef_Height()
   * @model
   * @generated
   */
  int getHeight();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getHeight <em>Height</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Height</em>' attribute.
   * @see #getHeight()
   * @generated
   */
  void setHeight(int value);

  /**
   * Returns the value of the '<em><b>Option</b></em>' attribute list.
   * The list contents are of type {@link java.lang.String}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Option</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Option</em>' attribute list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getWidgetDef_Option()
   * @model unique="false"
   * @generated
   */
  EList<String> getOption();

  /**
   * Returns the value of the '<em><b>Min Constraint</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Min Constraint</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Min Constraint</em>' attribute.
   * @see #setMinConstraint(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getWidgetDef_MinConstraint()
   * @model
   * @generated
   */
  String getMinConstraint();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getMinConstraint <em>Min Constraint</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Min Constraint</em>' attribute.
   * @see #getMinConstraint()
   * @generated
   */
  void setMinConstraint(String value);

  /**
   * Returns the value of the '<em><b>Max Constraint</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Max Constraint</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Max Constraint</em>' attribute.
   * @see #setMaxConstraint(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getWidgetDef_MaxConstraint()
   * @model
   * @generated
   */
  String getMaxConstraint();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef#getMaxConstraint <em>Max Constraint</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Max Constraint</em>' attribute.
   * @see #getMaxConstraint()
   * @generated
   */
  void setMaxConstraint(String value);

} // WidgetDef
