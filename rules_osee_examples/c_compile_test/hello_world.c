/*
#
# Copyright (c) 2024 Boeing
#
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0
#
# Contributors:
#     Boeing - initial API and implementation
#
*/ 
#include "test.h"
#include "test2.h"

int add_one(int value){
    return value+HELLO_WORLD;
}
int add_two(int value){
    return value+HELLO_WORLD+TEST_HELLO_WORLD;
}