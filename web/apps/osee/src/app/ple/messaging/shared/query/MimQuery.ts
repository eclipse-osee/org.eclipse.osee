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
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import type { MIMATTRIBUTETYPEID } from '@osee/messaging/shared/attr';
import {
	ARTIFACTTYPEID,
	ARTIFACTTYPEIDENUM,
	RELATIONTYPEID,
} from '@osee/shared/types/constants';
import type { enumerationSet } from '../types/enum';
import type { PlatformType } from '../types/platformType';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
type _mimQuery<T> = {
	type: ARTIFACTTYPEID;
	related?: {
		relation: RELATIONTYPEID;
		relatedId: string;
		side: 'SIDE_A' | 'SIDE_B';
	};
	queries?: _andQuery[];
};
type _andQuery = {
	attributeId: MIMATTRIBUTETYPEID;
	value: string;
};
export class andQuery implements _andQuery {
	constructor(attributeTypeId: MIMATTRIBUTETYPEID, value: string) {
		this.attributeId = attributeTypeId;
		this.value = value;
	}
	attributeId: MIMATTRIBUTETYPEID;
	value: string;
}
export class andUnitQuery extends andQuery {
	constructor(value: string) {
		super(ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEUNITS, value);
	}
}
export class andBitSizeQuery extends andQuery {
	constructor(value: string) {
		super(ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEBITSIZE, value);
	}
}
export class andLogicalTypeQuery extends andQuery {
	constructor(value: string) {
		super(ATTRIBUTETYPEIDENUM.LOGICALTYPE, value);
	}
}
export class andMinValQuery extends andQuery {
	constructor(value: string) {
		super(ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMINVAL, value);
	}
}
export class andMaxValQuery extends andQuery {
	constructor(value: string) {
		super(ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMAXVAL, value);
	}
}
export class andDefaultValQuery extends andQuery {
	constructor(value: string) {
		super(ATTRIBUTETYPEIDENUM.INTERFACEDEFAULTVAL, value);
	}
}
export class andMsbValQuery extends andQuery {
	constructor(value: string) {
		super(ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMSBVAL, value);
	}
}
export class andNameQuery extends andQuery {
	constructor(value: string) {
		super(ATTRIBUTETYPEIDENUM.NAME, value);
	}
}

export class andDescriptionQuery extends andQuery {
	constructor(value: string) {
		super(ATTRIBUTETYPEIDENUM.DESCRIPTION, value);
	}
}
export class MimQuery<T = unknown> implements _mimQuery<T> {
	constructor(
		type: ARTIFACTTYPEID,
		related?: {
			relation: RELATIONTYPEID;
			relatedId: string;
			side: 'SIDE_A' | 'SIDE_B';
		},
		queries?: _andQuery[]
	) {
		this.type = type;
		this.related = related;
		this.queries = queries;
	}
	type: ARTIFACTTYPEID;
	related?:
		| {
				relation: RELATIONTYPEID;
				relatedId: string;
				side: 'SIDE_A' | 'SIDE_B';
		  }
		| undefined;
	queries?: _andQuery[] | undefined;
}
export class PlatformTypeQuery extends MimQuery<PlatformType> {
	constructor(
		related?: {
			relation: RELATIONTYPEID;
			relatedId: string;
			side: 'SIDE_A' | 'SIDE_B';
		},
		queries?: _andQuery[]
	) {
		super(ARTIFACTTYPEIDENUM.PLATFORMTYPE, related, queries);
	}
}

export class EnumerationSetQuery extends MimQuery<enumerationSet> {
	constructor(
		related?: {
			relation: RELATIONTYPEID;
			relatedId: string;
			side: 'SIDE_A' | 'SIDE_B';
		},
		queries?: _andQuery[]
	) {
		super(ARTIFACTTYPEIDENUM.ENUMSET, related, queries);
	}
}
