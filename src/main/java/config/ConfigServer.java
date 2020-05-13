package config;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ConfigServer {

	public static final String fileName = "configServer.properties";
	public static final String ELEMENT_WAITING_CAUSE = "element_waiting_cause";
	public static final String ELEMENT_CODE_GROUP_IS = "element_code_group_is";
	public static final String CODE_GROUP_IS = "code_group_is";
	public static final String WAITING_TO_JOIN = "waiting_to_join";
	public static final String WAITING_TO_FINISH_REGISTERING = "waiting_to_finish_registering";
	public static final String IMAGE_FILE_TYPE = "image_file_type";
	public static final String PROFIL_POSTFIX = "profil_postfix";
	public static final String IMAGES_PROFIL_BASE_DIR = "images_profil_base_dir";
	public static final String ASSETS_BASE_DIR = "assets_base_dir";
	public static final String IMAGES_DRAWING_BASE_DIR = "images_drawing_base_dir";
	public static final String DRAWING_POSTFIX = "drawing_postfix";
	public static final String ELEMENT_INFO_COLUMN = "element_info_column";
//	public static final String PROFIL_WIDTH = "profil_width";
//	public static final String PROFIL_HEIGHT = "profil_height";
	public static final String ELEMENT_TRUE_SENTENCE = "element_true_sentence";
	public static final String ELEMENT_DRAWING = "element_drawing";
	public static final String WAITING_TO_FINISH_DRAWING = "waiting_to_finish_drawing";
	public static final String WAITING_FALSING_YOUR_DRAWING = "waiting_falsing_your_drawing";
	public static final String ELEMENT_GUESS_SENTENCE = "element_guess_sentence";
	public static final String ELEMENT_GUESS_HEAD = "element_guess_head";
	public static final String HEAD_FOR_GUESSERS = "head_for_guessers";
	public static final String HEAD_FOR_PAINTER = "head_for_painter";
	public static final String ELEMENT_GUESS_BOTTOM = "element_guess_bottom";
	public static final String BOTTOM_FOR_PAINTER = "bottom_for_painter";
	public static final String WAITING_TO_FINISH_GUESSING = "waiting_to_finish_guessing";
	public static final String ELEMENT_RESULT_HEAD = "element_result_head";
	public static final String ELEMENT_SENTENCE_RESULT = "element_sentence_result";
	public static final String ELEMENT_PLAYER_NAME = "element_player_name";
	public static final String ELEMENT_SCORE_RESULT = "element_score_result";
	public static final String ELEMENT_CHOOSERS_RESULT = "element_choosers_result";
	public static final String ELEMENT_CHOOSERS_WON = "element_choosers_won";
	public static final String CHOOSERS_WON_EACH = "choosers_won_each";
	public static final String RESULT_HEAD_FALSER = "result_head_falser";
	public static final String RESULT_HEAD_TRUE = "result_head_true";
	public static final String ELEMENT_SCORE = "element_score";
	public static final String SCORE_RECORD = "score_record";
	public static final String ELEMENT_SCORE_PAGE_HEAD = "element_score_page_head";
	public static final String SCORE_PAGE_LAST_ROUND = "score_page_last_round";
	public static final String SCORE_PAGE_SESSION = "score_page_session";
	public static final String WAITING_FOR_DECIDERS = "waiting_for_deciders";
	public static final String WAITING_FOR_SEE_SCORES = "waiting_for_see_scores";
	public static final String ELEMENT_WINNER = "element_winner";
	public static final String IMAGE_SIZE = "image_size";
	public static final String PROFIL_SIZE = "profil_size";
	public static final String WAITING_TO_FINISH_FALSING = "waiting_to_finish_flasing";

	public static Properties configFile;
	public static InputStream inputStream;
	
	public static void initConfig() {
		configFile = new java.util.Properties();
		inputStream = ConfigServer.class.
				getClassLoader().
				getResourceAsStream(fileName);
		try {
			configFile.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
		} catch (Exception eta) {
			eta.printStackTrace();
		}
	}

	public static String getProperty(String key) {
		String value = configFile.getProperty(key);
		return value;
	}
}
