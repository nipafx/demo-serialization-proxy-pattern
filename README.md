# Serialization Proxy Pattern

This project provides the code samples for the CodeFX post about the Serialization Proxy Pattern. It contains the following classes, which all implement the pattern:

* [ComplexNumber](https://github.com/CodeFX-org/demo-serialization-proxy-pattern/blob/master/src/org/codefx/lab/serialization/proxypattern/ComplexNumber.java): a simple examle where the class can keep its field's final due to the pattern.

As usual, the [Demo](https://github.com/CodeFX-org/demo-serialization-proxy-pattern/blob/master/src/org/codefx/lab/serialization/proxypattern/Demo.java) shows that everything worls as intended. It serializes and deserializes instances of the above classes and prints some explanatory messages to `System.out`.
