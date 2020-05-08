package htmlAccessories;

public class ElementData {
	
	private ElementActionType type;
	private String attr;
	private String value;
	
	public ElementData(ElementActionType type, String attr, String value) {
		super();
		this.type = type;
		this.attr = attr;
		this.value = value;
	}
	
	public ElementData(ElementActionType type, String value) {
		super();
		this.type = type;
		this.attr = "";
		this.value = value;
	}

	public ElementActionType getType() {
		return type;
	}

	public void setType(ElementActionType type) {
		this.type = type;
	}

	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "ElementData [type=" + type + ", attr=" + attr + ", value=" + value + "]";
	}

	public String getText() {
		// TODO Auto-generated method stub
		return value;
	}
	
	

}
