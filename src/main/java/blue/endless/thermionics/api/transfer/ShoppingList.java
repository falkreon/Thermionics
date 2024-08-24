package blue.endless.thermionics.api.transfer;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

public class ShoppingList {
	private Multimap<Class<?>, Entry<?>> resourceTypeMap = MultimapBuilder.hashKeys().arrayListValues().build();
	
	@SuppressWarnings("unchecked")
	private <T> Collection<Entry<T>> getResourceEntries(Class<T> clazz) {
		return (Collection<Entry<T>>) (Object) resourceTypeMap.get(clazz);
	}
	
	public <T> List<ResourceStack<T>> getListOfResourceType(Class<T> clazz) {
		return getResourceEntries(clazz).stream()
				.filter(it -> it.resourceClass.equals(clazz))
				.map(it -> it.resource)
				.collect(Collectors.toUnmodifiableList());
	}
	
	public <T> ShoppingList requestResource(Class<T> resourceClass, ResourceStack<T> resource) {
		resourceTypeMap.put(resourceClass, new Entry<T>(resourceClass, resource));
		
		return this;
	}
	
	public <T> long fulfill(Class<T> resourceClass, ResourceStack<T> resource, boolean simulate) {
		long remaining = resource.count();
		for(Entry<T> entry : getResourceEntries(resourceClass)) {
			if (entry.test(resource)) remaining = entry.fulfill(remaining, simulate);
			if (remaining <= 0L) break;
		}
		
		return remaining;
	}
	
	public boolean testSimulation() {
		for(Entry<?> entry : resourceTypeMap.values()) {
			if (entry.simulatedFulfill < entry.resource.count()) return false;
		}
		return true;
	}
	
	public void resetSimulation() {
		for(Entry<?> entry : resourceTypeMap.values()) {
			entry.simulatedFulfill = 0L;
		}
	}
	
	public boolean testTransfer() {
		for(Entry<?> entry : resourceTypeMap.values()) {
			if (entry.actualFulfill < entry.resource.count()) return false;
		}
		return true;
	}
	
	public void resetTransfer() {
		for(Entry<?> entry : resourceTypeMap.values()) {
			entry.actualFulfill = 0L;
		}
	}
	
	public static class Entry<T> {
		private final Class<T> resourceClass;
		private final ResourceStack<T> resource;
		private boolean ignoreComponents;
		private long simulatedFulfill = 0L;
		private long actualFulfill = 0L;
		
		public Entry(Class<T> resourceClass, ResourceStack<T> resource) {
			this.resourceClass = resourceClass;
			this.resource = resource;
		}
		
		public boolean test(ResourceStack<T> stack) {
			if (ignoreComponents) {
				return Objects.equals(stack.resource().object(), resource.resource().object());
			} else {
				return Objects.equals(stack.resource(), resource.resource());
			}
		}
		
		public long fulfill(long quantity, boolean simulate) {
			long existing = (simulate) ? simulatedFulfill : actualFulfill;
			long combined = existing + quantity;
			long actual = Math.min(combined, resource.count());
			long rejected = combined - actual;
			
			if (simulate) {
				simulatedFulfill = actual;
			} else {
				actualFulfill = actual;
			}
			
			return rejected;
		}
	}
}
