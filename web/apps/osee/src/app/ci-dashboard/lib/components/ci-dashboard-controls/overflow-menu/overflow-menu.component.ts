/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import {
	ChangeDetectionStrategy,
	Component,
	signal,
	computed,
	inject,
	effect,
	ElementRef,
	viewChild,
} from '@angular/core';
import { toSignal, toObservable } from '@angular/core/rxjs-interop';
import { CiDashboardUiService } from '../../../services/ci-dashboard-ui.service';
import { DashboardService } from '../../../services/dashboard.service';

import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { merge } from 'rxjs';
import { map, switchMap, skip } from 'rxjs/operators';

@Component({
	selector: 'osee-overflow-menu',
	standalone: true,
	imports: [MatMenuModule, MatButtonModule, MatIconModule, MatTooltip],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: `
		<div class="tw-flex tw-items-center">
			<button
				mat-icon-button
				aria-label="More actions"
				[matMenuTriggerFor]="exportMenu"
				[disabled]="!isBranchValid() && !isSetValid()">
				<mat-icon>more_vert</mat-icon>
			</button>

			<mat-menu #exportMenu="matMenu">
				<button
					mat-menu-item
					(click)="triggerBranchExport()"
					[disabled]="!isBranchValid()"
					[matTooltip]="
						isBranchValid()
							? 'Download a CSV export for the current branch'
							: 'Select a valid branch to enable branch export'
					">
					<mat-icon>download</mat-icon>
					<span>Download CSV for Branch</span>
				</button>
				<button
					mat-menu-item
					(click)="triggerSetExport()"
					[disabled]="!isSetValid()"
					[matTooltip]="
						isSetValid()
							? 'Download a CSV export for the selected CI Set'
							: 'Select a valid CI Set to enable set export'
					">
					<mat-icon>download</mat-icon>
					<span>Download CSV for Set</span>
				</button>
			</mat-menu>
			<a
				#downloadAnchor
				id="downloadAnchor"
				style="display:none"
				[attr.href]="exportedData().href"
				[attr.download]="exportedData().filename">
			</a>
		</div>
	`,
})
export class OverflowMenuComponent {
	private uiService = inject(CiDashboardUiService);
	private dashboardService = inject(DashboardService);

	branchId = toSignal(this.uiService.branchId);
	ciSetId = toSignal(this.uiService.ciSetId);

	private exportBranchDataTrigger = signal(0);
	private exportSetDataTrigger = signal(0);

	exportBranchDataTrigger$ = toObservable(this.exportBranchDataTrigger).pipe(
		skip(1)
	);
	exportSetDataTrigger$ = toObservable(this.exportSetDataTrigger).pipe(
		skip(1)
	);

	exportedBranchData$ = this.exportBranchDataTrigger$.pipe(
		switchMap(() => this.dashboardService.exportBranchData()),
		map((blob) => {
			if (!blob || blob.size === 0) {
				return { href: '', filename: '' };
			}
			const href = URL.createObjectURL(blob);
			return { href, filename: `dashboard_branch_${Date.now()}.csv` };
		})
	);

	exportedSetData$ = this.exportSetDataTrigger$.pipe(
		switchMap(() => this.dashboardService.exportSetData()),
		map((blob) => {
			if (!blob || blob.size === 0) {
				return { href: '', filename: '' };
			}
			const href = URL.createObjectURL(blob);
			return { href, filename: `dashboard_set_${Date.now()}.csv` };
		})
	);

	exportedData$ = merge(this.exportedBranchData$, this.exportedSetData$);

	exportedData = toSignal(this.exportedData$, {
		initialValue: { href: '', filename: '' },
	});

	private _anchor =
		viewChild.required<ElementRef<HTMLAnchorElement>>('downloadAnchor');

	private _openURL = effect(() => {
		const data = this.exportedData();
		const a = this._anchor().nativeElement;

		if (a && data.href && data.href.length > 0) {
			a.href = data.href;
			a.download = data.filename;
			a.click();
		}
	});

	triggerBranchExport() {
		this.exportBranchDataTrigger.update((v) => v + 1);
	}

	triggerSetExport() {
		this.exportSetDataTrigger.update((v) => v + 1);
	}

	isBranchValid = computed(() => {
		const id = this.branchId();
		return id !== '' && id !== '-1' && id !== '0';
	});

	isSetValid = computed(() => {
		const id = this.ciSetId();
		return id !== '-1' && id !== '0';
	});
}
