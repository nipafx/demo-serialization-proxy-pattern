package org.codefx.lab.serialization.proxypattern;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Immutable representation of a complex number.
 */
@SuppressWarnings("serial")
public final class ComplexNumber implements Serializable {

	/*
	 * Note that this class was designed to give a good example for serialization, not to provide a realistic
	 * implementation of a complex number. That being said, imagine it stores both representations (coordinates and
	 * polar form) for performance reasons.
	 */

	// FIELDS (define the state so they are of special interest for serialization)

	/*
	 * The serialization proxy should capture the perfect logical representation. Since storing the coordinate and the
	 * polar form for performance reasons is redundant and an implementation detail, it should not show in that
	 * representation. Hence the proxy only uses the coordinates to store the number. With the default or a custom
	 * serialized form the missing numbers would have to be initialized after deserialization so they couldn't be final.
	 * Due to the pattern, that is not the case here and all fields are final (as they should be).
	 */

	// serialized by the proxy
	private final double real;

	// serialized by the proxy
	private final double imaginary;

	// NOT serialized by the proxy
	private final double magnitude;

	// NOT serialized by the proxy
	private final double angle;

	// CONSTRUCTION (good example for static factory methods)

	private ComplexNumber(double real, double imaginary, double magnitude, double angle) {
		super();
		this.real = real;
		this.imaginary = imaginary;
		this.magnitude = magnitude;
		this.angle = angle;
	}

	// - from coordinates

	public static ComplexNumber fromCoordinates(double real, double imaginary) {
		double magnitude = computeMagnitude(real, imaginary);
		double angle = computeAngle(real, imaginary);
		return new ComplexNumber(real, imaginary, magnitude, angle);
	}

	private static double computeMagnitude(double real, double imaginary) {
		return sqrt(pow(real, 2) + pow(imaginary, 2));
	}

	private static double computeAngle(double real, double imaginary) {
		return atan2(imaginary, real);
	}

	// - from polar form

	public static ComplexNumber fromPolar(double magnitude, double angle) {
		double real = computeReal(magnitude, angle);
		double imaginary = computeImaginary(magnitude, angle);
		return new ComplexNumber(real, imaginary, magnitude, angle);
	}

	private static double computeReal(double magnitude, double angle) {
		return magnitude * cos(angle);
	}

	private static double computeImaginary(double magnitude, double angle) {
		return magnitude * sin(angle);
	}

	// ACCESSORS

	public double getReal() {
		return real;
	}

	public double getImaginary() {
		return imaginary;
	}

	public double getMagnitude() {
		return magnitude;
	}

	public double getAngle() {
		return angle;
	}

	// OTHER

	@Override
	public String toString() {
		return String.format("ComplexNumber (%.2f/%.2fi; %.2f@%.2fpi)", real, imaginary, magnitude, angle);
	}

	// SERIALIZATION BY PROXY

	/**
	 * Serialize the created proxy instead of this instance.
	 */
	private Object writeReplace() {
		System.out.println("-- replacing " + this + " with proxy on serialization");
		return new SerializationProxy(this);
	}

	/**
	 * Ensure that no instance of {@link ComplexNumber} is created because it was present in the stream. A correct
	 * stream should only contain instances of the proxy.
	 */
	private void readObject(ObjectInputStream stream) throws InvalidObjectException {
		throw new InvalidObjectException("Proxy required.");
	}

	private static class SerializationProxy implements Serializable {

		private static final long serialVersionUID = -5617583940055969353L;

		private final double real;

		private final double imaginary;

		public SerializationProxy(ComplexNumber complexNumber) {
			this.real = complexNumber.real;
			this.imaginary = complexNumber.imaginary;
		}

		/**
		 * After the proxy is deserialized, it invokes a static factory method to create a {@link ComplexNumber}
		 * "the regular way".
		 */
		private Object readResolve() {
			System.out.println("-- replacing " + this + " with complex number on deserialization");
			return ComplexNumber.fromCoordinates(real, imaginary);
		}

		@Override
		public String toString() {
			return String.format("ComplexNumber.SerializationProxy (%.2f/%.2fi)", real, imaginary);
		}
	}

}
