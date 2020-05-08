package generalTests;

import java.awt.image.BufferedImage;

import htmlAccessories.HtmlData;
import logic.DrawingTrueSentencePair;

public class TestHtmlData {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		HtmlData h = new HtmlData(123, "waiting_room_joiners", 
				new DrawingTrueSentencePair(456, new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB), "true sentence"));
		
		System.out.println(h);

	}

}
