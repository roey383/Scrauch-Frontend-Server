package generalTests;

public class StringTests {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String resultString;
		resultString = String.format("את התיאור הכוזב \"%s\" כתב %s, ובחרו בו: %s.\n\n%s הרוויח סך הכל %d נקודות",
				"משפט אחד", "משה", "אבי אפי אלי",
				"משה",
				1000 * 3);

		resultString = String.format(
				"המשפט האמיתי הוא \"%s\" ושייך ל %s. בחרו בו: %s.\n\n%s הרוויח סך הכל %d נקודות\nכל אחד מהבוחרים זכה ב %d נקודות",
				"משפט אחד", "משה", "אבי אפי אלי",
				"משה",
				1000 * 3,
				1000);

		System.out.println(resultString);
	}

}
