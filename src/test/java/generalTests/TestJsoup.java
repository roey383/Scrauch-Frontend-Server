package generalTests;

import org.jsoup.nodes.Element;

import config.Config;
import config.ConfigServer;

public class TestJsoup {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ConfigServer.initConfig();
		Element img = new Element("img");
		img.attr("src","path/to/file.png");
		img.attr("height", ConfigServer.getProperty(ConfigServer.IMAGE_SIZE));
		img.attr("width", ConfigServer.getProperty(ConfigServer.IMAGE_SIZE));
		System.out.println(img);
//		String nameElement = HtmlGenerator.generateNameElement(playerInfo.getName());
		Element name = new Element("div");
		name.html("playerName");
		Element playerInfoElement = new Element("div");
		playerInfoElement.append(img.toString());
		playerInfoElement.append(name.toString());
		
		System.out.println(playerInfoElement);
		
//		Element e = new Element("img");
//		System.out.println(e.toString());
//		String html = "<div id=\"col\"></div>";
//	    Document doc = Jsoup.parse(html);
//	    
//	    ElementSetter div = new ElementSetter(doc.selectFirst("#col"));
//	    ElementData data = new ElementData(DataType.ELEMENT, "<img src=\"fff.fff\">");
//	    
//	    System.out.println(div);
//	    
//	    div.set(data);
//	    System.out.println(doc);

	}

}
