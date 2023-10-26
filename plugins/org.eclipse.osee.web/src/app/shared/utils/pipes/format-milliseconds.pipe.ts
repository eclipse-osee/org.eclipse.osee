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
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
	name: 'formatMilliseconds',
	standalone: true,
})
export class FormatMillisecondsPipe implements PipeTransform {
	transform(value: unknown): string {
		const ms = Number(value);
		if (isNaN(ms) || value === '') {
			return '';
		}
		const hrs = Math.floor(ms / 1000 / 60 / 60);
		let remainingTime = ms - hrs * 60 * 60 * 1000;
		const mins = Math.floor(remainingTime / 1000 / 60);
		remainingTime = remainingTime - mins * 60 * 1000;
		const secs = Math.floor(remainingTime / 1000);
		return (
			this.padZeros(hrs) +
			':' +
			this.padZeros(mins) +
			':' +
			this.padZeros(secs)
		);
	}

	private padZeros(val: string | number) {
		const str = String(val);
		return str.length == 1 ? '0' + str : str;
	}
}
