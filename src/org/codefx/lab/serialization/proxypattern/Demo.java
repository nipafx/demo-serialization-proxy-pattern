package org.codefx.lab.serialization.proxypattern;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

public class Demo {

	public static void main(String[] args) throws Exception {
		Demo demo = new Demo();

		demo.serializeComplexNumbers();
		demo.serializeInstanceCache();
	}

	// DEMO

	private void serializeComplexNumbers() throws Exception {
		ComplexNumber one = ComplexNumber.fromCoordinates(1, 0);
		print("instance to serialize: " + one);
		ComplexNumber oneDeserialized = serializeAndDeserialize(one);
		print("deserialized instance: " + oneDeserialized);
		print("");

		ComplexNumber random = ComplexNumber.fromCoordinates(randomCoordinate(), randomCoordinate());
		print("instance to serialize: " + random);
		ComplexNumber randomDeserialized = serializeAndDeserialize(random);
		print("deserialized instance: " + randomDeserialized);
		print("");
	}

	private static double randomCoordinate() {
		double randomInInterval0To1 = new Random().nextDouble();
		double randomInInterval0To20 = randomInInterval0To1 * 20;
		double randomInIntervalMinus10To10 = randomInInterval0To20 - 10;
		return randomInIntervalMinus10To10;
	}

	private void serializeInstanceCache() throws Exception {
		InstanceCache cache = new InstanceCache();
		cache.put("a string");
		cache.put(0);
		cache.put(new NotSerializableString("not serializable!"));
		print("instance to serialize: " + cache);

		InstanceCache cacheDeserialized = serializeAndDeserialize(cache);
		print("deserialized instance: " + cacheDeserialized);
	}

	private static class NotSerializableString {

		private final String value;

		public NotSerializableString(String value) {
			super();
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	// USABILITY

	/**
	 * Serializes the specified instance to disk. Then deserializes the file and returns the deserialized value.
	 * 
	 * @param serialized
	 *            the instance to be serialized
	 * @return the deserialized instance
	 * @throws Exception
	 *             if (de)serialization fails
	 */
	private static <T> T serializeAndDeserialize(T serialized) throws Exception {
		File serializeFile = new File("_serialized");
		// serialize
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(serializeFile))) {
			out.writeObject(serialized);
		}
		print("-- serialized");
		// deserialize
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(serializeFile))) {
			@SuppressWarnings("unchecked")
			T deserialized = (T) in.readObject();
			print("-- deserialized");
			return deserialized;
		}
	}

	/**
	 * Prints the specified text to the console.
	 *
	 * @param text
	 *            the text to print
	 */
	private static void print(String text) {
		System.out.println(text);
	}

}
