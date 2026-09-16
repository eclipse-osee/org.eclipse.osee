/*********************************************************************
 * Copyright (c) 2026 Boeing
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
	input,
} from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { DiffRow, MarkdownDiffEntry } from '../types/markdown-diff';
import { computeSideBySideDiff } from '../utils/compute-diff';

@Component({
	selector: 'osee-markdown-diff-entry',
	imports: [MatIcon],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: `
		<div
			class="tw-mb-6 tw-overflow-hidden tw-rounded-lg tw-border tw-border-foreground-divider">
			<div
				class="tw-flex tw-items-center tw-gap-2 tw-border-b tw-border-foreground-divider tw-bg-background-selected-button tw-px-4 tw-py-3">
				<mat-icon [class]="changeTypeIconClass()">{{
					changeTypeIcon()
				}}</mat-icon>
				<span class="tw-font-bold">{{ entry().artifactName }}</span>
				<span
					class="tw-rounded tw-px-2 tw-py-0.5 tw-text-xs tw-font-bold"
					[class]="changeTypeBadgeClass()">
					{{ entry().changeDescription }}
				</span>
				<span class="tw-ml-auto tw-text-xs">
					Artifact ID: {{ entry().artifactId }}
				</span>
			</div>
			<div class="tw-grid tw-grid-cols-2">
				<div
					class="tw-border-b tw-border-r tw-border-foreground-divider tw-px-3 tw-py-2 tw-text-xs tw-font-bold tw-uppercase tw-tracking-wide"
					[class]="previousHeaderBg()">
					Previous
				</div>
				<div
					class="tw-border-b tw-border-foreground-divider tw-px-3 tw-py-2 tw-text-xs tw-font-bold tw-uppercase tw-tracking-wide"
					[class]="currentHeaderBg()">
					Current
				</div>
			</div>
			<div
				class="tw-max-h-[600px] tw-overflow-auto tw-bg-background-background">
				<table class="tw-w-full tw-table-fixed tw-border-collapse">
					<colgroup>
						<col class="tw-w-10" />
						<col />
						<col class="tw-w-10" />
						<col />
					</colgroup>
					<tbody>
						@for (row of diffRows(); track $index) {
							<tr>
								<td
									class="tw-select-none tw-border-r tw-border-foreground-divider tw-px-1 tw-text-right tw-font-mono tw-text-xs tw-opacity-40"
									[class]="oldCellBg(row)">
									{{ row.old.lineNumber ?? '' }}
								</td>
								<td
									class="tw-overflow-hidden tw-text-ellipsis tw-whitespace-pre-wrap tw-break-words tw-border-r tw-border-foreground-divider tw-px-3 tw-py-0.5 tw-font-mono tw-text-xs"
									[class]="oldCellBg(row)">
									{{ row.old.content }}
								</td>
								<td
									class="tw-select-none tw-border-r tw-border-foreground-divider tw-px-1 tw-text-right tw-font-mono tw-text-xs tw-opacity-40"
									[class]="newCellBg(row)">
									{{ row.new.lineNumber ?? '' }}
								</td>
								<td
									class="tw-overflow-hidden tw-text-ellipsis tw-whitespace-pre-wrap tw-break-words tw-px-3 tw-py-0.5 tw-font-mono tw-text-xs"
									[class]="newCellBg(row)">
									{{ row.new.content }}
								</td>
							</tr>
						}
					</tbody>
				</table>
			</div>
		</div>
	`,
})
export class MarkdownDiffEntryComponent {
	entry = input.required<MarkdownDiffEntry>();

	protected diffRows = computed<DiffRow[]>(() => {
		const e = this.entry();
		if (e.changeType === 'New' || !e.wasValue.trim()) {
			return e.isValue.split('\n').map(
				(line, i): DiffRow => ({
					old: { type: 'empty', lineNumber: null, content: '' },
					new: { type: 'added', lineNumber: i + 1, content: line },
				})
			);
		}
		if (
			e.changeType.toLowerCase().includes('deleted') ||
			!e.isValue.trim()
		) {
			return e.wasValue.split('\n').map(
				(line, i): DiffRow => ({
					old: { type: 'removed', lineNumber: i + 1, content: line },
					new: { type: 'empty', lineNumber: null, content: '' },
				})
			);
		}
		return computeSideBySideDiff(e.wasValue, e.isValue);
	});

	protected changeTypeIcon = computed(() => {
		const type = this.entry().changeType;
		if (type === 'New') {
			return 'add';
		}
		if (type.toLowerCase().includes('deleted')) {
			return 'delete';
		}
		return 'edit';
	});

	protected changeTypeIconClass = computed(() => {
		const type = this.entry().changeType;
		if (type === 'New') {
			return 'tw-text-success';
		}
		if (type.toLowerCase().includes('deleted')) {
			return 'tw-text-warning';
		}
		return 'tw-text-primary';
	});

	protected changeTypeBadgeClass = computed(() => {
		const type = this.entry().changeType;
		if (type === 'New') {
			return 'tw-bg-osee-green-8 tw-bg-opacity-20 tw-text-osee-green-10';
		}
		if (type.toLowerCase().includes('deleted')) {
			return 'tw-bg-osee-red-8 tw-bg-opacity-20 tw-text-osee-red-10';
		}
		return 'tw-bg-osee-light-blue-8 tw-bg-opacity-20 tw-text-osee-light-blue-10';
	});

	protected previousHeaderBg = computed(() => {
		return 'tw-bg-background-selected-button';
	});

	protected currentHeaderBg = computed(() => {
		return 'tw-bg-background-selected-button';
	});

	protected oldCellBg(row: DiffRow): string {
		return row.old.type === 'removed'
			? 'tw-bg-osee-red-8 tw-bg-opacity-[0.15]'
			: '';
	}

	protected newCellBg(row: DiffRow): string {
		return row.new.type === 'added'
			? 'tw-bg-osee-green-8 tw-bg-opacity-[0.15]'
			: '';
	}
}
