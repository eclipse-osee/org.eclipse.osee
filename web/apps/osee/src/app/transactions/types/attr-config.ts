/*********************************************************************
 * Copyright (c) 2026 Boeing
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
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import { attribute } from '@osee/attributes/types';

export type attrConfig = {
	set?: attribute<
		string | number | boolean | unknown[] | unknown,
		ATTRIBUTETYPEID
	>[];
	add?: attribute<
		string | number | boolean | unknown[] | unknown,
		ATTRIBUTETYPEID
	>[];
	delete?: attribute<
		string | number | boolean | unknown[] | unknown,
		ATTRIBUTETYPEID
	>[];
};
