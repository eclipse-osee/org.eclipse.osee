/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.config;

import java.util.Arrays;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.AbstractLdapAuthenticator;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * @author Ajay Chandrahasan
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
   @Value("${com.icteam.ldap.enable}")
   Boolean isLDAPEnable;
   @Value("${icteam.ldap.user}")
   String ldapuser;
   @Value("${icteam.ldap.password}")
   String ldapPassword;
   @Value("${icteam.ldap.server.name}")
   String serverName;
   @Value("${icteam.ldap.search.base}")
   String searchBase;
   @Autowired
   LogoutSuccessHandler logoutSuccessHandler;

   /**
    * @param auth
    * @throws Exception
    */
   @Autowired
   public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
      if (this.isLDAPEnable) {
         auth.authenticationProvider(ldapAuthenticationProvider());
      } else {
         auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
      }
   }

   public LdapUserSearch getLdapSerarch() {
      LdapUserSearch ldapUserSearch =
         new FilterBasedLdapUserSearch("", "(&(objectCategory=Person)(sAMAccountName={0}))", contextSource());
      ((FilterBasedLdapUserSearch) ldapUserSearch).setSearchSubtree(true);
      ((FilterBasedLdapUserSearch) ldapUserSearch).setSearchTimeLimit(0);
      ((FilterBasedLdapUserSearch) ldapUserSearch).setDerefLinkFlag(false);

      return ldapUserSearch;
   }

   @Bean
   public LdapAuthenticationProvider ldapAuthenticationProvider() {
      LdapAuthenticationProvider ldapAuthenticationProvider =
         new LdapAuthenticationProvider(authentificator(), authPopulator());
      ldapAuthenticationProvider.setUserDetailsContextMapper(userDetailsContextMapper());

      return ldapAuthenticationProvider;
   }

   @Bean
   public AbstractLdapAuthenticator authentificator() {
      BindAuthenticator bindAuthenticator = new BindAuthenticator(contextSource());
      bindAuthenticator.setUserSearch(getLdapSerarch());

      return bindAuthenticator;
   }

   @Bean
   public DefaultSpringSecurityContextSource contextSource() {
      DefaultSpringSecurityContextSource contextSource =
         new DefaultSpringSecurityContextSource(Arrays.asList(this.serverName), this.searchBase);
      String decryptedPwd = new String(Base64.decodeBase64(this.ldapPassword.getBytes()));
      contextSource.setUserDn(this.ldapuser);
      contextSource.setPassword(decryptedPwd);
      contextSource.afterPropertiesSet();

      return contextSource;
   }

   @Bean
   public DefaultLdapAuthoritiesPopulator authPopulator() {
      DefaultLdapAuthoritiesPopulator populator = new DefaultLdapAuthoritiesPopulator(contextSource(), "");
      populator.setGroupSearchFilter("(&(objectCategory=Person)(sAMAccountName={0}))");
      populator.setSearchSubtree(true);
      populator.setIgnorePartialResultException(true);

      return populator;
   }

   @Override
   @Bean
   public AuthenticationManager authenticationManagerBean() throws Exception {
      return super.authenticationManagerBean();
   }

   @Bean
   public UserDetailsContextMapper userDetailsContextMapper() {
      return new CustomUserDetailsContextMapper();
   }

   @Override
   protected void configure(final HttpSecurity http) throws Exception {
      http.httpBasic().and().authorizeRequests().antMatchers("index.html", "/", "/login", "/home", "/inline.**.js",
         "/polyfills.**.js", "/styles.**", "/iCTeam_logo.png", "/bosch_logo.png", "/bg-**.png", "/bg_**.png",
         "/vendor.**.js", "/main.**.js", "/glyphicons-halflings-regular.*", "/scripts.**.js",
         "/runtime.**.js").permitAll().anyRequest().authenticated().and().logout().logoutUrl(
            "/logout").logoutSuccessHandler(this.logoutSuccessHandler).deleteCookies(
               "JSESSIONID").invalidateHttpSession(false).permitAll().and().csrf().csrfTokenRepository(
                  CookieCsrfTokenRepository.withHttpOnlyFalse());
   }
}
