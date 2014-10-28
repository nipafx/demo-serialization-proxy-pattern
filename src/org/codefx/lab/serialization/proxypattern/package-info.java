/**
 * A demo on how to implement the Serialization Proxy Pattern.
 * <p>
 * A simple is example is the class {@link org.codefx.lab.serialization.proxypattern.ComplexNumber ComplexNumber} which
 * stores redundant information for performance reasons but should not fix that implementation detail by serializing its
 * complete state.
 * <p>
 * The {@link org.codefx.lab.serialization.proxypattern.Demo Demo} shows that instances of the classes can indeed be
 * deserialized.
 * 
 * @see http://
 */
package org.codefx.lab.serialization.proxypattern;