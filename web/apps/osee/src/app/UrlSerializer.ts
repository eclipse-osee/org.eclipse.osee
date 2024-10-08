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
import { UrlSerializer, UrlTree, DefaultUrlSerializer } from '@angular/router';

export class OseeUrlSerializer implements UrlSerializer {
	parse(url: string): UrlTree {
		const dus = new DefaultUrlSerializer();
		return dus.parse(url);
	}

	serialize(tree: UrlTree): string {
		const defaultSerializer = new DefaultUrlSerializer(),
			path = defaultSerializer.serialize(tree);
		// use your regex to replace as per your requirement.
		return path.replace(/-/g, '%2D').replace(/%20%3E%20/g, '%3E');
	}
}
