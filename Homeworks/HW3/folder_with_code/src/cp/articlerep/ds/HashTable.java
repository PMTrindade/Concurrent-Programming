package cp.articlerep.ds;

// this hash table is being used by a concurrent (multi-threaded) program
// and is not protected at all.  If you are 'lucky', everything goes all right.
// Otherwise you 'loose' elements that should be in the map (please note that
// if unprotected this data structure does not become inconsistent/corrupted)
public class HashTable<K, V> implements Map<K, V> {
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
	private int produced;
	private int consumed;
	private int size;

	public HashTable(int size) {
		this.table = new Node[size];
		this.produced = 0;
		this.consumed = 0;
		this.size = size;
	}

	private int calcTablePos(K key) {
		return key.hashCode() % this.table.length;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V put(K key, V value) {
		int pos = this.calcTablePos(key);
		Node n = this.table[pos];

		// lookup for the node
		while (n != null && !n.key.equals(key)) {
			n = n.next;
		}

		// node already exists... update the value and return the old value
		if (n != null) {
			V oldValue = (V) n.value;
			n.value = value;
			return oldValue;
		}

		// node does not exist... create a new node and return 'null'
		Node nn = new Node(key, value, this.table[pos]);
		this.table[pos] = nn;
		produced++;
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V remove(K key) {
		int pos = this.calcTablePos(key);
		Node p = this.table[pos];

		// node does not exist
		if (p == null) {
			return null;
		}

		// is it the first node?
		if (p.key.equals(key)) {
			this.table[pos] = p.next;
			consumed++;
			return (V) p.value;
		}

		// it was not the first node... look for the right node
		Node n = p.next;
		while(n != null && !n.key.equals(key)) {
			p = n;
			n = n.next;
		}

		if (n == null) {
			// the node was not found
			return null;
		}

		// we found the node... lets remove it
		p.next = n.next;
		consumed++;
		return (V) n.value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(K key) {
		int pos = this.calcTablePos(key);
		Node n = this.table[pos];

		// lookup for the node
		while(n != null && !n.key.equals(key)) {
			n = n.next;
		}

		// return the value if node was found, 'null' otherwise
		return (V) (n != null ? n.value : null);
	}

	// checks the invariants of the hash table (producer-consumer problem)
	public boolean validate() {
		boolean valid = false;
		if(consumed >= 0 && produced >= consumed && produced <= consumed + size)
			valid = true;

		return valid;
	}
}
