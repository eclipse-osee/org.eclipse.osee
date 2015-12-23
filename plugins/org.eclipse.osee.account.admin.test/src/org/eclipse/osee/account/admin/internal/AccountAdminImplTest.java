/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.account.admin.internal;

import static org.eclipse.osee.account.admin.AccountConstants.ACCOUNT_DISPLAY_NAME_VALIDATION_PATTERN;
import static org.eclipse.osee.account.admin.AccountConstants.ACCOUNT_EMAIL_VALIDATION_PATTERN;
import static org.eclipse.osee.account.admin.AccountConstants.ACCOUNT_USERNAME_VALIDATION_PATTERN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.AccountField;
import org.eclipse.osee.account.admin.AccountLoginException;
import org.eclipse.osee.account.admin.AccountLoginRequest;
import org.eclipse.osee.account.admin.AccountLoginRequestBuilder;
import org.eclipse.osee.account.admin.AccountPreferences;
import org.eclipse.osee.account.admin.AccountSession;
import org.eclipse.osee.account.admin.CreateAccountRequest;
import org.eclipse.osee.account.admin.ds.AccountStorage;
import org.eclipse.osee.account.admin.internal.validator.Validator;
import org.eclipse.osee.authentication.admin.AuthenticatedUser;
import org.eclipse.osee.authentication.admin.AuthenticationAdmin;
import org.eclipse.osee.authentication.admin.AuthenticationRequest;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.logger.Log;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * Test Case for {@link AccountAdminImpl}
 *
 * @author Roberto E. Escobar
 */
public class AccountAdminImplTest {

   private static final long ID = 123121412L;
   private static final String GUID_STRING = GUID.create();
   private static final String USERNAME = "atest";
   private static final String EMAIL = "atest@email.com";
   private static final String NAME = "myName";

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private Log logger;
   @Mock private AccountStorage storage;
   @Mock private AuthenticationAdmin authenticationAdmin;

   @Mock private CreateAccountRequest request;
   @Mock private Account account;
   @Mock private AccountSession accountSession;
   @Mock private AccountPreferences preferences;
   @Mock private Identifiable<String> newAccount;
   @Mock private AuthenticatedUser authenticatedUser;
   @Captor private ArgumentCaptor<Map<String, String>> newPrefsCaptor;
   @Captor private ArgumentCaptor<String> tokenCaptor;
   @Captor private ArgumentCaptor<AuthenticationRequest> authenticationRequestCaptor;
   // @formatter:on

   private AccountAdminImpl accountAdmin;

   @Before
   public void testSetup() {
      initMocks(this);

      accountAdmin = new AccountAdminImpl();
      accountAdmin.setLogger(logger);
      accountAdmin.setAccountStorage(storage);
      accountAdmin.setAuthenticationAdmin(authenticationAdmin);
      accountAdmin.start(Collections.<String, Object> emptyMap());

      when(newAccount.getGuid()).thenReturn(GUID_STRING);
   }

   @Test
   public void testGetAllAccounts() {
      accountAdmin.getAllAccounts();
      verify(storage).getAllAccounts();
   }

   @Test
   public void testGetAccountByIdWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("id cannot be null");
      accountAdmin.getAccountById(null);
   }

   @Test
   public void testGetById() {
      accountAdmin.getAccountById(newAccount);

      verify(storage).getAccountByGuid(GUID_STRING);
   }

   @Test
   public void testGetAccountByGuidWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("guid cannot be null");
      accountAdmin.getAccountByGuid(null);
   }

   @Test
   public void testGetByGuiId() {
      accountAdmin.getAccountByGuid(GUID_STRING);

      verify(storage).getAccountByGuid(GUID_STRING);
   }

   @Test
   public void testGetAccountById() {
      accountAdmin.getAccountById(ID);

      verify(storage).getAccountByLocalId(ID);
   }

   @Test
   public void testGetAccountByUserNameWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("username cannot be null");
      accountAdmin.getAccountByUserName(null);
   }

   @Test
   public void testGetByUserName() {
      accountAdmin.getAccountByUserName(USERNAME);

      verify(storage).getAccountByUserName(USERNAME);
   }

   @Test
   public void testGetAccountByEmailWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("email cannot be null");
      accountAdmin.getAccountByEmail(null);
   }

   @Test
   public void testGetByEmail() {
      accountAdmin.getAccountByEmail(EMAIL);

      verify(storage).getAccountByEmail(EMAIL);
   }

   @Test
   public void testGetAccountByNameWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("name cannot be null");
      accountAdmin.getAccountByName(null);
   }

   @Test
   public void testGetByName() {
      accountAdmin.getAccountByName(NAME);

      verify(storage).getAccountByName(NAME);
   }

   @Test
   public void testGetAccountPrefsByUuidWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("guid cannot be null");
      accountAdmin.getAccountPreferencesByGuid(null);
   }

   @Test
   public void testGetAccountPrefsByUuid() {
      accountAdmin.getAccountPreferencesByGuid(GUID_STRING);

      verify(storage).getAccountPreferencesByGuid(GUID_STRING);
   }

   @Test
   public void testGetAccountPrefsByIdWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("id cannot be null");
      accountAdmin.getAccountPreferencesById(null);
   }

   @Test
   public void testGetAccountPrefsById() {
      accountAdmin.getAccountPreferencesById(newAccount);

      verify(storage).getAccountPreferencesByGuid(GUID_STRING);
   }

   @Test
   public void testGetAccountPreferencesById() {
      accountAdmin.getAccountPreferencesById(ID);

      verify(storage).getAccountPreferencesById(ID);
   }

   @Test
   public void testCreateAccountRequestDataInvalid() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("create account request cannot be null");
      accountAdmin.createAccount(null);
   }

   @Test
   public void testCreateAccount() {
      when(request.getEmail()).thenReturn(EMAIL);
      when(request.getDisplayName()).thenReturn(NAME);
      when(request.getUserName()).thenReturn(USERNAME);

      accountAdmin.createAccount(request);

      verify(storage).createAccount(request);
   }

   @Test
   public void testSetActiveIdWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("account id cannot be null");
      accountAdmin.setActive(nullID(), true);
   }

   @Test
   public void testSetActiveModified() {
      ResultSet<Account> resultSet = ResultSets.singleton(account);

      when(storage.getAccountByGuid(GUID_STRING)).thenReturn(resultSet);
      when(account.isActive()).thenReturn(true);

      boolean modified = accountAdmin.setActive(newAccount, false);
      assertTrue(modified);

      verify(storage).getAccountByGuid(GUID_STRING);
      verify(storage).setActive(account, false);
   }

   @Test
   public void testSetActiveNotModified() {
      ResultSet<Account> resultSet = ResultSets.singleton(account);

      when(storage.getAccountByGuid(GUID_STRING)).thenReturn(resultSet);
      when(account.isActive()).thenReturn(true);

      boolean modified = accountAdmin.setActive(newAccount, true);
      assertFalse(modified);

      verify(storage).getAccountByGuid(GUID_STRING);
      verify(storage, times(0)).setActive(account, true);
   }

   @Test
   public void testDeleteAccountIdWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("account id cannot be null");
      accountAdmin.deleteAccount(nullID());
   }

   @Test
   public void testDeleteAccountIdModified() {
      ResultSet<Account> resultSet = ResultSets.singleton(account);

      when(storage.getAccountByGuid(GUID_STRING)).thenReturn(resultSet);

      boolean modified = accountAdmin.deleteAccount(newAccount);
      assertTrue(modified);

      verify(storage).getAccountByGuid(GUID_STRING);
      verify(storage).deleteAccount(account);
   }

   @Test
   public void testDeleteAccountIdNotModified() {
      @SuppressWarnings("unchecked")
      ResultSet<Account> resultSet = Mockito.mock(ResultSet.class);

      when(storage.getAccountByGuid(GUID_STRING)).thenReturn(resultSet);
      when(resultSet.getOneOrNull()).thenReturn(null);

      boolean modified = accountAdmin.deleteAccount(newAccount);
      assertFalse(modified);

      verify(storage, times(0)).deleteAccount(null);
   }

   @Test
   public void testSetAccountPreferencesIdWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("account preference id cannot be null");
      accountAdmin.setAccountPreferences(nullID(), Collections.<String, String> emptyMap());
   }

   @Test
   public void testSetAccountPreferencesPrefsWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("preferences cannot be null");
      accountAdmin.setAccountPreferences(newAccount, null);
   }

   @Test
   public void testSetAccountPreferencesModified() {
      Map<String, String> original = new HashMap<>();
      original.put("1", "2");

      Map<String, String> newMap = new HashMap<>();
      newMap.put("1", "3");

      ResultSet<AccountPreferences> resultSet = ResultSets.singleton(preferences);

      when(storage.getAccountPreferencesByGuid(GUID_STRING)).thenReturn(resultSet);
      when(preferences.asMap()).thenReturn(original);

      boolean modified = accountAdmin.setAccountPreferences(newAccount, newMap);
      assertTrue(modified);

      verify(storage).setAccountPreferences(preferences, newMap);
      verify(storage).getAccountPreferencesByGuid(GUID_STRING);
      verify(preferences).asMap();
   }

   @Test
   public void testSetAccountPreferencesNotModified() {
      Map<String, String> original = new HashMap<>();
      original.put("1", "2");

      Map<String, String> newMap = new HashMap<>();
      newMap.put("1", "2");

      ResultSet<AccountPreferences> resultSet = ResultSets.singleton(preferences);

      when(storage.getAccountPreferencesByGuid(GUID_STRING)).thenReturn(resultSet);
      when(preferences.asMap()).thenReturn(original);

      boolean modified = accountAdmin.setAccountPreferences(newAccount, newMap);
      assertFalse(modified);

      verify(storage, times(0)).setAccountPreferences(newAccount, newMap);
      verify(storage).getAccountPreferencesByGuid(GUID_STRING);
      verify(preferences).asMap();
   }

   @Test
   public void testSetAccountPreferenceIdWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("account preference id cannot be null");
      accountAdmin.setAccountPreference(nullID(), "b", "value");
   }

   @Test
   public void testSetAccountPreferenceKeyWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("account preference key cannot be null");
      accountAdmin.setAccountPreference(newAccount, null, "value");
   }

   @Test
   public void testSetAccountPreferenceValueWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("account preference value cannot be null - Use delete account preference instead");
      accountAdmin.setAccountPreference(newAccount, "b", null);
   }

   @Test
   public void testSetAccountPreferenceModified() {
      Map<String, String> map = new HashMap<>();
      map.put("a", "1");
      map.put("b", "2");
      map.put("c", "3");

      ResultSet<AccountPreferences> resultSet = ResultSets.singleton(preferences);

      when(storage.getAccountPreferencesByGuid(GUID_STRING)).thenReturn(resultSet);
      when(preferences.asMap()).thenReturn(map);

      boolean modified = accountAdmin.setAccountPreference(newAccount, "b", "123412");
      assertTrue(modified);

      verify(storage).getAccountPreferencesByGuid(GUID_STRING);
      verify(storage).setAccountPreferences(eq(preferences), newPrefsCaptor.capture());

      Map<String, String> actual = newPrefsCaptor.getValue();
      assertEquals(3, actual.size());
      assertEquals("1", actual.get("a"));
      assertEquals("123412", actual.get("b"));
      assertEquals("3", actual.get("c"));
   }

   @Test
   public void testSetAccountPreferenceNotModified() {
      Map<String, String> map = new HashMap<>();
      map.put("a", "1");
      map.put("b", "123412");
      map.put("c", "3");

      ResultSet<AccountPreferences> resultSet = ResultSets.singleton(preferences);

      when(storage.getAccountPreferencesByGuid(GUID_STRING)).thenReturn(resultSet);
      when(preferences.asMap()).thenReturn(map);

      boolean modified = accountAdmin.setAccountPreference(newAccount, "b", "123412");
      assertFalse(modified);

      verify(storage).getAccountPreferencesByGuid(GUID_STRING);
      verify(storage, times(0)).setAccountPreferences(eq(newAccount), anyMapOf(String.class, String.class));
   }

   @Test
   public void testDeleteAccountPreferenceIdWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("account preference id cannot be null");
      accountAdmin.deleteAccountPreference(nullID(), "b");
   }

   @Test
   public void testDeleteAccountPreferenceKeyWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("account preference key cannot be null");
      accountAdmin.deleteAccountPreference(newAccount, null);
   }

   @Test
   public void testDeleteAccountPreferenceModified() {
      Map<String, String> map = new HashMap<>();
      map.put("a", "1");
      map.put("b", "123412");
      map.put("c", "3");

      ResultSet<AccountPreferences> resultSet = ResultSets.singleton(preferences);

      when(storage.getAccountPreferencesByGuid(GUID_STRING)).thenReturn(resultSet);
      when(preferences.asMap()).thenReturn(map);

      boolean modified = accountAdmin.deleteAccountPreference(newAccount, "b");
      assertTrue(modified);

      verify(storage).getAccountPreferencesByGuid(GUID_STRING);
      verify(storage).setAccountPreferences(eq(preferences), newPrefsCaptor.capture());

      Map<String, String> actual = newPrefsCaptor.getValue();
      assertEquals(2, actual.size());
      assertEquals("1", actual.get("a"));
      assertEquals("3", actual.get("c"));
   }

   @Test
   public void testDeleteAccountPreferenceNotModified() {
      Map<String, String> map = new HashMap<>();
      map.put("a", "1");
      map.put("c", "3");

      ResultSet<AccountPreferences> resultSet = ResultSets.singleton(preferences);

      when(storage.getAccountPreferencesByGuid(GUID_STRING)).thenReturn(resultSet);
      when(preferences.asMap()).thenReturn(map);

      boolean modified = accountAdmin.deleteAccountPreference(newAccount, "b");
      assertFalse(modified);

      verify(storage).getAccountPreferencesByGuid(GUID_STRING);
      verify(storage, times(0)).setAccountPreferences(eq(newAccount), anyMapOf(String.class, String.class));
   }

   @Test
   public void testGetAccountByUniqueField() {
      ResultSet<Account> resultSet = ResultSets.singleton(account);

      when(storage.getAccountByEmail(EMAIL)).thenReturn(resultSet);

      ResultSet<Account> result = accountAdmin.getAccountByUniqueField(EMAIL);
      assertEquals(account, result.getExactlyOne());

      verify(storage).getAccountByEmail(EMAIL);
   }

   @Test
   public void testGetAccountPreferencesByUniqueField() {
      ResultSet<Account> resultSet = ResultSets.singleton(account);

      when(storage.getAccountByName(NAME)).thenReturn(resultSet);
      when(account.getPreferences()).thenReturn(preferences);

      ResultSet<AccountPreferences> result = accountAdmin.getAccountPreferencesByUniqueField(NAME);
      assertEquals(preferences, result.getExactlyOne());

      verify(storage).getAccountByName(NAME);
   }

   @Test
   public void testDeleteAccountByUniqueField() {
      ResultSet<Account> resultSet = ResultSets.singleton(account);

      when(storage.getAccountByEmail(EMAIL)).thenReturn(resultSet);

      boolean actual = accountAdmin.deleteAccount(EMAIL);
      assertEquals(true, actual);

      verify(storage).getAccountByEmail(EMAIL);
      verify(storage).deleteAccount(account);
   }

   @Test
   public void testSetActiveByUniqueField() {
      ResultSet<Account> resultSet = ResultSets.singleton(account);

      when(storage.getAccountByEmail(EMAIL)).thenReturn(resultSet);

      boolean actual = accountAdmin.setActive(EMAIL, true);
      assertEquals(true, actual);

      verify(storage).getAccountByEmail(EMAIL);
      verify(storage).setActive(account, true);
   }

   @Test
   public void testSetAccountPreferencesByUniqueField() {
      Map<String, String> original = new HashMap<>();
      original.put("a", "1");
      original.put("c", "3");

      Map<String, String> newMap = new HashMap<>();
      newMap.put("a", "1");

      ResultSet<Account> resultSet = ResultSets.singleton(account);

      when(storage.getAccountByEmail(EMAIL)).thenReturn(resultSet);
      when(account.getPreferences()).thenReturn(preferences);
      when(preferences.asMap()).thenReturn(original);

      boolean actual = accountAdmin.setAccountPreferences(EMAIL, newMap);
      assertEquals(true, actual);

      verify(storage).getAccountByEmail(EMAIL);
      verify(storage).setAccountPreferences(preferences, newMap);
   }

   @Test
   public void testAccountPreferenceByUniqueField() {
      Map<String, String> original = new HashMap<>();
      original.put("a", "1");
      original.put("c", "3");

      ResultSet<Account> resultSet = ResultSets.singleton(account);

      when(storage.getAccountByEmail(EMAIL)).thenReturn(resultSet);
      when(account.getPreferences()).thenReturn(preferences);
      when(preferences.asMap()).thenReturn(original);

      boolean actual = accountAdmin.setAccountPreference(EMAIL, "b", "2");
      assertEquals(true, actual);

      verify(storage).getAccountByEmail(EMAIL);
      verify(storage).setAccountPreferences(eq(preferences), newPrefsCaptor.capture());

      Map<String, String> newValues = newPrefsCaptor.getValue();
      assertEquals(3, newValues.size());
      assertEquals("1", newValues.get("a"));
      assertEquals("2", newValues.get("b"));
      assertEquals("3", newValues.get("c"));
   }

   @Test
   public void testDeleteAccountPreferenceByUniqueField() {
      Map<String, String> original = new HashMap<>();
      original.put("a", "1");
      original.put("b", "2");
      original.put("c", "3");

      ResultSet<Account> resultSet = ResultSets.singleton(account);

      when(storage.getAccountByEmail(EMAIL)).thenReturn(resultSet);
      when(account.getPreferences()).thenReturn(preferences);
      when(preferences.asMap()).thenReturn(original);

      boolean actual = accountAdmin.deleteAccountPreference(EMAIL, "b");
      assertEquals(true, actual);

      verify(storage).getAccountByEmail(EMAIL);
      verify(storage).setAccountPreferences(eq(preferences), newPrefsCaptor.capture());

      Map<String, String> newValues = newPrefsCaptor.getValue();
      assertEquals(2, newValues.size());
      assertEquals("1", newValues.get("a"));
      assertEquals("3", newValues.get("c"));
   }

   @Test
   public void testAccountLoginException() {
      String scheme = "myScheme";
      String userName = EMAIL;
      String password = "myPass";
      String accessDetails = "details";
      String remoteAddress = "blah";

      ResultSet<Account> resultSet = ResultSets.emptyResultSet();
      when(storage.getAccountByEmail(EMAIL)).thenReturn(resultSet);

      AccountLoginRequest request = AccountLoginRequestBuilder.newBuilder()//
      .userName(userName)//
      .password(password)//
      .scheme(scheme)//
      .remoteAddress(remoteAddress)//
      .accessedBy(accessDetails)//
      .build();

      when(authenticationAdmin.authenticate(any(AuthenticationRequest.class))).thenReturn(authenticatedUser);
      when(authenticatedUser.getName()).thenReturn(EMAIL);

      thrown.expect(AccountLoginException.class);
      thrown.expectMessage(
         "Login Error - Unable to find account for username[" + userName + "] using authentication scheme[" + scheme + "] and userId[" + userName + "]");
      accountAdmin.login(request);

      verify(storage, times(0)).createAccountSession(anyString(), any(Account.class), anyString(), anyString());
      verify(authenticationAdmin).authenticate(authenticationRequestCaptor.capture());

      AuthenticationRequest authRequest = authenticationRequestCaptor.getValue();
      assertEquals(scheme, authRequest.getScheme());
      assertEquals(userName, authRequest.getUserName());
      assertEquals(password, authRequest.getPassword());
   }

   @Test
   public void testAccountLogin() {
      String scheme = "myScheme";
      String userName = EMAIL;
      String password = "myPass";
      String accessDetails = "details";
      String remoteAddress = "blah";

      ResultSet<Account> resultSet = ResultSets.singleton(account);
      AccountSession session = Mockito.mock(AccountSession.class);

      when(storage.getAccountByEmail(EMAIL)).thenReturn(resultSet);
      when(storage.createAccountSession(anyString(), eq(account), anyString(), anyString())).thenReturn(session);
      when(authenticationAdmin.authenticate(any(AuthenticationRequest.class))).thenReturn(authenticatedUser);
      when(authenticatedUser.getName()).thenReturn(EMAIL);

      AccountLoginRequest request = AccountLoginRequestBuilder.newBuilder()//
      .userName(userName)//
      .password(password)//
      .scheme(scheme)//
      .remoteAddress(remoteAddress)//
      .accessedBy(accessDetails)//
      .build();

      AccountSession actual = accountAdmin.login(request);
      assertEquals(session, actual);

      verify(storage).createAccountSession(tokenCaptor.capture(), eq(account), eq(remoteAddress), eq(accessDetails));
      assertNotNull(tokenCaptor.getValue());

      verify(authenticationAdmin).authenticate(authenticationRequestCaptor.capture());

      AuthenticationRequest authRequest = authenticationRequestCaptor.getValue();
      assertEquals(scheme, authRequest.getScheme());
      assertEquals(userName, authRequest.getUserName());
      assertEquals(password, authRequest.getPassword());
   }

   @Test
   public void testGetAccountAccessByAccessTokenException() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("account session token cannot be null");
      accountAdmin.getAccountSessionBySessionToken(null);
   }

   @Test
   public void testGetAccountAccessByAccessToken() {
      String sessionToken = "myToken";

      ResultSet<AccountSession> resultSet = ResultSets.singleton(accountSession);
      when(storage.getAccountSessionBySessionToken(sessionToken)).thenReturn(resultSet);

      ResultSet<AccountSession> actual = accountAdmin.getAccountSessionBySessionToken(sessionToken);
      assertEquals(resultSet, actual);

      verify(storage).getAccountSessionBySessionToken(sessionToken);
   }

   @Test
   public void testLogoutModified() {
      String sessionToken = "myToken";

      ResultSet<AccountSession> resultSet = ResultSets.singleton(accountSession);
      when(storage.getAccountSessionBySessionToken(sessionToken)).thenReturn(resultSet);

      boolean actual = accountAdmin.logout(sessionToken);
      assertEquals(true, actual);

      verify(storage).getAccountSessionBySessionToken(sessionToken);
      verify(storage).deleteAccountSessionBySessionToken(sessionToken);
   }

   @Test
   public void testLogoutNotModified() {
      String sessionToken = "myToken";

      ResultSet<AccountSession> resultSet = ResultSets.emptyResultSet();
      when(storage.getAccountSessionBySessionToken(sessionToken)).thenReturn(resultSet);

      boolean actual = accountAdmin.logout(sessionToken);
      assertEquals(false, actual);

      verify(storage).getAccountSessionBySessionToken(sessionToken);
      verify(storage, times(0)).deleteAccountSessionBySessionToken(sessionToken);
   }

   @Test
   public void testGetAccountAccessByUniqueField() {
      ResultSet<Account> resultSet = ResultSets.singleton(account);
      ResultSet<AccountSession> resultSet2 = ResultSets.singleton(accountSession);

      when(storage.getAccountByEmail(EMAIL)).thenReturn(resultSet);
      when(account.getId()).thenReturn(ID);
      when(storage.getAccountSessionById(ID)).thenReturn(resultSet2);

      ResultSet<AccountSession> actual = accountAdmin.getAccountSessionByUniqueField(EMAIL);
      assertEquals(resultSet2, actual);

      verify(account).getId();
      verify(storage).getAccountSessionById(ID);
   }

   @Test
   public void testUpdateConfig() {
      String userNamePattern = "\\d+";
      String emailPattern = "(.*?)@gmail\\.com";
      String displayNamePattern = "[a-z ]+";

      Validator validator = accountAdmin.getValidator();
      assertEquals(true, validator.isValid(AccountField.EMAIL, "hello@hello.com"));
      assertEquals(true, validator.isValid(AccountField.USERNAME, "abcde"));
      assertEquals(true, validator.isValid(AccountField.DISPLAY_NAME, "1234"));

      Map<String, Object> props = new HashMap<>();
      props.put(ACCOUNT_USERNAME_VALIDATION_PATTERN, userNamePattern);
      props.put(ACCOUNT_EMAIL_VALIDATION_PATTERN, emailPattern);
      props.put(ACCOUNT_DISPLAY_NAME_VALIDATION_PATTERN, displayNamePattern);

      accountAdmin.update(props);

      assertEquals(false, validator.isValid(AccountField.EMAIL, "hello@hello.com"));
      assertEquals(false, validator.isValid(AccountField.USERNAME, "abcde"));
      assertEquals(false, validator.isValid(AccountField.DISPLAY_NAME, "1234"));

      assertEquals(true, validator.isValid(AccountField.EMAIL, "hello@gmail.com"));
      assertEquals(true, validator.isValid(AccountField.USERNAME, "1234"));
      assertEquals(true, validator.isValid(AccountField.DISPLAY_NAME, "abc def"));
   }

   private static Identifiable<String> nullID() {
      return null;
   }

}
