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
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
	name: 'splitApplicability',
	standalone: true,
})
export class SplitApplicabilityPipe implements PipeTransform {
	//eslint-disable-next-line @typescript-eslint/no-unused-vars
	transform(value: string): string {
		if (!value) {
			return "Excluded";
		}
		if (value.includes('|') || value.includes('&')) {
			return "Included"; // Return Included if a compound applicability
		}
		const parts = value.split(/\s?=\s?/);
		return parts.length > 1 ? parts[1] : value;
	}
}
