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
import { Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';
import {
	CurrentGraphService,
	ConnectionsComponent,
	RouteStateService,
} from '@osee/messaging/connection-view';
import { combineLatest, iif, of } from 'rxjs';

@Component({
	selector: 'osee-messaging-connection-view',
	templateUrl: './connection-view.component.html',
	styles: [
		':host{ height: 94vh; min-height: calc(94vh - 10%); max-height: 94vh; width: 100vw; min-width: calc(100vw - 10%); display: inline-block; overflow: hidden; margin-bottom: -1em;}',
	],
	imports: [ConnectionsComponent],
})
export class ConnectionViewComponent implements OnInit {
	private route = inject(ActivatedRoute);
	private router = inject(Router);
	private routerState = inject(RouteStateService);
	private graph = inject(CurrentGraphService);
	private destroyRef = inject(DestroyRef);

	ngOnInit(): void {
		combineLatest([
			this.route.queryParamMap,
			this.route.data,
			iif(() => this.router.url.includes('diff'), of(false), of(true)),
		])
			.pipe(takeUntilDestroyed(this.destroyRef))
			.subscribe(([params, data, mode]) => {
				if (mode) {
					this.routerState.branchId = params.get('branchId') || '';
					this.routerState.branchType =
						(params.get('branchType') as
							| 'working'
							| 'baseline'
							| '') || '';
					/**
					 * Set params to uninitalized state for invalid routes
					 */
					this.routerState.connectionId = '-1';
					this.routerState.messageId = '';
					this.routerState.subMessageId = '-1';
					this.routerState.subMessageToStructureBreadCrumbs = '';
					this.routerState.singleStructureId = '';
					///////////////////////////////////////////////////////////
					this.routerState.DiffMode = false;
				} else {
					this.routerState.connectionId = '-1';
					this.routerState.messageId = '';
					this.routerState.subMessageId = '-1';
					this.routerState.subMessageToStructureBreadCrumbs = '';
					this.routerState.singleStructureId = '';
					this.graph.difference = data.diff;
				}
			});
	}
}
export default ConnectionViewComponent;
