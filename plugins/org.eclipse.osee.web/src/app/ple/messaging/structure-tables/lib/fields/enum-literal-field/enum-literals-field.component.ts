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
import { NgFor, NgIf } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
	standalone: true,
	selector: 'osee-enum-literals-field',
	templateUrl: './enum-literals-field.component.html',
	styleUrls: ['./enum-literals-field.component.sass'],
	imports: [NgIf, NgFor],
})
export class EnumLiteralsFieldComponent {
	@Input() enumLiterals: string = '';
	@Input() wordWrap: boolean = false;

	toggleExpanded() {
		this.wordWrap = !this.wordWrap;
	}

	getEnumLiteralsArray() {
		return this.enumLiterals.split('\n');
	}
}
