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
import { feature, trackableFeature } from './features/base';
import { baseFeature } from './features/feature';

export class writeFeature extends baseFeature implements feature {}
export class modifyFeature extends baseFeature implements trackableFeature {
	constructor(
		feature?: trackableFeature,
		valueStr?: string,
		productAppStr?: string
	) {
		super();
		this.id = (feature && feature.id) || '';
		this.defaultValue = (feature && feature.defaultValue) || '';
		this.description = (feature && feature.description) || '';
		this.idIntValue = feature && feature?.idIntValue;
		this.idString = (feature && feature?.idString) || '';
		this.multiValued = (feature && feature?.multiValued) || false;
		this.name = (feature && feature?.name) || '';
		this.productApplicabilities =
			(feature && feature?.productApplicabilities) || [];
		this.type = feature && feature?.type;
		this.valueType = (feature && feature?.valueType) || '';
		this.values = (feature && feature.values) || [];
		this.productAppStr = productAppStr || '';
		this.valueStr = valueStr || '';
	}
	id: string = '';
	idIntValue?: number;
	idString: string = '';
	type: null | undefined;
}
export interface PLAddFeatureData extends PlFeatureData {
	feature: writeFeature;
}
interface PlFeatureData {
	currentBranch: string | number | undefined;
}
export interface PLEditFeatureData extends PlFeatureData {
	editable: boolean;
	feature: modifyFeature;
}
