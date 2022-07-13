package me.blazingtwist.loadingspinner;

public class IconKey {
	public final Integer index;
	public final String key;

	public static IconKey getByIndex(int index) {
		return new IconKey(index, null);
	}

	public static IconKey getByKey(String key) {
		return new IconKey(null, key);
	}

	private IconKey(Integer index, String key) {
		this.index = index;
		this.key = key;
	}

	@Override
	public String toString() {
		return "IconKey{"
				+ "index: " + index
				+ ", key: '" + key + '\''
				+ '}';
	}
}
