package org.codefx.lab.serialization.proxypattern;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A heterogeneous container, which can cache one instance per class.
 */
@SuppressWarnings("serial")
public final class InstanceCache implements Serializable {

	/*
	 * Note that this class was designed to give a good example for serialization, not to provide a realistic
	 * implementation of a cache. That being said, imagine the cache was developed for performance reasons (not for
	 * correctness of some subsystem) and it is ok that non-serializable instance go missing when the cache is
	 * serialized.
	 */

	// FIELDS (define the state so they are of special interest for serialization)

	/*
	 * The serialization proxy should capture the perfect logical representation. Storing the classes together with the
	 * instances is redundant, so it is not done. A simple serializable representation of a bulk of instances is an
	 * array list so it is chosen as the proxy's data structure. The map can contain instances of every type but because
	 * the contract states that only the serializable ones need to be written to the stream, the others can and must be
	 * filtered out.
	 */

	// stored by the proxy but filtered and in different type
	private final ConcurrentMap<Class<?>, Object> cacheMap;

	public InstanceCache() {
		cacheMap = new ConcurrentHashMap<>();
	}

	public InstanceCache(Iterable<?> initialInstances) {
		this();
		for (Object instance : initialInstances)
			cacheMap.put(instance.getClass(), instance);
	}

	// ACCESSORS

	public <T> T put(T instance) {
		Object previousInstance = cacheMap.put(instance.getClass(), instance);
		return castInstanceFromMap(previousInstance);
	}

	private static <T> T castInstanceFromMap(Object instance) {
		@SuppressWarnings("unchecked")
		/*
		 * The only way the instance could have gotten into the map is the 'put' above. This associates the instance
		 * with its own class. So if it is returned by the map due to a 'put' with some class as key, it must be of that
		 * class. The cast can hence not fail.
		 */
		T typedInstance = (T) instance;
		return typedInstance;
	}

	public <T> Optional<T> get(Class<T> clazz) {
		Object instance = cacheMap.get(clazz);
		T instanceOfCorrectType = clazz.cast(instance);
		return Optional.ofNullable(instanceOfCorrectType);
	}

	public <T> boolean containsKey(Class<T> clazz) {
		return cacheMap.containsKey(clazz);
	}

	// OTHERS

	@Override
	public String toString() {
		String prefixWithItemCount = "[" + cacheMap.size() + " items: ";
		StringJoiner joiner = new StringJoiner(", ", prefixWithItemCount, "]");
		cacheMap.entrySet().stream()
				.map(InstanceCache::entryToString)
				.forEach(joiner::add);
		return "InstanceCache " + joiner;
	}

	private static final String entryToString(Entry<Class<?>, ?> entry) {
		return entryToString(entry.getKey(), entry.getValue());
	}

	private static final String entryToString(Class<?> key, Object value) {
		return key.getSimpleName() + " (" + value + ")";
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
	 * Ensure that no instance of {@link InstanceCache} is created because it was present in the stream. A correct
	 * stream should only contain instances of the proxy.
	 */
	private void readObject(ObjectInputStream stream) throws InvalidObjectException {
		throw new InvalidObjectException("Proxy required.");
	}

	private static class SerializationProxy implements Serializable {

		private static final long serialVersionUID = -6056026042294082359L;

		// array lists are serializable
		private final ArrayList<Serializable> serializableInstances;

		public SerializationProxy(InstanceCache cache) {
			serializableInstances = extractSerializableValues(cache);
		}

		private static ArrayList<Serializable> extractSerializableValues(InstanceCache cache) {
			return cache.cacheMap.values().stream()
					.filter(instance -> instance instanceof Serializable)
					.map(instance -> (Serializable) instance)
					.collect(Collectors.toCollection(ArrayList::new));
		}

		/**
		 * After the proxy is deserialized, it invokes a constructor to create an {@link InstanceCache}
		 * "the regular way".
		 */
		private Object readResolve() {
			System.out.println("-- replacing " + this + " with instance cache on deserialization");
			return new InstanceCache(serializableInstances);
		}

		@Override
		public String toString() {
			String prefixWithItemCount = "[" + serializableInstances.size() + " items: ";
			StringJoiner joiner = new StringJoiner(", ", prefixWithItemCount, "]");
			Stream.of(serializableInstances)
					.map(instance -> entryToString(instance.getClass(), instance))
					.forEach(joiner::add);
			return "InstanceCache.SerializarionProxy " + joiner;
		}
	}

}
