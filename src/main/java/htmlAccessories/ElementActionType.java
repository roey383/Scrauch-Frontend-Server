package htmlAccessories;

public enum ElementActionType {

	ATTR("attr"),
	TEXT("text"),
	ELEMENT("element");

	protected final String name;
	
	private ElementActionType(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
}
