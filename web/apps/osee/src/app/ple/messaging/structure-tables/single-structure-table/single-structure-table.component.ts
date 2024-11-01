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
import { AsyncPipe } from '@angular/common';
import {
	ChangeDetectionStrategy,
	Component,
	OnDestroy,
	OnInit,
	inject,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { StructureInterfaceComponent } from '@osee/messaging/structure-tables';
import { combineLatest, iif, of } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
	selector: 'osee-messaging-single-structure-table',
	templateUrl: './single-structure-table.component.html',
	styles: [
		':host{ height: 94vh; min-height: calc(94vh - 10%); max-height: 94vh; width: 100vw; min-width: calc(100vw - 10%); display: inline-block;}',
	],
	standalone: true,
	imports: [StructureInterfaceComponent, AsyncPipe],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SingleStructureTableComponent implements OnInit, OnDestroy {
	private route = inject(ActivatedRoute);
	private router = inject(Router);
	private structureService = inject(STRUCTURE_SERVICE_TOKEN);

	breadCrumb = this.structureService.breadCrumbs;

	ngOnInit(): void {
		combineLatest([
			this.route.paramMap,
			this.route.data,
			iif(() => this.router.url.includes('diff'), of(true), of(false)),
		])
			.pipe(takeUntil(this.structureService.done))
			.subscribe(([paramMap, data, mode]) => {
				if (mode) {
					this.structureService.BranchType =
						(paramMap.get('branchType') as
							| 'working'
							| 'baseline'
							| '') || '';
					this.structureService.branchId =
						paramMap.get('branchId') || '';
					this.structureService.messageId =
						paramMap.get('messageId') || '';
					this.structureService.subMessageId =
						(paramMap.get('subMessageId') as `${number}`) || '-1';
					this.structureService.connection =
						(paramMap.get('connection') as `${number}`) || '-1';
					this.structureService.singleStructureIdValue =
						paramMap.get('structureId') || '';
					this.structureService.difference = data?.diff;
					this.structureService.DiffMode = true;
				} else {
					this.structureService.BranchType =
						(paramMap.get('branchType') as
							| 'working'
							| 'baseline'
							| '') || '';
					this.structureService.branchId =
						paramMap.get('branchId') || '';
					this.structureService.messageId =
						paramMap.get('messageId') || '';
					this.structureService.subMessageId =
						(paramMap.get('subMessageId') as `${number}`) || '-1';
					this.structureService.connection =
						(paramMap.get('connection') as `${number}`) || '-1';
					this.structureService.singleStructureIdValue =
						paramMap.get('structureId') || '';
					this.structureService.DiffMode = false;
					this.structureService.difference = [];
				}
			});
	}
	ngOnDestroy(): void {
		this.structureService.clearRows();
		this.structureService.toggleDone = true;
	}
}

export default SingleStructureTableComponent;
