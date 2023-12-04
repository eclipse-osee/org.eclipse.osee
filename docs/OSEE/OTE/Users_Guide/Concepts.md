# Concepts

## Core Test Enviroment

TestEnvironment is the base class the provides the core execution
management of tests to all of the other more specialized test
environments. It manages models, test execution, and holds onto many
interfaces that determine the behavior of the environment. It is the
starting point for testing in OSEE.

## Message System Test Enviroment

The Message System Test Environment is the base environment that
provides message and element support. This includes most of the element
types such as Integer, Float, etc... and the checks that are available
on both the message and the element. It also adds in the remote message
service which enables all of the messaging based tools that remote
clients use.

## Model

Models in the OTE environment are intended to be java objects that
'model' some aspect of the test environment needed by the Unit Under
Test. Most likely this will be an external device that communicates with
the unit under test so that different scenarios can be recreated and
tested.

OTE provides management capabilities for models so that they can be
easily accessed from both Java test programs and GUI's in the testers
workbench.

