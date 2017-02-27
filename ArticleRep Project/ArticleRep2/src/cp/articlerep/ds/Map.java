package cp.articlerep.ds;

/**
 * @author Ricardo Dias
 */
public interface Map<K extends Comparable<K>, V> {
	public V put(K key, V value);
	public boolean contains(K key);
	public V remove(K key);
	public V get(K key);
	public Iterator<V> values();
	public Iterator<K> keys();
	public boolean validate();
	public void singleLock(K key);
	public void listLock(List<K> keys);
	public void singleUnlock(K key);
	public void listUnlock(List<K> keys);
}
