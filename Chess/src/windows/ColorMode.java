package windows;

public enum ColorMode {
	negative, mono, custom;
	public int index() {
		for(int i = 0; i < 7; i++)
			if(values()[i]==this)
				return i;
		return -1;
	}
}
