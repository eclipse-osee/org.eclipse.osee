[comment]: # (Copyright (c) 2023 Boeing This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0/ SPDX-License-Identifier: EPL-2.0 Contributors: Boeing - initial API and implementation)

graph TD
B([tables]) --> A([lib]);
C([fields]) --> A([lib]);
D([dialogs]) --> A([lib]);
E([menus]) --> A([lib]);
D([dialogs]) --> B([tables]);
C([fields]) --> B([tables]);
