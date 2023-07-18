## 1.1 Robot API

### 1.1.1 Robot Interfaces

The robot API shall provide an interface to the different robots that may be attached to the system, including:

#### 1.1.1.1 Research daVinci systems

Via the read-write research interface (Reference 2.1.2).

#### 1.1.1.2 Clinical daVinci systems

Via the read-only research interface (older V4.x interface mentioned in Reference 2.1.2).

#### 1.1.1.3 DaVinci master and slave arms

Via the JHU controller.

#### 1.1.1.4 JHU robots

Such as the “snake” robot and the steady hand robot for retinal surgery.

### 1.1.2 Interface Initialization

The robot API shall provide method(s) to initialize the interface. For the daVinci research API, this would encompass the stream management functions.

### 1.1.3 Robot collaboration

The robot API shall provide an interface to individual robots (e.g., daVinci PSM or MTM) and collaborations of multiple robots:

#### 1.1.3.1 Robot Object

Each individual robot shall be represented by an instance of a robot object. The methods of that object shall provide the API for a single robot. This is analogous to the “manipulator” commands in the daVinci research API.

#### 1.1.3.2 Collaborative Robot

Collaborative groups of robots (such as master-slave pairs) shall be represented by an instance of a “collaborative robot” object, which shall contain two or more robot objects, as well as other devices, such as surgeon console buttons (see Section 4.3.1). This is analogous to the “supervisor” commands in the daVinci research API.

### 1.1.4 Read-only Robots

Individual and collaborative robots can be “read-only” (i.e., provide only state information) or “read-write” (i.e., provide state information and allow state changes).

### 1.1.5 CISST fundamental data types

The robot API for the individual and collaborative robot objects shall use the CISST fundamental data types (vectors, matrices, transformations), rather than the math support functions in the daVinci research API. This may require translation from CISST data types to ISI data types (e.g., array of floats) and vice-versa.

### 1.1.6 Functional Specification

The methods of the individual and collaborative robot objects shall be documented in an external database/document, which shall become the functional specification for the robot API. This database shall contain the following information:

#### 1.1.6.1 Method name

#### 1.1.6.2 Number and types of parameters

#### 1.1.6.3 Functional description

### 1.1.7 Events

The API shall generate events to notify the user application about asynchronous actions detected by the lower level software. The events of the individual and collaborative robot objects shall be documented in an external database/document.

#### 1.1.7.1 Individual robot events

Individual robot events shall include: Emergency stop signaled, power amplifier fault, hardware limit reached, etc. (if available for that robot).

#### 1.1.7.2 Collaborative robot events

Collaborative robot events shall include all individual robot events and master console events such as buttons or pedals pressed.

#### 1.1.7.3 Extendable Events Architecture

The API shall have an extendable architecture to allow new events to be added.

#### 1.1.7.4 Event Implementation

Individual and collaborative robots are not required to implement all defined events.

### 1.1.8 Virtual fixtures

#### 1.1.8.1 Haptic Constraints

The API shall include commands for specifying and enabling haptic constraints, such as virtual fixtures.

#### 1.1.8.2 JHU and the ISI Constraint Primitives

This interface shall be designed to incorporate, as seamlessly as possible, both the JHU constrained optimization implementation (Reference 2.3.1) and the ISI embedded constraint primitives (e.g., infinite planes, triangle patches, detents) described in Reference 2.1.2
