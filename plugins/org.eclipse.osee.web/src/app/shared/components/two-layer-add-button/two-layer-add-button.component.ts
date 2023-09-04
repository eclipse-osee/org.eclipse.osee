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
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { BehaviorSubject } from 'rxjs';
import {
	addButtonHoverIconTransition,
	addButtonIconTransition,
	slidingAddButtonAnim,
} from './two-layer-add-button.animation';

@Component({
	selector: 'osee-two-layer-add-button',
	templateUrl: './two-layer-add-button.component.html',
	styles: [],
	animations: [
		slidingAddButtonAnim,
		addButtonIconTransition,
		addButtonHoverIconTransition,
	],
	standalone: true,
	imports: [NgFor, AsyncPipe, MatIconModule, MatButtonModule, NgIf],
})
export class TwoLayerAddButtonComponent<
	T extends string = any,
	R extends { id: string; name: string } = any,
> {
	@Input() baseLevel: T = {} as T;
	@Input() nestedLevel: R[] = [] as R[];
	@Input() nestedLevelPrefix = '';
	@Input() firstOptionDisabled = false;
	@Input() baseIcon: string = 'add';
	@Input() nestedIcon: string = 'add';
	@Input() openDirection: string = 'UP'; //@todo implement later when this is needed, default should be UP
	defaultValue: R = { id: '-1', name: '' } as R;
	hoveredElements: string[] = [];

	@Output() normalClick = new EventEmitter<string | undefined>();
	@Output() nestedClick = new EventEmitter<R>();
	isOpen = new BehaviorSubject<boolean>(false);

	constructor() {}

	mainClick() {
		this.isOpen.next(!this.isOpen.getValue());
	}
	removeHover(element: R) {
		this.hoveredElements = this.hoveredElements.filter(
			(item) => item !== element.id
		);
	}
}
