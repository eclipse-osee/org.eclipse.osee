/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.core.enums;

import java.sql.JDBCType;

import org.eclipse.osee.framework.jdk.core.type.NamedBase;

/**
 * @author Ryan D. Brooks
 */
public class SqlColumn extends NamedBase {
	private final int length;
	private final JDBCType type;
	private final boolean isNull;
	private final SqlTable table;

	public SqlColumn(SqlTable table, String name, JDBCType type) {
		this(table, name, type, false, 0);
	}

	public SqlColumn(SqlTable table, String name, JDBCType type, boolean isNull) {
		this(table, name, type, isNull, 0);
	}

	public SqlColumn(SqlTable table, String name, JDBCType type, boolean isNull, int length) {
		super(name);
		this.table = table;
		this.length = length;
		this.type = type;
		this.isNull = isNull;
	}

	public JDBCType getType() {
		return type;
	}

	public int getLength() {
		return length;
	}

	public boolean isNull() {
		return isNull;
	}

	public SqlTable getTable() {
		return table;
	}
}