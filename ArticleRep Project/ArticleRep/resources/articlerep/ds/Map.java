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
	public void singleReadLock(K key);
	public void listReadLock(List<K> keys);
	public void singleReadUnlock(K key);
	public void listReadUnlock(List<K> keys);
	public void singleWriteLock(K key);
	public void listWriteLock(List<K> keys);
	public void singleWriteUnlock(K key);
	public void listWriteUnlock(List<K> keys);
}
