package org.jabref.logic.integrity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jabref.logic.l10n.Localization;
import org.jabref.model.database.BibDatabase;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.Field;

/**
 * Checks, if there is an undefined string # (FieldWriter.BIBTEX_STRING_START_END_SYMBOL)
 */
public class UndefinedChecker implements EntryChecker {

    private static final Pattern INSIDE_BIBTEX_STRING_START_END_SYMBOL = Pattern.compile("\\#[^#\\#]*\\#");
    private final BibDatabase database;

    public UndefinedChecker(BibDatabase database) {
        this.database = Objects.requireNonNull(database);
    }

    @Override
    public List<IntegrityMessage> check(BibEntry entry) {
        List<IntegrityMessage> result = new ArrayList<>();
        for (Map.Entry<Field, String> field : entry.getFieldMap().entrySet()) {
            String fieldValue = field.getValue();

            List<String> allMatches = new ArrayList<String>();
            Matcher matcher = INSIDE_BIBTEX_STRING_START_END_SYMBOL.matcher(fieldValue);
            while (matcher.find()) {
                allMatches.add(matcher.group());
            }
            for (String match : allMatches) {
                String constantId = match.substring(1, match.length() - 1);

                if (!database.getStringByName(constantId).isPresent()) {
                    result.add(new IntegrityMessage(Localization.lang("String %0 is undefined", match), entry, field.getKey()));
                }
            }
        }
        return result;
    }
}
