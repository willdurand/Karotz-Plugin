package org.miquelmartin.karotz;

import java.io.IOException;
import java.util.logging.LogManager;

import org.jenkinsci.plugins.karotz.KarotzClient;
import org.jenkinsci.plugins.karotz.KarotzException;
import org.jenkinsci.plugins.karotz.action.EarAction;
import org.jenkinsci.plugins.karotz.action.LedColor;
import org.jenkinsci.plugins.karotz.action.LedFadeAction;
import org.jenkinsci.plugins.karotz.action.SpeakAction;

public class Tester {

	private static final String INSTALL_ID = "36bc42c1-c3c8-4c3e-9552-a84e1fa813b3";
	private static final String API_KEY = "b68bbed3-349f-49d9-976e-8a601abcba26";
	private static final String SECRET_KEY = "1929fbe1-54f2-44a7-81c2-80778dbef498";

	public static void main(String[] args) throws InterruptedException,
			KarotzException, SecurityException, IOException {
		LogManager.getLogManager().readConfiguration(
				Tester.class.getResourceAsStream("/logging.properties"));
		KarotzClient client = null;
		try {
			client = new KarotzClient(API_KEY, SECRET_KEY, INSTALL_ID);
			client.startInteractiveMode();
			(new LedFadeAction(LedColor.YELLOW, 1000)).execute(client);
			Thread.sleep(10000);
		} catch (KarotzException e) {
			e.printStackTrace();
		} finally {
			if (client != null) {
				client.stopInteractiveMode();
			}
		}
	}

	public static void resetEars(KarotzClient client) throws KarotzException {
		System.out.println("Resetting ears");
		System.out.flush();
		(new EarAction()).execute(client);
	}

	public static void speakBriefly(KarotzClient client) throws KarotzException {
		System.out.println("Speaking for a brief time");
		System.out.flush();
		(new SpeakAction("Hello!", "EN")).execute(client);
	}

	public static void speakMedium(KarotzClient client) throws KarotzException {
		System.out.println("Speaking for a medium time");
		System.out.flush();
		(new SpeakAction(
				"Hello! My name is Inigo Montoya. You killed my father. Prepare to die",
				"EN")).execute(client);
	}

	public static void speakLong(KarotzClient client) throws KarotzException {
		System.out.println("Speaking for a long time");
		System.out.flush();
		String news = "As the effects of years of recession pile up here, more and more Spanish "
				+ "families — with unemployment checks running out and stuck with mortgages they "
				+ "cannot pay — are leaning hard on their elderly relatives. And there is little "
				+ "relief in sight. Spain’s latest round of austerity measures appears to have done "
				+ "little to restore investor confidence. And new employment statistics released "
				+ "Friday showed that the jobless rate had risen to a record 25 percent. Pensions for "
				+ "the elderly are among the few benefits that have not been slashed, though they have "
				+ "been frozen since last year. The Spanish are known for their strong family networks, "
				+ "and most grandparents are eager to help, unwilling to admit to outsiders what is "
				+ "going on, experts say. But those who work with older people say it has not been easy. "
				+ "Many struggle to feed three generations now, their homes overcrowded and the tensions "
				+ "of the situation sometimes turning their lives to misery. In some cases, families are "
				+ "removing their relatives from nursing homes so they can collect their pensions. It "
				+ "is a trend that has advocates concerned about whether the younger generations are "
				+ "going too far, even if grandparents agree to the move or are too infirm to notice."
				+ " \\“The crisis in Spain is affecting the elderly in a very special way,\\” said "
				+ "the Rev. Ángel García, who runs a nonprofit group helping children and the elderly."
				+ " \\“Many grandparents want to give what they can, and they do. But, unfortunately,"
				+ " sometimes what is happening is that the younger generation is ransacking the "
				+ "older generation. They are taking all that they have.\\”";
		(new SpeakAction(news, "EN")).execute(client);
	}
}
