package cp.articlerep.ds;

public interface Map<K, V> {
	public V put(K key, V value);
	public V remove(K key);
	public V get(K key);
	public boolean validate();
}
