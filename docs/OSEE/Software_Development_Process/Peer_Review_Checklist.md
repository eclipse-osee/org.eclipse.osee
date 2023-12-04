## Arithmetic

1.  Are divisors tested for zero?
2.  Do comparisons of floating point numbers have tolerance?

## Change Management

1.  Does the commit have a single focus?
2.  Is the commit too complicated to review effectively?

## Classes

1.  Do any subclasses have common members that should be in the
    superclass?
2.  Can the class inheritance hierarchy be simplified?
3.  Are any modules excessively complex and need to be restructured or
    decomposed?
4.  Do class names reflect the functionality they provide?
5.  Does the class have a clear singular responsibility?
6.  Does the class have a clear lifecycle?

## Code Style/Formatting

1.  Has the standard formatter been applied?
2.  Are methods and classes within size guidelines?
3.  Are coding guidelines checked with tools?

## Comments

1.  Do the comments help understand the code?
2.  Are all comments consistent with the code?
3.  Do the commit comments follow the convention?
4.  Does the code contain sufficient, descriptive, and well written
    comments?
5.  Are comments only used for technical notes?
6.  Are any code segments commented out?

## Concurrency

1.  Are there mutable member variables in classes with static methods
    that can be called concurrently?

## Configuration

1.  Is the constant or configurable item acquired by a centralized
    object?
2.  Does the item need to be configurable?

## Control Flow

1.  Are all for-loop control variables declared in the loop header?
2.  Is the best choice of looping constructs used?
3.  Will all loops terminate?
4.  When multiple exits from a loop are used, is each exit necessary and
    handled properly?
5.  Does each switch statement have a default case?
6.  Are missing switch case break statements correct and marked with a
    comment?
7.  Is the nesting of loops and branches too deep, and is it correct?
8.  Are conditionals checking for the positive?
9.  When ‘goto’ statements (continue, break, or multiple returns) are
    used, is each necessary and handled properly?
10. Is polymorphism used to operate on items using a single
    if/else/switch to construct polymorphic objects instead of using
    if/else/switch each time we want to manipulate items of that type?

## Exception Handling

1.  Are caught exceptions handled in a meaningful way?
2.  When exceptions are swallowed, is the exceptional case completely
    resolved? Was the system left in a consistent state?
3.  Are Exceptions only used for signaling error conditions and not flow
    control, which causes bad performance?
4.  Are runtime and checked exceptions and throwable java.lang.error
    considered and handled appropriately?

## I/O

1.  Are all resources closed after use (files, images… etc.)\[using
    try…finally\]?
2.  Is the read buffer of a resource appropriately sized?
3.  Does the reading of resources take into account memory usage
    \[streaming\]?
4.  Is I/O access minimized using an appropriate streaming mechanism?

## Memory

1.  Are intermediate objects, used in computations, scoped appropriately
    so they can be garbage collected quickly when no longer needed?

## Methods

1.  Are descriptive method names used in accord with naming conventions?
2.  Do all methods have appropriate access modifiers (private,
    protected, public)?
3.  Should any static methods be non-static, or any non-static methods
    be static?
4.  Do methods have a clear flow and return criteria?
5.  Do method names describe the entire functionality?
6.  Does the method provide one function?
7.  Is an empty set returned instead of null (Collections.empty…)?
8.  Avoid returning null, or, at a minimum, document (in the interface
    if applicable) methods that may return null.

## Modularity

1.  Do modules have a low level of coupling between them (methods and
    classes)?
2.  Do modules have a high level of cohesion within each one (methods or
    class)?
3.  Is dependency injection used instead of singletons? (singletons hide
    dependencies)

## Performance

1.  Can better data structures or more efficient algorithms be used?
2.  Are logical tests arranged such that the often successful and
    inexpensive tests precede the more pensive and less frequently
    successful tests?
3.  Can the cost of re-computing a value be reduced by computing it once
    and storing the results?
4.  Is every result that is computed and stored actually used?
5.  Can a computation be moved outside a loop?
6.  Do all tests within a loop need to be performed?
7.  Can multiple loops, operating on the same data, be combined into one
    loop?

## Redundant Code

1.  Are any variables or attributes redundant or unused?
2.  Does the code contain any uncalled or unneeded methods or leftover
    code stubs?
3.  Can any code be replaced by calls to external reusable objects?
4.  Are any blocks of code repeated that could be condensed into a
    single method?

## REST

1.  Are the JAX-RS annotations only on the endpoint interface (and not
    on the implementation)

## Variables

1.  Are descriptive variable and constant names used in accord with
    naming conventions (camel)?
2.  Are all variables properly defined with meaningful, consistent, and
    clear names?
3.  Is every variable correctly typed?
4.  Is every variable properly initialized?
5.  Should any of the variables be constants (final)?
6.  Should any of the fields be local variables?
7.  Do all fields have appropriate access modifiers (private, protected,
    public)?
8.  Should any static fields be non-static, or any non-static fields be
    static?
9.  Do any variables have confusingly similar names as other variables?
10. Are variables defined close to where they are used?
11. Are variables used defensively (add link)?

