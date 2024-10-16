#!/bin/bash

#
# Bash shell script to create java-rust ffi. 
# Update the JAVA_FILE_DIR, RUST_TARGET_RELEASE_DIR variables if the location of this script changes from org.eclipse.osee\plugins\org.eclipse.osee.framework.core\scripts\build_java_rust_ffi_jar.sh
#
# Author: Jaden W. Puckett
#

#
# Define global variables
#

BASE_DIR="java_rust_ffi"
JAVA_PACKAGE_NAME="applicability"
JAVA_DIR="${BASE_DIR}/${JAVA_PACKAGE_NAME}"
NATIVE_DIR="${BASE_DIR}/native"
JAVA_FILE="ApplicabilityParseSubstituteAndSanitize.java"
MANIFEST_FILE="MANIFEST.MF"

# !!! Update these variables if the location of this script changes
JAVA_FILE_DIR="../src/org/eclipse/osee/framework/core/applicability/${JAVA_FILE}"
RUST_TARGET_RELEASE_DIR="../../../target/release"

#
# Create necessary directories
#

mkdir -p "." || { echo "Failed to create directory: ${BASE_DIR}"; exit 1; }
echo "Created directory: ${BASE_DIR} (Complete)"

mkdir -p "${JAVA_DIR}" || { echo "Failed to create directory: ${JAVA_DIR}"; exit 1; }
echo "Created directory: ${JAVA_DIR} (Complete)"

mkdir -p "${NATIVE_DIR}" || { echo "Failed to create directory: ${NATIVE_DIR}"; exit 1; }
echo "Created directory: ${NATIVE_DIR} (Complete)"

# Create platform-specific directories under the native directory
mkdir -p "${NATIVE_DIR}/mac" || { echo "Failed to create directory: ${NATIVE_DIR}/mac"; exit 1; }
echo "Created directory: ${NATIVE_DIR}/mac (Complete)"

mkdir -p "${NATIVE_DIR}/win" || { echo "Failed to create directory: ${NATIVE_DIR}/win"; exit 1; }
echo "Created directory: ${NATIVE_DIR}/win (Complete)"

mkdir -p "${NATIVE_DIR}/linux" || { echo "Failed to create directory: ${NATIVE_DIR}/linux"; exit 1; }
echo "Created directory: ${NATIVE_DIR}/linux (Complete)"

echo ""

#
# Build native libraries using Cargo
#

echo "Running cargo build --release..."
cargo build --release || { echo "Cargo build failed"; exit 1; }
echo "Cargo build (Complete)"

echo ""

#
# Copy native libraries from RUST_TARGET_RELEASE_DIR to platform-specific directories
#

echo "Copying native libraries from "${RUST_TARGET_RELEASE_DIR}"..."

# Copy .dylib files to the mac directory
cp "${RUST_TARGET_RELEASE_DIR}"/*.dylib "${NATIVE_DIR}/mac/" 2>/dev/null
if [ "$(ls -A "${NATIVE_DIR}/mac/")" ]; then
    echo "Files successfully copied to mac: "
    echo "$(ls "${NATIVE_DIR}/mac/") (Complete)"
else
    echo "No .dylib files found for mac."
fi

# Copy .dll files to the win directory
cp "${RUST_TARGET_RELEASE_DIR}"/*.dll "${NATIVE_DIR}/win/" 2>/dev/null
if [ "$(ls -A "${NATIVE_DIR}/win/")" ]; then
    echo "Files successfully copied to win: "
    echo "$(ls "${NATIVE_DIR}/win/") (Complete)"
else
    echo "No .dll files found for win."
fi

# Copy .so files to the linux directory
cp "${RUST_TARGET_RELEASE_DIR}"/*.so "${NATIVE_DIR}/linux/" 2>/dev/null
if [ "$(ls -A "${NATIVE_DIR}/linux/")" ]; then
    echo "Files successfully copied to linux: "
    echo "$(ls "${NATIVE_DIR}/linux/") (Complete)"
else
    echo "No .so files found for linux."
fi

echo ""

# 
# Extract the Java file (one java file for now...)
#

echo "Copying ${JAVA_FILE}..."
cp ../src/org/eclipse/osee/framework/core/applicability/"${JAVA_FILE}" "${BASE_DIR}/"
echo "Copied ${JAVA_FILE} to ${BASE_DIR} (Complete)"

echo ""

# 
# Replace package references from the Java file
#

echo "Removing package references from the Java file..."
sed -i "/^package/c\package ${JAVA_PACKAGE_NAME};" "${BASE_DIR}/${JAVA_FILE}" 
echo "Removed package references from ${JAVA_FILE} (Complete)"

echo ""

# 
# Navigate to JAR directory
#

cd "${BASE_DIR}" || { echo "Failed to navigate to ${BASE_DIR}"; exit 1; }
echo "Navigated to ${BASE_DIR} (Complete)"

echo ""

# 
# Create a manifest file
#

echo "Creating manifest file..."
{
  echo "Manifest-Version: 1.0"
  echo "Main-Class: ApplicabilityParseSubstituteAndSanitize"
} > "${MANIFEST_FILE}"
echo "Created manifest file ${MANIFEST_FILE} (Complete)"

echo ""

#
# Compile Java file(s)
#

echo "Compiling Java file(s)..."
javac -d . -h . "${JAVA_FILE}" || { echo "Compilation failed"; exit 1; }
echo "Java file(s) compiled (Complete)"

echo ""

#
# Package everything into a JAR
#

echo "Creating JAR file..."
jar cfm "${BASE_DIR}.jar" "${MANIFEST_FILE}" -C . .
echo "JAR file ${BASE_DIR}.jar created (Complete)"

echo ""

echo "Build complete. JAR file created: ${BASE_DIR}/${BASE_DIR}.jar"
