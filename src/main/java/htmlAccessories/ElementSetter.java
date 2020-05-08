package htmlAccessories;

import org.jsoup.nodes.Element;

public class ElementSetter implements IElemetSetter{

	private Element element;

	public ElementSetter(Element element) {
		this.element = element;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void set(ElementData elementData) {
		// TODO Auto-generated method stub
		
		switch (elementData.getType()) {
		case ATTR:
			element.attr(elementData.getAttr(), elementData.getValue());
			break;
			
		case TEXT:
			element.html(elementData.getText());
			break;

		case ELEMENT:
			element.append(elementData.getValue());
			break;
		default:
			break;
		}
		
	}

}
