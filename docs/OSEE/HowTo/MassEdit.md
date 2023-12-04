There are times, particularly with enumerated attributes, when it is
useful to be able to change the value of an attribute in many artifacts
at the same time.

Mass edit is the way to do this.

In Artifact explorer, select all the artifacts that you want to change
(the "Expand All" option is often useful here), then right-click "Mass
Edit".

In the Mass Edit window, again select the artifacts that you want to
change. Note that they must all be of the same artifact type for this to
work. Then, right-click "Column Multi Edit", pick the name of the
attribute you want to change, and then the new value.

Save, and all the artifacts are updated.

Note: If the arttribute is an enumeration, you get three options when
choosing the new value.

The default is to add the new value to the enumeration if it is not
already chosen so, for example, a value of "None" where they were all
"E" will yield a result of "E","None"

The next, which is typically what you want, is to replace any existing
values with the new value so, for example, a value of "None" where they
were all "E" will yield a result of "None"

The final option is to remove the value if it is already chosen so, for
example, a value of "E" where they were all "E","None" will yield a
result of "None"