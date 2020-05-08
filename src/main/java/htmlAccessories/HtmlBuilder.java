package htmlAccessories;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import app.Application;
import config.ConfigServer;
import controller.UserStageMonitor;
import logic.PlayerPersonalInfo;
import logic.Result;
import networking.WebServer;

public class HtmlBuilder {

	private static UserStageMonitor userStage;

	public static void init(UserStageMonitor userStage2) {
		// TODO Auto-generated method stub
		userStage = userStage2;
	}

	public static byte[] loadHtml(String htmlFilePath, long userId) throws IOException {

		HtmlData htmlData = userStage.getHtmlData(userId);
		Application.logger.info("user Id = " + userId + " is on " + htmlFilePath + " page");
//		InputStream htmlInputStream = HtmlBuilder.class.getClassLoader().getResourceAsStream(htmlFilePath);
		InputStream htmlInputStream;
		try {
			htmlInputStream = new FileInputStream(WebServer.RESOURCES_BASE_DIR + "/" + htmlFilePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return new byte[] {};
		}
//		if (htmlInputStream == null) {
//			return new byte[] {};
//		}

		Document document = Jsoup.parse(htmlInputStream, "UTF-8", "");

		String modifiedHtml = populateHtmlDocument(document, htmlData);
		
		return modifiedHtml.getBytes("UTF-8");
	}

	private static String populateHtmlDocument(Document document, HtmlData htmlData) throws IOException {
		// TODO Auto-generated method stub

		Application.logger.info(htmlData.getUserId() + " is on loading html page");
		for (Map.Entry<String, ElementData> elementNameToValue : getHtmlElementsToValues(htmlData)) {
			Application.logger.info("got " + elementNameToValue.toString() + " to populate");
			ElementSetter element = new ElementSetter(document.selectFirst(elementNameToValue.getKey()));
			ElementData value = elementNameToValue.getValue();
			element.set(value);
			Application.logger.info(elementNameToValue.toString() + " added");
		}

		return document.toString();
	}

	private static Set<Entry<String, ElementData>> getHtmlElementsToValues(HtmlData htmlData) throws IOException {
		// TODO Auto-generated method stub

		return buildElementsToValues(htmlData);
	}

	public static Set<Entry<String, ElementData>> buildElementsToValues(HtmlData htmlData) throws IOException {
		// TODO Auto-generated method stub

//		if (elementToValue != null || elementToChild != null)
//			return;

		Map<String, ElementData> elementToValue = new HashMap<String, ElementData>();

		switch (htmlData.getStage()) {
		case UserStageMonitor.WAITING_ROOM_JOINERS:
			elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_WAITING_CAUSE),
					new ElementData(ElementActionType.TEXT, ConfigServer.getProperty(ConfigServer.WAITING_TO_JOIN)));
			elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_CODE_GROUP_IS),
					new ElementData(ElementActionType.TEXT,
							htmlData.getGameCode() + " " + ConfigServer.getProperty(ConfigServer.CODE_GROUP_IS)));
			break;

		case UserStageMonitor.WAITING_ROOM_REGISTERING_INFO:
			elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_WAITING_CAUSE), new ElementData(
					ElementActionType.TEXT, ConfigServer.getProperty(ConfigServer.WAITING_TO_FINISH_REGISTERING)));
			break;

		case UserStageMonitor.PLAYERS_PRESENTATION: {
			int col = -1;
			for (PlayerPersonalInfo playerInfo : htmlData.getPlayersInfo()) {
				col++;
				String imagePath = saveImageToResources(playerInfo.getProfil(), playerInfo.getId(), "profil");
				Element playerInfoElement = generatePlayerInfoElement(playerInfo.getName(), imagePath);
				elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_INFO_COLUMN) + col,
						new ElementData(ElementActionType.ELEMENT, playerInfoElement.toString()));
				Application.logger.info(playerInfo.toString() + playerInfoElement.toString());
			}
			break;
		}
		case UserStageMonitor.DRAWING:
			elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_TRUE_SENTENCE),
					new ElementData(ElementActionType.TEXT, htmlData.getTrueSentence()));
			break;

		case UserStageMonitor.WAITING_ROOM_DRAWING:
			elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_WAITING_CAUSE), new ElementData(
					ElementActionType.TEXT, ConfigServer.getProperty(ConfigServer.WAITING_TO_FINISH_DRAWING)));
			break;

		case UserStageMonitor.FALSING: {
			if (htmlData.getUserId() == htmlData.getDrawingSentence().getPlayerId()) {
				elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_WAITING_CAUSE), new ElementData(
						ElementActionType.TEXT, ConfigServer.getProperty(ConfigServer.WAITING_FALSING_YOUR_DRAWING)));
				htmlData.setStage(UserStageMonitor.WAITING_ROOM_FALSING);
				break;
			}

			String imagePath = saveImageToResources(htmlData.getDrawingSentence().getDrawing(),
					htmlData.getDrawingSentence().getPlayerId(), "drawing");
			Element image = generateImageElement(imagePath);
			elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_DRAWING),
					new ElementData(ElementActionType.ELEMENT, image.toString()));
			Application.logger.info(htmlData.getUserId() + image.toString());

			break;
		}

		case UserStageMonitor.GUESSING: {
			int i = -1;
			for (String sentence : htmlData.getAllSentences()) {
				i++;
				String element = ConfigServer.getProperty(ConfigServer.ELEMENT_GUESS_SENTENCE) + i;
				elementToValue.put(element, new ElementData(ElementActionType.TEXT, sentence));
			}

			String imagePath = saveImageToResources(htmlData.getDrawingSentence().getDrawing(),
					htmlData.getDrawingSentence().getPlayerId(), "drawing");
			Element image = generateImageElement(imagePath);
			elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_DRAWING),
					new ElementData(ElementActionType.ELEMENT, image.toString()));
			Application.logger.info(htmlData.getUserId() + image.toString());

			String head = htmlData.getUserId() == htmlData.getDrawingSentence().getPlayerId()
					? ConfigServer.getProperty(ConfigServer.HEAD_FOR_PAINTER)
					: ConfigServer.getProperty(ConfigServer.HEAD_FOR_GUESSERS);
			elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_GUESS_HEAD),
					new ElementData(ElementActionType.TEXT, head));
			if (htmlData.getUserId() == htmlData.getDrawingSentence().getPlayerId()) {
				elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_GUESS_BOTTOM), new ElementData(
						ElementActionType.TEXT, ConfigServer.getProperty(ConfigServer.BOTTOM_FOR_PAINTER)));
				htmlData.setStage(UserStageMonitor.WAITING_ROOM_GUESSING);
			}
			break;
		}

		case UserStageMonitor.WAITING_ROOM_GUESSING:
			elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_WAITING_CAUSE), new ElementData(
					ElementActionType.TEXT, ConfigServer.getProperty(ConfigServer.WAITING_TO_FINISH_GUESSING)));
			break;

		case UserStageMonitor.RESULTS: {
			Application.logger.info(htmlData.getUserId() + " on building for RESULT page");
			int col = -1;
			for (Result result : htmlData.getResults()) {
				col++;
				elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_RESULT_HEAD) + col, new ElementData(
						ElementActionType.TEXT, ConfigServer.getProperty(ConfigServer.RESULT_HEAD_FALSER)));
				elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_SENTENCE_RESULT) + col,
						new ElementData(ElementActionType.TEXT, result.getSentence()));
				elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_PLAYER_NAME) + col,
						new ElementData(ElementActionType.TEXT, result.getPlayer().getName()));
				elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_SCORE_RESULT) + col,
						new ElementData(ElementActionType.TEXT,
								Integer.toString(result.getPointsCollectPerHead() * result.getChoosers().size())));
				elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_CHOOSERS_RESULT) + col,
						new ElementData(ElementActionType.TEXT, buildChoosersString(result.getChoosers())));
				if (result.isTrueSentence()) {
					elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_RESULT_HEAD) + col,
							new ElementData(ElementActionType.TEXT,
									ConfigServer.getProperty(ConfigServer.RESULT_HEAD_TRUE)));
					elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_CHOOSERS_WON) + col,
							new ElementData(ElementActionType.TEXT,
									String.format(ConfigServer.getProperty(ConfigServer.CHOOSERS_WON_EACH),
											buildChoosersString(result.getChoosers()),
											result.getPointsEarnedForHead())));
				}
			}
			Application.logger
					.info(htmlData.getUserId() + " got elementToValue " + elementToValue.entrySet().toString());
			break;
		}
		case UserStageMonitor.LAST_ROUND_SCORES: {

			elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_SCORE_PAGE_HEAD), new ElementData(
					ElementActionType.TEXT, ConfigServer.getProperty(ConfigServer.SCORE_PAGE_LAST_ROUND)));
			addScoreValues(elementToValue, htmlData);
			break;
		}

		case UserStageMonitor.TOTAL_SCORES: {
			elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_SCORE_PAGE_HEAD),
					new ElementData(ElementActionType.TEXT, ConfigServer.getProperty(ConfigServer.SCORE_PAGE_SESSION)));
			addScoreValues(elementToValue, htmlData);
			break;
		}

		case UserStageMonitor.WAITING_ROOM_SEE_RESULTS:
			elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_WAITING_CAUSE), new ElementData(
					ElementActionType.TEXT, ConfigServer.getProperty(ConfigServer.WAITING_FOR_SEE_SCORES)));
			break;
			
		case UserStageMonitor.WINNER:
			elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_WINNER), new ElementData(
					ElementActionType.TEXT, htmlData.getWinner().getName()));
			break;

		case UserStageMonitor.WAITING_ROOM_DECIDING:
			elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_WAITING_CAUSE), new ElementData(
					ElementActionType.TEXT, ConfigServer.getProperty(ConfigServer.WAITING_FOR_DECIDERS)));
			break;
		default:
			break;
		}

		return elementToValue.entrySet();

	}

	private static synchronized String saveImageToResources(Image image, long id, String type) throws IOException {
		// TODO Auto-generated method stub
		String postfix = type.equals("profil") ? ConfigServer.getProperty(ConfigServer.PROFIL_POSTFIX)
				: ConfigServer.getProperty(ConfigServer.DRAWING_POSTFIX);
		String baseDir = type.equals("profil") ? ConfigServer.getProperty(ConfigServer.IMAGES_PROFIL_BASE_DIR)
				: ConfigServer.getProperty(ConfigServer.IMAGES_DRAWING_BASE_DIR);
		String imagePath = baseDir + id + postfix /*+ new Random().nextInt(1000)*/ + "."
				+ ConfigServer.getProperty(ConfigServer.IMAGE_FILE_TYPE);
		ImageIO.write((RenderedImage) image, ConfigServer.getProperty(ConfigServer.IMAGE_FILE_TYPE),
				new File(ConfigServer.getProperty(ConfigServer.ASSETS_BASE_DIR) + imagePath));
		
		return imagePath;
	}

	private static Element generatePlayerInfoElement(String name, String imagePath) {
		Element img = generateImageElement(imagePath);
		Element nameElement = new Element("div").html(name);
		Element playerInfoElement = new Element("div").append(img.toString()).append(nameElement.toString());
		return playerInfoElement;
	}

	private static Element generateImageElement(String imagePath) {
		Element img = new Element("img").attr("src", imagePath)
				.attr("height", ConfigServer.getProperty(ConfigServer.PROFIL_HEIGHT))
				.attr("width", ConfigServer.getProperty(ConfigServer.PROFIL_WIDTH));
		return img;
	}

	private static void addScoreValues(Map<String, ElementData> elementToValue, HtmlData htmlData) {
		int col = -1;
		for (Entry<PlayerPersonalInfo, Integer> record : htmlData.getScoreBoard().entrySet()) {
			col++;
			elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_SCORE) + col,
					new ElementData(ElementActionType.TEXT,
							String.format(ConfigServer.getProperty(ConfigServer.SCORE_RECORD),
									record.getKey().getName(), record.getValue())));
		}
	}

	private static String buildChoosersString(List<PlayerPersonalInfo> choosers) {
		// TODO Auto-generated method stub

		StringBuilder str = new StringBuilder();
		for (PlayerPersonalInfo playerPersonalInfo : choosers) {
			str.append(playerPersonalInfo.getName() + ", ");
		}
		if (str.length() > 1) {
			str.delete(str.length() - 2, str.length() - 1);
		}
		return str.toString();
	}

}
