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
import { feature } from './base';

export class baseFeature implements feature {
	constructor(defaultFeature?: defaultBaseFeature) {
		if (defaultFeature) {
			this.valueType = defaultFeature.valueType;
			this.valueStr = defaultFeature.valueStr;
			this.defaultValue = defaultFeature.defaultValue;
			this.productAppStr = defaultFeature.productAppStr;
			this.productApplicabilities = defaultFeature.productApplicabilities;
			this.values = defaultFeature.values;
			this.multiValued = defaultFeature.multiValued;
			this.name = defaultFeature.name;
			this.description = defaultFeature.description;
		}
	}
	public name = '';
	public description = '';
	public valueType = '';
	public valueStr = '';
	public defaultValue = '';
	public productAppStr = '';
	public values: string[] = [];
	public productApplicabilities: string[] = [];
	public multiValued = false;
	public setValueStr(): void {
		this.valueStr = this.values.toString();
	}
	public setProductAppStr(): void {
		this.productAppStr = this.productApplicabilities.toString();
	}
}
export class defaultBaseFeature implements feature {
	public name = '';
	public description = '';
	public valueType = 'String';
	public valueStr = 'String';
	public defaultValue = 'Included';
	public productAppStr = '';
	public values: string[] = ['Included', 'Excluded'];
	public productApplicabilities: string[] = [];
	public multiValued = false;
	public setValueStr(): void {
		this.valueStr = this.values.toString();
	}
	public setProductAppStr(): void {
		this.productAppStr = this.productApplicabilities.toString();
	}
}
