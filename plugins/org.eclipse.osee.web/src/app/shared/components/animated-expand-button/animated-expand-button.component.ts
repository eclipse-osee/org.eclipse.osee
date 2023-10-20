/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { Component, Input, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { skip } from 'rxjs';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';
import { toObservable } from '@angular/core/rxjs-interop';

@Component({
	selector: 'osee-animated-expand-button',
	standalone: true,
	imports: [CommonModule, MatIconModule, MatListModule, MatButtonModule],
	templateUrl: './animated-expand-button.component.html',
})
export class AnimatedExpandButtonComponent {
	@Input() set open(value: boolean) {
		this._open.set(value);
	}
	protected _open = signal<boolean>(false);
	@Output() openChange = toObservable(this._open).pipe(skip(1));

	toggle() {
		this._open.set(!this._open());
	}
}
