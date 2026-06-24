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
	inject,
	input,
	signal,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { UiService } from '@osee/shared/services';
import { take, tap } from 'rxjs';
import { ArtifactExplorerHttpService } from '../../../services/artifact-explorer-http.service';
import { artifactHistoryEntry } from '../../../types/artifact-history';
import { artifactTab } from '../../../types/artifact-explorer';
import {
	ArtifactHistoryDiffDialogComponent,
	ArtifactHistoryDiffDialogData,
} from './artifact-history-diff-dialog/artifact-history-diff-dialog.component';

export type historyGroup = {
	txId: string;
	comment: string;
	date: string;
	changes: artifactHistoryEntry[];
};

@Component({
	selector: 'osee-artifact-history-panel',
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [MatIcon, MatIconButton, MatTooltip],
	templateUrl: './artifact-history-panel.component.html',
})
export class ArtifactHistoryPanelComponent {
	private httpService = inject(ArtifactExplorerHttpService);
	private uiService = inject(UiService);
	private dialog = inject(MatDialog);

	tab = input.required<artifactTab>();

	private branchId = computed(() => this.tab().branchId);
	private artifactId = computed(() => this.tab().artifact.id);

	/** Pagination state — backend-driven by transaction count. */
	protected readonly pageSize = signal(10);
	protected currentPage = signal(0);

	/** Backend-paginated history resource (paginated by distinct transaction count). */
	protected historyResource =
		this.httpService.getArtifactHistoryResource(
			this.branchId,
			this.artifactId,
			this.currentPage,
			this.pageSize
		);

	/** Group history entries by transaction ID for table display. */
	protected groupedHistory = computed<historyGroup[]>(() => {
		const result = this.historyResource.value();
		if (!result) return [];
		const entries = result.changes ?? [];
		const txMeta = result.transactions ?? {};

		const groups = new Map<string, historyGroup>();

		for (const entry of entries) {
			const txId =
				entry.currentVersion?.transactionToken?.id ?? '';
			if (!groups.has(txId)) {
				const meta = txMeta[txId];
				const date = meta?.timestamp
					? new Date(meta.timestamp).toLocaleString()
					: '';
				groups.set(txId, {
					txId,
					comment: meta?.comment ?? '',
					date,
					changes: [],
				});
			}
			groups.get(txId)!.changes.push(entry);
		}

		return [...groups.values()].sort(
			(a, b) => Number(b.txId) - Number(a.txId)
		);
	});

	/** Whether there might be more pages (if we got a full page worth of transactions). */
	protected hasNextPage = computed(() => {
		const result = this.historyResource.value();
		if (!result) return false;
		const entries = result.changes ?? [];
		const txIds = new Set(
			entries.map((e) => e.currentVersion?.transactionToken?.id)
		);
		return txIds.size >= this.pageSize();
	});

	nextPage() {
		this.currentPage.update((p) => p + 1);
	}

	prevPage() {
		if (this.currentPage() > 0) {
			this.currentPage.update((p) => p - 1);
		}
	}

	setPageSize(event: Event) {
		const value = (event.target as HTMLSelectElement).value;
		this.pageSize.set(Number(value));
		this.currentPage.set(0);
	}

	/** Safely extract a display string from changeType (may be object or string). */
	getChangeTypeDisplay(entry: artifactHistoryEntry): string {
		const ct = entry.changeType;
		if (!ct) return '';
		if (typeof ct === 'string') return ct;
		if (typeof ct === 'object') {
			return (ct as { name?: string }).name ?? JSON.stringify(ct);
		}
		return String(ct);
	}

	truncate(value: string | undefined | null, maxLen = 40): string {
		if (!value) return '';
		if (typeof value !== 'string') return String(value);
		return value.length > maxLen
			? value.substring(0, maxLen) + '…'
			: value;
	}

	openDiffDialog(group: historyGroup) {
		const data: ArtifactHistoryDiffDialogData = {
			txId: group.txId,
			comment: group.comment,
			date: group.date,
			changes: group.changes,
			branchId: this.branchId(),
			artifactId: this.artifactId(),
		};
		this.dialog
			.open(ArtifactHistoryDiffDialogComponent, {
				data,
				width: '800px',
				maxHeight: '80vh',
			})
			.afterClosed()
			.pipe(
				take(1),
				tap((result) => {
					if (result === 'restored') {
						this.uiService.updated = true;
					}
				})
			)
			.subscribe();
	}
}
