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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
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
   @Mock private AuthenticatedUser authenticatedUser;
   @Captor private ArgumentCaptor<Map<String, String>> newPrefsCaptor;
   @Captor private ArgumentCaptor<String> tokenCaptor;
   @Captor private ArgumentCaptor<AuthenticationRequest> authenticationRequestCaptor;
   // @formatter:on

   private final ArtifactId newAccountId = ArtifactId.valueOf(123121412);

   private AccountAdminImpl accountAdmin;

   @Before
   public void testSetup() {
      initMocks(this);

      accountAdmin = new AccountAdminImpl();
      accountAdmin.setLogger(logger);
      accountAdmin.setAccountStorage(storage);
      accountAdmin.setAuthenticationAdmin(authenticationAdmin);
      accountAdmin.start(Collections.<String, Object> emptyMap());

      when(account.getId()).thenReturn(newAccountId.getUuid());
   }

   @Test
   public void testGetAllAccounts() {
      accountAdmin.getAllAccounts();
      verify(storage).getAllAccounts();
   }

   @Test
   public void testGetAccountById() {
      accountAdmin.getAccountById(newAccountId);

      verify(storage).getAccountById(newAccountId);
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
   public void testGetAccountPrefsById() {
      accountAdmin.getAccountPreferencesById(newAccountId);

      verify(storage).getAccountPreferencesById(newAccountId);
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
   public void testSetActiveModified() {
      when(storage.getAccountById(newAccountId)).thenReturn(account);
      when(account.isActive()).thenReturn(true);

      boolean modified = accountAdmin.setActive(newAccountId, false);
      assertTrue(modified);

      verify(storage).getAccountById(newAccountId);
      verify(storage).setActive(newAccountId, false);
   }

   @Test
   public void testSetActiveNotModified() {
      when(storage.getAccountById(newAccountId)).thenReturn(account);
      when(account.isActive()).thenReturn(true);

      boolean modified = accountAdmin.setActive(newAccountId, true);
      assertFalse(modified);

      verify(storage).getAccountById(newAccountId);
      verify(storage, times(0)).setActive(newAccountId, true);
   }

   @Test
   public void testDeleteAccountId() {
      accountAdmin.deleteAccount(newAccountId);
      verify(storage).deleteAccount(newAccountId);
   }

   @Test
   public void testSetAccountPreferencesPrefsWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("preferences cannot be null");
      accountAdmin.setAccountPreferences(newAccountId, null);
   }

   @Test
   public void testSetAccountPreferencesModified() {
      Map<String, String> original = new HashMap<>();
      original.put("1", "2");

      Map<String, String> newMap = new HashMap<>();
      newMap.put("1", "3");

      ResultSet<AccountPreferences> resultSet = ResultSets.singleton(preferences);

      when(storage.getAccountPreferencesById(newAccountId)).thenReturn(resultSet);
      when(preferences.asMap()).thenReturn(original);

      boolean modified = accountAdmin.setAccountPreferences(newAccountId, newMap);
      assertTrue(modified);

      verify(storage).setAccountPreferences(newAccountId, newMap);
      verify(storage).getAccountPreferencesById(newAccountId);
      verify(preferences).asMap();
   }

   @Test
   public void testSetAccountPreferencesNotModified() {
      Map<String, String> original = new HashMap<>();
      original.put("1", "2");

      Map<String, String> newMap = new HashMap<>();
      newMap.put("1", "2");

      ResultSet<AccountPreferences> resultSet = ResultSets.singleton(preferences);

      when(storage.getAccountPreferencesById(newAccountId)).thenReturn(resultSet);
      when(preferences.asMap()).thenReturn(original);

      boolean modified = accountAdmin.setAccountPreferences(newAccountId, newMap);
      assertFalse(modified);

      verify(storage, times(0)).setAccountPreferences(newAccountId, newMap);
      verify(storage).getAccountPreferencesById(newAccountId);
      verify(preferences).asMap();
   }

   @Test
   public void testSetAccountPreferenceKeyWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("account preference key cannot be null");
      accountAdmin.setAccountPreference(newAccountId, null, "value");
   }

   @Test
   public void testSetAccountPreferenceValueWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("account preference value cannot be null - Use delete account preference instead");
      accountAdmin.setAccountPreference(newAccountId, "b", null);
   }

   @Test
   public void testSetAccountPreferenceModified() {
      Map<String, String> map = new HashMap<>();
      map.put("a", "1");
      map.put("b", "2");
      map.put("c", "3");

      ResultSet<AccountPreferences> resultSet = ResultSets.singleton(preferences);

      when(storage.getAccountPreferencesById(newAccountId)).thenReturn(resultSet);
      when(preferences.asMap()).thenReturn(map);

      boolean modified = accountAdmin.setAccountPreference(newAccountId, "b", "123412");
      assertTrue(modified);

      verify(storage).getAccountPreferencesById(newAccountId);
      verify(storage).setAccountPreferences(eq(newAccountId), newPrefsCaptor.capture());

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

      when(storage.getAccountPreferencesById(newAccountId)).thenReturn(resultSet);
      when(preferences.asMap()).thenReturn(map);

      boolean modified = accountAdmin.setAccountPreference(newAccountId, "b", "123412");
      assertFalse(modified);

      verify(storage).getAccountPreferencesById(newAccountId);
      verify(storage, times(0)).setAccountPreferences(eq(newAccountId), anyMapOf(String.class, String.class));
   }

   @Test
   public void testDeleteAccountPreferenceKeyWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("account preference key cannot be null");
      accountAdmin.deleteAccountPreference(newAccountId, null);
   }

   @Test
   public void testDeleteAccountPreferenceModified() {
      Map<String, String> map = new HashMap<>();
      map.put("a", "1");
      map.put("b", "123412");
      map.put("c", "3");

      ResultSet<AccountPreferences> resultSet = ResultSets.singleton(preferences);

      when(storage.getAccountPreferencesById(newAccountId)).thenReturn(resultSet);
      when(preferences.asMap()).thenReturn(map);

      boolean modified = accountAdmin.deleteAccountPreference(newAccountId, "b");
      assertTrue(modified);

      verify(storage).getAccountPreferencesById(newAccountId);
      verify(storage).setAccountPreferences(eq(newAccountId), newPrefsCaptor.capture());

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

      when(storage.getAccountPreferencesById(newAccountId)).thenReturn(resultSet);
      when(preferences.asMap()).thenReturn(map);

      boolean modified = accountAdmin.deleteAccountPreference(newAccountId, "b");
      assertFalse(modified);

      verify(storage).getAccountPreferencesById(newAccountId);
      verify(storage, times(0)).setAccountPreferences(eq(newAccountId), anyMapOf(String.class, String.class));
   }

   @Test
   public void testGetAccountByEmail() {
      ResultSet<Account> resultSet = ResultSets.singleton(account);

      when(storage.getAccountByEmail(EMAIL)).thenReturn(resultSet);

      ResultSet<Account> result = accountAdmin.getAccountByEmail(EMAIL);
      assertEquals(account, result.getExactlyOne());

      verify(storage).getAccountByEmail(EMAIL);
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
      when(authenticatedUser.getEmailAddress()).thenReturn(EMAIL);

      thrown.expect(AccountLoginException.class);
      thrown.expectMessage(
         "Login Error - Unable to find account for username[" + userName + "] using authentication scheme[" + scheme + "]");
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
      when(authenticatedUser.getEmailAddress()).thenReturn(EMAIL);

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

}
