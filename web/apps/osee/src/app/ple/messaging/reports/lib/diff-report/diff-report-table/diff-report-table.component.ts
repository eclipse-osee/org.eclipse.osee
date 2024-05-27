/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { AsyncPipe, NgClass } from '@angular/common';
import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
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
} from '@angular/material/table';
import { MimChangeSummaryItem } from '@osee/messaging/shared/types';
import { changeReportRow } from '@osee/shared/types/change-report';

@Component({
	selector: 'osee-messaging-diff-report-table',
	templateUrl: './diff-report-table.component.html',
	styles: [],
	standalone: true,
	imports: [
		AsyncPipe,
		NgClass,
		MatTable,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatCell,
		MatCellDef,
		MatIcon,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
	],
})
export class DiffReportTableComponent implements OnChanges {
	@Input({ required: true }) items: MimChangeSummaryItem[] = [];
	@Input() title = '';
	@Input() headers: string[] = [];
	@Input() showChildren = false;

	private _changeMap = new Map<
		string,
		Map<string, changeReportRow | undefined>
	>();

	getChange(item: MimChangeSummaryItem, header: string) {
		if (this.isChanged(item, header)) {
			return this._changeMap.get(item.artId)?.get(header);
		}
		return undefined;
	}

	isChanged(item: MimChangeSummaryItem, header: string) {
		if (
			this._changeMap.has(item.artId) &&
			this._changeMap.get(item.artId)?.has(header)
		) {
			return true;
		}
		let changeMap = this._changeMap.get(item.artId);
		if (!changeMap) {
			changeMap = new Map<string, changeReportRow | undefined>();
			this._changeMap.set(item.artId, changeMap);
		}
		if (header === 'Applicability' && item.applicabilityChanged) {
			changeMap.set(header, undefined);
			return true;
		}
		const change = item.attributeChanges.find(
			(attr) => attr.itemType === header
		);
		if (change) {
			changeMap.set(header, change);
			return true;
		}
		return false;
	}

	ngOnChanges(changes: SimpleChanges): void {
		if (changes.items.isFirstChange()) {
			if (this.headers.length === 0) {
				this.headers = this._mapToHeaders(changes.items.currentValue);
			}
		}
	}

	private _mapToHeaders(items: MimChangeSummaryItem[]) {
		const headers = new Map<string, null>();
		headers.set('Name', null);
		let applicChange = false;
		items.forEach((item) => {
			item.attributeChanges.forEach((attrChange) => {
				if (attrChange.itemType !== 'Name') {
					headers.set(attrChange.itemType, null);
				}
			});
			applicChange = applicChange || item.applicabilityChanged;
		});
		if (applicChange) {
			headers.set('Applicability', null);
		}
		return [...headers.keys()];
	}
}
