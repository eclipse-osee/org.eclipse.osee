*********************************************
	LDAP Core Connectivity plug-in for OSEE
*********************************************


OVERVIEW
********
LDAP Core Connectivity plugin for OSEE is an open-source Eclipse plug-in developed
by Robert Bosch Engineering and Business Solutions Ltd India. 
This plug-in provides Authentication to OSEE clients to access OSEE data store based on LDAP authentication.
	
The LDAP User directory search can be used to decide the whether an the user can be authenticated.
The plug-in provides provision to configure custom LDAP server connectivity. 
The plug-in will perform a check that OSEE credential matches with the
LDAP User directory. If so the user will be authenticated.

USAGE
*****

This plug-in will be deployed in the OSEE application server.
At the OSEE Client the Authentication protocol should be set to ldap to use LDAP Authentication feature.
The following change has to be done in the Eclipse OSEE Client arguments 
"-Dosee.authentication.protocol=ldap"

CONFIGURATION 
*************
It provides an  extension point org.eclipse.osee.framework.authentication.ldap.core.service to configure the connectivity to custom LDAP server.
The contributing plugin should provide the necesary information to make connection to LDAP server like,
LDAP User name, Credentaials, Authentication type, LDAP Server name, Port and the  search base (LDAP search domain names)


KNOWN BUGS
**********
NIL