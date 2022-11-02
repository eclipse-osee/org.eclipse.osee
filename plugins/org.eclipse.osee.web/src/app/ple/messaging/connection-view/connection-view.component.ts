/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, UrlSegment } from '@angular/router';
import { combineLatest, iif, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { CurrentGraphService } from './services/current-graph.service';
import { RouteStateService } from './services/route-state-service.service';

@Component({
	selector: 'osee-messaging-connection-view',
	templateUrl: './connection-view.component.html',
	styleUrls: ['./connection-view.component.sass'],
})
export class ConnectionViewComponent implements OnInit {
	constructor(
		private route: ActivatedRoute,
		private router: Router,
		private routerState: RouteStateService,
		private graph: CurrentGraphService
	) {}

	ngOnInit(): void {
		combineLatest([
			this.route.paramMap,
			this.route.data,
			iif(() => this.router.url.includes('diff'), of(false), of(true)),
		]).subscribe(([params, data, mode]) => {
			if (mode) {
				this.routerState.branchId = params.get('branchId') || '';
				this.routerState.branchType = params.get('branchType') || '';
				/**
				 * Set params to uninitalized state for invalid routes
				 */
				this.routerState.connectionId = '';
				this.routerState.messageId = '';
				this.routerState.subMessageId = '';
				this.routerState.subMessageToStructureBreadCrumbs = '';
				this.routerState.singleStructureId = '';
				///////////////////////////////////////////////////////////
				this.routerState.DiffMode = false;
			} else {
				this.routerState.connectionId = '';
				this.routerState.messageId = '';
				this.routerState.subMessageId = '';
				this.routerState.subMessageToStructureBreadCrumbs = '';
				this.routerState.singleStructureId = '';
				this.graph.difference = data.diff;
			}
		});
	}
}
