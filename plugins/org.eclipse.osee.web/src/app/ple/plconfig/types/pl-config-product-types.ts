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
export interface productType {
	id?: string;
	name: string;
	description: string;
}
export class ProductType implements productType {
	constructor(name: string, description: string, id?: string) {
		this.name = name;
		this.description = description;
		if (this.id) {
			this.id = id;
		}
	}
	id?: string;
	name: string = '';
	description: string = '';
}

export class RequiredProductType extends ProductType {
	id: string;
	constructor(name: string, description: string, id: string) {
		super(name, description, id);
		this.id = id;
	}
}

export class DefaultProductType extends ProductType {
	constructor() {
		super('', '');
	}
}
