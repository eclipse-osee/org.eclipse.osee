/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { NgTemplateOutlet } from '@angular/common';
import {
	Component,
	TemplateRef,
	computed,
	contentChild,
	input,
	output,
} from '@angular/core';
import { MatList, MatListItem } from '@angular/material/list';

@Component({
	selector: 'osee-paginated-mat-list',
	imports: [MatList, MatListItem, NgTemplateOutlet],
	templateUrl: './paginated-mat-list.component.html',
})
export class PaginatedMatListComponent<T> {
	currentPageItems = input.required<T[]>();
	allItems = input.required<T[]>();
	pageSize = input.required<number>();
	count = input<number>(-1);
	paginate = output();

	template = contentChild.required(TemplateRef, {
		read: TemplateRef<{ $implicit: T; item: T }>,
	});

	useCount = computed(() => this.count() >= 0);

	nextPage() {
		this.paginate.emit();
	}
}
