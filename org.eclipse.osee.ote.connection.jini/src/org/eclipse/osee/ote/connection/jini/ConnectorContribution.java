package org.eclipse.osee.ote.connection.jini;

import org.eclipse.osee.connection.service.IConnectorContributor;

public class ConnectorContribution implements IConnectorContributor {

    public ConnectorContribution() {
	// TODO Auto-generated constructor stub
    }

    @Override
    public void init() throws Exception {
	Activator.getDefault().startJini();
    }

}
