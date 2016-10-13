/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>XAttribute Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getBaseAttributeType <em>Base Attribute Type</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getOverride <em>Override</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getDataProvider <em>Data Provider</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getMin <em>Min</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getMax <em>Max</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getTaggerId <em>Tagger Id</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getEnumType <em>Enum Type</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getDefaultValue <em>Default Value</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getFileExtension <em>File Extension</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getMediaType <em>Media Type</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXAttributeType()
 * @model
 * @generated
 */
public interface XAttributeType extends OseeType
{
  /**
   * Returns the value of the '<em><b>Base Attribute Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Base Attribute Type</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Base Attribute Type</em>' attribute.
   * @see #setBaseAttributeType(String)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXAttributeType_BaseAttributeType()
   * @model
   * @generated
   */
  String getBaseAttributeType();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getBaseAttributeType <em>Base Attribute Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Base Attribute Type</em>' attribute.
   * @see #getBaseAttributeType()
   * @generated
   */
  void setBaseAttributeType(String value);

  /**
   * Returns the value of the '<em><b>Override</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Override</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Override</em>' reference.
   * @see #setOverride(XAttributeType)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXAttributeType_Override()
   * @model
   * @generated
   */
  XAttributeType getOverride();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getOverride <em>Override</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Override</em>' reference.
   * @see #getOverride()
   * @generated
   */
  void setOverride(XAttributeType value);

  /**
   * Returns the value of the '<em><b>Data Provider</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Data Provider</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Data Provider</em>' attribute.
   * @see #setDataProvider(String)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXAttributeType_DataProvider()
   * @model
   * @generated
   */
  String getDataProvider();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getDataProvider <em>Data Provider</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Data Provider</em>' attribute.
   * @see #getDataProvider()
   * @generated
   */
  void setDataProvider(String value);

  /**
   * Returns the value of the '<em><b>Min</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Min</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Min</em>' attribute.
   * @see #setMin(String)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXAttributeType_Min()
   * @model
   * @generated
   */
  String getMin();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getMin <em>Min</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Min</em>' attribute.
   * @see #getMin()
   * @generated
   */
  void setMin(String value);

  /**
   * Returns the value of the '<em><b>Max</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Max</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Max</em>' attribute.
   * @see #setMax(String)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXAttributeType_Max()
   * @model
   * @generated
   */
  String getMax();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getMax <em>Max</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Max</em>' attribute.
   * @see #getMax()
   * @generated
   */
  void setMax(String value);

  /**
   * Returns the value of the '<em><b>Tagger Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Tagger Id</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Tagger Id</em>' attribute.
   * @see #setTaggerId(String)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXAttributeType_TaggerId()
   * @model
   * @generated
   */
  String getTaggerId();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getTaggerId <em>Tagger Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Tagger Id</em>' attribute.
   * @see #getTaggerId()
   * @generated
   */
  void setTaggerId(String value);

  /**
   * Returns the value of the '<em><b>Enum Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Enum Type</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Enum Type</em>' reference.
   * @see #setEnumType(XOseeEnumType)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXAttributeType_EnumType()
   * @model
   * @generated
   */
  XOseeEnumType getEnumType();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getEnumType <em>Enum Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Enum Type</em>' reference.
   * @see #getEnumType()
   * @generated
   */
  void setEnumType(XOseeEnumType value);

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
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXAttributeType_Description()
   * @model
   * @generated
   */
  String getDescription();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getDescription <em>Description</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Description</em>' attribute.
   * @see #getDescription()
   * @generated
   */
  void setDescription(String value);

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
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXAttributeType_DefaultValue()
   * @model
   * @generated
   */
  String getDefaultValue();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getDefaultValue <em>Default Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Default Value</em>' attribute.
   * @see #getDefaultValue()
   * @generated
   */
  void setDefaultValue(String value);

  /**
   * Returns the value of the '<em><b>File Extension</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>File Extension</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>File Extension</em>' attribute.
   * @see #setFileExtension(String)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXAttributeType_FileExtension()
   * @model
   * @generated
   */
  String getFileExtension();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getFileExtension <em>File Extension</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>File Extension</em>' attribute.
   * @see #getFileExtension()
   * @generated
   */
  void setFileExtension(String value);

  /**
   * Returns the value of the '<em><b>Media Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Media Type</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Media Type</em>' attribute.
   * @see #setMediaType(String)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXAttributeType_MediaType()
   * @model
   * @generated
   */
  String getMediaType();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getMediaType <em>Media Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Media Type</em>' attribute.
   * @see #getMediaType()
   * @generated
   */
  void setMediaType(String value);

} // XAttributeType
