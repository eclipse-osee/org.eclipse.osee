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
	computed,
	effect,
	ElementRef,
	inject,
	signal,
	viewChild,
} from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltip } from '@angular/material/tooltip';
import { merge } from 'rxjs';
import { map, skip, switchMap } from 'rxjs/operators';
import { CiDashboardUiService } from '../../../services/ci-dashboard-ui.service';
import { DashboardService } from '../../../services/dashboard.service';

@Component({
	selector: 'osee-overflow-menu',
	standalone: true,
	imports: [MatMenuModule, MatButtonModule, MatIconModule, MatTooltip],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: `
		<div class="tw-flex tw-items-center">
			<span
				class="tw-flex"
				matTooltip="Select a valid branch to enable dashboard export."
				[matTooltipDisabled]="isBranchValid()"
				[attr.tabindex]="!isBranchValid() ? 0 : null"
				[attr.aria-label]="
					!isBranchValid()
						? 'Select a valid branch to enable dashboard export.'
						: null
				">
				<button
					mat-icon-button
					aria-label="Download Dashboard CSV"
					matTooltip="Download Dashboard CSV"
					[matMenuTriggerFor]="exportMenu"
					[disabled]="!isBranchValid()">
					<mat-icon>download</mat-icon>
				</button>
			</span>

			<mat-menu #exportMenu="matMenu">
				<span
					class="tw-block"
					matTooltip="Select a valid branch to enable branch export."
					[matTooltipDisabled]="isBranchValid()"
					[attr.tabindex]="!isBranchValid() ? 0 : null"
					[attr.aria-label]="
						!isBranchValid()
							? 'Select a valid branch to enable branch export.'
							: null
					">
					<button
						mat-menu-item
						(click)="triggerBranchExport()"
						[disabled]="!isBranchValid()">
						<mat-icon>download</mat-icon>
						<span>Download CSV for Branch</span>
					</button>
				</span>
				<span
					class="tw-block"
					matTooltip="Select a valid CI Set to enable set export."
					[matTooltipDisabled]="isSetValid()"
					[attr.tabindex]="!isSetValid() ? 0 : null"
					[attr.aria-label]="
						!isSetValid()
							? 'Select a valid CI Set to enable set export.'
							: null
					">
					<button
						mat-menu-item
						(click)="triggerSetExport()"
						[disabled]="!isSetValid()">
						<mat-icon>download</mat-icon>
						<span>Download CSV for Set</span>
					</button>
				</span>
			</mat-menu>
			<a
				#downloadAnchor
				id="downloadAnchor"
				style="display: none"
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

	private _openURL = effect((onCleanup) => {
		const data = this.exportedData();
		const anchor = this._anchor().nativeElement;

		if (anchor && data.href) {
			anchor.href = data.href;
			anchor.download = data.filename;
			anchor.click();
			onCleanup(() => URL.revokeObjectURL(data.href));
		}
	});

	triggerBranchExport() {
		this.exportBranchDataTrigger.update((value) => value + 1);
	}

	triggerSetExport() {
		this.exportSetDataTrigger.update((value) => value + 1);
	}

	isBranchValid = computed(() => {
		const id = this.branchId();
		return id !== undefined && id !== '' && id !== '-1' && id !== '0';
	});

	isSetValid = computed(() => {
		const id = this.ciSetId();
		return id !== undefined && id !== '-1' && id !== '0';
	});
}
