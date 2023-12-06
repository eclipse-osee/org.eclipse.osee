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
import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ServerHealthHttpService } from '../shared/services/server-health-http.service';
import { shareReplay } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { navigationStructure } from '@osee/layout/routing';
import {
	navigationElement,
	defaultNavigationElement,
} from '@osee/shared/types';
import { ServerHealthPageHeaderComponent } from '../shared/components/server-health-page-header/server-health-page-header.component';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import {
	session,
	user,
	versionNameMap,
	versionTypeMap,
} from '../shared/types/server-health-types';
import { MatExpansionModule } from '@angular/material/expansion';

const _currNavItem: navigationElement =
	navigationStructure[1].children.find((c) => c.label === 'Usage') ||
	defaultNavigationElement;

@Component({
	selector: 'osee-server-health-usage',
	standalone: true,
	imports: [
		CommonModule,
		ServerHealthPageHeaderComponent,
		MatTableModule,
		MatExpansionModule,
	],
	templateUrl: './server-health-usage.component.html',
})
export class ServerHealthUsageComponent {
	constructor(private serverHealthHttpService: ServerHealthHttpService) {}

	get currNavItem() {
		return _currNavItem;
	}

	displayedUserColumns: string[] = ['name', 'email', 'userId', 'accountId'];
	displayedSessionColumns: string[] = [
		'name',
		'email',
		'userId',
		'accountId',
		'date',
		'version',
		'sessionId',
		'clientAddress',
		'clientMachineName',
		'port',
	];

	usage = this.serverHealthHttpService.Usage.pipe(
		shareReplay({ bufferSize: 1, refCount: true }),
		takeUntilDestroyed()
	);

	getUsersDataSource(usersArray: user[]) {
		// Sort user's by name alphabetically
		const sortedUsers = usersArray
			.slice()
			.sort((a, b) => a.name.localeCompare(b.name));
		return new MatTableDataSource(sortedUsers);
	}
	getUsersSize(usersArray: user[]) {
		return usersArray.length;
	}
	getAllSessionsDataSource(sessionsArray: session[]) {
		return new MatTableDataSource(sessionsArray);
	}
	getAllSessionsSize(sessionsArray: session[]) {
		return sessionsArray.length;
	}
	getVersionMapKeys(map: versionTypeMap | versionNameMap) {
		return Object.keys(map);
	}

	// Managing state of open expansion panels (used for styling)
	protected panelsOpen = signal<string[]>([]);
	addToPanelsOpen(value: string) {
		this.panelsOpen.update((rows) => [...rows, value]);
	}
	removeFromPanelsOpen(value: string) {
		this.panelsOpen.update((rows) => rows.filter((v) => v !== value));
	}
}
export default ServerHealthUsageComponent;
