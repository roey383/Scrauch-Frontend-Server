package htmlAccessories;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
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

public class HtmlBuilder {

	private static UserStageMonitor userStage;

	public static void init(UserStageMonitor userStage2) {
		// TODO Auto-generated method stub
		userStage = userStage2;
	}

	public static byte[] loadHtml(String htmlFilePath, long userId) throws IOException {

		HtmlData htmlData = userStage.getHtmlData(userId);
		Application.logger.info("user Id = " + userId + " is on " + htmlFilePath + " page");
		InputStream htmlInputStream;
		htmlInputStream = HtmlBuilder.class.getClassLoader().getResourceAsStream(htmlFilePath);
		if (htmlInputStream == null) {
			return new byte[] {};
		}

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
			Element image = generateImageElement(imagePath, ConfigServer.getProperty(ConfigServer.IMAGE_SIZE));
			elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_DRAWING),
					new ElementData(ElementActionType.ELEMENT, image.toString()));
			Application.logger.info(htmlData.getUserId() + image.toString());

			break;
		}

		case UserStageMonitor.WAITING_ROOM_FALSING:
			elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_WAITING_CAUSE), new ElementData(
					ElementActionType.TEXT, ConfigServer.getProperty(ConfigServer.WAITING_TO_FINISH_FALSING)));
			break;

		case UserStageMonitor.GUESSING: {
			int i = -1;
			for (String sentence : htmlData.getAllSentences()) {
				i++;
				String element = ConfigServer.getProperty(ConfigServer.ELEMENT_GUESS_SENTENCE) + i;
				elementToValue.put(element, new ElementData(ElementActionType.TEXT, sentence));
			}

			String imagePath = saveImageToResources(htmlData.getDrawingSentence().getDrawing(),
					htmlData.getDrawingSentence().getPlayerId(), "drawing");
			Element image = generateImageElement(imagePath, ConfigServer.getProperty(ConfigServer.IMAGE_SIZE));
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
				elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_INFO_COLUMN) + col,
						new ElementData(ElementActionType.ELEMENT, generateResultElement(result).toString()));
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
			elementToValue.put(ConfigServer.getProperty(ConfigServer.ELEMENT_WINNER),
					new ElementData(ElementActionType.TEXT, htmlData.getWinner().getName()));
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
//		String baseDir = type.equals("profil") ? ConfigServer.getProperty(ConfigServer.IMAGES_PROFIL_BASE_DIR)
//				: ConfigServer.getProperty(ConfigServer.IMAGES_DRAWING_BASE_DIR);
		String imagePath = /* baseDir + */ id + postfix/* + new Random().nextInt(1000) */ + "."
				+ ConfigServer.getProperty(ConfigServer.IMAGE_FILE_TYPE);

		Application.logger.info("image path: " + imagePath);
		File dir = new File("images");
		if (!dir.exists()) {
			Application.logger.info("creating directory: " + dir.getName());
			boolean result = false;

			try {
				dir.mkdir();
				result = true;
			} catch (SecurityException se) {
				// handle it
			}
			if (result) {
				Application.logger.info("DIR created");
			}

		}
		String fullPath = new File(dir + "/" + imagePath).getAbsolutePath();
		Application.logger.info("image absolutePath : " + fullPath);
		ImageIO.write((RenderedImage) image, ConfigServer.getProperty(ConfigServer.IMAGE_FILE_TYPE),
				new File(fullPath));

		return fullPath;
	}

	private static Element generatePlayerInfoElement(String name, String imagePath) {
		Element img = generateImageElement(imagePath, ConfigServer.getProperty(ConfigServer.PROFIL_SIZE));
		Element nameElement = new Element("div").html(name);
		Element playerInfoElement = new Element("div").append(img.toString()).append(nameElement.toString());
		return playerInfoElement;
	}

	private static Element generateResultElement(Result result) {
		// TODO Auto-generated method stub

		Element resultElement = new Element("div");
		Element intro = new Element("div");
		Element chosers = new Element("div");
		Element winnerPoints = new Element("div");
		Element pointsEarned = new Element("div");

		if (!result.isTrueSentence()) {
			intro.html(
					String.format("את התיאור הכוזב \"%s\" כתב %s", result.getSentence(), result.getPlayer().getName()));
		} else {
			intro.html(String.format("המשפט האמיתי הוא \"%s\" ושייך ל%s", result.getSentence(),
					result.getPlayer().getName()));
		}
		if (result.getChoosers().size() == 0) {
			chosers.html("אף אחד לא בחר בו");
			winnerPoints.html(result.getPlayer().getName() + " לא הרוויח אף נקודה");
		} else {
			chosers.html(String.format("בחרו בו: %s", buildChoosersString(result.getChoosers())));
			winnerPoints.html(String.format("%s הרוויח סך הכל %d נקודות", result.getPlayer().getName(),
					result.getPointsCollectPerHead() * result.getChoosers().size()));
		}
		if (result.isTrueSentence() && result.getChoosers().size() > 0) {
			pointsEarned.html(String.format("כל אחד מהבוחרים זכה ב%d נקודות", result.getPointsEarnedForHead()));
		}

		return resultElement.append(intro.toString()).append("</br>").append(chosers.toString()).append("</br>")
				.append(winnerPoints.toString()).append("</br>").append(pointsEarned.toString());
	}

	private static Element generateImageElement(String imagePath, String size) {
		Element img = new Element("img").attr("src", imagePath)
				.attr("height", size)
				.attr("width", size);
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
