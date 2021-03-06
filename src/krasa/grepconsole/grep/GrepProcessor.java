package krasa.grepconsole.grep;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import krasa.grepconsole.model.GrepExpressionItem;
import krasa.grepconsole.filter.support.ConsoleMode;

import org.apache.commons.lang.StringUtils;

import com.intellij.openapi.diagnostic.Logger;

public class GrepProcessor {
	private static final Logger log = Logger.getInstance(GrepProcessor.class.getName());

	private GrepExpressionItem grepExpressionItem;

	public GrepProcessor(GrepExpressionItem grepExpressionItem) {
		this.grepExpressionItem = grepExpressionItem;
	}

	public FilterState process(FilterState state) {
		if (grepExpressionItem.isEnabled() && !StringUtils.isEmpty(grepExpressionItem.getGrepExpression())) {
			String matchedLine = state.getText();

			if (matches(matchedLine) && !matchesUnless(matchedLine)) {
				state.setNextOperation(grepExpressionItem.getOperationOnMatch());
				state.setConsoleViewContentType(grepExpressionItem.getConsoleViewContentType(state.getConsoleViewContentType()));
				state.setExclude(grepExpressionItem.isInputFilter());

				playSound(state);
			}
		}
		return state;
	}

	private void playSound(FilterState flow) {
		if (flow.getConsoleMode() != ConsoleMode.NO_SOUND) {
			grepExpressionItem.getSound().play();
		}
	}

	private boolean matches(String matchedLine) {
		Pattern pattern = grepExpressionItem.getPattern();
		boolean matches = false;
		if (pattern != null) {
			matches = pattern.matcher(matchedLine).matches();
		}
		return matches;
	}

	private boolean matchesUnless(String matchedLine) {
		boolean matchUnless = false;
		Pattern unlessPattern = grepExpressionItem.getUnlessPattern();
		if (unlessPattern != null) {
			Matcher unlessMatcher = unlessPattern.matcher(matchedLine);
			if (unlessMatcher.matches()) {
				matchUnless = true;
			}
		}
		return matchUnless;
	}
}
