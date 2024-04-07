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
import { state, style, trigger } from '@angular/animations';
import { CommonModule } from '@angular/common';
import { Component, signal } from '@angular/core';
import {
	MatExpansionPanel,
	MatExpansionPanelHeader,
	MatExpansionPanelTitle,
} from '@angular/material/expansion';
import {
	MatCell,
	MatCellDef,
	MatColumnDef,
	MatHeaderCell,
	MatHeaderCellDef,
	MatHeaderRow,
	MatHeaderRowDef,
	MatRow,
	MatRowDef,
	MatTable,
	MatTableDataSource,
} from '@angular/material/table';
import { navigationStructure } from '@osee/layout/routing';
import {
	defaultNavigationElement,
	navigationElement,
} from '@osee/shared/types';
import { Observable, map } from 'rxjs';
import { ServerHealthPageHeaderComponent } from '../shared/components/server-health-page-header/server-health-page-header.component';
import { ServerHealthHttpService } from '../shared/services/server-health-http.service';
import { healthServer } from '../shared/types/server-health-types';
import { ServerHealthDetailsComponent } from './components/server-health-details/server-health-details.component';
import { ServerHealthJavaComponent } from './components/server-health-java/server-health-java.component';
import { ServerHealthLogComponent } from './components/server-health-log/server-health-log.component';
import { ServerHealthTopComponent } from './components/server-health-top/server-health-top.component';

const _currNavItem: navigationElement =
	navigationStructure[1].children.find((c) => c.label === 'Status') ||
	defaultNavigationElement;

@Component({
	selector: 'osee-server-health-status',
	standalone: true,
	imports: [
		CommonModule,
		ServerHealthDetailsComponent,
		ServerHealthPageHeaderComponent,
		ServerHealthLogComponent,
		ServerHealthJavaComponent,
		ServerHealthTopComponent,
		MatTable,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatCell,
		MatCellDef,
		MatExpansionPanel,
		MatExpansionPanelHeader,
		MatExpansionPanelTitle,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
	],
	templateUrl: './server-health-status.component.html',
	animations: [
		trigger('detailExpand', [
			state(
				'collapsed',
				style({ height: '0px', minHeight: '0', visibility: 'hidden' })
			),
			state('expanded', style({ height: '*', visibility: 'visible' })),
		]),
	],
})
export class ServerHealthStatusComponent {
	get currNavItem() {
		return _currNavItem;
	}

	private dataSource = new MatTableDataSource<healthServer>();
	serversAsDataSource: Observable<MatTableDataSource<healthServer>> =
		this.serverHealthHttpService.Status.pipe(
			map((data) => {
				const dataSource = this.dataSource;
				dataSource.data = data.servers;
				return dataSource;
			})
		);

	constructor(private serverHealthHttpService: ServerHealthHttpService) {}
	displayedColumns = ['name', 'serverAlive', 'dbAlive'];

	toggleRow(element: { expanded: boolean }, serverName: string) {
		element.expanded = !element.expanded;
		// Remove all open panels from panelsOpen signal when closing the table row
		if (!element.expanded) {
			this.panelsOpen.update((panels) =>
				panels.filter((v) => !v.includes(serverName))
			);
		}
	}

	// Managing state of open expansion panels (used for styling)
	protected panelsOpen = signal<string[]>([]);
	addToPanelsOpen(value: string) {
		this.panelsOpen.update((panels) => [...panels, value]);
	}
	removeFromPanelsOpen(value: string) {
		this.panelsOpen.update((panels) => panels.filter((v) => v !== value));
	}
}
export default ServerHealthStatusComponent;
