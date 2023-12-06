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
import { Component, SecurityContext } from '@angular/core';
import { CommonModule } from '@angular/common';
import { navigationStructure } from '@osee/layout/routing';
import {
	navigationElement,
	defaultNavigationElement,
} from '@osee/shared/types';
import { ServerHealthPageHeaderComponent } from '../shared/components/server-health-page-header/server-health-page-header.component';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { ServerHealthHttpService } from '../shared/services/server-health-http.service';
import { Observable, map } from 'rxjs';
import { healthBalancer } from '../shared/types/server-health-types';
import { trigger, state, style } from '@angular/animations';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { MatIconModule } from '@angular/material/icon';
const _currNavItem: navigationElement =
	navigationStructure[1].children.find((c) => c.label === 'Balancers') ||
	defaultNavigationElement;

@Component({
	selector: 'osee-server-health-balancers',
	standalone: true,
	imports: [
		CommonModule,
		ServerHealthPageHeaderComponent,
		MatTableModule,
		MatIconModule,
	],
	templateUrl: './server-health-balancers.component.html',
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
export class ServerHealthBalancersComponent {
	get currNavItem() {
		return _currNavItem;
	}
	constructor(
		private serverHealthHttpService: ServerHealthHttpService,
		private sanitizer: DomSanitizer
	) {}

	private dataSource = new MatTableDataSource<healthBalancer>();
	balancersAsDataSource: Observable<MatTableDataSource<healthBalancer>> =
		this.serverHealthHttpService.Balancers.pipe(
			map((data) => {
				const dataSource = this.dataSource;
				dataSource.data = data.balancers;
				return dataSource;
			})
		);

	displayedColumns = ['name', 'alive'];

	toggleRow(element: { expanded: boolean }) {
		element.expanded = !element.expanded;
	}

	buildBalancerManagerUrl(name: string) {
		// Mark the url as safe for iframe
		return this.sanitizer.bypassSecurityTrustResourceUrl(
			'http://' + name + '/balancer-manager'
		);
	}

	openLink(safeUrl: SafeResourceUrl) {
		const url = this.sanitizer.sanitize(
			SecurityContext.URL,
			safeUrl
		) as string;
		window.open(url, '_blank');
	}
}
export default ServerHealthBalancersComponent;
