JNI Wrapper

Do not change any of the method names/parameters/output types within the lib.rs without ensuring that the dependent java file(s) used to build the jar(s) have the appropriate name(s). Generate C header file(s) for the java file(s) method(s) to see what the expected name(s)/declaration(s) should be in Rust.
