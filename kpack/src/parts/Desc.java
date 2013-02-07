package parts;

public class Desc {

	public static Desc create(int num) {
		return create(num, "Description");
	}
	
	public static Desc create(String str) {
		return create(-1, str);
	}
	
	public static Desc create(int num, String str) {
		return new Desc(num, str);
	}

	public final int num;
	public final String str;
	
	private Desc(int num, String str) {
		this.num = num;
		this.str = str;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + num;
		result = prime * result + ((str == null) ? 0 : str.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Desc other = (Desc) obj;
		if (num != other.num)
			return false;
		if (str == null) {
			if (other.str != null)
				return false;
		} else if (!str.equals(other.str))
			return false;
		return true;
	}
	
}
