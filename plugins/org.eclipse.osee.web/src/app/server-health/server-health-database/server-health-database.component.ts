/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ServerHealthSqlComponent } from './components/server-health-sql/server-health-sql.component';
import { ServerHealthTablespaceComponent } from './components/server-health-tablespace/server-health-tablespace.component';
import { navigationStructure } from '@osee/layout/routing';
import {
	navigationElement,
	defaultNavigationElement,
} from '@osee/shared/types';
import { ServerHealthPageHeaderComponent } from '../shared/components/server-health-page-header/server-health-page-header.component';
const _currNavItem: navigationElement =
	navigationStructure[1].children.find((c) => c.label === 'Database') ||
	defaultNavigationElement;

@Component({
	selector: 'osee-server-health-database',
	standalone: true,
	imports: [
		CommonModule,
		ServerHealthSqlComponent,
		ServerHealthTablespaceComponent,
		ServerHealthPageHeaderComponent,
	],
	templateUrl: './server-health-database.component.html',
})
export class ServerHealthDatabaseComponent {
	get currNavItem() {
		return _currNavItem;
	}
}
export default ServerHealthDatabaseComponent;
