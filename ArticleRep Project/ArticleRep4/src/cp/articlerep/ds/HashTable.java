package cp.articlerep.ds;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;
/**
 * @author Ricardo Dias
 */
public class HashTable<K extends Comparable<K>, V> implements Map<K, V> {

	private static class Node {
		public Object key;
		public Object value;
		public Node next;

		public Node(Object key, Object value, Node next) {
			this.key = key;
			this.value = value;
			this.next = next;
		}
	}

	private Node[] table;
	private ReentrantLock[]	locks;
	// Invariant variables (producer/consumer)
	private int counterProduced; // Number of produced items
	private int counterConsumed; // Number of consumed items
	private int size; // Size of the data structure

	public HashTable() {
		this(1000);
		this.counterProduced = 0;
		this.counterConsumed = 0;
		this.size = 1000;
	}

	public HashTable(int size) {
		this.counterProduced = 0;
		this.counterConsumed = 0;
		this.size = size;
		this.table = new Node[size];
		this.locks = new ReentrantLock [size];

		for(int i = 0; i < size; i++){
			this.locks[i] = new ReentrantLock();
		}
	}

	private int calcTablePos(K key) {
		return Math.abs(key.hashCode()) % this.table.length;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V put(K key, V value) {
		int pos = this.calcTablePos(key);
		Node n = this.table[pos];

		while (n != null && !n.key.equals(key)) {
			n = n.next;
		}

		if (n != null) {
			V oldValue = (V) n.value;
			n.value = value;
			return oldValue;
		}

		Node nn = new Node(key, value, this.table[pos]);
		this.table[pos] = nn;

		this.counterProduced++; // Item produced

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V remove(K key) {
		int pos = this.calcTablePos(key);
		Node p = this.table[pos];
		if (p == null) {
			return null;
		}

		if (p.key.equals(key)) {
			this.table[pos] = p.next;
			return (V) p.value;
		}

		Node n = p.next;
		while (n != null && !n.key.equals(key)) {
			p = n;
			n = n.next;
		}

		if (n == null) {
			return null;
		}

		p.next = n.next;

		this.counterConsumed++; // Item consumed

		return (V) n.value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(K key) {
		int pos = this.calcTablePos(key);
		Node n = this.table[pos];
		while (n != null && !n.key.equals(key)) {
			n = n.next;
		}
		return (V) (n != null ? n.value : null);
	}

	@Override
	public boolean contains(K key) {
		return get(key) != null;
	}

	/**
	 * No need to protect this method from concurrent interactions
	 */
	@Override
	public Iterator<V> values() {
		return new Iterator<V>() {

			private int pos = -1;
			private Node nextBucket = advanceToNextBucket();

			private Node advanceToNextBucket() {
				pos++;
				while (pos < HashTable.this.table.length
						&& HashTable.this.table[pos] == null) {
					pos++;
				}
				if (pos < HashTable.this.table.length)
					return HashTable.this.table[pos];

				return null;
			}

			@Override
			public boolean hasNext() {
				return nextBucket != null;
			}

			@SuppressWarnings("unchecked")
			@Override
			public V next() {
				V result = (V) nextBucket.value;

				nextBucket = nextBucket.next != null ? nextBucket.next
						: advanceToNextBucket();

				return result;
			}

		};
	}

	// Implementation needed to check article consistency in both authors and keywords hash maps
	// Improve by removing  duplicate code
	@Override
	public Iterator<K> keys() {
		return new Iterator<K>() {
			private int pos = -1;
			private Node nextBucket = advanceToNextBucket();

			private Node advanceToNextBucket() {
				pos++;
				while (pos < HashTable.this.table.length
						&& HashTable.this.table[pos] == null) {
					pos++;
				}
				if (pos < HashTable.this.table.length)
					return HashTable.this.table[pos];

				return null;
			}

			@Override
			public boolean hasNext() {
				return nextBucket != null;
			}

			@SuppressWarnings("unchecked")
			@Override
			public K next() {
				K result = (K) nextBucket.key;

				nextBucket = nextBucket.next != null ? nextBucket.next
						: advanceToNextBucket();

				return result;
			}
		};
	}

	// Checks the invariants of the hash map data structure
	public boolean validate () {
		boolean valid = false;
		if(counterConsumed >= 0 && counterProduced >= counterConsumed && counterProduced <= counterConsumed + size)
			valid = true;

		return valid;
	}

	private SortedSet<Integer> sortedLockPositions(List<K> keys) {
		SortedSet<Integer> set = new TreeSet<Integer>();
		Iterator<K> it = keys.iterator();
		while (it.hasNext())
			set.add(calcTablePos(it.next()));
		return set;
	}

	@Override
	public void singleLock(K key) {
		locks[calcTablePos(key)].lock();
	}

	@Override
	public void singleUnlock(K key) {
		locks[calcTablePos(key)].unlock();
	}

}
