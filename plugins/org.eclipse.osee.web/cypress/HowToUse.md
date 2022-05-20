[comment]: # (Copyright (c) 2022 Boeing This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0/ SPDX-License-Identifier: EPL-2.0 Contributors:      Boeing - initial API and implementation)

Default usage: ng e2e - This will pull up cypress UI and allow you to filter specs. If you run all or run all messaging specs, the odds of cypress running out of memory is very high.

CI usage: ng e2e --watch=false --headless=true 
This will open up cypress headlessly and run all the specs, this is allowed to use a bit more memory and is a bit more efficient with memory as well.

ng e2e --watch=false --headless=true --spec 'cypress/integration/ple/plconfig/**/*'
This will run all tests under cypress/integration/ple/plconfig/. You may replace the string at the end with other regexes to run different sets of tests. This can be used in conjunction with the --group option to parallelize tests.